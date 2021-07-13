package uk.co.mattwhitaker.atlassian.jiraserveragileextended.panel;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.LinkCollection;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.issuelink.HierarchyIssueLinkType;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.LinkType;

public class ChildIssuesPanel extends AbstractJiraContextProvider {

  /**
   * Get the velocity parameters.
   *
   * @param applicationUser the current user.
   * @param jiraHelper      jira helper class
   * @return a map of variables to use in velocity template.
   */
  @Override
  public Map<String, Object> getContextMap(ApplicationUser applicationUser, JiraHelper jiraHelper) {
    IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
    Issue currentIssue = (Issue) jiraHelper.getContextParams().get("issue");
    Map<String, Object> contextMap = new HashMap<>();
    Map<Long, LinkType> linkTypes = new HashMap<>();

    LinkCollection issueLinks = issueLinkManager
        .getLinkCollection(currentIssue, applicationUser, false);
    for (IssueLinkType issueLinkType : issueLinks.getLinkTypes()) {
      String issueLinkTypeStyle = issueLinkType.getStyle();
      if (issueLinkTypeStyle != null) {
        if (issueLinkTypeStyle.equals(HierarchyIssueLinkType.LINK_STYLE)) {
          List<Issue> issues = issueLinks.getInwardIssues(issueLinkType.getName());
          if (issues != null) {
            for (Issue issue : issues) {
              if (linkTypes.containsKey(issueLinkType.getId())) {
                linkTypes.get(issueLinkType.getId()).getIssues().add(issue);
              } else {
                LinkType newLinkType = new LinkType(issueLinkType.getOutward(),
                    issueLinkType.getId());
                newLinkType.getIssues().add(issue);
                linkTypes.put(issueLinkType.getId(), newLinkType);
              }
            }
          }
        }
      }
    }
    contextMap.put("linkTypes", linkTypes);
    contextMap
        .put("baseUrl", ComponentAccessor.getApplicationProperties().getString("jira.baseurl"));
    return contextMap;
  }
}
