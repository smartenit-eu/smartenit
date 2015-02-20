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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.linear.LinearConstraint;
import org.apache.commons.math3.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optimization.linear.NoFeasibleSolutionException;
import org.apache.commons.math3.optimization.linear.Relationship;
import org.apache.commons.math3.optimization.linear.SimplexSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CostFunction;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;

/**
 * Class that implements the reference calculation algorithm.
 * 
 * @author Lukasz Lopatowski
 * @version 3.0
 * 
 */
public class ReferenceVectorCalculator {

	private static final Logger logger = LoggerFactory.getLogger(ReferenceVectorCalculator.class);

	/**
	 * Index for axis x1
	 */	
	private final int x1 = EconomicAnalyzerInternal.x1;
	
	/**
	 * Index for axis x2
	 */
	private final int x2 = EconomicAnalyzerInternal.x2;
	
	/**
	 * Contains the alpha values for x1 and x2
	 */
	private final List<List<Long>> alpha = new ArrayList<List<Long>>();
	
	/**
	 * Contains alphas for x1 axis
	 */
	private final List<Long> alpha1;

	/**
	 * Contains alphas for x2 axis
	 */
	private final List<Long> alpha2;
	
	/**
	 * Contains the Dij intervals
	 */
	private final List<List<DInterval>> D = new ArrayList<List<DInterval>>();

	/**
	 * Splits the Dij intervals into x1
	 */
	private final List<DInterval> D1;
	
	/**
	 * Splits the Dij intervals into x2
	 */
	private final List<DInterval> D2;
	
	/**
	 * Maps the alpha1 onto D1j intervals (lower left border to obtain the interval)
	 */
	private final Map<Long, List<DInterval>> D1Map = new HashMap<Long, List<DInterval>>();
	
	/**
	 * Maps the alpha2 onto D2j intervals (lower left border to obtain the interval)
	 */
	private final Map<Long, List<DInterval>> D2Map = new HashMap<Long, List<DInterval>>();

	/**
	 * Contains the cost functions
	 */
	private List<CostFunction> costFunctions;
	
	/**
	 * Maps the alpha1 onto the corresponding cost function segment (lower left border to obtain the cf segments)
	 */
	private final Map<Long, Segment> costFunctionMap1 = new HashMap<Long, Segment>();
	
	/**
	 * Maps the alpha2 onto the corresponding cost function segment (lower left border to obtain the cf segments)
	 */
	private final Map<Long, Segment> costFunctionMap2 = new HashMap<Long, Segment>();
	
	/**
	 * TODO Replace with actual values
	 * 
	 * Parameter which allows to influence on reference vector prediction by decreasing or increasing values for Z_V[x1]
	 */
	private final double tol1;
	
	/**
	 * TODO Replace with actual values
	 * 
	 * Parameter which allows to influence on reference vector prediction by decreasing or increasing values for Z_V[x2]
	 */
	private final double tol2;	
	
	/**
	 * Total volume traffic vector
	 */
	private long[] X_V;
	
	/**
	 * Contains the link IDs for links 1
	 */
	private final SimpleLinkID link1;
	
	/**
	 * Contains the link IDs for links 2
	 */
	private final SimpleLinkID link2;

	/**
	 * If true, ALL areas will be run against the cost functions without simplification
	 */
	private static final boolean SIMPLE_CALCULATION = false;
	
	public ReferenceVectorCalculator(SimpleLinkID link1, SimpleLinkID link2, 
			List<CostFunction> costFunctions, TimeScheduleParameters timeScheduleParameters) {
		this.link1 = link1;
		this.link2 = link2;
		this.costFunctions = costFunctions;
		
		//initialize
		initializeAlpha();
		alpha1 = alpha.get(x1);
		alpha2 = alpha.get(x2);
		
		initializeD();
		D1 = D.get(x1);
		D2 = D.get(x2);

		initializeD1Map();
		initializeD2Map();
		
		assert costFunctions.size() == 2;
		initializeCostFunctionMap1();
		initializeCostFunctionMap2();
		
		tol1 = timeScheduleParameters.getTol1();
		tol2 = timeScheduleParameters.getTol2();
	}
	
