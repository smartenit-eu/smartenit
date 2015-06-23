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
package eu.smartenit.sbox.ntm.dtm.receiver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.VectorValue;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Tests compensation vector updates management logic.
 * 
 * @author Lukasz Lopatowski
 * @version 3.1
 * 
 */
public class CVectorUpdateControllerTest {
	
	private SystemControlParametersDAO scpDAO = mock(SystemControlParametersDAO.class);
	private TimeScheduleParametersDAO tspDAO = mock(TimeScheduleParametersDAO.class);

	private CVector first = new CVector();
	private CVector second = new CVector();
	private CVector third = new CVector();
	
	@Before
	public void before() {
		VectorValue value11 = new VectorValue(100, new NetworkAddressIPv4("1.1.1.1", 24));
		VectorValue value12 = new VectorValue(-100, new NetworkAddressIPv4("2.1.1.1", 24));
		List<VectorValue> vectorValues1 = Arrays.asList(value11, value12);
		
		first.setSourceAsNumber(1);
		first.setVectorValues(vectorValues1);
		
		VectorValue value21 = new VectorValue(-20, new NetworkAddressIPv4("1.1.1.1", 24));
		VectorValue value22 = new VectorValue(20, new NetworkAddressIPv4("2.1.1.1", 24));
		List<VectorValue> vectorValues2 = Arrays.asList(value21, value22);
		
		second.setSourceAsNumber(1);
		second.setVectorValues(vectorValues2);
		
		VectorValue value31 = new VectorValue(1, new NetworkAddressIPv4("1.1.1.1", 24));
		VectorValue value32 = new VectorValue(-1, new NetworkAddressIPv4("2.1.1.1", 24));
		List<VectorValue> vectorValues3 = Arrays.asList(value31, value32);
		
		third.setSourceAsNumber(1);
		third.setVectorValues(vectorValues3);
		
    	SystemControlParameters scp = new SystemControlParameters(ChargingRule.volume, null, 0.15);
    	when(scpDAO.findLast()).thenReturn(scp);
    	TimeScheduleParameters tsp = new TimeScheduleParameters();
    	tsp.setCompensationPeriod(12);
    	tsp.setReportPeriodDTM(3);
    	when(tspDAO.findLast()).thenReturn(tsp);
    	
    	DAOFactory.setSCPDAOInstance(scpDAO);
    	DAOFactory.setTSPDAOInstance(tspDAO);
    	CVectorUpdateController.deactivate();
	}
	
	@Test
	public void shouldUpdateAfterCompensationPeriod() {
		CVectorUpdateController.activate();
		CVectorUpdateController controller = CVectorUpdateController.getInstance(); 
		controller.increaseCount();
		assertTrue(controller.updateRequired(first));
		controller.sent(first);
		controller.increaseCount();
		assertFalse(controller.updateRequired(first));
		controller.sent(first);
		controller.increaseCount();
		assertFalse(controller.updateRequired(first));
		controller.sent(first);
		controller.increaseCount();
		assertTrue(controller.updateRequired(first));
		controller.sent(first);
		controller.increaseCount();
		assertFalse(controller.updateRequired(first));
	}
	
	@Test
	public void shouldUpdateFirstCVector() {
		CVectorUpdateController.activate();
		assertTrue(CVectorUpdateController.getInstance().updateRequired(first));
	}
	
	@Test
	public void shouldUpdateOnThresholdExceeded() {
		CVectorUpdateController.activate();
		CVectorUpdateController.getInstance().sent(first);
		
		assertTrue(CVectorUpdateController.getInstance().updateRequired(second));
	}

	@Test
	public void shouldNotUpdateThresholdNotExceeded() {
		CVectorUpdateController.activate();
		CVectorUpdateController.getInstance().increaseCount();
		CVectorUpdateController.getInstance().sent(first);
		CVectorUpdateController.getInstance().increaseCount();
		CVectorUpdateController.getInstance().sent(second);
		CVectorUpdateController.getInstance().increaseCount();
		
		assertFalse(CVectorUpdateController.getInstance().updateRequired(third));
	}
	
	@Test
	public void shouldUpdateControllerNotActivated() {
		CVectorUpdateController.deactivate();
		assertTrue(CVectorUpdateController.getInstance().updateRequired(first));
	}

	@After
	public void after() {
		CVectorUpdateController.deactivate();
	}
	
}
