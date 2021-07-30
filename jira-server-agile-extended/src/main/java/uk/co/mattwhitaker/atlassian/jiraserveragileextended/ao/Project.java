package uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface Project extends Entity {

  Long getProjectID();

  HierarchyFieldSettings getHierarchyFieldSettings();

  void setProjectID(Long projectID);

  void setHierarchyFieldSettings(HierarchyFieldSettings hierarchyFieldSettings);
}