	/**
	 * Calculates the reference vector.
	 * 
	 * @param sourceAsNumber The number of the source AS
	 * 
	 * @return Returns an RVector containing the reference vector values.
	 */
	LocalRVector calculate(long[] X_V, long[] Z_V, int sourceAsNumber) {
		this.X_V = X_V;
		
		logger.info("Calculating S: ");
		long S = Z_V[x1] + Z_V[x2];
		logger.info("S: " + S);

		List<Area> areaList;
		if(SIMPLE_CALCULATION) {
			areaList = calculateAreaSimple(S);
			logger.info("areaList: " + areaList);
		} else {
			areaList = calculateAreaAdvanced(S);
			logger.info("areaList: " + areaList);
		}
		
		double[] Z_V_comma = {Z_V[x1] * tol1, Z_V[x2] * tol2};
		double[] S1 = {-Z_V_comma[x1], Z_V_comma[x1]};
		double[] S2 = {Z_V_comma[x2], -Z_V_comma[x2]};
		
		return calculateReferenceVector(S1, S2, areaList, sourceAsNumber);
	}
	
	/**
	 * Creates a list of areas containing ALL areas (no optimization)
	 * 
	 * @param S The DC manipulation freedom
	 * @return A list containing ALL areas (no optimization)
	 */
	private List<Area> calculateAreaSimple(long S) {
		List<Area> A = new ArrayList<Area>();
		for(int i = 0; i < D1.size(); i++) {
			for(int j = 0; j < D2.size(); j++) {
				A.add(new Area(D1.get(i), D2.get(j)));				
			}			
		}
		return A;
	}
	
	/**
	 * Creates a list of candidate areas that may contain the optimal reference vector using the specified algorithm.
	 * 
	 * @param S The DC manipulation freedom
	 * @return A list of candidate areas to be used for optimization.
	 */
	private List<Area> calculateAreaAdvanced(long S) {
		List<Area> areaList = new ArrayList<Area>();
		//List<List<Long>> E = calculateSetE(S, areaList);

		logger.info("Calculating set E ...");
		
		List<List<Long>> E = new ArrayList<List<Long>>();
		E.add(new ArrayList<Long>());
		E.add(new ArrayList<Long>());

		//Counts how many null values are in E1 and E2
		int nullCounter1 = 0;
		int nullCounter2 = 0;

		//Calculates set E1
		for (int i = 0; i < alpha1.size(); i++) {
			
			logger.info("Checking: " + alpha1.get(i) + ">=" + (X_V[x1] - S) + " && " + alpha1.get(i) +  "<=" + (X_V[x1] + S));
			
			if ((alpha1.get(i) >= X_V[x1] - S) && (alpha1.get(i) <= X_V[x1] + S)) {
				E.get(x1).add(alpha1.get(i));
				
				logger.info("Adding " + alpha1.get(i));
			} else {
				E.get(x1).add(null);
				nullCounter1++;
				logger.info("Adding " + null);
			}
		}

		//Calculates set E2
		for (int i = 0; i < alpha2.size(); i++) {
			
			logger.info("Checking: " + alpha2.get(i) + ">=" + (X_V[x2] - S) + " && " + alpha2.get(i) +  "<=" + (X_V[x2] + S));
			
			if ((alpha2.get(i) >= X_V[x2] - S) && (alpha2.get(i) <= X_V[x2] + S)) {
				E.get(x2).add(alpha2.get(i));
				
				logger.info("Adding " + alpha2.get(i));
			} else {
				E.get(x2).add(null);
				nullCounter2++;
				logger.info("Adding " + null);
			}
		}
		logger.info("Set E: " + E);
		
		//Deals with the special case where both E1 and E2 contain only null values (i.e. there is no intersection with any alpha)
		if(nullCounter1 == E.get(x1).size() && nullCounter2 == E.get(x2).size()) {
			DInterval dx1 = null;
			DInterval dx2 = null;
			
			for (int j = 0; j < D1.size(); j++) {
				if (D1.get(j).contains(X_V[x1])) {
					dx1 = D1.get(j);
				}
			}
			for (int j = 0; j < D2.size(); j++) {
				if (D2.get(j).contains(X_V[x2])) {
					dx2 = D2.get(j);
				}
			}
			areaList.add(new Area(dx1, dx2));
		} 
		
		//The case where either E1 or E2 contains an alpha value (i.e. there are intersections with at least one of the alphas)
		else {
			
			//Create D1k
			List<DInterval> D1k = calculateD1k(E, S);
					
			//Create D2k
			List<DInterval> D2k = calculateD2k(E, S);		
			
			for(int i = 0; i < E.get(x1).size(); i++) {
				List<Long> E1 = E.get(x1);
				if (E1.get(i) != null) {
					for(int j = 0; j < D2k.size(); j++) {
						List<DInterval> intervalList = D1Map.get(E1.get(i));
						for(int k = 0; k < intervalList.size(); k++) {
							Area area = new Area(intervalList.get(k), D2k.get(j));
							if(!areaList.contains(area)) {
								areaList.add(area);
							}
						}
					}
				}
			}
			
			for(int i = 0; i < E.get(x2).size(); i++) {
				List<Long> E2 = E.get(x2);
				if (E2.get(i) != null) {
					for(int j = 0; j < D1k.size(); j++) {
						List<DInterval> intervalList = D2Map.get(E2.get(i));
						for(int k = 0; k < intervalList.size(); k++) {
							Area area = new Area(D1k.get(j), intervalList.get(k));
							if(!areaList.contains(area)) {
								areaList.add(area);
							}							
						}
						
					}
				}
			}			
			
		}
		logger.info("Area list size: " + areaList.size());
		
		return areaList;
	}
	
