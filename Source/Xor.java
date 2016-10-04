import java.util.List;

public class Xor {
	public static void main(String args[]) {
		NEAT n = new NEAT(1, 2);
		for (int g = 0; g < 100; g++) {
			List<List<Float[][]>> species = n.species;
			float highestFitness = Float.MIN_VALUE;
			for (int i = 0; i < species.size(); i++) {
				List<Float[][]> s = species.get(i);
				for (int j = 0; j < s.size(); j++) {
					Float[][] net = s.get(j);
					float err = 0;
					float[] input = new float[2];
					input[0] = 0;
					input[1] = 0;
					err += Math.abs(n.getOutput(input, net)[0] - 0);

					input[0] = 1;
					input[1] = 0;
					err += Math.abs(n.getOutput(input, net)[0] - 1);

					input[0] = 0;
					input[1] = 1;
					err += Math.abs(n.getOutput(input, net)[0] - 1);

					input[0] = 1;
					input[1] = 1;
					err += Math.abs(n.getOutput(input, net)[0] - 0);
					float fitness = 5- err;
					if(fitness> highestFitness) {
						highestFitness = fitness;
					}
					net[0][0] = fitness;
				}
			}
			System.out.println(g+": "+highestFitness);
			n.step();
		}
	}
}
