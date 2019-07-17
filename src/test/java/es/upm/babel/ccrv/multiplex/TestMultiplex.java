package es.upm.babel.ccrv.multiplex;

import es.upm.babel.ccrv.Semaphore;
import es.upm.babel.ccrv.Invariant;

public class TestMultiplex {

  private static final int MULTIPLEX_CAPACITY=3;
  // How many clients will be admitted simultaneously inside the
  // critical section
  private static Semaphore semMultiplex=new Semaphore(MULTIPLEX_CAPACITY);
  // The semaphore that will control access to the critical section and which
  // will be shared by all the clients.

  public static void main(String[] args) {
    int i;
    MultiplexClient[] arrMutexClients;
    // All the clients that will use the multiplex

    Semaphore.addInvariant(() -> {
      return
        Semaphore.after("mutexIn") - Semaphore.after("mutexOut")
        ==
        Math.min(Semaphore.before("mutexIn") - Semaphore.after("mutexOut"),
                 MULTIPLEX_CAPACITY);
      });
    arrMutexClients=new MultiplexClient[100];
    // A lot of excess capacity, in case we decide to enlarge the example.

    for(i=0;i<10;i++) { // Creating the 10 threads...
      arrMutexClients[i]=new MultiplexClient(semMultiplex,i);
    } // for

    for(i=0;i<10;i++) { // Starting the threads...
      arrMutexClients[i].start();
    } // for
  } // main

} // class

// -------------------------------------
// Escenario sin problemas
//     0:	[9] -> Iteration 0 of thread 0 starts
//    14:		[16] -> Iteration 0 of thread 7 starts
//    15:			[10] -> Iteration 0 of thread 1 starts
//    15:				[11] -> Iteration 0 of thread 2 starts
//    16:					[12] -> Iteration 0 of thread 3 starts
//    16:						[13] -> Iteration 0 of thread 4 starts
//    16:							[14] -> Iteration 0 of thread 5 starts
//    16:								[15] -> Iteration 0 of thread 6 starts
//    16:									[17] -> Iteration 0 of thread 8 starts
//    17:										[18] -> Iteration 0 of thread 9 starts
//    17:	[9] -> Invariant OK: {cmutexIn__m=1, cmutexIn__p=1}
//    17:	[9] -> After await in iteration 0 of thread 0

//    18:		[16] -> Invariant OK: {cmutexIn__m=2, cmutexIn__p=2}
//    18:		[16] -> After await in iteration 0 of thread 7

//    18:			[10] -> Invariant OK: {cmutexIn__m=3, cmutexIn__p=3}
//    18:			[10] -> After await in iteration 0 of thread 1

//    19:				[11] -> Invariant OK: {cmutexIn__m=4, cmutexIn__p=3}
//    19:					[12] -> Invariant OK: {cmutexIn__m=5, cmutexIn__p=3}
//    19:						[13] -> Invariant OK: {cmutexIn__m=6, cmutexIn__p=3}
//    20:							[14] -> Invariant OK: {cmutexIn__m=7, cmutexIn__p=3}
//    20:								[15] -> Invariant OK: {cmutexIn__m=8, cmutexIn__p=3}
//    21:									[17] -> Invariant OK: {cmutexIn__m=9, cmutexIn__p=3}
//    21:										[18] -> Invariant OK: {cmutexIn__m=10, cmutexIn__p=3}
//   321:				[11] -> Invariant OK: {cmutexIn__m=10, cmutexIn__p=4, cmutexOut__m=1, cmutexOut__p=1}
//   321:				[11] -> After await in iteration 0 of thread 2

//   322:	[9] -> Invariant OK: {cmutexIn__m=10, cmutexIn__p=4, cmutexOut__m=1, cmutexOut__p=1}
//   322:	[9] -> After signal in iteration 0 of thread 0

//   497:					[12] -> Invariant OK: {cmutexIn__m=10, cmutexIn__p=5, cmutexOut__m=2, cmutexOut__p=2}
//   498:					[12] -> After await in iteration 0 of thread 3

//   498:		[16] -> Invariant OK: {cmutexIn__m=10, cmutexIn__p=5, cmutexOut__m=2, cmutexOut__p=2}
//   498:		[16] -> After signal in iteration 0 of thread 7

//   635:						[13] -> Invariant OK: {cmutexIn__m=10, cmutexIn__p=6, cmutexOut__m=3, cmutexOut__p=3}
//   635:						[13] -> After await in iteration 0 of thread 4

//   636:			[10] -> Invariant OK: {cmutexIn__m=10, cmutexIn__p=6, cmutexOut__m=3, cmutexOut__p=3}
//   636:			[10] -> After signal in iteration 0 of thread 1

//   655:		[16] -> Iteration 1 of thread 7 starts
//   656:		[16] -> Invariant OK: {cmutexIn__m=11, cmutexIn__p=6, cmutexOut__m=3, cmutexOut__p=3}
//   736:							[14] -> Invariant OK: {cmutexIn__m=11, cmutexIn__p=7, cmutexOut__m=4, cmutexOut__p=4}
//   736:							[14] -> After await in iteration 0 of thread 5

//   737:					[12] -> Invariant OK: {cmutexIn__m=11, cmutexIn__p=7, cmutexOut__m=4, cmutexOut__p=4}
//   737:					[12] -> After signal in iteration 0 of thread 3

//  1004:								[15] -> Invariant OK: {cmutexIn__m=11, cmutexIn__p=8, cmutexOut__m=5, cmutexOut__p=5}
//  1004:								[15] -> After await in iteration 0 of thread 6

//  1004:				[11] -> Invariant OK: {cmutexIn__m=11, cmutexIn__p=8, cmutexOut__m=5, cmutexOut__p=5}
//  1005:				[11] -> After signal in iteration 0 of thread 2

//  1125:	[9] -> Iteration 1 of thread 0 starts
//  1125:	[9] -> Invariant OK: {cmutexIn__m=12, cmutexIn__p=8, cmutexOut__m=5, cmutexOut__p=5}
//  1253:			[10] -> Iteration 1 of thread 1 starts
//  1253:			[10] -> Invariant OK: {cmutexIn__m=13, cmutexIn__p=8, cmutexOut__m=5, cmutexOut__p=5}
//  1261:									[17] -> Invariant OK: {cmutexIn__m=13, cmutexIn__p=9, cmutexOut__m=6, cmutexOut__p=6}
//  1261:									[17] -> After await in iteration 0 of thread 8

//  1261:							[14] -> Invariant OK: {cmutexIn__m=13, cmutexIn__p=9, cmutexOut__m=6, cmutexOut__p=6}
//  1261:							[14] -> After signal in iteration 0 of thread 5

//  1299:										[18] -> Invariant OK: {cmutexIn__m=13, cmutexIn__p=10, cmutexOut__m=7, cmutexOut__p=7}
//  1299:										[18] -> After await in iteration 0 of thread 9

//  1299:									[17] -> Invariant OK: {cmutexIn__m=13, cmutexIn__p=10, cmutexOut__m=7, cmutexOut__p=7}
//  1299:									[17] -> After signal in iteration 0 of thread 8

//  1300:					[12] -> Iteration 1 of thread 3 starts
//  1300:					[12] -> Invariant OK: {cmutexIn__m=14, cmutexIn__p=10, cmutexOut__m=7, cmutexOut__p=7}
//  1432:		[16] -> Invariant OK: {cmutexIn__m=14, cmutexIn__p=11, cmutexOut__m=8, cmutexOut__p=8}
//  1432:		[16] -> After await in iteration 1 of thread 7

//  1432:								[15] -> Invariant OK: {cmutexIn__m=14, cmutexIn__p=11, cmutexOut__m=8, cmutexOut__p=8}
//  1432:								[15] -> After signal in iteration 0 of thread 6

//  1436:							[14] -> Iteration 1 of thread 5 starts
//  1436:							[14] -> Invariant OK: {cmutexIn__m=15, cmutexIn__p=11, cmutexOut__m=8, cmutexOut__p=8}
//  1551:				[11] -> Iteration 1 of thread 2 starts
//  1551:				[11] -> Invariant OK: {cmutexIn__m=16, cmutexIn__p=11, cmutexOut__m=8, cmutexOut__p=8}
//  1559:	[9] -> Invariant OK: {cmutexIn__m=16, cmutexIn__p=12, cmutexOut__m=9, cmutexOut__p=9}
//  1559:	[9] -> After await in iteration 1 of thread 0

//  1559:						[13] -> Invariant OK: {cmutexIn__m=16, cmutexIn__p=12, cmutexOut__m=9, cmutexOut__p=9}
//  1559:						[13] -> After signal in iteration 0 of thread 4

