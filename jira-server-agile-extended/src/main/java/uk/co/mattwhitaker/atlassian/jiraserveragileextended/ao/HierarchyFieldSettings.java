package uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao;

import net.java.ao.OneToMany;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table("Hierarchy")
@Preload
public interface HierarchyFieldSettings extends RawEntity<Integer> {

  @NotNull
  @PrimaryKey("FIELD_ID")
  Long getFieldID();

  CustomField getCustomField();

  @OneToMany
  IssueType[] getIssueTypes();

  @OneToMany
  Project[] getProjects();

  IssueLink getIssueLink();

  String getInwardLink();

  String getOutwardLink();

  String getJqlStatement();

  void setFieldID(Long fieldID);

  void setCustomField(CustomField customField);

  //  void setIssueTypes(IssueType[] issueTypes);
//  void setProjects(List<String> projects);
  void setIssueLink(IssueLink issueLink);

  void setInwardLink(String inwardLink);

  void setOutwardLink(String outwardLink);

  void setJqlStatement(String jqlStatement);
}
