package es.upm.babel.ccrv.p1c;

import es.upm.babel.cclib.Consumo;
import es.upm.babel.cclib.Fabrica;
import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.ConcIO;
import es.upm.babel.ccrv.Semaphore;

public class P1C
{
  private static final int N = 10;
  private static volatile Producto buffer=null;

  private final static Semaphore mutex = new Semaphore("mutex", 1);
  private final static Semaphore retrievals = new Semaphore("retrievals");
  private final static Semaphore storage = new Semaphore("storage");

  public static class Consumer extends Thread {
    public void run() {
      int i = 0;
      while(i < N) {

        // (try to) Gets the product
        mutex.await("consumer locks");
        if (buffer != null) {
	  Consumo.consumir(buffer);
	  buffer=null;
	  retrievals.signal();
	  i++;
        }
        mutex.signal("consumer unlocks");
      } // while
      ConcIO.printfnl("CONSUMER ENDED");
    }
  }

  public static class Producer extends Thread {
    public void run() {
      int i = 0;
      while(i < N) {
	// (try to) Store the producto
        mutex.await("producer locks");
        if (buffer==null) {
	  buffer = Fabrica.producir();
          storage.signal();
          i++;
        }
        mutex.signal("producer unlocks");
      }
      ConcIO.printfnl("PRODUCER ENDED");
    }
  }
}
