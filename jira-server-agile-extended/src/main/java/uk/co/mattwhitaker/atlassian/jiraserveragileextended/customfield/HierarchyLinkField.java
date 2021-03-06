package uk.co.mattwhitaker.atlassian.jiraserveragileextended.customfield;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.TextFieldCharacterLengthValidator;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.sal.api.web.context.HttpContext;
import com.google.common.base.Preconditions;
import java.util.Enumeration;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao.HierarchyFieldSettingsService;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.HierarchyLinkManager;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.PluginInitializer;

public class HierarchyLinkField extends GenericTextCFType {
  public static final String CUSTOM_FIELD_TYPE = PluginInitializer.PLUGIN_KEY + ":hierarchylinkfield";

  private final HierarchyLinkManager hierarchyLinkManager;
  private final JiraAuthenticationContext jiraAuthenticationContext;
  private final HierarchyFieldSettingsService hierarchyFieldSettingsService;
  private final HttpContext httpContext;


  public HierarchyLinkField(
      @JiraImport CustomFieldValuePersister customFieldValuePersister,
      @JiraImport GenericConfigManager genericConfigManager,
      @JiraImport TextFieldCharacterLengthValidator textFieldCharacterLengthValidator,
      @JiraImport JiraAuthenticationContext jiraAuthenticationContext,
      @Autowired HierarchyLinkManager hierarchyLinkManager,
      @Autowired HierarchyFieldSettingsService hierarchyFieldSettingsService,
      @ComponentImport HttpContext httpContext) {
    super(customFieldValuePersister, genericConfigManager, textFieldCharacterLengthValidator,
        jiraAuthenticationContext);
    this.hierarchyLinkManager = hierarchyLinkManager;
    this.jiraAuthenticationContext = jiraAuthenticationContext;
    this.hierarchyFieldSettingsService = hierarchyFieldSettingsService;
    this.httpContext = httpContext;
  }

  @Nonnull
  @Override
  public Map<String, Object> getVelocityParameters(Issue issue, CustomField field,
      FieldLayoutItem fieldLayoutItem) {
    Map<String, Object> params = super.getVelocityParameters(issue, field, fieldLayoutItem);
    if (issue == null)
      return params;
    String baseUrl = ComponentAccessor.getApplicationProperties().getString("jira.baseurl");
    IssueManager issueManager = ComponentAccessor.getIssueManager();

    Issue destinationIssue =
        field.hasValue(issue) ? issueManager.getIssueObject(field.getValue(issue).toString())
            : null;
    if (destinationIssue != null) {
      params.put("destinationIssueIconUrl", baseUrl + destinationIssue.getIssueType().getIconUrl());
      params
          .put("destinationIssueIconDescription", destinationIssue.getIssueType().getDescription());
      params.put("destinationIssueSummary", destinationIssue.getSummary());
    }
    params.put("jqlStatement",
        hierarchyFieldSettingsService.getFieldSettings(field.getIdAsLong()).getJqlStatement());
    params.put("issue", issue);
    return params;
  }

  @Override
  public void createValue(CustomField field, Issue issue, @NotNull String parent) {
    Preconditions.checkNotNull(issue);
    Preconditions.checkNotNull(parent);
    Issue parentIssue = ComponentAccessor.getIssueManager().getIssueObject(parent);
    try {
      hierarchyLinkManager
          .associateIssueWithParent(jiraAuthenticationContext.getLoggedInUser(), issue, parentIssue, field);
      super.createValue(field, issue, parent);
    } catch (CreateException e) {
      log.error("Failed to create link...");
    }
  }

  @Override
  public void updateValue(CustomField customField, Issue issue, String parent) {
    Preconditions.checkNotNull(issue);
    if (parent == null) {
      hierarchyLinkManager
          .disassociateParentFromIssue(jiraAuthenticationContext.getLoggedInUser(), issue, customField);
      super.updateValue(customField, issue, null);
    } else {
      hierarchyLinkManager
          .disassociateParentFromIssue(jiraAuthenticationContext.getLoggedInUser(), issue, customField);
      Issue parentIssue = ComponentAccessor.getIssueManager().getIssueObject(parent);
      try {
        hierarchyLinkManager
            .associateIssueWithParent(jiraAuthenticationContext.getLoggedInUser(), issue,
                parentIssue, customField);
        super.updateValue(customField, issue, parent);
      } catch (CreateException e) {
        log.error("Failed to create link: " + e.getMessage());
      }
    }
  }

  @Override
  public void validateFromParams(CustomFieldParams relevantParams,
      ErrorCollection errorCollectionToAddTo, FieldConfig config) {
    super.validateFromParams(relevantParams, errorCollectionToAddTo, config);
  }

  /**
   * Checks to see if the user is using the mobile application.
   * @return false if user is using the mobile app else true.
   */
  private Boolean isEnabled() {
    HttpServletRequest req = httpContext.getRequest();

    if (req != null) {
      Enumeration<String> headerNames = req.getHeaderNames();

      if (headerNames != null) {
        while (headerNames.hasMoreElements()) {
          String headerName = headerNames.nextElement();
          log.error(headerName + ": " + req.getHeader(headerName));
          if (headerName.equals("mobile-app-request"))
            return false;
        }
      }
    }
    return true;
  }
}