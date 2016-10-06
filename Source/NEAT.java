
/**
 * Class used for NEAT calculations
 * 
 * @author Christian Duffee
 * @author Joshua Kim
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NEAT {

	/**
	 * [0][0] represents the fitness of the network, each after in the [0][n] is
	 * a node alive inside the network
	 * 
	 * Following this, Each Network is represented by a 2D array, the first
	 * dimension (starting at index 1) represents each gene The second dimension
	 * has these fields in this order Gene Index, Weight, Enabled (0 for
	 * disabled, 1 for enabled)
	 */

	/**
	 * List of each species' fitness
	 */
	float[] speciesFitness;

	/**
	 * List of species
	 */
	public List<List<Float[][]>> species = new ArrayList<>();

	/**
	 * Holds new networks before they are assigned to species
	 */
	public List<Float[][]> crib = new ArrayList<>();

	/**
	 * List of Genes. Each Gene has these fields in this order Start, End
	 */
	public List<float[]> genes = new ArrayList<>();

	/**
	 * List of Nodes, Each node has these fields in this order initial start,
	 * initial end
	 */
	public List<float[]> nodes = new ArrayList<>();

	/**
	 * The coefficients for the distance function
	 */
	float cE = 1f; // constant of excess genesdiscor
	float cD = 1f; // constant of disjointed genes
	float cW = 0.4f; // constant of weights
	final float cutoffN = 20; // max at which N round to 1

	/**
	 * Threshold for the distance function
	 */
	float dT = 3.0f; // distance function threshold

	/**
	 * Coefficient used in the activation function
	 */
	final double cA = -4.9; // constant of activation

	/**
	 * Constants used for data storage
	 */
	final float enabled = 1.0f;
	final float disabled = 0.0f;

	/**
	 * Constants used for managing reproduction
	 */
	public float weightMutationRate = 0.8f; // change one gene will mutate
	public float newWeightRate = 0.1f; // change that the mutated gene will be
										// assigned a new value
	public float weightChangeRate = 0.05f; // rate at which weights are changed
	public float inheritedOfDisabledGeneActivated = 0.25f; // chance that an
															// inherited
															// disabled gene
															// will activate
	public int cutoffGeneration = 15; // if the average fitness of a species has
										// not improved by this many generations
										// they are eliminated
	public float interspeciesMateingRate = 0.001f; // the rate of mutations
													// between species
	public float newNodeRate = 0.03f; // chance of adding a new node
	public float smallNewLinkRate = 0.05f; // change of adding a new link in
											// small populations
	public float largeNewLinkRate = 0.3f; // chance of adding a new link in
											// large populations
	public float randMinWeight = -10f; // minimum weight chosen in random
										// function
	public float randMaxWeight = 10f; // maximum weight chosen in random
										// function
	public float newLeadingNodeWeight = 1.0f;
	public int populationSize = 150; // the size of the population

	/**
	 * Random number generator used for breeding
	 */
	Random randy = new Random(); // RNG

	/**
	 * Used in calculating output
	 */
	int numTimeSteps = 5; // number of time steps useda

	int outputWidth = 1;
	int inputWidth = 1;

	/**
	 * Constructor for NEAT
	 * 
	 * @param outputWidth
	 *            The width of the output row
	 * @param inputWidth
	 *            THe width of the input row
	 */
	public NEAT(int outputWidth, int inputWidth) {
		this.outputWidth = outputWidth;
		this.inputWidth = inputWidth;
		nodes.add(new float[0]); // placeholder
		for (int i = 1; i < inputWidth + 1; i++) {
			float[] node = new float[2];
			float nodeID = getNodeID();
			node[0] = -1 * nodeID;
			node[1] = nodeID;
			nodes.add(node);
		}
		for (int i = 0; i < outputWidth; i++) {
			float[] node = new float[2];
			float nodeID = getNodeID();
			node[0] = -1 * (1 + nodeID + inputWidth);
			node[1] = nodeID + inputWidth;
			nodes.add(node);
		}

		float[][] adam = new float[inputWidth * outputWidth + 1][0];
		adam[0] = new float[inputWidth + outputWidth + 1];
		for (int i = 1; i < adam[0].length; i++) {
			adam[0][i] = i;
		}

		int i = 1;
		for (int s = 0; s < inputWidth; s++) {
			for (int e = 0; e < outputWidth; e++) {
				float geneID = getGeneID();
				float[] gene = new float[2];
				gene[0] = s + 1;
				gene[1] = inputWidth + 1 + e;
				genes.add(gene);
				gene = new float[3];
				gene[0] = geneID; // added new gene first so can't condense here
				gene[1] = 1.0f;
				gene[2] = enabled;
				adam[i++] = gene;
			}
		}

		species = new ArrayList<>();
		ArrayList<Float[][]> firstSpecies = new ArrayList<>();
		for (int n = 0; n < populationSize; n++) {
			Float[][] adamClone = new Float[adam.length][0];
			for (int j = 0; j < adam.length; j++) {
				adamClone[j] = new Float[adam[j].length];
				for (int k = 0; k < adam[j].length; k++) {
					adamClone[j][k] = adam[j][k];
				}
			}
			firstSpecies.add(adamClone);
		}

		species.add(firstSpecies);

	}

	/**
	 * Function to calculate the distance between two genomes
	 * 
	 * @param E
	 *            The number of excess genes
	 * @param D
	 *            The number of disjoint genes
	 * @param W
	 *            The total distance in weights between the two genomes
	 * @param N
	 *            The number of genes in the larger genome, to normalize for
	 *            genome size. Can be set to 1 if both genomes are small (<20
	 *            genes)
	 * @return delta The distance measure between the two networks
	 */
	public float delta(float E, float D, float W, float N) {
		float delta = cE * E / N + cD * D / N + cW * W;
		return delta;
	}

	/**
	 * Returns distance between two networks
	 * 
	 * @param net1
	 *            First Network
	 * @param net2
	 *            Second Network
	 * @return distance between them
	 */
	public float delta(Float[][] net1, Float[][] net2) {
		float E = 0;
		float D = 0;
		float W = 0;
		float N = 1;
		if (!(net1.length < cutoffN && net2.length < cutoffN)) {
			N = Math.max(net1.length, net2.length);
		}
		int ia = 1;
		int ib = 1;
		while (ia < net1.length && ib < net2.length) {
			float na = net1[ia][0];
			float nb = net2[ib][0];
			if (na == nb) {
				W += abs(net1[ia][1] - net2[ib][1]);
				ia++;
				ib++;
			} else if (na < nb) {
				D++;
				ia++;
			} else if (nb < na) {
				D++;
				ib++;
			}
		}
		if (ia < net1.length) {
			E += net1.length - ia;
		} else if (ib < net2.length) {
			E += net2.length - ib;
		}
		return delta(E, D, W, N);
	}

	/**
	 * Runs one "step" generation of breeding
	 */
	public void step() {
		pruneAll();
		calcSpeciesFitness();
		breedAll();
		seperateSpecies();
	}

	/**
	 * Removes the least fit network from each species
	 */
	public void pruneAll() {
		purgeExtinctSpecies();
		for (int i = 0; i < species.size(); i++) {
			prune(species.get(i));
		}
		purgeExtinctSpecies();
	}

	/**
	 * Removes Extinct Species
	 */
	public void purgeExtinctSpecies() {
		for (int i = species.size() - 1; i >= 0; i--) {
			if (species.get(i).size() == 0) {
				species.remove(i);
			}
		}
	}

	/**
	 * Removes the least fit network from the provided species
	 * 
	 * @param s
	 *            species
	 */
	public void prune(List<Float[][]> s) {
		int lowestFitness = Integer.MAX_VALUE;
		int lowestFitnessIndex = -1;
		for (int n = 0; n < s.size(); n++) {
			Float[][] network = s.get(n);
			if (network[0][0] < lowestFitness) {
				lowestFitness = network[0][0].intValue();
				lowestFitnessIndex = n;
			}
		}
		s.remove(lowestFitnessIndex);
	}

	/**
	 * Calculates the fitness of each species
	 */
	public void calcSpeciesFitness() {
		speciesFitness = new float[species.size()];
		for (int i = 0; i < speciesFitness.length; i++) {
			float total = 0;
			List<Float[][]> s = species.get(i);
			for (int n = 0; n < s.size(); n++) {
				total += s.get(n)[0][0];
			}
			speciesFitness[i] = total / s.size();
		}
	}

	/**
	 * Breeds the new generations
	 */
	public void breedAll() {
		crib = new ArrayList<>();
		float sumFitness = 0;
		for (int i = 0; i < speciesFitness.length; i++) {
			sumFitness += speciesFitness[i];
		}
		for (int i = 0; i < species.size(); i++) {
			int numberOfChildren = (int) (speciesFitness[i] / sumFitness * populationSize);
			breedSpecies(species.get(i), numberOfChildren);
		}
		// TODO: Cross species reproduction
	}

	/**
	 * Breeds the species and adds the children to the crib
	 * 
	 * @param s
	 *            species that is to be bred
	 * @param numberOfChildren
	 *            the number of children to make
	 */
	public void breedSpecies(List<Float[][]> s, int numberOfChildren) {
		for (int i = 0; i < numberOfChildren; i++) {
			int mother = rand(0, s.size() - 1);
			int father = rand(0, s.size() - 1);
			Float[][] child = breed(s.get(mother), s.get(father));
			crib.add(child);
		}
	}

	/**
	 * Breeds two networks
	 * 
	 * @param a
	 *            Parent A
	 * @param b
	 *            Parent B
	 * @return the child network
	 */
	public Float[][] breed(Float[][] a, Float[][] b) {
		boolean aMoreFit = false;
		if (a[0][0] >= b[0][0]) {
			aMoreFit = true;
		}
		int len = (aMoreFit ? a.length : b.length);

		Float[][] c = new Float[len][0];
		if (aMoreFit) {
			c[0] = new Float[a[0].length];
			for (int i = 0; i < a[0].length; i++) {
				c[0][i] = a[0][i];
			}
		} else {
			c[0] = new Float[b[0].length];
			for (int i = 0; i < b[0].length; i++) {
				c[0][i] = b[0][i];
			}
		}

		int ic = 1;
		int ia = 1;
		int ib = 1;
		while (ia < a.length && ib < b.length) {
			float na = a[ia][0];
			float nb = b[ib][0];
			if (na == nb) {
				boolean randParent = randy.nextBoolean();
				copyGene(c, (randParent ? a : b), ic, (randParent ? ia : ib),
						a[ia][2] == disabled || b[ib][2] == disabled);
				ic++;
				ia++;
				ib++;
			} else if (na < nb) {
				if (aMoreFit) { // inherits only if parent more fit
					copyGene(c, a, ic, ia, a[ia][2] == 1.0f);
					ic++;
				}
				ia++;
			} else if (nb < na) {
				if (!aMoreFit) { // inherits only if parent more fit
					copyGene(c, b, ic, ib, b[ib][2] == 1.0f);
					ic++;
				}
				ib++;
			}
		}

		if (aMoreFit) {
			while (ia < a.length) {
				copyGene(c, a, ic, ia, a[ia][2] == 1.0f);
				ic++;
				ia++;
			}
		} else {
			while (ib < b.length) {
				copyGene(c, b, ic, ib, b[ib][2] == 1.0f);
				ic++;
				ib++;
			}
		}

		// Modifies weight
		if (randy.nextFloat() < weightMutationRate) {
			for (int g = 1; g < c.length; g++) {
				Float[] gene = c[g];
				if (randy.nextFloat() < newWeightRate) {
					gene[1] = randWeight();
				} else {
					gene[1] = gene[1] + (randy.nextBoolean() ? 1 : -1) * weightChangeRate;
				}
			}
		}

		// Adds a new Node
		if (randy.nextFloat() < newNodeRate) {
			Float start = c[0][rand(1, c[0].length - 1)];
			Float end = c[0][rand(1, c[0].length - 1)];
			float nodeID = -1f;

			found: for (int i = 1; i < c.length; i++) {
				float[] node = nodes.get(i);
				if (node[0] == start && node[1] == end) {
					nodeID = i;
					break found;
				}
			}

			if (nodeID == -1) {
				nodeID = getNodeID();
				float[] newNode = new float[2];
				newNode[0] = start;
				newNode[1] = end;
				nodes.add(newNode);
			}

			int firstLinkID = -1;
			int lastLinkID = -1;
			found: for (int g = 0; g < genes.size(); g++) {
				float[] gene = genes.get(g);
				if (gene[0] == start && gene[1] == nodeID) {
					firstLinkID = g;
				}
				if (gene[0] == nodeID && gene[1] == end) {
					lastLinkID = g;
				}
				if (firstLinkID != -1 && lastLinkID != -1) {
					break found;
				}
			}

			if (firstLinkID == -1) {
				firstLinkID = (int) getGeneID();
				float[] newGene = new float[2];
				newGene[0] = start;
				newGene[1] = nodeID;
				genes.add(firstLinkID, newGene);
			}

			if (lastLinkID == -1) {
				lastLinkID = (int) getGeneID();
				float[] newGene = new float[2];
				newGene[0] = nodeID;
				newGene[1] = end;
				genes.add(lastLinkID, newGene);
			}

			// adds node to the array of nodes
			Float[] newFirstLayer = new Float[c[0].length + 1];
			for (int i = 0; i < c[0].length; i++) {
				newFirstLayer[i] = c[0][i];
			}
			newFirstLayer[newFirstLayer.length - 1] = nodeID;

			Float[][] c2 = new Float[c.length + 2][0];
			c2[0] = newFirstLayer;
			float oldWeight = 1.0f;
			for (int i = 1; i < c.length; i++) {
				// index, weight, enabled
				Float[] copyGene = new Float[3];
				copyGene[0] = c[i][0];
				copyGene[1] = c[i][1];
				copyGene[2] = c[i][2];

				float[] storedGene = genes.get(c[i][0].intValue());
				if (storedGene[0] == start && storedGene[1] == end) {
					copyGene[2] = disabled;
					oldWeight = copyGene[1];
				}
				c2[i] = copyGene;
			}

			Float[] firstAddedGene = new Float[3];
			firstAddedGene[0] = (float) firstLinkID;
			firstAddedGene[1] = newLeadingNodeWeight;
			firstAddedGene[2] = enabled;
			c2[c2.length - 2] = firstAddedGene;

			Float[] lastAddedGene = new Float[3];
			lastAddedGene[0] = (float) lastLinkID;
			lastAddedGene[1] = oldWeight;
			lastAddedGene[2] = enabled;
			c2[c2.length - 1] = lastAddedGene;

			c = c2;
		}

		// TODO: Add large network support
		addGene: if (randy.nextFloat() < smallNewLinkRate) {
			List<Float[]> possNewGenes = new ArrayList<>();
			for (int s = 1; s < c[0].length; s++) {
				for (int e = 1; e < c[0].length; e++) {
					for (int g = 1; g < c.length; g++) {
						Float[] dna = c[g];
						float[] gene = genes.get(dna[0].intValue());
						if (!(s == gene[0] && e == gene[1])) {
							Float[] possNewGene = new Float[2];
							possNewGene[0] = (float) s;
							possNewGene[1] = (float) e;
							possNewGenes.add(possNewGene);
						}
					}
				}
			}

			if (possNewGenes.size() == 0) {
				break addGene;
			}

			int possNewGeneIndex = rand(1, possNewGenes.size() - 1);
			float start = possNewGenes.get(possNewGeneIndex)[0];
			float end = possNewGenes.get(possNewGeneIndex)[1];
			float weight = randWeight();
			int geneID = -1;

			found: for (int i = 0; i < genes.size(); i++) {
				float[] gene = genes.get(i);
				if (start == gene[0] && end == gene[1]) {
					geneID = i;
					break found;
				}
			}

			if (geneID == -1) {
				geneID = (int) getGeneID();
				float[] newGene = new float[2];
				newGene[0] = start;
				newGene[1] = end;
				genes.add(geneID, newGene);
			}

			Float[][] c2 = new Float[c.length + 1][0];
			for (int i = 0; i < c.length; i++) {
				c2[i] = c[i];
			}
			Float[] newGene = new Float[3];
			newGene[0] = (float) geneID;
			newGene[1] = weight;
			newGene[2] = enabled;
			c2[c2.length - 1] = newGene;
			c = c2;
		}
		return c;
	}

	/**
	 * Separates crib into separate species and replaces old species list
	 */
	public void seperateSpecies() {
		// random old species used as samples to separate children
		List<Float[][]> oldSpeciesSamples = new ArrayList<>();
		for (int i = 0; i < species.size(); i++) {
			List<Float[][]> s = species.get(i);
			oldSpeciesSamples.add(s.get(rand(0, s.size() - 1)));
		}

		// will replace species
		List<List<Float[][]>> newSpecies = new ArrayList<>();

		for (int i = 0; i < species.size(); i++) {
			newSpecies.add(new ArrayList<>());
		}

		for (int i = 0; i < crib.size(); i++) {
			boolean isUnique = true;
			match: for (int n = 0; n < oldSpeciesSamples.size(); n++) {
				float delta = delta(crib.get(i), oldSpeciesSamples.get(n));
				if (delta < dT) {
					isUnique = false;
					newSpecies.get(n).add(crib.get(i));
					break match;
				}
			}
			if (isUnique) {
				oldSpeciesSamples.add(crib.get(i));
				newSpecies.add(new ArrayList<>());
				newSpecies.get(newSpecies.size() - 1).add(crib.get(i));
			}
		}

		species = newSpecies;
	}

	/**
	 * Copies the gene from the parent to the child
	 * 
	 * @param c
	 *            Child network
	 * @param p
	 *            Parent network
	 * @param ic
	 *            Child index
	 * @param ip
	 *            Parent index
	 * @param isDisabled
	 *            Whether either parent has the gene disabled
	 */
	public void copyGene(Float[][] c, Float[][] p, int ic, int ip, boolean isDisabled) {
		c[ic] = new Float[3];
		c[ic][0] = p[ip][0];
		c[ic][1] = p[ip][1];
		if (isDisabled) {
			if (randy.nextFloat() < inheritedOfDisabledGeneActivated) {
				c[ic][2] = enabled;
			} else {
				c[ic][2] = disabled;
			}
		} else {
			c[ic][2] = enabled;
		}
	}

	/**
	 * Returns output from network n
	 * 
	 * @param input
	 *            Input to the network
	 * @param n
	 *            Network
	 * @return Output from the network
	 */
	public float[] getOutput(float[] input, Float[][] n) {
		float[] output = new float[3];
		int stateSize = 0;
		for (int i = 1; i < n[0].length; i++) {
			int node = n[0][i].intValue();
			if (node > stateSize) {
				stateSize = node;
			}
		}
		float[] state = new float[stateSize + 1];
		float[] oldState = new float[stateSize + 1];
		// input is 0 indexed, state is 1 indexed.

		for (int t = 0; t < numTimeSteps; t++) {
			for (int i = 0; i < state.length; i++) {
				oldState[i] = state[i];
			}
			// Assuming no activation to input neurons and reset of input each
			// time
			for (int i = 0; i < input.length; i++) {
				state[i + 1] = input[i];
			}
			for (int g = 1; g < n.length; g++) {
				int geneIndex = n[g][0].intValue();
				float weight = n[g][1];
				float isEnabled = n[g][2];
				if (isEnabled == enabled) {
					float[] gene = genes.get(geneIndex);
					int startIndex = (int) gene[0];
					int endIndex = (int) gene[1];
					if (startIndex > 3 || endIndex > 3) {
						for (int i = 0; i < genes.size(); i++) {
						}
					}
					state[endIndex] += oldState[startIndex] * weight;
				}
			}
			for (int i = inputWidth + 1; i < state.length; i++) {
				state[i] = activation(state[i]);
			}
		}
		for (int i = 0; i < outputWidth; i++) {
			output[i] = state[1 + i + inputWidth];
		}
		return output;
	}

	/**
	 * Function applied to the output of nodes before the signal is transfered
	 * to the next node
	 * 
	 * @param input
	 *            of the activation function
	 * @return output of the activation function
	 */
	public float activation(float input) {
		float output = (float) (1 / (1 + Math.pow(Math.E, cA * input)));
		return output;
	}

	/**
	 * Function to get the next ID for a new gene and increment the numGeneID
	 * count
	 * 
	 * @return the next usable ID number for genes
	 */
	public float getGeneID() {
		return genes.size();
	}

	/**
	 * Gets the next ID for a new node and increments the numNodeID count
	 * 
	 * @return the next usable ID number for nodes
	 */
	public float getNodeID() {
		return nodes.size();
	}

	/**
	 * Gets the absolute value of f
	 * 
	 * @param f
	 * @return the absolute value of f
	 */
	public float abs(float f) {
		return Math.abs(f);
	}

	/**
	 * Returns a random integer between min and max
	 * 
	 * @param min
	 * @param max
	 * @return random integer
	 */
	public int rand(int min, int max) {
		return randy.nextInt(max + 1 - min) + min;
	}

	/**
	 * Returns a random float between min and max
	 * 
	 * @param min
	 * @param max
	 * @return random float
	 */
	public float rand(float min, float max) {
		return randy.nextFloat() * (max - min) + min;
	}

	/**
	 * Returns a random weight within bounds
	 * 
	 * @return random weight
	 */
	public float randWeight() {
		return rand(randMinWeight, randMaxWeight);
	}
}
