package es.upm.babel.ccrv;

/*
 * MSemaphore represents a Monitor-embedded semaphore. All the operations on
 * these semaphores (await, signal) are run inside a monitor, so they cannot
 * overlap. This makes it possible to keep two vital assumptions on how the
 * instrumental counters are incremented:
 * - A signal increments both its pre- and post- counters atomically.
 * - When a signal awakes some thread, the post-signal counter and the
 *   post-await counter are incremented atomically.
 */

import java.util.Hashtable;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections; // sort
import java.util.concurrent.ThreadLocalRandom;
// TESTING: Used to provoke invariant violations

import es.upm.babel.cclib.Semaphore;
import es.upm.babel.cclib.Monitor;
import es.upm.babel.cclib.Monitor.Cond;

import java.util.function.Function;

class MSemaphore {

  public static final int PRE=0;
  public static final int POST=1;

  /*
   * MSem represents a normal semaphore:
   * - A counter
   * - A condition represents the set of threads that are waiting on the
   *   semaphore.
   * - Additionally, a boolean attribute (bMachineNamed) states whether the
   *   semaphore was named by the mathine (the default case) or by the
   *   programmer. This information is only use in 'displayCounters', which
   *   will show the counter of every semaphore named by the programmer along
   *   with the counters. It is assumed that semaphores that have not been
   *   named by the programmer (i.e. those which received an automatic name
   *   created by the machine) are internal semaphores whose behaviour has
   *   no interest.
   */

  private class MSem {
    private int value;
    private Cond queue;
    private boolean bMachineNamed;
    // Was the semaphore automatically named by the machine?

    public MSem(Monitor m,int value,boolean named) {
      this.value=value;
      this.queue=m.newCond();
      this.bMachineNamed=named;
    } // MSem/3

    public int getValue() {return value;}
    public Cond getQueue() {return queue;}
    public void setValue(int value) {this.value=value;}
    public boolean getBMachineNamed() {return bMachineNamed;}

  } // class MSem

    // --------------------------------------------

  private static Monitor monitor;
  private static Hashtable<String,MSem> htMSems;
  // All the semaphores (MSem) known to the system, indexed by name
  // (a String)
  private static Hashtable<String,Integer> htCounters;
  // All the counters (Integer) known to the system, indexed by its name
  private static Semaphore lockSem;
  // A normal Semaphore that is used to implement the atomicity of the
  // increment of a (post-)signal counter and its subsequent post-await
  // counter increment.
  // This semaphore is also used to lock the table of counters while one of
  // them is being incremented. This is not strictly necessary since all
  // MSemaphores are handled inside a monitor, but it seems a good practice...
  private static boolean bLocked;
  // Indicates whether lockSem is closed or not.
  private static Invariant oInvariant;
  // The invariant that will be checked right after every operation on a
  // MSemaphore. If this attribute is null hwn one such operation finishes,
  // no check is performed.

  private static Function<Void,Boolean> fInvariant;
  // A function that takes no argument and returns a Boolean. If it is
  // defined, rhe semaphore primitives (await, signal) will call it
  // instead of calling oInvariant.check(). If this function is called
  // and it returns false, the semaphore primitive will throw an exception

  static{
    // Initializing the system of MSemaphores:
    monitor=new Monitor(); // The monitor that encloses all the MSemaphores
    htMSems=new Hashtable<String,MSem>(); // The table with the semaphores
    htCounters=new Hashtable<String,Integer>(); // The counters
    lockSem=new Semaphore(1);
    // The semaphore used to guarantee that a post-await immediatelly
    // follows a signal when the signalled semaphore had threads waiting.
    bLocked=false;
    // Initaly, lockSem is not closed.
  } // static code

    // Given a line number and a value for pre or post, returns the name
    // of he corresponding counter (cNNm, cNNp):
  private static String getCounterName(String sCounterName,int nPrePost) {
    return "c"+sCounterName+""+(nPrePost==MSemaphore.PRE?"-":"+");
  } // getCounterName

