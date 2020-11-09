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
import java.util.Scanner;

/**
 * Some thoughts: make two arrays containing their hero picks, then loop through
 * them to add to the object (function) iterate through dict, if value =
 * Radiant, do X, if Dire, do Y.
 * Could return a 2D array containing heroes
 * Rework it and have the iteration done within the checkBanorPick or BanOrPick method, return map there, ultimately return 2d array
 * 
 * @author Jordan
 *
 */
public class Drafter {

	public final static int MAX_HEROES_ON_TEAM = 5;
	public final static int MAX_DRAFT_ROUNDS = 24;
	public static String[] radiantHeroes = new String[5];
	public static String[] direHeroes = new String[5];

	public static void main(String[] args) {
		Team radiant = new Team();
		radiant.setSide(Side.RADIANT);
		Team dire = new Team();
		dire.setSide(Side.DIRE);
		Map<String, Boolean> heroes = new TreeMap<String, Boolean>();
		Scanner userInput = new Scanner(System.in);
		int[] bans = { 1, 2, 3, 4, 9, 10, 11, 12, 13, 14, 19, 20, 21, 22 }; // notes which rounds of the draft are bans
		heroes = createHeroes(); // read in from file

		Map<Integer, String> turnOrder = determineDraftOrder(userInput);
		int count = 1; // controls the below loop

		do {
			String banOrPick = checkBanOrPick(count, bans);
			String teamForTurn = turnOrder.get(count);
			if (banOrPick == "Ban") {
				System.out.println(teamForTurn.toUpperCase() + ", please enter the hero you wish to ban.");
			} else {
				System.out.println(teamForTurn.toUpperCase() + ", please enter the hero you wish to pick.");
			}
			heroes = banOrPickHero(heroes, userInput, banOrPick, teamForTurn);
			count++;
		} while (count <= MAX_DRAFT_ROUNDS);

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
	}

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

			}
			turnOrder.put(count, teamToAdd);
			count++;
		} while (count <= MAX_DRAFT_ROUNDS);
		return turnOrder;
	}

	/**
	 * Reads in every possible hero from file, adding it to a Map with a Key of the
	 * hero name and a Value of a Boolean. True = the hero is available to pick,
	 * False = the hero is not available to pick.
	 * 
	 * @return Map<String, Boolean> containing all heroes.
	 */
	public static Map<String, Boolean> createHeroes() {
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
			System.out.println("Cannot find hero file to read.");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return heroes;
	}

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
			}
		}
		return banOrPick;

	}

	/**
	 * Simulates the banning of a hero, making it unable to be used for picking or
	 * banning in the future.
	 * 
	 * Change iteration to be within this function and maybe have a sub function where the heroes are banned?
	 * Ultimately return a 2d array?
	 * @return heroToBan, the hero to be banned.
	 */
	public static Map<String, Boolean> banOrPickHero(Map<String, Boolean> heroes, Scanner userInput, String banOrPick, String team) {
		String heroToPick = "";
		Boolean allowedHero = false;
		while (allowedHero == false) {
			try {
				heroToPick = userInput.nextLine();
				Boolean status = heroes.get(heroToPick.toUpperCase());

				if (status == true) { // if hero is in the pool, ban it and exit look
					heroes.put(heroToPick.toUpperCase(), false);
					if (banOrPick == "Pick" && team == "Radiant") {
						System.out.println("Radiant has picked... " +heroToPick.toUpperCase());
					} else if (banOrPick == "Pick" && team == "Dire") {
						System.out.println("Dire has picked... " +heroToPick.toUpperCase());
					}
					allowedHero = true;
				} else {
					System.err.println(heroToPick.toUpperCase() + " has been banned or picked. Please select another.");
				}

			} catch (InputMismatchException invalidInput) {
				System.out.println("Invalid input, please try again.");
			} catch (NullPointerException e) {
				System.err.println("Invalid hero entered, try again.");
			}
		}
		return heroes;

	}

	public static void updateTeamObject(Team teamToUpdate, String[] heroes) {

		teamToUpdate.setFirstHero(heroes[0]);
		teamToUpdate.setSecondHero(heroes[1]);
		teamToUpdate.setThirdHero(heroes[2]);
		teamToUpdate.setFourthHero(heroes[3]);
		teamToUpdate.setFifthHero(heroes[4]);

	}
}
