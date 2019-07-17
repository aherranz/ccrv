package es.upm.babel.ccrv.p1c;

import es.upm.babel.ccrv.Semaphore;

public class TestP1C
{
  public static void main(String[] args) {
    P1C pc=new P1C();
    P1C.Consumer c=pc.new Consumer();
    P1C.Consumer c2=pc.new Consumer();
    // One consumer...
    P1C.Producer p=pc.new Producer();
    P1C.Producer p2=pc.new Producer();
    // ... and a producer

    Semaphore.addInvariant(() -> {
      return ((Semaphore.after("32") - Semaphore.after("41"))
              + (Semaphore.after("61") - Semaphore.after("70")))
              < 2;
    });

    c.start(); // Start the consumer
    p.start(); // Start the producer
    // c2.start();
    // p2.start();
  } // main

} // class TestProducerConsumer

// jose@tantor8:~/docto/cclib/cclib$ java SingleElementPC.TestSingleElementPC
//      0:	[11] -> Generado elemento: 0
//    740:		[9] -> Obtenido elemento: 0
//   4993:	[11] -> Generado elemento: 11
//  12105:		[9] -> Obtenido elemento: 11
//  16966:	[11] -> Generado elemento: 22
//  19187:		[9] -> Obtenido elemento: 22
//  20973:	[11] -> Generado elemento: 33
//  25106:		[9] -> Obtenido elemento: 33
//  33215:	[11] -> Generado elemento: 44
//  38219:		[9] -> Obtenido elemento: 44
//  42837:	[11] -> Generado elemento: 55
//  44572:		[9] -> Obtenido elemento: 55
//  44983:	[11] -> Generado elemento: 66
//  47439:		[9] -> Obtenido elemento: 66
//  52002:	[11] -> Generado elemento: 77
//  54497:		[9] -> Obtenido elemento: 77
//  62533:	[11] -> Generado elemento: 88
//  68051:		[9] -> Obtenido elemento: 88
//  77324:	[11] -> Generado elemento: 99
//  85881:		[9] -> Obtenido elemento: 99
