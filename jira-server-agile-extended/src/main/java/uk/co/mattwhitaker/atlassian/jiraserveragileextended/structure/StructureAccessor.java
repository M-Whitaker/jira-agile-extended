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

//    public static ForestSource getForest(long structureId) {
//        if (!isStructurePresent()) return null;
//        StructureComponents structureComponents;
//        try {
//            structureComponents = ComponentAccessor.getOSGiComponentInstanceOfType(StructureComponents.class);
//        } catch (Exception e) {
//            return null;
//        }
//
//
//        try {
//            return new ForestAccessor(structureComponents.getForestService().getForestSource(ForestSpec.structure(structureId)));
//        } catch (StructureException e) {
//            return null;
//        }
//    }
}
