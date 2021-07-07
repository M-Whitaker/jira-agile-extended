package uk.co.mattwhitaker.atlassian.jiraserveragileextended.admin;


import com.atlassian.greenhopper.api.issuetype.ManagedIssueTypesService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.customfield.HierarchyLinkField;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.CustomFieldBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.JAECustomFieldManager;

@Path("hierarchyfield")
public class HierarchyFieldAdminResource {

  private final UserManager userManager;
  private final TransactionTemplate transactionTemplate;
  private final JAECustomFieldManager jaeCustomFieldManager;
  private final ManagedIssueTypesService managedIssueTypesService;

  @Autowired
  public HierarchyFieldAdminResource(@ComponentImport UserManager userManager,
      @ComponentImport TransactionTemplate transactionTemplate,
      @Autowired JAECustomFieldManager jaeCustomFieldManager, @ComponentImport ManagedIssueTypesService managedIssueTypesService) {
    this.userManager = userManager;
    this.transactionTemplate = transactionTemplate;
    this.jaeCustomFieldManager = jaeCustomFieldManager;
    this.managedIssueTypesService = managedIssueTypesService;
  }

  @GET
  @Path("all")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAll(@Context HttpServletRequest request, @QueryParam("html") String htmlOutput) {
    if (!checkAuthorized(request)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    return Response.ok(transactionTemplate.execute(new TransactionCallback() {
      public Object doInTransaction() {
        List<HierarchyFieldConfigBean> configList = new ArrayList<>();
        List<String> issueTypes = new ArrayList<>();
        List<String> projects = new ArrayList<>();
        if (htmlOutput != null && htmlOutput.equals("true")) {
          IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
          List<IssueType> issueTypeList = new ArrayList<>();
          issueTypeList.add(managedIssueTypesService.getEpicIssueType().getReturnedValue());
          issueTypeList.add(managedIssueTypesService.getStoryIssueType().getReturnedValue());
          for (IssueType issueType : issueTypeManager.getIssueTypes()) {
            if (!issueTypeList.contains(issueType)) {
              issueTypes.add(String.format("<option>%s</option>", issueType.getName()));
            } else {
              issueTypes.add(String.format("<option selected>%s</option>", issueType.getName()));
            }
          }
          ProjectManager projectManager = ComponentAccessor.getProjectManager();
          List<Project> projectList = new ArrayList<>();
          projectList.add(projectManager.getProjectObjByKey("TEST"));
          for (Project project : projectManager.getProjects()) {
            if (!projectList.contains(project)) {
              projects.add(String.format("<option>%s</option>", project.getKey()));
            } else {
              projects.add(String.format("<option selected>%s</option>", project.getKey()));
            }
          }
        } else {
          issueTypes.add("Epic");
          projects.add("TEST");
        }
        CustomField customField = jaeCustomFieldManager.getOrCreateHierarchyField("Parent Link",
            HierarchyLinkField.CUSTOM_FIELD_TYPE);
        CustomFieldBean customFieldBean = new CustomFieldBean(customField.getId(), customField.getIdAsLong(),
            customField.getName());
        HierarchyFieldConfigBean config = new HierarchyFieldConfigBean(String.valueOf(customField.getIdAsLong()), customFieldBean, issueTypes, projects, "Parent", "is parent", "parents", "project = TEST");
        configList.add(config);
        CustomField customField1 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(10105L);
        if (customField1 != null) {
          CustomFieldBean customFieldBean1 = new CustomFieldBean(customField1.getId(), customField1.getIdAsLong(),
              customField1.getName());
          HierarchyFieldConfigBean config1 = new HierarchyFieldConfigBean(String.valueOf(customField1.getIdAsLong()), customFieldBean1, issueTypes, projects, "Sprint", "sprint", "in sprint", "project = TEST2");
          configList.add(config1);
        }
        return configList;
      }
    })).build();
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response get(@Context HttpServletRequest request, @PathParam("id") final String id) {
    if (!checkAuthorized(request)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    return Response.ok(transactionTemplate.execute(new TransactionCallback() {
      public Object doInTransaction() {
        List<String> issueTypes = new ArrayList<>();
        issueTypes.add("Epic");
        List<String> projects = new ArrayList<>();
        projects.add("TEST");
        CustomField customField = jaeCustomFieldManager.getOrCreateHierarchyField("Parent Link",
            HierarchyLinkField.CUSTOM_FIELD_TYPE);
        CustomFieldBean customFieldBean = new CustomFieldBean(customField.getId(), customField.getIdAsLong(),
            customField.getName());
        HierarchyFieldConfigBean config = new HierarchyFieldConfigBean(String.valueOf(customField.getIdAsLong()), customFieldBean, issueTypes, projects, "Parent", "is parent", "parents", "project = TEST");
        return config;
      }
    })).build();
  }

  @PUT
  @Path ("{id}")
  public Response update(@Context HttpServletRequest request, @PathParam ("id") final String id, final HierarchyFieldConfigBean bean) {
    if (!checkAuthorized(request)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    System.out.println("updating id: " + id);
    System.out.println(bean.getJqlStatement());

    return Response.ok(transactionTemplate.execute(new TransactionCallback() {
      public Object doInTransaction() {
        Map<String, String> map = new HashMap<>();
        map.put("response", "ok");
        return map;
      }
    })).build();
  }


  @POST
  public Response create(@Context HttpServletRequest request, final HierarchyFieldConfigBean bean) {
    if (!checkAuthorized(request)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    System.out.println(bean.getJqlStatement());

    return Response.ok(transactionTemplate.execute(new TransactionCallback() {
      public Object doInTransaction() {
        Map<String, String> map = new HashMap<>();
        map.put("response", "ok");
        return map;
      }
    })).build();
  }

  @DELETE
  @Path ("{id}")
  public Response delete(@Context HttpServletRequest request, @PathParam ("id") final String id) {
    if (!checkAuthorized(request)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    System.out.println("deleting id: " + id);

    return Response.ok(transactionTemplate.execute(new TransactionCallback() {
      public Object doInTransaction() {
        Map<String, String> map = new HashMap<>();
        map.put("response", "ok");
        return map;
      }
    })).build();
  }

  private Boolean checkAuthorized(HttpServletRequest request) {
    UserKey userKey = userManager.getRemoteUserKey(request);
    if (userKey == null || !userManager.isSystemAdmin(userKey)) {
      return false;
    } else {
      return true;
    }
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  public static final class HierarchyFieldConfigBean {

    @JsonProperty
    private String id;
    @JsonProperty
    private CustomFieldBean customField;
    @JsonProperty
    private List<String> issueTypes;
    @JsonProperty
    private List<String> projects;
    @JsonProperty
    private String linkName;
    @JsonProperty
    private String inwardLink;
    @JsonProperty
    private String outwardLink;
    @JsonProperty
    private String jqlStatement;
  }
}
