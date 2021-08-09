package it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.ui;

import it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.pageobjects.BacklogAdminPage;
import it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.pageobjects.LoginPage;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class RedirectToLoginTest extends UITestBase {

    @Test
    @InSequence(10)
    public void given_we_are_not_logged_in() {
        // Do nothing, as a new session is started for each test class
    }

    @Test
    @InSequence(20)
    public void when_we_attempt_to_view_hit_count(@InitialPage BacklogAdminPage backlogAdminPage) {
        // Do nothing, as the @InitialPage will take us to the desired page
    }

    @Test
    @InSequence(30)
    public void then_we_are_redirected_to_login_page(@Page LoginPage loginPage) {
        loginPage.assertOnLoginPage();
    }

}
