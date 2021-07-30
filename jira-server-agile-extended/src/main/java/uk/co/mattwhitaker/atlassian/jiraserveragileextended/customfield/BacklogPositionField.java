package uk.co.mattwhitaker.atlassian.jiraserveragileextended.customfield;

import com.atlassian.jira.bc.issue.properties.IssuePropertyService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.entity.property.EntityProperty;
import com.atlassian.jira.entity.property.EntityPropertyService.PropertyResult;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.CalculatedCFType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.search.DocumentWithId;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchQuery;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.search.providers.LuceneSearchProvider;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.admin.BacklogAdminServlet;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.PropertyDao;

@Component
public class BacklogPositionField extends CalculatedCFType {

  private static final Logger log = LoggerFactory.getLogger(BacklogPositionField.class);

  private final JiraAuthenticationContext jiraAuthenticationContext;
  private final IssuePropertyService issuePropertyService;
  private final JqlQueryParser jqlQueryParser;
  private final LuceneSearchProvider searchProvider;
  private final PropertyDao propertyDao;

  @Autowired
  public BacklogPositionField(@ComponentImport JqlQueryParser jqlQueryParser,
      @ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
      @ComponentImport IssuePropertyService issuePropertyService, @Autowired PropertyDao propertyDao) {
    this.jqlQueryParser = jqlQueryParser;
    this.jiraAuthenticationContext = jiraAuthenticationContext;
    this.searchProvider = ComponentAccessor.getComponent(LuceneSearchProvider.class);
    this.issuePropertyService = issuePropertyService;
    this.propertyDao = propertyDao;
  }

  @NotNull
  @Override
  public Map<String, Object> getVelocityParameters(Issue issue, CustomField field,
      FieldLayoutItem fieldLayoutItem) {
    Map<String, Object> params =  super.getVelocityParameters(issue, field, fieldLayoutItem);
    params.put("issue", issue);
    params.put("clientRefresh", propertyDao.getLongProperty(BacklogAdminServlet.KEY_DEFAULT_BACKLOG_CLIENT_REFRESH) * 1000);
    return params;
  }

  @Override
  public String getStringFromSingularObject(Object o) {
    return o.toString();
  }

  @Override
  public Object getSingularObjectFromString(String s) throws FieldValidationException {
    return s;
  }

  @Nullable
  @Override
  public Object getValueFromIssue(CustomField customField, Issue issue) {
    PropertyResult result = issuePropertyService.getProperty(jiraAuthenticationContext.getLoggedInUser(), issue.getId(), "agileExtendedBacklogPosition");
    EntityProperty backlogPosition = result.getEntityProperty().getOrNull();
    if (backlogPosition != null) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        Map json = mapper.readValue(backlogPosition.getValue(), Map.class);
        return json.get("backlogPosition");
      } catch (IOException e) {
        e.printStackTrace();
        return "0/0";
      }
    } else {
      return null;
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
        .search(query, PagerFilter.getUnlimitedFilter(), new HashSet<>());
    return results.getResults();
  }

  private Long getNumberOfIssues(String jqlStatement) throws JqlParseException, SearchException {
    SearchQuery query = validateQuery(jqlStatement);
    return searchProvider.getHitCount(query);
  }

  private String getPositionInBacklog(Issue issue, String backlog) {
    String jqlFilter = "filter=10100 AND issuetype != \"Epic\" ORDER BY rank";
    List<DocumentWithId> issues = new ArrayList<>();
    long numberOfIssues = 0L;
    int backlogPosition = 0;

    try {
      issues = getIssues(jqlFilter);
      numberOfIssues = issues.size();
//      numberOfIssues = getNumberOfIssues(jqlFilter);
    } catch (JqlParseException | SearchException e) {
      e.printStackTrace();
    }
    return String.format("%d/%d", backlogPosition + 1, numberOfIssues);
  }
}
