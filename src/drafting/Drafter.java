package drafting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @author Jordan
 *
 */
public class Drafter {

	public final static int MAX_DRAFT_ROUNDS = 24;
	public static ArrayList<String> radiantHeroes = new ArrayList<String>();
	public static ArrayList<String> direHeroes = new ArrayList<String>();

	public static void main(String[] args) {
		Team radiant = new Team();
		radiant.setSide(Side.RADIANT);
		Team dire = new Team();
		dire.setSide(Side.DIRE);
		Map<String, Boolean> heroes = new TreeMap<String, Boolean>();
		Scanner userInput = new Scanner(System.in);
		ReserveTimeThread radiantReserveTimeThread = new ReserveTimeThread();
		ReserveTimeThread direReserveTimeThread = new ReserveTimeThread();

		int[] bans = { 1, 2, 3, 4, 9, 10, 11, 12, 13, 14, 19, 20, 21, 22 }; // notes which rounds of the draft are bans
		heroes = createHeroesMap(); // read in from file
		Map<Integer, String> turnOrder = determineDraftOrder(userInput);
		int count = 1; // controls the below do while loop
		do {
			DraftTimeThread normalDraftTime = new DraftTimeThread(); // new thread every cycle, as will be interrupted.
			String banOrPick = checkBanOrPick(count, bans);
			String teamForTurn = turnOrder.get(count);
			if (banOrPick == "Ban") {
				System.out.println(teamForTurn.toUpperCase() + " - please enter the hero you wish to ban.");
			} else {
				System.out.println(teamForTurn.toUpperCase() + " - please enter the hero you wish to pick.");
			}

			normalDraftTime.startThread(); // start thread which tracks the initial 35 seconds of draft time

			Timer checkDraftTime = new Timer(); // timer to schedule checking draft time function
			checkDraftTime.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					if (normalDraftTime.draftTime == 0) {
						normalDraftTime.interrupt();
						if (teamForTurn == "Radiant") {
							if (radiantReserveTimeThread.getHasStarted() == false) {
								radiantReserveTimeThread.startThread();
							} else {
								radiantReserveTimeThread.resume();
							}

						} else if (teamForTurn == "Dire") {
							if (direReserveTimeThread.getHasStarted() == false) {
								direReserveTimeThread.startThread();
							} else {
								direReserveTimeThread.resume();
							}
						}
						checkDraftTime.cancel();
					} // end of if
				} // end of run()
			}, 0, 1000);

			heroes = banOrPickHero(heroes, userInput, banOrPick, teamForTurn);
			count++;
			// only check which reserve time thread to suspend if there is a chance it is
			// active
			if (normalDraftTime.draftTime == 0) {
				determineThreadToSuspend(teamForTurn, radiantReserveTimeThread, direReserveTimeThread);
			}

			normalDraftTime.interrupt();
		} while (count <= MAX_DRAFT_ROUNDS);

		updateTeamObject(radiantHeroes, radiant);
		updateTeamObject(direHeroes, dire);

	}

	/**
	 * Prompts the user for which team will have first pick (and ultimately decide
	 * the drafting order).
	 * 
	 * @param userInput, Scanner used to capture user input.
	 * @return firstPick, a String detailing which team will go first.
	 */
	public static Map<Integer, String> determineDraftOrder(Scanner userInput) {
		Map<Integer, String> turnOrder = new TreeMap<Integer, String>();
		String firstPick = "";
		Boolean validInput = false;
		do {
			System.out.println("Please enter the side who will go first.");
			String checkInput = userInput.next();
			if (checkInput.equalsIgnoreCase("Radiant") || checkInput.equalsIgnoreCase("Dire")) {
				firstPick = checkInput;
				turnOrder = generateDraftOrderMap(firstPick);
				validInput = true;
			}
		} while (validInput == false);

		System.out.println(firstPick.toUpperCase() + " will have first pick.");
		userInput.nextLine(); // clear scanner for future use
		return turnOrder;
	}// end of determineDraftOrder()

	/**
	 * Generates a Map which will contain the draft order.
	 * 
	 * @param firstTeam, a String representing the first team to pick.
	 * @return
	 */
	public static Map<Integer, String> generateDraftOrderMap(String firstTeam) {
		Map<Integer, String> turnOrder = new TreeMap<Integer, String>();
		int count = 1;
		String first = "";
		String second = "";
		if (firstTeam.equalsIgnoreCase("Radiant")) {
			first = "Radiant";
			second = "Dire";
		} else if (firstTeam.equalsIgnoreCase("Dire")) {
			first = "Dire";
			second = "Radiant";
		}

		do {
			String teamToAdd = null;
			switch (count) { // determine which side based on a pre-set drafting order
			case 1:
			case 3:
			case 5:
			case 7:
			case 9:
			case 11:
			case 13:
			case 16:
			case 18:
			case 19:
			case 21:
			case 23:
				teamToAdd = first;
				break;
			case 2:
			case 4:
			case 6:
			case 8:
			case 10:
			case 12:
			case 14:
			case 15:
			case 17:
			case 20:
			case 22:
			case 24:
				teamToAdd = second;

			} // end of switch
			turnOrder.put(count, teamToAdd);
			count++;
		} while (count <= MAX_DRAFT_ROUNDS);
		return turnOrder;
	} // end of generateDraftOrderMap()

	/**
	 * Reads in every possible hero from file, adding it to a Map with a Key of the
	 * hero name and a Value of a Boolean. True = the hero is available to pick,
	 * False = the hero is not available to pick.
	 * 
	 * @return Map<String, Boolean> containing all heroes.
	 */
	public static Map<String, Boolean> createHeroesMap() {
		Map<String, Boolean> heroes = new TreeMap<String, Boolean>();

		try {
			File heroesToRead = new File("dota_heroes.txt");
			FileReader readHeroes = new FileReader(heroesToRead);
			BufferedReader bufferedReadHeroes = new BufferedReader(readHeroes);
			String line = bufferedReadHeroes.readLine(); // read first line

			while (line != null) {
				heroes.put(line, true); // add to Map, K = hero name, V = true
				line = bufferedReadHeroes.readLine(); // get next line
			}

			bufferedReadHeroes.close(); // tidy resources
			readHeroes.close(); // tidy resources
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find hero file to read.");
		} catch (IOException e) {
			e.printStackTrace();
		} // end of try catch

		return heroes;
	} // end of createHeroesMap()

	/**
	 * 
	 * @param count
	 * @param bans
	 * @return
	 */
	public static String checkBanOrPick(int count, int[] bans) {
		String banOrPick = "";

		for (int i : bans) {
			if (i == count) {
				banOrPick = "Ban";
				break;
			} else {
				banOrPick = "Pick";
			} // end of if else
		} // end of enhanced for
		return banOrPick;

	} // end of checkBanOrPick()

	/**
	 * Simulates the banning of a hero, making it unable to be used for picking or
	 * banning in the future.
	 * 
	 * Change iteration to be within this function and maybe have a sub function
	 * where the heroes are banned? Ultimately return a 2d array?
	 * 
	 * @return heroToBan, the hero to be banned.
	 */
	public static Map<String, Boolean> banOrPickHero(Map<String, Boolean> heroes, Scanner userInput, String banOrPick,
			String team) {
		String heroToPick = "";
		Boolean allowedHero = false;
		int counter = 30;

		while (allowedHero == false) {
			try {
				heroToPick = userInput.nextLine();
				Boolean status = heroes.get(heroToPick.toUpperCase());

				if (status == true) { // if hero is in the pool, ban or pick it.
					heroes.put(heroToPick.toUpperCase(), false);
					if (banOrPick == "Pick" && team == "Radiant") {
						System.out.println("Radiant has picked " + heroToPick.toUpperCase());
						radiantHeroes.add(heroToPick);
					} else if (banOrPick == "Pick" && team == "Dire") {
						System.out.println("Dire has picked " + heroToPick.toUpperCase());
						direHeroes.add(heroToPick);
					} else if (banOrPick == "Ban") {
						System.out.println(heroToPick.toUpperCase() + " has been banned by " + team + ".");
					}
					allowedHero = true; // exit loop
				} else {
					System.err.println(
							heroToPick.toUpperCase() + " has been banned or picked. Please select a different hero.");
				} // end of if else

			} catch (InputMismatchException invalidInput) {
				System.out.println("Invalid input, please try again.");
			} catch (NullPointerException e) {
				System.err.println("Invalid hero entered, try again.");
			} // end of try catch
		} // end of while
		return heroes;

	} // end of banOrPickHero

	/**
	 * Determines which reserve time Thread to suspend.
	 * 
	 * @param teamForTurn              the team whose turn in the draft it is
	 * @param radiantReserveTimeThread the thread representing Radiant's reserve
	 *                                 time
	 * @param direReserveTimeThread    the thread representing Dire's reserve time
	 */
	public static void determineThreadToSuspend(String teamForTurn, ReserveTimeThread radiantReserveTimeThread,
			ReserveTimeThread direReserveTimeThread) {
		if (teamForTurn == "Radiant") {
			radiantReserveTimeThread.suspend();
			;
		} else if (teamForTurn == "Dire") {
			direReserveTimeThread.suspend();
		}

	}

	/**
	 * If a team runs out of time in a pick, they randomly pick a hero. This method
	 * grabs all keys from the heroes Map and randomly picks one, ensuring that it
	 * is still in play by checking the boolean before ultimately returning a
	 * randomly picked hero that is still in play.
	 * 
	 * @param heroes a map containing all heroes
	 * @return randomHero a String, containing the name of the randomly generated
	 *         hero.
	 */
	public static String randomHero(Map<String, Boolean> heroes) {
		String randomHero = "";
		Random generator = new Random();
		do {
			Object[] values = heroes.keySet().toArray();
			randomHero = (String) values[generator.nextInt(values.length)];
		} while (heroes.get(randomHero) == false);
		System.out.println(randomHero);
		return randomHero;
	}

	/***
	 * Updates the Team object to set all of the heroes picked.
	 * 
	 * @param heroes       the heroes picked for the relevant team
	 * @param teamToUpdate the team to update.
	 */
	public static void updateTeamObject(ArrayList<String> heroes, Team teamToUpdate) {

		teamToUpdate.setFirstHero(heroes.get(0));
		teamToUpdate.setSecondHero(heroes.get(1));
		teamToUpdate.setThirdHero(heroes.get(2));
		teamToUpdate.setFourthHero(heroes.get(3));
		teamToUpdate.setFifthHero(heroes.get(4));

	} // end of updateTeamObject()
} // end of class
