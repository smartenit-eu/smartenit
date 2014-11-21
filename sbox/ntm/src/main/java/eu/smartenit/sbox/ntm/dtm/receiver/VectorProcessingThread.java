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

import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.db.dto.XVector;

/**
 * Each compensation vector calculation and subsequent delivery of updated
 * vectors to remote SBoxes is performed in separate thread.
 * 
 * Base class to be extended.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public abstract class VectorProcessingThread implements Runnable {

	protected XVector xVector;
	protected LocalRVector rVector;
	protected List<SBox> remoteSboxes;
	protected DTMVectorsSender sender;
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param xVector
	 *            link traffic vector to be used in compensation vector
	 *            calculation
	 * @param rVector
	 *            reference vector to be used in compensation vector calculation
	 * @param remoteSboxes
	 *            list of {@link SBox}es that should be updated with the new
	 *            calculated compensation vector
	 */
	public VectorProcessingThread(XVector xVector, LocalRVector rVector, List<SBox> remoteSboxes) {
		this.xVector = xVector;
		this.rVector = rVector;
		this.remoteSboxes = remoteSboxes;
		this.sender = new DTMVectorsSender();
	}

}
