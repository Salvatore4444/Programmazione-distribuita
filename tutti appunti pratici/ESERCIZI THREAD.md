<h1 style="color:#C00000"><strong>THREAD — ESERCIZI COMPLETI DEL VAULT</strong></h1>

Tutti gli esercizi del tuo file `3 ESERCIZI/1 THREAD.md`, mantenendo codice, varianti e spiegazioni. Le regole operative sono in [THREAD](THREAD.md), la teoria e le tue domande in [appunti THREAD](../tutti%20appunti/THREAD.md).

**Organizzazione dei file:** ogni variante con una classe `Main`, `Counter` o `Incrementatore` va in una cartella/progetto separato. Non incollare insieme classi con lo stesso nome. Salva ogni classe pubblica in un file con lo stesso nome. Quando è indicato «rimane uguale», riusa la classe della variante precedente.

**Correzioni esplicite rispetto al testo:** aggiunto il quarto parametro `42` alle chiamate di ArrayInitializer; completata la classe ArraySum mancante; resi generali gli estremi dei blocchi della somma; aggiunte versioni complete per la stampa e il Monte Carlo con un numero arbitrario di worker. Race condition e deadlock rimangono esempi intenzionalmente errati, utili per riconoscere il problema.

<h1 style="color:#C00000"><strong>1. CONTATORE: RUN DIRETTO, RACE CONDITION E SYNCHRONIZED</strong></h1>


<h2 style="color:#C00000"><strong>1 PUNTO, UN THREAD, IL MAIN </strong></h2>


3 FILE       .JAVA
Counter
Incrementatore
Main


<h2 style="color:#C00000"><strong>COUNTER</strong></h2>

```java
public class Counter {
    private int valore = 0;

    public void incrementa() {
        valore++;
    }

    public int leggiValore() {
        return valore;
    }
}
```


<h2 style="color:#C00000"><strong>INCREMENTATORE</strong></h2>

```java
public class Incrementatore implements Runnable {
    private final Counter contatore;
    private final int quanteVolte;

    public Incrementatore(Counter contatore, int quanteVolte) {
        this.contatore = contatore;
        this.quanteVolte = quanteVolte;
    }

    @Override
    public void run() {
        for (int i = 0; i < quanteVolte; i++) {
            contatore.incrementa();
        }
    }
}
```


<h2 style="color:#C00000"><strong>MAIN</strong></h2>

```java
public class Main {
    public static void main(String[] args) {
        Counter contatore = new Counter();

        Incrementatore lavoro =
                new Incrementatore(contatore, 40000);

        lavoro.run();

        System.out.println(
                "Valore finale: " + contatore.leggiValore()
        );
    }
}
```


<h2 style="color:#C00000"><strong>2 , 4 THREAD INCREMENTANO LO STESSO CONTATORE MA SENZA PROTEZIONE SULLE RACE CONDITION ECC..</strong></h2>


***!!! Cambia solo il MAIN***

```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Counter contatore = new Counter();
        Thread[] thread = new Thread[4];

        for (int i = 0; i < 4; i++) {
            Incrementatore lavoro =
                    new Incrementatore(contatore, 10000);

            thread[i] = new Thread(lavoro);
            thread[i].start();
        }

        for (Thread t : thread) {
            t.join();
        }

        System.out.println("Valore finale: " + contatore.leggiValore());
    }
}
```



<h2 style="color:#C00000"><strong>3, 4 THREAD SULLO STESSO CONTATORE MA CURANDOSI DELLE RACE CONDITION </strong></h2>

```java
public class Counter {
    private int valore = 0;

    public synchronized void incrementa() {
        valore++;
    }

    public int leggiValore() {
        return valore;
    }
}
```
La parola `synchronized` fa sì che **un solo thread alla volta** possa eseguire `incrementa()` su questo oggetto `Counter`. Gli altri thread aspettano il proprio turno.


<h3 style="color:#C00000"><strong>INCREMENTATORE</strong></h3>

Rimane uguale


<h3 style="color:#C00000"><strong>MAIN</strong></h3>

Rimane uguale al secondo punto



<h1 style="color:#C00000"><strong>2. RIEMPIRE UN ARRAY: UNO, DUE E QUATTRO THREAD</strong></h1>

ARRAY INITIALIZER
```java
public class ArrayInitializer implements Runnable {
    private final int[] array;
    private final int inizio;
    private final int fine;
    private final int valore;

    public ArrayInitializer(int[] array, int inizio, int fine, int valore) {
        this.array = array;
        this.inizio = inizio;
        this.fine = fine;
        this.valore = valore;
    }

    @Override
    public void run() {
        for (int i = inizio; i < fine; i++) {
            array[i] = valore;
        }
    }
}
```


<h3 style="color:#C00000"><strong>RIEMPIMENTO SEQUENZIALE NEL MAIN</strong></h3>
```java
public class Esercizio2Sequenziale {
    public static void main(String[] args) {
        int[] array = new int[1_200_000];

        long inizio = System.nanoTime();

        for (int i = 0; i < array.length; i++) {
            array[i] = 42;
        }

        long durata = System.nanoTime() - inizio;

        System.out.printf(
                "Tempo sequenziale: %.3f ms%n",
                durata / 1_000_000.0
        );
    }
}
```

