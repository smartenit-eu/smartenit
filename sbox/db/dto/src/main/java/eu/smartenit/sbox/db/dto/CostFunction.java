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
import java.util.ArrayList;
import java.util.List;

/**
 * The CostFunction class.
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public final class CostFunction implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public CostFunction() {
		this.segments = new ArrayList<Segment>();
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param type
	 * @param subtype
	 * @param inputUnit
	 * @param outputUnit
	 * @param segments
	 */
	public CostFunction(String type, String subtype, String inputUnit,
			String outputUnit, List<Segment> segments) {
		
		this.type = type;
		this.subtype = subtype;
		this.inputUnit = inputUnit;
		this.outputUnit = outputUnit;
		this.segments = segments;
	}

	private String type;
	
	private String subtype;
	
	private String inputUnit;
	
	private String outputUnit;
	
	private List<Segment> segments;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getInputUnit() {
		return inputUnit;
	}

	public void setInputUnit(String inputUnit) {
		this.inputUnit = inputUnit;
	}

	public String getOutputUnit() {
		return outputUnit;
	}

	public void setOutputUnit(String outputUnit) {
		this.outputUnit = outputUnit;
	}

	public List<Segment> getSegments() {
		return segments;
	}

	public void setSegments(List<Segment> segments) {
		this.segments = segments;
	}

	@Override
	public String toString() {
		return "CostFunction [type=" + type + ", subtype=" + subtype
				+ ", inputUnit=" + inputUnit + ", outputUnit=" + outputUnit
				+ ", segments=" + segments + "]";
	}
	
	

}
