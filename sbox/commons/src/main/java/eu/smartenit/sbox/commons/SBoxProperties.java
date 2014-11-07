/**
 * Copyright (C) 2014 The SmartenIT consortium (http://www.smartenit.eu)
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
package eu.smartenit.sbox.commons;

/**
 * The global SBox properties. 
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public class SBoxProperties {
	
	public static String PROPERTIES_FILE_NAME = "./sbox.properties";
		
	public static String DB_FILE = "smartenit.db";
	
	public static int CORE_POOL_SIZE = 10;

	public static long CONNECTION_TIMEOUT = 1500;
	
	public static int CONNECTION_RETRIES = 2; 
	
	public static long MAX_FETCHING_TIME = 2000;
	
	public static int INTER_SBOX_PORT = 9999;
}
