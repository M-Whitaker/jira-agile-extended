package uk.co.mattwhitaker.atlassian.jiraserveragileextended.service;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.customfield.HierarchyLinkField;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.listener.BacklogPositionListener;

@ExportAsService({PluginInitializer.class})
@Named("PluginInitializer")
public class PluginInitializer implements InitializingBean, DisposableBean {

  private static final Logger log = LoggerFactory.getLogger(PluginInitializer.class);

  public final static String PLUGIN_KEY = "uk.co.mattwhitaker.atlassian.jiraserveragileextended.jira-server-agile-extended";
  private final EventPublisher eventPublisher;
  private final JAECustomFieldManager jaeCustomFieldManager;
  private final IssueLinkTypeManager issueLinkTypeManager;
  private final BacklogPositionListener backlogPositionListener;


  @Autowired
  public PluginInitializer(@ComponentImport EventPublisher eventPublisher,
      @ComponentImport IssueLinkTypeManager issueLinkTypeManager,
      @Autowired JAECustomFieldManager jaeCustomFieldManager, @Autowired BacklogPositionListener backlogPositionListener) {
    this.eventPublisher = eventPublisher;
    this.issueLinkTypeManager = issueLinkTypeManager;
    this.jaeCustomFieldManager = jaeCustomFieldManager;
    this.backlogPositionListener = backlogPositionListener;
  }


  /**
   * Called when the plugin is enabled/installed.
   */
  @Override
  public void afterPropertiesSet() {
    System.out.println("------- CREATING " + PLUGIN_KEY + " -------");
    eventPublisher.register(this);
  }

  /**
   * Called when the plugin is disabled/uninstalled.
   */
  @Override
  public void destroy() {
    // TODO: Check if disabling and not uninstalling
    System.out.println("------- DELETING " + PLUGIN_KEY + " -------");
//        pluginFields.deleteField("Parent Link");
    eventPublisher.unregister(this);
  }

  /**
   * Called when a plugin is started by the UPM.
   * @param event information about the plugin that was started.
   */
  @EventListener
  public void onPluginStarted(PluginEnabledEvent event) {
    String startUpPluginKey = event.getPlugin().getKey();
    if (PLUGIN_KEY.equals(startUpPluginKey)) {
      log.info("Starting " + PLUGIN_KEY + "...");
      try {
        CustomField customField = jaeCustomFieldManager.getOrCreateHierarchyField("Parent Link",
            HierarchyLinkField.CUSTOM_FIELD_TYPE);
      } catch (IllegalArgumentException error) {
        log.error(error.getMessage());
      }
      issueLinkTypeManager.getIssueLinkTypes(false).forEach((n) -> {
        log.debug(n.getName() + " " + "is system: " + n.isSystemLinkType());
      });
    }
  }

  /**
   * Called when an issue event is fired in jira.
   * @param issueEvent information about the issue event.
   */
  @EventListener
  public void onIssueEvent(IssueEvent issueEvent) {
    Long eventTypeId = issueEvent.getEventTypeId();
    Issue issue = issueEvent.getIssue();

    if (eventTypeId.equals(EventType.ISSUE_CREATED_ID)) {
      log.info("Issue {} has been created at {}.", issue.getKey(), issue.getCreated());
    } else if (eventTypeId.equals(EventType.ISSUE_UPDATED_ID)) {
      log.info("Issue {} has been updated.", issue.getKey());
      backlogPositionListener.calculateRanks();
    } else if (eventTypeId.equals(EventType.ISSUE_CLOSED_ID)) {
      log.info("Issue {} has been closed at {}.", issue.getKey(), issue.getUpdated());
    }
  }
}
