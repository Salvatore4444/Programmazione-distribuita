<h1 style="color:#C00000"><strong>Thread — Appunti teorici</strong></h1>

<h2 style="color:#C00000"><strong>1. Core, concorrenza e parallelismo</strong></h2>

Con l’aumento dei transistor non è sempre possibile continuare ad aumentare la frequenza di un singolo processore: il calore e il consumo diventano limiti importanti. Per aumentare le prestazioni si usano processori con più **core**.

I core possono condividere la memoria principale e possono avere cache proprie. Per sfruttare più core, il software deve dividere il lavoro in attività che possano procedere in modo indipendente.

- **Concorrenza:** più attività avanzano nello stesso intervallo di tempo, alternandosi o cooperando. Può esserci anche su un solo core tramite alternanza.
- **Parallelismo:** più attività vengono eseguite davvero nello stesso momento, per esempio su core distinti.

Con un solo core, il sistema alterna l’esecuzione dei thread. Con più core, thread diversi possono essere eseguiti contemporaneamente. In entrambi i casi, se condividono dati, devono essere coordinati.

<h3 style="color:#C00000"><strong>Costi e difficoltà</strong></h3>

- **Context switch:** il sistema interrompe temporaneamente un’attività e riprende un’altra, salvando e ripristinando lo stato. Ha un costo.
- **Cache miss:** il dato richiesto non è nella cache e deve essere recuperato da un livello di memoria più lento.
- **Page fault:** la pagina di memoria richiesta non è disponibile nella RAM e va recuperata, spesso dallo storage.
- **Attesa e sincronizzazione:** un thread può dover aspettare una risorsa o un altro thread.

Questi fenomeni non sono esclusivi dei sistemi multicore, ma possono influenzare prestazioni e coordinamento quando si lavora in concorrenza.

<h2 style="color:#C00000"><strong>2. Programmazione distribuita, processi e thread</strong></h2>

- **Programmazione distribuita:** attività su computer diversi comunicano tramite rete.
- **Processi concorrenti:** programmi o processi diversi, gestiti dal sistema operativo, avanzano sulla stessa macchina. Ogni processo ha il proprio spazio di memoria.
- **Thread concorrenti:** flussi di esecuzione distinti all’interno dello stesso processo. Condividono memoria e risorse del processo, ma ciascuno esegue il proprio percorso di codice.

La memoria condivisa rende semplice scambiare dati fra thread, ma può provocare interferenze se più thread accedono agli stessi dati senza coordinamento.

<h2 style="color:#C00000"><strong>3. Multitasking e multithreading</strong></h2>

- **Multitasking:** il sistema operativo gestisce più programmi/processi, alternandone l’esecuzione e, se possibile, eseguendoli in parallelo.
- **Multithreading:** un singolo programma usa più thread per svolgere attività diverse, per esempio ricevere dati di rete, aggiornare l’interfaccia e controllare notifiche.

Ogni applicazione Java inizia con almeno il **main thread**. La JVM può inoltre creare thread di sistema.

<h2 style="color:#C00000"><strong>4. Processo e thread</strong></h2>

Un **processo** è un programma in esecuzione con spazio di memoria e risorse proprie. Un **thread** è un flusso di esecuzione all’interno del processo.

I thread dello stesso processo condividono memoria e risorse (per esempio gli oggetti raggiungibili dall’heap), ma ognuno ha il proprio flusso di esecuzione e stack.

<h2 style="color:#C00000"><strong>5. Creare e avviare un thread in Java</strong></h2>

Si può descrivere il lavoro con `Runnable` e affidarlo a un oggetto `Thread`, oppure usare un executor. `Runnable` è spesso preferibile all’estensione di `Thread`, perché una classe Java può estendere una sola classe.

```java
Runnable lavoro = () -> System.out.println("Attività in esecuzione");
Thread t = new Thread(lavoro);
t.start();
```

- `run()` contiene il lavoro da eseguire.
- `start()` avvia un nuovo thread; la JVM esegue `run()` su quel thread.
- Chiamare direttamente `run()` non crea un nuovo thread: è una normale chiamata di metodo nel thread corrente.
- Un oggetto `Thread` terminato non può essere riavviato chiamando di nuovo `start()`.

Per applicazioni reali, `ExecutorService` e altri strumenti del package `java.util.concurrent` aiutano a gestire gruppi di attività senza creare manualmente un thread per ogni lavoro.

<h2 style="color:#C00000"><strong>6. Sospensione, interruzione e attesa</strong></h2>

<h3 style="color:#C00000"><strong><code>sleep()</code></strong></h3>

`Thread.sleep(millisecondi)` sospende temporaneamente il thread che sta eseguendo quella riga. Non sospende automaticamente tutti gli altri thread. Può lanciare `InterruptedException` se il thread viene interrotto durante l’attesa.

` sleep()` non rilascia i monitor/lock `synchronized` che il thread possiede.

<h3 style="color:#C00000"><strong><code>interrupt()</code></strong></h3>

`interrupt()` è una richiesta/segnalazione al thread, non un comando generale per ucciderlo. Il thread deve collaborare controllando il segnale o reagendo alle operazioni interrompibili.

