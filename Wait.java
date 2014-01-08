/**
 * Ben Rodriguez
 * 
 * This code is use to place a one second pause in the loa.java under 
 * the public void make_user_move() class. The pause is necessary for 
 * JDK version 6. The code was retrieved from the following URL February 2010. 
 * 
 * http://www.java-tips.org/java-se-tips/java.lang/pause-the-execution.html
 * http://www.rgagnon.com/javadetails/java-0145.html
 */
public class Wait {
	public static void oneSec() {
	     try {
	       Thread.currentThread();
	       Thread.sleep(1000);
	       }
	     catch (InterruptedException e) {
	       e.printStackTrace();
	       }
	     }  
	  public static void manySec(long s) {
	     try {
	       Thread.currentThread();
	       Thread.sleep(s * 1000);
	       }
	     catch (InterruptedException e) {
	       e.printStackTrace();
	       }
	     }
}