//  1970:			[10] -> Invariant OK: {cmutexIn__m=16, cmutexIn__p=13, cmutexOut__m=10, cmutexOut__p=10}
//  1970:			[10] -> After await in iteration 1 of thread 1

//  1971:										[18] -> Invariant OK: {cmutexIn__m=16, cmutexIn__p=13, cmutexOut__m=10, cmutexOut__p=10}
//  1971:										[18] -> After signal in iteration 0 of thread 9

//  2059:					[12] -> Invariant OK: {cmutexIn__m=16, cmutexIn__p=14, cmutexOut__m=11, cmutexOut__p=11}
//  2059:					[12] -> After await in iteration 1 of thread 3

//  2059:			[10] -> Invariant OK: {cmutexIn__m=16, cmutexIn__p=14, cmutexOut__m=11, cmutexOut__p=11}
//  2059:			[10] -> After signal in iteration 1 of thread 1

//  2102:									[17] -> Iteration 1 of thread 8 starts
//  2103:									[17] -> Invariant OK: {cmutexIn__m=17, cmutexIn__p=14, cmutexOut__m=11, cmutexOut__p=11}
//  2127:						[13] -> Iteration 1 of thread 4 starts
//  2127:						[13] -> Invariant OK: {cmutexIn__m=18, cmutexIn__p=14, cmutexOut__m=11, cmutexOut__p=11}
//  2299:							[14] -> Invariant OK: {cmutexIn__m=18, cmutexIn__p=15, cmutexOut__m=12, cmutexOut__p=12}
//  2299:							[14] -> After await in iteration 1 of thread 5

//  2299:	[9] -> Invariant OK: {cmutexIn__m=18, cmutexIn__p=15, cmutexOut__m=12, cmutexOut__p=12}
//  2299:	[9] -> After signal in iteration 1 of thread 0

//  2329:								[15] -> Iteration 1 of thread 6 starts
//  2330:								[15] -> Invariant OK: {cmutexIn__m=19, cmutexIn__p=15, cmutexOut__m=12, cmutexOut__p=12}
//  2372:			[10] -> Iteration 2 of thread 1 starts
//  2372:			[10] -> Invariant OK: {cmutexIn__m=20, cmutexIn__p=15, cmutexOut__m=12, cmutexOut__p=12}
//  2420:				[11] -> Invariant OK: {cmutexIn__m=20, cmutexIn__p=16, cmutexOut__m=13, cmutexOut__p=13}
//  2421:				[11] -> After await in iteration 1 of thread 2

//  2421:		[16] -> Invariant OK: {cmutexIn__m=20, cmutexIn__p=16, cmutexOut__m=13, cmutexOut__p=13}
//  2421:		[16] -> After signal in iteration 1 of thread 7

//  2429:									[17] -> Invariant OK: {cmutexIn__m=20, cmutexIn__p=17, cmutexOut__m=14, cmutexOut__p=14}
//  2429:									[17] -> After await in iteration 1 of thread 8

//  2429:					[12] -> Invariant OK: {cmutexIn__m=20, cmutexIn__p=17, cmutexOut__m=14, cmutexOut__p=14}
//  2429:					[12] -> After signal in iteration 1 of thread 3

//  2483:						[13] -> Invariant OK: {cmutexIn__m=20, cmutexIn__p=18, cmutexOut__m=15, cmutexOut__p=15}
//  2484:						[13] -> After await in iteration 1 of thread 4

//  2484:							[14] -> Invariant OK: {cmutexIn__m=20, cmutexIn__p=18, cmutexOut__m=15, cmutexOut__p=15}
//  2484:							[14] -> After signal in iteration 1 of thread 5

//  2536:	[9] -> Iteration 2 of thread 0 starts
//  2537:	[9] -> Invariant OK: {cmutexIn__m=21, cmutexIn__p=18, cmutexOut__m=15, cmutexOut__p=15}
//  2741:								[15] -> Invariant OK: {cmutexIn__m=21, cmutexIn__p=19, cmutexOut__m=16, cmutexOut__p=16}
//  2741:								[15] -> After await in iteration 1 of thread 6

//  2741:				[11] -> Invariant OK: {cmutexIn__m=21, cmutexIn__p=19, cmutexOut__m=16, cmutexOut__p=16}
//  2741:				[11] -> After signal in iteration 1 of thread 2

//  2848:					[12] -> Iteration 2 of thread 3 starts
//  2849:					[12] -> Invariant OK: {cmutexIn__m=22, cmutexIn__p=19, cmutexOut__m=16, cmutexOut__p=16}
//  2953:			[10] -> Invariant OK: {cmutexIn__m=22, cmutexIn__p=20, cmutexOut__m=17, cmutexOut__p=17}
//  2953:			[10] -> After await in iteration 2 of thread 1

//  2953:								[15] -> Invariant OK: {cmutexIn__m=22, cmutexIn__p=20, cmutexOut__m=17, cmutexOut__p=17}
//  2954:								[15] -> After signal in iteration 1 of thread 6

//  2958:										[18] -> Iteration 1 of thread 9 starts
//  2959:										[18] -> Invariant OK: {cmutexIn__m=23, cmutexIn__p=20, cmutexOut__m=17, cmutexOut__p=17}
//  3004:							[14] -> Iteration 2 of thread 5 starts
//  3004:							[14] -> Invariant OK: {cmutexIn__m=24, cmutexIn__p=20, cmutexOut__m=17, cmutexOut__p=17}
//  3259:		[16] -> Iteration 2 of thread 7 starts
//  3260:		[16] -> Invariant OK: {cmutexIn__m=25, cmutexIn__p=20, cmutexOut__m=17, cmutexOut__p=17}
//  3273:	[9] -> Invariant OK: {cmutexIn__m=25, cmutexIn__p=21, cmutexOut__m=18, cmutexOut__p=18}
//  3274:	[9] -> After await in iteration 2 of thread 0

//  3274:									[17] -> Invariant OK: {cmutexIn__m=25, cmutexIn__p=21, cmutexOut__m=18, cmutexOut__p=18}
//  3274:									[17] -> After signal in iteration 1 of thread 8

//  3366:				[11] -> Iteration 2 of thread 2 starts
//  3366:					[12] -> Invariant OK: {cmutexIn__m=25, cmutexIn__p=22, cmutexOut__m=19, cmutexOut__p=19}
//  3366:					[12] -> After await in iteration 2 of thread 3

//  3366:						[13] -> Invariant OK: {cmutexIn__m=25, cmutexIn__p=22, cmutexOut__m=19, cmutexOut__p=19}
//  3367:						[13] -> After signal in iteration 1 of thread 4

//  3367:				[11] -> Invariant OK: {cmutexIn__m=26, cmutexIn__p=22, cmutexOut__m=19, cmutexOut__p=19}
//  3490:						[13] -> Iteration 2 of thread 4 starts
//  3490:						[13] -> Invariant OK: {cmutexIn__m=27, cmutexIn__p=22, cmutexOut__m=19, cmutexOut__p=19}
//  3602:									[17] -> Iteration 2 of thread 8 starts
//  3602:									[17] -> Invariant OK: {cmutexIn__m=28, cmutexIn__p=22, cmutexOut__m=19, cmutexOut__p=19}
//  3809:										[18] -> Invariant OK: {cmutexIn__m=28, cmutexIn__p=23, cmutexOut__m=20, cmutexOut__p=20}
//  3809:										[18] -> After await in iteration 1 of thread 9

//  3809:	[9] -> Invariant OK: {cmutexIn__m=28, cmutexIn__p=23, cmutexOut__m=20, cmutexOut__p=20}
//  3810:	[9] -> After signal in iteration 2 of thread 0

//  3917:								[15] -> Iteration 2 of thread 6 starts
//  3918:								[15] -> Invariant OK: {cmutexIn__m=29, cmutexIn__p=23, cmutexOut__m=20, cmutexOut__p=20}
//  3932:							[14] -> Invariant OK: {cmutexIn__m=29, cmutexIn__p=24, cmutexOut__m=21, cmutexOut__p=21}
//  3933:							[14] -> After await in iteration 2 of thread 5

//  3933:			[10] -> Invariant OK: {cmutexIn__m=29, cmutexIn__p=24, cmutexOut__m=21, cmutexOut__p=21}
//  3933:			[10] -> After signal in iteration 2 of thread 1

//  4059:		[16] -> Invariant OK: {cmutexIn__m=29, cmutexIn__p=25, cmutexOut__m=22, cmutexOut__p=22}
//  4059:		[16] -> After await in iteration 2 of thread 7

//  4059:					[12] -> Invariant OK: {cmutexIn__m=29, cmutexIn__p=25, cmutexOut__m=22, cmutexOut__p=22}
//  4059:					[12] -> After signal in iteration 2 of thread 3

