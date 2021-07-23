package ut.uk.co.mattwhitaker.atlassian.jiraserveragileextended.jql;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.atlassian.jira.JiraDataTypes;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.jql.operand.QueryLiteral;
import com.atlassian.jira.plugin.jql.function.JqlFunctionModuleDescriptor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.query.operand.FunctionOperand;
import com.atlassian.query.operand.SingleValueOperand;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.customfield.HierarchyLinkField;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.jql.HierarchyChildIssues;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.HierarchyLinkManager;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.JAECustomFieldManager;

@Component
public class HierarchyChildIssuesTest {
    private static final String FUNC_NAME = "agileChildIssuesOf";
    protected HierarchyChildIssues function;

    @ComponentImport
    IssueManager issueManager;

    @ComponentImport
    IssueFactory issueFactory;

    @ComponentImport
    JiraAuthenticationContext jiraAuthenticationContext;

    @Autowired
    HierarchyLinkManager hierarchyLinkManager;

    @Autowired
    JAECustomFieldManager jaeCustomFieldManager;


    @Before
    public void setup() {
        JqlFunctionModuleDescriptor descriptor = mock(JqlFunctionModuleDescriptor.class);
        I18nHelper i18nHelper = mock(I18nHelper.class);

        when(i18nHelper.getText(anyString(), anyString(), anyString(), anyString())).thenReturn(String.format("Function %s expected '1' arguments but received '0'.", FUNC_NAME));
        when(descriptor.getI18nBean()).thenReturn(i18nHelper);

        function = new HierarchyChildIssues(issueManager, hierarchyLinkManager, jiraAuthenticationContext);
        function.init(descriptor);
    }

    @Test
    public void testDataType() throws Exception {
        assertEquals(JiraDataTypes.ISSUE, function.getDataType());
    }

    @Test
    public void testValidateEmptyArguments() throws Exception {
        final FunctionOperand functionOperand = new FunctionOperand(FUNC_NAME);
        final MessageSet messageSet = function.validate(null, functionOperand, null);
        assertTrue(messageSet.hasAnyErrors());
        assertEquals(String.format("Function %s expected '1' arguments but received '0'.", FUNC_NAME), messageSet.getErrorMessages().iterator().next());
    }

    @Test
    public void testValidateArguments() throws Exception {
        final FunctionOperand functionOperand = new FunctionOperand(FUNC_NAME, "TEST-1");
        final MessageSet messageSet = function.validate(null, functionOperand, null);
        assertFalse(messageSet.hasAnyErrors());
    }

    @Test
    @Ignore
    public void testGetValues() {
        //TODO: Mock this data
        List<QueryLiteral> actualList = new ArrayList<>();
        MutableIssue parentIssue = issueFactory.getIssue();

        parentIssue.setProjectId(10001L);
        parentIssue.setIssueTypeId("1");
        parentIssue.setSummary("Parent Issue");

        MutableIssue childIssue = issueFactory.getIssue();

        parentIssue.setProjectId(10001L);
        parentIssue.setIssueTypeId("1");
        parentIssue.setSummary("Child Issue");
        parentIssue.setCustomFieldValue(jaeCustomFieldManager.getOrCreateHierarchyField("Delivery Link", HierarchyLinkField.CUSTOM_FIELD_TYPE, "delivers", "is delivered by"), parentIssue.getKey());
        try {
            Issue issue = issueManager.createIssueObject(jiraAuthenticationContext.getLoggedInUser(), parentIssue);
            actualList = function.getValues(null, new FunctionOperand(FUNC_NAME, issue.getKey()), null);
        } catch (CreateException e) {
            e.printStackTrace();
        }
        assertEquals(Collections.singletonList(createLiteral(childIssue.getKey())), actualList);
    }

    @Test
    public void testGetMinimumNumberOfExpectedArguments() {
        assertEquals(1, function.getMinimumNumberOfExpectedArguments());
    }

    private QueryLiteral createLiteral(String value) {
        return new QueryLiteral(new SingleValueOperand(value), value);
    }

}
