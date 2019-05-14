import org.um.feri.ears.algorithms.Algorithm;
import org.um.feri.ears.algorithms.WhaleOptimizationAlgorithm;
import org.um.feri.ears.algorithms.so.cro.CRO;
import org.um.feri.ears.algorithms.so.gwo.GWO;
import org.um.feri.ears.algorithms.so.jade.JADE;
import org.um.feri.ears.algorithms.so.random.RandomWalkAlgorithm;
import org.um.feri.ears.benchmark.*;
import org.um.feri.ears.problems.DoubleSolution;
import org.um.feri.ears.problems.results.BankOfResults;
import org.um.feri.ears.rating.Player;
import org.um.feri.ears.rating.ResultArena;
import org.um.feri.ears.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tomaz Stoiljkovic
 *
 * @User: nxf19259
 * @Email: tomaz.stoiljkovi@nxp.com
 * @Date: 13.3.2019
 **/
public class MetaEvolution {

    private static final int population_size = 50;
    private static final int D = 3; // population, CR, F
    private static DoubleSolution[] population;
    private static DoubleSolution best;
    private static long generations;
    private static final long max_generations = 1000;
    private static double CR;
    private static double F;

    public static void main(String[] args) {

        // Set the seed of the random generator
        Util.rnd.setSeed(42);

        CR = 0.9;
        F = 0.9;

        // prints one on one results
        RatingBenchmark.debugPrint = false;

        // Initialize
        population = new DoubleSolution[population_size];
        best = new DoubleSolution();
        best.setEval(1);
        for (int i = 0; i < population_size; i++) {
            population[i] = new DoubleSolution();
            for (int j = 0; j < D; j++) {
                population[i].setVariables(getRandomIndividual(D));
            }
            population[i].setEval(evaluate(population[i]));
            if (population[i].getEval() > best.getEval()) {
                best = population[i];
                printBest();
            }
        }

        // DE
        int a, b, c, indD;
        DoubleSolution tmpY;
        Double y[];
        double ran;
        while (generations < max_generations) {
            for (int i = 0; i < population_size; i++) {
                a = Util.nextInt(population_size);
                while (a == i) a = Util.nextInt(population_size);
                b = Util.nextInt(population_size);
                while ((b == a) || (b == i)) b = Util.nextInt(population_size);
                c = Util.nextInt(population_size);
                while ((c == b) || (c == a) || (c == i)) c = Util.nextInt(population_size);
                indD = Util.nextInt(D);
                y = new Double[D];
                for (int n = 0; n < D; n++) {
                    ran = Util.nextDouble();
                    if ((ran < CR) || (n == indD)) {
                        y[n] = population[a].getValue(n) + F * (population[b].getValue(n) - population[c].getValue(n));
                    } else
                        y[n] = population[i].getValue(n);
                    if (y[n] > 1.0)
                        y[n] = 1.0;
                    else if (y[n] < 0.0)
                        y[n] = 0.0;
                }
                tmpY = new DoubleSolution(Arrays.asList(y), evaluate(y));
                if (tmpY.getEval() > population[i].getEval()) {
                    population[i] = tmpY;
                    if (population[i].getEval() > best.getEval()) {
                        best = population[i];
                        printBest();
                    }
                }
            }
            generations++;
        }
    }

    private static double evaluate(DoubleSolution solution) {
        return evaluate(solution.getValue(0), solution.getValue(1), solution.getValue(2));
    }

    private static double evaluate(Double[] solution) {
        return evaluate(solution[0], solution[1], solution[2]);
    }

    private static double evaluate(double pop_size, double cr, double f) {

        // add algorithms to list of players
        ArrayList<Algorithm> players = new ArrayList<>();
        players.add(new CRO());
        players.add(new GWO());
        players.add(new DE08(20, 0.9, 0.9, "DE08"));
        players.add(new DE08(65, 0.9, 0.9, "DE08_65_0.9_0.9"));
        players.add(new DE08(97, 1.000000,0.819024, "DE08_97_1.000000_0,819024"));
        int new_pop_size = getPopulation(pop_size);
        players.add(new DE08(new_pop_size, cr, f, String.format("DE_META_%d_%f_%f", new_pop_size, cr, f)));
        players.add(new WhaleOptimizationAlgorithm());
        players.add(new RandomWalkAlgorithm());
        players.add(new JADE());
        ResultArena ra = new ResultArena(100);
        //RatingRPCOe1 rpcOe1 = new RatingRPCOe1();
        RatingRPUOed2 rpuoed2 = new RatingRPUOed2(); // Create benchmark
        //RatingRPUOed30 rpuoed30 = new RatingRPUOed30();

        //rpcOe1.registerAlgorithms(players);
        rpuoed2.registerAlgorithms(players);
        //rpuoed30.registerAlgorithms(players);

        // set initial rating data for each participating player
        for (Algorithm al : players) {
            ra.addPlayer(al, al.getID(), 1500, 350, 0.06, 0, 0, 0);
        }
        BankOfResults ba = new BankOfResults();

        //rpcOe1.run(ra, ba, 1);
        rpuoed2.run(ra, ba, 1);
        //rpuoed30.run(ra, ba, 1); // start the tournament

        // display the leader board
        ArrayList<Player> list = ra.getPlayers();
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).getPlayerId().startsWith("DE_META"))
                return list.get(i).getRatingData().getRating();

        return 1; // TOTAL KAOS
    }

    private static int getPopulation(double value) {
        return Math.max(6, (int) Math.round(value * 150));
    }

    private static void printBest() {
        System.out.println("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n");
        System.out.println(String.format("Generation:%d\tRating:%f\tPopulation Size:%d\tCR:%f\tF:%f", generations, best.getEval(), getPopulation(best.getValue(0)), best.getValue(1), best.getValue(2)));
    }

    private static List<Double> getRandomIndividual(int size) {
        ArrayList<Double> randoms = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            randoms.add(Util.nextDouble());
        }
        return randoms;
    }
}
