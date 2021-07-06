package uk.co.mattwhitaker.atlassian.jiraserveragileextended.service;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.customfield.HierarchyLinkField;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.issuelink.HierarchyIssueLinkType;

@ExportAsService({PluginInitializer.class})
@Named("PluginInitializer")
public class PluginInitializer implements InitializingBean, DisposableBean {

  private static final Logger log = LoggerFactory.getLogger(PluginInitializer.class);

  public final static String PLUGIN_KEY = "uk.co.mattwhitaker.atlassian.jiraserveragileextended.jira-server-agile-extended";
  private final EventPublisher eventPublisher;
  private final JAECustomFieldManager jaeCustomFieldManager;
  private final IssueLinkTypeManager issueLinkTypeManager;


  @Autowired
  public PluginInitializer(@ComponentImport EventPublisher eventPublisher,
      @ComponentImport IssueLinkTypeManager issueLinkTypeManager,
      @Autowired JAECustomFieldManager jaeCustomFieldManager) {
    this.eventPublisher = eventPublisher;
    this.issueLinkTypeManager = issueLinkTypeManager;
    this.jaeCustomFieldManager = jaeCustomFieldManager;
  }


  @Override
  public void afterPropertiesSet() throws Exception {
    System.out.println("------- CREATING " + PLUGIN_KEY + " -------");
    eventPublisher.register(this);
  }

  @Override
  public void destroy() throws Exception {
    // TODO: Check if disabling and not uninstalling
    System.out.println("------- DELETING " + PLUGIN_KEY + " -------");
//        pluginFields.deleteField("Parent Link");
    eventPublisher.unregister(this);
  }

  @EventListener
  public void onPluginStarted(PluginEnabledEvent event) {
    String startUpPluginKey = event.getPlugin().getKey();
    if (PLUGIN_KEY.equals(startUpPluginKey)) {
      log.info("Starting " + PLUGIN_KEY + "...");
      try {
        CustomField customField = jaeCustomFieldManager.getOrCreateHierarchyField("Parent Link",
            HierarchyLinkField.CUSTOM_FIELD_TYPE);
        System.out.println(customField.getName());
      } catch (IllegalArgumentException error) {
        log.error(error.getMessage());
      }
      issueLinkTypeManager.getIssueLinkTypes(false).forEach((n) -> {
        log.debug(n.getName() + " " + "is system: " + n.isSystemLinkType());
      });
    }
  }
}
