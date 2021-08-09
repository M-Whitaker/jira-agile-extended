package it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.customfield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.atlassian.jira.bc.ServiceResult;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.ProjectCreationData;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.template.ProjectTemplateManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.List;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class HierarchyLinkFieldTest {

  static final String PROJECT_KEY = "FOO2";


  // Service under test


  // Services required for setup

  @ComponentImport
  @Inject
  public JiraAuthenticationContext jiraAuthenticationContext;

  @ComponentImport
  @Inject
  public UserManager userManager;

  @ComponentImport
  @Inject
  public ProjectTemplateManager projectTemplateManager;

  @ComponentImport
  @Inject
  public ProjectService projectService;

  @ComponentImport
  @Inject
  public IssueService issueService;


  // Test fixtures

  private ApplicationUser user;
  private Project project;


  // Fixture setup/teardown

  @Before
  public void setUp() {

    // Use the utilities defined in this class to setup a test project...
    asAdmin();
    createTestProject();
  }

  @After
  public void tearDown() throws Exception {

    // ...and tear it down
    deleteTestProject();
  }

  public void asAdmin() {
    user = userManager.getUserByName("admin");
    jiraAuthenticationContext.setLoggedInUser(user);
  }

  public void createTestProject() {
    final String projectTemplateKey = projectTemplateManager.getDefaultTemplate().getKey().getKey();

    final ProjectCreationData projectCreationData = new ProjectCreationData.Builder()
        .withKey(PROJECT_KEY)
        .withName(PROJECT_KEY)
        .withLead(user)
        .withProjectTemplateKey(projectTemplateKey)
        .build();

    final ProjectService.CreateProjectValidationResult validationResult = projectService.validateCreateProject(user, projectCreationData);

    assertValidServiceResult("Failed to create project", validationResult);

    project = projectService.createProject(validationResult);
  }

  public void deleteTestProject() {
    final ProjectService.DeleteProjectValidationResult validationResult = projectService.validateDeleteProject(user, project.getKey());

    assertValidServiceResult("Failed to delete project", validationResult);

    final ProjectService.DeleteProjectResult result = projectService.deleteProject(user, validationResult);

    assertValidServiceResult("Failed to delete project", result);
  }

  public void assertValidServiceResult(String msg, ServiceResult result) {
    if (!result.isValid()) {
      fail(msg + ":\n" + result.getErrorCollection().getErrorMessages() + "\n" + result.getErrorCollection().getErrors());

    }
  }

  @Test
  public void testProjectsExist() {
    List<Project> projects = projectService.getAllProjects(user).getReturnedValue();
    assertEquals("FOO2", projects.get(0).getKey());
  }
}
