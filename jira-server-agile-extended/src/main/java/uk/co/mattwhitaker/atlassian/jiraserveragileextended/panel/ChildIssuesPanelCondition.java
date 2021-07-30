package uk.co.mattwhitaker.atlassian.jiraserveragileextended.panel;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.HierarchyLinkTypeManager;

public class ChildIssuesPanelCondition extends AbstractWebCondition {

  private final HierarchyLinkTypeManager hierarchyLinkTypeManager;

  public ChildIssuesPanelCondition(
      @Autowired HierarchyLinkTypeManager hierarchyLinkTypeManager) {
    this.hierarchyLinkTypeManager = hierarchyLinkTypeManager;
  }

  /**
   * Checks if the issue has hierarchy child issues.
   *
   * @param applicationUser the current user
   * @param jiraHelper      jira helper
   * @return true if panel should display else false.
   */
  @Override
  public boolean shouldDisplay(ApplicationUser applicationUser, JiraHelper jiraHelper) {
    Issue issue = (Issue) jiraHelper.getContextParams().get("issue");
    IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();

    List<IssueLink> childIssues = new ArrayList<>();
    List<IssueLinkType> hierarchyLinks = hierarchyLinkTypeManager.getHierarchyLinkTypes();
    if (!hierarchyLinks.isEmpty()) {
      for (IssueLinkType hierarchyLink : hierarchyLinks) {
        issueLinkManager.getIssueLinks(hierarchyLink.getId()).forEach(issueLink -> {
          if (!issueLink.getSourceId().equals(issue.getId())) {
            childIssues.add(issueLink);
          }
        });
      }
      return !childIssues.isEmpty();
    }
    else
      return false;
  }
}
