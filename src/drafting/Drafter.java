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
 * This class will run the Dota Draft Simulator programme.
 * 
 * Each team will take it in turn to either uniquely ban or pick a hero from a
 * pool of 119.
 * 
 * Either entering 'random' when prompted for a hero, or failing to pick a hero
 * within the alloted time (35 seconds of general time, which replenishes with
 * every new pick/ban and a one-time bank of 130 seconds for each team) will
 * randomly select a hero from the remaining pool.
 *
 */
public class Drafter {

	public final static int MAX_DRAFT_ROUNDS = 24; // there is set amount of rounds of a draft
	public static boolean needToRandom = false; // tracks if the team has run out of both draft and reserve time

	public static void main(String[] args) {
		Team radiant = new Team();
		radiant.setSide(Side.RADIANT);
		Team dire = new Team();
		dire.setSide(Side.DIRE);
		Map<String, Boolean> heroes = new TreeMap<String, Boolean>(); // used to store all heroes, read in from file
		Scanner userInput = new Scanner(System.in);
		ReserveTimeThread radiantReserveTimeThread = new ReserveTimeThread();
		ReserveTimeThread direReserveTimeThread = new ReserveTimeThread();
		ArrayList<String> radiantHeroes = new ArrayList<String>(); // to store confirmed heroes for the Radiant
		ArrayList<String> direHeroes = new ArrayList<String>(); // to store confirmed heroes for the Dire
		int[] bans = { 1, 2, 3, 4, 9, 10, 11, 12, 13, 14, 19, 20, 21, 22 }; // which rounds of the draft are bans
		heroes = createHeroesMap(); // populate Map from file
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
			reserveTimeStarter(teamForTurn, normalDraftTime, radiantReserveTimeThread, direReserveTimeThread);
			determineWhenToRandom(teamForTurn, normalDraftTime, radiantReserveTimeThread, direReserveTimeThread);
			heroes = banOrPickHero(heroes, userInput, banOrPick, teamForTurn, normalDraftTime, radiantHeroes,
					direHeroes);

			// only check which reserve time thread to suspend if it is active
			if (normalDraftTime.getDraftTime() == 0) {
				determineThreadToSuspend(teamForTurn, radiantReserveTimeThread, direReserveTimeThread);
			}
			normalDraftTime.interrupt();
			count++;
		} while (count <= MAX_DRAFT_ROUNDS);

		// adds the heroes within the ArrayLists to the team objects
		updateTeamObject(radiantHeroes, radiant);
		updateTeamObject(direHeroes, dire);

