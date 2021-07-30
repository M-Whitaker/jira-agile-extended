package uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface IssueType extends Entity {

  Long getIssueTypeID();

  HierarchyFieldSettings getHierarchyFieldSettings();

  void setIssueTypeID(Long IssueTypeID);

  void setHierarchyFieldSettings(HierarchyFieldSettings hierarchyFieldSettings);
}
