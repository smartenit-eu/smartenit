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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.LocalVector;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.VectorValue;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Constructs compensation vector. 
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class CVectorConstructor {
	
	private static final Logger logger = LoggerFactory.getLogger(CVectorConstructor.class);

	/**
	 * Constructs new compensation vector based on provided arguments: link
	 * traffic vector and reference vector. Uses {@link CVectorValuesCalculator}
	 * to calculate vector values.
	 * 
	 * @param xVector
	 *            link traffic vector
	 * @param rVector
	 *            reference vector
	 * @return new compensation vector
	 * @throws IllegalArgumentException
	 *             if either of the provided vectors is incorrect
	 */
	public CVector construct(XVector xVector, LocalRVector rVector) throws IllegalArgumentException {
		validateInput(xVector, rVector);
	
		List<LocalVectorValue> cVectorValues = new CVectorValuesCalculator(xVector, rVector).calculate();
		logger.info("VECTOR-VALUES-C:(" 
				+ ((SimpleLinkID)cVectorValues.get(0).getLinkID()).getLocalLinkID() + ":" 
				+ cVectorValues.get(0).getValue() + ")|(" 
				+ ((SimpleLinkID)cVectorValues.get(1).getLinkID()).getLocalLinkID() + ":"
				+ cVectorValues.get(1).getValue() + ")"); 
		
		CVector cVector = new CVector();
		cVector.setSourceAsNumber(xVector.getSourceAsNumber());
		cVector.setVectorValues(replaceVectorValueIDs(cVectorValues));
		
		logger.debug("CVector constucted.");
		return cVector;
	}

	private List<VectorValue> replaceVectorValueIDs(List<LocalVectorValue> cLocalVectorValues) {
		List<VectorValue> cVectorValues = new ArrayList<>();
		for(LocalVectorValue localValue : cLocalVectorValues)
			cVectorValues.add(new VectorValue(localValue.getValue(), tunnelEndNetworkPrefix(localValue.getLinkID())));
		return cVectorValues;
	}

	private NetworkAddressIPv4 tunnelEndNetworkPrefix(LinkID linkId) {
		Link link = DAOFactory.getLinkDAOInstance().findById((SimpleLinkID)linkId); 
		if (link == null) throw new IllegalArgumentException("Link not found in DB (LinkID: " + linkId.toString() + ")");
		return link.getTunnelEndPrefix();
	}

	private void validateInput(XVector xVector, LocalRVector rVector) throws IllegalArgumentException {
		logger.debug("Validating input arguments ...");
		if (rVector == null || xVector == null)
			throw new IllegalArgumentException("Either one or both Vectors are null.");
		
		if (xVector.getVectorValues().size() != rVector.getVectorValues().size())
			throw new IllegalArgumentException("Vectors of different sizes.");
		
		if (xVector.getVectorValues().size() < 2)
			throw new IllegalArgumentException("Vector size less that 2.");
		
		if (!getLinkIDs(xVector).containsAll(getLinkIDs(rVector)))
			throw new IllegalArgumentException("Different link identifiers in both Vectors.");
		
		logger.debug("... validated successfuly.");
	}
	
	private List<LinkID> getLinkIDs(LocalVector vector) {
		List<LinkID> ids = new ArrayList<LinkID>();
		for(LocalVectorValue value : vector.getVectorValues())
			ids.add(value.getLinkID());
		
		return ids;
	}
	
}
