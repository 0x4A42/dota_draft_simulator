package drafting;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReserveTimeThreadTest {

	ReserveTimeThread reserveTimeThreadToTest;

	@BeforeEach
	void setUp() throws Exception {
		reserveTimeThreadToTest = new ReserveTimeThread();
	}

	@Test
	/**
	 * Tests that the startThread() method successfully works by starting it,
	 * waiting 5 seconds and checking the time has decreased by 5.
	 */
	void testStartThread() {
		int expectedTime = 125;
		try {
			reserveTimeThreadToTest.startThread();
			Thread.sleep(5000);
			assertEquals(expectedTime, reserveTimeThreadToTest.getReserveTime());
		} catch (InterruptedException e) {
		}
	}

	@Test
	/**
	 * Tests that the run() method successfully works by starting it, waiting 2
	 * seconds and checking the time has decreased by 2.
	 */
	void testRun() {
		int expectedTime = 128;
		try {
			reserveTimeThreadToTest.startThread();
			Thread.sleep(2000);
			assertEquals(expectedTime, reserveTimeThreadToTest.getReserveTime());
		} catch (InterruptedException e) {
		}
	}

	@Test
	/**
	 * Tests that the isSuspended Boolean begins as false
	 */
	void testSuspendDefault() {
		assertTrue(!reserveTimeThreadToTest.getSuspended());
	}

	@Test
	/**
	 * Tests that the suspend() method successfully sets the isSuspended Boolean to
	 * true
	 */
	void testSuspend() {
		reserveTimeThreadToTest.suspend();
		assertTrue(reserveTimeThreadToTest.getSuspended());
	}

	@Test
	/**
	 * Tests that the isSuspended function ceases the running down of the clock.
	 * Decreases time by 2 seconds, suspends the thread and then waits an additional
	 * 3 seconds. Ensures time has only decreased by 2 seconds.
	 */
	void testSuspendTime() {
		int expectedTime = 128;
		try {
			reserveTimeThreadToTest.startThread();
			Thread.sleep(2000);
			reserveTimeThreadToTest.suspend();
			Thread.sleep(3000);
		} catch (InterruptedException e) {

		}
		assertEquals(expectedTime, reserveTimeThreadToTest.getReserveTime());
	}

	@Test
	/**
	 * Tests that the resume() function sets the isSuspended Boolean back to false.
	 */
	void testResume() {
		reserveTimeThreadToTest.suspend();
		reserveTimeThreadToTest.resume();
		assertTrue(!reserveTimeThreadToTest.getSuspended());
	}

	@Test
	/***
	 * Tests that the resume() function once again runs down the clock.
	 */
	void testResumeTime() {
		int expectedTime = 127;
		try {
			reserveTimeThreadToTest.startThread();
			reserveTimeThreadToTest.suspend();
			reserveTimeThreadToTest.resume();
			Thread.sleep(3000);
		} catch (InterruptedException e) {

		}
		assertEquals(expectedTime, reserveTimeThreadToTest.getReserveTime());
	}

	@Test
	/***
	 * Tests the default reserve time is correctly set.
	 */
	void testReserveTimeDefault() {
		int expectedTime = 130;
		assertEquals(expectedTime, reserveTimeThreadToTest.getReserveTime());
	}

	@Test
	/***
	 * Tests the getter for reserveTieme after time has passed
	 */
	void testReserveTimeTimePassed() {
		int expectedTime = 126;
		try {
			reserveTimeThreadToTest.startThread();
			Thread.sleep(4000);
			reserveTimeThreadToTest.suspend();
		} catch (InterruptedException e) {

		}
		assertEquals(expectedTime, reserveTimeThreadToTest.getReserveTime());
	}

	@Test
	/**
	 * Tests the default value of hasStarted is false
	 */
	void testHasStartedFalse() {
		assertEquals(false, reserveTimeThreadToTest.getHasStarted());
	}

	@Test
	/**
	 * Tests that calling startThread() sets hasStarted to true
	 */
	void testHasStartedTrue() {
		reserveTimeThreadToTest.startThread();
		assertEquals(true, reserveTimeThreadToTest.getHasStarted());
	}
}
