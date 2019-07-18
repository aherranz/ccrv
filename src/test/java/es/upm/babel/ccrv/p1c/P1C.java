package es.upm.babel.ccrv.p1c;

import es.upm.babel.cclib.Consumo;
import es.upm.babel.cclib.Fabrica;
import es.upm.babel.cclib.Producto;
import es.upm.babel.ccrv.Semaphore;

public class P1C
{
  private static final int N = 10;
  private static volatile Producto buffer;

  private final static Semaphore mutex = new Semaphore("mutex", 1);
  private final static Semaphore retrievals = new Semaphore("retrievals");
  private final static Semaphore storage = new Semaphore("storage");

  public static class Consumer extends Thread {
    public void run() {
      Producto p;
      int i = 0;
      while(i < N) {

        // (try to) Gets the product
        mutex.await("consumer locks");
        p = buffer;
        if (buffer != null) {
          buffer=null;
          retrievals.signal();
        }
        mutex.signal("consumer unlocks");

        // Consume if product retrieved (busy waiting!)
        if (p != null) {
          Consumo.consumir(p);
          i++;
        }
      }
    }
  }

  public static class Producer extends Thread {
    public void run() {
      Producto p = null;
      int i = 0;
      while(i < N) {
        // Produces if no pending product (busy waiting!)
        if (p == null) {
          p = Fabrica.producir();
          i++;
        }

        // (try to) Store the producto
        mutex.await("producer locks");
        if (buffer==null) {
          buffer = p;
          p = null;
          storage.signal();
        }
        mutex.signal("producer unlocks");
      }
    }
  }
}
