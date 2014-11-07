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
package eu.smartenit.sbox.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import eu.smartenit.sbox.db.dao.gen.*;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.db.dto.SDNController;

import org.skife.jdbi.v2.DBI;
import org.sqlite.SQLiteConfig;

/**
 * The CloudDCDAO class.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public class CloudDCDAO {
	
	final AbstractCloudDCDAO dao;
	
	private PrefixDAO pdao;

	/**
	 * The constructor.
	 */
	public CloudDCDAO() {
		Properties connectionProperties = new Properties();
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connectionProperties = config.toProperties(); 
		DBI dbi = new DBI(DbConstants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractCloudDCDAO.class);
	}

	/**
	 * The method that creates the Cloud table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the Cloud table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a Cloud into the Cloud table.
	 * 
	 * @param c The Cloud to be inserted.
	 */
	public void insert(CloudDC c) throws Exception {
		dao.insert(c);
		pdao = new PrefixDAO();
		for (NetworkAddressIPv4 p : c.getDcNetworks()) {
			pdao.insertByCloudName(p, c.getCloudDcName());
		}
	}
	
	/**
	 * The method that updates a stored Cloud into the Cloud table.
	 * 
	 * @param c The Cloud to be updated.
	 */
	public void update(CloudDC c) throws Exception {
		dao.update(c);
		pdao = new PrefixDAO();
		pdao.deleteByCloudName(c.getCloudDcName());
		if (c.getDcNetworks() != null) {
			for (NetworkAddressIPv4 p : c.getDcNetworks())
				pdao.insertByCloudName(p, c.getCloudDcName());
		}
	}

	/**
	 * The method that finds all the stored Clouds from the Cloud table.
	 * 
	 * @return The list of stored Clouds.
	 */
	public List<CloudDC> findAll() {
		List<CloudPrefixJoinRow> joinRows = dao.findAll();
		Map<String, CloudDC> map = joinRowToCloudDC(joinRows);
		return new ArrayList<CloudDC>(map.values());
	}
	

	/**
	 * The method that finds all the stored local Clouds from the Cloud table.
	 * 
	 * @return The list of stored local Clouds.
	 */
	public List<CloudDC> findLocalClouds() {
		List<CloudPrefixJoinRow> joinRows = dao.findLocalClouds();
		Map<String, CloudDC> map = joinRowToCloudDC(joinRows);
		return new ArrayList<CloudDC>(map.values());
	}
	
	/**
	 * The method that finds all the stored remote Clouds from the Cloud table.
	 * 
	 * @return The list of stored remote Clouds.
	 */
	public List<CloudDC> findRemoteClouds() {
		List<CloudPrefixJoinRow> joinRows = dao.findRemoteClouds();
		Map<String, CloudDC> map = joinRowToCloudDC(joinRows);
		return new ArrayList<CloudDC>(map.values());
	}

	/**
	 * The method that finds a stored Cloud by its Cloud name.
	 * 
	 * @param name The Cloud name.
	 */
	public CloudDC findById(String name) {
		List<CloudPrefixJoinRow> joinRows = dao.findById(name);
		Map<String, CloudDC> map = joinRowToCloudDC(joinRows);
		return map.get(name);
	}
	
	/**
	 * The method that finds a stored Cloud by its AS number.
	 * 
	 * @param asNumber The AS number.
	 */
	public List<CloudDC> findByASNumber(int asNumber) {
		List<CloudPrefixJoinRow> joinRows = dao.findByASNumber(asNumber);
		Map<String, CloudDC> map = joinRowToCloudDC(joinRows);
		return new ArrayList<CloudDC>(map.values());
	}
	
	/**
	 * The method that deletes a stored Cloud by its Cloud name.
	 * 
	 * @param name The Cloud name.
	 */
	public void deleteById(String name) {
		dao.deleteById(name);
	}
	
	/**
	 * The method that deletes all the stored Clouds from the AS table.
	 * 
	 */
	public void deleteAll() {
		dao.deleteAll();
	}
	
	/**
	 * The method that maps a list of clouddc, prefix rows into clouds.
	 * 
	 */
	private Map<String, CloudDC> joinRowToCloudDC (List<CloudPrefixJoinRow> joinRows) {
		Map<String, CloudDC> map = new HashMap<String, CloudDC>();
		for (CloudPrefixJoinRow row : joinRows) {
			if (map.containsKey(row.cloudName)) {
				map.get(row.cloudName).getDcNetworks().add(row.joinRowToPrefix());
			} 
			else {
				map.put(row.cloudName, row.joinRowToCloud());
			}			
		}
		return map;
	}
	
	
	public static class CloudPrefixJoinRow {
		public String cloudName;
		public int asNumber;
		public String daRouterAddress;
		public String sdnAddress;
		public String prefix;
		public int prefixLength;
		public String sboxAddress;
		
		public String getCloudName() {
			return cloudName;
		}

		public void setCloudName(String cloudName) {
			this.cloudName = cloudName;
		}

		public int getAsNumber() {
			return asNumber;
		}

		public void setAsNumber(int asNumber) {
			this.asNumber = asNumber;
		}

		public String getDaRouterAddress() {
			return daRouterAddress;
		}

		public void setDaRouterAddress(String daRouterAddress) {
			this.daRouterAddress = daRouterAddress;
		}

		public String getSdnAddress() {
			return sdnAddress;
		}

		public void setSdnAddress(String sdnAddress) {
			this.sdnAddress = sdnAddress;
		}

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public int getPrefixLength() {
			return prefixLength;
		}

		public void setPrefixLength(int prefixLength) {
			this.prefixLength = prefixLength;
		}

		public String getSboxAddress() {
			return sboxAddress;
		}

		public void setSboxAddress(String sboxAddress) {
			this.sboxAddress = sboxAddress;
		}

		public CloudDC joinRowToCloud() {
			CloudDC cloud = new CloudDC();
			cloud.setCloudDcName(cloudName);
			AS as = new AS();
			as.setAsNumber(asNumber);
			as.setSbox(new SBox(new NetworkAddressIPv4(sboxAddress, 0)));
			cloud.setAs(as);
			DARouter da = new DARouter();	
			da.setManagementAddress(new NetworkAddressIPv4(daRouterAddress, 0));
			cloud.setDaRouter(da);
			SDNController s = new SDNController();	
			s.setManagementAddress(new NetworkAddressIPv4(sdnAddress, 0));
			cloud.setSdnController(s);
			
			NetworkAddressIPv4 p = new NetworkAddressIPv4(prefix, prefixLength);
			List<NetworkAddressIPv4> prefixes = new ArrayList<NetworkAddressIPv4>();
			if (prefix != null)
				prefixes.add(p);
			cloud.setDcNetworks(prefixes);
			
			return cloud;
		}
		
		public NetworkAddressIPv4 joinRowToPrefix() {
			return new NetworkAddressIPv4(prefix, prefixLength);
		}
	}

}
