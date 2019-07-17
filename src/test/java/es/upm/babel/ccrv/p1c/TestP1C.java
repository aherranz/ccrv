package es.upm.babel.ccrv.p1c;

// import java.util.concurrent.ThreadLocalRandom;

import es.upm.babel.ccrv.SSemaphore;
import es.upm.babel.ccrv.Invariant;

public class TestP1C
{
    public static void main(String args[]) {
        P1C pc=new P1C();
	P1C.Consumer c=pc.new Consumer();
	P1C.Consumer c2=pc.new Consumer();
	// One consumer...
	P1C.Producer p=pc.new Producer();
	P1C.Producer p2=pc.new Producer();
	// ... and a producer
	Invariant oInv=new InvariantForP1C();
        // We create the invariant we want to enforce in this scenario

        SSemaphore.setInvariant(oInv);
	// ... and pass the invariant to SSemaphore, so from now on, every call
	// an SSemaphore (await/signal) checks whether the invariant holds.

	c.start(); // Start the consumer
	p.start(); // Start the producer
	c2.start();
	p2.start();
    } // main

} // class TestProducerConsumer

// jose@tantor8:~/docto/cclib/cclib$ java P1C.TestP1C
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

// ----------------------------------------------------
// Escenario con dos productores y dos consumidores sin mutex
// Tambien se han eliminado los mensajes OK del invariante, aunque el
// invariante sigue verificandose (no se ha violado, lo que parece indicar
// que, efectivamente, el mutex no era necesario).

// jose@tantor7:~/docto/cclib$ java P1C.TestP1C
//      1:	[11] -> Generado elemento: 0
//   2286:		[9] -> Obtenido elemento: 0
//   2820:			[12] -> Generado elemento: 0
//   5458:		[9] -> Obtenido elemento: 0
//   6611:			[12] -> Generado elemento: 11
//   9249:		[9] -> Obtenido elemento: 11
//  10636:	[11] -> Generado elemento: 11
//  12121:				[10] -> Obtenido elemento: 11
//  13739:			[12] -> Generado elemento: 22
//  16512:				[10] -> Obtenido elemento: 22
//  18254:	[11] -> Generado elemento: 22
//  19968:				[10] -> Obtenido elemento: 22
//  20318:	[11] -> Generado elemento: 33
//  22182:				[10] -> Obtenido elemento: 33
//  24140:	[11] -> Generado elemento: 44
//  26040:				[10] -> Obtenido elemento: 44
//  26770:			[12] -> Generado elemento: 33
//  27836:				[10] -> Obtenido elemento: 33
//  29134:			[12] -> Generado elemento: 44
//  30540:				[10] -> Obtenido elemento: 44
//  31204:			[12] -> Generado elemento: 55
//  33032:		[9] -> Obtenido elemento: 55
//  34789:	[11] -> Generado elemento: 55
//  35664:		[9] -> Obtenido elemento: 55
//  35736:			[12] -> Generado elemento: 66
//  37180:		[9] -> Obtenido elemento: 66
//  37829:			[12] -> Generado elemento: 77
//  40056:		[9] -> Obtenido elemento: 77
//  40648:			[12] -> Generado elemento: 88
//  40750:		[9] -> Obtenido elemento: 88
//  42770:			[12] -> Generado elemento: 99
//  43301:				[10] -> Obtenido elemento: 99
//  43314:	[11] -> Generado elemento: 66
//  45086:				[10] -> Obtenido elemento: 66
//  45416:	[11] -> Generado elemento: 77
//  47413:				[10] -> Obtenido elemento: 77
//  48657:	[11] -> Generado elemento: 88
//  50356:		[9] -> Obtenido elemento: 88
//  52147:	[11] -> Generado elemento: 99
//  54982:		[9] -> Obtenido elemento: 99

//-------------------------------------------------------------------
// Escenario con problemas, creado teniendo 2 productores + 2 consumidores
// Y CREANDO EL SEMAFORO sem CON CREDITO 2 EN LUGAR DE 1

    //  0: [9] -> Invariant OK: {c32m=1, c32p=1} sem=1
    // 14:         [10] -> >>>>> Illegal system state: {c32m=2, c32p=2} sem=0
    // 14:         [10] -> ERROR EN EL CONSUMIDOR: >>>>> Illegal system state: {c32m=2, c32p=2} sem=0
    // 15: [9] -> Invariant OK: {c32m=2, c32p=2, c41m=1, c41p=1} sem=1
    // 15:         [10] -> >>>>> Illegal system state: {c32m=3, c32p=3, c41m=1, c41p=1} sem=0
    // 15:         [10] -> ERROR EN EL CONSUMIDOR: >>>>> Illegal system state: {c32m=3, c32p=3, c41m=1, c41p=1} sem=0
    // 15: [9] -> >>>>> Illegal system state: {c32m=4, c32p=3, c41m=1, c41p=1} sem=-1
    // 16: [9] -> ERROR EN EL CONSUMIDOR: >>>>> Illegal system state: {c32m=4, c32p=3, c41m=1, c41p=1} sem=-1
    // 16:         [10] -> >>>>> Illegal system state: {c32m=5, c32p=3, c41m=1, c41p=1} sem=-2
    // 16:         [10] -> ERROR EN EL CONSUMIDOR: >>>>> Illegal system state: {c32m=5, c32p=3, c41m=1, c41p=1} sem=-2
