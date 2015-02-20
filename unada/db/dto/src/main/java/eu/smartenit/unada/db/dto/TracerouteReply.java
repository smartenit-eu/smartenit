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
import java.util.List;

/**
 * The TraceRouteReply class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class TracerouteReply implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2032718554659601544L;

	public TracerouteReply() {
		
	}
	
	private String type;
    private String targetAddress;
    private String sourceAddress;
    private List<Integer> ASVector;
    
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTargetAddress() {
		return targetAddress;
	}
	public void setTargetAddress(String targetAddress) {
		this.targetAddress = targetAddress;
	}
	public String getSourceAddress() {
		return sourceAddress;
	}
	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}
	public List<Integer> getASVector() {
		return ASVector;
	}
	public void setASVector(List<Integer> aSVector) {
		ASVector = aSVector;
	}
	
	@Override
	public String toString() {
		return "TracerouteReply [type=" + type + ", targetAddress="
				+ targetAddress + ", sourceAddress=" + sourceAddress
				+ ", ASVector=" + ASVector + "]";
	}

}
