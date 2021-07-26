package uk.co.mattwhitaker.atlassian.jiraserveragileextended.service;

import com.atlassian.jira.bc.ServiceOutcome;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.managedconfiguration.ConfigurationItemAccessLevel;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItem;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemBuilder;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.customfields.CustomFieldSearcher;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.CustomFieldUtils;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigManager;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.ofbiz.core.entity.GenericEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.admin.HierarchyFieldAdminResource.HierarchyFieldConfigBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.admin.HierarchyFieldAdminResource.HierarchyFieldConfigEditBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.customfield.HierarchyLinkField;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.IssueTypeBean;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.model.ProjectBean;

@Service
public class JAECustomFieldManager {

  private static final Logger log = LoggerFactory.getLogger(JAECustomFieldManager.class);

  private static final String KEY_DEFAULT_CUSTOMFIELD_ID_TEMPLATE = "JiraAgileExtended.CustomField.Default.%s.id";
  public final IssueManager issueManager;

  private static final String CUSTOMFIELD_DESCRIPTION_TEMPLATE = "%s field for Jira Agile Extended use only.";
  private final CustomFieldManager customFieldManager;
  private final FieldConfigManager fieldConfigManager;
  private final FieldConfigSchemeManager fieldConfigSchemeManager;
  private final ManagedConfigurationItemService managedConfigurationItemService;
  private final PropertyDao propertyDao;
  private final HierarchyLinkTypeManager hierarchyLinkTypeManager;

  @Autowired
  public JAECustomFieldManager(@ComponentImport CustomFieldManager customFieldManager,
      @ComponentImport IssueManager issueManager,
      @ComponentImport FieldConfigManager fieldConfigManager,
      @ComponentImport FieldConfigSchemeManager fieldConfigSchemeManager,
      @ComponentImport ManagedConfigurationItemService managedConfigurationItemService,
      @Autowired PropertyDao propertyDao,
      @Autowired HierarchyLinkTypeManager hierarchyLinkTypeManager) {
    this.customFieldManager = customFieldManager;
    this.issueManager = issueManager;
    this.fieldConfigManager = fieldConfigManager;
    this.fieldConfigSchemeManager = fieldConfigSchemeManager;
    this.managedConfigurationItemService = managedConfigurationItemService;
    this.propertyDao = propertyDao;
    this.hierarchyLinkTypeManager = hierarchyLinkTypeManager;
  }

  public List<CustomField> getHierarchyFields(Issue issue) {
    List<CustomField> customFields = customFieldManager.getCustomFieldObjects(issue);
    return customFields.stream().filter(customField -> customField.getCustomFieldType().getKey().equals(HierarchyLinkField.CUSTOM_FIELD_TYPE)).collect(
        Collectors.toList());
  }

  /**
   * Gets or creates a managed custom field of a given name and type.
   * @param name field name.
   * @param type field type.
   * @return the custom field requested.
   */
  public CustomField getOrCreateHierarchyField(String name, String type, String outwardLink, String inwardLink) {
    return getHierarchyField(name).orElseGet(() -> {
      try {
        return createHierarchyField(name, type, outwardLink, inwardLink);
      } catch (GenericEntityException e) {
        log.error("Could not create custom field");
        e.printStackTrace();
        return null;
      }
    });
  }

  /**
   * Helper function to get an issue object from custom field.
   * @param issue the issue.
   * @param customField the custom field with an issue key/id in it.
   * @return the issue object from the string value in the custom field.
   */
  public Issue getIssueFromField(Issue issue, CustomField customField) {
    if (customField != null) {
      String parentLinkFieldValue = (String) customField.getValue(issue);
      if (parentLinkFieldValue != null) {
        return issueManager.getIssueObject(parentLinkFieldValue);
      }
    }
    return null;
  }

  private Optional<CustomField> getHierarchyField(String name) {
    Long customFieldId = propertyDao.getLongProperty(
        String.format(KEY_DEFAULT_CUSTOMFIELD_ID_TEMPLATE, name.replaceAll("\\s", "")));
    return customFieldId != null ? Optional
        .ofNullable(customFieldManager.getCustomFieldObject(customFieldId)) : Optional.empty();
  }

  private CustomField createHierarchyField(String name, String type, String outwardLink, String inwardLink)
      throws IllegalArgumentException, GenericEntityException {
    Collection<CustomField> fields = customFieldManager.getCustomFieldObjectsByName(name);
    // Check if field already exists with the same name
    if (fields.isEmpty()) {
      log.info("Creating field: " + name);
      CustomField field = generateCustomField(name, type);
      // TODO: Check for field nullptr
      propertyDao.setLongProperty(
          String.format(KEY_DEFAULT_CUSTOMFIELD_ID_TEMPLATE, name.replaceAll("\\s", "")),
          field.getIdAsLong());
      // TODO: Create link type for field
      IssueLinkType hierarchyLink = hierarchyLinkTypeManager.getOrCreateHierarchyLinkType(name,
          outwardLink, inwardLink);
      log.info("Locking field: " + field.getFieldName());
      lock(field);
      return field;
    } else {
      throw new IllegalArgumentException(
          "The field name ( " + name + " ) already exists in jira: " + fields.stream().findFirst()
              .get().getHiddenFieldId());
    }
  }

