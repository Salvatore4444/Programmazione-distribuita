<h1 style="color:#C00000"><strong>THREAD — APPUNTI PRATICI</strong></h1>

Solo checklist e schemi da applicare negli esercizi. La teoria è in [tutti appunti/THREAD.md](../tutti%20appunti/THREAD.md).

<h2 style="color:#C00000"><strong>AVVIARE UN’ATTIVITÀ</strong></h2>

- Un `Thread` accetta direttamente un `Runnable`, il cui lavoro sta in `run()`. Un `Callable<V>` ha invece `call()`, restituisce un valore e normalmente si passa a `ExecutorService.submit(...)`, ottenendo un `Future<V>`: non si può passare direttamente a `new Thread(callable)`.
- Metti il codice dell’attività in `run()`; usa `start()` per avviare un thread nuovo.
- Non chiamare `run()` aspettandoti un nuovo thread: esegue il metodo nel thread corrente.
- Non riutilizzare un `Thread` terminato: non può essere avviato una seconda volta.

```java
Runnable task = () -> eseguiLavoro();
Thread t = new Thread(task);
t.start();
```

<h2 style="color:#C00000"><strong>ATTENDERE E COORDINARE</strong></h2>

- Usa `join()` per attendere la terminazione di un altro thread.
- Usa `join(timeout)` con timeout positivo quando vuoi limitare quanto resta in attesa il chiamante (`join(0)` non impone una scadenza); il timeout non ferma il thread osservato.
- Usa `isAlive()` per controllare se un thread avviato non è ancora terminato.
- Non usare `sleep()` per tentare di sincronizzare l’ordine: sospende il thread corrente per un tempo approssimativo, ma non garantisce che un altro abbia completato il lavoro.
- Ricorda che `sleep()` non rilascia un lock `synchronized` posseduto.

<h2 style="color:#C00000"><strong>GESTIRE L’INTERRUZIONE</strong></h2>

- Tratta `interrupt()` come una richiesta di arresto/cooperazione, non come una terminazione forzata.
- Controlla `isInterrupted()` nei cicli di lavoro oppure gestisci `InterruptedException` dalle operazioni interrompibili.
- Se intercetti `InterruptedException` e non puoi gestirla lì, ripristina il segnale con `Thread.currentThread().interrupt()` e consenti al chiamante di decidere come procedere.
- Dopo aver richiesto l’interruzione, usa `join()` se devi assicurarti che il thread abbia terminato davvero.

Esempio di ciclo cooperativo:

```java
while (!Thread.currentThread().isInterrupted()) {
    eseguiUnPasso();
}
```

<h2 style="color:#C00000"><strong>PROTEGGERE LO STATO CONDIVISO</strong></h2>

- Individua ogni variabile o oggetto accessibile da più thread.
- Se una sequenza legge-modifica-scrive deve comportarsi come un’unica operazione, usa un lock/sincronizzazione oppure una classe atomica.
- `volatile` è adatto alla visibilità di un segnale semplice, non a rendere atomico `count++`.
- Usa lo stesso lock per tutti gli accessi concorrenti allo stato che protegge.
- Mantieni le sezioni sincronizzate brevi e non fare operazioni lente o chiamate esterne mentre tieni un lock se puoi evitarlo.
- Se acquisisci più lock, usa sempre lo stesso ordine per ridurre il rischio di deadlock.
- Non pubblicare `this` da un costruttore e non avviare un thread che usa l’oggetto prima che la costruzione sia terminata.

<h2 style="color:#C00000"><strong><code>wait()</code> E <code>notify()</code></strong></h2>

- Chiama `wait()`, `notify()` e `notifyAll()` solo mentre possiedi il monitor dello stesso oggetto, di solito dentro `synchronized`.
- Controlla la condizione in un ciclo `while`, perché il thread deve ricontrollarla quando si risveglia.
- Dopo una notifica, il thread risvegliato deve riacquisire il lock; non è garantito che esegua subito.
- Per nuovi esercizi, valuta prima strumenti di `java.util.concurrent` come `BlockingQueue`, `Lock` e `Condition` quando appropriato.

Schema concettuale:

