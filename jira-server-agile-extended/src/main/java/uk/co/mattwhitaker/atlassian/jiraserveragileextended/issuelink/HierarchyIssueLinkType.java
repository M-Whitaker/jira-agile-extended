package uk.co.mattwhitaker.atlassian.jiraserveragileextended.issuelink;

import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.PropertyDao;

@Service
public class HierarchyIssueLinkType {

  private static final Logger log = LoggerFactory.getLogger(HierarchyIssueLinkType.class);

  public static final String KEY_DEFAULT_LINKTYPE_ID_TEMPLATE = "JiraAgileExtended.HierarchyLink.%s.linktype.id";
  public static final String LINK_STYLE = "jira_jae_hierarchy_link";
  private final PropertyDao propertyDao;
  private final IssueLinkTypeManager issueLinkTypeManager;

  @Autowired
  public HierarchyIssueLinkType(@Autowired PropertyDao propertyDao,
      @ComponentImport IssueLinkTypeManager issueLinkTypeManager) {
    this.propertyDao = propertyDao;
    this.issueLinkTypeManager = issueLinkTypeManager;
  }

  public List<IssueLinkType> getHierarchyLinkTypes() {
    return new ArrayList<>(issueLinkTypeManager.getIssueLinkTypesByStyle(LINK_STYLE));
  }

  public IssueLinkType getHierarchyLinkTypeByName(String linkTypeName) {
    return getHierarchyLinkType(linkTypeName).orElse(null);
  }

  /**
   * Gets the issue link type created for a given name else creates it.
   * @param linkTypeName The name of the issue link type.
   * @return The issue link type.
   */
  public IssueLinkType getOrCreateHierarchyLinkType(String linkTypeName, String outwardLink, String inwardLink) {
    return getHierarchyLinkType(linkTypeName)
        .orElseGet(() -> createHierarchyLinkType(linkTypeName, outwardLink, inwardLink));
  }

  private Optional<IssueLinkType> getHierarchyLinkType(String linkTypeName) {
    Long linkTypeId = propertyDao.getLongProperty(String.format(KEY_DEFAULT_LINKTYPE_ID_TEMPLATE, linkTypeName));
    return linkTypeId != null ? Optional
        .ofNullable(issueLinkTypeManager.getIssueLinkType(linkTypeId, false)) : Optional.empty();
  }

  private IssueLinkType createHierarchyLinkType(String linkTypeName, String outwardLink, String inwardLink) {
    issueLinkTypeManager
        .createIssueLinkType(linkTypeName, inwardLink, outwardLink, LINK_STYLE);
    Collection<IssueLinkType> linkTypes = issueLinkTypeManager.getIssueLinkTypesByStyle(LINK_STYLE);
    List<IssueLinkType> hierarchyLinkType = linkTypes.stream()
        .filter(linkType -> linkType.getName().equals(linkTypeName))
        .collect(Collectors.toList());
    if (hierarchyLinkType.isEmpty())
      throw new RuntimeException("Could not create issue link type Hierarchy for " + linkTypeName);
    else if (hierarchyLinkType.size() > 1)
      log.warn("Found more than one issue link type with the same name: using the first");
    Long linkTypeId = hierarchyLinkType.get(0).getId();
    propertyDao.setLongProperty(String.format(KEY_DEFAULT_LINKTYPE_ID_TEMPLATE, linkTypeName), linkTypeId);
    log.info("Hierarchy Link Type now exists with ID {}", linkTypeId);
    return hierarchyLinkType.get(0);
  }
}
