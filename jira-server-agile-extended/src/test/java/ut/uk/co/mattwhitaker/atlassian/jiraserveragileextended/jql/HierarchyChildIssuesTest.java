package ut.uk.co.mattwhitaker.atlassian.jiraserveragileextended.jql;


//public class HierarchyChildIssuesTest {
//    private static final String FUNC_NAME = "agileChildIssuesOf";
//    protected HierarchyChildIssues function;
//
//    @ComponentImport
//    IssueManager issueManager;
//
//    @ComponentImport
//    IssueLinkManager issueLinkManager;
//
//    @ComponentImport
//    JiraAuthenticationContext jiraAuthenticationContext;
//
//    @Autowired
//    HierarchyLinkManager hierarchyLinkManager;
//
//
//    @Before
//    public void setup() {
//        JqlFunctionModuleDescriptor descriptor = mock(JqlFunctionModuleDescriptor.class);
//        I18nHelper i18nHelper = mock(I18nHelper.class);
//
//        when(i18nHelper.getText(anyString(), anyString(), anyString(), anyString())).thenReturn("Function 'funcName' expected '1' arguments but received '0'.");
//        when(descriptor.getI18nBean()).thenReturn(i18nHelper);
//
//        function = new HierarchyChildIssues(issueManager, hierarchyLinkManager, jiraAuthenticationContext);
//        function.init(descriptor);
//    }
//
//    @Test
//    public void testDataType() throws Exception {
//        assertEquals(JiraDataTypes.ISSUE, function.getDataType());
//    }
//
//    @Test
//    public void testValidateEmptyArguments() throws Exception {
//        final FunctionOperand functionOperand = new FunctionOperand(FUNC_NAME);
//        final MessageSet messageSet = function.validate(null, functionOperand, null);
//        assertTrue(messageSet.hasAnyErrors());
//        assertEquals("Function 'funcName' expected '1' arguments but received '0'.", messageSet.getErrorMessages().iterator().next());
//    }
//
//    @Test
//    public void testValidateArguments() throws Exception {
//        final FunctionOperand functionOperand = new FunctionOperand(FUNC_NAME, "TEST-1");
//        final MessageSet messageSet = function.validate(null, functionOperand, null);
//        assertFalse(messageSet.hasAnyErrors());
//    }
//
////    @Test
////    public void testGetValues() {
////        List<QueryLiteral> actualList;
////        issueManager.createIssueObject("")
////
////        actualList = function.getValues(null, new FunctionOperand(FUNC_NAME, "one"), null);
////        assertEquals(Collections.singletonList(createLiteral("one")), actualList);
////    }
//
//    @Test
//    public void testGetMinimumNumberOfExpectedArguments() {
//        assertEquals(1, function.getMinimumNumberOfExpectedArguments());
//    }
//
//    private QueryLiteral createLiteral(String value) {
//        return new QueryLiteral(new SingleValueOperand(value), value);
//    }
//
//}
