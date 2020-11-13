package drafting;

/***
 * This class will be used to track an int, which decrements once per second
 * when active, that will represent the team's natural draft time.
 * 
 * Each team has 35 seconds of draft time with each pick or ban in which they
 * must select a hero. Failure to do so will result in their result time (130
 * seconds) being used.
 * 
 * This 35 seconds refreshes with each new pick or ban.
 * 
 * Will be ran in a separate thread as there is need to constantly update and
 * track this value.
 */
public class DraftTimeThread implements Runnable {

	private int draftTime = 35;
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
	 * Runs the thread.
	 * 
	 * Counts down from 35 every second, representing the reserve time a team has.
	 * 
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

	/**
	 * @return the draftTime
	 */
	public int getDraftTime() {
		return draftTime;
	}

	/**
	 * @param draftTime the draftTime to set
	 */
	public void setDraftTime(int draftTime) {
		this.draftTime = draftTime;
	}

}