<h3 style="color:#C00000"><strong>RIEMPIMENTO CON DUE WORKER</strong></h3>
```java
public class Esercizio2DueThread {
    public static void main(String[] args) throws InterruptedException {
        int[] array = new int[1_200_000];
        Thread[] thread = new Thread[2];

        long inizio = System.nanoTime();

        for (int id = 0; id < 2; id++) {
            int da = id * array.length / 2;
            int a = (id + 1) * array.length / 2;

            ArrayInitializer lavoro =
                    new ArrayInitializer(array, da, a, 42);

            thread[id] = new Thread(lavoro);
            thread[id].start();
        }

        for (Thread t : thread) {
            t.join();
        }

        long durata = System.nanoTime() - inizio;

        System.out.printf(
                "Tempo con 2 thread: %.3f ms%n",
                durata / 1_000_000.0
        );
    }
}
```

<h3 style="color:#C00000"><strong>RIEMPIMENTO CON QUATTRO WORKER E RUNNABLE ANONIMO</strong></h3>
```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        int dimensione = 1_200_000;

        int[] arraySequenziale = new int[dimensione];
        int[] arrayQuattroThread = new int[dimensione];


        // Misuriamo il riempimento sequenziale
        long inizioSequenziale = System.nanoTime();

        for (int i = 0; i < arraySequenziale.length; i++) {
            arraySequenziale[i] = 42;
        }

        long tempoSequenziale =
                System.nanoTime() - inizioSequenziale;


        // Misuriamo il riempimento con 4 thread
        Thread[] thread = new Thread[4];
        long inizioQuattroThread = System.nanoTime();

        for (int id = 0; id < 4; id++) {
            int da = id * arrayQuattroThread.length / 4;
            int a = (id + 1) * arrayQuattroThread.length / 4;

            thread[id] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = da; i < a; i++) {
                        arrayQuattroThread[i] = 42;
                    }
                }
            });

            thread[id].start();
        }

        for (Thread t : thread) {
            t.join();
        }

        long tempoQuattroThread =
                System.nanoTime() - inizioQuattroThread;


        // Calcoliamo e stampiamo lo speedup
        double speedup =
                (double) tempoSequenziale / tempoQuattroThread;

        System.out.printf("Tempo sequenziale: %.3f ms%n",
                tempoSequenziale / 1_000_000.0);

        System.out.printf("Tempo con 4 thread: %.3f ms%n",
                tempoQuattroThread / 1_000_000.0);

        System.out.printf("Speedup: %.2f%n", speedup);
    }
}
```


Frammento alternativo da inserire nel ciclo che dispone di array, inizio, fine, threads e id:

```java
ArrayInitializer lavoro =
        new ArrayInitializer(array, inizio, fine, 42);

threads[id] = new Thread(lavoro);
threads[id].start();
```




<h1 style="color:#C00000"><strong>3. SOMMA DI DUE ARRAY: SEQUENZIALE E SEDICI THREAD</strong></h1>

<h3 style="color:#C00000"><strong>SOMMA NEL THREAD MAIN</strong></h3>

```java
public class SommaSequenziale {
    public static void main(String[] args) {
        int dimensione = 1_200_000;

        int[] arrayA = new int[dimensione];
        int[] arrayB = new int[dimensione];
        int[] risultato = new int[dimensione];

        // Prepariamo i due array
        for (int i = 0; i < dimensione; i++) {
            arrayA[i] = i;
            arrayB[i] = i * 2;
        }

        // Misuriamo il tempo impiegato per la somma
        long inizio = System.nanoTime();

        // Sommiamo gli elementi allo stesso indice
        for (int i = 0; i < dimensione; i++) {
            risultato[i] = arrayA[i] + arrayB[i];
        }

        long tempo = System.nanoTime() - inizio;

        System.out.println("Tempo sequenziale: " + tempo + " nanosecondi");
        System.out.println("Primo risultato: " + risultato[0]);
        System.out.println("Ultimo risultato: " + risultato[dimensione - 1]);
    }
}
```

<h3 style="color:#C00000"><strong>ARRAYSUM.JAVA: COMPLETAMENTO NECESSARIO</strong></h3>

La classe era richiamata nel tuo Main ma non definita: salva questo file insieme a SommaConSediciThread. Gli intervalli sono [inizio, fine), senza sovrapposizioni; il main legge i risultati dopo tutte le join.

```java
public class ArraySum implements Runnable {
    private final int[] a, b, risultato;
    private final int inizio, fine;

    public ArraySum(int[] a, int[] b, int[] risultato, int inizio, int fine) {
        this.a = a;
        this.b = b;
        this.risultato = risultato;
        this.inizio = inizio;
        this.fine = fine;
    }

    @Override
    public void run() {
        for (int i = inizio; i < fine; i++) {
            risultato[i] = a[i] + b[i];
        }
    }
}
```

