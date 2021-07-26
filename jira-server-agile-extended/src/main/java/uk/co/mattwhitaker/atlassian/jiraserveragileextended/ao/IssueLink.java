package uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao;

import net.java.ao.Entity;

public interface IssueLink extends Entity {
  Long getIssueLinkIdAsLong();
  String getName();

  void setIssueLinkIdAsLong(Long cfIdAsLong);
  void setName(String name);
}
