<h1 style="color:#C00000"><strong>SOCKET — APPUNTI TEORICI</strong></h1>

<h2 style="color:#C00000"><strong>1. CHE COS’È UN SOCKET</strong></h2>

Un **socket** è un punto di comunicazione usato da programmi in esecuzione per scambiarsi dati attraverso una rete. Per raggiungere un servizio servono:

- **Indirizzo IP**, che identifica la macchina;
- **porta**, che identifica il servizio o il programma in ascolto su quella macchina.

Il socket rappresenta un endpoint della comunicazione. In Java, con TCP, `ServerSocket` accetta nuove connessioni e `Socket` rappresenta una connessione effettiva.

<h2 style="color:#C00000"><strong>2. TCP E UDP</strong></h2>

<h3 style="color:#C00000"><strong>TCP</strong></h3>

TCP è orientato alla connessione e fornisce un flusso di byte affidabile e ordinato: se i dati arrivano, l’applicazione li riceve nell’ordine inviato. Non significa “rischio zero”: connessione e comunicazione possono comunque fallire, ad esempio per guasti della rete o arresto del programma.

Il modello client-server tipico è:

1. il server apre una porta e rimane in ascolto;
2. il client conosce IP e porta e prova a collegarsi;
3. il server accetta la connessione;
4. client e server scambiano dati attraverso i rispettivi socket.

<h3 style="color:#C00000"><strong>UDP</strong></h3>

UDP invia datagrammi senza stabilire una connessione come TCP. Non garantisce consegna, ordine o assenza di duplicati: l’applicazione deve gestire queste proprietà se le richiede. In cambio, UDP può essere adatto a comunicazioni in cui contano tempi ridotti o tolleranza alla perdita di alcuni pacchetti.

<h2 style="color:#C00000"><strong>3. <code>ServerSocket</code> E <code>Socket</code></strong></h2>

- **`ServerSocket`** si usa sul server per ascoltare su una porta e accettare nuove connessioni.
- Il metodo **`accept()`** attende una connessione; è bloccante finché un client non si collega.
- **`Socket`** rappresenta la connessione TCP effettiva. Il client lo crea per collegarsi; il server riceve il socket corrispondente quando `accept()` termina.

Il server può mantenere il `ServerSocket` in ascolto per accettare altre connessioni, mentre ogni `Socket` accettato rappresenta la comunicazione con uno specifico client.

<h2 style="color:#C00000"><strong>4. STREAM DI INPUT E OUTPUT</strong></h2>

Dopo aver stabilito la connessione, i due programmi usano stream per scambiare dati. `getInputStream()` e `getOutputStream()` forniscono stream di byte.

Input e output sono relativi al programma che li usa:

- l’output del client invia dati che il server riceve nel proprio input;
- l’output del server invia dati che il client riceve nel proprio input.

TCP offre un flusso di byte, non una sequenza di messaggi con confini automatici. Client e server devono concordare come riconoscere la fine di un dato: per esempio usando una lunghezza, un separatore, una chiusura della connessione o un protocollo di messaggi.

<h2 style="color:#C00000"><strong>5. WRAPPING DEGLI STREAM</strong></h2>

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

<h2 style="color:#C00000"><strong>6. TRASFERIRE OGGETTI JAVA</strong></h2>

Per trasferire oggetti Java si possono usare `ObjectOutputStream` e `ObjectInputStream`:

```java
ObjectOutputStream out =
    new ObjectOutputStream(socket.getOutputStream());
out.flush(); // rende disponibile anche l’header al peer

ObjectInputStream in =
    new ObjectInputStream(socket.getInputStream());
```

`ObjectOutputStream.writeObject(...)` serializza un oggetto; `ObjectInputStream.readObject()` lo ricostruisce. Le classi trasferite devono essere serializzabili e disponibili sul programma ricevente.

Gli stream a oggetti scambiano un’intestazione (header) quando vengono creati. Se entrambi i lati creano per primi `ObjectInputStream`, ciascuno aspetta l’header dell’altro e la comunicazione può bloccarsi. L’ordine di creazione è quindi importante; i dettagli operativi sono nella sezione pratica.

