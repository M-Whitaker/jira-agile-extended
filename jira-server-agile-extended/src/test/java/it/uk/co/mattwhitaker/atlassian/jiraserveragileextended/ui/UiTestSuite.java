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

package it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.ui;

import it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.admin.BacklogAdminServletUITest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// This test suite isn't strictly necessary, as the test classes in this plugin don't depend on ordering, it's here
// simply as a demonstration of how to create a test suite.

@RunWith(Suite.class)
@Suite.SuiteClasses({
        Bootstrap.class,
        RedirectToLoginTest.class,
        BacklogAdminServletUITest.class
})
public class UiTestSuite {
}
