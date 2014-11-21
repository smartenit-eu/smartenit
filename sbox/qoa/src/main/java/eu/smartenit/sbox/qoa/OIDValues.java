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
package eu.smartenit.sbox.qoa;

/**
 * Object identifiers (OIDs) for interface counters utilized by DTM.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @version 1.2
 * 
 */
public enum OIDValues {
	IF_NAME_OID ("1.3.6.1.2.1.31.1.1.1.1"),
	IF_HC_IN_OCTETS_OID ("1.3.6.1.2.1.31.1.1.1.6"),
	IF_HC_OUT_OCTETS_OID ("1.3.6.1.2.1.31.1.1.1.10"),
	/* Extended to support packet counters*/
	IF_HC_IN_UCAST_PKTS_OID ("1.3.6.1.2.1.31.1.1.1.7"),
	IF_HC_OUT_UCAST_PKTS_OID ("1.3.6.1.2.1.31.1.1.1.11"),
	IF_HC_IN_MULTICAST_PKTS_OID ("1.3.6.1.2.1.31.1.1.1.8"),
	IF_HC_OUT_MULTICAST_PKTS_OID ("1.3.6.1.2.1.31.1.1.1.12"),
	IF_HC_IN_BROADCAST_PKTS_OID ("1.3.6.1.2.1.31.1.1.1.9"),
	IF_HC_OUT_BROADCAST_PKTS_OID ("1.3.6.1.2.1.31.1.1.1.13");
	
	private String value;
	
	private OIDValues(String oidValue) {
		value = oidValue;
	}
	
	public String getValue() {
		return value;
	}
}
