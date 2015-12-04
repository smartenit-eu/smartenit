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
package eu.smartenit.sbox.eca;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;
import eu.smartenit.sbox.ntm.dtm.receiver.DTMTrafficManager;

/**
 * Main class of the Economic Analyzer. Implements external interface method
 * used by the QoS Analyzer to update traffic information. Uses
 * {@link EconomicAnalyzerInternal} to handle traffic samples and trigger
 * actions in scope of a single Autonomous System.
 * 
 * @author D. D&ouml;nni, K. Bhargav, T. Bocek
 */
public class EconomicAnalyzer {
	
	/**
	 * The DTM traffic manager instance.
	 */
	final private DTMTrafficManager dtmTrafficManager;
	
	/**
	 * Map that maps the Key (consisting of two link ids) to the corresponding
	 * EconomicAnalyzerInternal instance.
	 */
	final private Map<Key, EconomicAnalyzerInternal> map = new HashMap<Key, EconomicAnalyzerInternal>();
	
	/**
	 * Constructor for the EconomicAnalyzer.
	 * 
	 * @param dtmTrafficManager
	 *            instance of DTM traffic manager
	 */
	public EconomicAnalyzer(DTMTrafficManager dtmTrafficManager) {
		this.dtmTrafficManager=dtmTrafficManager;
	}
	
	/**
	 * Economic Analyzer external interface method used to update information
	 * about link traffic vector and tunnel traffic vectors.
	 * 
	 * @param xVector
	 *            new link traffic vector
	 * @param zVectors
	 *            new list of tunnel traffic vectors
	 */
	public void updateXZVectors(XVector xVector, List<ZVector> zVectors) {
		makeSanityCheck(xVector);
		makeSanityCheck(zVectors);
		
		SimpleLinkID link1 = (SimpleLinkID) xVector.getVectorValues().get(0).getLinkID();
		SimpleLinkID link2 = (SimpleLinkID) xVector.getVectorValues().get(1).getLinkID();
		
		Key key = new Key(link1, link2);
		EconomicAnalyzerInternal eca = map.get(key);
		if(eca == null) {
			eca = new EconomicAnalyzerInternal(dtmTrafficManager, link1, link2);
			map.put(key, eca);
		}
		eca.updateXZVectors(xVector, zVectors);
	}
	
	/**
	 * Makes sure that all link ids in the {@link ZVector} {@link List} are of
	 * type {@link SimpleLinkID}. Otherwise, an {@link IllegalArgumentException}
	 * is thrown.
	 * 
	 * @param z_in_list
	 *            The {@link ZVector} {@link List} whose link ids are to be
	 *            checked for {@link SimpleLinkID} type.
	 */
	private void makeSanityCheck(List<ZVector> z_in_list) {
		for(int i = 0; i < z_in_list.size(); i++) {
			ZVector zvector = z_in_list.get(i);
			for(int j = 0; j < zvector.getVectorValues().size(); j++) {
				if(!(zvector.getVectorValues().get(j).getLinkID() instanceof SimpleLinkID)) {
					throw new IllegalArgumentException("Z_in[" + i + ", " + j + "] link id is of type: " + zvector.getVectorValues().get(j).getLinkID().getClass().getCanonicalName());
				}
			}
		}
	}

	/**
	 * Makes sure that all link ids in the {@link XVector} are of type
	 * {@link SimpleLinkID}. Otherwise, an {@link IllegalArgumentException} is
	 * thrown.
	 * 
	 * @param X_in
	 *            The {@link XVector} whose link ids are to be checked for
	 *            {@link SimpleLinkID} type.
	 */
	private void makeSanityCheck(XVector X_in) {
		for(int i = 0; i < X_in.getVectorValues().size(); i++) {
			if(!(X_in.getVectorValues().get(i).getLinkID() instanceof SimpleLinkID)) {
				throw new IllegalArgumentException("X_in[" + i + "] link id is of type: " + X_in.getVectorValues().get(i).getLinkID().getClass().getCanonicalName());
			}	
		}
	}

	/**
	 * Class used to map two {@link SimpleLinkID}s to the suitable
	 * {@link EconomicAnalyzerInternal} instance.
	 * 
	 * @author D. Dönni, K. Bhargav, T. Bocek
	 * 
	 */
	private static class Key {

		/**
		 * The {@link SimpleLinkID} of link 1
		 */
		final private SimpleLinkID link1;
		
		/**
		 * The {@link SimpleLinkID} of link 2
		 */
		final private SimpleLinkID link2;		
		
		/**
		 * Constructor for the {@link Key} instance.
		 * 
		 * @param link1 The {@link SimpleLinkID} of link 1
		 * @param link2 The {@link SimpleLinkID} of link 2
		 */
		public Key(SimpleLinkID link1, SimpleLinkID link2) {
			this.link1=link1;
			this.link2=link2;
		}
		
		@Override
		public int hashCode() {
			return link1.hashCode() ^ link2.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			if(this == o) {
				return true;
			} else if (!(o instanceof Key)) {
				return false;
			}
			Key k = (Key) o;
			return k.link1.equals(link1) && k.link2.equals(link2);
		}
	}
}