  public void deleteHierarchyField(String name) throws RemoveException {
    Collection<CustomField> customFields = customFieldManager.getCustomFieldObjectsByName(name);

    for (CustomField customField : customFields) {
      customFieldManager.removeCustomField(customField);
      hierarchyLinkTypeManager.deleteLinkTypeByName(customField.getFieldName(), null);
    }

  }

  public void editHierarchyField(CustomField customfield,
      HierarchyFieldConfigEditBean configBean) {
    //TODO: Edit custom field name & link name
    CustomFieldSearcher customFieldSearcher = customFieldManager.getCustomFieldSearcher(
        "com.atlassian.jira.plugin.system.customfieldtypes:exacttextsearcher");
    customFieldManager.updateCustomField(customfield.getIdAsLong(),
        customfield.getFieldName(),
        String.format(CUSTOMFIELD_DESCRIPTION_TEMPLATE, customfield.getFieldName()),
        customFieldSearcher);
    hierarchyLinkTypeManager.editLinkType(
        hierarchyLinkTypeManager.getHierarchyLinkTypeByName(customfield.getFieldName()),
        configBean);
  }

  public void editHierarchyFieldConfigurationContext(CustomField customfield,
      HierarchyFieldConfigBean configBean) {
    List<FieldConfigScheme> configSchemes = customfield.getConfigurationSchemes();
    if (configSchemes.size() != 1) {
      throw new RuntimeException("There should be only one config!!!");
    }
    FieldConfigScheme scheme = configSchemes.get(0);
    List<Long> projects = new ArrayList<>();
    for (ProjectBean project : configBean.getProjects()) {
      projects.add(project.getId());
    }
    List<JiraContextNode> context = CustomFieldUtils.buildJiraIssueContexts(false,
        projects.toArray(new Long[0]),
        ComponentAccessor.getProjectManager());

    FieldConfigScheme.Builder builder = new FieldConfigScheme.Builder(scheme);
    FieldConfig config = fieldConfigManager.getFieldConfig(scheme.getId());
    Map<String, FieldConfig> configs = new HashMap<>();
    for (IssueTypeBean issueType : configBean.getIssueTypes()) {
      configs.put(String.valueOf(issueType.getId()), config);
    }
    builder.setConfigs(configs);
    scheme = builder.toFieldConfigScheme();
    fieldConfigSchemeManager.updateFieldConfigScheme(scheme, context, customfield);
  }

  private CustomField generateCustomField(String name) throws GenericEntityException {
    return generateCustomField(name, "com.atlassian.jira.plugin.system.customfieldtypes:textfield",
        "com.atlassian.jira.plugin.system.customfieldtypes:exacttextsearcher");
  }

  private CustomField generateCustomField(String name, String type) throws GenericEntityException {
    return generateCustomField(name, type,
        "com.atlassian.jira.plugin.system.customfieldtypes:exacttextsearcher");
  }

  private CustomField generateCustomField(String name, String type, String searcher)
      throws GenericEntityException {
    List<IssueType> issueTypes = (List<IssueType>) ComponentAccessor.getIssueTypeSchemeManager()
        .getDefaultIssueTypeScheme().getAssociatedIssueTypes();
    List<JiraContextNode> contexts = ComponentAccessor.getIssueTypeSchemeManager()
        .getDefaultIssueTypeScheme().getContexts();

    CustomFieldType customFieldType = customFieldManager.getCustomFieldType(type);
    CustomFieldSearcher customFieldSearcher = customFieldManager.getCustomFieldSearcher(searcher);
    return customFieldManager.createCustomField(
        name, String.format(CUSTOMFIELD_DESCRIPTION_TEMPLATE, name), customFieldType,
        customFieldSearcher,
        contexts, issueTypes);
  }

  private void lock(CustomField field) {
    if (field != null) {
      ManagedConfigurationItem managedField = ManagedConfigurationItemBuilder
          .builder(managedConfigurationItemService.getManagedCustomField(field))
          .setManaged(true)
          .setConfigurationItemAccessLevel(ConfigurationItemAccessLevel.LOCKED)
          .build();

      ServiceOutcome<ManagedConfigurationItem> outcome = managedConfigurationItemService
          .updateManagedConfigurationItem(managedField);
      if (outcome.hasWarnings() || outcome.getErrorCollection().hasAnyErrors()) {
        log.info("----- Locking field outcome -----");
        log.warn(outcome.getWarningCollection().toString());
        log.error(outcome.getErrorCollection().toString());
      }
    } else {
      log.error("Cannot find field to lock. (nullptr exception on custom field)");
    }
  }
}