    // Check the invariant defined: If the invariant has been defined as a
    // boolean function, it is evaluated first. A exception is thrown if this
    // evaluation returns false.
    // If no boolean function is defined as invariant, then the object
    // oInvariant (which must implement Invariant is tried by
    // calling the method of such an object.
  private static void check() throws IllegalArgumentException
  {
    Boolean bResultFInvariant;
    String sError;

    if (fInvariant!=null) { // Boolean function defined
      bResultFInvariant=fInvariant.apply(null);
      if (!bResultFInvariant) {
        sError="Illegal system state "+MSemaphore.displayCounters();
        throw new IllegalArgumentException(sError);
      } // if !bResultFInvariant
    } else { // fInvariant is null
      if (oInvariant!=null) {
        oInvariant.check();
      } // if oInvariant!=null
    } // else: fInvariant==null
  } // check

    // Adding a new semaphore to the table of known semaphores. The following
    // values are needed:
    // - A name for the new semaphore (String)
    // - The starting value for the semaphore
    // - Whether 'name' is an String cread by the machine or by the programmer
  public void addMSemaphore(String name, int nInitialValue, boolean bMachineNamed) {
    MSem msem=new MSem(MSemaphore.monitor,nInitialValue,bMachineNamed);

    MSemaphore.htMSems.put(name,msem);
    // We do not even bother to check whether a semaphore by that
    // name already exists.
  } // addMSemaphore

    // Sets the invariant that will be checked right after any operation on any
    // known MSemaphore
  public static void setInvariant(Invariant inv) {
    oInvariant=inv;
  } // setInvariant

    // Sets the boolean function that will be checked right after
    // any operation on any known MSemaphore
  public static void addInvariant(Function<Void,Boolean> fInv) {
    fInvariant=fInv;
  } // addInvariant

    // The 4-th element of the stack is taken because when Semaphore.await
    // (or signal) is called, the stack contains:
    // - getStackTrace (top of the stack)
    // - getLineNumber
    // - MSemaphore.await/signal
    // - Semaphore.await/signal
    // - run method of the class being tested (this is the line we use to
    //   give a name to the counter, which is what this function is ued for:
    //   create counters named cNNm/NNp, where NN is the line number where the
    //   operation on a semaphore occurs)
  public static int getLineNumber() {
    return Thread.currentThread().getStackTrace()[4].getLineNumber();
  } // getLineNumber

    // Increments a counter given by a line number and a position indicator:
    // pre-counter:0, post-counter=1.
    // The other arguments (sSemaphoreName,sOperation) are not used by the
    // moment but could be used in the future to give a more meaningful name
    // for the counters.
    // If the parameter sCounterName is not empty, it is used to give a name
    // to the counters that will be incremented by the await or signal
    // instruction that called this method (instead of using counters with
    // the name based on the line number in which the await or signal
    // instruction appears)
  private static void incCounter(String sSemaphoreName,String sCounterName ,String sOperation,int nLine,int nPrePost) {
    Integer iAuxValue;
    String sCounterNameAux;

    // If incCounter is called with a counter name (i.e., sCounterName
    // is not empty), then that is name is used to form the name of the
    // counter. Otherwise, the line number in which the counter is required
    // is used to calculate the counter's name.
    if (sCounterName.equals(""))
      {sCounterNameAux=getCounterName(""+nLine,nPrePost);}
    else
      {sCounterNameAux=getCounterName(sCounterName,nPrePost);}
    // Given a line number and a position indicator, calculate the name
    // of the corresponding counter
    try {
      lockSem.await(); // No updates are allowed for the counters
      iAuxValue=htCounters.get(sCounterNameAux); // Current counter value
      if (iAuxValue==null)
        {htCounters.put(sCounterNameAux,1);}
      // If it is the first time this counter is used, a value of 1
      // is assigned to it
      else // The counter already exists
        {htCounters.put(sCounterNameAux,iAuxValue.intValue()+1);}
      if (oInvariant!=null) {
        // If an invariant has been defined, set the counters and the
        // semaphores on which it can operate
        oInvariant.setCounters(htCounters);
        oInvariant.setSemaphores(htMSems);
      } // oInvariant!=null
    } catch (Exception ex) {
      throw ex;
    } finally {
      lockSem.signal(); // Updates to the table of counters are allowed again
    } // finally
  } // incCounter

