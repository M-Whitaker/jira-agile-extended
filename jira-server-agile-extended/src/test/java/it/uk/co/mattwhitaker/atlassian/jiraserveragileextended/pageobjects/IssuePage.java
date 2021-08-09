package it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.pageobjects;

import com.atlassian.jira.pageobjects.pages.AbstractJiraPage;
import com.atlassian.pageobjects.elements.ElementBy;
import com.atlassian.pageobjects.elements.PageElement;
import com.atlassian.pageobjects.elements.query.TimedCondition;

public class IssuePage extends AbstractJiraPage {

  @ElementBy(id = "hierarchyField")
  protected PageElement field;

  @Override
  public TimedCondition isAt() {
    return field.timed().isVisible();
  }

  @Override
  public String getUrl() {
    return "/browse/TEST-1";
  }
}
