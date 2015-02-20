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
package eu.smartenit.unada.ctm.cache;

import java.util.Date;

import eu.smartenit.unada.ctm.cache.impl.ContentAccessLoggerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import eu.smartenit.unada.commons.commands.ARP;
import eu.smartenit.unada.db.dao.impl.ContentAccessDAO;
import eu.smartenit.unada.db.dao.impl.TrustedUserDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.ContentAccess;
import eu.smartenit.unada.db.dto.TrustedUser;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VimeoAccessLoggerTest {
	
	TrustedUserDAO trustedUserDAO = mock(TrustedUserDAO.class);
	ContentAccessDAO contentAccessDAO = mock(ContentAccessDAO.class);
	ARP arp = mock(ARP.class);
	ContentAccessLogger contentAccessLogger;
	
	@Before
	public void setup() {
        /*
		when(arp.execute("1.1.1.1")).thenReturn(null);
		when(arp.execute("2.2.2.2")).thenReturn("00::00");
		when(arp.execute("3.3.3.3")).thenReturn("00::ff");
		ARP.setArpInstance(arp);
		
		when(trustedUserDAO.findByMacAddress("00::00")).thenReturn(null);
		TrustedUser trustedUser = new TrustedUser();
		trustedUser.setFacebookID("333333");
		trustedUser.setMacAddress("00::ff");
		when(trustedUserDAO.findByMacAddress("00::ff")).thenReturn(trustedUser);
		DAOFactory.setTrustedUserDAO(trustedUserDAO);
		
		when(contentAccessDAO.findLatestByContentIDFacebookID(1213131, "333333")).thenReturn(null);
		ContentAccess contentAccess = new ContentAccess();
		contentAccess.setContentID(345678);
		contentAccess.setFacebookID("333333");
		contentAccess.setTimeStamp(new Date(System.currentTimeMillis()));
		when(contentAccessDAO.findLatestByContentIDFacebookID(345678, "333333")).thenReturn(contentAccess);
		contentAccess = new ContentAccess();
		contentAccess.setContentID(987654);
		contentAccess.setFacebookID("333333");
		contentAccess.setTimeStamp(new Date(System.currentTimeMillis()-120000));
		when(contentAccessDAO.findLatestByContentIDFacebookID(987654, "333333")).thenReturn(contentAccess);
		Mockito.doNothing().when(contentAccessDAO).insert(any(ContentAccess.class));
		*/

        when(contentAccessDAO.findLatestByContentID(1213131)).thenReturn(null);
        ContentAccess contentAccess = new ContentAccess();
        contentAccess.setContentID(345678);
        contentAccess.setFacebookID("333333");
        contentAccess.setTimeStamp(new Date(System.currentTimeMillis()));
        when(contentAccessDAO.findLatestByContentID(345678)).thenReturn(contentAccess);
        contentAccess = new ContentAccess();
        contentAccess.setContentID(987654);
        contentAccess.setFacebookID("333333");
        contentAccess.setTimeStamp(new Date(System.currentTimeMillis()-120000));
        when(contentAccessDAO.findLatestByContentID(987654)).thenReturn(contentAccess);
        Mockito.doNothing().when(contentAccessDAO).insert(any(ContentAccess.class));
		DAOFactory.setContentAccessDAO(contentAccessDAO);
		
	}
	
	@Test @Ignore
	public void testUpdateAccessLogNoMAC() {
        contentAccessLogger = new ContentAccessLoggerImpl(1213131, "1.1.1.1");
		boolean access = contentAccessLogger.updateAccessLog();
		assertFalse(access);
	}
	
	@Test @Ignore
	public void testUpdateAccessLogNoTrustedUser() {
        contentAccessLogger = new ContentAccessLoggerImpl(1213131, "2.2.2.2");
		boolean access = contentAccessLogger.updateAccessLog();
		assertFalse(access);
	}
	
	@Test
	public void testUpdateAccessLogNoContentAccess() {
        contentAccessLogger = new ContentAccessLoggerImpl(1213131, "3.3.3.3");
		boolean access = contentAccessLogger.updateAccessLog();
		assertTrue(access);
	}
	
	@Test
	public void testUpdateAccessLogRecentContentAccess() {
        contentAccessLogger = new ContentAccessLoggerImpl(345678, "3.3.3.3");
		boolean access = contentAccessLogger.updateAccessLog();
		assertFalse(access);
	}
	
	@Test
	public void testUpdateAccessLogNotRecentContentAccess() {
        contentAccessLogger = new ContentAccessLoggerImpl(987654, "3.3.3.3");
		boolean access = contentAccessLogger.updateAccessLog();
		assertTrue(access);
	}
	
	@After
	public void teardown() {
		
	}

}
