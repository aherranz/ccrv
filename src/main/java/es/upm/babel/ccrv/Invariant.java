package es.upm.babel.ccrv;

/**
 * Functional interface to specify an invariant on semaphores and
 * ghost counters.
 */
public interface Invariant {
  /**
   * Check the invariant.
   * @return true if the invariant hold, false otherwise
   */
  boolean check();
}
