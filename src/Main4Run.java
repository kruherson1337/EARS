import org.um.feri.ears.algorithms.Algorithm;
import org.um.feri.ears.algorithms.so.de.DEAlgorithm;
import org.um.feri.ears.problems.EnumStopCriteria;
import org.um.feri.ears.problems.StopCriteriaException;
import org.um.feri.ears.problems.Task;
import org.um.feri.ears.problems.unconstrained.Schwefel;
import org.um.feri.ears.problems.unconstrained.Sphere;

public class Main4Run {
	public static void main(String[] args) {
		Task t = new Task(EnumStopCriteria.EVALUATIONS, 125000, 0, 0, 0.0001, new Schwefel(30)); //run problem Sphere Dimension 5, 3000 evaluations
		RandomWalkAlgorithm test = new RandomWalkAlgorithm();
		try {
			System.out.println(test.execute(t)); //prints best result afrer 3000 runs
		} catch (StopCriteriaException e) {
			e.printStackTrace();
		}
		//Algorithm al = new DEAlgorithm(DEAlgorithm.JDE_rand_1_bin);
		Algorithm al = new DE08(20,0.9,0.9,"DE08");
		t = new Task(EnumStopCriteria.EVALUATIONS, 5003, 0, 0, 0.0001, new Sphere(5)); //run problem Sphere Dimension 5, 3000 evaluations

		try {
			System.out.println("Rezultat za:"+al.getAlgorithmInfoCSV());
			System.out.println(al.execute(t)); //prints best result afrer 3000 runs
		} catch (StopCriteriaException e) {
			e.printStackTrace();
		}
		
		
	}
}