```java
synchronized (lock) {
    while (!condizione) {
        lock.wait();
    }
    usaDatoCondiviso();
}
```

<h2 style="color:#C00000"><strong>SCEGLIERE IL LOCK</strong></h2>

- Per un metodo sincronizzato di istanza il lock è `this`.
- Per un metodo `static synchronized` il lock è `NomeClasse.class`.
- Per blocchi sincronizzati espliciti, usa un oggetto lock privato dedicato quando non vuoi esporre il monitor di `this`.
- Lock distinti permettono attività parallele solo se proteggono davvero dati indipendenti.

<h2 style="color:#C00000"><strong>EVITARE ERRORI DI SINCRONIZZAZIONE</strong></h2>

- **Race condition:** rendi atomica la sezione critica o usa una struttura concorrente.
- **Deadlock:** acquisisci lock multipli sempre nello stesso ordine e mantieni semplice la gestione dei lock.
- **Starvation:** evita di monopolizzare risorse o lock a lungo.
- **Livelock:** evita strategie in cui i thread reagiscono ripetutamente senza avanzare; usa attese/backoff o un coordinamento più chiaro.

<h2 style="color:#C00000"><strong>SINGLETON THREAD-SAFE</strong></h2>

- Non usare il Singleton lazy senza sincronizzazione se può essere richiesto da più thread.
- Evita di sincronizzare ogni chiamata senza necessità quando la classe richiede solo inizializzazione lazy.
- Preferisci il **lazy holder** per un Singleton lazy semplice e thread-safe, oppure una `enum` Singleton se adatta al progetto.
- Se implementi double-checked locking, dichiara il campo `volatile` e ricontrolla `instance` dentro il blocco sincronizzato.

<h2 style="color:#C00000"><strong>PARALLELISMO E PRESTAZIONI</strong></h2>

- Valuta quanta parte del lavoro è davvero parallelizzabile prima di aumentare il numero di thread.
- Applica la legge di Amdahl per ricordare che la parte sequenziale limita il guadagno.
- Considera i costi di context switch, attese e contesa sui lock: più thread non garantiscono automaticamente maggiore velocità.

<h2 style="color:#C00000"><strong>METODO PER ANALIZZARE UN ESERCIZIO O UNO SNIPPET</strong></h2>

1. Elenca i thread e indica chi li avvia.
2. Segna i dati e gli oggetti condivisi.
3. Per ogni accesso condiviso, identifica il lock oppure l’operazione atomica che lo protegge.
4. Cerca operazioni composte come `x++` e verifica che non vengano trattate erroneamente come atomiche.
5. Individua l’ordine di acquisizione dei lock e controlla se due thread possono acquisirli in ordine opposto.
6. Segui `start`, `join`, `sleep`, `wait` e `interrupt`: chi sta aspettando, che cosa può sbloccarlo, e quali lock conserva o rilascia?
7. Distingui il thread risvegliato dal thread che ha effettivamente acquisito il lock e ottenuto la CPU.
8. Concludi quali risultati sono possibili, senza assumere un ordine di scheduling non garantito.

<h2 style="color:#C00000"><strong>RISPONDERE A RICHIESTE DI ARRESTO</strong></h2>

Schema semplice per un’attività che può essere interrotta:

```java
try {
    while (!Thread.currentThread().isInterrupted()) {
        eseguiUnPasso();
        Thread.sleep(100);
    }
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

Nel codice reale, il `catch` può terminare il ciclo o propagare l’interruzione. Non ignorare l’eccezione: altrimenti il chiamante può perdere il segnale di arresto.

<h2 style="color:#C00000"><strong>CHECKLIST SU SYNCHRONIZED</strong></h2>

- Il lock protegge solo i thread che usano quel medesimo oggetto lock.
- Tutte le letture e scritture dello stato protetto devono seguire la stessa disciplina.
- Un metodo sincronizzato di istanza e uno statico non condividono lo stesso lock.
- `wait()` rilascia il monitor e lo riacquisisce prima del ritorno; `sleep()` non lo rilascia.
- Dopo `notify()`/`notifyAll()`, il thread risvegliato deve ancora riacquisire il monitor.
- Usa `while (condizioneNonSoddisfatta) wait()` per attendere una condizione.
