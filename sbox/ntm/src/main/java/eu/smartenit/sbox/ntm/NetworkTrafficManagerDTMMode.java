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
package eu.smartenit.sbox.ntm;

/**
 * Enumeration class that represents three DTM running modes: 
 * <li>{@link #TRAFFIC_RECEIVER}</li>, 
 * <li>{@link #TRAFFIC_SENDER}</li> 
 * <li>{@link #TRAFFIC_SENDER_AND_RECEIVER}</li>.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public enum NetworkTrafficManagerDTMMode {
	
	/**
	 * DTM instance is run on SBox that manages AS(s) that only sends traffic to
	 * remote ASs. In this case some of the modules do not need to be
	 * initialized.
	 */
	TRAFFIC_SENDER,
	
	/**
	 * DTM instance is run on SBox that manages AS(s) that only receives traffic
	 * from remote ASs. In this case some of the modules do not need to be
	 * initialized.
	 */
	TRAFFIC_RECEIVER,
	
	/**
	 * General case enabled by default.
	 */
	TRAFFIC_SENDER_AND_RECEIVER;
}
