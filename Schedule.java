/**
 * Status	Finished
 * Last Update	01/04/25
 * Submitted	N/A
 * Comment	All code is my own original work
 * 
 * @author	Alaina Iris
 * @version	2025.01.04
 */

import java.util.ArrayList;

public class Schedule {
	private ArrayList<ArrayList<Integer[]>> schedule;
	private String[] names;

	/**
	 * Create a new schedule
	 */
	public Schedule() {
		schedule = new ArrayList<ArrayList<Integer[]>>();
	}

	/**
	 * Create a new schedule with names
	 *
	 * @param names Names of individuals for printing
	 */
	public Schedule(String[] names) {
		schedule = new ArrayList<ArrayList<Integer[]>>();
		this.names = names;
	}

	/**
	 * Get the days planned in the schedule
	 *
	 * @return Schedule days
	 */
	public ArrayList<ArrayList<Integer[]>> getDays() {
		return schedule;
	}

	/**
	 * Add a new day to the schedule
	 *
	 * @param day Day to add
	 */
	public void add(ArrayList<Integer[]> day) {
		schedule.add(day);
	}

	/**
	 * Add a new day to the schedule at a given index
	 *
	 * @param index Where to add day
	 * @param day Day to add
	 */
	public void add(int index, ArrayList<Integer[]> day) {
		schedule.add(index, day);
	}

	/**
	 * Get the size of the schedule
	 *
	 * @return Schedule size
	 */
	public int size() {
		return schedule.size();
	}

	/**
	 * Pretty String for schedule
	 *
	 * @return Pretty Schedule
	 */
	public String toString() {
		String str = "";
		int dayNumber = 1;
		for (ArrayList<Integer[]> day : schedule) {
			str = str + "\nDAY #" + dayNumber + ":";
			for (Integer[] meetup : day) {
				String first;
				String second;
				if (names != null) {
					first = names[meetup[0]];
					second = names[meetup[1]];
				} else {
					first = "" + meetup[0];
					second = "" + meetup[1];
				}
				str = str + "\n\t" + first
					+ " and " + second + " meet";
			}
			dayNumber++;
		}
		return str;
	}
}
