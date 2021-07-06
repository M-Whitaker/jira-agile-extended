package uk.co.mattwhitaker.atlassian.jiraserveragileextended.model;

import com.atlassian.jira.issue.Issue;
import java.util.ArrayList;
import java.util.List;

public class LinkType {

  private String name;
  private final long id;
  private final List<Issue> issues = new ArrayList<>();

  public LinkType(String name, long id) {
    this.name = name;
    this.id = id;
  }

  public List<Issue> getIssues() {
    return issues;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "LinkType{" +
        "name='" + name + '\'' +
        ", id=" + id +
        ", issues=" + issues +
        '}';
  }
}
