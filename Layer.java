/**
 * Purpose	Schedule for meetups
 * Status	Barely Started
 * Last Update	01/03/25
 * Submitted	N/A
 * Comment	All code is my own original work
 *
 * @author	Alaina Iris
 * @version	2025.01.03
 */

import java.util.ArrayList;
import java.util.HashMap;

public class Layer {
	private int[][] relation;
	private Schedule days;
	private int[][] colors;

	/**
	 * Create a new layer
	 */
	public Layer(int[][] relation) {
		this.relation = relation;
		colors = initializeColors();
		makeMap();
		days = optimizeSchedule();
	}

	/**
	 * Get the schedule for this layer
	 *
	 * @return Layer schedule
	 */
	public Schedule getSchedule() {
		return days;
	}

	/**
	 * Provide a matrix for the cells in the network matrix to color in
	 *
	 * @return Uncolored, unweighted graph
	 */
	private int[][] initializeColors() {
		int[][] colors = new int[relation.length][relation.length];
		for (int i = 0; i < relation.length - 1; i++) {
			for (int j = 0; j < relation.length; j++) {
				if (relation[i][j] > 0) {
					colors[i][j] = -1;
					colors[j][i] = -1;
				}
			}
		}
		return colors;
	}

	/**
	 * Create the edge-coloring of the network
	 */
	private void makeMap() {
		ArrayList<Integer[]> edges = getEdges();
		int i = 0;
		while (!edges.isEmpty()) {
			Integer[] edge = edges.get(0);
			Fan f = new Fan(colors, edge[0], edge[1]);
			f.invertCDPath();
			edges.remove(0);

		}
	}

	/**
	 * Get a list of relationships an individual has
	 *
	 * @param i Individual to find relationships of
	 * @return List of relationships for individual
	 */
	private ArrayList<Integer> getAdjacent(int i) {
		ArrayList<Integer> adj = new ArrayList<Integer>();
		for (int j = 0; j < relation.length; j++) {
			if (relation[i][j] > 0) {
				adj.add(j);
			}
		}
		return adj;
	}

	/**
	 * Get a list of all relationships
	 *
	 * @return List of relationships
	 */
	private ArrayList<Integer[]> getEdges() {
		ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
		for (int i = 0; i < relation.length - 1; i++) {
			for (int j = i + 1; j < relation.length; j++) {
				if (relation[i][j] > 0) {
					edges.add(new Integer[] {i, j});
				}
			}
		}
		return edges;
	}

	/**
	 * Get the days mapped to the maximum weight of a relationship on that day
	 *
	 * @return List of days mapped with maximum strain
	 */
	private HashMap<Integer, Integer> getDays() {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < relation.length - 1; i++) {
			for (int j = i + 1; j < relation.length; j++) {
				if (colors[i][j] != 0) {
					int max;
					Integer val = map.get(colors[i][j]);
					if (val == null) {
						max = 0;
					} else {
						max = val;
					}
					if (max < relation[i][j]) {
						map.put(colors[i][j], relation[i][j]);
					}
				}
			}
		}
		return map;
	}

	/**
	 * Optimize the schedule to minimize strain
	 *
	 * @return optimized schedule with minimized strain
	 */
	private Schedule optimizeSchedule() {
		HashMap<Integer, Integer> days = getDays();
		HashMap<Integer, Integer> waits = new HashMap<Integer, Integer>();
		ArrayList<Integer> order = new ArrayList<Integer>();
		boolean minimum = false;
		for (Integer i : days.keySet()) {
			order.add(i);
		}
		while (!minimum) {
			int[] maximumWait = getMaximumWait(order, days);
			// Attempt reduction
			if (maximumWait[3] - maximumWait[2] <= 2) {
				minimum = true;
			} else {
				int added = (maximumWait[2] + maximumWait[3] + 1) / 2 % order.size();
				order.add(added, maximumWait[1]);
				if (getMaximumWait(order, days)[0] > maximumWait[0]) {
					minimum = true;
					order.remove(added);
				}
			}
		}
		Schedule schedule = new Schedule();
		for (int i : order) {
			schedule.add(getDay(i));
		}
		return schedule;
	}

	/**
	 * Get the meetups in a day
	 *
	 * @param color The color of the day (from edge-coloring)
	 * @return List of meetups of the day
	 */
	private ArrayList<Integer[]> getDay(int color) {
		ArrayList<Integer[]> meetups = new ArrayList<Integer[]>();
		for (int i = 0; i < colors.length - 1; i++) {
			for (int j = i + 1; j < colors.length; j++) {
				if (colors[i][j] == color) {
					meetups.add(new Integer[] {i, j});
				}
			}
		}
		return meetups;
	}

	/**
	 * Get maximum strain, start index, and end index
	 *
	 * @param order Order of days
	 * @param days Map of days and their maximum strains
	 * @return When the occurrance of maximum strain happens in the order as [weight, day, start, end]
	 */
	private int[] getMaximumWait(ArrayList<Integer> order, HashMap<Integer, Integer> days) {
		int[] maximum = new int[4]; // weight, day, start, end
		for (int i = 0; i < order.size(); i++) {
			int day = order.get(i);
			int end = i + 1;
			while (order.get(end % order.size()) != day) {
				end++;
			}
			int weight = days.get(day) * (end - i);
			if (weight > maximum[0]) {
				maximum[0] = weight;
				maximum[1] = day;
				maximum[2] = i;
				maximum[3] = end;
			}
		}
		return maximum;
	}

	/**
	 * Get pretty string of colors
	 *
	 * @return Pretty string
	 */
	public String toString() {
		String str = "";
		for (int[] arr : colors) {
			for (int val : arr) {
				str = str + val + "  ";
			}
			str = str + "\n";
		}
		return str;
	}
}