<h3 style="color:#C00000"><strong>SOMMA CON SEDICI THREAD</strong></h3>
```java
public class SommaConSediciThread {
    public static void main(String[] args) throws InterruptedException {
        int dimensione = 1_200_000;
        int numeroThread = 16;

        int[] arrayA = new int[dimensione];
        int[] arrayB = new int[dimensione];
        int[] risultato = new int[dimensione];

        // Prepariamo i due array
        for (int i = 0; i < dimensione; i++) {
            arrayA[i] = i;
            arrayB[i] = i * 2;
        }

        Thread[] threads = new Thread[numeroThread];

        // Creiamo i sedici thread e assegniamo a ciascuno la sua parte
        for (int id = 0; id < numeroThread; id++) {
            int inizio = id * dimensione / numeroThread;
            int fine = (id + 1) * dimensione / numeroThread;

            ArraySum lavoro = new ArraySum(
                    arrayA,
                    arrayB,
                    risultato,
                    inizio,
                    fine
            );

            threads[id] = new Thread(lavoro);
        }

        long inizioTempo = System.nanoTime();

        // Avviamo tutti i sedici thread
        for (int id = 0; id < numeroThread; id++) {
            threads[id].start();
        }

        // Aspettiamo che tutti abbiano finito
        for (int id = 0; id < numeroThread; id++) {
            threads[id].join();
        }

        long tempo = System.nanoTime() - inizioTempo;

        System.out.println("Tempo con 16 thread: " + tempo + " nanosecondi");
        System.out.println("Primo risultato: " + risultato[0]);
        System.out.println("Ultimo risultato: " + risultato[dimensione - 1]);
    }
}
```



<h1 style="color:#C00000"><strong>4. STAMPE COMPOSTE E SEZIONI CRITICHE</strong></h1>

```java
synchronized (System.out) {
    printDuePunti();
    printTrattatino();
    printParentesi();
}
```




<h3 style="color:#C00000"><strong>VERSIONE ESEGUIBILE: STAMPACOORDINATA.JAVA</strong></h3>

Il blocco originale è un frammento di metodo. Qui definiamo anche le tre stampe e i worker. Il lock protegge tutta la sequenza `:-)`: senza il blocco, singole chiamate print possono alternarsi con quelle di altri thread. Tutti i partecipanti devono rispettare lo stesso monitor.

```java
public class StampaCoordinata {
    private static void printDuePunti() { System.out.print(":"); }
    private static void printTrattatino() { System.out.print("-"); }
    private static void printParentesi() { System.out.print(")"); }

    public static void main(String[] args) throws InterruptedException {
        Runnable lavoro = () -> {
            for (int i = 0; i < 100; i++) {
                synchronized (System.out) {
                    printDuePunti();
                    printTrattatino();
                    printParentesi();
                }
            }
        };
        Thread t1 = new Thread(lavoro);
        Thread t2 = new Thread(lavoro);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println();
    }
}
```

<h1 style="color:#C00000"><strong>5. MONTE CARLO: STIMA DI PI GRECO</strong></h1>


<h2 style="color:#C00000"><strong>SOTTOESERCIZIO 1: VERSIONE SEQUENZIALE</strong></h2>


In questa prima versione, il thread `main` genera e controlla tutti i punti, uno dopo l’altro. Il calcolo di π usa il metodo Monte Carlo: generiamo punti uniformi nel quadrato [0,1) × [0,1), contiamo quelli nel quarto di cerchio x² + y² ≤ 1 e stimiamo π = 4 × puntiDentro / numeroPunti.

```java
import java.util.Random;

public class CalcoloPiSequenziale {
    public static void main(String[] args) {
        long numeroPunti = 10_000_000L;
        long puntiDentro = 0;

        Random casuale = new Random(42L);

        long inizio = System.nanoTime();

        for (long i = 0; i < numeroPunti; i++) {
            double x = casuale.nextDouble();
            double y = casuale.nextDouble();

            if (x * x + y * y <= 1.0) {
                puntiDentro++;
            }
        }

        long tempo = System.nanoTime() - inizio;

        double piStimato = 4.0 * puntiDentro / numeroPunti;

        System.out.printf("Pi greco stimato: %.8f%n", piStimato);
        System.out.printf("Tempo sequenziale: %.3f ms%n",
                tempo / 1_000_000.0);
    }
}
```