La deserializzazione di oggetti ricevuti da origini non fidate comporta rischi: non considerare sicuro un oggetto soltanto perché è stato letto con `ObjectInputStream`.

<h2 style="color:#C00000"><strong>7. CHIAMATE REMOTE COSTRUITE SOPRA I SOCKET</strong></h2>

Un socket trasferisce byte. Per simulare una **invocazione remota** si costruisce sopra il trasporto un protocollo che descrive metodo, parametri e risultato:

- il **client** invoca un metodo attraverso uno stub;
- lo **stub** traduce la chiamata e i parametri in una richiesta di rete;
- il **server** riceve la richiesta e la passa al componente che invoca il metodo sull’oggetto vero;
- il risultato torna al client e lo stub lo restituisce come risultato del metodo.

Il componente sul lato server è spesso chiamato **skeleton** nel modello storico. Stub e skeleton non sono funzioni primitive del socket: sono livelli applicativi che devono concordare formato e ordine dei dati.

<h3 style="color:#C00000"><strong>INTERFACCIA REMOTA COME CONTRATTO</strong></h3>

Un’interfaccia dichiara quali operazioni sono disponibili; non invoca da sola i metodi del server. L’oggetto server realizza l’interfaccia e il client usa uno stub con le stesse operazioni.

```java
// Contratto didattico di invocazione sopra socket, non interfaccia Java RMI.
public interface Impiegato {
    String getNome() throws Throwable;
    String getID() throws Throwable;
    int getStipendio() throws Throwable;
    int aumentaStipendio(int diQuanto) throws Throwable;
}
```

- `getNome()` restituisce il nome; `getID()` l’identificativo.
- `getStipendio()` restituisce lo stipendio corrente.
- `aumentaStipendio(diQuanto)` aggiorna lo stipendio e restituisce il nuovo valore.

La firma `throws Throwable` è conservata dall’esempio originale per riconoscere la slide. Per un nuovo protocollo si preferiscono eccezioni più specifiche; per Java RMI serve invece un’interfaccia `extends Remote` con metodi che dichiarano `RemoteException` o un suo supertipo ammesso.

<h3 style="color:#C00000"><strong>SEQUENZA CONCETTUALE DELLA CHIAMATA</strong></h3>

1. Il server crea il socket di ascolto e l’oggetto vero.
2. Il server rende individuabile il servizio, associandolo a un nome o a un indirizzo secondo il protocollo usato.
3. Il client trova il servizio e ottiene/usa lo stub.
4. Il client chiama un metodo; lo stub invia metodo e parametri.
5. Il componente server legge la richiesta e invoca il metodo sull’oggetto vero.
6. Il risultato viene inviato indietro e restituito al client dallo stub.

Esempio originale: `aumentaStipendio(1000)` quando lo stipendio iniziale è 30000:

```text
client/stub                         server/skeleton
"aumentaStipendio", 1000  --------> legge metodo e parametro
                                   chiama il metodo: 30000 → 31000
nuovo stipendio 31000    <-------- invia il risultato
```

Lo stub traduce la chiamata in una richiesta di rete; lo skeleton traduce la richiesta in una chiamata all’oggetto vero. Entrambi devono concordare ordine e formato dei dati. In un protocollo socket il registro è facoltativo: il client può conoscere già host e porta; RMI offre invece il proprio registry.

**Locale/remoto:** chiamare direttamente un normale `ImpiegatoImpl` locale esegue il codice locale; chiamare uno stub/proxy può inviare la richiesta al server. La parola `new` da sola non decide il tipo di chiamata: anche lo stub è un oggetto creato nella JVM client.

Questa struttura è una forma di RPC (chiamata di procedura/metodo remoto). Java RMI fornisce un’infrastruttura per questo modello; usando socket grezzi, il programmatore deve definire e implementare il protocollo.

