package drafting;

public class DraftTimeThread implements Runnable {

	int draftTime = 10;
	Thread thread = new Thread(this);

	/**
	 * Default constructor
	 */
	public void startThread() {
		thread.start();
	}

	public void interrupt() {
		thread.interrupt();
	}

	@Override
	/**
	 * Runs the thread. Counts down from 35 every second, representing the reserve
	 * time a team has. Pauses when suspended is true (set when not the team's turn
	 * to pick, or when within normal draft time)
	 */
	public void run() {
		do {

			try {
				if (this.draftTime % 5 == 0) {
					System.out.println(draftTime + " seconds of normal draft time remaining.");
				}
				this.draftTime--;
				Thread.sleep(1000);

			} catch (InterruptedException e) {
				break;
			}

		} while (this.draftTime > 0);

	} // end of run()

}
