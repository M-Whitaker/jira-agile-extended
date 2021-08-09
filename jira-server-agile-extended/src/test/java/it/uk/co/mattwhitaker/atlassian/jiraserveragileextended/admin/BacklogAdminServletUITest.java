package it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.admin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.pageobjects.BacklogAdminPage;
import it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.pageobjects.LoginPage;
import it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.ui.UITestBase;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BacklogAdminServletUITest extends UITestBase {

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
    public void thenBacklogAdminPageShouldBeAccessible(@InitialPage BacklogAdminPage backlogAdminPage) throws Exception {
        assertThat("Expected to see a title", backlogAdminPage.getPageTitle(), is(equalTo("Backlog osition")));
    }
}
