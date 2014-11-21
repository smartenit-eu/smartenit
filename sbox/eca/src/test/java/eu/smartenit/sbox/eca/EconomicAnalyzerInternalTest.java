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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.smartenit.sbox.db.dao.CostFunctionDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dto.CostFunction;
import eu.smartenit.sbox.db.dto.LocalVector;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.VectorValue;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;
import eu.smartenit.sbox.ntm.dtm.receiver.DTMTrafficManager;

/**
 * Test class for the {@link EconomicAnalyzerInternal} class.
 * 
 * @author D. D&ouml;nni, K. Bhargav, T. Bocek
 *
 */
public class EconomicAnalyzerInternalTest {
	
	private static final int x1 = 0;
	private static final int x2 = 1;
	
	private static final CostFunction costFunction1;	
	private static final CostFunction costFunction2;
	private static final List<CostFunction> costFunctions;

	private static final CostFunction cf1;
	private static final CostFunction cf2;
	private final static List<CostFunction> cfList = new ArrayList<CostFunction>();

	
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
		cf1 = new CostFunction("type", "piecewiselinear", "byte", "Euro", segments1);
		
		List<Segment> segments2 = new ArrayList<Segment>();
		segments2.add(new Segment(0, 14, 10, 1));
		segments2.add(new Segment(14, 30, 20.5f, 0.25f));
		segments2.add(new Segment(30, -1, -2, 1));
		cf2 = new CostFunction("type", "piecewiselinear", "byte", "Euro", segments2);
		
