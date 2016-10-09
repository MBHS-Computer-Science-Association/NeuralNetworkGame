package neuralnetworkgame;
import java.util.List;

public class Xor {
	public static void main(String args[]) {
		NEAT n = new NEAT(1, 3);
		int highestI = 0;
		int highestJ = 0;

		found: for (int g = 1; g <= 128; g++) {
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
					float fitness = 5 - err;
					if (fitness > highestFitness) {
						highestFitness = fitness;
						highestI = i;
						highestJ = j;
					}
					net[0][0] = fitness;
				}
			}
			System.out.println(g + ": " + highestFitness);
			if (highestFitness > 4.9f) {
				break found;
			}
		}

		Float[][] net = n.species.get(highestI).get(highestJ);
		float err = 0;
		float[] input = new float[3];
		input[2] = 1.0f; // BIAS

		input[0] = 0;
		input[1] = 0;
		err += Math.abs(n.getOutput(input, net)[0] - 0);
		System.out.println(n.getOutput(input, net)[0]);

		input[0] = 1;
		input[1] = 0;
		err += Math.abs(n.getOutput(input, net)[0] - 1);
		System.out.println(n.getOutput(input, net)[0]);

		input[0] = 0;
		input[1] = 1;
		err += Math.abs(n.getOutput(input, net)[0] - 1);
		System.out.println(n.getOutput(input, net)[0]);

		input[0] = 1;
		input[1] = 1;
		err += Math.abs(n.getOutput(input, net)[0] - 0);
		System.out.println(n.getOutput(input, net)[0]);
		System.out.println("error: " + err);
		System.out.println("nodes: " + (net[0].length - 1));
		System.out.println("links: " + (net.length - 1));
	}
}
