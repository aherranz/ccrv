package es.upm.babel.ccrv.rw;

import es.upm.babel.ccrv.Invariant;
import es.upm.babel.ccrv.Semaphore;
import es.upm.babel.cclib.ConcIO;

import java.util.Hashtable;

// Equivalences: (Article-->Java code)
// c7 ==> roomEmptyWaitW
// c9 ==> roomEmptySignalW
// c18 ==> mutexSignalR
// c22 ==> mutexWaitR2

public class InvariantForRW implements Invariant {
    private Hashtable counters;
    private Hashtable msems;
    // The counters and the semaphores on which the Invariant will operate

    // Remember that at most one semaphore operation can be running at any
    // given time since all the semaphores are operated from inside
    // a monitor
    public void check() {
	int cRoomEmptyWaitW,cRoomEmptySignalW,cMutexSignalR,cMutexWaitR2;
        boolean bConjunct11; // roomEmptyWaitW-roomEmptySignalW==1
	boolean bConjunct12; // mutexSignalR-mutexWaitR2-==0
	boolean bDisjunct2; // rommEmptyWaitW-roomEmptySignalW==0
        int nLineNumber;
	String sError;

        if (counters==null) {throw new IllegalArgumentException("No counters have been defined");}
	cRoomEmptyWaitW= Semaphore.after("roomEmptyWaitW");
	cRoomEmptySignalW= Semaphore.after("roomEmptySignalW");
	cMutexSignalR= Semaphore.after("mutexSignalR");
	cMutexWaitR2= Semaphore.before("mutexWaitR2");

	bConjunct11=(cRoomEmptyWaitW-cRoomEmptySignalW)==1;
	bConjunct12=(cMutexSignalR-cMutexWaitR2)==0;
	bDisjunct2=(cRoomEmptyWaitW-cRoomEmptySignalW)==0;

	if (!((bConjunct11 && bConjunct12) || bDisjunct2))
	    {sError="Illegal system state "+ Semaphore.displayCounters();
	ConcIO.printfnl(sError);
	throw new IllegalArgumentException(sError);}
	else {ConcIO.printfnl("Invariant OK %s", Semaphore.displayCounters());}
    } // check

    public void setCounters(Hashtable ht) {this.counters=ht;}
    // Provide the invariant with the counters it needs to operate.
    // Called from incCounter

    public void setSemaphores(Hashtable ht) {this.msems=ht;}
    // Optionally, provide the invariant with the semaphores, so the invariant
    // can make calculation with their counters.
    // Called from incCounter
} // class
