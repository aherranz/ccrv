package es.upm.babel.ccrv;

import java.util.Hashtable;

/**
 * An interface that defines an object which declares an internal condition
 * (a boolean function) and which throws an Exception whenever that condition
 * turns out to be false.
 */
public interface Invariant {

  // Assigs a hastable with the counters that will be used to check whether
  // or not the invariant remains valid.
  public void setCounters(Hashtable ht);

  // Since we want an Invariant to be able to operate on the counters of the
  // semaphores, we also have to pass the table with MSems to the Invariant.
  public void setSemaphores(Hashtable ht);

  // Evaluates a condition based on the counters in the hashtable and throws
  // an exception if the condition is false.
  public void check() throws IllegalArgumentException;

} // interface
