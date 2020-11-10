package drafting;

public class DraftingThread implements Runnable {
	int reserveTime = 130;
	Boolean suspended = false;

	/**
	 * Default constructor
	 */
	public DraftingThread() {
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		do {

			try {
				System.out.println(this.reserveTime);
				this.reserveTime--;
				Thread.sleep(1000);
				synchronized (this) {
					while(this.suspended == true) {
						wait();
					}
				}
			} catch (InterruptedException e) {
			}

		} while (reserveTime >= 0 && suspended == false);

	}

	synchronized void suspend() {
		suspended = true;
	}

	synchronized void resume() {
		suspended = false;
		notify();
	}

}
