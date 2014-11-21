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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * The LocalVectorValue class.
 *
 * @author George Petropoulos
 * @version 1.2
 * 
 */
public final class LocalVectorValue implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public LocalVectorValue() {
		
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param value
	 * @param linkID
	 */
	public LocalVectorValue(long value, LinkID linkID) {
		super();
		this.value = value;
		this.linkID = linkID;
	}

	private long value;
	
	@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
	private LinkID linkID;

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public LinkID getLinkID() {
		return linkID;
	}

	public void setLinkID(LinkID linkID) {
		this.linkID = linkID;
	}

	@Override
	public String toString() {
		return "LocalVectorValue [value=" + value + ", linkID=" + linkID + "]";
	}

	
	
	

}
