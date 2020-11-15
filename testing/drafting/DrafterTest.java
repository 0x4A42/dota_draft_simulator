package drafting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class DrafterTest {

	int MAX_DRAFT_ROUNDS;
	boolean needToRandom;
	Team radiant = new Team();
	Team dire = new Team();
	Map<String, Boolean> heroes = new TreeMap<String, Boolean>(); // used to store all heroes, read in from file
	Scanner userInput;
	ReserveTimeThread radiantReserveTimeThread;
	ReserveTimeThread direReserveTimeThread;
	DraftTimeThread normalDraftTime;
	ArrayList<String> radiantHeroes; // to store confirmed heroes for the Radiant
	ArrayList<String> direHeroes; // to store confirmed heroes for the Dire
	Map<Integer, String> turnOrder;
	int[] bans = { 1, 2, 3, 4, 9, 10, 11, 12, 13, 14, 19, 20, 21, 22 }; // which rounds of the draft are bans

	@BeforeEach
	void setUp() throws Exception {
		MAX_DRAFT_ROUNDS = 24;
		radiant.setSide(Side.RADIANT);
		dire.setSide(Side.DIRE);
		userInput = new Scanner(System.in);
		radiantHeroes = new ArrayList<String>();
		direHeroes = new ArrayList<String>();
		radiantReserveTimeThread = new ReserveTimeThread();
		direReserveTimeThread = new ReserveTimeThread();
		normalDraftTime = new DraftTimeThread();
		heroes = Drafter.createHeroesMap(); // populate Map from file
		needToRandom = false;
	}

	@AfterEach
	void tearDown() {
		radiantReserveTimeThread.suspend();
		direReserveTimeThread.suspend();
		normalDraftTime.interrupt();
		System.out.flush();
		needToRandom = false;
	}

	@Test
	/**
	 * Tests that when 'radiant' is entered, Radiant is the first value in the Map.
	 * Must enter 'radiant' to the prompt
	 */
	void testDetermineDraftOrderRadiant() {
		String expected = "Radiant";
		turnOrder = Drafter.determineDraftOrder(userInput);
		assertEquals(expected, turnOrder.get(1));
	}

	@Test
	/**
	 * Tests that when 'Dire' is entered, Dire is the third value in the Map. Must
	 * enter 'Dire' to the prompt
	 */
	void testDetermineDraftOrderDire() {
		String expected = "Dire";
		turnOrder = Drafter.determineDraftOrder(userInput);
		assertEquals(expected, turnOrder.get(3));
	}

	@Test
	/***
	 * Tests the consistent reading in from file, ensuring the same amount of values
	 * each time
	 */
	void testCreateHeroesMap() {
		int expected = 120;
		assertEquals(expected, heroes.size());
	}

	@Test
	/**
	 * Tests that the turnOrder consistently labels the ban phases as bans.
	 */
	void testBanOrPickTurnBan() {
		int count = 1;
		String expected = "Ban";
		String banOrPick = Drafter.checkBanOrPick(count, bans);
		assertEquals(expected, banOrPick);
	}

	@Test
	/**
	 * Tests that the turnOrder consistently labels the pick phases as picks.
	 */
	void testBanOrPickTurnPick() {
		int count = 6;
		String expected = "Pick";
		String banOrPick = Drafter.checkBanOrPick(count, bans);
		assertEquals(expected, banOrPick);
	}

	@Test
	/**
	 * Tests the banOrPickHero method when it is a Ban phase of the draft
	 */
	void testBanOrPickHeroBan() {
		System.out.println("Please enter which hero you wish to ban.");
		String heroToBan = "MARS";
		Drafter.banOrPickHero(heroes, userInput, "Ban", "Dire", normalDraftTime, radiantHeroes, direHeroes);
		assertFalse(heroes.get(heroToBan));
	}

	@Test
	/**
	 * Tests the banOrPickHero method when it is a Pick phase of the draft
	 */
	void testBanOrPickHeroPick() {
		String heroToPick = "JUGGERNAUT";
		System.out.println("Please enter which hero you wish to pick.");
		Drafter.banOrPickHero(heroes, userInput, "Pick", "Dire", normalDraftTime, radiantHeroes, direHeroes);
		assertFalse(heroes.get(heroToPick));
	}

	@Test
	/**
	 * Tests the Radiant reserve time thread is started when the normal draft time
	 * runs out
	 */
	void testReserveTimeStarterNotStartedBefore() {
		Boolean expected = true;
		try {
			normalDraftTime.setDraftTime(0);
			Drafter.reserveTimeStarter("Radiant", normalDraftTime, radiantReserveTimeThread, direReserveTimeThread);
			Thread.sleep(5000);
		} catch (InterruptedException e) {

		}

		assertEquals(expected, radiantReserveTimeThread.getHasStarted());

	}

	@Test
	/**
	 * Tests the Dire reserve time thread is started when the normal draft time runs
	 * out
	 */
	void testReserveTimeStarterNotStartedBeforeDire() {
		Boolean expected = true;
		try {
			normalDraftTime.setDraftTime(0);
			Drafter.reserveTimeStarter("Dire", normalDraftTime, radiantReserveTimeThread, direReserveTimeThread);
			Thread.sleep(5000);
		} catch (InterruptedException e) {

		}

		assertEquals(expected, radiantReserveTimeThread.getHasStarted());

	}

	@Test
	/***
	 * Tests that when a ReserveTimeThread has previously been started, that
	 * resuming it will set suspended to false.
	 */
	void testReserveTimeStarterStartedBefore() {
		Boolean expected = false;
		try {
			normalDraftTime.setDraftTime(5);
			Thread.sleep(5000);
			direReserveTimeThread.setHasStarted(true);
			Drafter.reserveTimeStarter("Radiant", normalDraftTime, radiantReserveTimeThread, direReserveTimeThread);
		} catch (InterruptedException e) {

		}
		assertEquals(expected, radiantReserveTimeThread.getSuspended());

	}

	@Test
	/***
	 * Tests that when a ReserveTimeThread has previously been started, that
	 * resuming it will set suspended to false.
	 */
	void testReserveTimeStarterStartedBeforeDire() {
		Boolean expected = false;
		try {
			normalDraftTime.setDraftTime(5);
			Thread.sleep(5000);
			direReserveTimeThread.setHasStarted(true);
			Drafter.reserveTimeStarter("Dire", normalDraftTime, radiantReserveTimeThread, direReserveTimeThread);
		} catch (InterruptedException e) {

		}
		assertEquals(expected, radiantReserveTimeThread.getSuspended());

	}

	@Test
	/***
	 * Tests that only the relevant Thread is started.
	 */
	void testReserveTimeStarterOnlyOne() {
		Boolean expected = false;
		try {
			normalDraftTime.setDraftTime(5);
			Thread.sleep(5000);
			Drafter.reserveTimeStarter("Dire", normalDraftTime, radiantReserveTimeThread, direReserveTimeThread);
		} catch (InterruptedException e) {

		}
		assertEquals(expected, radiantReserveTimeThread.getHasStarted());

	}

	@Test
	/**
	 * Tests that Radiant thread is suspended when it is their turn
	 */
	void testDetermineThreadToSuspendRadiant() {
		Drafter.determineThreadToSuspend("Radiant", radiantReserveTimeThread, direReserveTimeThread);
		assertTrue(radiantReserveTimeThread.getSuspended());
	}

	@Test
	/**
	 * Tests that Dire thread is suspended when it is their turn
	 */
	void testDetermineThreadToSuspendOnlyOne() {
		Drafter.determineThreadToSuspend("Radiant", radiantReserveTimeThread, direReserveTimeThread);
		assertFalse(radiantReserveTimeThread.getSuspended());
	}

	@Test
	/**
	 * Tests that only the relevant thread is suspended when it is their turn by
	 * checking the thread for the team whose turn it is not
	 */
	void testDetermineThreadToSuspendDire() {
		Drafter.determineThreadToSuspend("Dire", radiantReserveTimeThread, direReserveTimeThread);
		assertTrue(direReserveTimeThread.getSuspended());
	}

	@Test
	/**
	 * Tests the method which tracks when to random a hero. This tests the
	 * functionality when randoming will occur.
	 */
	void testDetermineWhenToRandomTrue() {
		normalDraftTime.setDraftTime(0);
		radiantReserveTimeThread.setReserveTime(0);
		String unexpected = "JAKIRO";
		System.out.println("Please enter a hero to pick (testing Random functionality - True).");
		Drafter.determineWhenToRandom("Radiant", normalDraftTime, radiantReserveTimeThread, direReserveTimeThread);
		Drafter.banOrPickHero(heroes, userInput, "Pick", "Radiant", normalDraftTime, radiantHeroes, direHeroes);
		System.out.println(radiantHeroes.get(0));
		assertNotEquals(unexpected, radiantHeroes.get(0));
	}

	@Test
	/**
	 * Tests the method which tracks when to random a hero. This tests the
	 * functionality when randoming will not occur.
	 */
	void testDetermineWhenToRandomFalse() {
		normalDraftTime.setDraftTime(35);
		radiantReserveTimeThread.setReserveTime(130);
		String expected = "Io";
		System.out.println("Please enter a hero to pick (testing Random functionality - false).");
		Drafter.determineWhenToRandom("Dire", normalDraftTime, radiantReserveTimeThread, direReserveTimeThread);
		Drafter.banOrPickHero(heroes, userInput, "Pick", "Dire", normalDraftTime, radiantHeroes, direHeroes);
		System.out.println(direHeroes.get(0));
		assertEquals(expected, direHeroes.get(0));
	}

	@Test
	/**
	 * This tests the method which rolls a random hero.
	 */
	void testRandomHero() {
		String expected = "default value";
		String randomHero = Drafter.randomHero(heroes);
		assertNotEquals(expected, randomHero);
	}

	@Test
	/**
	 * Tests the first hero is successfully updated for the Team object.
	 */
	void testUpdateTeamObjectFirst() {
		String firstHero = "JAKIRO";
		String secondHero = "IO";
		String thirdHero = "INVOKER";
		String fourthHero = "JUGGERNAUT";
		String fifthHero = "TIMBERSAW";
		radiantHeroes.add(firstHero);
		radiantHeroes.add(secondHero);
		radiantHeroes.add(thirdHero);
		radiantHeroes.add(fourthHero);
		radiantHeroes.add(fifthHero);
		Drafter.updateTeamObject(radiantHeroes, radiant);
		assertEquals(firstHero, radiant.getFirstHero());
	}

	@Test
	/**
	 * Tests the second hero is successfully updated for the Team object.
	 */
	void testUpdateTeamObjectSecond() {
		String firstHero = "JAKIRO";
		String secondHero = "IO";
		String thirdHero = "INVOKER";
		String fourthHero = "JUGGERNAUT";
		String fifthHero = "TIMBERSAW";
		radiantHeroes.add(firstHero);
		radiantHeroes.add(secondHero);
		radiantHeroes.add(thirdHero);
		radiantHeroes.add(fourthHero);
		radiantHeroes.add(fifthHero);
		Drafter.updateTeamObject(radiantHeroes, radiant);
		assertEquals(secondHero, radiant.getSecondHero());
	}

	@Test
	/**
	 * Tests the third hero is successfully updated for the Team object.
	 */
	void testUpdateTeamObjectThird() {
		String firstHero = "JAKIRO";
		String secondHero = "IO";
		String thirdHero = "INVOKER";
		String fourthHero = "JUGGERNAUT";
		String fifthHero = "TIMBERSAW";
		radiantHeroes.add(firstHero);
		radiantHeroes.add(secondHero);
		radiantHeroes.add(thirdHero);
		radiantHeroes.add(fourthHero);
		radiantHeroes.add(fifthHero);
		Drafter.updateTeamObject(radiantHeroes, radiant);
		assertEquals(thirdHero, radiant.getThirdHero());
	}

	@Test
	/**
	 * Tests the fourth hero is successfully updated for the Team object.
	 */
	void testUpdateTeamObjectFourth() {
		String firstHero = "JAKIRO";
		String secondHero = "IO";
		String thirdHero = "INVOKER";
		String fourthHero = "JUGGERNAUT";
		String fifthHero = "TIMBERSAW";
		direHeroes.add(firstHero);
		direHeroes.add(secondHero);
		direHeroes.add(thirdHero);
		direHeroes.add(fourthHero);
		direHeroes.add(fifthHero);
		Drafter.updateTeamObject(direHeroes, dire);
		assertEquals(fourthHero, dire.getFourthHero());
	}

	@Test
	/**
	 * Tests the fifth hero is successfully updated for the Team object.
	 */
	void testUpdateTeamObjectFifth() {
		String firstHero = "JAKIRO";
		String secondHero = "IO";
		String thirdHero = "INVOKER";
		String fourthHero = "JUGGERNAUT";
		String fifthHero = "TIMBERSAW";
		direHeroes.add(firstHero);
		direHeroes.add(secondHero);
		direHeroes.add(thirdHero);
		direHeroes.add(fourthHero);
		direHeroes.add(fifthHero);
		Drafter.updateTeamObject(direHeroes, dire);
		assertEquals(fifthHero, dire.getFifthHero());
	}
}
