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
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.customfield.HierarchyLinkField;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.Field;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.JAECustomFieldManager;

public class ParentPanel extends AbstractJiraContextProvider {

  public final IssueManager issueManager;
  public final JAECustomFieldManager jaeCustomFieldManager;

  public ParentPanel(@ComponentImport IssueManager issueManager, @Autowired JAECustomFieldManager jaeCustomFieldManager) {
    this.issueManager = issueManager;
    this.jaeCustomFieldManager = jaeCustomFieldManager;
  }

  @Override
  public Map<String, Object> getContextMap(ApplicationUser applicationUser, JiraHelper jiraHelper) {
    Map<String, Object> contextMap = new HashMap<>();
    CustomField parentLinkField = jaeCustomFieldManager.getOrCreateHierarchyField("Parent Link",
        HierarchyLinkField.CUSTOM_FIELD_TYPE);
    Issue currentIssue = (Issue) jiraHelper.getContextParams().get("issue");
    Issue parentIssue = jaeCustomFieldManager.getIssueFromField(currentIssue, parentLinkField);
    if (parentIssue != null) {
      contextMap.put("fields", getFieldValues(parentIssue));
    }
    return contextMap;
  }

  private List<Field> getFieldValues(Issue issue) {
    List<Field> fields = new ArrayList<>();
    fields.add(new Field("Key", issue.getKey()));
    fields.add(new Field("Summary", issue.getSummary()));
//    switch (issue.getIssueType().getName()) {
//      case "Initiative": {
//
//      }
//    }

    return fields;
  }
}
