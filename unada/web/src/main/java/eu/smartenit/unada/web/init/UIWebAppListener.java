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
package eu.smartenit.unada.web.init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import eu.smartenit.unada.commons.threads.UnadaThreadService;
import eu.smartenit.unada.ctm.cache.impl.UnadaOrchestrator;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Cache;
import eu.smartenit.unada.db.dto.SocialPredictionParameters;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;
import eu.smartenit.unada.db.dto.WifiConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.db.dao.util.Constants;
import eu.smartenit.unada.db.dao.util.Tables;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * The UIWebAppListener class. It initializes all the required functions of the
 * uNaDa UI application.
 * 
 * @author George Petropoulos
 * @version 3.1
 * 
 */
@WebListener
public class UIWebAppListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory
			.getLogger(UIWebAppListener.class);

	private static final String hostapdFile = "/etc/hostapd/hostapd.conf";

	/**
	 * The method that initializes all functions, when context is initialized.
	 * 
	 * @param arg0
	 *            The servlet context event
	 * 
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		logger.info("uNada application is initialized.");

		Constants.DBI_URL = "jdbc:h2:" + System.getenv("HOME") + "/unada";
		logger.info("Updated db url to " + Constants.DBI_URL);

		Tables t = new Tables();
		t.createTables();

		configure();
		
		UnadaThreadService.getThreadService().schedule(new UnadaOrchestrator(),
				0, TimeUnit.SECONDS);
	}

	/**
	 * The method that finalizes all functions, when context is destroyed.
	 * 
	 * @param arg0
	 *            The servlet context event
	 * 
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("uNada application is finalized.");
        UnadaThreadService.shutdownNowThreads();
	}

	private void configure() {
		UNaDaConfiguration u = DAOFactory.getuNaDaConfigurationDAO().findLast();
		if (u != null) {
			logger.debug("Existing configuration parameters = " + u);
		} else {
			u = new UNaDaConfiguration();

			// Overlay parameters
			u.setChunkSize(102400);
			u.setBootstrapNode(false);
			u.setIpAddress("150.254.185.239");
			u.setPort(4001);
			u.setMacAddress(getMacAddress());

			// Prediction parameters and intervals
			u.setSocialPredictionEnabled(true);
			u.setOverlayPredictionEnabled(true);
			u.setPredictionInterval(60 * 60000); // every 60 minutes
			u.setSocialInterval(10 * 60000); // every 10 minutes
			u.setOverlayInterval(60 * 60000); // every 60 minutes

			// SSID parameters
			updateSSIDs(u);
			logger.info("Unada configuration parameters = " + u);
			try {
				DAOFactory.getuNaDaConfigurationDAO().insert(u);
			} catch (Exception e) {
				logger.error("Exception while inserting unada configuration: "
						+ e.getMessage());
			}
		}

		SocialPredictionParameters s = DAOFactory.getSocialPredictionParametersDAO().findLast();
		if (s == null) {
			s = new SocialPredictionParameters();
			s.setLambda1(-3646.8405);
			s.setLambda2(-348.238);
			s.setLambda3(-174.6666);
			s.setLambda4(32.539);
			s.setLambda5(-1.0468);
			s.setLambda6(1.8463);
			try {
				DAOFactory.getSocialPredictionParametersDAO().insert(s);
			} catch (Exception e) {
				logger.error("Exception while inserting social prediction parameters: "
						+ e.getMessage());
			}
		}
		
		Cache c = DAOFactory.getCacheDAO().findLast();
		if (c == null) {
			c = new Cache();
			c.setSize(8000000000L); //8 GB
			c.setSizeThreshold(500000000L); //500 MB
			c.setTimeToLive(5 * 24 * 60 * 60 * 1000); //5 days
			c.setSocialThreshold(0);
			c.setOverlayThreshold(0);
			DAOFactory.getCacheDAO().insert(c);
		}
	}

	private String getMacAddress() {
		String macAddress = null;
		try {
			Enumeration<NetworkInterface> networks = NetworkInterface
					.getNetworkInterfaces();
			while (networks.hasMoreElements()) {
				NetworkInterface network = networks.nextElement();
                if (!network.getName().startsWith("eth"))
                    continue;
				byte[] mac = network.getHardwareAddress();

				if (mac != null) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < mac.length; i++) {
						sb.append(String.format("%02X%s", mac[i],
								(i < mac.length - 1) ? ":" : ""));
					}
					macAddress = sb.toString();
					logger.debug("Found MAC address = " + macAddress);
					return macAddress;
				}
			}
		} catch (SocketException e) {
			logger.error("Error while getting the network interfaces of the uNaDa: "
					+ e.getMessage());
		}
		if (macAddress == null) {
			// if null mac address so far, then return some random string..
			macAddress = UUID.randomUUID().toString();
		}
		return macAddress;
	}

	private static void updateSSIDs(UNaDaConfiguration u) {
		File f;
		f = new File(hostapdFile);
		// if hostapd software is used for SSID configuration
		if (f.exists()) {
			logger.debug("Reading /etc/hostapd/hostapd.conf file.");
			Scanner in;
			try {
				in = new Scanner(f);

				String currentLine;
				WifiConfiguration wifi;
				String ssid = null;
				String password = null;

				while (in.hasNextLine()) {
					currentLine = in.nextLine();
					if (currentLine.startsWith("ssid")) {
						ssid = currentLine.split("=")[1].trim();
					} 
					if (currentLine.startsWith("wpa_passphrase")) {
						password = currentLine.split("=")[1].trim();
					} 
					if (currentLine.isEmpty() || !in.hasNextLine()) {
                        if (ssid != null && ssid.equals("HORST")) {
                            wifi = new WifiConfiguration(ssid, password);
                            logger.debug("Setting open wifi " + wifi);
                            u.setOpenWifi(wifi);
                        }
						else if (ssid != null && password != null) {
							wifi = new WifiConfiguration(ssid, password);
							logger.debug("Setting private wifi " + wifi);
							u.setPrivateWifi(wifi);
						}
						ssid = null;
						password = null;
					}
				}
				in.close();
			} catch (Exception e) {
				logger.error("Exception while reading /etc/hostapd/hostapd.conf file " 
						+ e.getMessage());
			}
			return;
		}
	}
}