<h2 style="color:#C00000"><strong>APPROFONDIMENTI PER CAPIRE GLI SCAMBI TCP</strong></h2>

<h3 style="color:#C00000"><strong>TCP È UN FLUSSO, NON UNA CODA DI MESSAGGI</strong></h3>

TCP conserva ordine e affidabilità del flusso, ma non mantiene i confini delle singole chiamate a `write()`. Se il mittente esegue due scritture, il destinatario può leggerne i dati insieme o in più letture. `read()` può restituire meno byte di quelli richiesti; il programma deve usare un ciclo, una lunghezza nota o un delimitatore per ricostruire il messaggio completo.

Per questo client e server devono concordare un **protocollo applicativo**: campi, ordine, lunghezza o terminazione dei messaggi, gestione degli errori e fine della sessione.

<h3 style="color:#C00000"><strong>COMUNICAZIONE BIDIREZIONALE</strong></h3>

Una connessione TCP è bidirezionale: entrambi i lati hanno uno stream di input e uno di output. Il nome input/output è sempre relativo al programma che usa lo stream. Se il client scrive su `getOutputStream()`, il server legge dal proprio `getInputStream()`.

<h3 style="color:#C00000"><strong>TESTO E CHARSET</strong></h3>

Per testo si avvolge lo stream di byte in un `InputStreamReader`/`OutputStreamWriter`, specificando la stessa codifica da entrambe le parti (spesso UTF-8). `BufferedReader.readLine()` aspetta un terminatore di riga; se il mittente non invia `\n` o non chiude lo stream, il ricevente può restare in attesa.

<h3 style="color:#C00000"><strong>OGGETTI E INTESTAZIONI DEGLI STREAM</strong></h3>

`ObjectOutputStream` scrive un header quando viene costruito; `ObjectInputStream` attende e legge l’header del peer. Perciò entrambi i lati dovrebbero costruire e fare `flush()` del proprio `ObjectOutputStream` prima di costruire `ObjectInputStream`, evitando che entrambi aspettino. Le classi devono essere serializzabili e disponibili su entrambi i lati.

La serializzazione Java non definisce da sola il protocollo dell’applicazione: client e server devono sapere quale oggetto o messaggio leggere in ogni fase. La deserializzazione di input non fidati va evitata o protetta.


<h2 style="color:#C00000"><strong>PUNTI ESSENZIALI PER L’ESAME</strong></h2>

- Un socket è un endpoint; per contattare un servizio servono indirizzo IP e porta.
- `ServerSocket` ascolta e `accept()` restituisce un nuovo `Socket` connesso al client.
- TCP è connesso, affidabile e ordinato ma offre un flusso di byte senza confini applicativi dei messaggi. UDP trasporta datagrammi senza garanzia di consegna o ordine.
- Input/output sono relativi al processo: l’output di un lato diventa l’input dell’altro.
- Gli stream di base trasportano byte; wrapper aggiungono buffering, conversione testo o serializzazione di oggetti.
- Con `ObjectInputStream` e `ObjectOutputStream`, considera l’header e l’ordine di costruzione; specifica un protocollo di lettura/scrittura coerente.
- Un socket trasferisce dati; RPC, stub e skeleton sono livelli applicativi che definiscono come rappresentare una chiamata e il suo risultato.

<h2 style="color:#C00000"><strong>DOMANDE NUMERATE — SOCKET E STREAM (1–8)</strong></h2>

### 1. Come funzionano i socket TCP in Java?

Il server crea un `ServerSocket` su una porta e chiama `accept()`, che attende una connessione. Il client crea un `Socket(host, porta)`; quando la connessione riesce, `accept()` restituisce al server un **nuovo** `Socket` dedicato a quel client. Entrambi usano input e output per scambiare byte in entrambe le direzioni e chiudono i socket a fine uso. TCP consegna un flusso ordinato, senza conservare i confini delle singole `write`: il protocollo applicativo deve delimitare i messaggi. La comunicazione può bloccarsi o fallire.

### 2. Qual è la differenza tra `ServerSocket` e `Socket`?

