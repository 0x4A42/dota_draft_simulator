package drafting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TeamTest {

	Side direSide; // represents the side for Dire
	Side radiantSide; // represents the side for radiant
	Side sideToSet;
	String firstHero; // represents the first hero picked
	String secondHero; // represents the second hero picked
	String thirdHero; // represents the third hero picked
	String fourthHero; // represents the fourth hero picked
	String fifthHero; // represents the fifth hero picked
	Team testTeam;
	Team testConstructor;

	@BeforeEach
	void setUp() throws Exception {
		firstHero = "Axe";
		secondHero = "Pugna";
		thirdHero = "Io";
		fourthHero = "Faceless Void";
		fifthHero = "Chen";
		radiantSide = Side.RADIANT;
		direSide = Side.DIRE;
		testTeam = new Team();
	}

	
	@Test
	/**
	 * Tests the Side is successfully set to RADIANT
	 */
	void testSideRadiant() {
		sideToSet = Side.RADIANT;
		testTeam.setSide(sideToSet);
		assertEquals(sideToSet, testTeam.getSide());
	}

	@Test
	/**
	 * Tests the Side is successfully set to DIRE
	 */
	void testSideDire() {
		sideToSet = Side.DIRE;
		testTeam.setSide(sideToSet);
		assertEquals(sideToSet, testTeam.getSide());
	}

	@Test
	/**
	 * Tests the first Hero getter and setter
	 */
	void testFirstHero() {
		testTeam.setFirstHero(firstHero);
		assertEquals(firstHero, testTeam.getFirstHero());
	}

	@Test
	/**
	 * Tests the second Hero getter and setter
	 */
	void testSecondHero() {
		testTeam.setSecondHero(secondHero);
		assertEquals(secondHero, testTeam.getSecondHero());
	}

	@Test
	/**
	 * Tests the third Hero getter and setter
	 */
	void testThirdHero() {
		testTeam.setThirdHero(thirdHero);
		assertEquals(thirdHero, testTeam.getThirdHero());
	}

	@Test
	/**
	 * Tests the fourth Hero getter and setter
	 */
	void testFourthHero() {
		testTeam.setFourthHero(fourthHero);
		assertEquals(fourthHero, testTeam.getFourthHero());
	}

	@Test
	/**
	 * Tests the fifth Hero getter and setter
	 */
	void testFifthHero() {
		testTeam.setFifthHero(fifthHero);
		assertEquals(fifthHero, testTeam.getFifthHero());
	}

	@Test
	/**
	 * Tests the printHeroes() method, which prints a string containing all of a
	 * team's Heroes. Captures the output and compares it (after being trimmed to
	 * remove an excess line) to what the String is expected to be.
	 */
	void testPrintHeroes() {
		String expectedOutput = "The heroes on RADIANT are: Axe, Pugna, Io, Faceless Void, and Chen.";
		testConstructor = new Team(radiantSide, firstHero, secondHero, thirdHero, fourthHero, fifthHero);
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		testConstructor.printHeroes();
		assertEquals(expectedOutput, outContent.toString().trim());

	}
}