<h3 style="color:#C00000"><strong>MONTE CARLO CON UN WORKER</strong></h3>
```java
import java.util.Random;

public class CalcoloPiUnThread {
    public static void main(String[] args) throws InterruptedException {
        final long numeroPunti = 10_000_000L;
        final long seme = 42L;

        // Calcolo sequenziale di riferimento
        Random casualeSequenziale = new Random(seme);
        long puntiDentroSequenziale = 0;

        long inizioSequenziale = System.nanoTime();

        for (long i = 0; i < numeroPunti; i++) {
            double x = casualeSequenziale.nextDouble();
            double y = casualeSequenziale.nextDouble();

            if (x * x + y * y <= 1.0) {
                puntiDentroSequenziale++;
            }
        }

        long tempoSequenziale =
                System.nanoTime() - inizioSequenziale;

        // Contenitore per il risultato calcolato dal worker
        final long[] risultatoThread = {0};

        // Crea il worker e inserisce il suo lavoro nella lambda
        Thread worker = new Thread(() -> {
            Random casualeThread = new Random(seme);
            long puntiDentro = 0;

            for (long i = 0; i < numeroPunti; i++) {
                double x = casualeThread.nextDouble();
                double y = casualeThread.nextDouble();

                if (x * x + y * y <= 1.0) {
                    puntiDentro++;
                }
            }

            risultatoThread[0] = puntiDentro;
        });

        long inizioThread = System.nanoTime();

        worker.start(); // Avvia il thread worker
        worker.join();  // Aspetta che termini

        long tempoThread = System.nanoTime() - inizioThread;

        double piSequenziale =
                4.0 * puntiDentroSequenziale / numeroPunti;
        double piConThread =
                4.0 * risultatoThread[0] / numeroPunti;
        double speedup =
                (double) tempoSequenziale / tempoThread;

        System.out.printf("Pi sequenziale: %.8f%n", piSequenziale);
        System.out.printf("Tempo sequenziale: %.3f ms%n",
                tempoSequenziale / 1_000_000.0);

        System.out.printf("%nPi con un thread: %.8f%n", piConThread);
        System.out.printf("Tempo con un thread: %.3f ms%n",
                tempoThread / 1_000_000.0);
        System.out.printf("Speedup: %.2f%n", speedup);
    }
}
```

<h3 style="color:#C00000"><strong>MONTE CARLO CON DUE WORKER</strong></h3>
```java
import java.util.Random;

public class CalcoloPiDueThread {
    public static void main(String[] args) throws InterruptedException {
        final long numeroPunti = 10_000_000L;
        final long seme = 42L;

        // Calcolo sequenziale di riferimento
        Random casualeSequenziale = new Random(seme);
        long puntiDentroSequenziale = 0;

        long inizioSequenziale = System.nanoTime();

        for (long i = 0; i < numeroPunti; i++) {
            double x = casualeSequenziale.nextDouble();
            double y = casualeSequenziale.nextDouble();

            if (x * x + y * y <= 1.0) {
                puntiDentroSequenziale++;
            }
        }

        long tempoSequenziale =
                System.nanoTime() - inizioSequenziale;

        // NUOVO: divido i punti tra i due worker
        final long puntiPrimoThread = numeroPunti / 2;
        final long puntiSecondoThread = numeroPunti - puntiPrimoThread;

        // NUOVO: uno spazio per il risultato di ciascun worker
        final long[] risultatiThread = new long[2];

        // NUOVO: il primo worker calcola la prima metà
        Thread worker1 = new Thread(() -> {
            Random casualeThread = new Random(seme + 1);
            long puntiDentro = 0;

            for (long i = 0; i < puntiPrimoThread; i++) {
                double x = casualeThread.nextDouble();
                double y = casualeThread.nextDouble();

                if (x * x + y * y <= 1.0) {
                    puntiDentro++;
                }
            }

            risultatiThread[0] = puntiDentro;
        });

        // NUOVO: il secondo worker calcola l'altra metà
        Thread worker2 = new Thread(() -> {
            Random casualeThread = new Random(seme + 2);
            long puntiDentro = 0;

            for (long i = 0; i < puntiSecondoThread; i++) {
                double x = casualeThread.nextDouble();
                double y = casualeThread.nextDouble();

                if (x * x + y * y <= 1.0) {
                    puntiDentro++;
                }
            }

            risultatiThread[1] = puntiDentro;
        });

        long inizioThread = System.nanoTime();

        // NUOVO: avvio entrambi prima di aspettarli
        worker1.start();
        worker2.start();

        // NUOVO: aspetto che entrambi finiscano
        worker1.join();
        worker2.join();

        long tempoThread = System.nanoTime() - inizioThread;

        // NUOVO: sommo i risultati dei due worker
        long puntiDentroConThread =
                risultatiThread[0] + risultatiThread[1];

        double piSequenziale =
                4.0 * puntiDentroSequenziale / numeroPunti;
        double piConThread =
                4.0 * puntiDentroConThread / numeroPunti;
        double speedup =
                (double) tempoSequenziale / tempoThread;

        System.out.printf("Pi sequenziale: %.8f%n", piSequenziale);
        System.out.printf("Tempo sequenziale: %.3f ms%n",
                tempoSequenziale / 1_000_000.0);

        System.out.printf("%nPi con 2 worker: %.8f%n", piConThread);
        System.out.printf("Tempo con 2 worker: %.3f ms%n",
                tempoThread / 1_000_000.0);
        System.out.printf("Speedup: %.2f%n", speedup);
    }
}
```




<h2 style="color:#C00000"><strong>VARIANTE CON FOR: FRAMMENTO ORIGINALE</strong></h2>

