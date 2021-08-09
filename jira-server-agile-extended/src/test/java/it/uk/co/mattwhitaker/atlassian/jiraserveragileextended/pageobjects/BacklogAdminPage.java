package it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.pageobjects;

import java.net.URI;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Location("plugins/servlet/jiraagileextended/admin/backlog")
public class BacklogAdminPage {
  @Drone
  private WebDriver browser;

  @FindBy(tagName = "h1")
  private WebElement title;

  public String getPageTitle() {
    return title.getText();
  }

  public boolean isDebugEnabled() {
    return URI.create(browser.getCurrentUrl()).getQuery().contains("debug=true");
  }
}
