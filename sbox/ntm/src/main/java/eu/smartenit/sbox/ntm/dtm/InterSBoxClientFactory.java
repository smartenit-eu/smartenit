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
package eu.smartenit.sbox.ntm.dtm;

import eu.smartenit.sbox.interfaces.intersbox.client.InterSBoxClient;

/**
 * Static factory class used by {@link DTMVectorsSender} to obtain instances of
 * {@link InterSBoxClient} to be used for communication with remote SBoxes.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class InterSBoxClientFactory {

	private static InterSBoxClient client;
	private static boolean uniqueClientCreationMode = false;
	
	/**
	 * Method returns an instance of the {@link InterSBoxClient} according to
	 * currently enabled client creation mode. By default the so called unique
	 * client creation mode is disabled.
	 * 
	 * @return instance of {@link InterSBoxClient}
	 */
	public static InterSBoxClient getInstance() {
		return (uniqueClientCreationMode) ? new InterSBoxClient() : getOrCreateClient();
	}

	/**
	 * Method enables the so called unique client creation mode - new client
	 * instance is created and returned on each request.
	 */
	public static void enableUniqueClientCreationMode() {
		uniqueClientCreationMode = true;
	}
	
	/**
	 * Method disables the so called unique client creation mode - on each
	 * request the same instance of the client will be returned.
	 */
	public static void disableUniqueClientCreationMode() {
		uniqueClientCreationMode = false;
	}
	
	/**
	 * Method sets the locally stored {@link InterSBoxClient} instance to the
	 * one provided. Method is mainly meant to be used in unit testing.
	 * 
	 * @param defaultClient
	 *            specific instance of the {@link InterSBoxClient}
	 */
	public static void setClientInstance(InterSBoxClient defaultClient) {
		client = defaultClient;
	}
	
	private static InterSBoxClient getOrCreateClient() {
		if (client == null) client = new InterSBoxClient();
		return client;
	}
	
}
