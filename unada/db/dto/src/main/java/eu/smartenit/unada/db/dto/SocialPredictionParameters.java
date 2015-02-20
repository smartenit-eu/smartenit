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
package eu.smartenit.unada.db.dto;

import java.io.Serializable;

/**
 * The SocialPredictionParameters class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class SocialPredictionParameters implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5597409924902974278L;

	public SocialPredictionParameters() {
		
	}
	
	private double lambda1;
	private double lambda2;
	private double lambda3;
	private double lambda4;
	private double lambda5;
	private double lambda6;

	public double getLambda1() {
		return lambda1;
	}
	public void setLambda1(double lambda1) {
		this.lambda1 = lambda1;
	}
	public double getLambda2() {
		return lambda2;
	}
	public void setLambda2(double lambda2) {
		this.lambda2 = lambda2;
	}
	public double getLambda3() {
		return lambda3;
	}
	public void setLambda3(double lambda3) {
		this.lambda3 = lambda3;
	}
	public double getLambda4() {
		return lambda4;
	}
	public void setLambda4(double lambda4) {
		this.lambda4 = lambda4;
	}
	public double getLambda5() {
		return lambda5;
	}
	public void setLambda5(double lambda5) {
		this.lambda5 = lambda5;
	}
	public double getLambda6() {
		return lambda6;
	}
	public void setLambda6(double lambda6) {
		this.lambda6 = lambda6;
	}
	@Override
	public String toString() {
		return "SocialPredictionParameters [lambda1=" + lambda1 + ", lambda2="
				+ lambda2 + ", lambda3=" + lambda3 + ", lambda4=" + lambda4
				+ ", lambda5=" + lambda5 + ", lambda6=" + lambda6 + "]";
	}
	
	
}