- `Thread.currentThread().isInterrupted()` legge il flag senza azzerarlo.
- `Thread.interrupted()` legge il flag del thread corrente e lo azzera.
- Se il thread è bloccato in `sleep()`, `wait()` o `join()`, l’interruzione normalmente fa terminare l’attesa con `InterruptedException`; in tale caso il flag viene azzerato.

Se un metodo non può gestire l’interruzione, in genere deve ripristinare il flag con `Thread.currentThread().interrupt()` e lasciare che il livello superiore decida come terminare.

<h3 style="color:#C00000"><strong><code>join()</code> e <code>isAlive()</code></strong></h3>

`join()` fa aspettare il thread chiamante finché il thread indicato termina. `join(timeout)` attende al massimo il tempo specificato: il timeout limita l’attesa del chiamante, ma non interrompe il thread osservato.

`isAlive()` restituisce `true` se il thread è stato avviato e non è ancora terminato. `join()` può a sua volta lanciare `InterruptedException` se viene interrotto il thread che sta aspettando.

<h3 style="color:#C00000"><strong>Attesa con scadenza e richiesta di arresto</strong></h3>

Un programma può attendere un thread con timeout ripetuti, controllare se è ancora attivo e, superata una scadenza, chiamare `interrupt()`. Poi può usare `join()` per aspettare la conclusione effettiva. L’interruzione è una richiesta: il thread deve reagire, per esempio uscendo da `sleep()` e terminando in modo ordinato.

<h2 style="color:#C00000"><strong>7. Stati e ciclo di vita di un thread</strong></h2>

Il modello concettuale spesso descritto è: creato, pronto, in esecuzione, in attesa o bloccato, terminato. In Java, `Thread.State` espone questi stati:

- **NEW:** thread creato ma non ancora avviato;
- **RUNNABLE:** thread avviato ed eseguibile; la JVM non distingue qui lo stato “ready” dall’esecuzione effettiva su CPU;
- **BLOCKED:** attende di acquisire un monitor `synchronized` già posseduto da un altro thread;
- **WAITING:** attende senza scadenza, per esempio su `wait()`, `join()` senza timeout o `LockSupport.park()`;
- **TIMED_WAITING:** attende con scadenza, per esempio su `sleep()`, `wait(timeout)` o `join(timeout)`;
- **TERMINATED:** esecuzione terminata.

`Thread.yield()` suggerisce allo scheduler che il thread corrente può cedere la CPU. È solo un suggerimento: non garantisce che il thread cambi stato o che un altro thread venga eseguito immediatamente.

In `WAITING`, un thread può essere risvegliato da notifiche, dalla fine del thread atteso o da altre operazioni previste dal meccanismo usato. Il risveglio non implica che inizi subito a eseguire: deve prima soddisfare le condizioni necessarie (per esempio riacquisire il lock) e ottenere tempo CPU.

<h2 style="color:#C00000"><strong>8. Condivisione dei dati, interferenza e race condition</strong></h2>

I thread possono accedere agli stessi oggetti e alle stesse variabili. Se il risultato dipende dall’ordine in cui operazioni di thread diversi vengono eseguite, si ha una **race condition**.

Un’operazione che sembra singola nel codice può essere composta da più passi. Per esempio, `contatore++` equivale concettualmente a leggere il valore, incrementarlo e scriverlo. Due thread possono sovrapporre questi passi e perdere un aggiornamento.

La **visibilità della memoria** è un problema distinto: un thread può non vedere subito le scritture di un altro se il programma non usa una relazione di sincronizzazione prevista dal modello di memoria Java.

<h2 style="color:#C00000"><strong>9. Relazioni happens-before</strong></h2>

Una relazione **happens-before** ordina operazioni e rende visibili gli effetti delle scritture tra thread. Tra gli esempi principali:

- Le operazioni eseguite prima di `Thread.start()` sono visibili al thread avviato.
- Le operazioni del thread terminato sono visibili al thread che ha atteso con successo la sua fine tramite `join()`.
- Un thread che rilascia un monitor e un altro che poi acquisisce lo stesso monitor hanno una relazione di visibilità.
- Una scrittura su una variabile `volatile` è visibile alle letture successive di quella stessa variabile secondo le regole del modello di memoria.

<h3 style="color:#C00000"><strong><code>volatile</code></strong></h3>

`volatile` assicura visibilità e ordinamento per accessi alla variabile volatile, ed è utile come segnale semplice:

```java
volatile boolean pronto = false;
int dato;

// Produttore
dato = 42;
pronto = true;

// Consumatore: dopo aver osservato pronto == true,
// vede anche la scrittura precedente di dato.
```

`volatile` non rende atomiche operazioni composte. Per esempio `volatile int contatore; contatore++;` può perdere incrementi perché l’incremento consiste di lettura, calcolo e scrittura.

<h2 style="color:#C00000"><strong>10. Sincronizzazione e monitor</strong></h2>

Ogni oggetto Java ha un **lock intrinseco**, detto anche monitor. Un metodo o blocco `synchronized` acquisisce il monitor associato, esegue la sezione protetta e lo rilascia quando esce, anche se esce per un’eccezione.

