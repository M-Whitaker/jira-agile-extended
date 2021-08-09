/*-
 * #%L
 * Reference Plugin
 * %%
 * Copyright (C) 2015 - 2016 Adaptavist.com Ltd
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.pageobjects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.jboss.arquillian.graphene.Graphene.guardAjax;
import static org.jboss.arquillian.graphene.Graphene.guardHttp;

import com.adaptavist.arquillian.atlassian.remote.container.AtlassianApplication;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Location;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Location("")
public class LoginPage {

    @ArquillianResource
    private AtlassianApplication app;

    @Drone
    private WebDriver browser;

    @FindBy(css = "input[name=os_username]")
    private WebElement username;

    @FindBy(css = "input[name=os_password]")
    private WebElement password;

    @FindBy(css = "input[name=login][type=submit]")
    private WebElement login;

    public void login(String username, String password) {
        this.username.sendKeys(username);
        this.password.sendKeys(password);

        switch (app) {
            case JIRA:
                if (browser.getCurrentUrl().contains("Dashboard.jspa")) {
                    // JIRA posts an XHR request to login when on the Dashboard, and then redirects the browser
                    guardAjax(guardHttp(login)).click();
                    break;
                }
                // Otherwise fall thru to default login handling

            default:
                guardHttp(login).click();
        }
    }

    public void assertOnLoginPage() {
        assertThat("Expected to see a username input", username.isDisplayed(), is(true));
        assertThat("Expected to see a password input", password.isDisplayed(), is(true));
        assertThat("Expected to see a login button", login.isDisplayed(), is(true));
    }

    public void assertOnDashboard() {
        assertThat("Expected to be redirected to dashboard page", browser.getCurrentUrl(), anyOf(containsString("index.action"), containsString("Dashboard.jspa")));
    }
}
