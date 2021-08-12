package it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.ui;

import it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.pageobjects.LoginPage;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

/**
 * Base test class for all of our UI tests.
 * <p>
 * This will hold common injected components, deployment methods, and utility fns.
 */
public abstract class UITestBase {

    @Drone
    protected WebDriver browser;


    // @Deployment methods run before the test class is instantiated and are used to create Archive for deployment to
    // an Arquillian container - in our case, we are deploying plugins to an Atlassian application.
    //
    // This particular method will wrap this test class and the additional packages into a plugin.
    @Deployment
    public static Archive<?> deployTests() {
        return ShrinkWrap.create(JavaArchive.class, "tests.jar")
            .addClass(UITestBase.class)
            .addPackage(LoginPage.class.getPackage())
            .addClasses(WebDriver.class, SearchContext.class);
    }
}
