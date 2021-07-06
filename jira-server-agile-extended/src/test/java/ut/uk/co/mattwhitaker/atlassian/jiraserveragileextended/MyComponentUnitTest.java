package ut.uk.co.mattwhitaker.atlassian.jiraserveragileextended;

import org.junit.Test;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.api.MyPluginComponent;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest {
    @Test
    public void testMyName() {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}