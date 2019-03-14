import org.um.feri.ears.algorithms.Algorithm;
import org.um.feri.ears.algorithms.AlgorithmInfo;
import org.um.feri.ears.algorithms.Author;
import org.um.feri.ears.problems.DoubleSolution;
import org.um.feri.ears.problems.StopCriteriaException;
import org.um.feri.ears.problems.Task;

public class RandomWalkAlgorithm extends Algorithm { //needs to me extended 
	private DoubleSolution i; //EARS Individual includes solution vector and its fitness value
	private boolean debug = true;

	public RandomWalkAlgorithm() { 
		super();
		setDebug(debug);  //EARS prints some debug info
		ai = new AlgorithmInfo("","","RWSi+","Random Walk+");  //EARS add algorithm name
		au =  new Author("robi", "N/A"); //EARS author info
	}

	@Override  
	public DoubleSolution execute(Task taskProblem) throws StopCriteriaException{ //EARS main evaluation loop 
		DoubleSolution ii;
		i = taskProblem.getRandomSolution(); //EARS Helper for creating random solution, it takes one evaluation (eval++)
		//user can use its own representation for example double[] and in fase of evaluation calls taskProblem.eval that creates individual
		System.out.println(taskProblem.getNumberOfEvaluations()+" "+i); //prints number of evaluations

		while (!taskProblem.isStopCriteria()) {   //EARS user needs to take care about number of evaluations
			ii = taskProblem.getRandomSolution();
			if (taskProblem.isFirstBetter(ii, i)) { //EARS primary function it takes care if we are searching minimum or maximum, if solution is valit etc.
				i = ii;
				if (debug) System.out.println(taskProblem.getNumberOfEvaluations()+" "+i);
			}
		}
		return i;

	}

	@Override
	public void resetDefaultsBeforNewRun() {
		i=null;
	}
}