//  4072:				[11] -> Invariant OK: {cmutexIn__m=29, cmutexIn__p=26, cmutexOut__m=23, cmutexOut__p=23}
//  4072:				[11] -> After await in iteration 2 of thread 2

//  4072:										[18] -> Invariant OK: {cmutexIn__m=29, cmutexIn__p=26, cmutexOut__m=23, cmutexOut__p=23}
//  4072:										[18] -> After signal in iteration 1 of thread 9

//  4105:	[9] -> Iteration 3 of thread 0 starts
//  4105:	[9] -> Invariant OK: {cmutexIn__m=30, cmutexIn__p=26, cmutexOut__m=23, cmutexOut__p=23}
//  4258:						[13] -> Invariant OK: {cmutexIn__m=30, cmutexIn__p=27, cmutexOut__m=24, cmutexOut__p=24}
//  4258:						[13] -> After await in iteration 2 of thread 4

//  4258:							[14] -> Invariant OK: {cmutexIn__m=30, cmutexIn__p=27, cmutexOut__m=24, cmutexOut__p=24}
//  4258:							[14] -> After signal in iteration 2 of thread 5

//  4337:									[17] -> Invariant OK: {cmutexIn__m=30, cmutexIn__p=28, cmutexOut__m=25, cmutexOut__p=25}
//  4338:									[17] -> After await in iteration 2 of thread 8

//  4338:		[16] -> Invariant OK: {cmutexIn__m=30, cmutexIn__p=28, cmutexOut__m=25, cmutexOut__p=25}
//  4338:		[16] -> After signal in iteration 2 of thread 7

//  4405:								[15] -> Invariant OK: {cmutexIn__m=30, cmutexIn__p=29, cmutexOut__m=26, cmutexOut__p=26}
//  4406:								[15] -> After await in iteration 2 of thread 6

//  4406:						[13] -> Invariant OK: {cmutexIn__m=30, cmutexIn__p=29, cmutexOut__m=26, cmutexOut__p=26}
//  4406:						[13] -> After signal in iteration 2 of thread 4

//  4426:						[13] -> Iteration 3 of thread 4 starts
//  4426:						[13] -> Invariant OK: {cmutexIn__m=31, cmutexIn__p=29, cmutexOut__m=26, cmutexOut__p=26}
//  4461:	[9] -> Invariant OK: {cmutexIn__m=31, cmutexIn__p=30, cmutexOut__m=27, cmutexOut__p=27}
//  4462:	[9] -> After await in iteration 3 of thread 0

//  4462:								[15] -> Invariant OK: {cmutexIn__m=31, cmutexIn__p=30, cmutexOut__m=27, cmutexOut__p=27}
//  4462:								[15] -> After signal in iteration 2 of thread 6

//  4470:						[13] -> Invariant OK: {cmutexIn__m=31, cmutexIn__p=31, cmutexOut__m=28, cmutexOut__p=28}
//  4470:						[13] -> After await in iteration 3 of thread 4

//  4470:									[17] -> Invariant OK: {cmutexIn__m=31, cmutexIn__p=31, cmutexOut__m=28, cmutexOut__p=28}
//  4470:									[17] -> After signal in iteration 2 of thread 8

//  4490:				[11] -> Invariant OK: {cmutexIn__m=31, cmutexIn__p=31, cmutexOut__m=29, cmutexOut__p=29}
//  4490:				[11] -> After signal in iteration 2 of thread 2

//  4561:	[9] -> Invariant OK: {cmutexIn__m=31, cmutexIn__p=31, cmutexOut__m=30, cmutexOut__p=30}
//  4561:	[9] -> After signal in iteration 3 of thread 0

//  4669:						[13] -> Invariant OK: {cmutexIn__m=31, cmutexIn__p=31, cmutexOut__m=31, cmutexOut__p=31}
//  4669:						[13] -> After signal in iteration 3 of thread 4

//  4724:										[18] -> Iteration 2 of thread 9 starts
//  4725:										[18] -> Invariant OK: {cmutexIn__m=32, cmutexIn__p=32, cmutexOut__m=31, cmutexOut__p=31}
//  4725:										[18] -> After await in iteration 2 of thread 9

//  4769:				[11] -> Iteration 3 of thread 2 starts
//  4769:				[11] -> Invariant OK: {cmutexIn__m=33, cmutexIn__p=33, cmutexOut__m=31, cmutexOut__p=31}
//  4769:				[11] -> After await in iteration 3 of thread 2

//  4799:						[13] -> Iteration 4 of thread 4 starts
//  4800:						[13] -> Invariant OK: {cmutexIn__m=34, cmutexIn__p=34, cmutexOut__m=31, cmutexOut__p=31}
//  4800:						[13] -> After await in iteration 4 of thread 4

//  4820:			[10] -> Iteration 3 of thread 1 starts
//  4820:			[10] -> Invariant OK: {cmutexIn__m=35, cmutexIn__p=34, cmutexOut__m=31, cmutexOut__p=31}
//  4826:		[16] -> Iteration 3 of thread 7 starts
//  4826:		[16] -> Invariant OK: {cmutexIn__m=36, cmutexIn__p=34, cmutexOut__m=31, cmutexOut__p=31}
//  4932:								[15] -> Iteration 3 of thread 6 starts
//  4933:								[15] -> Invariant OK: {cmutexIn__m=37, cmutexIn__p=34, cmutexOut__m=31, cmutexOut__p=31}
//  4967:					[12] -> Iteration 3 of thread 3 starts
//  4967:					[12] -> Invariant OK: {cmutexIn__m=38, cmutexIn__p=34, cmutexOut__m=31, cmutexOut__p=31}
//  5162:							[14] -> Iteration 3 of thread 5 starts
//  5163:							[14] -> Invariant OK: {cmutexIn__m=39, cmutexIn__p=34, cmutexOut__m=31, cmutexOut__p=31}
//  5264:			[10] -> Invariant OK: {cmutexIn__m=39, cmutexIn__p=35, cmutexOut__m=32, cmutexOut__p=32}
//  5264:			[10] -> After await in iteration 3 of thread 1

//  5265:						[13] -> Invariant OK: {cmutexIn__m=39, cmutexIn__p=35, cmutexOut__m=32, cmutexOut__p=32}
//  5265:						[13] -> After signal in iteration 4 of thread 4

//  5320:		[16] -> Invariant OK: {cmutexIn__m=39, cmutexIn__p=36, cmutexOut__m=33, cmutexOut__p=33}
//  5320:		[16] -> After await in iteration 3 of thread 7

//  5320:										[18] -> Invariant OK: {cmutexIn__m=39, cmutexIn__p=36, cmutexOut__m=33, cmutexOut__p=33}
//  5320:										[18] -> After signal in iteration 2 of thread 9

//  5389:									[17] -> Iteration 3 of thread 8 starts
//  5389:									[17] -> Invariant OK: {cmutexIn__m=40, cmutexIn__p=36, cmutexOut__m=33, cmutexOut__p=33}
//  5399:										[18] -> Iteration 3 of thread 9 starts
//  5399:										[18] -> Invariant OK: {cmutexIn__m=41, cmutexIn__p=36, cmutexOut__m=33, cmutexOut__p=33}
//  5505:								[15] -> Invariant OK: {cmutexIn__m=41, cmutexIn__p=37, cmutexOut__m=34, cmutexOut__p=34}
//  5505:								[15] -> After await in iteration 3 of thread 6

//  5505:				[11] -> Invariant OK: {cmutexIn__m=41, cmutexIn__p=37, cmutexOut__m=34, cmutexOut__p=34}
//  5505:				[11] -> After signal in iteration 3 of thread 2

//  5528:	[9] -> Iteration 4 of thread 0 starts
//  5529:	[9] -> Invariant OK: {cmutexIn__m=42, cmutexIn__p=37, cmutexOut__m=34, cmutexOut__p=34}
//  5546:					[12] -> Invariant OK: {cmutexIn__m=42, cmutexIn__p=38, cmutexOut__m=35, cmutexOut__p=35}
//  5546:					[12] -> After await in iteration 3 of thread 3

//  5546:		[16] -> Invariant OK: {cmutexIn__m=42, cmutexIn__p=38, cmutexOut__m=35, cmutexOut__p=35}
//  5546:		[16] -> After signal in iteration 3 of thread 7

//  5595:				[11] -> Iteration 4 of thread 2 starts
//  5595:				[11] -> Invariant OK: {cmutexIn__m=43, cmutexIn__p=38, cmutexOut__m=35, cmutexOut__p=35}
//  5719:							[14] -> Invariant OK: {cmutexIn__m=43, cmutexIn__p=39, cmutexOut__m=36, cmutexOut__p=36}
//  5719:							[14] -> After await in iteration 3 of thread 5