`ServerSocket` **ascolta** e accetta nuove connessioni; non è lo stream di dati di un client. `Socket` rappresenta una **connessione TCP già stabilita**: lo crea il client o lo restituisce `accept()` sul server. Un server mantiene il socket di ascolto e può avere molti socket connessi, uno per ogni client.

### 3. Cosa sono e a cosa servono gli stream?

Sono astrazioni per leggere o scrivere una sequenza di dati. `InputStream` legge byte da una sorgente; `OutputStream` scrive byte verso una destinazione. La sorgente o destinazione può essere un file, memoria o un socket. Gli stream non decidono da soli il formato dei messaggi.

### 4. Cosa significa usare uno stream come wrapper di un altro stream?

Si costruisce uno stream sopra un altro, aggiungendo una funzione senza cambiare la sorgente sottostante. Per esempio, `new BufferedInputStream(socket.getInputStream())` aggiunge buffering; `new ObjectInputStream(socket.getInputStream())` interpreta oggetti serializzati. Per testo, `InputStreamReader` converte byte in caratteri secondo un charset e `BufferedReader` aggiunge `readLine()`. Chiudere il wrapper normalmente chiude anche lo stream sottostante.

### 5. Quali sono i metodi più importanti di `InputStream` e `OutputStream`?

| Classe | Metodo | Effetto |
|---|---|---|
| `InputStream` | `read()` | Legge un byte (`0`–`255`), oppure `-1` a fine stream. |
| `InputStream` | `read(byte[])`, `read(byte[], off, len)` | Legge **fino a** un certo numero di byte; può leggerne meno di quelli richiesti. |
| `InputStream` | `skip(n)`, `available()` | Salta byte; indica quelli leggibili senza bloccare, **non** la lunghezza totale. |
| `InputStream` | `close()` | Rilascia la risorsa. |
| `OutputStream` | `write(int)`, `write(byte[])`, `write(byte[], off, len)` | Scrive uno o più byte. |
| `OutputStream` | `flush()` | Spinge i dati eventualmente trattenuti dai buffer verso lo stream sottostante; non prova che il peer li abbia letti. |
| `OutputStream` | `close()` | Chiude e rilascia la risorsa, dopo l'eventuale svuotamento dei buffer. |

### 6. Perché normalmente non si usano direttamente `InputStream` e `OutputStream`?

Sono classi astratte che forniscono operazioni di base sui byte. Nella pratica si ottengono implementazioni concrete, ad esempio da `Socket` o `Files`, e spesso si avvolgono in classi più comode: buffering, conversione di testo, valori primitivi o oggetti. Si possono comunque usare come **tipi** di variabile e chiamarne i metodi di base.

### 7. Come si usano gli stream associati a un socket?

Si chiamano `socket.getInputStream()` e `socket.getOutputStream()`. L'output di un lato arriva all'input dell'altro. Si concordano formato, ordine dei campi e terminazione dei messaggi; con i wrapper bufferizzati può servire `flush()`. Per esempio: `DataOutputStream out = new DataOutputStream(socket.getOutputStream()); out.writeUTF("ciao"); out.flush();` e sull'altro lato `new DataInputStream(socket.getInputStream()).readUTF()`. Usare `try` con risorse per chiudere il socket.

### 8. Quali metodi leggono e scrivono oggetti con `ObjectInputStream` e `ObjectOutputStream`?

`ObjectOutputStream.writeObject(obj)` serializza e scrive un oggetto; `ObjectInputStream.readObject()` lo ricostruisce e restituisce `Object`, quindi può servire un cast. `readObject()` può sollevare `IOException` o `ClassNotFoundException`; la classe e i suoi campi trasferiti devono essere serializzabili e disponibili al destinatario. `flush()` invia i dati bufferizzati. Quando entrambi i peer usano stream a oggetti, conviene creare e svuotare prima `ObjectOutputStream`, poi costruire `ObjectInputStream`, perché quest'ultimo attende l'header dello stream ricevuto.