```java
final int numeroThread = 4;
final long puntiPerThread = numeroPunti / numeroThread;
final long[] risultatiThread = new long[numeroThread];
Thread[] workers = new Thread[numeroThread];

for (int t = 0; t < numeroThread; t++) {
    final int indice = t;

    workers[t] = new Thread(() -> {
        Random casuale = new Random(seme + indice + 1);
        long puntiDentro = 0;

        for (long i = 0; i < puntiPerThread; i++) {
            double x = casuale.nextDouble();
            double y = casuale.nextDouble();

            if (x * x + y * y <= 1.0) {
                puntiDentro++;
            }
        }

        risultatiThread[indice] = puntiDentro;
    });
}
```

![Ripartizione originale di 17 punti su 5 worker](../tutti%20appunti/immagini/ripartizione-17-punti.png)

**Limite del frammento precedente:** costruisce soltanto i worker; mancano start, join e somma finale. Inoltre usa una divisione intera: funziona senza perdere punti solo se numeroPunti è divisibile per numeroThread. La versione completa sotto gestisce anche il resto.



<h3 style="color:#C00000"><strong>VERSIONE COMPLETA: CALCOLOPINTHREAD.JAVA</strong></h3>

Con 17 punti e 5 thread: base = 3, resto = 2; i primi due worker ricevono un punto in più, quindi `[4, 4, 3, 3, 3]`. La somma deve essere 17. Se i worker superano i punti, alcuni ricevono zero punti: nessun punto viene perso. Ogni worker accumula localmente e scrive una cella distinta; il main riduce i risultati dopo le join.

```java
import java.util.Random;

public class CalcoloPiNThread {
    public static void main(String[] args) throws InterruptedException {
        final long numeroPunti = args.length > 0 ? Long.parseLong(args[0]) : 10_000_000L;
        final int numeroThread = args.length > 1 ? Integer.parseInt(args[1]) : 4;
        if (numeroPunti <= 0 || numeroThread <= 0) {
            throw new IllegalArgumentException("Punti e numero di thread devono essere positivi");
        }
        final long seme = 42L;
        final long base = numeroPunti / numeroThread;
        final long resto = numeroPunti % numeroThread;
        final long[] risultati = new long[numeroThread];
        Thread[] workers = new Thread[numeroThread];

        long inizio = System.nanoTime();
        for (int t = 0; t < numeroThread; t++) {
            final int indice = t;
            final long puntiWorker = base + (indice < resto ? 1 : 0);
            workers[t] = new Thread(() -> {
                Random casuale = new Random(seme + indice + 1);
                long dentro = 0;
                for (long i = 0; i < puntiWorker; i++) {
                    double x = casuale.nextDouble();
                    double y = casuale.nextDouble();
                    if (x * x + y * y <= 1.0) dentro++;
                }
                risultati[indice] = dentro;
            });
        }
        for (Thread worker : workers) worker.start();
        for (Thread worker : workers) worker.join();
        long puntiDentro = 0;
        for (long risultato : risultati) puntiDentro += risultato;
        long tempo = System.nanoTime() - inizio;
        System.out.printf("Pi con %d worker: %.8f%n", numeroThread,
                4.0 * puntiDentro / numeroPunti);
        System.out.printf("Punti distribuiti: %d%n", base * numeroThread + resto);
        System.out.printf("Tempo: %.3f ms%n", tempo / 1_000_000.0);
    }
}
```

<h1 style="color:#C00000"><strong>6. DEADLOCK CON DUE E TRE THREAD</strong></h1>

**Esempi intenzionali:** questi due programmi non terminano da soli. La latch fa acquisire a tutti il primo lock prima di chiedere il secondo; await non rilascia il monitor già posseduto. Eseguili separatamente e interrompi il processo dopo aver osservato il ciclo di attesa.

```java
import java.util.concurrent.CountDownLatch;

public class DeadlockDueThread {

    static class Worker implements Runnable {
        private final String nome;
        private final Object primoLock;
        private final Object secondoLock;
        private final CountDownLatch entrambiPronti;

        Worker(
                String nome,
                Object primoLock,
                Object secondoLock,
                CountDownLatch entrambiPronti) {
            this.nome = nome;
            this.primoLock = primoLock;
            this.secondoLock = secondoLock;
            this.entrambiPronti = entrambiPronti;
        }

        @Override
        public void run() {
            synchronized (primoLock) {
                System.out.println(nome + " ha preso il primo lock");

                // Segnala che questo thread ha preso il suo primo lock
                entrambiPronti.countDown();

                try {
                    // Aspetta che anche l'altro thread abbia preso il suo lock
                    entrambiPronti.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                System.out.println(nome + " prova a prendere il secondo lock");

                synchronized (secondoLock) {
                    System.out.println(nome + " ha preso anche il secondo lock");
                }
            }
        }
    }

    public static void main(String[] args) {
        Object lockA = new Object();
        Object lockB = new Object();

        CountDownLatch entrambiPronti = new CountDownLatch(2);

        Thread t1 = new Thread(
                new Worker("T1", lockA, lockB, entrambiPronti)
        );

        Thread t2 = new Thread(
                new Worker("T2", lockB, lockA, entrambiPronti)
        );

        t1.start();
        t2.start();
    }
}
```