//  5719:			[10] -> Invariant OK: {cmutexIn__m=43, cmutexIn__p=39, cmutexOut__m=36, cmutexOut__p=36}
//  5719:			[10] -> After signal in iteration 3 of thread 1

//  5751:		[16] -> Iteration 4 of thread 7 starts
//  5751:		[16] -> Invariant OK: {cmutexIn__m=44, cmutexIn__p=39, cmutexOut__m=36, cmutexOut__p=36}
//  5933:			[10] -> Iteration 4 of thread 1 starts
//  5934:			[10] -> Invariant OK: {cmutexIn__m=45, cmutexIn__p=39, cmutexOut__m=36, cmutexOut__p=36}
//  5939:									[17] -> Invariant OK: {cmutexIn__m=45, cmutexIn__p=40, cmutexOut__m=37, cmutexOut__p=37}
//  5939:									[17] -> After await in iteration 3 of thread 8

//  5939:					[12] -> Invariant OK: {cmutexIn__m=45, cmutexIn__p=40, cmutexOut__m=37, cmutexOut__p=37}
//  5939:					[12] -> After signal in iteration 3 of thread 3

//  6093:						[13] -> Iteration 5 of thread 4 starts
//  6093:						[13] -> Invariant OK: {cmutexIn__m=46, cmutexIn__p=40, cmutexOut__m=37, cmutexOut__p=37}
//  6278:										[18] -> Invariant OK: {cmutexIn__m=46, cmutexIn__p=41, cmutexOut__m=38, cmutexOut__p=38}
//  6278:										[18] -> After await in iteration 3 of thread 9

//  6279:								[15] -> Invariant OK: {cmutexIn__m=46, cmutexIn__p=41, cmutexOut__m=38, cmutexOut__p=38}
//  6279:								[15] -> After signal in iteration 3 of thread 6

//  6404:	[9] -> Invariant OK: {cmutexIn__m=46, cmutexIn__p=42, cmutexOut__m=39, cmutexOut__p=39}
//  6404:	[9] -> After await in iteration 4 of thread 0

//  6404:									[17] -> Invariant OK: {cmutexIn__m=46, cmutexIn__p=42, cmutexOut__m=39, cmutexOut__p=39}
//  6404:									[17] -> After signal in iteration 3 of thread 8

//  6459:				[11] -> Invariant OK: {cmutexIn__m=46, cmutexIn__p=43, cmutexOut__m=40, cmutexOut__p=40}
//  6459:				[11] -> After await in iteration 4 of thread 2

//  6459:	[9] -> Invariant OK: {cmutexIn__m=46, cmutexIn__p=43, cmutexOut__m=40, cmutexOut__p=40}
//  6459:	[9] -> After signal in iteration 4 of thread 0

//  6484:		[16] -> Invariant OK: {cmutexIn__m=46, cmutexIn__p=44, cmutexOut__m=41, cmutexOut__p=41}
//  6485:		[16] -> After await in iteration 4 of thread 7

//  6485:							[14] -> Invariant OK: {cmutexIn__m=46, cmutexIn__p=44, cmutexOut__m=41, cmutexOut__p=41}
//  6485:							[14] -> After signal in iteration 3 of thread 5

//  6491:								[15] -> Iteration 4 of thread 6 starts
//  6491:								[15] -> Invariant OK: {cmutexIn__m=47, cmutexIn__p=44, cmutexOut__m=41, cmutexOut__p=41}
//  6696:	[9] -> Iteration 5 of thread 0 starts
//  6696:	[9] -> Invariant OK: {cmutexIn__m=48, cmutexIn__p=44, cmutexOut__m=41, cmutexOut__p=41}
//  6908:					[12] -> Iteration 4 of thread 3 starts
//  6908:					[12] -> Invariant OK: {cmutexIn__m=49, cmutexIn__p=44, cmutexOut__m=41, cmutexOut__p=41}
//  7100:			[10] -> Invariant OK: {cmutexIn__m=49, cmutexIn__p=45, cmutexOut__m=42, cmutexOut__p=42}
//  7100:			[10] -> After await in iteration 4 of thread 1

//  7100:				[11] -> Invariant OK: {cmutexIn__m=49, cmutexIn__p=45, cmutexOut__m=42, cmutexOut__p=42}
//  7100:				[11] -> After signal in iteration 4 of thread 2

//  7151:						[13] -> Invariant OK: {cmutexIn__m=49, cmutexIn__p=46, cmutexOut__m=43, cmutexOut__p=43}
//  7151:						[13] -> After await in iteration 5 of thread 4

//  7151:										[18] -> Invariant OK: {cmutexIn__m=49, cmutexIn__p=46, cmutexOut__m=43, cmutexOut__p=43}
//  7151:										[18] -> After signal in iteration 3 of thread 9

//  7162:									[17] -> Iteration 4 of thread 8 starts
//  7162:									[17] -> Invariant OK: {cmutexIn__m=50, cmutexIn__p=46, cmutexOut__m=43, cmutexOut__p=43}
//  7428:							[14] -> Iteration 4 of thread 5 starts
//  7428:							[14] -> Invariant OK: {cmutexIn__m=51, cmutexIn__p=46, cmutexOut__m=43, cmutexOut__p=43}
//  7450:										[18] -> Iteration 4 of thread 9 starts
//  7451:										[18] -> Invariant OK: {cmutexIn__m=52, cmutexIn__p=46, cmutexOut__m=43, cmutexOut__p=43}
//  7465:								[15] -> Invariant OK: {cmutexIn__m=52, cmutexIn__p=47, cmutexOut__m=44, cmutexOut__p=44}
//  7465:								[15] -> After await in iteration 4 of thread 6

//  7465:		[16] -> Invariant OK: {cmutexIn__m=52, cmutexIn__p=47, cmutexOut__m=44, cmutexOut__p=44}
//  7465:		[16] -> After signal in iteration 4 of thread 7

//  7477:	[9] -> Invariant OK: {cmutexIn__m=52, cmutexIn__p=48, cmutexOut__m=45, cmutexOut__p=45}
//  7477:	[9] -> After await in iteration 5 of thread 0

//  7478:			[10] -> Invariant OK: {cmutexIn__m=52, cmutexIn__p=48, cmutexOut__m=45, cmutexOut__p=45}
//  7478:			[10] -> After signal in iteration 4 of thread 1

//  7516:			[10] -> Iteration 5 of thread 1 starts
//  7516:			[10] -> Invariant OK: {cmutexIn__m=53, cmutexIn__p=48, cmutexOut__m=45, cmutexOut__p=45}
//  7586:					[12] -> Invariant OK: {cmutexIn__m=53, cmutexIn__p=49, cmutexOut__m=46, cmutexOut__p=46}
//  7586:					[12] -> After await in iteration 4 of thread 3

//  7587:						[13] -> Invariant OK: {cmutexIn__m=53, cmutexIn__p=49, cmutexOut__m=46, cmutexOut__p=46}
//  7587:						[13] -> After signal in iteration 5 of thread 4

//  7796:									[17] -> Invariant OK: {cmutexIn__m=53, cmutexIn__p=50, cmutexOut__m=47, cmutexOut__p=47}
//  7796:									[17] -> After await in iteration 4 of thread 8

//  7796:					[12] -> Invariant OK: {cmutexIn__m=53, cmutexIn__p=50, cmutexOut__m=47, cmutexOut__p=47}
//  7796:					[12] -> After signal in iteration 4 of thread 3

//  7833:		[16] -> Iteration 5 of thread 7 starts
//  7834:		[16] -> Invariant OK: {cmutexIn__m=54, cmutexIn__p=50, cmutexOut__m=47, cmutexOut__p=47}
//  7846:						[13] -> Iteration 6 of thread 4 starts
//  7846:						[13] -> Invariant OK: {cmutexIn__m=55, cmutexIn__p=50, cmutexOut__m=47, cmutexOut__p=47}
//  7904:				[11] -> Iteration 5 of thread 2 starts
//  7904:				[11] -> Invariant OK: {cmutexIn__m=56, cmutexIn__p=50, cmutexOut__m=47, cmutexOut__p=47}
//  7929:							[14] -> Invariant OK: {cmutexIn__m=56, cmutexIn__p=51, cmutexOut__m=48, cmutexOut__p=48}
//  7930:							[14] -> After await in iteration 4 of thread 5

//  7930:								[15] -> Invariant OK: {cmutexIn__m=56, cmutexIn__p=51, cmutexOut__m=48, cmutexOut__p=48}
//  7930:								[15] -> After signal in iteration 4 of thread 6

