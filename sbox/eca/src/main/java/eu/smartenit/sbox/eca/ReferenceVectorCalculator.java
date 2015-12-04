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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CostFunction;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;

/**
 * Class that implements the reference vector calculation algorithm.
 *
 * @author D. D&ouml;nni, K. Bhargav, T. Bocek
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
    private final List<List<Long>> alpha = new ArrayList<>();

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
    private final List<List<DInterval>> D = new ArrayList<>();

    /**
     * Splits the Dij intervals into x1
     */
    private final List<DInterval> D1;

    /**
     * Splits the Dij intervals into x2
     */
    private final List<DInterval> D2;

    /**
     * Maps the alpha1 onto D1j intervals (lower left border to obtain the
     * interval)
     */
    private final Map<Long, List<DInterval>> D1Map = new HashMap<>();

    /**
     * Maps the alpha2 onto D2j intervals (lower left border to obtain the
     * interval)
     */
    private final Map<Long, List<DInterval>> D2Map = new HashMap<>();

    /**
     * Contains the cost functions
     */
    private final List<CostFunction> costFunctions;

    /**
     * Maps the alpha1 onto the corresponding cost function segment (lower left
     * border to obtain the cf segments)
     */
    private final Map<Long, Segment> costFunctionMap1 = new HashMap<>();

    /**
     * Maps the alpha2 onto the corresponding cost function segment (lower left
     * border to obtain the cf segments)
     */
    private final Map<Long, Segment> costFunctionMap2 = new HashMap<>();

    /**
     * Parameter which allows to influence on reference vector prediction by
     * decreasing or increasing values for Z_V[x1]
     */
    private final double tol1;

    /**
     * Parameter which allows to influence on reference vector prediction by
     * decreasing or increasing values for Z_V[x2]
     */
    private final double tol2;

    /**
     * Total volume traffic vector
     */
    private long[] X_V;

    /**
     * Contains the identifier of the first link
     */
    private final SimpleLinkID link1;

    /**
     * Contains the identifier for the second link
     */
    private final SimpleLinkID link2;

    /**
     * If true, ALL areas will be run against the cost functions without
     * simplification
     */
    private static final boolean SIMPLE_CALCULATION = false;

    /**
     * The constructor with arguments.
     *
     * @param link1 identifier of the first link
     * @param link2 identified of the second link
     * @param costFunctions cost functions used for given links
     * @param timeScheduleParameters additional control parameters
     */
    public ReferenceVectorCalculator(SimpleLinkID link1, SimpleLinkID link2,
            List<CostFunction> costFunctions, TimeScheduleParameters timeScheduleParameters) {
        this.link1 = link1;
        this.link2 = link2;
        this.costFunctions = costFunctions;

        //initialize
        logger.info("Initializing reference vector calculator structures.");
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
     * Calculates the reference vector for an AS.
     *
     * @param X_V aggregated link traffic value
     * @param Z_V aggregated tunnel traffic value
     * @param sourceAsNumber number of the AS with given links
     * @return calculated reference vector
     */
    LocalRVector calculate(long[] X_V, long[] Z_V, int sourceAsNumber) {
        logger.info("Calculating new reference vector for AS {}", sourceAsNumber);
        this.X_V = X_V;

        logger.debug("Calculating S: ");
        long S = Z_V[x1] + Z_V[x2];
        logger.debug("S: " + S);

        List<Area> areaList;
        if (SIMPLE_CALCULATION) {
            areaList = calculateAreaSimple(S);
            logger.debug("areaList: " + areaList);
        } else {
            areaList = calculateAreaAdvanced(S);
            logger.debug("areaList: " + areaList);
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
        List<Area> A = new ArrayList<>();
        for (DInterval D11 : D1) {
            for (DInterval D21 : D2) {
                A.add(new Area(D11, D21));
            }
        }
        return A;
    }

    /**
     * Creates a list of candidate areas that may contain the optimal reference
     * vector using the specified algorithm.
     *
     * @param S The DC manipulation freedom
     * @return A list of candidate areas to be used for optimization.
     */
    private List<Area> calculateAreaAdvanced(long S) {
        List<Area> areaList = new ArrayList<>();
        //List<List<Long>> E = calculateSetE(S, areaList);

        logger.debug("Calculating set E ...");

        List<List<Long>> E = new ArrayList<>();
        E.add(new ArrayList<Long>());
        E.add(new ArrayList<Long>());

        //Counts how many null values are in E1 and E2
        int nullCounter1 = 0;
        int nullCounter2 = 0;

        //Calculates set E1
        for (Long alpha11 : alpha1) {
            logger.debug("Checking: " + alpha11 + ">=" + (X_V[x1] - S) + " && " + alpha11 + "<=" + (X_V[x1] + S));
            if ((alpha11 >= X_V[x1] - S) && (alpha11 <= X_V[x1] + S)) {
                E.get(x1).add(alpha11);
                logger.debug("Adding " + alpha11);
            } else {
                E.get(x1).add(null);
                nullCounter1++;
                logger.debug("Adding null");
            }
        }

        //Calculates set E2
        for (Long alpha21 : alpha2) {
            logger.debug("Checking: " + alpha21 + ">=" + (X_V[x2] - S) + " && " + alpha21 + "<=" + (X_V[x2] + S));
            if ((alpha21 >= X_V[x2] - S) && (alpha21 <= X_V[x2] + S)) {
                E.get(x2).add(alpha21);
                logger.debug("Adding " + alpha21);
            } else {
                E.get(x2).add(null);
                nullCounter2++;
                logger.debug("Adding null");
            }
        }
        logger.debug("Set E: " + E);

        //Deals with the special case where both E1 and E2 contain only null values (i.e. there is no intersection with any alpha)
        if (nullCounter1 == E.get(x1).size() && nullCounter2 == E.get(x2).size()) {
            DInterval dx1 = null;
            DInterval dx2 = null;

            for (DInterval D11 : D1) {
                if (D11.contains(X_V[x1])) {
                    dx1 = D11;
                }
            }
            for (DInterval D21 : D2) {
                if (D21.contains(X_V[x2])) {
                    dx2 = D21;
                }
            }
            areaList.add(new Area(dx1, dx2));
        } //The case where either E1 or E2 contains an alpha value (i.e. there are intersections with at least one of the alphas)
        else {

            //Create D1k
            List<DInterval> D1k = calculateD1k(E, S);

            //Create D2k
            List<DInterval> D2k = calculateD2k(E, S);

            for (int i = 0; i < E.get(x1).size(); i++) {
                List<Long> E1 = E.get(x1);
                if (E1.get(i) != null) {
                    for (DInterval D2k1 : D2k) {
                        List<DInterval> intervalList = D1Map.get(E1.get(i));
                        for (DInterval intervalList1 : intervalList) {
                            Area area = new Area(intervalList1, D2k1);
                            if (!areaList.contains(area)) {
                                areaList.add(area);
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < E.get(x2).size(); i++) {
                List<Long> E2 = E.get(x2);
                if (E2.get(i) != null) {
                    for (DInterval D1k1 : D1k) {
                        List<DInterval> intervalList = D2Map.get(E2.get(i));
                        for (DInterval intervalList1 : intervalList) {
                            Area area = new Area(D1k1, intervalList1);
                            if (!areaList.contains(area)) {
                                areaList.add(area);
                            }
                        }
                    }
                }
            }

        }
        logger.debug("Area list size: " + areaList.size());

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
        double[] optimum = null;
        double optimumCost = Double.POSITIVE_INFINITY;

        for (Area a : aList) {

            Long x1Lower = a.getD1().getLowerBound(); // Corresponds to alpha1i
            Long x1Upper = a.getD1().getUpperBound(); // Corresponds to alpha1i+1
            Long x2Lower = a.getD2().getLowerBound(); // Corresponds to alpha2i
            Long x2Upper = a.getD2().getUpperBound(); // Corresponds to alpha2i+1

            Segment segment1 = costFunctionMap1.get(x1Lower); // Corresponds to the suitable cf segment for alpha1
            Segment segment2 = costFunctionMap2.get(x2Lower); // Corresponds to the suitable cf segment for alpha2

            double[] end1 = new double[]{X_V[x1] + S2[x1], X_V[x2] + S2[x2]}; // all managed traffic @ link 1
            double[] end2 = new double[]{X_V[x1] + S1[x1], X_V[x2] + S1[x2]}; // all managed traffic @ link 2

            boolean outside = true;
            if (end1[x1] < x1Lower || end2[x1] > x1Upper || end1[x2] > x2Upper || end2[x2] < x2Lower) {
                // outside = true
            } else if (end1[x1] <= x1Upper && end2[x1] >= x1Lower && end1[x2] >= x2Lower && end2[x2] <= x2Upper) {
                logger.info("Both ends in area {}", a);
                outside = false;
            } else {
                double ix1L = (end1[x1] - x1Lower) + end1[x2]; // x2 of intersection of line containing end1-end2 segment with x1 = x1Lower
                double ix1U = (end1[x1] - x1Upper) + end1[x2]; // x2 of intersection of line containing end1-end2 segment with x1 = x1Upper
                double ix2L = (end1[x2] - x2Lower) + end1[x1]; // x1 of intersection of line containing end1-end2 segment with x2 = x2Lower
                double ix2U = (end1[x2] - x2Upper) + end1[x1]; // x1 of intersection of line containing end1-end2 segment with x2 = x2Upper

                // the segment is not outside if it crosses at least one boundary with another area 
                if (ix1L >= x2Lower && ix1L <= x2Upper && end2[x1] < x1Lower) { // end1-end2 segment intersects with x1Lower
                    end2 = new double[]{x1Lower, ix1L};
                    outside = false;
                } else if (ix2U >= x1Lower && ix2U <= x1Upper && end2[x2] > x2Upper) { // end1-end2 segment intersects with x2Upper
                    end2 = new double[]{ix2U, x2Upper};
                    outside = false;
                }
                if (ix1U >= x2Lower && ix1U <= x2Upper && end1[x1] > x1Upper) { // end1-end2 segment intersects with x1Upper
                    end1 = new double[]{x1Upper, ix1U};
                    outside = false;
                } else if (ix2L >= x1Lower && ix2L <= x1Upper && end1[x2] < x2Lower) { // end1-end2 segment intersects with x2Lower
                    end1 = new double[]{ix2L, x2Lower};
                    outside = false;
                }
            }
            if (outside) {
                logger.info("No solution in area {}", a);
                continue;
            }

            logger.info("In area {}:", a);
            logger.info("   Cost function: {} * x1 + {} * x2 + {}", segment1.getB(), segment2.getB(), segment1.getA() + segment2.getA());
            double cost1 = segment1.getB() * end1[x1] + segment2.getB() * end1[x2] + segment1.getA() + segment2.getA();
            double cost2 = segment1.getB() * end2[x1] + segment2.getB() * end2[x2] + segment1.getA() + segment2.getA();
            logger.info("   Cost for [{}, {}]: {}", end1[x1], end1[x2], cost1);
            logger.info("   Cost for [{}, {}]: {}", end2[x1], end2[x2], cost2);
            if (cost1 <= cost2 && cost1 < optimumCost) {
                logger.info("   Found new minimum {} for [{}, {}]", cost1, end1[x1], end1[x2]);
                optimum = end1;
                optimumCost = cost1;
            } else if (cost2 < cost1 && cost2 < optimumCost) {
                logger.info("   Found new minimum {} for [{}, {}]", cost2, end2[x1], end2[x2]);
                optimum = end2;
                optimumCost = cost2;
            }
        }

        LocalRVector referenceVector = constructReferenceVector(optimum, sourceAsNumber);

        logger.info("Chosen reference vector: "
                + referenceVector.getVectorValues().get(0).getValue()
                + " "
                + referenceVector.getVectorValues().get(1).getValue()
        );
        logger.info("VECTOR-VALUES-R:("
                + ((SimpleLinkID) referenceVector.getVectorValues().get(0).getLinkID()).getLocalLinkID() + ":"
                + referenceVector.getVectorValues().get(0).getValue() + ")|("
                + ((SimpleLinkID) referenceVector.getVectorValues().get(1).getLinkID()).getLocalLinkID() + ":"
                + referenceVector.getVectorValues().get(1).getValue() + ")");

        return referenceVector;
    }

    /**
     * Creates the RVector object from the optimal solution.
     *
     * @param minimalSolution The optimal solution.
     * @param sourceAsNumber The number of the source AS
     * @return The RVector object of the optimal solution.
     */
    private LocalRVector constructReferenceVector(double[] minimalSolution, int sourceAsNumber) {
        LocalVectorValue vvx1 = new LocalVectorValue(Math.round(minimalSolution[x1]), link1);
        LocalVectorValue vvx2 = new LocalVectorValue(Math.round(minimalSolution[x2]), link2);
        List<LocalVectorValue> referenceVectorList = new ArrayList<>();
        referenceVectorList.add(vvx1);
        referenceVectorList.add(vvx2);
        LocalRVector referenceVector = new LocalRVector(referenceVectorList, sourceAsNumber);

        return referenceVector;
    }

    /**
     * Calculates the D1k corresponding to each of the alpha2s in E2
     *
     * @param E The set E
     * @param S The DC traffic manipulation freedom
     * @return A list of matching intervals (D1k)
     */
    private List<DInterval> calculateD1k(List<List<Long>> E, long S) {
        logger.debug("Creating D1k ...");
        List<DInterval> D1k = new ArrayList<>();
        for (int i = 0; i < alpha2.size(); i++) {
            if (E.get(x2).get(i) != null) {

                // S = X_V[x1] + X_V[x2]
                logger.debug("alpha2: " + alpha2.get(i));
                long result = X_V[x1] + X_V[x2] - alpha2.get(i);
                logger.debug("x1 = " + result);

                for (DInterval D11 : D1) {
                    if (D11.contains(result)) {
                        if (!D1k.contains(D11)) {
                            D1k.add(D11); // Only if it is not contained yet? TODO
                        }
                    }
                }
            }
        }
        logger.debug("D1k: " + D1k);
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
        logger.debug("Creating D2k ...");
        List<DInterval> D2k = new ArrayList<>();
        for (int i = 0; i < alpha1.size(); i++) {
            if (E.get(x1).get(i) != null) {

                // S = X_V[x1] + X_V[x2]
                long result = X_V[x1] + X_V[x2] - alpha1.get(i);
                logger.debug("x2 = " + result);

                for (DInterval D21 : D2) {
                    if (D21.contains(result)) {
                        if (!D2k.contains(D21)) {
                            D2k.add(D21);
                        }
                    }
                }
            }
        }
        logger.debug("D2k: " + D2k);
        return D2k;
    }

    /**
     * Maps the lower border of the segment to corresponding cost function
     * segment
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
     * Maps the lower border of the segment to corresponding cost function
     * segment
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
     * Maps the alpha1 onto D1j intervals (lower left border to obtain the
     * interval)
     */
    private void initializeD1Map() {
        logger.info("Initializing map D1 ... ");
        for (int i = 0; i < alpha1.size(); i++) {
            if (i == 0) {
                List<DInterval> intervalList = new ArrayList<>();
                intervalList.add(D1.get(i));
                D1Map.put(alpha1.get(i), intervalList);
            } else if (i == alpha1.size() - 1) {
                List<DInterval> intervalList = new ArrayList<>();
                intervalList.add(D1.get(i - 1));
                D1Map.put(alpha1.get(i), intervalList);
            } else {
                List<DInterval> intervalList = new ArrayList<>();
                intervalList.add(D1.get(i - 1));
                intervalList.add(D1.get(i));
                D1Map.put(alpha1.get(i), intervalList);
            }
        }
        logger.info("Map D1 initialized: " + D1Map);
    }

    /**
     * Maps the alpha2 onto D2j intervals (lower left border to obtain the
     * interval)
     */
    private void initializeD2Map() {
        logger.info("Initializing map D2 ... ");
        for (int i = 0; i < alpha2.size(); i++) {
            if (i == 0) {
                List<DInterval> intervalList = new ArrayList<>();
                intervalList.add(D2.get(i));
                D2Map.put(alpha2.get(i), intervalList);
            } else if (i == alpha2.size() - 1) {
                List<DInterval> intervalList = new ArrayList<>();
                intervalList.add(D2.get(i - 1));
                D2Map.put(alpha2.get(i), intervalList);
            } else {
                List<DInterval> intervalList = new ArrayList<>();
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
        for (int i = 0; i < alpha.size(); i++) {
            D.add(new ArrayList<DInterval>());
            List<Long> alphai = alpha.get(i);
            for (int j = 0; j < alphai.size() - 1; j++) {
                D.get(i).add(new DInterval(alphai.get(j), alphai.get(j + 1)));
            }
            logger.info("D" + (i + 1) + " initialized to " + D.get(i));
        }
    }

    /**
     * Creates the alphas from the cost function segments
     */
    private void initializeAlpha() {

        for (int i = 0; i < costFunctions.size(); i++) {
            alpha.add(new ArrayList<Long>());
            CostFunction cf = costFunctions.get(i);
            List<Segment> segmentList = cf.getSegments();
            for (int j = 0; j < segmentList.size(); j++) {
                Segment segment = segmentList.get(j);
                alpha.get(i).add(segment.getLeftBorder());
                if (j == segmentList.size() - 1) {
                    assert segment.getRightBorder() < 0;
                    alpha.get(i).add(Long.MAX_VALUE);
                }
            }
            logger.info("Alpha" + (i + 1) + " initialized to " + alpha.get(i));
        }
    }

}