    // Throws an exception if the requested counter does not exist
  public static int getCounter(String sName, int nPrePost) throws ArrayIndexOutOfBoundsException {
    Integer oInt;

    lockSem.await(); // Lock th table of counters
    oInt=htCounters.get(MSemaphore.getCounterName(sName,nPrePost));
    // We use getCounterName to find the name a counter must have
    // according to its own name and position with respect the semaphore
    // operation the counter relates to.
    lockSem.signal(); // Unlock the table of counters
    if (oInt==null) {
      throw new ArrayIndexOutOfBoundsException("Counter named "+sName+" and position "+(nPrePost>0?"POST":"PRE")+" does not exist ("+htCounters.toString()+")");
    } else {
      return oInt.intValue();
    }
  } // getCounter

    // Like getCounter but returns 0 (without exception) if the counter does
    // not exist
  private static int getCounterSafe(String sName, int nPrePost) throws ArrayIndexOutOfBoundsException {
    Integer oInt;

    lockSem.await();
    oInt=htCounters.get(MSemaphore.getCounterName(sName,nPrePost));
    lockSem.signal();
    return (oInt==null)?0:oInt.intValue();
  } // getCounterSafe

    // New interface: 'before' and 'after' repalace 'getCounter'
    // Both functions return 0 if the counter whose name is passed as argument
    // does not exist (i.e. they do not throw exceptions)
    // before: return the value of the PRECOUNTER by that name
  public static int before(String sName) {
    return getCounterSafe(sName,MSemaphore.PRE);
  } // before

    // after: return the value of the POSTCOUNTER by that name
  public static int after(String sName) {
    return getCounterSafe(sName,MSemaphore.POST);
  } // after

    // Given a semaphore name, perform a P operation on it
    // The second argument (which can be empty) are used to give a name
    // to the pre- and post-counters that will be incremented by
    // this operation.
    // If counterName is empty, the name for both counters is calculated
    // based on the line number in which the await instruction appears.
  public static void await(String name, String counterName) {
    MSem msemAux;
    int nLineNumber;

    nLineNumber=getLineNumber();
    // Find the line number within the code this await operation appears in,
    // so we know what counters must be incremented.
    try {
      MSemaphore.monitor.enter(); // Entering the monitor
      msemAux=htMSems.get(name);
      // Lookup the table of semaphores (an MSem is returned)
      if (msemAux!=null) { // If the referenced semaphore exists...
        incCounter(name,counterName,"Await",nLineNumber,PRE);
        // The precounter is incremented. The name and "Await" are not
        // used yet
        msemAux.setValue(msemAux.getValue()-1);
        // Decrement the semaphore's counter. This value can drop
        // below 0.
        if (msemAux.getValue()>=0) // The semaphore has credit
          {} // Nothing is done (in this case, the invariant is checked
        // after the post counter is incremented since both
        // counters are incremented atomically
        else // The semaphore does not have any credit
          {
            MSemaphore.check();
            // Check the invariant, if it is defined
            msemAux.getQueue().await();
            // Send the thread to sleep. This makes the thread
            // leave the monitor (no need for leave and enter)
            // MSemaphore.monitor.leave();
            // MSemaphore.monitor.enter();
          } // msemAux.getValue()<0
        if (bLocked) {bLocked=false; lockSem.signal();}
        // If updates to counters were forbidden and a thread awakes,
        // updates to counters are reallowed.
        // That is, if we were in a state where a signal operation
        // had to awake a thread, that has just happened (this position
        // is right after an await), so reset the indicator and reallow
        // any thread (not just those coming out of an await) to update
        // the table of counters.
        incCounter(name,counterName,"Await",nLineNumber,POST);
        // Increment the counter after the await
        MSemaphore.check();
        // And check the invariant, if one is defined.
      } // if msemAux!=null
    } catch (Exception e) {
      throw e;
    } // catch
    finally {MSemaphore.monitor.leave();}
    // Always leave the monitor
  } // await

