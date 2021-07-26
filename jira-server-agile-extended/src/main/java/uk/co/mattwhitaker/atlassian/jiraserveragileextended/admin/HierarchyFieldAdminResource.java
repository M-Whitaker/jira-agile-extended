package uk.co.mattwhitaker.atlassian.jiraserveragileextended.admin;


import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.IssueTypeService;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao.HierarchyFieldSettings;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao.HierarchyFieldSettingsService;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao.IssueType;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao.Project;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.customfield.HierarchyLinkField;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.CustomFieldBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.IssueLinkBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.IssueTypeBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.ProjectBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.HierarchyLinkTypeManager;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.JAECustomFieldManager;

@Path("hierarchyfield")
public class HierarchyFieldAdminResource {

  private static final Logger log = LoggerFactory.getLogger(HierarchyFieldAdminResource.class);


  private final UserManager userManager;
  private final TransactionTemplate transactionTemplate;
  private final JAECustomFieldManager jaeCustomFieldManager;
  private final HierarchyLinkTypeManager hierarchyLinkTypeManager;
  private final CustomFieldManager customFieldManager;
  private final IssueTypeService issueTypeService;
  private final ProjectService projectService;
  private final IssueTypeManager issueTypeManager;
  private final ProjectManager projectManager;
  private final HierarchyFieldSettingsService hierarchyFieldSettingsService;
  private final JiraAuthenticationContext jiraAuthenticationContext;

  @Autowired
  public HierarchyFieldAdminResource(@ComponentImport UserManager userManager,
      @ComponentImport TransactionTemplate transactionTemplate,
      @Autowired JAECustomFieldManager jaeCustomFieldManager,
      HierarchyLinkTypeManager hierarchyLinkTypeManager,
      @ComponentImport CustomFieldManager customFieldManager,
      @ComponentImport IssueTypeService issueTypeService,
      @ComponentImport ProjectService projectService,
      @ComponentImport IssueTypeManager issueTypeManager,
      @ComponentImport ProjectManager projectManager,
      @ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
      @Autowired HierarchyFieldSettingsService hierarchyFieldSettingsService) {
    this.userManager = userManager;
    this.transactionTemplate = transactionTemplate;
    this.jaeCustomFieldManager = jaeCustomFieldManager;
    this.hierarchyLinkTypeManager = hierarchyLinkTypeManager;
    this.customFieldManager = customFieldManager;
    this.issueTypeService = issueTypeService;
    this.projectService = projectService;
    this.issueTypeManager = issueTypeManager;
    this.projectManager = projectManager;
    this.hierarchyFieldSettingsService = hierarchyFieldSettingsService;
    this.jiraAuthenticationContext = jiraAuthenticationContext;
  }

  private HierarchyFieldConfigEditBean createJsonBean(Long id,
      HierarchyFieldSettings hierarchyFieldSettings) {
    List<IssueTypeBean> issueTypes = new ArrayList<>();
    for (IssueType issueType : hierarchyFieldSettings.getIssueTypes()) {
      issueTypes.add(new IssueTypeBean(issueType.getIssueTypeID(),
          issueTypeManager.getIssueType(String.valueOf(issueType.getIssueTypeID())).getName(),
          true));
    }
    List<ProjectBean> projects = new ArrayList<>();
    for (Project project : hierarchyFieldSettings.getProjects()) {
      projects.add(new ProjectBean(project.getProjectID(),
          Objects.requireNonNull(projectManager.getProjectObj(project.getProjectID())).getName(),
          true));
    }
    return new HierarchyFieldConfigEditBean(String.valueOf(id), new CustomFieldBean(
        String.valueOf(hierarchyFieldSettings.getCustomField().getCfIdAsLong()),
        hierarchyFieldSettings.getCustomField().getCfIdAsLong(),
        hierarchyFieldSettings.getCustomField().getName()),
        issueTypes, projects,
        new IssueLinkBean(
            String.valueOf(hierarchyFieldSettings.getIssueLink().getIssueLinkIdAsLong()),
            hierarchyFieldSettings.getIssueLink().getIssueLinkIdAsLong(),
            hierarchyFieldSettings.getIssueLink().getName()),
        hierarchyFieldSettings.getInwardLink(),
        hierarchyFieldSettings.getOutwardLink(), hierarchyFieldSettings.getJqlStatement());
  }

