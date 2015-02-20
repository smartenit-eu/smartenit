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
package eu.smartenit.unada.om;

import eu.smartenit.unada.db.dto.UnadaInfo;

public class NeighborStatistics {
	
	private UnadaInfo UnadaInfo = null;
	private int totalContent = 0;
	private int commonContent = 0;
	
	
	@Override
	public int hashCode() {
		return getUnadaInfo().getUnadaID().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof NeighborStatistics){
			return UnadaInfo.getUnadaID().equals(((NeighborStatistics)obj).getUnadaInfo().getUnadaID());
		}
		return super.equals(obj);
	}

	public UnadaInfo getUnadaInfo() {
		return UnadaInfo;
	}

	public void setUnadaInfo(UnadaInfo unadaInfo) {
		UnadaInfo = unadaInfo;
	}

	public int getTotalContent() {
		return totalContent;
	}

	public void setTotalContent(int totalContent) {
		this.totalContent = totalContent;
	}

	public int getCommonContent() {
		return commonContent;
	}

	public void setCommonContent(int commonContent) {
		this.commonContent = commonContent;
	}
	

}
