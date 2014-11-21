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
package eu.smartenit.sbox.ntm.dtm.receiver;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Implements new compensation vector calculation after new reference vector is
 * received followed by sending both compensation and reference vectors to
 * remote SBoxes.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class CRVectorProcessingThread extends VectorProcessingThread {

	private static final Logger logger = LoggerFactory.getLogger(CRVectorProcessingThread.class);
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param xVector
	 *            link traffic vector to be used in compensation vector
	 *            calculation
	 * @param rVector
	 *            reference vector to be used in compensation vector calculation
	 * @param remoteSboxes
	 *            list of target {@link SBox} which should be updated with
	 *            calculated compensation vector
	 */
	public CRVectorProcessingThread(XVector xVector, LocalRVector rVector, List<SBox> remoteSboxes) {
		super(xVector, rVector, remoteSboxes);
	}
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param vectorPair
	 *            contains vectors that should be used in compensation vector
	 *            calculation
	 * @param remoteSboxes
	 *            list of target {@link SBox} which should be updated with
	 *            calculated compensation vector
	 */
	public CRVectorProcessingThread(XRVectorPair vectorPair, List<SBox> remoteSboxes) {
		super(vectorPair.getXVector(), vectorPair.getRVector(), remoteSboxes);
	}

	/**
	 * Method launched when thread is started. Calculates compensation vector
	 * with {@link CVectorConstructor} and sends both vectors to remote
	 * {@link SBox}es using instance of {@link DTMVectorsSender}.
	 */
	public void run() {
		logger.info("Running C vector calculation thread.");
		try {
			CVector cVector = new CVectorConstructor().construct(xVector, rVector);
			RVector rVector = createRVectorForRemoteSBox();

			if (cVector != null) {
				CVectorHistory.storeInHistory(cVector);
				for (SBox remoteSbox : remoteSboxes) {
					logger.info("Sending C and R vectors to remote SBox: {}", remoteSbox.getManagementAddress().getPrefix());
					sender.send(remoteSbox, cVector, rVector);
				}
			}
		} catch (IllegalArgumentException e) {
			logger.error("Compensation vector was not constructed properly: {}", e.getMessage());
		}
	}

	private RVector createRVectorForRemoteSBox() {
		RVector rVector = new RVector();
		rVector.setSourceAsNumber(this.rVector.getSourceAsNumber());
		
		for (LocalVectorValue vectorValue : this.rVector.getVectorValues())
			rVector.addVectorValueForTunnelEndPrefix(tunnelEndNetworkPrefix(vectorValue.getLinkID()), vectorValue.getValue());
		
		return rVector;
	}

	private NetworkAddressIPv4 tunnelEndNetworkPrefix(LinkID linkId) {
		Link link = DAOFactory.getLinkDAOInstance().findById((SimpleLinkID)linkId); 
		if (link == null) throw new IllegalArgumentException("Link not found in DB (LinkID: " + linkId.toString() + ")");
		return link.getTunnelEndPrefix();
	}

}