		cfList.add(cf1);
		cfList.add(cf2);		
	}
	
	/**
	 * Creates an {@link EconomicAnalyzerInternalTest} instance.
	 */
	public EconomicAnalyzerInternalTest() {		
	}
	
	/**
	 * Tests whether the traffic volume accumulation is correct,
	 * without crossing the accounting period threshold 
	 */
	@Test
	public void prepareTotalVolumeXVBeforeAccountingPeriod() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);

		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);
		
		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(costFunction1);
		when(dao.findByLinkId(id2)).thenReturn(costFunction2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		
		for (int i = 0; i < 9; i++) {
			ea.updateXZVectors(createXVector(1, 1, id1, id2, 2), createZVectorList(2, 2, id1, id2, 2));
		}
		
		try {
			Field X_V_Field = EconomicAnalyzerInternal.class.getDeclaredField("X_V");
			X_V_Field.setAccessible(true);
			
			long[] X_V = (long[]) X_V_Field.get(ea);
			assertEquals(9, X_V[x1]);
			assertEquals(9, X_V[x2]);
			
		} catch (Exception e) {
			fail();
		}
	}
	
	/**
	 * Tests whether the traffic volume accumulation is correct,
	 * without crossing the accounting period threshold 
	 */
	@Test
	public void prepareTotalVolumeZVBeforeAccountingPeriod() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);
		
		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(costFunction1);
		when(dao.findByLinkId(id2)).thenReturn(costFunction2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		
		for (int i = 0; i < 9; i++) {
			ea.updateXZVectors(createXVector(1, 1, id1, id2, 2), createZVectorList(2, 4, id1, id2, 2));
		}
		
		try {
			Field Z_V_Field = EconomicAnalyzerInternal.class.getDeclaredField("Z_V");
			Z_V_Field.setAccessible(true);
			
			long[] Z_V = (long[]) Z_V_Field.get(ea);
			assertEquals(18, Z_V[x1]);
			assertEquals(36, Z_V[x2]);
			
		} catch (Exception e) {
			fail();
		}
		
	}

	/**
	 * Tests whether the traffic volume accumulation is correct,
	 * when crossing the accounting period threshold 
	 */
	@Test
	public void prepareTotalVolumeXVAfterAccountingPeriod() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);
		
		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(costFunction1);
		when(dao.findByLinkId(id2)).thenReturn(costFunction2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		
		for (int i = 0; i < 11; i++) {
			ea.updateXZVectors(createXVector(1, 2, id1, id2, 2), createZVectorList(2, 2, id1, id2, 2));
		}
		
		try {
			Field X_V_Field = EconomicAnalyzerInternal.class.getDeclaredField("X_V");
			X_V_Field.setAccessible(true);
			
			long[] X_V = (long[]) X_V_Field.get(ea);
			assertEquals(1, X_V[x1]);
			assertEquals(2, X_V[x2]);
			
		} catch (Exception e) {
			fail();
		}		
		
	}
	
	/**
	 * Tests whether the traffic volume accumulation is correct,
	 * when crossing the accounting period threshold 
	 */
	@Test
	public void prepareTotalVolumeZVAfterAccountingPeriod() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);
		
		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(costFunction1);
		when(dao.findByLinkId(id2)).thenReturn(costFunction2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		
		for (int i = 0; i < 22; i++) {
			ea.updateXZVectors(createXVector(1, 1, id1, id2, 2), createZVectorList(2, 4, id1, id2, 2));
		}
		
		try {
			Field Z_V_Field = EconomicAnalyzerInternal.class.getDeclaredField("Z_V");
			Z_V_Field.setAccessible(true);
			
			long[] Z_V = (long[]) Z_V_Field.get(ea);
			assertEquals(4, Z_V[x1]);
			assertEquals(8, Z_V[x2]);
			
		} catch (Exception e) {
			fail();
		}		
		
	}
	
	/**
	 * Creates an {@link XVector} object from traffic volumes and link ids.
	 * 
	 * @param x1 Traffic volume on link 1
	 * @param x2 Traffic volume on link 2
	 * @param link1 {@link SimpleLinkID} of link 1.
	 * @param link2 {@link SimpleLinkID} of link 2.
	 * @param sourceAsNumber The number of the source AS
	 * @return The {@link XVector} object.
	 */
	private static XVector createXVector(long x1, long x2, SimpleLinkID link1, SimpleLinkID link2, int sourceAsNumber) {
		List<LocalVectorValue> vectorValues = createVectorValues(x1, x2, link1, link2);
		return new XVector(vectorValues, sourceAsNumber);
	}
	
	/**
	 * Creates a {@link List} {@link ZVector} objects from traffic volumes and link ids
	 * 
	 * @param z1 Traffic volume on link 1
	 * @param z2 Traffic volume on link 2
	 * @param link1 {@link SimpleLinkID} of link 1.
	 * @param link2 {@link SimpleLinkID} of link 2.
	 * @param sourceAsNumber The number of the source AS
	 * @return The {@link List} containing the {@link ZVector} objects.
	 */
	private static List<ZVector> createZVectorList(long z1, long z2, SimpleLinkID link1, SimpleLinkID link2, int sourceAsNumber) {
		List<ZVector> zVectorList = new ArrayList<ZVector>();
		List<LocalVectorValue> vectorValues = createVectorValues(z1, z2, link1, link2);
		zVectorList.add(new ZVector(vectorValues, sourceAsNumber));
		return zVectorList;
	}
	
	/**
	 * Creates a {@link List} of {@link VectorValue}s from traffic volumes and link ids
	 * 
	 * @param p Traffic volume on link 1
	 * @param q Traffic volume on link 2
	 * @param link1 {@link SimpleLinkID} of link 1.
	 * @param link2 {@link SimpleLinkID} of link 1.
	 * @return A {@link List} of {@link VectorValue}s
	 */
	private static List<LocalVectorValue> createVectorValues(long p, long q, SimpleLinkID link1, SimpleLinkID link2) {
		List<LocalVectorValue> values = new ArrayList<LocalVectorValue>();
		values.add(new LocalVectorValue(p, link1));
		values.add(new LocalVectorValue(q, link2));
		return values;
	}

	/**
	 * Tests the reference vector calculation using a first set of test data
	 */
	@Test
	public void firstTestData() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);

		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(cf1);
		when(dao.findByLinkId(id2)).thenReturn(cf2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		
		
		ea.updateXZVectors(createXVector(14, 22, id1, id2, 2), createZVectorList(2, 1, id1, id2, 2));
		
		LocalVector referenceVector = ea.calculateReferenceVector(2);
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		assertEquals(15, vectorValueList.get(0).getValue());
		assertEquals(21, vectorValueList.get(1).getValue());

	}
	
	/**
	 * Tests the reference vector calculation using a second set of test data
	 */	
	@Test
	public void secondTestData() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);
		
		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(cf1);
		when(dao.findByLinkId(id2)).thenReturn(cf2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);
		
		ea.updateXZVectors(createXVector(14, 22, id1, id2, 2), createZVectorList(3,4, id1, id2, 2));

		LocalVector referenceVector = ea.calculateReferenceVector(2);
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		assertEquals(18, vectorValueList.get(0).getValue());
		assertEquals(18, vectorValueList.get(1).getValue());		
	}
	
	
	/**
	 * Tests the reference vector calculation using a third set of test data
	 */
	@Test
	public void thirdTestData() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);

		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(cf1);
		when(dao.findByLinkId(id2)).thenReturn(cf2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);

		
		ea.updateXZVectors(createXVector(14, 22, id1, id2, 2), createZVectorList(2,3, id1, id2, 2));

		LocalVector referenceVector = ea.calculateReferenceVector(2);
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		assertEquals(17, vectorValueList.get(0).getValue());
		assertEquals(19, vectorValueList.get(1).getValue());		
		
	}
	
	/**
	 * Tests the reference vector calculation using a fourth set of test data
	 */
	@Test
	public void fourthTestData() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);

		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(cf1);
		when(dao.findByLinkId(id2)).thenReturn(cf2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);

		
		ea.updateXZVectors(createXVector(14, 22, id1, id2, 2), createZVectorList(4, 4, id1, id2, 2));

		LocalVector referenceVector = ea.calculateReferenceVector(2);
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		assertEquals(18, vectorValueList.get(0).getValue());
		assertEquals(18, vectorValueList.get(1).getValue());		
		
	}

	/**
	 * Tests the reference vector calculation using a fifth set of test data
	 */
	@Test
	public void fifthTestData() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);

		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(cf1);
		when(dao.findByLinkId(id2)).thenReturn(cf2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);

		
		ea.updateXZVectors(createXVector(14, 22, id1, id2, 2), createZVectorList(3,7, id1, id2, 2));

		LocalVector referenceVector = ea.calculateReferenceVector(2);
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
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);

		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(cf1);
		when(dao.findByLinkId(id2)).thenReturn(cf2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);

		
		ea.updateXZVectors(createXVector(14, 22, id1, id2, 2), createZVectorList(2, 1, id1, id2, 2));

		LocalVector referenceVector = ea.calculateReferenceVector(2);
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		
		assertEquals(15, vectorValueList.get(0).getValue());
		assertEquals(21, vectorValueList.get(1).getValue());		
		
	}
	
	/**
	 * Tests the reference vector calculation using a seventh set of test data
	 */
	@Test
	public void seventhTestData() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);

		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(cf1);
		when(dao.findByLinkId(id2)).thenReturn(cf2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);

		
		ea.updateXZVectors(createXVector(14, 22, id1, id2, 2), createZVectorList(10, 7, id1, id2, 2));

		LocalVector referenceVector = ea.calculateReferenceVector(2);
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		
		// The values 6, 30 would also be ok (i.e. all values on the line 4/32 - 6/30)
		assertEquals(4, vectorValueList.get(0).getValue());
		assertEquals(32, vectorValueList.get(1).getValue());		
		
	}
	
	/**
	 * Tests the reference vector calculation using a eighth set of test data
	 */
	@Test
	public void eightTestData() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);

		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(cf1);
		when(dao.findByLinkId(id2)).thenReturn(cf2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);

		
		ea.updateXZVectors(createXVector(8, 28, id1, id2, 2), createZVectorList(3, 7, id1, id2, 2));

		LocalVector referenceVector = ea.calculateReferenceVector(2);
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		
		// The values 6, 30 would also be ok (i.e. all values on the line 5/31 - 6/30)
		assertEquals(5, vectorValueList.get(0).getValue());
		assertEquals(31, vectorValueList.get(1).getValue());		
		
	}	
	
	/**
	 * Tests the reference vector calculation using a ninth set of test data
	 */
	@Test
	public void ninthTestData() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);

		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(cf1);
		when(dao.findByLinkId(id2)).thenReturn(cf2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);

		
		ea.updateXZVectors(createXVector(14, 22, id1, id2, 2), createZVectorList(3, 4, id1, id2, 2));

		LocalVector referenceVector = ea.calculateReferenceVector(2);
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		
		assertEquals(18, vectorValueList.get(0).getValue());
		assertEquals(18, vectorValueList.get(1).getValue());		
		
	}
	
	/**
	 * Tests the reference vector calculation using a tenth set of test data
	 */
	@Test
	public void tenthTestData() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);

		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(cf1);
		when(dao.findByLinkId(id2)).thenReturn(cf2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);

		
		ea.updateXZVectors(createXVector(14, 22, id1, id2, 2), createZVectorList(3, 7, id1, id2, 2));

		LocalVector referenceVector = ea.calculateReferenceVector(2);
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		
		assertEquals(20, vectorValueList.get(0).getValue());
		assertEquals(16, vectorValueList.get(1).getValue());		
		
	}
	
	/**
	 * Tests the reference vector calculation using a eleventh set of test data
	 */
	@Test
	public void eleventhTestData() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);

		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(cf1);
		when(dao.findByLinkId(id2)).thenReturn(cf2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);

		
		ea.updateXZVectors(createXVector(14, 22, id1, id2, 2), createZVectorList(5, 7, id1, id2, 2));

		LocalVector referenceVector = ea.calculateReferenceVector(2);
		List<LocalVectorValue> vectorValueList = referenceVector.getVectorValues();
		
		assertEquals(20, vectorValueList.get(0).getValue());
		assertEquals(16, vectorValueList.get(1).getValue());		
		
	}
	
	/**
	 * Tests whether the number of the XVector and the RVector match
	 */
	@Test
	public void asNumberMatch() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);

		SimpleLinkID id1 = new SimpleLinkID("1", null);
		SimpleLinkID id2 = new SimpleLinkID("2", null);
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(cf1);
		when(dao.findByLinkId(id2)).thenReturn(cf2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);

		XVector xvector = createXVector(14, 22, id1, id2, 2);
		ea.updateXZVectors(xvector, createZVectorList(3,7, id1, id2, 2));

		LocalVector referenceVector = ea.calculateReferenceVector(2);
		assertEquals(xvector.getSourceAsNumber(), referenceVector.getSourceAsNumber());
		
	}

	/**
	 * Tests whether the number of the XVector and the RVector match
	 */
	@Test
	public void linkIDConsistence() {
		TimeScheduleParametersDAO tspdao = mock(TimeScheduleParametersDAO.class);
		TimeScheduleParameters tsp = new TimeScheduleParameters(null, 300, 30);
		tsp.setTol1(1);
		tsp.setTol2(1);
		when(tspdao.findLast()).thenReturn(tsp);
		
		
		DAOFactory.setTimeScheduleParametersDAO(tspdao);

		SimpleLinkID id1 = new SimpleLinkID("1", "ISP A");
		SimpleLinkID id2 = new SimpleLinkID("2", "ISP B");
		DTMTrafficManager dtm = mock(DTMTrafficManager.class);
		
		CostFunctionDAO dao = mock(CostFunctionDAO.class);
		when(dao.findByLinkId(id1)).thenReturn(cf1);
		when(dao.findByLinkId(id2)).thenReturn(cf2);
		
		DAOFactory.setCostFunctionDAOInstance(dao);
		
		EconomicAnalyzerInternal ea = new EconomicAnalyzerInternal(dtm, id1, id2);

		XVector xvector = createXVector(14, 22, id1, id2, 2);
		ea.updateXZVectors(xvector, createZVectorList(3,7, id1, id2, 2));

		LocalVector referenceVector = ea.calculateReferenceVector(2);
		assertEquals(id1, referenceVector.getVectorValues().get(0).getLinkID());
		assertEquals(id2, referenceVector.getVectorValues().get(1).getLinkID());
		
	}
	
}
