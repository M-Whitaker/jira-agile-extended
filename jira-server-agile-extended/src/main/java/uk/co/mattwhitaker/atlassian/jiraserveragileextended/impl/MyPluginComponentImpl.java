package uk.co.mattwhitaker.atlassian.jiraserveragileextended.impl;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import javax.inject.Inject;
import javax.inject.Named;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.api.MyPluginComponent;

@ExportAsService({MyPluginComponent.class})
@Named("myPluginComponent")
public class MyPluginComponentImpl implements MyPluginComponent {

  @ComponentImport
  private final ApplicationProperties applicationProperties;

  @Inject
  public MyPluginComponentImpl(final ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
  }

  public String getName() {
    if (null != applicationProperties) {
      return "myComponent:" + applicationProperties.getDisplayName();
    }

    return "myComponent";
  }
}