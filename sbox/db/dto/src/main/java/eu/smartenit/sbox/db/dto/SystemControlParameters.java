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
package eu.smartenit.sbox.db.dto;

import java.io.Serializable;

/**
 * The SystemControlParameters class.
 *
 * @author George Petropoulos
 * @version 3.0
 * 
 */
public class SystemControlParameters implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5079270467982343745L;	
	
	
	/**
	 * The constructor.
	 */
	public SystemControlParameters() {

	}

	/**
	 * The constructor using fields.
	 * 
	 * @param chargingRule
	 * @param operationModeSDN
	 * @param compensationThreshold
	 */
	public SystemControlParameters(ChargingRule chargingRule,
			OperationModeSDN operationModeSDN, double compensationThreshold) {
		
		this.chargingRule = chargingRule;
		this.operationModeSDN = operationModeSDN;
		this.compensationThreshold = compensationThreshold;
	}


	private ChargingRule chargingRule;
	
	private OperationModeSDN operationModeSDN;
	
	private double compensationThreshold;

	public ChargingRule getChargingRule() {
		return chargingRule;
	}

	public void setChargingRule(ChargingRule chargingRule) {
		this.chargingRule = chargingRule;
	}

	public OperationModeSDN getOperationModeSDN() {
		return operationModeSDN;
	}

	public void setOperationModeSDN(OperationModeSDN operationModeSDN) {
		this.operationModeSDN = operationModeSDN;
	}

	public double getCompensationThreshold() {
		return compensationThreshold;
	}

	public void setCompensationThreshold(double compensationThreshold) {
		this.compensationThreshold = compensationThreshold;
	}

	@Override
	public String toString() {
		return "SystemControlParameters [chargingRule=" + chargingRule
				+ ", operationModeSDN=" + operationModeSDN
				+ ", compensationThreshold=" + compensationThreshold + "]";
	}

	
}
