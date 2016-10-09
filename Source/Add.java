import java.util.List;
import java.util.Random;

public class Add {
	public static void main(String args[]) {
		Random randy = new Random();
		int totalQ=0;
		for (int q = 1; q < 100; q++) {
			NEAT n = new NEAT(1, 3);
			int highestI = 0;
			int highestJ = 0;

			found: for (int g = 1; g <= 10000; g++) {
				if (g > 1) {
					n.step();
				}
				List<List<Float[][]>> species = n.species;
				float highestFitness = Float.MIN_VALUE;
				highestI = -1;
				highestJ = -1;
				for (int i = 0; i < species.size(); i++) {
					List<Float[][]> s = species.get(i);
					for (int j = 0; j < s.size(); j++) {
						Float[][] net = s.get(j);
						float err = 0;
						float[] input = new float[3];
						input[2] = 1.0f; // BIAS

						for(int k=0; k<5; k++) {
							int one = randy.nextInt(2);
							int two = randy.nextInt(2);
							input[0] = one;
							input[1] = two;
							err += Math.abs(n.getOutput(input, net)[0] - (one+two));

						}

						float fitness = 100 - err;
						if (fitness > highestFitness) {
							highestFitness = fitness;
							highestI = i;
							highestJ = j;
						}
						net[0][0] = fitness;
					}
				}
				//System.out.println(g + ": " + highestFitness);
				if (highestFitness > 95) {
					System.out.println("win");
					totalQ+=g;
					break found;
				}
			}

			float err = 0;
			float[] input = new float[3];
			input[2] = 1.0f; // BIAS

			for(int k=0; k<5; k++) {
				int one = randy.nextInt(10);
				int two = randy.nextInt(10);
				input[0] = one;
				input[1] = two;
				err += Math.abs(n.getOutput(input, n.species.get(highestI).get(highestJ))[0] - (one+two));
			}
	
			System.out.println("error: " + err);
			//System.out.println("nodes: " + (net[0].length - 1));
			//System.out.println("links: " + (net.length - 1));
		}
		System.out.println(totalQ/100.0);
	}
}
