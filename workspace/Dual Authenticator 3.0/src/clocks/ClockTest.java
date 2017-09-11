package clocks;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class ClockTest {
	//private static int min;
	//private static int max;
	
	public static void main(String[] args) {
		int period = 50;
		int timeout = 500;
		//min = 0;
		//max = timeout;
		ClockRunner clientClock = new ClockRunner(period, timeout, null);
		ClockRunner serverClock = new ClockRunner(period, 0, clientClock.getSeed());
		clientClock.run();
		serverClock.run();
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask()
		{
			int i = 0;
	        public void run() 
	        {
	        	i++;
	        	byte[] key = serverClock.getKey();
	        	getWait();
	        	boolean found = clientClock.keyFound(key);
	        	System.out.println(found + " " + i);
	        	if(!found) {	        		
	        		timer.cancel();
	        		clientClock.cancel();
	        		serverClock.cancel();
	        	}
	        }
	    }, 0, 750);

	}
	
	public static int getRandom(int min, int max) {
		Random rand = new Random();
		return rand.nextInt(max - min + 1) + min;
	}
	
	public static void getWait() {
		try {
			Thread.sleep(getRandom(0, 125));
//			Thread.sleep(125);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
