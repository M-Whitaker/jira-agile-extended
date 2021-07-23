package uk.co.mattwhitaker.atlassian.jiraserveragileextended.panel;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.Field;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.JAECustomFieldManager;

public class ParentPanel extends AbstractJiraContextProvider {

  public final IssueManager issueManager;
  public final JAECustomFieldManager jaeCustomFieldManager;

  public ParentPanel(@ComponentImport IssueManager issueManager,
      @Autowired JAECustomFieldManager jaeCustomFieldManager) {
    this.issueManager = issueManager;
    this.jaeCustomFieldManager = jaeCustomFieldManager;
  }

  /**
   * Get the velocity parameters.
   *
   * @param applicationUser the current user.
   * @param jiraHelper      jira helper class
   * @return a map of variables to use in velocity template.
   */
  @Override
  public Map<String, Object> getContextMap(ApplicationUser applicationUser, JiraHelper jiraHelper) {
    Map<String, Object> contextMap = new HashMap<>();
    Map<CustomField, List<Field>> linkTypes = new HashMap<>();
    Issue currentIssue = (Issue) jiraHelper.getContextParams().get("issue");
    List<CustomField> hierarchyLinkFields = jaeCustomFieldManager.getHierarchyFields(currentIssue);
    for (CustomField hierarchyLinkField : hierarchyLinkFields) {
      Issue parentIssue = jaeCustomFieldManager.getIssueFromField(currentIssue, hierarchyLinkField);
      if (parentIssue != null) {
        linkTypes.put(hierarchyLinkField, getFieldValues(parentIssue));
      }
    }
    contextMap.put("linkTypes", linkTypes);
    return contextMap;
  }

  private List<Field> getFieldValues(Issue issue) {
    List<Field> fields = new ArrayList<>();
    fields.add(new Field("Key", issue.getKey()));
    fields.add(new Field("Summary", issue.getSummary()));
    return fields;
  }
}
