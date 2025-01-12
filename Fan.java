/**
 * Purpose	Fan structure for graph coloring
 * Status	Finished
 * Last Update	01/04/25
 * Submitted	N/A
 * Comment	All code is my own original work
 * Comment	Solution credit goes to Jayadev Misra and David Gries
 * 		Their 1992 paper on the implemented algorithm is below
 * 
 * Readings	https://www.cs.utexas.edu/~misra/psp.dir/vizing.pdf
 *
 * @author	Alaina Iris
 * @version	2025.01.04
 */

import java.util.ArrayList;

public class Fan {
	private int[][] colorMap;
	private int root;
	private int c;
	private int d;
	private ArrayList<Integer> children;

	/**
	 * Generate the fan from given parameters.
	 *
	 * @param  colorMap What our color map is. Mutable
	 * @param  root The root of the connection (first edge point)
	 * @param  firstChild The first child node of the root (second edge point)
	 */
	public Fan(int[][] colorMap, int root, int firstChild) {
		this.colorMap = colorMap;
		this.root = root;
		children = new ArrayList<Integer>();
		children.add(firstChild);
		buildFan();
		c = findC();
		d = findD();
	}

	/**
	 * Apply the inversion of the cd-x path defined in the Misra and
	 * Gries edge coloring algorithm
	 */
	public void invertCDPath() {
		ArrayList<Integer> path = new ArrayList<Integer>();
		path.add(root);
		boolean isC = true;
		int pos = root;
		do {
			pos = findConnection(isC ? c : d, pos);
			if (pos >= 0 && !path.contains(pos)) {
				path.add(pos);
			} else {
				break;
			}
			isC = !isC;
		} while (pos >= 0);
		isC = false;
		pos = root;
		do {
			pos = findConnection(isC ? c : d, pos);
			if (pos >= 0 && !path.contains(pos)) {
				path.add(0, pos);
			} else {
				break;
			}
			isC = !isC;
		} while (pos >= 0);
		
		for (int i = 0; i < path.size() - 1; i++) {
			int v1 = path.get(i);
			int v2 = path.get(i + 1);
			int val = colorMap[v1][v2] == c ? d : c;
			colorMap[v1][v2] = val;
			colorMap[v2][v1] = val;
		}
		reverseIndex();
	}

	/**
	 * After we've inverted the cdx path, we reverse the maximal
	 * valid subfan of F, where a subfan is F[1:w] of F[1:k] and
	 * w is less than or equal to k
	 */
	private void reverseIndex() {
		int reverseIndex = findReverseCount();
		int[] colors = new int[reverseIndex];
		for (int i = 0; i < colors.length; i++) {
			colors[i] = colorMap[root][children.get((i + 1) % reverseIndex)];
		}
		for (int i = colors.length - 1; i >= 0; i--) {
			if (colors[i] == -1) {
				colors[i] = d;
				break;
			}
		}
		for (int i = 0; i < colors.length; i++) {
			colorMap[root][children.get(i)] = colors[i];
			colorMap[children.get(i)][root] = colors[i];
		}
	}

	/**
	 * Find the length for the maximal subfan starting at F[1]
	 * which can be rotated while preserving the valid coloring
	 *
	 * @return w for the F[1:w] subtree
	 */
	private int findReverseCount() {
		int count = 1; // Guaranteed that first child can be d
		while ( count < children.size() &&
			!colorsFrom(children.get(count - 1)).contains(
			colorMap[root][children.get(count)])) {
			count++;
		}
		return count;
	}

	/**
	 * Find what the edge of a certain color connects a given node to
	 *
	 * @param  color  Color of the edge
	 * @param  start  Node to find connection from
	 * @return match  Defaults to -1 if none found
	 */
	private int findConnection(int color, int start) {
		for (int i = 0; i < colorMap.length; i++) {
			if (colorMap[start][i] == color) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Find a maximal fan
	 * Maximal does not always mean maximum length!
	 */
	private void buildFan() {
		ArrayList<Integer> order = new ArrayList<Integer>();
		order.add(children.get(0));
		ArrayList<Integer> rootConnections = getConnectionsTo(root);
		boolean maximal = false;
		int last = order.get(0);
		while (!maximal) {
			maximal = true;
			for (int i : rootConnections) {
				ArrayList<Integer> usedLast = colorsFrom(last);
				if (!order.contains(i) &&
				    colorMap[root][i] > 0 &&
				    !usedLast.contains(colorMap[root][i])) {
				
					maximal = false;
				    	last = i;
					order.add(i);
				}
			}
		}
		children = order;
	}
	
	/**
	 * Set the children of the fan
	 *
	 * @param  children  Children to add
	 */
	private void setChildren(Integer[] children) {
		ArrayList<Integer> newChildren = new ArrayList<Integer>();
		for (int i : children) {
			newChildren.add(i);
		}
		this.children = newChildren;
	}

	/**
	 * Find the value for the c color (A color free on our root)
	 *
	 * @return Color for C
	 */
	private int findC() {
		ArrayList<Integer> used = colorsFrom(root);
		int color = 1;
		while (used.contains(color)) {
			color++;
		}
		return color;
	}

	/**
	 * Find the value of the d color (A color free on our LAST child)
	 *
	 * @return Color for D
	 */
	private int findD() {
		ArrayList<Integer> used = colorsFrom(children.get(children.size() - 1));
		int color = 1;
		while (used.contains(color) || color == c) {
			color++;
		}
		return color;
	}

	/**
	 * Return all of the colors used on a vertex
	 *
	 * @return List of used colors
	 */
	private ArrayList<Integer> colorsFrom(int i) {
		ArrayList<Integer> colors = new ArrayList<Integer>();
		for (int j = 0; j < colorMap.length; j++) {
			int color = colorMap[i][j];
			if (color > 0 & !colors.contains(color)) {
				colors.add(color);
			}
		}
		return colors;
	}

	/**
	 * Get list of individuals that someone has a relationship to
	 *
	 * @param i Individual to find relationships of
	 * @return List of relationships
	 */
	private ArrayList<Integer> getConnectionsTo(int i) {
		ArrayList<Integer> connections = new ArrayList<Integer>();
		for (int j = 0; j < colorMap.length; j++) {
			int color = colorMap[i][j];
			if (color > 0) {
				connections.add(j);
			}
		}
		return connections;
	}

	/**
	 * Pretty print color map
	 *
	 * @return pretty map
	 */
	public String toString() {
                String str = "";
                for (int[] arr : colorMap) {
                        for (int val : arr) {
                                str = str + val + "  ";
                        }
                        str = str + "\n";
                }
                return str;
	}
}
