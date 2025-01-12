/**
 * Purpose	Network base which can provide an (approximate)
 * 		optimized schedule when given a hypothetical
 * 		relationship matrix
 * Status	Finished
 * Last update	01/06/25
 * Submitted	N/A
 * Comment	All code is my own original work
 * 
 * @author	Alaina Iris
 * @version	2025.01.06
 */
import java.util.ArrayList;

public class Network {
	private int[][] network;
	private String[] names;

	/**
	 * Create a new network
	 *
	 * @param network network relations
	 * @param names names of individuals
	 */
	public Network(int[][] network, String[] names) {
		this.network = network;
		this.names = names;
	}

	/**
	 * Get max # of relationships a person has
	 *
	 * @return maximum degree
	 */
	public int getDegree() {
		int max = 0;
		for(int i = 0; i < network.length; i++) {
			int degree = 0;
			for(int j = 0; j < network.length; j++) {
				if (network[i][j] > 0) {
					degree++;
				}
			}
			if (degree > max) {
				max = degree;
			}
		}
		return max;
	}

	/**
	 * Get the number of layers to make
	 *
	 * @return Layers to make
	 */
	public int getLayers() {
		int degree = (getDegree() + 1) / 3;
		int count;
		for(count = 0; degree >= 2; degree /= 2) {
			count++;
		}
		return count;
	}

	/**
	 * Get the maximum weight of a relationship
	 *
	 * @return Maximum weight of a relationship
	 */
	public int getMax() {
		int max = 0;
		for(int i = 0; i < network.length - 1; i++) {
			for(int j = i + 1; j < network.length; j++) {
				if (network[i][j] > max) {
					max = network[i][j];
				}
			}
		}
		return max;
	}

	/**
	 * Get maximum weight from a network
	 *
	 * @return Maximum weight of network
	 */
	public int getMax(int[][] network) {
		int max = 0;

		for(int i = 0; i < network.length - 1; i++) {
			for(int j = i + 1; j < network.length; j++) {
				if (network[i][j] > max) {
					max = network[i][j];
				}
			}
		}
		return max;
	}

	/**
	 * Get the total number of relationships in the network
	 *
	 * @return # of relationships
	 */
	public int getNumberOfRelationships() {
		int count = 0;
		for(int i = 0; i < network.length - 1; i++) {
			for(int j = i + 1; j < network.length; j++) {
				if (network[i][j] > 0) {
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * Get the maximum bound of our approximation
	 *
	 * @return Maximum bound for approximation
	 */
	public int getApproximationLimit() {
		int size = network.length;
		size = size * size * size;
		int count = 0;
		while (size >= 2) {
			count++;
			size /= 2;
		}

		return getMinimumRun() * count;
	}

	/**
	 * Get the minimum possible optimal schedule weight
	 *
	 * @return Best case of schedule weight
	 */
	public int getMinimumRun() {
		int maximum = getMax();
		for(int i = 0; i < network.length; i++) {
			int degree = 0;
			int lowest = maximum;
			for(int j = 0; j < network.length; j++) {
				int weight = network[i][j];
				if (weight > 0) {
					degree++;
					if (weight < lowest) {
						lowest = weight;
					}
				}
			}
			if (degree * lowest > maximum) {
				maximum = degree * lowest;
			}
		}
		return maximum;
	}

	/**
	 * Get our approximate optimized schedule
	 *
	 * @return Approximate optimized schedule
	 */
	public Schedule optimizedSchedule() {
		ArrayList<Layer> scheduleLayers = new ArrayList<Layer>();
		int layers = getLayers();
		int size = getMax();
		while (layers > 0) { // Break down layers
			int min = size / 2;
			int max = size;
			int[][] section = clone2DArr(network);

			for(int i = 0; i < section.length - 1; i++) {
				for(int j = i + 1; j < section.length; j++) {
					if (section[i][j] > max || section[i][j] <= min) {
						section[i][j] = 0;
						section[i][j] = 0;
					}
				}
			}

			scheduleLayers.add(new Layer(section));
			layers--;
			size /= 2;
		}
		// Get the final layer with any remaining values
		int[][] layer = this.clone2DArr(this.network);

		for(int i = 0; i < network.length - 1; i++) {
			for(int j = i + 1; j < network.length; j++) {
				if (network[i][j] > size) {
					layer[i][j] = 0;
					layer[j][i] = 0;
				}
			}
		}

		scheduleLayers.add(new Layer(layer));
		Schedule s = new Schedule(names);
		// Interleave the layers
		for(int i = scheduleLayers.size() - 1; i >= 0; i--) {
			int increment = 0;
			ArrayList<ArrayList<Integer[]>> days = scheduleLayers.get(i).getSchedule().getDays();
			int day = 0;
			while (day < days.size() || increment < s.size()) {
				if (increment >= s.size()) {
					s.add(days.get(day));
					day++;
					increment++;
				} else {
					s.add(increment, days.get(day % days.size()));
					increment += 2;
					day++;
				}
			}
		}

		return s;
	}

	/**
	 * Find the maximum strain on a relationship
	 *
	 * @return Schedule weight
	 */
	public int getScheduleWeight(Schedule s) {
		ArrayList<ArrayList<Integer[]>> days = s.getDays();
		int[][] weights = clone2DArr(network);
		int max = getMax(weights);
		int iterations = 2;
		while (iterations > 0) {
			for (ArrayList<Integer[]> day : days) {
				for (int i = 0; i < weights.length - 1; i++) {
					for (int j = i + 1; j < weights.length; j++) {
						weights[i][j] += network[i][j];
						weights[j][i] = weights[i][j];
					}
				}
				for (Integer[] meetup : day) {
					weights[meetup[0]][meetup[1]] = network[meetup[0]][meetup[1]];
				}
				int newMax = getMax(weights);
				if (newMax > max) {
					max = newMax;
				}
			}
			iterations--;
		}

		return max;
	}

	/**
	 * Clone square network arrays
	 *
	 * @return Deep clone of array
	 */
	private int[][] clone2DArr(int[][] arr) {
		int len = arr.length;
		int[][] clone = new int[len][];
		for(int i = 0; i < len; i++) {
			clone[i] = new int[len];

			for(int j = 0; j < len; j++) {
				clone[i][j] = arr[i][j];
			}
		}

		return clone;
	}

	/**
	 * Get a pretty string for output
	 *
	 * @return Pretty string
	 */
	public String toString() {
		String str = "";
		int len = network.length;
		for(int i = 0; i < len; i++) {
			int[] var6 = network[i];

			for(int j = 0; j < len; j++) {
				int var9 = var6[j];
				str = str + var9 + "  ";
			}

			str = str + "\n";
		}

		return str;
	}
}
