package uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao;

import static com.google.common.base.Preconditions.checkNotNull;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Named;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.CustomFieldBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.IssueLinkBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.IssueTypeBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.ProjectBean;

@Named
public class HierarchyFieldSettingsServiceImpl implements HierarchyFieldSettingsService {

  private final ActiveObjects ao;

  @Autowired
  public HierarchyFieldSettingsServiceImpl(@ComponentImport ActiveObjects ao) {
    this.ao = checkNotNull(ao);
  }

  @Override
  public HierarchyFieldSettings addFieldSettings(Long cfID, CustomFieldBean customFieldBean,
      List<IssueTypeBean> issueTypes, List<ProjectBean> projects, IssueLinkBean issueLinkBean,
      String inwardLink,
      String outwardLink, String jqlStatement) {
    final HierarchyFieldSettings hierarchyFieldSettings = ao
        .create(HierarchyFieldSettings.class, new DBParam("FIELD_ID", cfID));
    hierarchyFieldSettings.setCustomField(addCustomField(customFieldBean));
    removeIssueTypes(hierarchyFieldSettings);
    for (IssueTypeBean issueTypeID : issueTypes) {
      addIssueType(issueTypeID.getId(), hierarchyFieldSettings);
    }
    removeProjects(hierarchyFieldSettings);
    for (ProjectBean projectID : projects) {
      addProject(projectID.getId(), hierarchyFieldSettings);
    }
    hierarchyFieldSettings.setIssueLink(addIssueLink(issueLinkBean));
    hierarchyFieldSettings.setInwardLink(inwardLink);
    hierarchyFieldSettings.setOutwardLink(outwardLink);
    hierarchyFieldSettings.setJqlStatement(jqlStatement);
    hierarchyFieldSettings.save();
    return hierarchyFieldSettings;
  }

  @Override
  public HierarchyFieldSettings editFieldSettings(Long cfID, CustomFieldBean customFieldBean,
      List<IssueTypeBean> issueTypes,
      List<ProjectBean> projects, IssueLinkBean issueLinkBean, String inwardLink,
      String outwardLink,
      String jqlStatement) {
    final List<HierarchyFieldSettings> hierarchyFieldSettingsArray = new ArrayList<>(Arrays.asList(
        ao.find(HierarchyFieldSettings.class, Query.select().where("FIELD_ID = ?", cfID))));
    if (hierarchyFieldSettingsArray.size() != 1) {
      throw new RuntimeException("More than one field / no field found");
    } else {
      HierarchyFieldSettings hierarchyFieldSettings = hierarchyFieldSettingsArray.get(0);
      if (customFieldBean != null) {
        hierarchyFieldSettings.setCustomField(addCustomField(customFieldBean));
      }
      if (issueTypes != null) {
        removeIssueTypes(hierarchyFieldSettings);
        for (IssueTypeBean issueTypeID : issueTypes) {
          addIssueType(issueTypeID.getId(), hierarchyFieldSettings);
        }
      }
      if (projects != null) {
        removeProjects(hierarchyFieldSettings);
        for (ProjectBean projectID : projects) {
          addProject(projectID.getId(), hierarchyFieldSettings);
        }
      }
      if (issueLinkBean != null) {
        hierarchyFieldSettings.setIssueLink(addIssueLink(issueLinkBean));
      }
      if (inwardLink != null) {
        hierarchyFieldSettings.setInwardLink(inwardLink);
      }
      if (outwardLink != null) {
        hierarchyFieldSettings.setOutwardLink(outwardLink);
      }
      if (jqlStatement != null) {
        hierarchyFieldSettings.setJqlStatement(jqlStatement);
      }
      hierarchyFieldSettings.save();
      return hierarchyFieldSettings;
    }
  }

  @Override
  public void removeCustomField(Long cfID) {
    HierarchyFieldSettings hierarchyFieldSettings = getOneResponse(
        ao.find(HierarchyFieldSettings.class, Query.select().where("FIELD_ID = ?", cfID)));
    if (hierarchyFieldSettings != null) {
      ao.delete(
          ao.find(Project.class, Query.select()
              .where("HIERARCHY_FIELD_SETTINGS_ID = ?",
                  hierarchyFieldSettings)));
      ao.delete(ao.find(IssueType.class,
          Query.select().where("HIERARCHY_FIELD_SETTINGS_ID = ?", hierarchyFieldSettings)));
      ao.delete(hierarchyFieldSettings);
    }
  }

