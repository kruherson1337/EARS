package org.um.feri.ears.algorithms;

import org.um.feri.ears.problems.DoubleSolution;
import org.um.feri.ears.problems.StopCriteriaException;
import org.um.feri.ears.problems.Task;
import org.um.feri.ears.util.Util;

/**
 * Created by Tomaz Stoiljkovic
 *
 * @User: nxf19259
 * @Email: tomaz.stoiljkovi@nxp.com
 * @Date: 12.3.2019
 **/
public class WhaleOptimizationAlgorithm extends Algorithm {
    private final boolean debug = true;

    private final int populationSize; /* number of population members */
    private double a; /* a are linearly decreased from 2 to 0 over the course of iterations Eq. (2.3) */
    private double a2; /* a2 are linearly decreased from -1 to -2 to calculate t in Eq. (3.12) */
    private double b; /* parameters in Eq. (2.5) */
    private double A; /* A = 2*a*r1-a */
    private double C; /* C = 2*r2 */
    private double l; /* */
    private double p; /* p is a random number in [0,1] */
    private long gen;

    public WhaleOptimizationAlgorithm() {
        this(100);
    }

    public WhaleOptimizationAlgorithm(int populationSize) {
        super();
        setDebug(debug);

        ai = new AlgorithmInfo("", "", "WOA", "Whale Optimization Algorithm");
        au = new Author("E5027006", "tomaz.stoiljkovic@gmail.com");

        this.populationSize = populationSize;
        this.gen = 0;
    }

    @Override
    public DoubleSolution execute(Task taskProblem) throws StopCriteriaException {

        // Initialize the whales population  Xi (i = 1, 2, ..., n)
        DoubleSolution[] Xi = new DoubleSolution[populationSize];
        for (int i = 0; i < populationSize; i++) {
            Xi[i] = taskProblem.getRandomSolution();
        }

        DoubleSolution XBest = Xi[0];

        while (!taskProblem.isStopCriteria()) {

            // X*= the best search agent
            for (int i = 0; i < populationSize; i++) {
                if (taskProblem.isFirstBetter(Xi[i], XBest)) {
                    XBest = Xi[i];
                }
            }

            // Update a, a2
            a = 2d - gen * (2d / (double) taskProblem.getMaxEvaluations()); // % a decreases linearly fron 2 to 0 in Eq. (2.3)
            a2 = -1d + gen * ((-1d) / (double) taskProblem.getMaxEvaluations()); // a2 linearly dicreases from -1 to -2 to calculate t in Eq. (3.12)

            // for each search agent
            for (int i = 0; i < populationSize; i++) {

                // Update A, C, l and p
                double r1 = Util.rnd.nextDouble();
                double r2 = Util.rnd.nextDouble();

                A = 2d * a * r1 - a; // Eq. (2.3)
                C = 2d * r2; // Eq. (2.4)

                b = 1; // parameters in Eq. (2.5)
                l = (a2 - 1) * Util.rnd.nextDouble() + 1; // parameters in Eq. (2.5)

                p = Util.rnd.nextDouble(); // parameters in Eq. (2.6)

                for (int j = 0; j < taskProblem.getNumberOfDimensions(); j++) {
                    if (p < 0.5) {
                        if (Math.abs(A) < 1) {

                            // Update the position of the current search agent by the equation (1)
                            final double D_Leader = Math.abs(C * XBest.getValue(j) - Xi[i].getValue(j)); // Eq. (2.1)
                            Xi[i].setValue(j, XBest.getValue(j) - A * D_Leader); // Eq. (2.2)
                        } else if (Math.abs(A) >= 1) {

                            // Select a random search agent (X_rand)
                            final int rand_leader_index = (int) Math.floor(populationSize * Util.rnd.nextDouble());
                            final DoubleSolution X_rand = Xi[rand_leader_index];
                            final double D_X_rand = Math.abs(C * X_rand.getValue(j) - Xi[i].getValue(j));

                            // Update the position of the current search agent by the equation (3)
                            Xi[i].setValue(j, X_rand.getValue(j) - A * D_X_rand);
                        }
                    } else if (p >= 0.5) {

                        // Update the position of the current search by the by the equation (2)
                        final double distance2Leader = Math.abs(XBest.getValue(j) - Xi[i].getValue(j));
                        Xi[i].setValue(j, distance2Leader * Math.exp(b * l) * Math.cos(l * 2 * Math.PI) + XBest.getValue(j));
                    }
                }
            }

            // Check feasibility
            for (int i = 0; i < populationSize; i++) {
                for (int j = 0; j < taskProblem.getNumberOfDimensions(); j++) {
                    Xi[i].setValue(j, taskProblem.setFeasible(Xi[i].getValue(j), j));
                }
            }

            // Calculate the fitness of each search agent
            for (int i = 0; i < populationSize; i++) {
                Xi[i] = taskProblem.eval(Xi[i]);
            }

            // Update X* if there is a better solution
            for (int i = 0; i < populationSize; i++) {
                if (taskProblem.isFirstBetter(Xi[i], XBest)) {
                    XBest = Xi[i];
                }
            }

            gen++;
        }

        return XBest;
    }

    @Override
    public void resetDefaultsBeforNewRun() {

    }
}
