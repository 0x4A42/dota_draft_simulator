package drafting;

/***
 * This class will be used for running an additional thread that will represent
 * the team's reserve time. Each team has 130 seconds of reserve time that they
 * can use through the entire draft, which begins to be used once they run out
 * of the initial 30 seconds draft team for each pick or ban. The 30 seconds
 * refreshes with each new pick or ban, whereas the reserve time is a constant.
 * 
 * @author Jordan
 *
 */
public class DraftingThread implements Runnable {
	int reserveTime = 130; // the team's total reserve time for the draft
	Boolean suspended = false; // controls the pausing of the thread

	/**
	 * Default constructor
	 */
	public DraftingThread() {
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	/**
	 * Runs the thread. Counts down from 130 every second, representing the reserve
	 * time a team has. Pauses when suspended is true (set when not the team's turn
	 * to pick, or when within normal draft time)
	 */
	public void run() {
		do {

			try {
				System.out.println(this.reserveTime);
				this.reserveTime--;
				Thread.sleep(1000);
				synchronized (this) {
					while (this.suspended == true) {
						wait();
					}
				} // end of synchronized
			} catch (InterruptedException e) {
			} // end of try catch

		} while (reserveTime >= 0 && suspended == false);

	} // end of run()

	/**
	 * Sets the value of suspended to true, pausing the run() method.
	 */
	synchronized void suspend() {
		this.suspended = true;
	}

	/**
	 * Sets the value of suspended to false, resuming the run() method.
	 */
	synchronized void resume() {
		this.suspended = false;
		notify();
	}

}