  @Override
  public CustomField addCustomField(CustomFieldBean customFieldBean) {
    final CustomField customField = ao.create(CustomField.class);
    customField.setCfIdAsLong(customFieldBean.getIdAsLong());
    customField.setName(customFieldBean.getName());
    customField.save();
    return customField;
  }

  @Override
  public void removeCustomField(CustomFieldBean customFieldBean) {
    ao.delete(getOneResponse(ao.find(CustomField.class,
        Query.select().where("CF_ID_AS_LONG = ?", customFieldBean.getIdAsLong()))));
  }

  @Override
  public IssueLink addIssueLink(IssueLinkBean issueLinkBean) {
    final IssueLink issueLink = ao.create(IssueLink.class);
    issueLink.setIssueLinkIdAsLong(issueLinkBean.getIdAsLong());
    issueLink.setName(issueLinkBean.getName());
    issueLink.save();
    return issueLink;
  }

  @Override
  public void removeIssueLink(IssueLinkBean issueLinkBean) {
    ao.delete(getOneResponse(ao.find(CustomField.class,
        Query.select()
            .where("ISSUE_LINK_ID_AS_LONG = ?", issueLinkBean.getIdAsLong()))));
  }

  @Override
  public IssueType addIssueType(Long issueTypeID, HierarchyFieldSettings hierarchyFieldSettings) {
    IssueType[] issueTypes = ao.find(IssueType.class, Query.select()
        .where("ISSUE_TYPE_ID = ? AND HIERARCHY_FIELD_SETTINGS_ID = ?", issueTypeID,
            hierarchyFieldSettings));
    if (issueTypes.length == 1) {
      return issueTypes[0];
    }
    IssueType issueType = ao.create(IssueType.class);
    issueType.setIssueTypeID(issueTypeID);
    issueType.setHierarchyFieldSettings(hierarchyFieldSettings);
    issueType.save();
    return issueType;
  }

  @Override
  public void removeIssueTypes(HierarchyFieldSettings hierarchyFieldSettings) {
    ao.delete(ao.find(IssueType.class, Query.select().where("HIERARCHY_FIELD_SETTINGS_ID = ?",
        hierarchyFieldSettings)));
  }

  @Override
  public Project addProject(Long projectID, HierarchyFieldSettings hierarchyFieldSettings) {
    Project[] projects;
    if ((projects = ao.find(Project.class,
        Query.select()
            .where("PROJECT_ID = ?  AND HIERARCHY_FIELD_SETTINGS_ID = ?", projectID,
                hierarchyFieldSettings))).length
        == 1) {
      return projects[0];
    } else {
      Project project = ao.create(Project.class, new DBParam("PROJECT_ID", projectID));
      project.setHierarchyFieldSettings(hierarchyFieldSettings);
      project.save();
      return project;
    }
  }

  @Override
  public void removeProjects(HierarchyFieldSettings hierarchyFieldSettings) {
    ao.delete(ao.find(Project.class, Query.select().where("HIERARCHY_FIELD_SETTINGS_ID = ?",
        hierarchyFieldSettings)));
  }

  @Override
  public HierarchyFieldSettings getFieldSettings(Long id) {
    Query query = Query.select().where("FIELD_ID = ?", id);
    HierarchyFieldSettings[] hierarchyFieldSettings = ao.find(HierarchyFieldSettings.class, query);
    return getOneResponse(hierarchyFieldSettings);
  }

  private <T> T getOneResponse(T[] settings) {
    if (settings.length == 0) {
      return null;
    } else if (settings.length == 1) {
      return settings[0];
    } else {
      throw new IllegalStateException("There shouldn't be 2 rows the same! ");
    }
  }

  @Override
  public List<HierarchyFieldSettings> getAllFieldSettings() {
    return new ArrayList<>(Arrays.asList(ao.find(HierarchyFieldSettings.class)));
  }

  @Override
  public void removeAll() {
    ao.delete(ao.find(IssueType.class));
    ao.delete(ao.find(Project.class));
    ao.delete(ao.find(HierarchyFieldSettings.class));
    ao.delete(ao.find(CustomField.class));
    ao.delete(ao.find(IssueLink.class));
  }
}