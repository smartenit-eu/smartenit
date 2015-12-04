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
package eu.smartenit.sbox.db.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

import eu.smartenit.sbox.db.dto.util.ClassNameAndIntSequenceGenerator;

/**
 * The CloudDC class.
 * 
 * @author George Petropoulos
 * @version 1.0
 * 
 */
@JsonIdentityInfo(generator=ClassNameAndIntSequenceGenerator.class, scope=CloudDC.class, property="@id")
public final class CloudDC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public CloudDC() {
		this.as = new AS();
		this.daRouter = new DARouter();
		this.sdnController = new SDNController();
		this.dcNetworks = new ArrayList<NetworkAddressIPv4>();
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param cloudDcName
	 * @param as
	 * @param daRouter
	 * @param sdnController
	 * @param dcNetworks
	 */
	public CloudDC(String cloudDcName, AS as, DARouter daRouter,
			SDNController sdnController, List<NetworkAddressIPv4> dcNetworks) {

		this.cloudDcName = cloudDcName;
		this.as = as;
		this.daRouter = daRouter;
		this.sdnController = sdnController;
		this.dcNetworks = dcNetworks;
	}

	private String cloudDcName;

	private AS as;

	private DARouter daRouter;

	private SDNController sdnController;

	private List<NetworkAddressIPv4> dcNetworks;

	public String getCloudDcName() {
		return cloudDcName;
	}

	public void setCloudDcName(String cloudDcName) {
		this.cloudDcName = cloudDcName;
	}

	public AS getAs() {
		return as;
	}

	public void setAs(AS as) {
		this.as = as;
	}

	public DARouter getDaRouter() {
		return daRouter;
	}

	public void setDaRouter(DARouter daRouter) {
		this.daRouter = daRouter;
	}

	public SDNController getSdnController() {
		return sdnController;
	}

	public void setSdnController(SDNController sdnController) {
		this.sdnController = sdnController;
	}

	public List<NetworkAddressIPv4> getDcNetworks() {
		return dcNetworks;
	}

	public void setDcNetworks(List<NetworkAddressIPv4> dcNetworks) {
		this.dcNetworks = dcNetworks;
	}

	@Override
	public String toString() {
		return "CloudDC [cloudDcName=" + cloudDcName + ", daRouter=" + daRouter
				+ ", sdnController=" + sdnController + ", dcNetworks="
				+ dcNetworks + "]";
	}

}
