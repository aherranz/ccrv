package es.upm.babel.ccrv;

import es.upm.babel.cclib.Monitor;
import es.upm.babel.cclib.Monitor.Cond;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implementation of semaphores where every method invocation in every
 * instance is serialized.
 *
 * This Semaphores can be used with a classical Dijkstra's API:
 *
 * - new Semaphore(n)
 * - s.await()
 * - s.signal()
 *
 * Some extra methods have been added:
 *
 * - new Semaphore(name, n) - gives a name to the new semaphore whose
 *   internal value will be accessible to specify a program invariant.
 *
 * - s.await(programPoint) - before-after ghostcounters
 *   referenced with name programPoint will be accessible for
 *   specifying a program invariant.
 *
 * - s.signal(programPoint) - before-after ghost counters
 *   referenced with name programPoint will be accessible for
 *   specifying a program invariant.
 *
 * Some extra class members allows the programmer to specify program
 * invariants based on the values of named semaphores and ghost
 * counters.
 */
public class Semaphore {
  /**
   * Global monitor that serializes every operation on semaphores.
   */
  private static Monitor mutex = new Monitor();

  /**
   * Global map with named semaphores.
   */
  private static Map<String, Semaphore> namedSemaphores = new HashMap<>();

  /**
   * Global map with ghost before-after counters.
   */
  private static Map<String, GhostPair> ghostCounters = new HashMap<>();

  /**
   * Global set of added invariants.
   */
  private static Set<Invariant> invariants = new TreeSet<>();

  /**
   * The internal counter of the semaphore.
   */
  private int value;

  /**
   * The name of the semaphore (null if no name is given).
   */
  @Nullable
  private String name;

  /**
   * Condition to implement calling process block in await when value < 1
   */
  @Nonnull
  private Cond queue;

  /**
   * Create a new named semaphore initialized to n.
   */
  public Semaphore(String name, int n) {
    if (n < 0)
      throw new IllegalArgumentException("Semaphore cannot be initialized with a negative value");

    this.name = name;
    this.value = n;
    this.queue = mutex.newCond();

    if (name != null) {
      mutex.enter();
      namedSemaphores.put(name, this);
      mutex.leave();
    }
  }

  /**
   * Create a new named semaphore initialized to n.
   */
  public Semaphore(int n, String name) {
    this(name, n);
  }

  /**
   * Create a new named semaphore initialized to 0.
   */
  public Semaphore(String name) {
    this(name, 0);
  }

  /**
   * Create a new (unnamed) semaphore initialized to n.
   */
  public Semaphore(int n) {
    this(null, n);
  }

  /**
   * Create a new (unnamed) semaphore initialized to 0.
   */
  public Semaphore() {
    this(0);
  }

  /**
   * The Dijkstra P operation: delays until the internal counter is
   * greater than 0 and then decrements it.
   *
   * The usual name of this method is wait but, unfortunately, wait
   * is an important predefined method in Object and we don't want to
   * invalidate its semantics.
   */
  public void await() {
    // IMPORTANT: do not refactor this code since checkInvariants
    // depends on inspecting the stacktrace
    mutex.enter();
    value--;
    checkInvariants();
    if (value < 0)
      queue.await();
    checkInvariants();
    mutex.leave();
  }

  /**
   * Version of the Dijkstra's P operation acting on the ghost
   * before-after counters referenced with programPoint.
   */
  public void await(String programPoint) {
    mutex.enter();
    incBefore(programPoint);
    value--;
    checkInvariants();
    if (value < 0)
      queue.await();
    incAfter(programPoint);
    checkInvariants();
    mutex.leave();
  }

  /**
   * The Dijkstra V operation: increments the internal counter.
   */
  public void signal() {
    // IMPORTANT: do not refactor this code since checkInvariants
    // depends on inspecting the stacktrace
    mutex.enter();
    checkInvariants();
    if (value == 0 && queue.waiting() > 0)
      queue.signal();
    else
      value++;
    checkInvariants();
    mutex.leave();
  }

  /**
   * Version of the Dijkstra's V operation acting on the ghost
   * before-after counters referenced with programPoint.
   */
  public void signal(String programPoint) {
    mutex.enter();
    incBefore(programPoint);
    checkInvariants();
    if (value == 0 && queue.waiting() > 0)
      queue.signal();
    else
      value++;
    incAfter(programPoint);
    checkInvariants();
    mutex.leave();
  }

  /**
   * Adds a new invariant to be checked.
   * @param inv the invariant to be added.
   */
  public static void addInvariant(@Nonnull Invariant inv) {
    invariants.add(inv);
  }

  /**
   * Checks added invariants. Stops the program if an invariant fails.
   */
  private static void checkInvariants() {
    for(Invariant i : invariants) {
      if (!i.check()) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        String filename = ste.getFileName();
        int line = ste.getLineNumber();
        System.err.println(String.format("Invariant failed at line %d in file '%s'",
                line,
                filename));
        System.exit(1);
      }
    }
  }

  /**
   * Class to represent a pair of before-after ghost counters.
   */
  private static class GhostPair {
    private int before;
    private int after;
    public GhostPair() {
      before = 0;
      after = 0;
    }
    public void incBefore() {
      before++;
    }
    public void incAfter() {
      after++;
    }
    public int before() {
      return before;
    }
    public int after() {
      return after;
    }
  }

  /**
   * Increments the before ghost counter referenced by programPoint.
   */
  private static void incBefore(String programPoint) {
    if (programPoint != null) {
      GhostPair ghostPair = ghostCounters.get(programPoint);
      if (ghostPair == null)
        ghostPair = new GhostPair();
      ghostPair.incBefore();
      ghostCounters.put(programPoint, ghostPair);
    }
  }

  /**
   * Increments the after ghost counter referenced by programPoint.
   */
  private static void incAfter(String programPoint) {
    if (programPoint != null) {
      GhostPair ghostPair = ghostCounters.get(programPoint);
      ghostPair.incAfter();
      ghostCounters.put(programPoint, ghostPair);
    }
  }

  /**
   * Returns the before ghost counter referenced by programPoint.
   */
  public static int before(String programPoint) {
    GhostPair ghostPair = ghostCounters.get(programPoint);
    if (ghostPair != null)
      return ghostPair.before();
    else
      throw new IllegalArgumentException(String.format("No ghost before counter for program point '%s'",
                                                       programPoint));
  }

  /**
   * Returns the after ghost counter referenced by programPoint.
   */
  public static int after(String programPoint) {
    GhostPair ghostPair = ghostCounters.get(programPoint);
    if (ghostPair != null)
      return ghostPair.after();
    else
      throw new IllegalArgumentException(String.format("No ghost after counter for program point '%s'",
                                                       programPoint));
  }

  /**
   * Returns the value of the named semaphore with name name.
   */
  public static int semaphore(String name) {
    Semaphore s = namedSemaphores.get(name);
    if (s != null)
      return s.value;
    else
      throw new IllegalArgumentException(String.format("No semaphore with name '%s'",
                                                       name));
  }
}
