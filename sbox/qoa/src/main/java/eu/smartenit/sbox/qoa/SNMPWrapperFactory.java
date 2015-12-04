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
package eu.smartenit.sbox.qoa;

/**
 * Static factory class used by the {@link CounterCollectorThread} and
 * {@linkSNMPOIDCollector} to obtain instances of {@link SNMPWrapper} to be used
 * for communication with SNM4J library.
 * 
 * Supports two object creation modes.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @author <a href="mailto:llopat@man.poznan.pl">Lukasz Lopatowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @version 1.00
 * 
 */
public class SNMPWrapperFactory {
	private static SNMPWrapper snmpWrapper;
	private static boolean uniqueSnmpWrappperInstance = true;
	
	/**
	 * Method returns an instance of the {@link SNMPWrapper} according to
	 * currently enabled creation mode. By default the so called unique
	 * creation mode is enabled.
	 * 
	 * @return instance of {@link SNMPWrapper}
	 */
	public static SNMPWrapper getInstance() {
		return (uniqueSnmpWrappperInstance) ? new SNMPWrapper() : getOrCreateSNMPWrapper();
	}
	
	/**
	 * Method sets the locally stored {@link SNMPWrapper} instance to the one
	 * provided. Method is mainly meant to be used in unit testing.
	 * 
	 * @param providedSnmpWrapper
	 *            specific instance of the {@link SNMPWrapper}
	 */
	public static void setSNMPWrapperInstance(SNMPWrapper providedSnmpWrapper) {
		snmpWrapper = providedSnmpWrapper;
	}
	
	/**
	 * Method enables the so called unique creation mode - new
	 * instance of the {@link SNMPWrapper} is created and 
	 * returned on each request.
	 */
	public static void enableUniqueSnmpWrappperCreationMode() {
		uniqueSnmpWrappperInstance = true;
	}
	
	/**
	 * Method disables the so called unique creation mode - on each
	 * request the same instance of the {@link SNMPWrapper} will be returned.
	 */
	public static void disableUniqueSnmpWrappperCreationMode() {
		uniqueSnmpWrappperInstance = false;
	}

	protected static SNMPWrapper getOrCreateSNMPWrapper() {
		if(snmpWrapper == null) {
			snmpWrapper = new SNMPWrapper();
		}
		return snmpWrapper;
	}
}
