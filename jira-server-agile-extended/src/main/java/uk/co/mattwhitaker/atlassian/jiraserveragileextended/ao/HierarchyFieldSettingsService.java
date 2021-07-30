package uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao;

import com.atlassian.activeobjects.tx.Transactional;
import java.util.List;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.CustomFieldBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.IssueLinkBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.IssueTypeBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.ProjectBean;

@Transactional
public interface HierarchyFieldSettingsService {

  HierarchyFieldSettings addFieldSettings(Long cfID, CustomFieldBean customFieldBean,
      List<IssueTypeBean> issueTypes,
      List<ProjectBean> projects, IssueLinkBean issueLinkBean, String inwardLink,
      String outwardLink,
      String jqlStatement);

  HierarchyFieldSettings editFieldSettings(Long cfID, CustomFieldBean customFieldBean,
      List<IssueTypeBean> issueTypes,
      List<ProjectBean> projects, IssueLinkBean issueLinkBean, String inwardLink,
      String outwardLink,
      String jqlStatement);

  HierarchyFieldSettings getFieldSettings(Long id);

  List<HierarchyFieldSettings> getAllFieldSettings();

  void removeAll();

  CustomField addCustomField(CustomFieldBean customFieldBean);

  void removeCustomField(Long cfID);

  void removeCustomField(CustomFieldBean customFieldBean);

  IssueLink addIssueLink(IssueLinkBean issueLinkBean);

  void removeIssueLink(IssueLinkBean issueLinkBean);

  IssueType addIssueType(Long issueTypeID, HierarchyFieldSettings hierarchyFieldSettings);

  void removeIssueTypes(HierarchyFieldSettings hierarchyFieldSettings);

  Project addProject(Long projectID, HierarchyFieldSettings hierarchyFieldSettings);

  void removeProjects(HierarchyFieldSettings hierarchyFieldSettings);
}