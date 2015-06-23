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
package eu.smartenit.sbox.eca;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dao.CostFunctionDAO;
import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.CostFunction;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;
import eu.smartenit.sbox.ntm.dtm.receiver.DTMTrafficManager;

/**
 * Includes test method for workflow triggered by the {@link EconomicAnalyzer}
 * after receiving updated link and tunnel traffic vectors in the case the
 * {@link ChargingRule}<code>.the95thPercentile</code> is enabled.
 */
public class The95thPercentileWorkflowTest {

	private DTMTrafficManager dtm = mock(DTMTrafficManager.class);
	
	private final SimpleLinkID id1 = new SimpleLinkID("1", null);
	private final SimpleLinkID id2 = new SimpleLinkID("2", null);
	
	private XVector xVector1, xVector2, xVector3, xVector4; 
	private ZVector zVector1, zVector2, zVector3, zVector4;
	
	@Before
	public void setup() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 1200, 300, TimeUnit.SECONDS, 1, 1, 300, 300, 30, 300);
		when(tspdao.findLast()).thenReturn(tsp);
		DAOFactory.setTSPDAOInstance(tspdao);

    	SystemControlParametersDAO scpdao = mock(SystemControlParametersDAO.class);
		SystemControlParameters scp = new SystemControlParameters(ChargingRule.the95thPercentile, null, 0.1);
    	when(scpdao.findLast()).thenReturn(scp);
    	DAOFactory.setSCPDAOInstance(scpdao);
		
		Segment segment1 = new Segment(0L, 20L, 0.0f, 1.0f);
		Segment segment2 = new Segment(20L, 100L, 20.0f, 0.5f);
		Segment segment3 = new Segment(100L, -1L, 20.0f, 2.0f);
		
		List<Segment> segments = new ArrayList<Segment>();
		segments.add(segment1);
		segments.add(segment2);
		segments.add(segment3);
		
		CostFunction costFunction1 = new CostFunction("type", "piecewiselinear", "byte", "Euro", segments);
		CostFunction costFunction2 = new CostFunction("type", "piecewiselinear", "byte", "Euro", segments);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(costFunction1);
		when(dao.findByLinkId(id2)).thenReturn(costFunction2);
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		xVector1 = new XVector(Arrays.asList(new LocalVectorValue(1000, id1), new LocalVectorValue(1200, id2)), 1);
		xVector2 = new XVector(Arrays.asList(new LocalVectorValue(1100, id1), new LocalVectorValue(1100, id2)), 1);
		xVector3 = new XVector(Arrays.asList(new LocalVectorValue(1200, id1), new LocalVectorValue(1000, id2)), 1);
		xVector4 = new XVector(Arrays.asList(new LocalVectorValue(1300, id1), new LocalVectorValue(900, id2)), 1);
		
		zVector1 = new ZVector(Arrays.asList(new LocalVectorValue(600, id1), new LocalVectorValue(10, id2)), 1);
		zVector2 = new ZVector(Arrays.asList(new LocalVectorValue(700, id1), new LocalVectorValue(9, id2)), 1);
		zVector3 = new ZVector(Arrays.asList(new LocalVectorValue(650, id1), new LocalVectorValue(11, id2)), 1);
		zVector4 = new ZVector(Arrays.asList(new LocalVectorValue(900, id1), new LocalVectorValue(8, id2)), 1);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldCalculateRefVectorAfterAccountingPeriod() {
		EconomicAnalyzer eca = new EconomicAnalyzer(dtm);
		
		for(int i=0; i<9; i++)
			eca.updateXZVectors(xVector1, Arrays.asList(zVector1));
		eca.updateXZVectors(xVector2, Arrays.asList(zVector2));
		eca.updateXZVectors(xVector3, Arrays.asList(zVector3));
		eca.updateXZVectors(xVector4, Arrays.asList(zVector4));
		
		verify(dtm, times(3)).updateRVector(any(LocalRVector.class));
		verify(dtm, times(0)).updateLinksWithRVectorAchieved(anyInt(), any(List.class));
	}
	
}
