package uk.co.mattwhitaker.atlassian.jiraserveragileextended.panel;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.customfield.HierarchyLinkField;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.JAECustomFieldManager;

public class ParentPanelCondition extends AbstractWebCondition {

  public final JAECustomFieldManager jaeCustomFieldManager;

  public ParentPanelCondition(@Autowired JAECustomFieldManager jaeCustomFieldManager) {
    this.jaeCustomFieldManager = jaeCustomFieldManager;
  }

  /**
   * Check if the issue has any value in hierarchy fields.
   * @param applicationUser the current user.
   * @param jiraHelper      jira helper class
   * @return true if panel should display else false.
   */
  @Override
  public boolean shouldDisplay(ApplicationUser applicationUser, JiraHelper jiraHelper) {
    Issue currentIssue = (Issue) jiraHelper.getContextParams().get("issue");
    List<CustomField> hierarchyLinkFields = jaeCustomFieldManager.getHierarchyFields(currentIssue);
    for (CustomField hierarchyLinkField:hierarchyLinkFields) {
      if (jaeCustomFieldManager.getIssueFromField(currentIssue, hierarchyLinkField) != null)
        return true;
    }
    return false;
  }
}