1. **T1 esegue il suo `run()`.** Per T1, `primoLock` è A:

    ```java
    synchronized (primoLock) {
    ```

    Quindi prende A.

2. T1 esegue:

    ```java
    entrambiPronti.countDown(); // contatore: 2 → 1
    entrambiPronti.await();     // T1 si ferma qui
    ```

    T1 aspetta, tenendo ancora A.

3. **T2 esegue il suo `run()`.** Per T2, `primoLock` è B. Quindi prende B: può farlo perché T1 sta tenendo A.

4. T2 esegue:

    ```java
    entrambiPronti.countDown(); // contatore: 1 → 0
    entrambiPronti.await();     // passa subito: il contatore è zero
    ```

    Il contatore a zero sblocca anche T1.

5. **Entrambi proseguono nel proprio `run()`:**

    - T1 tiene A e cerca di prendere B.
    - T2 tiene B e cerca di prendere A.

Ora ciascuno aspetta il lock posseduto dall’altro: è il deadlock.



<h3 style="color:#C00000"><strong>DEADLOCK CON TRE THREAD</strong></h3>
```java
import java.util.concurrent.CountDownLatch;

public class DeadlockTreThread {

    static class Worker implements Runnable {
        private final String nome;
        private final Object primoLock;
        private final Object secondoLock;
        private final CountDownLatch tuttiPronti;

        Worker(
                String nome,
                Object primoLock,
                Object secondoLock,
                CountDownLatch tuttiPronti) {
            this.nome = nome;
            this.primoLock = primoLock;
            this.secondoLock = secondoLock;
            this.tuttiPronti = tuttiPronti;
        }

        @Override
        public void run() {
            synchronized (primoLock) {
                System.out.println(nome + " ha preso il primo lock");

                tuttiPronti.countDown();

                try {
                    tuttiPronti.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                System.out.println(nome + " prova a prendere il secondo lock");

                synchronized (secondoLock) {
                    System.out.println(nome + " ha preso anche il secondo lock");
                }
            }
        }
    }

    public static void main(String[] args) {
        Object lockA = new Object();
        Object lockB = new Object();
        Object lockC = new Object();

        CountDownLatch tuttiPronti = new CountDownLatch(3);

        Thread t1 = new Thread(
                new Worker("T1", lockA, lockB, tuttiPronti)
        );

        Thread t2 = new Thread(
                new Worker("T2", lockB, lockC, tuttiPronti)
        );

        Thread t3 = new Thread(
                new Worker("T3", lockC, lockA, tuttiPronti)
        );

        t1.start();
        t2.start();
        t3.start();
    }
}
```



<h2 style="color:#C00000"><strong>RITORNO AL CONTATORE: VARIANTE CON OTTO WORKER</strong></h2>

Questo Main riguarda il contatore senza protezione della sezione 1: usa quei Counter e Incrementatore, in un progetto distinto dai deadlock. La versione estesa successiva ripropone la prova con tutti e tre i file.

```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        int numeroThread = 8;
        int incrementiPerThread = 1_000_000;

        Counter counter = new Counter();
        Thread[] threads = new Thread[numeroThread];

        // Creiamo tutti i thread usando lo stesso counter
        for (int i = 0; i < numeroThread; i++) {
            Incrementatore lavoro =
                    new Incrementatore(counter, incrementiPerThread);

            threads[i] = new Thread(lavoro);
        }

        long inizio = System.nanoTime();

        // Avviamo i thread
        for (int i = 0; i < numeroThread; i++) {
            threads[i].start();
        }

        // Aspettiamo che terminino tutti
        for (int i = 0; i < numeroThread; i++) {
            threads[i].join();
        }

        long tempo = System.nanoTime() - inizio;
        long valoreAtteso =
                (long) numeroThread * incrementiPerThread;

        System.out.println("Valore atteso: " + valoreAtteso);
        System.out.println("Valore ottenuto: " + counter.leggiValore());
        System.out.println("Tempo: " + tempo + " nanosecondi");
    }
}
```





<h2 style="color:#C00000"><strong>7. CONTATORE CONDIVISO: VERSIONE ESTESA CON MOLTI THREAD</strong></h2>


La traccia chiede di far incrementare lo stesso contatore a tanti thread. Se abbiamo `n` thread e ciascuno incrementa `m` volte, il risultato atteso è:

```text
n × m
```

Partiamo dalla versione **senza mutua esclusione**, così vediamo il problema. Tutti i thread condividono lo stesso oggetto `Counter`.


<h3 style="color:#C00000"><strong>COUNTER.JAVA</strong></h3>


```java
public class Counter {
    private int valore = 0;

    public void incrementa() {
        valore++;
    }

    public int leggiValore() {
        return valore;
    }
}
```