	/**
	 * Calculates the reference vector using simplex method.
	 * 
	 * @param S1 The DC traffic manipulation freedom 
	 * @param S2 The DC traffic manipulation freedom 
	 * @param aList The list of candidate areas
	 * @param sourceAsNumber The number of the source AS
	 * @return Returns an RVector containing the reference vector values.
	 */
	private LocalRVector calculateReferenceVector(double[] S1, double[] S2, List<Area> aList, int sourceAsNumber) {
		List<PointValuePair> solutionList = new ArrayList<PointValuePair>();
		for(Area a : aList) {
			
			Long x1Lower = a.getD1().getLowerBound(); // Corresponds to alpha1i
			Long x1Upper = a.getD1().getUpperBound(); // Corresponds to alpha1i+1
			Long x2Lower = a.getD2().getLowerBound(); // Corresponds to alpha2i
			Long x2Upper = a.getD2().getUpperBound(); // Corresponds to alpha2i+1
			
			Segment segment1 = costFunctionMap1.get(x1Lower); // Corresponds to the suitable cf segment for alpha1
			Segment segment2 = costFunctionMap2.get(x2Lower); // Corresponds to the suitable cf segment for alpha2
			
			// Function    f:  f1(x1) + f2(x2) + c //c corresponds to the sum of the constant part of f1 and f2
			// Constraints c1: x1       >  alpha1i
			//             c2: x1       <= alpha1i+1 
			//             c3:      x2  >  alpha2j
			//             c4:      x2  <= alpha2j+1
			//             c5: x1 + x2  == X_V[x1] + X_V[x2]
			//			   c6: x1       >= X_V[x1] + S1[x1]
			//             c7: x1       <= X_V[x1] + S2[x1]
			//             c8: x2       >= X_V[x2] + S2[x2]
			//	           c9: x2       <= X_V[x2] + S1[x2]
			
			LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {segment1.getB(), segment2.getB()}, (segment1.getA() + segment2.getA())); //f1(x1) + f2(x2)
			Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
			constraints.add(new LinearConstraint(new double[]{1, 0}, Relationship.GEQ, x1Lower.doubleValue()));
			constraints.add(new LinearConstraint(new double[]{1, 0}, Relationship.LEQ, x1Upper.doubleValue()));
			constraints.add(new LinearConstraint(new double[]{0, 1}, Relationship.GEQ, x2Lower.doubleValue()));
			constraints.add(new LinearConstraint(new double[]{0, 1}, Relationship.LEQ, x2Upper.doubleValue()));
			constraints.add(new LinearConstraint(new double[]{1, 1}, Relationship.EQ, X_V[x1] + X_V[x2]));
			constraints.add(new LinearConstraint(new double[]{1, 0}, Relationship.GEQ, X_V[x1] + S1[x1]));
			constraints.add(new LinearConstraint(new double[]{1, 0}, Relationship.LEQ, X_V[x1] + S2[x1]));
			constraints.add(new LinearConstraint(new double[]{0, 1}, Relationship.GEQ, X_V[x2] + S2[x2]));
			constraints.add(new LinearConstraint(new double[]{0, 1}, Relationship.LEQ, X_V[x2] + S1[x2]));
			
			try {
				PointValuePair  solution = new SimplexSolver().optimize(f, constraints, GoalType.MINIMIZE, true);
				solutionList.add(solution);
				
				double x = solution.getPoint()[0];
				double y = solution.getPoint()[1];
				double value = solution.getValue();
				logger.info("\n");
				logger.info("Area [" + x1Lower + ", " + x1Upper + ", " + x2Lower + ", " + x2Upper + "]");
				logger.info("Cost function: " + segment1.getB() + "x1 " + segment2.getB() + "x2 " + " + " + (segment1.getA() + segment2.getA()));
				logger.info("Reference vector x1=" + x + " x2=" + y + " f1(x1) + f2(x2)=" + value);
			} catch (NoFeasibleSolutionException e) {
				logger.info("\n");
				logger.info("Area [" + x1Lower + ", " + x1Upper + ", " + x2Lower + ", " + x2Upper + "]");
				logger.info("Cost function: " + segment1.getB() + "x1 " + segment2.getB() + "x2 " + " + " + (segment1.getA() + segment2.getA()));
				logger.error("No feasible solution found: for area " + a);
			}

		}
		
