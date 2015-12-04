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

/**
 * The Segment class.
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public final class Segment implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public Segment() {
		
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param leftBorder
	 * @param rightBorder
	 * @param a
	 * @param b
	 */
	public Segment(long leftBorder, long rightBorder, float a, float b) {
		
		this.leftBorder = leftBorder;
		this.rightBorder = rightBorder;
		this.a = a;
		this.b = b;
	}

	private long leftBorder;
	
	private long rightBorder;
	
	private float a;
	
	private float b;

	public long getLeftBorder() {
		return leftBorder;
	}

	public void setLeftBorder(long leftBorder) {
		this.leftBorder = leftBorder;
	}

	public long getRightBorder() {
		return rightBorder;
	}

	public void setRightBorder(long rightBorder) {
		this.rightBorder = rightBorder;
	}

	public float getA() {
		return a;
	}

	public void setA(float a) {
		this.a = a;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	@Override
	public String toString() {
		return "Segment [leftBorder=" + leftBorder + ", rightBorder="
				+ rightBorder + ", a=" + a + ", b=" + b + "]";
	}
	
	

}
