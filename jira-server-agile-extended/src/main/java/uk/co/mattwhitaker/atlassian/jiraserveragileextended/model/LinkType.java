package uk.co.mattwhitaker.atlassian.jiraserveragileextended.model;

import com.atlassian.jira.issue.Issue;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LinkType {
  private String name;
  private final long id;
  private final List<Issue> issues = new ArrayList<>();

  public LinkType(String name, long id) {
    this.name = name;
    this.id = id;
  }
}
