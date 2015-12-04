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
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.ntm.dtm.receiver.DTMTrafficManager;

/**
 * Includes a set of test methods for reference vector calculation logic
 * implemented by the {@link ReferenceVectorCalculator}.
 */
public class ReferenceVectorCalculationTest {

	private static final CostFunction costFunction1;
	private static final CostFunction costFunction2;
	private static final CostFunction costFunction3;
	private static final CostFunction costFunction4;
	private static final List<CostFunction> costFunctions;
        private static final CostFunction costFunction5;
        private static final CostFunction costFunction6;
        
	private final static DTMTrafficManager dtm = mock(DTMTrafficManager.class);
	private final static CostFunctionDAO dao = mock(CostFunctionDAO.class);
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
		
		//Create cost functions for test data
		List<Segment> segments1 = new ArrayList<Segment>();
		segments1.add(new Segment(0, 10, 5, 1));
		segments1.add(new Segment(10, 20, 15, 0));
		segments1.add(new Segment(20, -1, 0, 0.75f));		
		costFunction3 = new CostFunction("type", "piecewiselinear", "byte", "Euro", segments1);
		
		List<Segment> segments2 = new ArrayList<Segment>();
		segments2.add(new Segment(0, 14, 10, 1));
		segments2.add(new Segment(14, 30, 20.5f, 0.25f));
		segments2.add(new Segment(30, -1, -2, 1));
		costFunction4 = new CostFunction("type", "piecewiselinear", "byte", "Euro", segments2);
                
                List<Segment> segments5 = new ArrayList<Segment>();
		segments5.add(new Segment(0, 42240000000L, 0, 1.04167E-08f));
		segments5.add(new Segment(42240000000L, 61440000000L, -990, 3.38542E-08f));
		segments5.add(new Segment(61440000000L, -1, -5310, 1.04167E-07f));
                costFunction5 = new CostFunction("type", "piecewiselinear", "byte", "Euro", segments5);
                
