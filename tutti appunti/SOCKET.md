<h1 style="color:#C00000"><strong>Socket — Appunti teorici</strong></h1>

<h2 style="color:#C00000"><strong>1. Che cos’è un socket</strong></h2>

Un **socket** è un punto di comunicazione usato da programmi in esecuzione per scambiarsi dati attraverso una rete. Per raggiungere un servizio servono:

- **Indirizzo IP**, che identifica la macchina;
- **porta**, che identifica il servizio o il programma in ascolto su quella macchina.

Il socket rappresenta un endpoint della comunicazione. In Java, con TCP, `ServerSocket` accetta nuove connessioni e `Socket` rappresenta una connessione effettiva.

<h2 style="color:#C00000"><strong>2. TCP e UDP</strong></h2>

<h3 style="color:#C00000"><strong>TCP</strong></h3>

TCP è orientato alla connessione e fornisce un flusso di byte affidabile e ordinato: se i dati arrivano, l’applicazione li riceve nell’ordine inviato. Non significa “rischio zero”: connessione e comunicazione possono comunque fallire, ad esempio per guasti della rete o arresto del programma.

Il modello client-server tipico è:

1. il server apre una porta e rimane in ascolto;
2. il client conosce IP e porta e prova a collegarsi;
3. il server accetta la connessione;
4. client e server scambiano dati attraverso i rispettivi socket.

<h3 style="color:#C00000"><strong>UDP</strong></h3>

UDP invia datagrammi senza stabilire una connessione come TCP. Non garantisce consegna, ordine o assenza di duplicati: l’applicazione deve gestire queste proprietà se le richiede. In cambio, UDP può essere adatto a comunicazioni in cui contano tempi ridotti o tolleranza alla perdita di alcuni pacchetti.

<h2 style="color:#C00000"><strong>3. <code>ServerSocket</code> e <code>Socket</code></strong></h2>

- **`ServerSocket`** si usa sul server per ascoltare su una porta e accettare nuove connessioni.
- Il metodo **`accept()`** attende una connessione; è bloccante finché un client non si collega.
- **`Socket`** rappresenta la connessione TCP effettiva. Il client lo crea per collegarsi; il server riceve il socket corrispondente quando `accept()` termina.

Il server può mantenere il `ServerSocket` in ascolto per accettare altre connessioni, mentre ogni `Socket` accettato rappresenta la comunicazione con uno specifico client.

<h2 style="color:#C00000"><strong>4. Stream di input e output</strong></h2>

Dopo aver stabilito la connessione, i due programmi usano stream per scambiare dati. `getInputStream()` e `getOutputStream()` forniscono stream di byte.

Input e output sono relativi al programma che li usa:

- l’output del client invia dati che il server riceve nel proprio input;
- l’output del server invia dati che il client riceve nel proprio input.

TCP offre un flusso di byte, non una sequenza di messaggi con confini automatici. Client e server devono concordare come riconoscere la fine di un dato: per esempio usando una lunghezza, un separatore, una chiusura della connessione o un protocollo di messaggi.

<h2 style="color:#C00000"><strong>5. Wrapping degli stream</strong></h2>

**Wrapping** significa avvolgere uno stream di base in stream più specializzati. Ogni livello aggiunge una funzione.

Per leggere testo riga per riga:

```java
BufferedReader reader = new BufferedReader(
    new InputStreamReader(socket.getInputStream()));
```

- `socket.getInputStream()` riceve byte;
- `InputStreamReader` converte i byte in caratteri;
- `BufferedReader` aggiunge metodi pratici come `readLine()`.

Quando i caratteri devono essere interpretati in modo coerente tra macchine, è bene specificare esplicitamente la codifica, per esempio UTF-8.

<h2 style="color:#C00000"><strong>6. Trasferire oggetti Java</strong></h2>

Per trasferire oggetti Java si possono usare `ObjectOutputStream` e `ObjectInputStream`:

```java
ObjectOutputStream out =
    new ObjectOutputStream(socket.getOutputStream());

ObjectInputStream in =
    new ObjectInputStream(socket.getInputStream());
```

`ObjectOutputStream.writeObject(...)` serializza un oggetto; `ObjectInputStream.readObject()` lo ricostruisce. Le classi trasferite devono essere serializzabili e disponibili sul programma ricevente.

Gli stream a oggetti scambiano un’intestazione (header) quando vengono creati. Se entrambi i lati creano per primi `ObjectInputStream`, ciascuno aspetta l’header dell’altro e la comunicazione può bloccarsi. L’ordine di creazione è quindi importante; i dettagli operativi sono nella sezione pratica.

La deserializzazione di oggetti ricevuti da origini non fidate comporta rischi: non considerare sicuro un oggetto soltanto perché è stato letto con `ObjectInputStream`.

<h2 style="color:#C00000"><strong>7. Chiamate remote costruite sopra i socket</strong></h2>

Un socket trasferisce byte. Per simulare una **invocazione remota** si costruisce sopra il trasporto un protocollo che descrive metodo, parametri e risultato:

- il **client** invoca un metodo attraverso uno stub;
- lo **stub** traduce la chiamata e i parametri in una richiesta di rete;
- il **server** riceve la richiesta e la passa al componente che invoca il metodo sull’oggetto vero;
- il risultato torna al client e lo stub lo restituisce come risultato del metodo.

Il componente sul lato server è spesso chiamato **skeleton** nel modello storico. Stub e skeleton non sono funzioni primitive del socket: sono livelli applicativi che devono concordare formato e ordine dei dati.

<h3 style="color:#C00000"><strong>Interfaccia remota come contratto</strong></h3>

Un’interfaccia dichiara quali operazioni sono disponibili; non invoca da sola i metodi del server. L’oggetto server realizza l’interfaccia e il client usa uno stub con le stesse operazioni.

Nelle note compare un esempio di interfaccia `Impiegato` con `getNome()`, `getID()`, `getStipendio()` e `aumentaStipendio(diQuanto)`. La firma con `throws Throwable` è solo un esempio generico e non è una scelta consigliata. Per Java RMI si usa `RemoteException` nei metodi remoti; con un protocollo socket progettato manualmente si definiscono eccezioni e messaggi adeguati al proprio protocollo.

<h3 style="color:#C00000"><strong>Sequenza concettuale della chiamata</strong></h3>

1. Il server crea il socket di ascolto e l’oggetto vero.
2. Il server rende individuabile il servizio, associandolo a un nome o a un indirizzo secondo il protocollo usato.
3. Il client trova il servizio e ottiene/usa lo stub.
4. Il client chiama un metodo; lo stub invia metodo e parametri.
5. Il componente server legge la richiesta e invoca il metodo sull’oggetto vero.
6. Il risultato viene inviato indietro e restituito al client dallo stub.

Questa struttura è una forma di RPC (chiamata di procedura/metodo remoto). Java RMI fornisce un’infrastruttura per questo modello; usando socket grezzi, il programmatore deve definire e implementare il protocollo.