		PointValuePair minimalSolution = getMinimalSolution(solutionList);
		LocalRVector referenceVector = constructReferenceVector(minimalSolution, sourceAsNumber);
		
		logger.info("Chosen reference vector: " 
				+ referenceVector.getVectorValues().get(0).getValue()
				+ " "
				+ referenceVector.getVectorValues().get(1).getValue()
				);
		
		return referenceVector;
	}
	
	/**
	 * Creates the RVector object from the optimal solution.
	 * 
	 * @param minimalSolution The optimal solution.
	 * @param sourceAsNumber The number of the source AS
	 * @return The RVector object of the optimal solution.
	 */
	private LocalRVector constructReferenceVector(PointValuePair minimalSolution, int sourceAsNumber) {
		LocalVectorValue vvx1 = new LocalVectorValue(Math.round(minimalSolution.getPoint()[0]), link1);
		LocalVectorValue vvx2 = new LocalVectorValue(Math.round(minimalSolution.getPoint()[1]), link2);
		List<LocalVectorValue> referenceVectorList = new ArrayList<LocalVectorValue>();
		referenceVectorList.add(vvx1);
		referenceVectorList.add(vvx2);
		LocalRVector referenceVector = new LocalRVector(referenceVectorList, sourceAsNumber);
		
		return referenceVector;
	}
	
	/**
	 * Selects the point value pair having the minimum target function value among all the candidate solutions.
	 * 
	 * @param solutionList A list of candidate solutions 
	 * @return The point value pair having the minimum target function value among all the candidate solutions.
	 */
	private PointValuePair getMinimalSolution(List<PointValuePair> solutionList) {
		assert !solutionList.isEmpty();
		PointValuePair minimalSolution = solutionList.get(0);

		for(int i = 0; i < solutionList.size(); i++) {
			if(solutionList.get(i).getValue() < minimalSolution.getValue()) {
				minimalSolution = solutionList.get(i);
			}
		}
		return minimalSolution;
	}

	/**
	 * Calculates the D1k corresponding to each of the alpha2s in E2
	 * 
	 * @param E The set E
	 * @param S The DC traffic manipulation freedom
	 * @return A list of matching intervals (D1k)
	 */
	private List<DInterval> calculateD1k(List<List<Long>> E, long S) {
		logger.info("Creating D1k ...");
		List<DInterval> D1k = new ArrayList<DInterval>();
		for (int i = 0; i < alpha2.size(); i++) {
			if (E.get(x2).get(i) != null) {

				// S = X_V[x1] + X_V[x2]
				logger.info("alpha2: " + alpha2.get(i));
				long result = X_V[x1] + X_V[x2] - alpha2.get(i);
				logger.info("x1 = " + result);
				
				for (int j = 0; j < D1.size(); j++) {
					if (D1.get(j).contains(result)) {
						if(!D1k.contains(D1.get(j))) {
							D1k.add(D1.get(j)); // Only if it is not contained yet? TODO
						}
					}
				}
			}
		}
		logger.info("D1k: " + D1k);
		return D1k;
	}
	
	/**
	 * Calculates the D2k corresponding to each of the alpha1s in E1
	 * 
	 * @param E The set E
	 * @param S The DC traffic manipulation freedom
	 * @return A list of matching intervals (D2k)
	 */
	private List<DInterval> calculateD2k(List<List<Long>> E, long S) {
		logger.info("Creating D2k ...");
		List<DInterval> D2k = new ArrayList<DInterval>();
		for (int i = 0; i < alpha1.size(); i++) {
			if (E.get(x1).get(i) != null) {

				// S = X_V[x1] + X_V[x2]
				long result = X_V[x1] + X_V[x2] - alpha1.get(i);
				logger.info("x2 = " + result);
				
				for (int j = 0; j < D2.size(); j++) {
					if (D2.get(j).contains(result)) {
						if(!D2k.contains(D2.get(j))) {
							D2k.add(D2.get(j));
						}
					}
				}
			}
		}
		logger.info("D2k: " + D2k);
		return D2k;		
	}
	
	/**
	 * Maps the lower border of the segment to corresponding cost function segment
	 */
	private void initializeCostFunctionMap1() {
		// Cost Function 1
		logger.info("Initializing cost function map 1 ...");
		for (Segment segment : costFunctions.get(0).getSegments()) {
			costFunctionMap1.put(segment.getLeftBorder(), segment);
		}
		logger.info("Cost function map 1 initialized to " + costFunctionMap1);		
	}
	
	/**
	 * Maps the lower border of the segment to corresponding cost function segment
	 */
	private void initializeCostFunctionMap2() {
		// Cost Function 2
		logger.info("Initializing cost function map 2 ...");
		for (Segment segment : costFunctions.get(1).getSegments()) {
			costFunctionMap2.put(segment.getLeftBorder(), segment);
		}
		logger.info("Cost function map 2 initialized to " + costFunctionMap2);		
	}
	
	/**
	 * Maps the alpha1 onto D1j intervals (lower left border to obtain the interval)
	 */
	private void initializeD1Map() {
		logger.info("Initializing map D1 ... ");
		for(int i = 0; i < alpha1.size(); i++) {
			if(i == 0) {
				List<DInterval> intervalList = new ArrayList<DInterval>();
				intervalList.add(D1.get(i));				
				D1Map.put(alpha1.get(i), intervalList);
			} else if (i == alpha1.size() - 1) {
				List<DInterval> intervalList = new ArrayList<DInterval>();
				intervalList.add(D1.get(i - 1));
				D1Map.put(alpha1.get(i), intervalList);
			} else {
				List<DInterval> intervalList = new ArrayList<DInterval>();
				intervalList.add(D1.get(i - 1));
				intervalList.add(D1.get(i));				
				D1Map.put(alpha1.get(i), intervalList);				
			}			
		}
		logger.info("Map D1 initialized: " + D1Map);		
	}
	
	/**
	 * Maps the alpha2 onto D2j intervals (lower left border to obtain the interval)
	 */
	private void initializeD2Map() {
		logger.info("Initializing map D2 ... ");
		for(int i = 0; i < alpha2.size(); i++) {
			if(i == 0) {
				List<DInterval> intervalList = new ArrayList<DInterval>();
				intervalList.add(D2.get(i));				
				D2Map.put(alpha2.get(i), intervalList);
			} else if (i == alpha2.size() - 1) {
				List<DInterval> intervalList = new ArrayList<DInterval>();
				intervalList.add(D2.get(i - 1));
				D2Map.put(alpha2.get(i), intervalList);
			} else {
				List<DInterval> intervalList = new ArrayList<DInterval>();
				intervalList.add(D2.get(i - 1));
				intervalList.add(D2.get(i));				
				D2Map.put(alpha2.get(i), intervalList);				
			}			
		}
		logger.info("Map D2 initialized: " + D2Map);	
	}
	
	/**
	 * Creates the D intervals for each of the alphas.
	 */	
	private void initializeD() {		
		for(int i = 0; i < alpha.size(); i++) {
			D.add(new ArrayList<DInterval>());
			List<Long> alphai = alpha.get(i);
			for(int j = 0; j < alphai.size() - 1; j++) {
				D.get(i).add(new DInterval(alphai.get(j), alphai.get(j + 1)));
			}
			logger.info("D" + (i + 1) + " initialized to " + D.get(i));
		}		
	}
	
	/**
	 * Creates the alphas from the cost function segments
	 */
	private void initializeAlpha() {

		for(int i = 0; i < costFunctions.size(); i++) {
			alpha.add(new ArrayList<Long>());
			CostFunction cf = costFunctions.get(i);
			List<Segment> segmentList = cf.getSegments();
			for (int j = 0; j < segmentList.size(); j++) {
				Segment segment = segmentList.get(j);
				alpha.get(i).add(segment.getLeftBorder());
				if(j == segmentList.size() - 1) {
					assert segment.getRightBorder() < 0;
					alpha.get(i).add(Long.MAX_VALUE);
				} 
			}
			logger.info("Alpha" + (i + 1) + " initialized to " + alpha.get(i));
		}	
	}

	
}
