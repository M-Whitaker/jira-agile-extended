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
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.issuelink.HierarchyIssueLinkType;

public class ChildIssuesPanelCondition extends AbstractWebCondition {

  private final HierarchyIssueLinkType hierarchyIssueLinkType;

  public ChildIssuesPanelCondition(@Autowired HierarchyIssueLinkType hierarchyIssueLinkType) {
    this.hierarchyIssueLinkType = hierarchyIssueLinkType;
  }

  @Override
  public boolean shouldDisplay(ApplicationUser applicationUser, JiraHelper jiraHelper) {
    Issue issue = (Issue) jiraHelper.getContextParams().get("issue");
    IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();

    List<IssueLink> childIssues = new ArrayList<>();
    IssueLinkType hierarchyLink = hierarchyIssueLinkType.getOrCreateHierarchyLinkType("");
    issueLinkManager.getIssueLinks(hierarchyLink.getId()).forEach(issueLink -> {
      if (!issueLink.getSourceId().equals(issue.getId())) {
        childIssues.add(issueLink);
      }
    });
    return !childIssues.isEmpty();
  }
}
