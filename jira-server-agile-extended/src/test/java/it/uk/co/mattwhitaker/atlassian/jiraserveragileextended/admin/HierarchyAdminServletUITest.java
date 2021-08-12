package it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.admin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.pageobjects.HierarchyAdminPage;
import it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.pageobjects.LoginPage;
import it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.ui.UITestBase;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.customfield.HierarchyLinkField;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.JAECustomFieldManager;

@RunWith(Arquillian.class)
public class HierarchyAdminServletUITest extends UITestBase {

  @ComponentImport
  @Inject
  private JAECustomFieldManager jaeCustomFieldManager;

  @Test
  @RunAsClient
  @InSequence(10)
  public void givenWeHaveLoggedIn(@InitialPage LoginPage loginPage) throws Exception {
    loginPage.assertOnLoginPage();
    loginPage.login("admin", "admin");
  }

  @Test
  @RunAsClient
  @InSequence(20)
  public void thenHierarchyAdminPageShouldBeAccessible(@InitialPage HierarchyAdminPage hierarchyAdminPage) throws Exception {
    assertThat("Expected to see a title", hierarchyAdminPage.getPageTitle(),
        is(equalTo("Hierarchy Field Configuration")));
  }

  @Test
  @InSequence(30)
  public void givenWeHaveCreatedHierarchyField() throws Exception {
    jaeCustomFieldManager.getOrCreateHierarchyField("Hierarchy Field", HierarchyLinkField.CUSTOM_FIELD_TYPE, "outward", "inward");
  }
}
