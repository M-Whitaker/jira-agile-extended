package it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.pageobjects;

import java.net.URI;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class JAEAdminPage {
  @Drone
  protected WebDriver browser;

  @FindBy(css = "#main > h1")
  protected WebElement title;

  public String getPageTitle() {
    return title.getText();
  }

  public boolean isDebugEnabled() {
    return URI.create(browser.getCurrentUrl()).getQuery().contains("debug=true");
  }
}