Il metodo `incrementa()` non è sincronizzato: più thread possono eseguirlo contemporaneamente.


<h3 style="color:#C00000"><strong>INCREMENTATORE.JAVA</strong></h3>


```java
public class Incrementatore implements Runnable {
    private final Counter counter;
    private final int numeroIncrementi;

    public Incrementatore(Counter counter, int numeroIncrementi) {
        this.counter = counter;
        this.numeroIncrementi = numeroIncrementi;
    }

    @Override
    public void run() {
        for (int i = 0; i < numeroIncrementi; i++) {
            counter.incrementa();
        }
    }
}
```

Ogni `Incrementatore` riceve il contatore condiviso e il numero di volte in cui deve incrementarlo.


<h3 style="color:#C00000"><strong>MAIN.JAVA</strong></h3>


```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        int numeroThread = 8;
        int incrementiPerThread = 1_000_000;

        Counter counter = new Counter();
        Thread[] threads = new Thread[numeroThread];

        // Creiamo tutti i thread usando lo stesso counter
        for (int i = 0; i < numeroThread; i++) {
            Incrementatore lavoro =
                    new Incrementatore(counter, incrementiPerThread);

            threads[i] = new Thread(lavoro);
        }

        long inizio = System.nanoTime();

        // Avviamo i thread
        for (int i = 0; i < numeroThread; i++) {
            threads[i].start();
        }

        // Aspettiamo che terminino tutti
        for (int i = 0; i < numeroThread; i++) {
            threads[i].join();
        }

        long tempo = System.nanoTime() - inizio;
        long valoreAtteso =
                (long) numeroThread * incrementiPerThread;

        System.out.println("Valore atteso: " + valoreAtteso);
        System.out.println("Valore ottenuto: " + counter.leggiValore());
        System.out.println("Tempo: " + tempo + " nanosecondi");
    }
}
```


<h3 style="color:#C00000"><strong>PERCHÉ IL RISULTATO PUÒ ESSERE SBAGLIATO?</strong></h3>


`valore++` sembra un’unica operazione, ma in realtà il programma deve:

1. leggere il valore;
2. aggiungere 1;
3. riscrivere il valore.

Due thread potrebbero leggere entrambi `5`, calcolare entrambi `6` e scrivere entrambi `6`. In quel caso, due incrementi hanno prodotto un solo aumento.

Il valore ottenuto può quindi essere **inferiore** a quello atteso. Non è detto che succeda identicamente a ogni esecuzione: è proprio la concorrenza a rendere il risultato imprevedibile.

Di seguito la stessa prova con mutua esclusione, in un programma separato.



**`Counter.java`** — contiene il contatore condiviso:

```java
public class Counter {
    private long valore = 0;

    public synchronized void incrementa() {
        valore++;
    }

    public synchronized long leggiValore() {
        return valore;
    }
}
```

**`Incrementatore.java`** — rappresenta il lavoro di ciascun thread:

```java
public class Incrementatore implements Runnable {
    private final Counter counter;
    private final int numeroIncrementi;

    public Incrementatore(Counter counter, int numeroIncrementi) {
        this.counter = counter;
        this.numeroIncrementi = numeroIncrementi;
    }

    @Override
    public void run() {
        for (int i = 0; i < numeroIncrementi; i++) {
            counter.incrementa();
        }
    }
}
```

**`Main.java`** — crea, avvia e aspetta i thread:

```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        int numeroThread = 100;
        int numeroIncrementi = 100_000;

        Counter counter = new Counter();
        Thread[] threads = new Thread[numeroThread];

        for (int id = 0; id < numeroThread; id++) {
            Incrementatore lavoro =
                    new Incrementatore(counter, numeroIncrementi);

            threads[id] = new Thread(lavoro);
        }

        long inizio = System.nanoTime();

        for (int id = 0; id < numeroThread; id++) {
            threads[id].start();
        }

        for (int id = 0; id < numeroThread; id++) {
            threads[id].join();
        }

        long tempo = System.nanoTime() - inizio;
        long valoreAtteso = (long) numeroThread * numeroIncrementi;

        System.out.println("Valore atteso: " + valoreAtteso);
        System.out.println("Valore ottenuto: " + counter.leggiValore());
        System.out.println("Tempo: " + tempo + " nanosecondi");
    }
}
```

Tutti gli `Incrementatore` ricevono **lo stesso oggetto `counter`**. Immaginiamo che due thread arrivino insieme a `incrementa()`:

1. T1 entra nel metodo e acquisisce il lock di `counter`.
2. T2 cerca di entrare nello stesso metodo, ma aspetta.
3. T1 completa `valore++` ed esce, liberando il lock.
4. T2 può entrare ed eseguire il proprio incremento.

Gli incrementi vengono quindi eseguiti uno alla volta, senza sovrapporsi. Dopo tutte le `join()`, il valore ottenuto sarà **10.000.000**.

<h2 style="color:#C00000"><strong>COSA RICORDARE DAGLI ESERCIZI</strong></h2>

