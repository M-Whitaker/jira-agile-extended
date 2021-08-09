package it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.ui;

import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class Bootstrap {

    // This could be used as place to initialise the global environment, eg. create applinks, projects, import data etc.

    @Test
    public void testSuiteFailure() throws Exception {
        // Set this to false to see the FailFast for the whole suite in action
        assertTrue(true);
    }
}
