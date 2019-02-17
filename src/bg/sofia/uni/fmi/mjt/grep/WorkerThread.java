package bg.sofia.uni.fmi.mjt.grep;

public class WorkerThread implements Runnable {
	static final int CYCLE_NUM = 10;
	static final int SLEEP_NUM = 1;

	@Override
	public void run() {
		for (int i = 0; i < CYCLE_NUM; i++) {
			try {
				Thread.sleep(SLEEP_NUM);
			} catch (InterruptedException e) {
				System.out.println("InterruptedException in Worker Thread");
			}

		}
	}
}