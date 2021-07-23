package uk.co.mattwhitaker.atlassian.jiraserveragileextended.service;

import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.LinkCollection;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.issuelink.HierarchyIssueLinkType;

@Service
public class HierarchyLinkManager {

  private static final Logger log = LoggerFactory.getLogger(HierarchyLinkManager.class);

  private final HierarchyIssueLinkType hierarchyIssueLinkType;
  private final IssueLinkManager issueLinkManager;

  @Autowired
  public HierarchyLinkManager(
      @Autowired HierarchyIssueLinkType hierarchyIssueLinkType,
      @ComponentImport IssueLinkManager issueLinkManager) {
    this.hierarchyIssueLinkType = hierarchyIssueLinkType;
    this.issueLinkManager = issueLinkManager;
  }

  /**
   * Create hierarchy issue link between two issues.
   * @param applicationUser the current user.
   * @param issue the outgoing issue.
   * @param parentIssue the incoming issue.
   */
  public void associateIssueWithParent(
      ApplicationUser applicationUser, Issue issue, Issue parentIssue, CustomField field) throws CreateException {
    // Get the CustomField name to get the link type
    IssueLinkType hierarchyLink = hierarchyIssueLinkType.getHierarchyLinkTypeByName(field.getFieldName());
    issueLinkManager.createIssueLink(
        issue.getId(), parentIssue.getId(), hierarchyLink.getId(), 0L, applicationUser);
  }

  /**
   * Delete hierarchy issue link between two issues.
   * @param applicationUser the current user.
   * @param issue the outgoing issue.
   * @param parentIssue the incoming issue.
   */
  public void disassociateIssueWithParent(
      ApplicationUser applicationUser, Issue issue, Issue parentIssue, CustomField field) {
    IssueLinkType hierarchyLink = hierarchyIssueLinkType.getHierarchyLinkTypeByName(field.getFieldName());
    IssueLink issueLink =
        issueLinkManager.getIssueLink(issue.getId(), parentIssue.getId(), hierarchyLink.getId());
    try {
      issueLinkManager.removeIssueLink(issueLink, applicationUser);
    } catch (IllegalArgumentException e) {
      log.error("Cannot Remove issue link!");
    }
  }

  /**
   * Delete hierarchy issue link between two issues.
   * @param applicationUser the current user.
   * @param issue the outgoing issue.
   */
  public void disassociateParentFromIssue(ApplicationUser applicationUser, Issue issue, CustomField field) {
    // TODO: add checks
    List<Issue> outWardIssues =
        getIssueLinksForIssue(issue, false, LinkDirection.OUTWARD, applicationUser);
    log.debug(String.valueOf(outWardIssues));
    for (Issue outWardIssue : outWardIssues) {
      disassociateIssueWithParent(applicationUser, issue, outWardIssue, field);
    }
  }

  /**
   * Get hierarchy issue links for a given issue.
   * @param issue the issue to get links of.
   * @param excludeSystemLinks whether to exclude hidden link types.
   * @param linkDirection The link direction to retrieve
   * @param applicationUser the current user.
   * @return a list of issues that are linked to issue given parameters.
   */
  public List<Issue> getIssueLinksForIssue(
      Issue issue,
      boolean excludeSystemLinks,
      LinkDirection linkDirection,
      ApplicationUser applicationUser) {
    LinkCollection issueLinks =
        issueLinkManager.getLinkCollection(issue, applicationUser, excludeSystemLinks);
    List<Issue> issues = new ArrayList<>();
    for (IssueLinkType issueLinkType : issueLinks.getLinkTypes()) {
      String issueLinkTypeStyle = issueLinkType.getStyle();
      if (issueLinkTypeStyle != null) {
        if (issueLinkTypeStyle.equals(HierarchyIssueLinkType.LINK_STYLE)) {
          if (linkDirection == LinkDirection.OUTWARD) {
            issues = issueLinks.getOutwardIssues(issueLinkType.getName());
          } else {
            issues = issueLinks.getInwardIssues(issueLinkType.getName());
          }
        }
      }
    }
    return issues != null ? issues : new ArrayList<>();
  }


  /**
   * Get all child issues of a given link type for an issue recursively.
   * @param issue the root issue.
   * @param applicationUser the current user.
   * @return a set of issues which are descendants of the issue.
   */
  public Set<Issue> getRecursiveChildIssues(Issue issue, ApplicationUser applicationUser) {
    Set<Issue> remainingIssues = new HashSet<>();
    Set<Issue> visitedIssues = new HashSet<>();
    Set<Issue> childIssues = new HashSet<>(
        getIssueLinksForIssue(issue, false, LinkDirection.INWARD, applicationUser));
    int i = 0;
    while (!childIssues.isEmpty()) {
      childIssues.forEach(childIssue -> {
        remainingIssues.addAll(
            getIssueLinksForIssue(childIssue, false, LinkDirection.INWARD, applicationUser));
        visitedIssues.add(childIssue);
      });
      childIssues.removeAll(visitedIssues);
      log.debug("First Child Issues: " + childIssues);
      log.debug("First Remaining Issues" + remainingIssues);
      log.debug("First Visited Issues: " + visitedIssues);
      for (Issue remainingIssue : remainingIssues) {
        childIssues.addAll(
            getIssueLinksForIssue(remainingIssue, false, LinkDirection.INWARD, applicationUser));
        visitedIssues.add(remainingIssue);
      }
      remainingIssues.removeAll(visitedIssues);
      log.debug("----------------------------");
      log.debug("Second Child Issues: " + childIssues);
      log.debug("Child issues empty? " + childIssues.isEmpty());
      log.debug("Second Remaining Issues" + remainingIssues);
      log.debug("Second Visited Issues: " + visitedIssues);
      i++;
      if (i == 10000000) {
        break;
      }
    }
    log.debug(String.valueOf(i));
    return visitedIssues;
  }

  public enum LinkDirection {
    INWARD,
    OUTWARD
  }
}
