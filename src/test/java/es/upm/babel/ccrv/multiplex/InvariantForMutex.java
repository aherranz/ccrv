package es.upm.babel.ccrv.multiplex;

import es.upm.babel.ccrv.Invariant;
import es.upm.babel.ccrv.Semaphore;

import es.upm.babel.cclib.ConcIO;

import java.util.Hashtable;

// INV_m --> c40+ - c43+ == min(c40- - c43+,n)
// n == Initial value for the semaphore

public class InvariantForMutex implements Invariant {
  private Hashtable counters;
  // The counters and the semaphores on which the Invariant will operate

  private static final int n = 3;

  public void check() {
    int cInMinus,cInPlus,cOut;
    int nLeftSide; // c40+ - c43+
    int nRightSide; // min (c40- - c43+,n)
    String sError;

    // Remember that at most one semaphore operation can be running at any
    // given time since all the semaphores are operated from inside
    // a monitor
    if (counters==null) {throw new IllegalArgumentException("No existen contadores");}
    cInMinus= Semaphore.before("mutexIn");
    cInPlus= Semaphore.after("mutexIn");
    cOut= Semaphore.after("mutexOut");

    nLeftSide=cInPlus-cOut;
    nRightSide=(cInMinus-cOut<n)?cInMinus-cOut:n;

    if (nLeftSide!=nRightSide) // If the invariant is violated, throw an exception
      {sError=">>>>> Illegal system state: cIn-="+cInMinus+", cIn+="+cInPlus+", cOut="+cOut;
	ConcIO.printfnl(sError);
	throw new IllegalArgumentException(sError);}
    else
      {ConcIO.printfnl("Invariant OK: %s", Semaphore.displayCounters());}
    // The invariant is true
  } // check

  public void setCounters(Hashtable ht) {this.counters=ht;}
  // Provide the invariant with the counters it needs to operate.
  // Called from incCounter

  public void setSemaphores(Hashtable ht) {this.msems=ht;}
  // Optionally, provide the invariant with the semaphores, so the invariant
  // can make calculation with their counters.
  // Called from incCounter
} // class