//  8084:					[12] -> Iteration 5 of thread 3 starts
//  8085:					[12] -> Invariant OK: {cmutexIn__m=57, cmutexIn__p=51, cmutexOut__m=48, cmutexOut__p=48}
//  8191:										[18] -> Invariant OK: {cmutexIn__m=57, cmutexIn__p=52, cmutexOut__m=49, cmutexOut__p=49}
//  8191:										[18] -> After await in iteration 4 of thread 9

//  8191:	[9] -> Invariant OK: {cmutexIn__m=57, cmutexIn__p=52, cmutexOut__m=49, cmutexOut__p=49}
//  8191:	[9] -> After signal in iteration 5 of thread 0

//  8301:			[10] -> Invariant OK: {cmutexIn__m=57, cmutexIn__p=53, cmutexOut__m=50, cmutexOut__p=50}
//  8301:			[10] -> After await in iteration 5 of thread 1

//  8301:							[14] -> Invariant OK: {cmutexIn__m=57, cmutexIn__p=53, cmutexOut__m=50, cmutexOut__p=50}
//  8301:							[14] -> After signal in iteration 4 of thread 5

//  8414:							[14] -> Iteration 5 of thread 5 starts
//  8415:							[14] -> Invariant OK: {cmutexIn__m=58, cmutexIn__p=53, cmutexOut__m=50, cmutexOut__p=50}
//  8499:								[15] -> Iteration 5 of thread 6 starts
//  8499:								[15] -> Invariant OK: {cmutexIn__m=59, cmutexIn__p=53, cmutexOut__m=50, cmutexOut__p=50}
//  8579:		[16] -> Invariant OK: {cmutexIn__m=59, cmutexIn__p=54, cmutexOut__m=51, cmutexOut__p=51}
//  8579:		[16] -> After await in iteration 5 of thread 7

//  8580:									[17] -> Invariant OK: {cmutexIn__m=59, cmutexIn__p=54, cmutexOut__m=51, cmutexOut__p=51}
//  8580:									[17] -> After signal in iteration 4 of thread 8

//  8844:						[13] -> Invariant OK: {cmutexIn__m=59, cmutexIn__p=55, cmutexOut__m=52, cmutexOut__p=52}
//  8844:						[13] -> After await in iteration 6 of thread 4

//  8844:		[16] -> Invariant OK: {cmutexIn__m=59, cmutexIn__p=55, cmutexOut__m=52, cmutexOut__p=52}
//  8844:		[16] -> After signal in iteration 5 of thread 7

//  9008:	[9] -> Iteration 6 of thread 0 starts
//  9008:	[9] -> Invariant OK: {cmutexIn__m=60, cmutexIn__p=55, cmutexOut__m=52, cmutexOut__p=52}
//  9063:				[11] -> Invariant OK: {cmutexIn__m=60, cmutexIn__p=56, cmutexOut__m=53, cmutexOut__p=53}
//  9063:				[11] -> After await in iteration 5 of thread 2

//  9064:										[18] -> Invariant OK: {cmutexIn__m=60, cmutexIn__p=56, cmutexOut__m=53, cmutexOut__p=53}
//  9064:										[18] -> After signal in iteration 4 of thread 9

//  9213:					[12] -> Invariant OK: {cmutexIn__m=60, cmutexIn__p=57, cmutexOut__m=54, cmutexOut__p=54}
//  9213:					[12] -> After await in iteration 5 of thread 3

//  9213:			[10] -> Invariant OK: {cmutexIn__m=60, cmutexIn__p=57, cmutexOut__m=54, cmutexOut__p=54}
//  9213:			[10] -> After signal in iteration 5 of thread 1

//  9299:							[14] -> Invariant OK: {cmutexIn__m=60, cmutexIn__p=58, cmutexOut__m=55, cmutexOut__p=55}
//  9299:							[14] -> After await in iteration 5 of thread 5

//  9299:					[12] -> Invariant OK: {cmutexIn__m=60, cmutexIn__p=58, cmutexOut__m=55, cmutexOut__p=55}
//  9299:					[12] -> After signal in iteration 5 of thread 3

//  9356:									[17] -> Iteration 5 of thread 8 starts
//  9356:									[17] -> Invariant OK: {cmutexIn__m=61, cmutexIn__p=58, cmutexOut__m=55, cmutexOut__p=55}
//  9380:										[18] -> Iteration 5 of thread 9 starts
//  9380:										[18] -> Invariant OK: {cmutexIn__m=62, cmutexIn__p=58, cmutexOut__m=55, cmutexOut__p=55}
//  9487:					[12] -> Iteration 6 of thread 3 starts
//  9488:					[12] -> Invariant OK: {cmutexIn__m=63, cmutexIn__p=58, cmutexOut__m=55, cmutexOut__p=55}
//  9489:			[10] -> Iteration 6 of thread 1 starts
//  9489:			[10] -> Invariant OK: {cmutexIn__m=64, cmutexIn__p=58, cmutexOut__m=55, cmutexOut__p=55}
//  9557:								[15] -> Invariant OK: {cmutexIn__m=64, cmutexIn__p=59, cmutexOut__m=56, cmutexOut__p=56}
//  9557:								[15] -> After await in iteration 5 of thread 6

//  9557:				[11] -> Invariant OK: {cmutexIn__m=64, cmutexIn__p=59, cmutexOut__m=56, cmutexOut__p=56}
//  9557:				[11] -> After signal in iteration 5 of thread 2

//  9558:		[16] -> Iteration 6 of thread 7 starts
//  9559:		[16] -> Invariant OK: {cmutexIn__m=65, cmutexIn__p=59, cmutexOut__m=56, cmutexOut__p=56}
//  9676:	[9] -> Invariant OK: {cmutexIn__m=65, cmutexIn__p=60, cmutexOut__m=57, cmutexOut__p=57}
//  9676:	[9] -> After await in iteration 6 of thread 0

//  9676:						[13] -> Invariant OK: {cmutexIn__m=65, cmutexIn__p=60, cmutexOut__m=57, cmutexOut__p=57}
//  9676:						[13] -> After signal in iteration 6 of thread 4

//  9792:						[13] -> Iteration 7 of thread 4 starts
//  9792:						[13] -> Invariant OK: {cmutexIn__m=66, cmutexIn__p=60, cmutexOut__m=57, cmutexOut__p=57}
//  9831:									[17] -> Invariant OK: {cmutexIn__m=66, cmutexIn__p=61, cmutexOut__m=58, cmutexOut__p=58}
//  9831:									[17] -> After await in iteration 5 of thread 8

//  9831:	[9] -> Invariant OK: {cmutexIn__m=66, cmutexIn__p=61, cmutexOut__m=58, cmutexOut__p=58}
//  9831:	[9] -> After signal in iteration 6 of thread 0

//  9868:				[11] -> Iteration 6 of thread 2 starts
//  9869:				[11] -> Invariant OK: {cmutexIn__m=67, cmutexIn__p=61, cmutexOut__m=58, cmutexOut__p=58}
// 10043:										[18] -> Invariant OK: {cmutexIn__m=67, cmutexIn__p=62, cmutexOut__m=59, cmutexOut__p=59}
// 10043:										[18] -> After await in iteration 5 of thread 9

// 10043:							[14] -> Invariant OK: {cmutexIn__m=67, cmutexIn__p=62, cmutexOut__m=59, cmutexOut__p=59}
// 10043:							[14] -> After signal in iteration 5 of thread 5

// 10052:					[12] -> Invariant OK: {cmutexIn__m=67, cmutexIn__p=63, cmutexOut__m=60, cmutexOut__p=60}
// 10052:					[12] -> After await in iteration 6 of thread 3

// 10053:								[15] -> Invariant OK: {cmutexIn__m=67, cmutexIn__p=63, cmutexOut__m=60, cmutexOut__p=60}
// 10053:								[15] -> After signal in iteration 5 of thread 6

// 10355:			[10] -> Invariant OK: {cmutexIn__m=67, cmutexIn__p=64, cmutexOut__m=61, cmutexOut__p=61}
// 10355:			[10] -> After await in iteration 6 of thread 1

// 10355:										[18] -> Invariant OK: {cmutexIn__m=67, cmutexIn__p=64, cmutexOut__m=61, cmutexOut__p=61}
// 10355:										[18] -> After signal in iteration 5 of thread 9

// 10372:		[16] -> Invariant OK: {cmutexIn__m=67, cmutexIn__p=65, cmutexOut__m=62, cmutexOut__p=62}
// 10372:		[16] -> After await in iteration 6 of thread 7

