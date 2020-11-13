package drafting;

/***
 * This class will be used to track an int, which decrements once per second
 * when active, that will represent the team's reserve time.
 * 
 * Each team has 130 seconds of reserve time that they can use through the
 * entire draft, which begins to be used once they run out of the initial 35
 * seconds draft team for each pick or ban.
 * 
 * This 130 seconds is a one time value which will never be refreshed, unlike
 * the natural draft time.
 * 
 * Failure to pick a hero once the reserve time has been depleted will result in
 * a random hero being chosen.
 * 
 * Will be ran in a separate thread as there is need to constantly update and
 * track this value.
 */
public class ReserveTimeThread implements Runnable {
	private int reserveTime = 130; // the team's total reserve time for the draft
	private Boolean suspended = false; // controls the pausing of the thread
	private Boolean hasStarted = false; // tracks if the thread has previously been started

	
	public ReserveTimeThread() {

	}
	

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
				if (this.reserveTime % 5 == 0) {
					System.out.println(this.reserveTime + " seconds of reserve time remaining.");
				}
				this.reserveTime--;
				Thread.sleep(1000);
				// if suspended = true, wait as thread has been 'paused'. This will be due to
				// the team either ending their turn or running out of reserve time. The thread
				// is paused until the next turn where they run out of default time.
				synchronized (this) {
					while (this.suspended == true) {
						wait();
					}
				} // end of synchronized
			} catch (InterruptedException e) {
			} // end of try catch

		} while (this.reserveTime > 0 && this.suspended == false);

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
