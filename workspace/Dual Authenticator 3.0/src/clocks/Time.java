package clocks;
/**
 * Time represents the different divisions of time: hour, minute, second and milliseconds.<br>
 * <br>
 * Latest Update: 4/21/2017
 * @author Anthony Wianecki
 * @author Izzy Beraja
 *
 */
public class Time {
	private int milliseconds, seconds, minutes, hours;
	
/**<i><b>Time</i></b><br>
 * <blockquote><code>public Time() </blockquote></code>	
 * Default construcfotr for Time.<br>
 * Sets every value to 0.
 **/
	
	public Time() {
		milliseconds = 0;
		seconds = 0;
		minutes = 0;
		hours = 0;
	}

/**<i><b>Time</i></b><br>
 * <blockquote><code>public Time(int milliseconds, int seconds, int minutes, int hours) </blockquote></code>
 * 
 * Constructs a Time with the provided values.<br>
 * Sets the values to 0 if the value given is too small, sets the value to its maximum if the value given is too large. 
 * 
 * @param milliseconds
 * @param seconds
 * @param minutes
 * @param hours
 */
	public Time(int milliseconds, int seconds, int minutes, int hours) {
		set(milliseconds, seconds, minutes, hours);
	}
	
/**
 * <i><b>Time</i></b><br>
 * <blockquote><code>public Time(Time t)</blockquote></code>
 * Copy constructor for Time.
 * @param t - the Time to be copied.
 */
	public Time(Time t) {
		milliseconds = t.getMilliseconds();
		seconds = t.getSeconds();
		minutes = t.getMinutes();
		hours = t.getHours();
	}

/**<i><b>set</i></b><br>
 * <blockquote><code>public void set(int milliseconds, int seconds, int minutes, int hours)</blockquote></code>
 * 
 * Changes the values of Time to the ones that are provided.<br>
 * Sets the values to 0 if the value given is too small, sets the value to its maximum if the value given is too large.
 * 
 * @param milliseconds
 * @param seconds
 * @param minutes
 * @param hours
 */
	public void set(int milliseconds, int seconds, int minutes, int hours) {
		this.milliseconds = milliseconds;
		this.seconds = seconds;
		this.minutes = minutes;
		this.hours = hours;
		
		if(this.milliseconds > 999)
			this.milliseconds = 999;
		else if(this.milliseconds < 0)
			this.milliseconds = 0;
		if(this.seconds > 59)
			this.seconds = 59;
		else if(this.seconds < 0)
			this.seconds = 0;
		if(this.minutes > 59)
			this.minutes = 59;
		else if(this.minutes < 0)
			this.minutes = 0;
		if(this.hours < 0)
			this.hours = 0;
	}
	
/**
 * <i><b>add</i></b>
 * <blockquote><code>public void add(int milliseconds)</blockquote></code>
 * Adds the provided value to Time.<br>
 * If the addition results in a negative value, the Time is set to 0.<br>
 * Supports any integer value.
 * @param milliseconds - the amount of milliseconds to be added to Time.
 */
	
	public void add(int milliseconds) {
		if(milliseconds > 0)
			addTime(milliseconds);
		if(milliseconds < 0)
			subTime(-milliseconds);
	}
	
	private void addTime(int milliseconds) {
		
		this.milliseconds += milliseconds;
		
		if(this.milliseconds > 999) {
			seconds += this.milliseconds/1000;
			this.milliseconds %= 1000;
			
			if(seconds >= 60) {
				minutes += seconds/60;
				seconds %= 60;
				
				if(minutes >= 60) {
					hours += minutes/60;
					minutes %= 60;
				}
			}
		}
	}
	
	private void subTime(int milliseconds) {
		this.milliseconds -= milliseconds;
		
		if(this.milliseconds < 0) {
			int carry = this.milliseconds/1000;
			if(this.milliseconds % 1000 !=0) {
				carry--;
			}
			seconds += carry;
			this.milliseconds -= carry*1000;
			
			if(seconds < 0) {
				carry = seconds/60;
				if(seconds % 60 !=0) {
					carry--;
				}
				minutes += carry;
				seconds -= carry*60;
				
				if(minutes < 0) {
					carry = minutes/60;
					if(minutes % 60 !=0) {
						carry--;
					}
					hours += carry;
					minutes -= carry*1000;
					
					if(hours < 0) {
						this.milliseconds = 0;
						seconds = 0;
						minutes = 0;
						hours = 0;
					}
				}
			}
		}
	}
	
/**
 * <i><b>getMilliseconds</i></b>
 * <blockquote><code>public int getMilliseconds()</code></blockquote>
 * Returns the current value of milliseconds, ranging from 0 to 999.
 * @return
 * int - the current value of milliseconds.
 */
	public int getMilliseconds() {
		return milliseconds;
	}
	
/**
 * <i><b>getSeconds</i></b>
 * <blockquote><code>public int getSeconds()</code></blockquote>
 * Returns the current value of seconds, ranging from 0 to 59.
 * @return
 * int - the current value of seconds.
 */
	public int getSeconds() {
		return seconds;
	}
	
/**
 * <i><b>getMinutes</i></b>
 * <blockquote><code>public int getMinutes()</code></blockquote>
 * Returns the current value of minutes, ranging from 0 to 59.
 * @return
 * int - the current value of minutes.
 */
	public int getMinutes() {
		return minutes;
	}
	
/**
 * <i><b>getHours</i></b>
 * <blockquote><code>public int getHours()</blockquote></code>
 * Returns the current value of hours.
 * @return
 * int - the current value of hours.
 */	
	public int getHours() {
		return hours;
	}
	
/**
 * <i><b>toString</i></b>
 * <blockquote><code>public String toString()</blockquote><code>
 * Converts the current value of Time into a String of the following format:<br>
 * <blockquote>"%d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds</blockquote>
 * @return
 * String - 
 */	
	@Override
	public String toString() {
		return (String.format("%d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds));
	}
}
