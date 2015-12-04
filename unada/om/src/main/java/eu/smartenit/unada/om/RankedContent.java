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
package eu.smartenit.unada.om;

import eu.smartenit.unada.db.dto.Content;

public class RankedContent implements Comparable<RankedContent>{
	
	private int score = 0;
	private Content content = null;
	
	public RankedContent(){
		
	}
	
	public RankedContent(Content content){
		this.content = content;
	}
	
	public int compareTo(RankedContent o) {
		if(this.score < o.getScore())
			return -1;
		if(this.score > o.getScore())
			return 1;
		
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RankedContent){
			return content.getContentID() == ((RankedContent)obj).getContent().getContentID();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return content.hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("ContentID: %s, Score: %s", getContent().getContentID(), getScore());
	}
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public void incrementScore(int increment){
		this.score += increment;
	}
	public Content getContent() {
		return content;
	}
	public void setContent(Content content) {
		this.content = content;
	}
	
}
