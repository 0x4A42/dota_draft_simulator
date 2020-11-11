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
public class ReserveTimeThread implements Runnable {
	private int reserveTime = 130; // the team's total reserve time for the draft
	private Boolean suspended = false; // controls the pausing of the thread
	private Boolean hasStarted = false;

	public void startThread() {
		this.setHasStarted(true);
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
				if (this.reserveTime % 10 == 0) {
					System.out.println(this.reserveTime + " seconds of reserve time remaining.");
				}
				this.reserveTime--;
				Thread.sleep(1000);
				synchronized (this) {
					while (this.suspended == true) {
						wait();
					}
				} // end of synchronized
			} catch (InterruptedException e) {
			} // end of try catch

		} while (this.reserveTime >= 0 && this.suspended == false);

	} // end of run()

	/**
	 * Sets the value of suspended to true, pausing the run() method.
	 */
	synchronized void suspend() {
		this.setSuspended(true);
	}

	/**
	 * Sets the value of suspended to false, resuming the run() method.
	 */
	synchronized void resume() {
		this.setSuspended(false);
		notify();
	}

	/**
	 * @return the reserveTime
	 */
	public int getReserveTime() {
		return reserveTime;
	}

	/**
	 * @param reserveTime the reserveTime to set
	 */
	public void setReserveTime(int reserveTime) {
		this.reserveTime = reserveTime;
	}

	/**
	 * @return the suspended
	 */
	public Boolean getSuspended() {
		return suspended;
	}

	/**
	 * @param suspended the suspended to set
	 */
	public void setSuspended(Boolean suspended) {
		this.suspended = suspended;
	}

	/**
	 * @return the hasStarted
	 */
	public Boolean getHasStarted() {
		return hasStarted;
	}

	/**
	 * @param hasStarted the hasStarted to set
	 */
	public void setHasStarted(Boolean hasStarted) {
		this.hasStarted = hasStarted;
	}

}