Se un altro thread richiede lo stesso monitor mentre è occupato, resta in stato `BLOCKED` finché può acquisirlo.

```java
synchronized (this) {
    nameCount++;
    nameList.add(name);
}
```

Il blocco protegge solo il codice al suo interno e solo rispetto ai thread che usano **lo stesso lock**. Due oggetti distinti hanno monitor distinti.

- Un metodo di istanza `synchronized` usa come lock `this`.
- Un metodo `static synchronized` usa come lock l’oggetto `NomeClasse.class`.
- Un metodo statico sincronizzato e un metodo sincronizzato su una singola istanza usano lock diversi e possono quindi procedere contemporaneamente.

I costruttori non possono essere dichiarati `synchronized`. È comunque importante non rendere un oggetto visibile ad altri thread prima che la sua costruzione sia terminata.

<h2 style="color:#C00000"><strong>11. Sincronizzazione a grana fine</strong></h2>

Si può proteggere solo la parte di codice che accede ai dati condivisi, invece di sincronizzare un metodo intero. Lock distinti possono permettere parallelismo quando proteggono dati indipendenti:

```java
synchronized (lock1) {
    c1++;
}

synchronized (lock2) {
    c2++;
}
```

La scelta del lock deve riflettere quali dati protegge. Più lock possono aumentare la concorrenza, ma rendono più complesso prevenire deadlock. Tutti gli accessi a uno stesso stato condiviso devono seguire una strategia coerente.

<h2 style="color:#C00000"><strong>12. Operazioni atomiche</strong></h2>

Un’operazione atomica appare come una singola azione indivisibile agli altri thread. `AtomicInteger` offre operazioni atomiche come `incrementAndGet()`:

```java
private final AtomicInteger c = new AtomicInteger(0);

public void increment() {
    c.incrementAndGet();
}

public int value() {
    return c.get();
}
```

È diverso da `volatile int c; c++`: `volatile` dà visibilità, ma non rende atomico l’incremento.

<h2 style="color:#C00000"><strong>13. Deadlock, starvation e livelock</strong></h2>

- **Deadlock:** due o più thread si attendono reciprocamente per lock che gli altri possiedono; nessuno può proseguire.
- **Starvation:** un thread non ottiene a lungo una risorsa o tempo CPU sufficiente, mentre altri continuano.
- **Livelock:** i thread sono attivi e reagiscono l’uno all’altro, ma le loro azioni impediscono il completamento del lavoro.

<h2 style="color:#C00000"><strong>14. Legge di Amdahl</strong></h2>

La legge di Amdahl stima il limite al guadagno ottenibile parallelizzando un programma:

\[
S = \frac{1}{(1-p)+\frac{p}{n}}
\]

- `S`: velocità relativa rispetto all’esecuzione con un solo processore;
- `p`: frazione parallelizzabile;
- `1-p`: frazione sequenziale;
- `n`: numero di processori.

Se il 90% del lavoro è parallelizzabile e si usano 4 processori:

\[
S = \frac{1}{0{,}1+\frac{0{,}9}{4}} \approx 3{,}08
\]

Il programma è circa 3,08 volte più veloce, non 4 volte: la parte sequenziale limita il guadagno massimo.

<h2 style="color:#C00000"><strong>15. Singleton e concorrenza</strong></h2>

Un **Singleton** è una classe progettata per fornire una sola istanza condivisa, per esempio per una configurazione comune. Non tutte le classi devono essere Singleton: se servono oggetti distinti (per esempio più studenti), si creano normalmente più istanze.

<h3 style="color:#C00000"><strong>Inizializzazione lazy non sincronizzata</strong></h3>

```java
class Singleton {
    private static Singleton instance;
    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

Questa versione crea l’istanza solo alla prima richiesta, ma non è thread-safe: due thread possono vedere `null` e creare due oggetti.

<h3 style="color:#C00000"><strong>Sincronizzare il metodo</strong></h3>

Dichiarare `getInstance()` `static synchronized` impedisce creazioni concorrenti e garantisce una sola istanza, ma ogni chiamata deve acquisire il lock anche dopo la creazione.

<h3 style="color:#C00000"><strong>Double-checked locking</strong></h3>

La forma classica richiede che il campo sia `volatile`:

```java
class Singleton {
    private static volatile Singleton instance;
    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

Il controllo esterno evita il lock dopo la creazione; quello interno ricontrolla il valore dopo aver acquisito il lock. `volatile` garantisce la corretta visibilità/pubblicazione in Java moderno.

<h3 style="color:#C00000"><strong>Lazy holder</strong></h3>

Un’alternativa semplice e thread-safe usa l’inizializzazione della classe interna:

```java
public class Something {
    private Something() {}

    private static class LazyHolder {
        private static final Something INSTANCE = new Something();
    }

    public static Something getInstance() {
        return LazyHolder.INSTANCE;
    }
}
```

La JVM inizializza la classe interna al primo uso e garantisce l’inizializzazione thread-safe. Per molte applicazioni è l’approccio preferibile rispetto a gestire manualmente lock e `volatile`.
