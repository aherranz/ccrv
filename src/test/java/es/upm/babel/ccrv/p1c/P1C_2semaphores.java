package es.upm.babel.ccrv.p1c;

import es.upm.babel.cclib.Consumo;
import es.upm.babel.cclib.Fabrica;
import es.upm.babel.cclib.Producto;
import es.upm.babel.ccrv.Semaphore;

public class P1C_2semaphores
{
  private static final int N = 10;
  private static volatile Producto buffer;

  private final static Semaphore sbusy = new Semaphore("sbusy",0);
  private final static Semaphore sfree = new Semaphore("sfree",1);

  public static class Consumer extends Thread {
    public void run() {
      Producto p;
      int i = 0;
      while(i < N) {

        // (try to) get the product
        sbusy.await("consumer waits");
        p = buffer;
        buffer=null;
        sfree.signal("consumer signals");

        // Consume product retrieved 
        Consumo.consumir(p);
	i++;
      } // while
    } // run
  } // class Consumer

  public static class Producer extends Thread {
    public void run() {
      Producto p = null;
      int i = 0;
      while(i < N) {
        // Produces a new product
        p = Fabrica.producir();

        // (try to) store the producto
        sfree.await("producer waits");
        buffer = p;
        p = null;
        sbusy.signal("producer signals");
      } // while
    } // run 
  } // class Producer
} // class P1C_2semaphores
