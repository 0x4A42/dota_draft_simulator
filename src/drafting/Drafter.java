package drafting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Map;
import java.util.Scanner;

public class Drafter {

	public static void main(String[] args) {
		Team radiant = new Team();
		radiant.setSide(Side.RADIANT);
		Team dire = new Team();
		dire.setSide(Side.DIRE); 
		Map<String, Boolean> heroes = new TreeMap<String, Boolean>();
		heroes = createHeroes();
		int count = 0;
		do {
			System.out.println(banHero(heroes) +" has been banned.");
			System.out.println(heroes);
			count++;
			
		} while (count < 3);
		
		
	}

	/**
	 * Reads in every possible hero from file, adding it to a Map with a Key of the hero name and a Value of a Boolean.
	 * True = the hero is available to pick, False = the hero is not available to pick.
	 * @return Map<String, Boolean> containing all heroes.
	 */
	public static Map<String, Boolean> createHeroes(){
		Map<String, Boolean> heroes = new TreeMap<String, Boolean>();
		
		try {
			File heroesToRead = new File("dota_heroes.txt");
			FileReader readHeroes = new FileReader(heroesToRead);
			BufferedReader bufferedReadHeroes = new BufferedReader(readHeroes);
			String line = bufferedReadHeroes.readLine();
			while (line != null) {
				heroes.put(line, true);
				line = bufferedReadHeroes.readLine();
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
	 * Simulates the banning of a hero, making it unable to be used for picking or banning in the future.
	 * 
	 * @return heroToBan, the hero to be banned.
	 */
	public static String banHero(Map<String, Boolean> heroes) {
		String heroToBan = "";
		Scanner getHero = new Scanner(System.in);
		heroToBan = getHero.next();
		Boolean allowedBan = false;
		while(allowedBan == false) {
			System.out.println("Please enter the hero you wish to ban.");
			heroToBan = getHero.next();
			Boolean status = heroes.get(heroToBan);
			if (status == true) {
				heroes.put(heroToBan, false);
				allowedBan = true;
			}
		}
		getHero.close(); // tidy resources
		return heroToBan;
		
	}
}
