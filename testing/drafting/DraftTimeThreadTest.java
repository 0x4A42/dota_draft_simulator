package drafting;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DraftTimeThreadTest {

	// test variables
	int initialTime;
	DraftTimeThread testThread;

	@BeforeEach
	void setUp() throws Exception {
		initialTime = 35;
		testThread = new DraftTimeThread();

	}

	@Test
	/**
	 * Tests that the thread successfully starts
	 */
	void testStartThread() {
		testThread.startThread();
		assertEquals(initialTime, testThread.getDraftTime());
	}

	@Test
	/**
	 * Tests that the thread successfully starts, and that the time successfully
	 * counts down
	 */
	void testStartThreadWaitFive() {
		int expectedSeconds = 30;
		try {
			testThread.startThread();
			Thread.sleep(5000);
			assertEquals(expectedSeconds, testThread.getDraftTime());

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	/***
	 * Tests that the thread is successfully interrupted
	 */
	void testInterrupt() {
		testThread.startThread();
		testThread.interrupt();
		assertTrue((testThread.thread.isInterrupted()));

	}

	@Test
	/**
	 * Tests that the thread is set up correctly, using the Getter
	 */
	void testGetDraftTime() {
		assertEquals(initialTime, testThread.getDraftTime());
	}

	@Test
	/**
	 * Tests that the Setter works correctly.
	 */
	void testSetDraftTime() {
		int newDraftTime = 40;
		testThread.setDraftTime(newDraftTime);
		assertEquals(newDraftTime, testThread.getDraftTime());
	}
}
