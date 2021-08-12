package ut.uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import com.atlassian.activeobjects.test.TestActiveObjects;
import java.util.ArrayList;
import net.java.ao.EntityManager;
import net.java.ao.test.converters.NameConverters;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.jdbc.Hsql;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao.CustomField;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao.HierarchyFieldSettings;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao.HierarchyFieldSettingsService;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao.HierarchyFieldSettingsServiceImpl;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao.IssueLink;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao.IssueType;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao.Project;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.CustomFieldBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.IssueLinkBean;
import ut.uk.co.mattwhitaker.atlassian.jiraserveragileextended.ao.HierarchyFieldSettingsServiceTest.HierarchyFieldSettingsServiceTestDatabaseUpdater;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(HierarchyFieldSettingsServiceTestDatabaseUpdater.class)
@Jdbc(Hsql.class)
@NameConverters
public class HierarchyFieldSettingsServiceTest {

  private EntityManager entityManager;

  private HierarchyFieldSettingsService hierarchyFieldSettingsService;

  @Before
  public void setUp() throws Exception {
    hierarchyFieldSettingsService = new HierarchyFieldSettingsServiceImpl(
        new TestActiveObjects(entityManager));
  }

  @Test
  @NonTransactional
  public void testGetAllFieldSettings() throws Exception {

    hierarchyFieldSettingsService.addFieldSettings(1000L,
        new CustomFieldBean("1000", 1000L, "HierarchyField"), new ArrayList<>(), new ArrayList<>(),
        new IssueLinkBean("1000", 1000L, "HierarchyField"), "hierarchy for", "is hierarchy of",
        "project = TEST");
    assertThat(hierarchyFieldSettingsService.getAllFieldSettings(), hasSize(1));
  }

  public static final class HierarchyFieldSettingsServiceTestDatabaseUpdater implements
      DatabaseUpdater {

    @Override
    public void update(EntityManager entityManager) throws Exception {
      entityManager.migrate(CustomField.class, HierarchyFieldSettings.class, IssueLink.class,
          IssueType.class, Project.class);
    }
  }
}