// 10372:									[17] -> Invariant OK: {cmutexIn__m=67, cmutexIn__p=65, cmutexOut__m=62, cmutexOut__p=62}
// 10372:									[17] -> After signal in iteration 5 of thread 8

// 10432:								[15] -> Iteration 6 of thread 6 starts
// 10432:								[15] -> Invariant OK: {cmutexIn__m=68, cmutexIn__p=65, cmutexOut__m=62, cmutexOut__p=62}
// 10434:							[14] -> Iteration 6 of thread 5 starts
// 10434:							[14] -> Invariant OK: {cmutexIn__m=69, cmutexIn__p=65, cmutexOut__m=62, cmutexOut__p=62}
// 10718:						[13] -> Invariant OK: {cmutexIn__m=69, cmutexIn__p=66, cmutexOut__m=63, cmutexOut__p=63}
// 10718:						[13] -> After await in iteration 7 of thread 4

// 10718:					[12] -> Invariant OK: {cmutexIn__m=69, cmutexIn__p=66, cmutexOut__m=63, cmutexOut__p=63}
// 10718:					[12] -> After signal in iteration 6 of thread 3

// 10795:	[9] -> Iteration 7 of thread 0 starts
// 10796:	[9] -> Invariant OK: {cmutexIn__m=70, cmutexIn__p=66, cmutexOut__m=63, cmutexOut__p=63}
// 10846:				[11] -> Invariant OK: {cmutexIn__m=70, cmutexIn__p=67, cmutexOut__m=64, cmutexOut__p=64}
// 10846:				[11] -> After await in iteration 6 of thread 2

// 10847:		[16] -> Invariant OK: {cmutexIn__m=70, cmutexIn__p=67, cmutexOut__m=64, cmutexOut__p=64}
// 10847:		[16] -> After signal in iteration 6 of thread 7

// 10851:									[17] -> Iteration 6 of thread 8 starts
// 10851:									[17] -> Invariant OK: {cmutexIn__m=71, cmutexIn__p=67, cmutexOut__m=64, cmutexOut__p=64}
// 10905:					[12] -> Iteration 7 of thread 3 starts
// 10906:					[12] -> Invariant OK: {cmutexIn__m=72, cmutexIn__p=67, cmutexOut__m=64, cmutexOut__p=64}
// 10963:								[15] -> Invariant OK: {cmutexIn__m=72, cmutexIn__p=68, cmutexOut__m=65, cmutexOut__p=65}
// 10963:								[15] -> After await in iteration 6 of thread 6

// 10963:			[10] -> Invariant OK: {cmutexIn__m=72, cmutexIn__p=68, cmutexOut__m=65, cmutexOut__p=65}
// 10963:			[10] -> After signal in iteration 6 of thread 1

// 11045:							[14] -> Invariant OK: {cmutexIn__m=72, cmutexIn__p=69, cmutexOut__m=66, cmutexOut__p=66}
// 11045:							[14] -> After await in iteration 6 of thread 5

// 11045:				[11] -> Invariant OK: {cmutexIn__m=72, cmutexIn__p=69, cmutexOut__m=66, cmutexOut__p=66}
// 11045:				[11] -> After signal in iteration 6 of thread 2

// 11045:	[9] -> Invariant OK: {cmutexIn__m=72, cmutexIn__p=70, cmutexOut__m=67, cmutexOut__p=67}
// 11045:	[9] -> After await in iteration 7 of thread 0

// 11045:						[13] -> Invariant OK: {cmutexIn__m=72, cmutexIn__p=70, cmutexOut__m=67, cmutexOut__p=67}
// 11045:						[13] -> After signal in iteration 7 of thread 4

// 11177:										[18] -> Iteration 6 of thread 9 starts
// 11177:										[18] -> Invariant OK: {cmutexIn__m=73, cmutexIn__p=70, cmutexOut__m=67, cmutexOut__p=67}
// 11239:		[16] -> Iteration 7 of thread 7 starts
// 11239:		[16] -> Invariant OK: {cmutexIn__m=74, cmutexIn__p=70, cmutexOut__m=67, cmutexOut__p=67}
// 11320:									[17] -> Invariant OK: {cmutexIn__m=74, cmutexIn__p=71, cmutexOut__m=68, cmutexOut__p=68}
// 11320:									[17] -> After await in iteration 6 of thread 8

// 11320:							[14] -> Invariant OK: {cmutexIn__m=74, cmutexIn__p=71, cmutexOut__m=68, cmutexOut__p=68}
// 11321:							[14] -> After signal in iteration 6 of thread 5

// 11406:					[12] -> Invariant OK: {cmutexIn__m=74, cmutexIn__p=72, cmutexOut__m=69, cmutexOut__p=69}
// 11406:					[12] -> After await in iteration 7 of thread 3

// 11406:									[17] -> Invariant OK: {cmutexIn__m=74, cmutexIn__p=72, cmutexOut__m=69, cmutexOut__p=69}
// 11407:									[17] -> After signal in iteration 6 of thread 8

// 11492:				[11] -> Iteration 7 of thread 2 starts
// 11492:				[11] -> Invariant OK: {cmutexIn__m=75, cmutexIn__p=72, cmutexOut__m=69, cmutexOut__p=69}
// 11511:						[13] -> Iteration 8 of thread 4 starts
// 11511:						[13] -> Invariant OK: {cmutexIn__m=76, cmutexIn__p=72, cmutexOut__m=69, cmutexOut__p=69}
// 11514:							[14] -> Iteration 7 of thread 5 starts
// 11514:							[14] -> Invariant OK: {cmutexIn__m=77, cmutexIn__p=72, cmutexOut__m=69, cmutexOut__p=69}
// 11559:										[18] -> Invariant OK: {cmutexIn__m=77, cmutexIn__p=73, cmutexOut__m=70, cmutexOut__p=70}
// 11559:										[18] -> After await in iteration 6 of thread 9

// 11559:					[12] -> Invariant OK: {cmutexIn__m=77, cmutexIn__p=73, cmutexOut__m=70, cmutexOut__p=70}
// 11559:					[12] -> After signal in iteration 7 of thread 3

// 11672:					[12] -> Iteration 8 of thread 3 starts
// 11672:					[12] -> Invariant OK: {cmutexIn__m=78, cmutexIn__p=73, cmutexOut__m=70, cmutexOut__p=70}
// 11809:		[16] -> Invariant OK: {cmutexIn__m=78, cmutexIn__p=74, cmutexOut__m=71, cmutexOut__p=71}
// 11809:		[16] -> After await in iteration 7 of thread 7

// 11809:								[15] -> Invariant OK: {cmutexIn__m=78, cmutexIn__p=74, cmutexOut__m=71, cmutexOut__p=71}
// 11809:								[15] -> After signal in iteration 6 of thread 6

// 11909:			[10] -> Iteration 7 of thread 1 starts
// 11909:			[10] -> Invariant OK: {cmutexIn__m=79, cmutexIn__p=74, cmutexOut__m=71, cmutexOut__p=71}
// 12029:				[11] -> Invariant OK: {cmutexIn__m=79, cmutexIn__p=75, cmutexOut__m=72, cmutexOut__p=72}
// 12029:				[11] -> After await in iteration 7 of thread 2

// 12029:	[9] -> Invariant OK: {cmutexIn__m=79, cmutexIn__p=75, cmutexOut__m=72, cmutexOut__p=72}
// 12029:	[9] -> After signal in iteration 7 of thread 0

// 12153:									[17] -> Iteration 7 of thread 8 starts
// 12153:									[17] -> Invariant OK: {cmutexIn__m=80, cmutexIn__p=75, cmutexOut__m=72, cmutexOut__p=72}
// 12195:	[9] -> Iteration 8 of thread 0 starts
// 12195:	[9] -> Invariant OK: {cmutexIn__m=81, cmutexIn__p=75, cmutexOut__m=72, cmutexOut__p=72}
// 12337:						[13] -> Invariant OK: {cmutexIn__m=81, cmutexIn__p=76, cmutexOut__m=73, cmutexOut__p=73}
// 12337:						[13] -> After await in iteration 8 of thread 4

// 12337:		[16] -> Invariant OK: {cmutexIn__m=81, cmutexIn__p=76, cmutexOut__m=73, cmutexOut__p=73}
// 12337:		[16] -> After signal in iteration 7 of thread 7

// 12454:							[14] -> Invariant OK: {cmutexIn__m=81, cmutexIn__p=77, cmutexOut__m=74, cmutexOut__p=74}
// 12454:							[14] -> After await in iteration 7 of thread 5

// 12454:										[18] -> Invariant OK: {cmutexIn__m=81, cmutexIn__p=77, cmutexOut__m=74, cmutexOut__p=74}
// 12454:										[18] -> After signal in iteration 6 of thread 9

