package es.upm.babel.ccrv;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.upm.babel.cclib.Monitor;
import es.upm.babel.cclib.Monitor.Cond;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
  private static List<Invariant> invariants = new ArrayList<>();

  /**
   * The internal counter of the semaphore.
   */
  private int value;

  @JsonValue
  private int value() {
    return value;
  }

  /**
   * Condition to implement calling process block in await when value < 1
   */
  private Cond queue;

  /**
   * Create a new named semaphore initialized to n.
   */
  public Semaphore(String name, int n) {
    if (n < 0)
      throw new IllegalArgumentException("Semaphore cannot be initialized with a negative value");

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
    P(null);
  }

  /**
   * Version of the Dijkstra's P operation acting on the ghost
   * before-after counters referenced with programPoint.
   */
  public void await(String programPoint) {
    P(programPoint);
  }

  /**
   * The Dijkstra V operation: increments the internal counter.
   */
  public void signal() {
    V(null);
  }

  /**
   * Version of the Dijkstra's V operation acting on the ghost
   * before-after counters referenced with programPoint.
   */
  public void signal(String programPoint) {
    V(programPoint);
  }

  /**
   * Dijkstra's P operation acting on the ghost before-after counters
   * referenced with programPoint if programPoint is not null.
   */
  private void P(String programPoint) {
    mutex.enter();
    incBefore(programPoint);
    value--;
    if (value < 0) {
      checkInvariants();
      queue.await();
    }
    incAfter(programPoint);
    checkInvariants();
    mutex.leave();
  }

  /**
   * Dijkstra's V operation acting on the ghost before-after counters
   * referenced with programPoint if programPoint is not null.
   */
  public void V(String programPoint) {
    mutex.enter();
    incBefore(programPoint);
    incAfter(programPoint);
    value++;
    if (value <= 0 && queue.waiting() > 0)
      queue.signal();
    else
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
    for(Invariant inv : invariants) {
      if (!inv.check()) {
        final int depth = 4;
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        System.err.println("CCRV detected an invariant violation");
        for (int i = depth; i < st.length; i++) {
          System.err.println(String.format("  at %s", st[i]));
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
          String semaphoresJson = mapper.writeValueAsString(namedSemaphores);
          String ghostJson = mapper.writeValueAsString(ghostCounters);
          System.err.println("CCRV semaphores and ghost counters");
          System.err.println(String.format("{ \"semaphores\" : %s, \"counters\" : %s}", semaphoresJson, ghostJson));
        } catch (IOException e) {
          e.printStackTrace();
        }
        System.exit(1);
      }
    }
  }

  /**
   * POJO to represent a pair of before-after ghost counters.
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
    @JsonGetter
    public int before() {
      return before;
    }
    @JsonGetter
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
   * Returns the before ghost counter referenced by
   * programPoint. Returns 0 if the programPoint is not yet
   * registered.
   */
  public static int before(String programPoint) {
    GhostPair ghostPair = ghostCounters.get(programPoint);
    return ghostPair != null ? ghostPair.before() : 0;
  }

  /**
   * Returns the after ghost counter referenced by programPoint.
   * Returns 0 if the programPoint is not yet registered.
   */
  public static int after(String programPoint) {
    GhostPair ghostPair = ghostCounters.get(programPoint);
    return ghostPair != null ? ghostPair.after() : 0;
  }

  /**
   * Returns the value of the named semaphore with name name. Returns
   * 0 if name is not yet registered.
   */
  public static int semaphore(String name) {
    Semaphore s = namedSemaphores.get(name);
    return s != null ? s.value : 0;
  }
}