  private List<IssueTypeBean> addOtherIssueTypes(HierarchyFieldConfigEditBean bean) {
    List<IssueTypeBean> listOfIssueTypes = bean.getIssueTypes();
    for (com.atlassian.jira.issue.issuetype.IssueType issueType : issueTypeService.getIssueTypes(
        jiraAuthenticationContext.getLoggedInUser())) {
      if (bean.getIssueTypes().stream()
          .noneMatch(o -> o.getId().equals(Long.valueOf(issueType.getId())))) {
        listOfIssueTypes.add(
            new IssueTypeBean(Long.valueOf(issueType.getId()), issueType.getName(), false));
      }
    }
    return listOfIssueTypes;
  }

  private List<ProjectBean> addOtherProjects(HierarchyFieldConfigEditBean bean) {
    List<ProjectBean> listOfProjects = bean.getProjects();
    for (com.atlassian.jira.project.Project project : projectService.getAllProjects(
        jiraAuthenticationContext.getLoggedInUser()).get()) {
      if (bean.getProjects().stream().noneMatch(o -> o.getId().equals(project.getId()))) {
        listOfProjects.add(new ProjectBean(project.getId(), project.getName(), false));
      }
    }
    return listOfProjects;
  }

  @GET
  @Path("all")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAll(@Context HttpServletRequest request,
      @QueryParam("html") String htmlOutput) {
    if (!AdminUtils.checkAuthorized(request, userManager)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    return Response.ok(transactionTemplate.execute(new TransactionCallback() {
      public Object doInTransaction() {
        List<HierarchyFieldConfigEditBean> fields = new ArrayList<>();
        List<HierarchyFieldSettings> hierarchyFieldSettingsList = hierarchyFieldSettingsService.getAllFieldSettings();
        for (HierarchyFieldSettings hierarchyFieldSettings:hierarchyFieldSettingsList) {
          HierarchyFieldConfigEditBean bean = createJsonBean(
              hierarchyFieldSettings.getCustomField().getCfIdAsLong(), hierarchyFieldSettings);
          bean.setIssueTypes(addOtherIssueTypes(bean));
          bean.setProjects(addOtherProjects(bean));
          fields.add(bean);
        }
        return fields;
      }
    })).build();
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response get(@Context HttpServletRequest request, @PathParam("id") final Long id) {
    if (!AdminUtils.checkAuthorized(request, userManager)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    HierarchyFieldSettings hierarchyFieldSettings = hierarchyFieldSettingsService.getFieldSettings(
        id);
    if (hierarchyFieldSettings == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    return Response.ok(transactionTemplate.execute(new TransactionCallback() {
      public Object doInTransaction() {
        HierarchyFieldConfigEditBean bean = createJsonBean(
            hierarchyFieldSettings.getCustomField().getCfIdAsLong(), hierarchyFieldSettings);
        bean.setIssueTypes(addOtherIssueTypes(bean));
        bean.setProjects(addOtherProjects(bean));
        return bean;
      }
    })).build();
  }

  @PUT
  @Consumes("application/json")
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{id}")
  public Response update(@Context HttpServletRequest request, @PathParam("id") final Long id,
      final HierarchyFieldConfigEditBean bean) {
    if (!AdminUtils.checkAuthorized(request, userManager)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    if (id == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }

    log.debug("updating: {}; ID: {}", bean, id);

    //TODO: Check if the issue types and projects have changed...
    jaeCustomFieldManager.editHierarchyFieldConfigurationContext(
        Objects.requireNonNull(customFieldManager.getCustomFieldObject(id)), bean);
    if (bean.getInwardLink() != null || bean.getOutwardLink() != null) {
      jaeCustomFieldManager.editHierarchyField(
          Objects.requireNonNull(customFieldManager.getCustomFieldObject(id)), bean);
    }

    HierarchyFieldSettings hierarchyFieldSettings = hierarchyFieldSettingsService.editFieldSettings(
        id,
        bean.getCustomField(), bean.getIssueTypes(), bean.getProjects(),
        bean.getIssueLink(), bean.getInwardLink(), bean.getOutwardLink(), bean.getJqlStatement());

    return Response.ok(transactionTemplate.execute(new TransactionCallback() {
      public Object doInTransaction() {
        HierarchyFieldConfigEditBean bean = createJsonBean(
            hierarchyFieldSettings.getCustomField().getCfIdAsLong(), hierarchyFieldSettings);
        bean.setIssueTypes(addOtherIssueTypes(bean));
        bean.setProjects(addOtherProjects(bean));
        return bean;
      }
    })).build();
  }


  @POST
  @Consumes("application/json")
  @Produces(MediaType.APPLICATION_JSON)
  public Response create(@Context HttpServletRequest request,
      final HierarchyFieldConfigCreateBean bean) {
    if (!AdminUtils.checkAuthorized(request, userManager)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    // TODO: Catch sql / field exists exception
    CustomField field = jaeCustomFieldManager.getOrCreateHierarchyField(bean.getCustomFieldName(),
        HierarchyLinkField.CUSTOM_FIELD_TYPE, bean.getOutwardLink(), bean.getInwardLink());

    if (field == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }

    jaeCustomFieldManager.editHierarchyFieldConfigurationContext(
        field, bean);

    final IssueLinkType hierarchyLinkType = hierarchyLinkTypeManager.getHierarchyLinkTypeByName(
        field.getFieldName());

    if (hierarchyLinkType == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }

    return Response.ok(transactionTemplate.execute(new TransactionCallback() {
      public Object doInTransaction() {
        HierarchyFieldSettings hierarchyFieldSettings = hierarchyFieldSettingsService
            .addFieldSettings(field.getIdAsLong(),
                new CustomFieldBean(field.getId(), field.getIdAsLong(), field.getName()),
                bean.getIssueTypes(),
                bean.getProjects(),
                new IssueLinkBean(String.valueOf(hierarchyLinkType.getId()),
                    hierarchyLinkType.getId(),
                    hierarchyLinkType.getName()), bean.getInwardLink(),
                bean.getOutwardLink(),
                bean.getJqlStatement());
        return createJsonBean(field.getIdAsLong(), hierarchyFieldSettings);
      }
    })).build();

  }

  @DELETE
  @Path("{id}")
  public Response delete(@Context HttpServletRequest request, @PathParam("id") final Long id) {
    if (!AdminUtils.checkAuthorized(request, userManager)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    System.out.println("deleting id: " + id);

    try {
      jaeCustomFieldManager.deleteHierarchyField(
          hierarchyFieldSettingsService.getFieldSettings(id).getCustomField().getName());
    } catch (RemoveException exception) {
      exception.printStackTrace();
    }

    hierarchyFieldSettingsService.removeCustomField(id);

    return Response.ok(transactionTemplate.execute(new TransactionCallback() {
      public Object doInTransaction() {
        Map<String, String> map = new HashMap<>();
        map.put("response", "ok");
        return map;
      }
    })).build();
  }

  public interface HierarchyFieldConfigBean {

    List<IssueTypeBean> getIssueTypes();

    List<ProjectBean> getProjects();

    String getInwardLink();

    String getOutwardLink();

    String getJqlStatement();

    void setIssueTypes(List<IssueTypeBean> issueTypeBeans);

    void setProjects(List<ProjectBean> projectBeans);

    void setInwardLink(String inwardLink);

    void setOutwardLink(String outwardLink);

    void setJqlStatement(String jqlStatement);
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  @ToString
  public static final class HierarchyFieldConfigEditBean implements HierarchyFieldConfigBean {

    @JsonProperty
    private String id;
    @JsonProperty
    private CustomFieldBean customField;
    @JsonProperty
    private List<IssueTypeBean> issueTypes;
    @JsonProperty
    private List<ProjectBean> projects;
    @JsonProperty
    private IssueLinkBean issueLink;
    @JsonProperty
    private String inwardLink;
    @JsonProperty
    private String outwardLink;
    @JsonProperty
    private String jqlStatement;
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  @ToString
  public static final class HierarchyFieldConfigCreateBean implements HierarchyFieldConfigBean {

    @JsonProperty
    private String customFieldName;
    @JsonProperty
    private List<IssueTypeBean> issueTypes;
    @JsonProperty
    private List<ProjectBean> projects;
    @JsonProperty
    private String inwardLink;
    @JsonProperty
    private String outwardLink;
    @JsonProperty
    private String jqlStatement;
  }
}
