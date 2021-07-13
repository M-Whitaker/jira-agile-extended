package uk.co.mattwhitaker.atlassian.jiraserveragileextended.jql;

import com.atlassian.jira.JiraDataType;
import com.atlassian.jira.JiraDataTypes;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.jql.operand.QueryLiteral;
import com.atlassian.jira.jql.query.QueryCreationContext;
import com.atlassian.jira.plugin.jql.function.AbstractJqlFunction;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.query.clause.TerminalClause;
import com.atlassian.query.operand.FunctionOperand;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.HierarchyLinkManager;

public class HierarchyChildIssues extends AbstractJqlFunction {

  private static final Logger log = LoggerFactory.getLogger(HierarchyChildIssues.class);

  private final IssueManager issueManager;
  private final HierarchyLinkManager hierarchyLinkManager;
  private final JiraAuthenticationContext jiraAuthenticationContext;

  public HierarchyChildIssues(@ComponentImport IssueManager issueManager,
      @Autowired HierarchyLinkManager hierarchyLinkManager,
      @ComponentImport JiraAuthenticationContext jiraAuthenticationContext) {
    this.issueManager = issueManager;
    this.hierarchyLinkManager = hierarchyLinkManager;
    this.jiraAuthenticationContext = jiraAuthenticationContext;
  }

  /**
   * Validates the parameters of the function.
   * @param applicationUser the current user.
   * @param functionOperand Represents the right hand side value of a clause.
   * @param terminalClause Denotes a terminal nodes that contain an Operator and an Operand.
   * @return if the function is valid.
   */
  @Nonnull
  @Override
  public MessageSet validate(ApplicationUser applicationUser,
      @Nonnull FunctionOperand functionOperand, @Nonnull TerminalClause terminalClause) {
    return validateNumberOfArgs(functionOperand, 1);
  }

  /**
   * Gets the child issues of a given issue.
   *
   * @param queryCreationContext encapsulates the context required when creating queries in the JQL way
   * @param operand Represents the right hand side value of a clause.
   * @param terminalClause Denotes a terminal nodes that contain an Operator and an Operand.
   * @return A list of issues that are child issues of the parameter.
   */
  public List<QueryLiteral> getValues(QueryCreationContext queryCreationContext,
      FunctionOperand operand, TerminalClause terminalClause) {
    Issue inputIssue = issueManager.getIssueObject(Iterables.get(operand.getArgs(), 0));
    Set<Issue> issues = hierarchyLinkManager
        .getRecursiveChildIssues(inputIssue, jiraAuthenticationContext.getLoggedInUser());
    List<QueryLiteral> literals = new ArrayList<>();

    for (Issue issue : issues) {
      literals.add(new QueryLiteral(operand, issue.getKey()));
    }
    return literals;
  }

  public int getMinimumNumberOfExpectedArguments() {
    return 1;
  }

  @Override
  public String getFunctionName() {
    return "agileChildIssuesOf";
  }

  public JiraDataType getDataType() {
    return JiraDataTypes.ISSUE;
  }
}