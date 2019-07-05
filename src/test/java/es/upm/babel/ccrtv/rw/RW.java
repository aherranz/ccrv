package es.upm.babel.ccrtv.rw;

/*
 * Problem of the Readers and Writers. This class defines
 * two subclasses representing both readers and writers along with
 * the semaphores required (mutex, items, spaces).
 */

import java.util.concurrent.ThreadLocalRandom;

import es.upm.babel.ccrtv.SSemaphore;
import es.upm.babel.ccrtv.Invariant;
import es.upm.babel.cclib.ConcIO;

public class RW
{
    private static int sharedResource;
    private static int readers; // Number of readers in critical section
    private static SSemaphore semMutex;
    // The semaphore that protects the shared resources
    private static SSemaphore semRoomEmpty;
    // Semaphore that the writers use as a mutex


    static { // Everything is initialized
        semMutex=new SSemaphore(1); // semMutex --> semaphore number 1
	semRoomEmpty=new SSemaphore(1); // roomEmpty --> number 2
	// The two SSemaphores above have not been given a name by the
        // programmer but the invariant will be later able to refer to them
	// since the name they are given is not random but sequential:
	// - The first SSemaphore created will be called "1"
	// - The second SSemaphore to be created (semRoomEmpty) will be
	//   called "2".
	sharedResource=0;
	// The shared space on which readers and writers operate
	readers=0;
    } // static code


    public class Reader extends Thread {
	public void run() {
	    int i;
	    int iValue;

	    for(i=0;i<10;i++) { // 10 iterations per Reader
		try {
		  semMutex.await("mutexWaitR");
                  readers+=1;
		  if (readers==1) {semRoomEmpty.await("roomEmptyWaitR");}
		  semMutex.signal("mutexSignalR");

		  // Critical section for readers
                  iValue=sharedResource; // get the element read
		  ConcIO.printfnl("Obtenido valor: %s",""+iValue);

// Hay 1 anotaciones.
// @es.upm.babel.ccrtv.CountersAnnot(semOperationLabels=[mutexWaitR, mutexSignalR, mutexWaitR2, mutexSignalR2, roomEmptyWaitR, roomEmptySignalR])
// Valor 0: mutexWaitR
// Valor 1: mutexSignalR
// Valor 2: mutexWaitR2
// Valor 3: mutexSignalR2
// Valor 4: roomEmptyWaitR
// Valor 5: roomEmptySignalR
// Clase=interface es.upm.babel.ccrtv.CountersAnnot
// Método 0: semOperationLabels default null
// Método 1: equals default null
// Método 2: toString default null
// Método 3: hashCode default null
// Método 4: annotationType default null
		  // --------------------------------------

		  semMutex.await("mutexWaitR2");
                  readers-=1;
		  if (readers==0) {semRoomEmpty.signal("roomEmptySignalR");}
		  // Thread.sleep(ThreadLocalRandom.current().nextInt(0,1001));
		  semMutex.signal("mutexSignalR2");
		} catch (Exception e) {
		    ConcIO.printfnl("ERROR EN READER: %s",e.getMessage());
		} // catch
		// Item sElement is processed
	    } // for
	} // run
    } // class Reader

    public class Writer extends Thread {
	public void run() {
	    int i;
	    int iValue;

	    for(i=0;i<10;i++) { // 10 iterations per Writer
		try {
		    semRoomEmpty.await("roomEmptyWaitW");

		    // Critical section for writers
                    sharedResource+=1; // Increment the common value
		    ConcIO.printfnl("Escrito valor: %s",""+sharedResource);
		    // --------------------------------------

		    semRoomEmpty.signal("roomEmptySignalW");
		} catch(Exception e) {
		    ConcIO.printfnl("ERROR EN WRITER: %s",e.getMessage());
		    e.printStackTrace();
		} // catch
	    } // for
	} // run
    } // class Writer
} // class RW