		System.out.println("----------------------");
		System.out.println("The draft has finished.");
		radiant.printHeroes();
		dire.printHeroes();
		System.out.println("----------------------");

	}

	/**
	 * Prompts the user for which team will have first pick (and ultimately decide
	 * the drafting order).
	 * 
	 * @param userInput Scanner used to capture user input.
	 * @return firstPick a String detailing which team will go first.
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
	 * Generates a Map which will contain the draft order (K = round number, V =
	 * Team).
	 * 
	 * @param firstTeam, a String representing the first team to pick.
	 * @return turnOrder the order of the draft
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
	 * @return Map<String, Boolean> A Map containing all heroes and their state of
	 *         play (K = hero name, V = true).
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
	 * This method checks if the current turn of the draft is a ban or pick.
	 * 
	 * @param count an int representing the round of the draft
	 * @param bans  an int array containing all rounds which are ban rounds
	 * @return banOrPick a String set to "Ban" or "Pick", representing the draft
	 *         round type
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
	 * /** Simulates the banning of a hero, making it unable to be used for picking
	 * or banning in the future.
	 * 
	 * Change iteration to be within this function and maybe have a sub function
	 * where the heroes are banned? Ultimately return a 2d array?
	 * 
	 * @param heroes          a map containing the heroes and their current state of
	 *                        play (K = hero name, V = boolean representing state of
	 *                        play [T = available, F = unavailable])
	 * @param userInput       a Scanner to capture user input
	 * @param banOrPick       a String representing if this turn of the draft is for
	 *                        a ban or pick
	 * @param team            the team whose turn it is
	 * @param normalDraftTime the thread of 35 seconds of normal draft time
	 * @param radiantHeroes   the thread of the Radiant's reserve time
	 * @param direHeroes      the thread of the Dire's reserve time
	 * @return heroToPick the hero to be picked or banned.
	 */
	public static Map<String, Boolean> banOrPickHero(Map<String, Boolean> heroes, Scanner userInput, String banOrPick,
			String team, DraftTimeThread normalDraftTime, ArrayList<String> radiantHeroes,
			ArrayList<String> direHeroes) {
		String heroToPick = "";
		Boolean allowedHero = false;

		while (allowedHero == false) {
			try {
				heroToPick = userInput.nextLine();
				if (needToRandom == true || heroToPick.equalsIgnoreCase("RANDOM")) {
					if (needToRandom == true) {
						System.err.println("Sorry, you ran out of time and your pick will be randomed.");
					}
					heroToPick = randomHero(heroes);
					needToRandom = false; // Reset to false as to not random by default in subsequent pick(s)
				}

				Boolean status = heroes.get(heroToPick.toUpperCase());
				if (status == true) { // if hero is in the pool, ban or pick it.
					heroes.put(heroToPick.toUpperCase(), false);

					if (banOrPick == "Pick") {
						System.out
								.println(heroToPick.toUpperCase() + " has been picked by " + team.toUpperCase() + ".");
					} else if (banOrPick == "Ban") {
						System.out
								.println(heroToPick.toUpperCase() + " has been banned by " + team.toUpperCase() + ".");
					}

					if (banOrPick == "Pick" && team == "Radiant") {
						radiantHeroes.add(heroToPick.toUpperCase());
					} else if (banOrPick == "Pick" && team == "Dire") {
						direHeroes.add(heroToPick.toUpperCase());
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
		return heroes; // returns updated value as a hero now be unavailable

	} // end of banOrPickHero

	/**
	 * This method runs a task every second to check if the normal draft time has
	 * ended. If so, it will run the reserve time thread for the respective team. It
	 * will first check if the thread has started previously, as a different method
	 * will be called depending on if the thread is being started or resumed.
	 * 
	 * @param teamForTurn              the team whose turn it is
	 * @param normalDraftTime          the thread representing the normal 35 seconds
	 *                                 of draft time
	 * @param radiantReserveTimeThread the thread representing the 130 seconds of
	 *                                 reserve time for the Radiant
	 * @param direReserveTimeThread    the thread representing the 130 seconds of
	 *                                 reserve time for the Dire
	 */
	public static void reserveTimeStarter(String teamForTurn, DraftTimeThread normalDraftTime,
			ReserveTimeThread radiantReserveTimeThread, ReserveTimeThread direReserveTimeThread) {
		Timer checkDraftTime = new Timer(); // timer to schedule checking draft time function
		checkDraftTime.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (normalDraftTime.getDraftTime() == 0) {
					normalDraftTime.interrupt();
					if (teamForTurn == "Radiant") {
						if (radiantReserveTimeThread.getHasStarted() == false) {
							radiantReserveTimeThread.startThread();
						} else {
							radiantReserveTimeThread.resume();
						} // end of else

					} else if (teamForTurn == "Dire") {
						if (direReserveTimeThread.getHasStarted() == false) {
							direReserveTimeThread.startThread();
						} else {
							direReserveTimeThread.resume();
						} // end of else
					} // end of else if
					checkDraftTime.cancel();
				} // end of if
			} // end of run()
		}, 0, 1000);
	} // end of reserveTimeStarter

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
		} else if (teamForTurn == "Dire") {
			direReserveTimeThread.suspend();
		}

	} // end of determineThreadToSuspend()

	/**
	 * Determines when to roll a random hero with regard to time (when reserve time
	 * is 0 and normal draft time is 0)
	 * 
	 * This will run a Timer method that will check, every second, if both the
	 * normal draft time and the team's reserve time == 0. If so, the team has ran
	 * out of time and no matter what they enter, it will not be accepted and they
	 * will instead have a hero randomly selected.
	 * 
	 * @param teamForTurn              the team whose turn it is
	 * @param normalDraftTime          the thread representing the normal 35 seconds
	 *                                 of draft time
	 * @param radiantReserveTimeThread the thread representing Radiant's reserve
	 *                                 time
	 * @param direReserveTimeThread    the thread representing Dire's reserve time
	 */
	public static void determineWhenToRandom(String teamForTurn, DraftTimeThread normalDraftTime,
			ReserveTimeThread radiantReserveTimeThread, ReserveTimeThread direReserveTimeThread) {
		Timer checkDraftTimeToRandom = new Timer(); // timer to schedule checking draft time function
		checkDraftTimeToRandom.scheduleAtFixedRate(new TimerTask() {

			public void run() {
				if (normalDraftTime.getDraftTime() == 0) {
					if (teamForTurn == "Radiant") {
						if (radiantReserveTimeThread.getReserveTime() == 0) {
							needToRandom = true;
							radiantReserveTimeThread.suspend();
							checkDraftTimeToRandom.cancel();
						}
					} else if (teamForTurn == "Dire") {
						if (direReserveTimeThread.getReserveTime() == 0) {
							needToRandom = true;
							direReserveTimeThread.suspend();
							checkDraftTimeToRandom.cancel();
						}
					} // end of else if

				} // end of if

			} // end of run()
		}, 0, 1000);

	}// end of determineWhenToRandom

	/**
	 * If a team runs out of time in a pick, they randomly pick a hero. This method
	 * grabs all keys from the heroes Map and randomly picks one, ensuring that it
	 * is still in play by checking the boolean before ultimately returning a
	 * randomly picked hero that is still in play.
	 * 
	 * @param heroes a map containing all heroes
	 * @return randomHero a String representing the name of the randomly generated
	 *         hero.
	 */
	public static String randomHero(Map<String, Boolean> heroes) {
		String randomHero = "";
		Random generator = new Random();
		do {
			Object[] values = heroes.keySet().toArray();
			randomHero = (String) values[generator.nextInt(values.length)];
		} while (heroes.get(randomHero) == false);
		return randomHero;
	} // end of randomHero

	/***
	 * Updates the Team object to set all of the heroes picked for each team.
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
