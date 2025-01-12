/**
 * Assignment 	Data Structure and Algorithms Lab 0
 * Status	Barely Started
 * Last Update	01/04/25
 * Submitted	N/A
 * Comment	All code is my own original work
 * Attribution	This program is an implementation of a solution to the
 * 		"Polyamorous Scheduling" paper by Leszek Gąsieniec,
 * 		Benjamin Smith, and Sebastian Wild.
 * 		https://arxiv.org/pdf/2403.00465
 * 		
 * @author	Alaina Iris
 * @version	2025.01.04
 */
import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Driver {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Invalid use. Add -h to get help.");
		}
		else {
			switch(args[0]) {
				case "-e":
					exampleTest();
					break;
				case "-p":
					boolean data = false;
					if (args.length > 1 && args[1].contains("-d")) {
						data = true;
					}
					performanceTest(data);
					break;
				case "-i":
					if (args.length < 2) {
						System.out.println("Please provide a file!");
					} else {
						inputTest(args[1]);
					}
					break;
				case "-h":
					System.out.println("Network Scheduler:\n" +
							"java Driver [OPTIONS]\n" +
							"-e: Base Example\t" + "-p: Performance Test [-d turns output to datapoints]\t" + 
							"-i [file]: Use input file");
					break;
				default:
					System.out.println("Invalid use. Add -h to get help");
					break;
			}
		}
	}

	/**
	 * Get detailed run information for a single run
	 *
	 * @param network Network to use
	 * @param names Names to use
	 */
	public static void singleRun(int[][] network, String[] names) {
		Network n = new Network(network, names);
		System.out.println(
				"/**********************/\n" +
				"Network Matrix Provided:\n" +
				"/**********************/\n" +
				n
				);
		Schedule s = n.optimizedSchedule();
		System.out.println(
				"/**********************/\n" +
				"   Generated Schedule   \n" +
				"/**********************/\n" +
				s
				);
		int weight = n.getScheduleWeight(s);
		int lowBound = n.getMinimumRun();
		int maxBound = n.getApproximationLimit();
		System.out.println(
				"\n/**********************/\n" +
				"   General Statistics   \n" +
				"/**********************/\n" +
				"\nMaximum Strain Endured:\t\t" + weight +
				"\nOptimal Solution Lower Bound:\t" + lowBound +
				"\nApproximation Limit:\t\t" + maxBound +
				"\nHypothetical Performance:\t" +
				(int) ((double) weight / (double) lowBound * 100.0)
				+ "% of Lower Bound for Optimal Run"
				);
	}

	/**
	 * Perform the default example as a test
	 */
	public static void exampleTest() {
		int[][] example = new int[][] {
			{ 0, 40,  0, 80,  0, 40,  0,  0},
				{40,  0, 80,  0,  0,  0,  0,  0},
				{ 0, 80,  0, 16,  0,  0,  0,  0},
				{80,  0, 16,  0, 20,  0, 16,  0},
				{ 0,  0,  0, 20,  0, 40,  0, 80},
				{40,  0,  0,  0, 40,  0, 40,  0},
				{ 0,  0,  0, 16,  0, 40,  0,  0},
				{ 0,  0,  0,  0, 80,  0,  0,  0}
		};
		String[] names = new String[] {"Alice", "Belle", "Claire", "Daisy", "Emily", "Felix", "Grace", "Holly"};
		singleRun(example, names);
	}

	/**
	 * Run an increasing progression of tests and measure time.
	 *
	 * @param  dataset Produce datapoint output instead, for easy transfer
	 */
	public static void performanceTest(boolean datapoint) {
		long start;
		long end;
		String output;
		if (!datapoint) {
			System.out.println("100 runs of random networks with:\n");
		}
		for (int i = 10; i <= 250; i += 10) {
			start = System.currentTimeMillis();
			testRuns(100, i, 1000);
			end = System.currentTimeMillis();
			output = datapoint ? 
				("(" + i + "," + (end - start) + "),") : 
				(i + " people took " + (end - start) + " milliseconds");
			System.out.println(output);
		}
	}

	/**
	 * Attempt to run a test on a given CSV file
	 *
	 * @param  fileName Name of file to use
	 */
	public static void inputTest(String fileName) {
		try {
			File f = new File(fileName);
			Scanner s = new Scanner(f);
			String[] names = s.nextLine().split(",");
			int len = names.length;
			int[][] network = new int[len][len];
			int rowNum = 0;
			while (s.hasNextLine()) {
				String[] row = s.nextLine().split(",");
				if (row.length != len) {
					throw new IllegalArgumentException("Please ensure your file has even rows!");
				} else {
					for (int i = rowNum + 1; i < len; i++) {
						network[i][rowNum] = Integer.parseInt(row[i]);
						network[rowNum][i] = Integer.parseInt(row[i]);
					}
				}
				rowNum++;
			}
			s.close();
			singleRun(network, names);
		} catch (FileNotFoundException e) {
			System.out.println("Unable to access file, check that it exists and you have permissions to it.");
		}
	}

	public static void testRuns(int runs, int nodes, int maxEdge) {
		for (int i = runs - 1; i >= 0; i--) {
			int[][] networkMatrix = generateMatrix(nodes, maxEdge);
			Network n = new Network(networkMatrix, null);
			Schedule s = n.optimizedSchedule();
		}
	}

	public static int[][] generateMatrix(int nodes, int maximumEdge) {
		int[][] network = new int[nodes][nodes];
		Random rand = new Random();
		for (int i = 0; i < nodes - 1; i++) {
			for (int j = i + 1; j < nodes; j++) {
				if (rand.nextInt(100) > 94) {
					int weight = rand.nextInt(maximumEdge);
					network[i][j] = weight;
					network[j][i] = weight;
				}
			}
		}
		return network;
	}
}
