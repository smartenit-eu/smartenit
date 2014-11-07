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

import eu.smartenit.sbox.interfaces.sboxsdn.SboxSdnClient;

/**
 * Static factory class used by the {@link SDNConfigPusher} to obtain instances
 * of {@link SboxSdnClient} to be used for communication with remote SDN
 * Controllers.
 * 
 * Supports two object creation modes.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class SDNClientFactory {

	private static SboxSdnClient client;
	private static boolean uniqueClientCreationMode = false;

	/**
	 * Method returns an instance of the {@link SboxSdnClient} according to
	 * currently enabled client creation mode. By default the so called unique
	 * client creation mode is disabled.
	 * 
	 * @return instance of {@link SboxSdnClient}
	 */
	public static SboxSdnClient getInstance() {
		return (uniqueClientCreationMode) ? new SboxSdnClient() : getOrCreateClient();
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
	 * Method sets the locally stored {@link SboxSdnClient} instance to the one
	 * provided. Method is mainly meant to be used in unit testing.
	 * 
	 * @param defaultClient
	 *            specific instance of the {@link SboxSdnClient}
	 */
	public static void setClientInstance(SboxSdnClient defaultClient) {
		client = defaultClient;
	}

	private static SboxSdnClient getOrCreateClient() {
		if (client == null)
			client = new SboxSdnClient();
		return client;
	}

}
