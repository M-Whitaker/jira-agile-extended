package uk.co.mattwhitaker.atlassian.jiraserveragileextended.issuelink;

import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.PropertyDao;

@Service
public class HierarchyIssueLinkType {

  private static final Logger log = LoggerFactory.getLogger(HierarchyIssueLinkType.class);

  private static final String KEY_DEFAULT_LINKTYPE_ID = "JiraAgileExtended.HierarchyLink.Default.linktype.id";
  public static final String LINK_STYLE = "jira_jae_parent_link";
  private final PropertyDao propertyDao;
  private final IssueLinkTypeManager issueLinkTypeManager;

  @Autowired
  public HierarchyIssueLinkType(@Autowired PropertyDao propertyDao,
      @ComponentImport IssueLinkTypeManager issueLinkTypeManager) {
    this.propertyDao = propertyDao;
    this.issueLinkTypeManager = issueLinkTypeManager;
  }

  /**
   * Gets the issue link type created for a given name else creates it.
   * @param linkTypeName The name of the issue link type.
   * @return The issue link type.
   */
  public IssueLinkType getOrCreateHierarchyLinkType(String linkTypeName) {
    return getHierarchyLinkType(linkTypeName)
        .orElseGet(() -> createHierarchyLinkType(linkTypeName));
  }

  private Optional<IssueLinkType> getHierarchyLinkType(String linkTypeName) {
    Long linkTypeId = propertyDao.getLongProperty(KEY_DEFAULT_LINKTYPE_ID);
    return linkTypeId != null ? Optional
        .ofNullable(issueLinkTypeManager.getIssueLinkType(linkTypeId, false)) : Optional.empty();
  }

  private IssueLinkType createHierarchyLinkType(String linkTypeName) {
    //TODO: Remove constants for link type.
    issueLinkTypeManager
        .createIssueLinkType("Parent Link", "is Parent of", "has Parent", LINK_STYLE);
    Collection<IssueLinkType> linkTypes = issueLinkTypeManager.getIssueLinkTypesByStyle(LINK_STYLE);
    if (linkTypes.size() > 1) {
      log.warn("More than one issue link type with hierarchy link style -- using first",
          new Object[0]);
    } else if (linkTypes.isEmpty()) {
      log.error("Could not create issue link type Hierarchy", new Object[0]);
      throw new RuntimeException("Could not create issue link type Hierarchy");
    }

    IssueLinkType epicLinkType = linkTypes.iterator().next();
    propertyDao.setLongProperty(KEY_DEFAULT_LINKTYPE_ID, epicLinkType.getId());
    log.info("Hierarchy Link Type now exists with ID " + Arrays
        .toString(new Object[]{epicLinkType.getId()}));
    return epicLinkType;
  }
}
