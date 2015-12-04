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
package eu.smartenit.sbox.eca;

import static eu.smartenit.sbox.eca.TestVectorsCreationHelper.createXVector;
import static eu.smartenit.sbox.eca.TestVectorsCreationHelper.createZVectorList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dao.CostFunctionDAO;
import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.CostFunction;
import eu.smartenit.sbox.db.dto.LocalVector;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.ntm.dtm.receiver.DTMTrafficManager;

/**
 * Includes test methods for handling traffic updates when {@link ChargingRule}
 * <code>.volume</code> is enabled. Traffic values are aggregated during an
 * accounting period and then reset.
 */
public class TotalVolumeReportsAggregationTest {
	
	private static final int x1 = 0;
	private static final int x2 = 1;
	
	private static final CostFunction costFunction1;
	private static final CostFunction costFunction2;
	private static final List<CostFunction> costFunctions;

	private final static DTMTrafficManager dtm = mock(DTMTrafficManager.class);
	private final static TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
	private final static SystemControlParametersDAO scpdao = mock(SystemControlParametersDAO.class);
	
	private final SimpleLinkID id1 = new SimpleLinkID("1", null);
	private final SimpleLinkID id2 = new SimpleLinkID("2", null);
	
	static {
		Segment segment1 = new Segment(0L, 20L, 0.0f, 1.0f);
		Segment segment2 = new Segment(20L, 100L, 20.0f, 0.5f);
		Segment segment3 = new Segment(100L, -1L, 20.0f, 2.0f);
		
		List<Segment> segments = new ArrayList<Segment>();
		segments.add(segment1);
		segments.add(segment2);
		segments.add(segment3);
		
		costFunction1 = new CostFunction(
				"type", 
				"piecewiselinear", 
				"byte",
				"Euro",
				segments);
		
		costFunction2 = new CostFunction(
				"type", 
				"piecewiselinear", 
				"byte",
				"Euro",
				segments);
		
		costFunctions = new ArrayList<CostFunction>();
		costFunctions.add(costFunction1);
		costFunctions.add(costFunction2);
	}
	
	@Before
	public void setup() {
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		DAOFactory.setTSPDAOInstance(tspdao);

    	SystemControlParameters scp = new SystemControlParameters(ChargingRule.volume, null, 0.1);
    	when(scpdao.findLast()).thenReturn(scp);
    	DAOFactory.setSCPDAOInstance(scpdao);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(costFunction1);
		when(dao.findByLinkId(id2)).thenReturn(costFunction2);
		DAOFactory.setCostFunctionDAOInstance(dao);
	}
	
	/**
	 * Tests whether the traffic volume accumulation is correct,
	 * without crossing the accounting period threshold 
	 */
	@Test
	public void prepareTotalVolumeXVBeforeAccountingPeriod() {
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		
		for (int i = 0; i < 9; i++) {
			ea.updateXZVectors(createXVector(1, 1, id1, id2, 2), createZVectorList(2, 2, id1, id2, 2));
		}

		assertEquals(9, ea.trafficContainer.getTrafficValuesForLinks()[x1]);
		assertEquals(9, ea.trafficContainer.getTrafficValuesForLinks()[x2]);
	}
	
	/**
	 * Tests whether the traffic volume accumulation is correct,
	 * without crossing the accounting period threshold 
	 */
	@Test
	public void prepareTotalVolumeZVBeforeAccountingPeriod() {
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		
		for (int i = 0; i < 9; i++) {
			ea.updateXZVectors(createXVector(1, 1, id1, id2, 2), createZVectorList(2, 4, id1, id2, 2));
		}
		
		assertEquals(18, ea.trafficContainer.getTrafficValuesForTunnels()[x1]);
		assertEquals(36, ea.trafficContainer.getTrafficValuesForTunnels()[x2]);
	}

	/**
	 * Tests whether the traffic volume accumulation is correct,
	 * when crossing the accounting period threshold 
	 */
	@Test
	public void prepareTotalVolumeXVAfterAccountingPeriod() {
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		
		for (int i = 0; i < 11; i++) {
			ea.updateXZVectors(createXVector(1, 2, id1, id2, 2), createZVectorList(2, 2, id1, id2, 2));
		}
		
		assertEquals(1, ea.trafficContainer.getTrafficValuesForLinks()[x1]);
		assertEquals(2, ea.trafficContainer.getTrafficValuesForLinks()[x2]);
	}
	
	/**
	 * Tests whether the traffic volume accumulation is correct,
	 * when crossing the accounting period threshold 
	 */
	@Test
	public void prepareTotalVolumeZVAfterAccountingPeriod() {
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		
		for (int i = 0; i < 22; i++) {
			ea.updateXZVectors(createXVector(1, 1, id1, id2, 2), createZVectorList(2, 4, id1, id2, 2));
		}
		
		assertEquals(4, ea.trafficContainer.getTrafficValuesForTunnels()[x1]);
		assertEquals(8, ea.trafficContainer.getTrafficValuesForTunnels()[x2]);
	}
	
	/**
	 * Tests whether the number of the XVector and the RVector match
	 */
	@Test
	public void asNumberMatch() {
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		XVector xvector = createXVector(14, 22, id1, id2, 2);
		ea.trafficContainer.storeTrafficValues(xvector, createZVectorList(3,7, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);
		
		assertEquals(xvector.getSourceAsNumber(), referenceVector.getSourceAsNumber());
	}

	/**
	 * Tests whether the number of the XVector and the RVector match
	 */
	@Test
	public void linkIDConsistence() {
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		XVector xvector = createXVector(14, 22, id1, id2, 2);
		ea.trafficContainer.storeTrafficValues(xvector, createZVectorList(3,7, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);

		assertEquals(xvector.getVectorValues().get(0).getLinkID(), referenceVector.getVectorValues().get(0).getLinkID());
		assertEquals(xvector.getVectorValues().get(1).getLinkID(), referenceVector.getVectorValues().get(1).getLinkID());
	}
	
}