// 12487:								[15] -> Iteration 7 of thread 6 starts
// 12488:								[15] -> Invariant OK: {cmutexIn__m=82, cmutexIn__p=77, cmutexOut__m=74, cmutexOut__p=74}
// 12720:					[12] -> Invariant OK: {cmutexIn__m=82, cmutexIn__p=78, cmutexOut__m=75, cmutexOut__p=75}
// 12720:					[12] -> After await in iteration 8 of thread 3

// 12720:						[13] -> Invariant OK: {cmutexIn__m=82, cmutexIn__p=78, cmutexOut__m=75, cmutexOut__p=75}
// 12720:						[13] -> After signal in iteration 8 of thread 4

// 12748:						[13] -> Iteration 9 of thread 4 starts
// 12749:						[13] -> Invariant OK: {cmutexIn__m=83, cmutexIn__p=78, cmutexOut__m=75, cmutexOut__p=75}
// 12827:			[10] -> Invariant OK: {cmutexIn__m=83, cmutexIn__p=79, cmutexOut__m=76, cmutexOut__p=76}
// 12828:			[10] -> After await in iteration 7 of thread 1

// 12828:					[12] -> Invariant OK: {cmutexIn__m=83, cmutexIn__p=79, cmutexOut__m=76, cmutexOut__p=76}
// 12828:					[12] -> After signal in iteration 8 of thread 3

// 12856:									[17] -> Invariant OK: {cmutexIn__m=83, cmutexIn__p=80, cmutexOut__m=77, cmutexOut__p=77}
// 12856:									[17] -> After await in iteration 7 of thread 8

// 12856:				[11] -> Invariant OK: {cmutexIn__m=83, cmutexIn__p=80, cmutexOut__m=77, cmutexOut__p=77}
// 12856:				[11] -> After signal in iteration 7 of thread 2

// 12913:	[9] -> Invariant OK: {cmutexIn__m=83, cmutexIn__p=81, cmutexOut__m=78, cmutexOut__p=78}
// 12913:	[9] -> After await in iteration 8 of thread 0

// 12913:							[14] -> Invariant OK: {cmutexIn__m=83, cmutexIn__p=81, cmutexOut__m=78, cmutexOut__p=78}
// 12914:							[14] -> After signal in iteration 7 of thread 5

// 13047:										[18] -> Iteration 7 of thread 9 starts
// 13047:										[18] -> Invariant OK: {cmutexIn__m=84, cmutexIn__p=81, cmutexOut__m=78, cmutexOut__p=78}
// 13061:								[15] -> Invariant OK: {cmutexIn__m=84, cmutexIn__p=82, cmutexOut__m=79, cmutexOut__p=79}
// 13061:								[15] -> After await in iteration 7 of thread 6

// 13061:			[10] -> Invariant OK: {cmutexIn__m=84, cmutexIn__p=82, cmutexOut__m=79, cmutexOut__p=79}
// 13061:			[10] -> After signal in iteration 7 of thread 1

// 13076:							[14] -> Iteration 8 of thread 5 starts
// 13076:							[14] -> Invariant OK: {cmutexIn__m=85, cmutexIn__p=82, cmutexOut__m=79, cmutexOut__p=79}
// 13274:		[16] -> Iteration 8 of thread 7 starts
// 13274:		[16] -> Invariant OK: {cmutexIn__m=86, cmutexIn__p=82, cmutexOut__m=79, cmutexOut__p=79}
// 13307:						[13] -> Invariant OK: {cmutexIn__m=86, cmutexIn__p=83, cmutexOut__m=80, cmutexOut__p=80}
// 13308:						[13] -> After await in iteration 9 of thread 4

// 13308:								[15] -> Invariant OK: {cmutexIn__m=86, cmutexIn__p=83, cmutexOut__m=80, cmutexOut__p=80}
// 13308:								[15] -> After signal in iteration 7 of thread 6

// 13360:										[18] -> Invariant OK: {cmutexIn__m=86, cmutexIn__p=84, cmutexOut__m=81, cmutexOut__p=81}
// 13360:										[18] -> After await in iteration 7 of thread 9

// 13360:									[17] -> Invariant OK: {cmutexIn__m=86, cmutexIn__p=84, cmutexOut__m=81, cmutexOut__p=81}
// 13360:									[17] -> After signal in iteration 7 of thread 8

// 13453:								[15] -> Iteration 8 of thread 6 starts
// 13453:								[15] -> Invariant OK: {cmutexIn__m=87, cmutexIn__p=84, cmutexOut__m=81, cmutexOut__p=81}
// 13514:							[14] -> Invariant OK: {cmutexIn__m=87, cmutexIn__p=85, cmutexOut__m=82, cmutexOut__p=82}
// 13514:							[14] -> After await in iteration 8 of thread 5

// 13514:	[9] -> Invariant OK: {cmutexIn__m=87, cmutexIn__p=85, cmutexOut__m=82, cmutexOut__p=82}
// 13514:	[9] -> After signal in iteration 8 of thread 0

// 13538:				[11] -> Iteration 8 of thread 2 starts
// 13538:				[11] -> Invariant OK: {cmutexIn__m=88, cmutexIn__p=85, cmutexOut__m=82, cmutexOut__p=82}
// 13793:					[12] -> Iteration 9 of thread 3 starts
// 13793:					[12] -> Invariant OK: {cmutexIn__m=89, cmutexIn__p=85, cmutexOut__m=82, cmutexOut__p=82}
// 13823:			[10] -> Iteration 8 of thread 1 starts
// 13824:			[10] -> Invariant OK: {cmutexIn__m=90, cmutexIn__p=85, cmutexOut__m=82, cmutexOut__p=82}
// 13865:		[16] -> Invariant OK: {cmutexIn__m=90, cmutexIn__p=86, cmutexOut__m=83, cmutexOut__p=83}
// 13865:		[16] -> After await in iteration 8 of thread 7

// 13865:										[18] -> Invariant OK: {cmutexIn__m=90, cmutexIn__p=86, cmutexOut__m=83, cmutexOut__p=83}
// 13866:										[18] -> After signal in iteration 7 of thread 9

// 14036:								[15] -> Invariant OK: {cmutexIn__m=90, cmutexIn__p=87, cmutexOut__m=84, cmutexOut__p=84}
// 14036:								[15] -> After await in iteration 8 of thread 6

// 14036:		[16] -> Invariant OK: {cmutexIn__m=90, cmutexIn__p=87, cmutexOut__m=84, cmutexOut__p=84}
// 14036:		[16] -> After signal in iteration 8 of thread 7

// 14089:				[11] -> Invariant OK: {cmutexIn__m=90, cmutexIn__p=88, cmutexOut__m=85, cmutexOut__p=85}
// 14089:				[11] -> After await in iteration 8 of thread 2

// 14089:							[14] -> Invariant OK: {cmutexIn__m=90, cmutexIn__p=88, cmutexOut__m=85, cmutexOut__p=85}
// 14089:							[14] -> After signal in iteration 8 of thread 5

// 14195:									[17] -> Iteration 8 of thread 8 starts
// 14195:									[17] -> Invariant OK: {cmutexIn__m=91, cmutexIn__p=88, cmutexOut__m=85, cmutexOut__p=85}
// 14278:					[12] -> Invariant OK: {cmutexIn__m=91, cmutexIn__p=89, cmutexOut__m=86, cmutexOut__p=86}
// 14278:					[12] -> After await in iteration 9 of thread 3

// 14278:						[13] -> Invariant OK: {cmutexIn__m=91, cmutexIn__p=89, cmutexOut__m=86, cmutexOut__p=86}
// 14278:						[13] -> After signal in iteration 9 of thread 4

// 14299:	[9] -> Iteration 9 of thread 0 starts
// 14299:	[9] -> Invariant OK: {cmutexIn__m=92, cmutexIn__p=89, cmutexOut__m=86, cmutexOut__p=86}
// 14552:										[18] -> Iteration 8 of thread 9 starts
// 14552:										[18] -> Invariant OK: {cmutexIn__m=93, cmutexIn__p=89, cmutexOut__m=86, cmutexOut__p=86}
// 14556:			[10] -> Invariant OK: {cmutexIn__m=93, cmutexIn__p=90, cmutexOut__m=87, cmutexOut__p=87}
// 14556:			[10] -> After await in iteration 8 of thread 1

// 14557:								[15] -> Invariant OK: {cmutexIn__m=93, cmutexIn__p=90, cmutexOut__m=87, cmutexOut__p=87}
// 14557:								[15] -> After signal in iteration 8 of thread 6

