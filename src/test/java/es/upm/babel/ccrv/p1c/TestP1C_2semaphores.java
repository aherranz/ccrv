package es.upm.babel.ccrv.p1c;

import es.upm.babel.ccrv.Semaphore;
import static es.upm.babel.ccrv.Semaphore.semaphore;
import static es.upm.babel.ccrv.Semaphore.after;

public class TestP1C_2semaphores
{
  public static void main(String[] args) {
    final int N_THREADS = 10;
    P1C_2semaphores.Consumer[] c = new P1C_2semaphores.Consumer[N_THREADS];
    P1C_2semaphores.Producer[] p = new P1C_2semaphores.Producer[N_THREADS];
    for (int i = 0; i < N_THREADS; i++) {
      c[i] = new P1C_2semaphores.Consumer();
      p[i] = new P1C_2semaphores.Producer();
    }

    Semaphore.addInvariant(() ->
                           after("consumer waits") - after("consumer signals")
                           + after("producer waits") - after("producer signals")
                           < 2);

    for (int i = 0; i < N_THREADS; i++) {
      c[i].start();
      p[i].start();
    } // for
  } // main
} // class TestP1C_2semaphores
