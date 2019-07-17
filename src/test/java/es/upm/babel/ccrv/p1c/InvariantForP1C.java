package es.upm.babel.ccrv.p1c;

import es.upm.babel.ccrv.Invariant;
import es.upm.babel.ccrv.SSemaphore;
import es.upm.babel.cclib.ConcIO;

import java.util.Hashtable;

public class InvariantForP1C implements Invariant {
    private Hashtable counters;
    private Hashtable msems;
    // The counters and the semaphores on which the Invariant will operate

    // Invariant: (c32-c41) + (c61-c70) < 2

    // Remember that at most one semaphore operation can be running at any
    // given time since all the semaphores are operated from inside
    // a monitor
    public void check() {
	String sError;
        int c32,c41,c61,c70;
	boolean bCondition;

	c32=SSemaphore.after("32");
	c41=SSemaphore.after("41");
	c61=SSemaphore.after("61");
	c70=SSemaphore.after("70");
	bCondition=((c32-c41)+(c61-c70)<2);

        if (counters==null) {throw new IllegalArgumentException("No counters have been defined");}


	if (!(bCondition)) // Condition to evaluate
	    {sError=">>>>> Illegal system state: "+SSemaphore.displayCounters();
	ConcIO.printfnl(sError);
	throw new IllegalArgumentException(sError);}
	else {ConcIO.printfnl("Invariant OK: %s",SSemaphore.displayCounters());}
    } // check

    public void setCounters(Hashtable ht) {this.counters=ht;}
    // Provide the invariant with the counters it needs to operate.
    // Called from incCounter

    public void setSemaphores(Hashtable ht) {this.msems=ht;}
    // Optionally, provide the invariant with the semaphores, so the invariant
    // can make calculation with their counters.
    // Called from incCounter
} // class
