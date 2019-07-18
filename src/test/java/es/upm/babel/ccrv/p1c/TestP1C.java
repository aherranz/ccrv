package es.upm.babel.ccrv.p1c;

import es.upm.babel.ccrv.Semaphore;
import static es.upm.babel.ccrv.Semaphore.semaphore;
import static es.upm.babel.ccrv.Semaphore.after;

public class TestP1C
{
  public static void main(String[] args) {
    final int N_THREADS = 2;
    P1C.Consumer[] c = new P1C.Consumer[N_THREADS];
    P1C.Producer[] p = new P1C.Producer[N_THREADS];
    for (int i = 0; i < N_THREADS; i++) {
      c[i] = new P1C.Consumer();
      p[i] = new P1C.Producer();
    }

    Semaphore.addInvariant(() ->
                           after("consumer locks") - after("consumer_unlocks")
                           + after("produce locks") - after("consumer unlocks")
                           < 2);

    Semaphore.addInvariant(() -> semaphore("storage") - semaphore("retrievals") <= 1);

    for (int i = 0; i < N_THREADS; i++) {
      c[i].start();
      p[i].start();
    }
  }
}
