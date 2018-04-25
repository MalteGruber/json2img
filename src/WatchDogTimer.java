
public class WatchDogTimer extends Thread {

	private final int TIMEOUT_SEC = 60 * 5;
	private final boolean WDT_ENABLED = false;
	private int timer = TIMEOUT_SEC;

	private boolean timedOut = false;
	private WdtCallbackListener wdtCallbackListener;

	public WatchDogTimer(WdtCallbackListener wdtCallbackListener) {
		this.wdtCallbackListener = wdtCallbackListener;
		start();
	}

	@Override
	public void run() {
		boolean go = true;
		while (go) {

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (!timedOut) {
				evaluateTimer();
			}
		}
		super.run();
	}

	private void evaluateTimer() {
		timer--;
		if (timer == 0) {
			timedOut = true;

			if (WDT_ENABLED) {
				System.err.println("Watch dog triggered!");
				wdtCallbackListener.timeout();
			}
		}
	}

	public void gentlyKickWatchDog() {
		timer = TIMEOUT_SEC;
		timedOut = false;

	}
}

interface WdtCallbackListener {
	void timeout();
}