// 14570:							[14] -> Iteration 9 of thread 5 starts
// 14570:							[14] -> Invariant OK: {cmutexIn__m=94, cmutexIn__p=90, cmutexOut__m=87, cmutexOut__p=87}
// 14641:									[17] -> Invariant OK: {cmutexIn__m=94, cmutexIn__p=91, cmutexOut__m=88, cmutexOut__p=88}
// 14641:									[17] -> After await in iteration 8 of thread 8

// 14641:			[10] -> Invariant OK: {cmutexIn__m=94, cmutexIn__p=91, cmutexOut__m=88, cmutexOut__p=88}
// 14641:			[10] -> After signal in iteration 8 of thread 1

// 14653:	[9] -> Invariant OK: {cmutexIn__m=94, cmutexIn__p=92, cmutexOut__m=89, cmutexOut__p=89}
// 14653:	[9] -> After await in iteration 9 of thread 0

// 14653:					[12] -> Invariant OK: {cmutexIn__m=94, cmutexIn__p=92, cmutexOut__m=89, cmutexOut__p=89}
// 14653:					[12] -> After signal in iteration 9 of thread 3

// 14663:										[18] -> Invariant OK: {cmutexIn__m=94, cmutexIn__p=93, cmutexOut__m=90, cmutexOut__p=90}
// 14663:										[18] -> After await in iteration 8 of thread 9

// 14663:				[11] -> Invariant OK: {cmutexIn__m=94, cmutexIn__p=93, cmutexOut__m=90, cmutexOut__p=90}
// 14663:				[11] -> After signal in iteration 8 of thread 2

// 14824:		[16] -> Iteration 9 of thread 7 starts
// 14824:		[16] -> Invariant OK: {cmutexIn__m=95, cmutexIn__p=93, cmutexOut__m=90, cmutexOut__p=90}
// 15019:			[10] -> Iteration 9 of thread 1 starts
// 15019:			[10] -> Invariant OK: {cmutexIn__m=96, cmutexIn__p=93, cmutexOut__m=90, cmutexOut__p=90}
// 15044:								[15] -> Iteration 9 of thread 6 starts
// 15044:								[15] -> Invariant OK: {cmutexIn__m=97, cmutexIn__p=93, cmutexOut__m=90, cmutexOut__p=90}
// 15143:							[14] -> Invariant OK: {cmutexIn__m=97, cmutexIn__p=94, cmutexOut__m=91, cmutexOut__p=91}
// 15143:							[14] -> After await in iteration 9 of thread 5

// 15143:									[17] -> Invariant OK: {cmutexIn__m=97, cmutexIn__p=94, cmutexOut__m=91, cmutexOut__p=91}
// 15143:									[17] -> After signal in iteration 8 of thread 8

// 15212:				[11] -> Iteration 9 of thread 2 starts
// 15212:				[11] -> Invariant OK: {cmutexIn__m=98, cmutexIn__p=94, cmutexOut__m=91, cmutexOut__p=91}
// 15247:									[17] -> Iteration 9 of thread 8 starts
// 15247:									[17] -> Invariant OK: {cmutexIn__m=99, cmutexIn__p=94, cmutexOut__m=91, cmutexOut__p=91}
// 15361:		[16] -> Invariant OK: {cmutexIn__m=99, cmutexIn__p=95, cmutexOut__m=92, cmutexOut__p=92}
// 15361:		[16] -> After await in iteration 9 of thread 7

// 15361:							[14] -> Invariant OK: {cmutexIn__m=99, cmutexIn__p=95, cmutexOut__m=92, cmutexOut__p=92}
// 15361:							[14] -> After signal in iteration 9 of thread 5

// 15548:			[10] -> Invariant OK: {cmutexIn__m=99, cmutexIn__p=96, cmutexOut__m=93, cmutexOut__p=93}
// 15548:			[10] -> After await in iteration 9 of thread 1

// 15548:	[9] -> Invariant OK: {cmutexIn__m=99, cmutexIn__p=96, cmutexOut__m=93, cmutexOut__p=93}
// 15548:	[9] -> After signal in iteration 9 of thread 0

// 15575:								[15] -> Invariant OK: {cmutexIn__m=99, cmutexIn__p=97, cmutexOut__m=94, cmutexOut__p=94}
// 15575:								[15] -> After await in iteration 9 of thread 6

// 15575:										[18] -> Invariant OK: {cmutexIn__m=99, cmutexIn__p=97, cmutexOut__m=94, cmutexOut__p=94}
// 15575:										[18] -> After signal in iteration 8 of thread 9

// 15739:										[18] -> Iteration 9 of thread 9 starts
// 15740:										[18] -> Invariant OK: {cmutexIn__m=100, cmutexIn__p=97, cmutexOut__m=94, cmutexOut__p=94}
// 15993:				[11] -> Invariant OK: {cmutexIn__m=100, cmutexIn__p=98, cmutexOut__m=95, cmutexOut__p=95}
// 15993:				[11] -> After await in iteration 9 of thread 2

// 15993:			[10] -> Invariant OK: {cmutexIn__m=100, cmutexIn__p=98, cmutexOut__m=95, cmutexOut__p=95}
// 15993:			[10] -> After signal in iteration 9 of thread 1

// 16020:									[17] -> Invariant OK: {cmutexIn__m=100, cmutexIn__p=99, cmutexOut__m=96, cmutexOut__p=96}
// 16020:									[17] -> After await in iteration 9 of thread 8

// 16020:		[16] -> Invariant OK: {cmutexIn__m=100, cmutexIn__p=99, cmutexOut__m=96, cmutexOut__p=96}
// 16020:		[16] -> After signal in iteration 9 of thread 7

// 16258:										[18] -> Invariant OK: {cmutexIn__m=100, cmutexIn__p=100, cmutexOut__m=97, cmutexOut__p=97}
// 16258:										[18] -> After await in iteration 9 of thread 9

// 16258:				[11] -> Invariant OK: {cmutexIn__m=100, cmutexIn__p=100, cmutexOut__m=97, cmutexOut__p=97}
// 16258:				[11] -> After signal in iteration 9 of thread 2

// 16336:								[15] -> Invariant OK: {cmutexIn__m=100, cmutexIn__p=100, cmutexOut__m=98, cmutexOut__p=98}
// 16336:								[15] -> After signal in iteration 9 of thread 6

// 16822:									[17] -> Invariant OK: {cmutexIn__m=100, cmutexIn__p=100, cmutexOut__m=99, cmutexOut__p=99}
// 16822:									[17] -> After signal in iteration 9 of thread 8

// 17052:										[18] -> Invariant OK: {cmutexIn__m=100, cmutexIn__p=100, cmutexOut__m=100, cmutexOut__p=100}
// 17052:										[18] -> After signal in iteration 9 of thread 9




// --------------------------------------------------

// Escenario con problemas: EL AWAIT RESTA DOS CREDITOS DE SEMAFORO CADA VEZ
// QUE UN THREAD CRUZA EL SEMAFORO:

//      0:	[10] -> Iteration 0 of thread 1 starts
//     17:		[9] -> Iteration 0 of thread 0 starts
//     17:			[11] -> Iteration 0 of thread 2 starts
//     17:				[12] -> Iteration 0 of thread 3 starts
//     18:	[10] -> Invariant OK: {cmutexIn__m=1, cmutexIn__p=1}
//     18:					[13] -> Iteration 0 of thread 4 starts
//     18:						[14] -> Iteration 0 of thread 5 starts
//     19:							[15] -> Iteration 0 of thread 6 starts
//     19:								[16] -> Iteration 0 of thread 7 starts
//     19:									[17] -> Iteration 0 of thread 8 starts
//     19:										[18] -> Iteration 0 of thread 9 starts
//     20:	[10] -> After await in iteration 0 of thread 1

//     20:		[9] -> >>>>> Illegal system state: cIn-=2, cIn+=1, cOut=0
//     20:		[9] -> Thread 0 has received exception >>>>> Illegal system state: cIn-=2, cIn+=1, cOut=0
//     20:			[11] -> >>>>> Illegal system state: cIn-=3, cIn+=1, cOut=0
//     20:			[11] -> Thread 2 has received exception >>>>> Illegal system state: cIn-=3, cIn+=1, cOut=0
// java.lang.IllegalArgumentException: >>>>> Illegal system state: cIn-=3, cIn+=1, cOut=0
//     21:				[12] -> >>>>> Illegal system state: cIn-=4, cIn+=1, cOut=0
// 	at Multiplex.InvariantForMutex.check(InvariantForMutex.java:41)
// 	at es.upm.babel.cclib.MSemaphore.await(MSemaphore.java:241)
// 	at es.upm.babel.cclib.Semaphore.await(Semaphore.java:79)
// 	at Multiplex.MultiplexClient.run(MultiplexClient.java:40)