    // Performs a V operation on the semaphore whose name is given
    // Given a semaphore name, perform a V operation on it
    // The second argument (which can be empty) are used to give a name
    // to the pre- and post-counters that will be incremented by
    // this operation.
    // If counterName is empty, the name for both counters is calculated
    // based on the line number in which the signal instruction appears.
  public static void signal(String name, String counterName) {
    MSem msemAux;
    int nLineNumber;

    // In order to know what counter must be incremented, find the line
    // number where this signal operation appears in the code.
    nLineNumber=getLineNumber();
    try {
      MSemaphore.monitor.enter(); // Into the monitor...
      msemAux=htMSems.get(name); // Get the corresponding MSem
      if (msemAux!=null) {
        // Since a signal operation never blocks the caller, increment
        // both the pre- and post- counters in one go.
        incCounter(name,counterName,"Signal",nLineNumber,PRE);
        incCounter(name,counterName,"Signal",nLineNumber,POST);
        // Increment the counter of the semaphore
        msemAux.setValue(msemAux.getValue()+1);
        if (msemAux.getValue()>0) // The semaphore has credit
          {}
        else // The semaphore does not have any credit
          {
            // No more counter updates are allowed until a
            // post-await counter is incremented
            msemAux.getQueue().signal(); // Signal one waiting thread
            bLocked=true; // The table of counters is locked until the
            // awaken thread increments the post-counter
            // after the await
            MSemaphore.monitor.leave();
            // Prepare to sleep until a post-await counter is
            // incremented (the monitor cannot be left busy).
            lockSem.await(); // Wait until a post-await counter is
            // incremented.
            MSemaphore.monitor.enter();
            // Regain ownership of the monitor
          }
        // incCounter(nLineNumber,POST);
        MSemaphore.check();
        // Check the invariant, if one is defined.
      } // if msemAux!=null
    } catch (Exception e) {
      throw e;
    } finally {
      MSemaphore.monitor.leave(); // Always leave the monitor
    } // finally
  } // signal

    // Returns whether a semaphore by the given name exists.
  public static boolean existsMSemaphore(String name)
  {return htMSems.get(name)!=null;}

  // Shows the table of counter together with the current counters of
  // programmer-named semaphores.
  // Returns a String (i.e. it is not a procedure)
  public static String displayCounters() {
    String sOutput="{";
    int nNumCounters;
    int i, nCountNamedSemaphores;
    Set<String> sKeys;
    ArrayList<String> lKeys=new ArrayList<String>();

    sKeys=MSemaphore.htCounters.keySet();

    // Convert the set into a list, so we can order it:
    for(String s : sKeys) {lKeys.add(s);}
    Collections.sort(lKeys);

    sOutput+="\n";
    sOutput+="\"counters\" : {";

    nNumCounters=sKeys.size();
    i=0;
    for (String sCounter : lKeys) {
      sOutput+=sCounter;
      sOutput+=" : ";
      sOutput+=MSemaphore.htCounters.get(sCounter);
      i++;
      if (i<nNumCounters) {sOutput+=", ";}
    } // foreach
    sOutput+="}";

    sOutput+="\n";
    sOutput+="\"semaphores\" : {";

    // We now dump named semaphores:
    nCountNamedSemaphores=0;
    sKeys=MSemaphore.htMSems.keySet();
    for (String sSemaphoreName : sKeys) {
      if (!htMSems.get(sSemaphoreName).bMachineNamed) {
        if (nCountNamedSemaphores>0) {sOutput+=", ";}
        sOutput+=sSemaphoreName+" : "+htMSems.get(sSemaphoreName).getValue();
        nCountNamedSemaphores++;
      } // if semaphore not named by machine
    } // for
    sOutput+="}\n";

    sOutput+="}";


    return sOutput;
  } // displayCounters

    // Returns the current counter of a semaphore named by the programmer
    // public static int getValue(String sName) {
    // 	return htMSems.get(sName).getValue();
    // } // getValue

    // Returns the current counter of a semaphore named by the programmer
  public static int semaphore(String sName) {
    return htMSems.get(sName).getValue();
  } // getValue


    // Used to provoke invariant violations
  private static int randomNumber(int min,int max)
  {return ThreadLocalRandom.current().nextInt(min, max + 1);}

  // Returns the number of known semaphores. This number is used when
  // generating names for SSemaphores for which the user has not provided
  // an explicit name.
  public static int getNumberOfSemaphores() {return htMSems.size();}

  // Increments the counter whose name is given (POST side). If the counter
  // does not exist, it is created with value 1.
  // This method is used to track the execution of instructions that are not
  // await or signal.
  public static void checkpoint(String sName) {
    MSemaphore.incCounter("",sName,"",0,MSemaphore.POST);
  } // checkpoint

} // class MSemaphore
