/**
 * Copyright (C) 2015 The SmartenIT consortium (http://www.smartenit.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.smartenit.unada.sm;

import eu.smartenit.unada.db.dto.Owner;
import org.junit.Test;

/**
 * Test class for MonitorRunner
 */
public class MonitorRunnerTest {

    /**
     * Used for test invocations of MonitorRunner
     *
     */
    @Test
    public void test() {
        Owner owner = new Owner();
        owner.setOauthToken("CAACEdEose0cBAMCKHcqSEOcS5O40e4co26HaL6YyEtM7PZAUlPSSDbIca80wF2w3G9B43ZCAx8vojAQKfeyk8ZAapSjv5ala0FPAawudbeNPpFMgyGCDmR0bEhNUuj9gbRjKHQqvVt28DUegx6uAVtMy5PYGhoFSYHstHBtt8Hby7wp2cIczDeZAiuoKe0L6qbGJnFRHqzO87pyZBEhPDpbAZBWjrtCyQZD");
        owner.setFacebookID("1492826064306740");

        MonitorRunner runner = new MonitorRunner(owner);
        //runner.run();
    }


}
