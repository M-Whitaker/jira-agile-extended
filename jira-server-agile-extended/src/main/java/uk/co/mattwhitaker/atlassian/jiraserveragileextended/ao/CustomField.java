package uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao;

import net.java.ao.Entity;

public interface CustomField extends Entity {
  Long getCfIdAsLong();
  String getName();

  void setCfIdAsLong(Long cfIdAsLong);
  void setName(String name);
}
