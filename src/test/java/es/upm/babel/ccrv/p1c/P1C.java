package es.upm.babel.ccrv.p1c;


import java.util.concurrent.ThreadLocalRandom;

import es.upm.babel.ccrv.SSemaphore;
import es.upm.babel.cclib.ConcIO;

public class P1C
{
    private static final int BUFFER_CAPACITY=3;
    private static Integer buffer;
    private static SSemaphore semMutex;
    private static SSemaphore sem, semCounterCons,semCounterProd;

    static { // Everything is initialized
        semMutex=new SSemaphore(1);
	sem=new SSemaphore(1,"sem");
	semCounterProd=new SSemaphore(0,"semCounterProd"); semCounterCons=new SSemaphore(0,"semCounterCons");
	buffer=null;
    } // static code

    public class Consumer extends Thread {
	public void run() {
	    int i;

	    i=0;
	    while(i<10) { // 10 iterations per Consumer
		try {
		    sem.await();
		    // semMutex.await();
		    if (buffer!=null) { // buffer not empty
			// Process buffer content
			Thread.sleep(ThreadLocalRandom.current().nextInt(0,3001));
			ConcIO.printfnl("Obtenido elemento: %s",buffer.toString());
			buffer=null; semCounterCons.signal(); // Example showing how the counters can be used to count how many times an instruction is executed
			i++;
		    } // if buffer not empty
		    sem.signal();
		    // semMutex.signal();
		} catch (Exception e) {
		    ConcIO.printfnl("ERROR EN EL CONSUMIDOR: %s",e.getMessage());
		} // catch
		// Item sElement is processed

	        // ConcIO.printfnl("Consumidor: Fin iteración %s",""+i);
	    } // while
	} // run
    } // class Consumer

    public class Producer extends Thread {
	public void run() {
	    int i;

	    i=0;
	    while(i<10) { // 10 iterations per Produceer
		try {
   	            Thread.sleep(ThreadLocalRandom.current().nextInt(200,500));
		    sem.await();
		    // semMutex.await();
		    if (buffer==null) { // if buffer is empty...
			// Generate new element
			Thread.sleep(ThreadLocalRandom.current().nextInt(0,2001));
			ConcIO.printfnl("Generado elemento: %s",""+i*11);
			buffer=new Integer(i*11); semCounterProd.signal();
			i++;
		    } // if buffer is empty...
		    sem.signal();
                    // semMutex.signal();
		} catch(Exception e) {
		    ConcIO.printfnl("ERROR EN EL PRODUCTOR: %s",e.getMessage());
		    e.printStackTrace();
		} // catch
	    } // while
	} // run
    } // class Producer
} // class P1C
