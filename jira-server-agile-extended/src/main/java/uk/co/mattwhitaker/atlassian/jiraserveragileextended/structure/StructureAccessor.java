package uk.co.mattwhitaker.atlassian.jiraserveragileextended.structure;

import com.atlassian.jira.component.ComponentAccessor;

public class StructureAccessor {

  public static boolean isStructurePresent() {
    if (!ComponentAccessor.getPluginAccessor().isPluginEnabled("com.almworks.jira.structure")) {
      return false;
    }
    try {
      Class.forName("com.almworks.jira.structure.api.StructureComponents");
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