1. La classe del compito implementa `Runnable` e definisce `run()`. È il thread avviato con `start()` a eseguire quel metodo; chiamarlo direttamente esegue nel thread corrente.
2. Sì. **Se chiami `run()` direttamente**, il suo codice viene eseguito dal thread che sta già lavorando in quel momento.
	- Se `main` chiama `lavoro.run()`, lavora `main`.
	- Se crei un `Thread` e chiami `t.start()`, la JVM avvia un nuovo thread e fa eseguire `run()` su quello.
	- Quindi `run()` contiene il lavoro; è `start()` che crea e avvia un thread separato
3. `InterruptedException` va gestita quando usi metodi che possono mettere un thread in attesa, come `sleep()`, `join()` o `wait()`.
4. `new Thread(...)` riceve normalmente un **compito**, cioè un oggetto che implementa `Runnable`. Nel nostro codice quel compito è `lavoro`, un `Incrementatore`


5. Lo SPEEDUP calcolato nei programmi si ottiene TEMPO sequenziale/ TEMPO thread

- Sì, **se i due generatori usano lo stesso seme e ricevono le stesse chiamate nello stesso ordine**, il primo valore di A coincide con il primo di B, il secondo con il secondo, e così via fino all’ultimo. Nel tuo codice succede proprio così: sia il calcolo sequenziale sia il worker creano un nuovo `Random` con seme `42L` e generano lo stesso numero di coordinate.



<h3 style="color:#C00000"><strong>LAMBDA, ARRAY FINAL E VARIABILI LOCALI</strong></h3>

![Tua annotazione sulle variabili catturate dalle lambda](../tutti%20appunti/immagini/lambda-cattura-variabili.png)

Una variabile locale usata nella lambda deve essere final o effectively final. Non puoi scrivere `risultato = altroArray` o modificare una variabile locale contatore catturata. Puoi scrivere `risultato[0] = valore`, perché il riferimento all’array resta uguale e cambia una sua cella. **Final non rende immutabili le celle e non elimina le race condition.** Nell’esempio un solo worker scrive la cella e il main la legge dopo join.

La classe anonima `new Runnable() { public void run() { ... } }` e la classe ArrayInitializer svolgono lo stesso ruolo di compito. La prima scrive il comportamento direttamente dove costruisci il Thread; la seconda permette di riusarlo con parametri. In entrambi i casi il parallelismo nasce da start, non dalla forma con cui scrivi Runnable.




<h1 style="color:#C00000"><strong>COUNTDOWN LATCH </strong></h1>

Nel programma scriviamo:

```java
CountDownLatch entrambiPronti = new CountDownLatch(2);
```

Il `2` è il **contatore iniziale** della barriera.


<h3 style="color:#C00000"><strong>COSA FANNO I DUE METODI</strong></h3>


```java
entrambiPronti.countDown();
```

Abbassa il contatore di uno. È come dire: «Sono arrivato al punto di attesa».

```java
entrambiPronti.await();
```

Aspetta che il contatore arrivi a zero. Se è ancora maggiore di zero, il thread resta fermo su questa riga.


<h3 style="color:#C00000"><strong>NEL NOSTRO ESEMPIO</strong></h3>


1. T1 prende il lock A, chiama `countDown()`: il contatore passa da `2` a `1`. Poi si ferma su `await()`.
2. T2 prende il lock B, chiama `countDown()`: il contatore passa da `1` a `0`.
3. Poiché è arrivato a zero, la barriera si apre e T1 e T2 possono superare `await()`.
4. Ora T1 chiede il lock B, mentre T2 chiede il lock A. Siccome ciascuno possiede il lock che serve all’altro, rimangono bloccati.

La `CountDownLatch` **non è uno dei lock A o B** e non crea da sola il deadlock. Serve soltanto a far partire la richiesta del secondo lock quando entrambi i thread hanno già preso il primo.

Misura durate con `System.nanoTime()`. `System.currentTimeMillis()` legge invece l’ora civile in millisecondi e può cambiare con la regolazione dell’orologio. Il rapporto di speedup usa durate confrontabili, non istanti assoluti.

Nei tuoi esempi alcune misure comprendono la costruzione dei thread e altre soltanto start/join: dichiara cosa cronometri e uniforma il confronto. Esegui più prove, considera riscaldamento della JVM, JIT e garbage collector. Un array piccolo o una CPU con pochi core può rendere più lenta la versione con più thread.

Con lo stesso seme Random e la stessa sequenza di chiamate, sequenziale e singolo worker generano gli stessi punti. Due o più worker con semi differenti stimano lo stesso π ma non devono produrre esattamente la stessa stima. Semi diversi evitano di riusare la stessa sequenza; non costituiscono una prova formale d’indipendenza statistica.

Nella prima variante sincronizzata del Counter, il getter non sincronizzato è letto dal main soltanto dopo le join: queste rendono visibili le scritture dei worker. Se invece vuoi leggere il contatore durante gli incrementi, applica la stessa sincronizzazione anche al getter, come nell’ultima variante.
