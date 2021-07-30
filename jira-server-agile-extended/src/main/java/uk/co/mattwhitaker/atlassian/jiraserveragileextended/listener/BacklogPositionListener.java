package uk.co.mattwhitaker.atlassian.jiraserveragileextended.listener;

import com.atlassian.jira.bc.issue.properties.IssuePropertyService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.entity.property.EntityPropertyService;
import com.atlassian.jira.entity.property.EntityPropertyService.PropertyResult;
import com.atlassian.jira.entity.property.EntityPropertyService.SetPropertyValidationResult;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.search.DocumentWithId;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchQuery;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.search.providers.LuceneSearchProvider;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BacklogPositionListener {

  private static final Logger log = LoggerFactory.getLogger(BacklogPositionListener.class);

  private final JiraAuthenticationContext jiraAuthenticationContext;
  private final IssueManager issueManager;
  private final IssuePropertyService issuePropertyService;
  private final JqlQueryParser jqlQueryParser;
  private final LuceneSearchProvider searchProvider;

  @Autowired
  public BacklogPositionListener(@ComponentImport JqlQueryParser jqlQueryParser,
      @ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
      @ComponentImport IssueManager issueManager,
      @ComponentImport IssuePropertyService issuePropertyService) {
    this.jqlQueryParser = jqlQueryParser;
    this.jiraAuthenticationContext = jiraAuthenticationContext;
    this.issueManager = issueManager;
    this.issuePropertyService = issuePropertyService;
    this.searchProvider = ComponentAccessor.getComponent(LuceneSearchProvider.class);
  }

  public void calculateRanks(IssueEvent issueEvent) {
    java.util.List<GenericValue> changeLog = new ArrayList<>();
    try {
      changeLog = issueEvent.getChangeLog().getRelated("ChildChangeItem");
    } catch (GenericEntityException e) {
      e.printStackTrace();
    }
    for (GenericValue value: changeLog) {
      if (value.getAllFields().get("field").equals("Rank")) {
        rankIssues("filter=10000 AND issuetype not in (Epic, subTaskIssueTypes()) AND resolution IS EMPTY ORDER BY rank");
      }
    }
  }

  private void rankIssues(String jqlStatement) {
    log.debug("Ranking issues in {}", jqlStatement);
    List<DocumentWithId> issues = new ArrayList<>();
    try {
      issues = getIssues(jqlStatement);
    } catch (JqlParseException | SearchException e) {
      e.printStackTrace();
    }
    for (int i = 0, issuesSize = issues.size(); i < issuesSize; i++) {
      DocumentWithId document = issues.get(i);
      Issue issue = issueManager
          .getIssueObject(document.getDocument().getField("key").stringValue());
      try {
        publishIssueProperties(issue, jiraAuthenticationContext.getLoggedInUser(),
            String.format("%d/%d", i + 1, issuesSize));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }

  private void publishIssueProperties(Issue issue, ApplicationUser user, String backlogPosition)
      throws JSONException {
    JSONObject json = new JSONObject();
    json.put("backlogPosition", backlogPosition);
    EntityPropertyService.PropertyInput propertyInput = new EntityPropertyService.PropertyInput(json.toString(), "agileExtendedBacklogPosition");
    SetPropertyValidationResult validationResult = issuePropertyService.validateSetProperty(user, issue.getId(), propertyInput);
    if (validationResult.isValid()) {
      PropertyResult result = issuePropertyService.setProperty(user, validationResult);
      ErrorCollection errors = result.getErrorCollection();
      if (errors.hasAnyErrors()) {
        log.error(String.valueOf(errors));
      }
    } else {
      log.error(String.valueOf(validationResult.getErrorCollection()));
    }
  }

  private SearchQuery validateQuery(String jqlStatement) throws JqlParseException {
    SearchQuery query = SearchQuery.create(jqlQueryParser.parseQuery(jqlStatement),
        jiraAuthenticationContext.getLoggedInUser());
    query.overrideSecurity(true);
    return query;
  }

  private List<DocumentWithId> getIssues(String jqlStatement)
      throws JqlParseException, SearchException {
    SearchQuery query = validateQuery(jqlStatement);
    // ~ 32 seconds (2000 issues with key field)
    SearchResults<DocumentWithId> results = searchProvider
        .search(query, PagerFilter.getUnlimitedFilter(), new HashSet<String>(
            Collections.singleton("key")));
    return results.getResults();
  }
}