                List<Segment> segments6 = new ArrayList<Segment>();
		segments6.add(new Segment(0, 19200000000L, 200, 0));
		segments6.add(new Segment(19200000000L, 96000000000L, -300, 2.60417E-08f));
		segments6.add(new Segment(96000000000L, -1, -5300, 7.8125E-08f));
                costFunction6 = new CostFunction("type", "piecewiselinear", "byte", "Euro", segments6);
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
	 * Tests the reference vector calculation using a first set of test data
	 */
	@Test
	public void firstTestData() {
		when(dao.findByLinkId(id1)).thenReturn(costFunction3);
		when(dao.findByLinkId(id2)).thenReturn(costFunction4);
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		ea.trafficContainer.storeTrafficValues(createXVector(14, 22, id1, id2, 2), createZVectorList(2, 1, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);
		
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		assertEquals(15, vectorValueList.get(0).getValue());
		assertEquals(21, vectorValueList.get(1).getValue());
	}
	
	/**
	 * Tests the reference vector calculation using a second set of test data
	 */	
	@Test
	public void secondTestData() {
		when(dao.findByLinkId(id1)).thenReturn(costFunction3);
		when(dao.findByLinkId(id2)).thenReturn(costFunction4);
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		ea.trafficContainer.storeTrafficValues(createXVector(14, 22, id1, id2, 2), createZVectorList(3,4, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);
		
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		assertEquals(18, vectorValueList.get(0).getValue());
		assertEquals(18, vectorValueList.get(1).getValue());
	}
	
	/**
	 * Tests the reference vector calculation using a third set of test data
	 */
	@Test
	public void thirdTestData() {
		when(dao.findByLinkId(id1)).thenReturn(costFunction3);
		when(dao.findByLinkId(id2)).thenReturn(costFunction4);
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		ea.trafficContainer.storeTrafficValues(createXVector(14, 22, id1, id2, 2), createZVectorList(2,3, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);
		
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		assertEquals(17, vectorValueList.get(0).getValue());
		assertEquals(19, vectorValueList.get(1).getValue());
	}
	
	/**
	 * Tests the reference vector calculation using a fourth set of test data
	 */
	@Test
	public void fourthTestData() {
		when(dao.findByLinkId(id1)).thenReturn(costFunction3);
		when(dao.findByLinkId(id2)).thenReturn(costFunction4);
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		ea.trafficContainer.storeTrafficValues(createXVector(14, 22, id1, id2, 2), createZVectorList(4, 4, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);
		
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		assertEquals(18, vectorValueList.get(0).getValue());
		assertEquals(18, vectorValueList.get(1).getValue());
	}

	/**
	 * Tests the reference vector calculation using a fifth set of test data
	 */
	@Test
	public void fifthTestData() {
		when(dao.findByLinkId(id1)).thenReturn(costFunction3);
		when(dao.findByLinkId(id2)).thenReturn(costFunction4);
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		ea.trafficContainer.storeTrafficValues(createXVector(14, 22, id1, id2, 2), createZVectorList(3,7, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);
		
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		// All values on the line x1 + x2 = 36 between points (4,32) and (6,30) are fine
		assertEquals(20, vectorValueList.get(0).getValue());
		assertEquals(16, vectorValueList.get(1).getValue());
	}
	
	/**
	 * Tests the reference vector calculation using a sixth set of test data
	 */
	@Test
	public void sixthTestData() {
		when(dao.findByLinkId(id1)).thenReturn(costFunction3);
		when(dao.findByLinkId(id2)).thenReturn(costFunction4);
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		ea.trafficContainer.storeTrafficValues(createXVector(14, 22, id1, id2, 2), createZVectorList(2, 1, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);
		
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		assertEquals(15, vectorValueList.get(0).getValue());
		assertEquals(21, vectorValueList.get(1).getValue());
	}
	
	/**
	 * Tests the reference vector calculation using a seventh set of test data
	 */
	@Test
	public void seventhTestData() {
		when(dao.findByLinkId(id1)).thenReturn(costFunction3);
		when(dao.findByLinkId(id2)).thenReturn(costFunction4);
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		ea.trafficContainer.storeTrafficValues(createXVector(14, 22, id1, id2, 2), createZVectorList(10, 7, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);
		
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		// The values 4/32 would also be ok (i.e. all values on the line 4/32 - 6/30)
		assertEquals(6, vectorValueList.get(0).getValue());
		assertEquals(30, vectorValueList.get(1).getValue());
	}
	
	/**
	 * Tests the reference vector calculation using a eighth set of test data
	 */
	@Test
	public void eightTestData() {
		when(dao.findByLinkId(id1)).thenReturn(costFunction3);
		when(dao.findByLinkId(id2)).thenReturn(costFunction4);
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		ea.trafficContainer.storeTrafficValues(createXVector(8, 28, id1, id2, 2), createZVectorList(3, 7, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);
		
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		// The values 5/31 would also be ok (i.e. all values on the line 5/31 - 6/30)
		assertEquals(6, vectorValueList.get(0).getValue());
		assertEquals(30, vectorValueList.get(1).getValue());
	}	
	
	/**
	 * Tests the reference vector calculation using a ninth set of test data
	 */
	@Test
	public void ninthTestData() {
		when(dao.findByLinkId(id1)).thenReturn(costFunction3);
		when(dao.findByLinkId(id2)).thenReturn(costFunction4);
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		ea.trafficContainer.storeTrafficValues(createXVector(14, 22, id1, id2, 2), createZVectorList(3, 4, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);
		
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		assertEquals(18, vectorValueList.get(0).getValue());
		assertEquals(18, vectorValueList.get(1).getValue());
	}
	
	/**
	 * Tests the reference vector calculation using a tenth set of test data
	 */
	@Test
	public void tenthTestData() {
		when(dao.findByLinkId(id1)).thenReturn(costFunction3);
		when(dao.findByLinkId(id2)).thenReturn(costFunction4);
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		ea.trafficContainer.storeTrafficValues(createXVector(14, 22, id1, id2, 2), createZVectorList(3, 7, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);
		
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		assertEquals(20, vectorValueList.get(0).getValue());
		assertEquals(16, vectorValueList.get(1).getValue());
	}
	
	/**
	 * Tests the reference vector calculation using a eleventh set of test data
	 */
	@Test
	public void eleventhTestData() {
		when(dao.findByLinkId(id1)).thenReturn(costFunction3);
		when(dao.findByLinkId(id2)).thenReturn(costFunction4);
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		ea.trafficContainer.storeTrafficValues(createXVector(14, 22, id1, id2, 2), createZVectorList(5, 7, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);
		
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		assertEquals(20, vectorValueList.get(0).getValue());
		assertEquals(16, vectorValueList.get(1).getValue());
	}
        
        @Test
        public void twelvethTestData(){
            when(dao.findByLinkId(id1)).thenReturn(costFunction5);
		when(dao.findByLinkId(id2)).thenReturn(costFunction6);
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		ea.trafficContainer.storeTrafficValues(createXVector(67115407122L, 97571439721L, id1, id2, 2), createZVectorList(13112002694L, 19749182206L, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);
		
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		assertEquals(61440000000L, vectorValueList.get(0).getValue());
		assertEquals(103246846843L, vectorValueList.get(1).getValue());
        }
        
        @Test
        public void thirteenthTestData(){
            when(dao.findByLinkId(id1)).thenReturn(costFunction6);
		when(dao.findByLinkId(id2)).thenReturn(costFunction5);
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		ea.trafficContainer.storeTrafficValues(createXVector(97571439721L, 67115407122L, id1, id2, 2), createZVectorList(19749182206L, 13112002694L, id1, id2, 2));
		
		LocalVector referenceVector = 
				ea.calculator.calculate(ea.trafficContainer.getTrafficValuesForLinks(), ea.trafficContainer.getTrafficValuesForTunnels(), 2);
		
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		assertEquals(61440000000L, vectorValueList.get(1).getValue());
		assertEquals(103246846843L, vectorValueList.get(0).getValue());
        }
	
}
