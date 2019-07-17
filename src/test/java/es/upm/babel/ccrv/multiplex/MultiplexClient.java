package es.upm.babel.ccrv.multiplex;

import es.upm.babel.ccrv.Semaphore;

import es.upm.babel.cclib.ConcIO;

import java.util.concurrent.ThreadLocalRandom;

/*
 * A thread that repeatedly enters and exits a critical section protected by
 * a semaphore called semMultiplex. The semaphore is termed 'multiplex' because
 * its counter is greater than 1.
 */

public class MultiplexClient extends Thread{
  private static Semaphore semMultiplex;
  // The semaphore that will be used by all the clients to control access
  // to the critical section
  private int id;

  // Used to generate random wait periods
  private int randomNumber(int min,int max)
  {return ThreadLocalRandom.current().nextInt(min, max + 1);}

  // Every thread that uses the critical section (every client) receives
  // an ID and a reference to the semaphore it must use to enter the critical
  // section
  public MultiplexClient(Semaphore semMutex, int id) {
    semMultiplex=semMutex;
    this.id=id;
  } // constructor/2

  public void run() {
    int i;

    // Ten iterations for every thread
    for(i=0;i<10;i++) {
      try {
        ConcIO.printfnl("Iteration %d of thread %d starts",i,this.id);
        semMultiplex.await("mutexIn"); // Into the critical section...
        ConcIO.printfnl("After await in iteration %d of thread %d\n",i,this.id);
        Thread.sleep(randomNumber(0,1001)); // Critical Section
        semMultiplex.signal("mutexOut"); // And out from the critical section
        ConcIO.printfnl("After signal in iteration %d of thread %d\n",i,this.id);
        Thread.sleep(randomNumber(0,1001));
      } catch (Exception e) {
        ConcIO.printfnl("Thread %d has received exception %s",this.id,e.getMessage());
        e.printStackTrace();
      }
    } // for
  } // run
} // class
