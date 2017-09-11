package clocks;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class ClockRunner implements Runnable {
	private int period;
	private int buffer;
	private Time t;
	private Timer incrementer;
	private byte[] key;
	private KeyGen gen;
	
	public ClockRunner() {
		period = 1000;
		buffer = 0;
		t = new Time();
		gen = new KeyGen(null);
		reSync(0);
	}
	
	public ClockRunner(int period, int timeout, String seedValue) {
		if(period < 0 || timeout < 0 || (timeout > 0 && ((timeout % period != 0) || (timeout < 0)))) {
			this.period = 1000;
			timeout = 0;
		}
		else {
			this.period = period;
		}
		buffer = timeout/period;
		t = new Time();
		gen = new KeyGen(seedValue);
		reSync(0);
	}
	
	public void run() {
		incrementer = new Timer();
		incrementer.scheduleAtFixedRate(new TimerTask()
		{
	        public void run() 
	        {
	        	tick();
	        }
	    }, 0, period);
	}
	
	public void cancel() {
		incrementer.cancel();
	}
	
	public void tick() {
		reSync(1);
//		System.out.println(" TIME: " + t.toString());
	}
	
	private void reSync(int offset) {
//		System.out.println(device + ", ADDING: " + offset * period);
		t.add(offset * period);
		String time = t.toString();
		key = gen.getHash(time);
	}
	
	public boolean keyFound(byte[] key) {
		if(keyCheck(key)) {
			return true;
		}
		
		if(buffer < 1) {
			return false;
		}
		
		int offset = searchBuffer(key);
		if(offset > buffer) {
			System.out.println("TIME: " + t.toString());
			return false;
		}
		else {
//			System.out.println("RESYNC: " + offset);
			reSync(offset);
			return true;
		}
	}
	
	public boolean keyCheck(byte[] key) {
		return Arrays.equals(this.key, key);
	}
	
	public byte[] getKey() {
		return key;
	}
	
	public String getSeed() {
		return gen.getSeed();
	}
	
	private int searchBuffer(byte[] key) {
		Time temp = new Time(t);
		temp.add(period * -buffer);
		for(int i = -buffer; i <= buffer; i++) {
			String tempTime = temp.toString();
			byte[] tempKey = gen.getHash(tempTime);
			if(Arrays.equals(tempKey, key)) {
				return i;
			}
			temp.add(period);
		}
		return buffer + 1;
	}
	
	@Override
	public String toString() {
		return t.toString();
	}
}
