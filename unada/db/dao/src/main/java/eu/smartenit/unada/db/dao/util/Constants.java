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
package eu.smartenit.unada.db.dao.util;

public final class Constants {

	public static String DBI_URL = "jdbc:h2:~/unada.h2"; 
	
	public static String TEST_DBI_URL = "jdbc:sqlite:"; 
	
	static {
        try {
        	Class.forName("org.h2.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println("h2 not available. ");
        }
	}
}
