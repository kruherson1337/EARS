import java.util.ArrayList;

import org.um.feri.ears.algorithms.Algorithm;
import org.um.feri.ears.algorithms.AlgorithmInfo;
import org.um.feri.ears.algorithms.Author;
import org.um.feri.ears.problems.DoubleSolution;
import org.um.feri.ears.problems.StopCriteriaException;
import org.um.feri.ears.problems.Task;
import org.um.feri.ears.util.Util;

/* By https://en.wikipedia.org/wiki/Differential_evolution
 * 
 */
public class DE08 extends Algorithm {
	ArrayList<DoubleSolution> pop_x;
	DoubleSolution best;
	int pop_size  = 20;
	double CR=0.9, F=0.9;
	
	public DE08(int pop_size, double CR, double F, String ime ) {
		this.pop_size = pop_size;
		this.CR = CR;
		this.F = F;
		ai = new AlgorithmInfo("DEStudentID_"+ime,"DEStudentID_"+ime,ime, ime);  //EARS add algorithm name
		au =  new Author("StudentX", "N/A"); //EARS author info

	}
	private void initAll(Task taskProblem, int pop_size) throws StopCriteriaException {
		pop_x = new ArrayList<>();
		//meje ce se rabijo DE ne potrebuje!
		double spodnjaMeja[] = taskProblem.getLowerLimit();
		double zgornjaMeja[] = taskProblem.getUpperLimit();
		double interval[] = taskProblem.getInterval();
		
		DoubleSolution tmp;
		for (int i=0; i<pop_size; i++) {
			tmp = taskProblem.getRandomSolution();
			pop_x.add(tmp);
			if (i==0) best = tmp;
			else if (taskProblem.isFirstBetter(tmp, best)) best = tmp;
			if (taskProblem.isStopCriteria()) break;
		}	
	}
	@Override
	public DoubleSolution execute(Task taskProblem) throws StopCriteriaException {
		initAll(taskProblem, pop_size);
		int a, b, c, indD;
		DoubleSolution tmpY;
		double y[];
		double ran;
		while (!taskProblem.isStopCriteria()) {
			for (int i=0; i<pop_size; i++) {
				a = Util.nextInt(pop_size);
				while (a==i) a = Util.nextInt(pop_size);
				b = Util.nextInt(pop_size);
				while ((b==a)||(b==i)) b = Util.nextInt(pop_size);
				c = Util.nextInt(pop_size);
				while ((c==b)||(c==a)||(c==i)) c = Util.nextInt(pop_size);
				indD = Util.nextInt(taskProblem.getNumberOfDimensions());
				y = new double[taskProblem.getNumberOfDimensions()];
				for (int n=0; n<taskProblem.getNumberOfDimensions(); n++) {
					ran = Util.nextDouble();
					if ((ran < CR) || (n == indD)) {
						y[n] = taskProblem.setFeasible(pop_x.get(a).getValue(n)+F*(pop_x.get(b).getValue(n)-pop_x.get(c).getValue(n)),n);
					} else
						y[n] = pop_x.get(i).getValue(n);
				}
				tmpY = taskProblem.eval(y);
				if (taskProblem.isFirstBetter(tmpY, pop_x.get(i))) {
					pop_x.set(i, tmpY);
					if (taskProblem.isFirstBetter(tmpY, best)) best = tmpY;
				}
				if (taskProblem.isStopCriteria()) break;
			}
		}
		return best;
	}

	@Override
	public void resetDefaultsBeforNewRun() {
		// TODO Auto-generated method stub
		
	}

}
