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
    // What that means is: At any given moment, the number of threads
    // running after sem.await in the consumer (line 32) plus the number
    // of threads running after sem.await in the producer (line 61) is
    // smaller than 2 (that is, at most 1).
    // Semaphore sem as a traditional mutex
    // Remember that at most one semaphore operation can be running at any
    // given time since all the semaphores are operated from inside
    // a monitor
    public void check() {
	String sError;
        int c32,c41,c61,c70;
	int nHowManyProduced, nHowManyConsumed;
	boolean bCondition;

        if (counters==null) {throw new IllegalArgumentException("No counters have been defined");}

	c32=SSemaphore.getCounterSafe("32",SSemaphore.POST);
	c41=SSemaphore.getCounterSafe("41",SSemaphore.POST);
	c61=SSemaphore.getCounterSafe("61",SSemaphore.POST);
	c70=SSemaphore.getCounterSafe("70",SSemaphore.POST);
	if (msems!=null) {
	    nHowManyProduced=SSemaphore.getValue("semCounterProd");
	    nHowManyConsumed=SSemaphore.getValue("semCounterCons");
	} // msems!=null
	else {nHowManyProduced=0; nHowManyConsumed=0;}
	bCondition=((c32-c41)+(c61-c70)<2) && (nHowManyProduced>=nHowManyConsumed);

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
