<h1 style="color:#C00000"><strong>JAVA RMI — APPUNTI COMPLETI</strong></h1>

<h2 style="color:#C00000"><strong>1. CHE COS’È JAVA RMI</strong></h2>

**RMI** (*Remote Method Invocation*) è una tecnologia Java che permette a un oggetto in una JVM di invocare metodi di un oggetto che si trova in un’altra JVM, anche su un’altra macchina. Serve a realizzare applicazioni distribuite.

- Il **server** crea gli oggetti remoti e mette a disposizione i loro servizi.
- Il **client** trova un riferimento remoto e invoca i metodi offerti.
- Il metodo viene eseguito nella JVM che ospita l’oggetto; client e server non condividono direttamente la memoria.

La chiamata può avere una sintassi simile a quella locale, ma attraversa la rete: può richiedere tempo e fallire se il server o la rete non sono disponibili.

<h2 style="color:#C00000"><strong>2. OBIETTIVI E CARATTERISTICHE</strong></h2>

<h3 style="color:#C00000"><strong>INVOCAZIONE TRASPARENTE</strong></h3>

RMI nasconde al programmatore molti dettagli della comunicazione: il client invoca un metodo sull’interfaccia del servizio e il runtime si occupa di trasferire richiesta e risultato. “Trasparente” non vuol dire che una chiamata remota abbia le stesse garanzie di una chiamata locale: restano latenza, guasti e possibili errori di comunicazione.

<h3 style="color:#C00000"><strong>INTEGRAZIONE CON JAVA</strong></h3>

RMI usa i normali concetti del linguaggio: oggetti, classi, interfacce, metodi, eccezioni e serializzazione. L’interfaccia remota descrive il contratto del servizio; l’implementazione concreta può cambiare senza coinvolgere il client, se il contratto rimane compatibile.

<h3 style="color:#C00000"><strong>GARBAGE COLLECTION DISTRIBUITA</strong></h3>

Il garbage collector recupera memoria occupata da oggetti non più raggiungibili. In RMI, un oggetto server potrebbe essere ancora usato da un client in un’altra JVM. Il meccanismo di **distributed garbage collection (DGC)** tiene conto dei riferimenti remoti e aiuta a determinare quando un oggetto può essere recuperato.

Il DGC non elimina tutti i **memory leak**: se il programma conserva riferimenti inutili, gli oggetti restano raggiungibili e il garbage collector non li libera. Su un server in esecuzione a lungo, questo può portare a esaurire la memoria.

<h3 style="color:#C00000"><strong>COMPLESSITÀ DI CLIENT E SERVER</strong></h3>

RMI gestisce molti dettagli di comunicazione, così il programmatore può concentrarsi sulla logica del servizio. Rimane necessario progettare le chiamate e gestire ritardi, errori e indisponibilità.

<h3 style="color:#C00000"><strong>SICUREZZA E SANDBOX</strong></h3>

La **sandbox** è un ambiente controllato nel quale il codice può compiere solo operazioni autorizzate. Nel modello storico descritto dalle slide, serviva a limitare i danni che poteva causare codice scaricato dalla rete, per esempio impedendogli di leggere file riservati, cancellare documenti o avviare programmi.

La sandbox non è una proprietà intrinseca di RMI. Le slide descrivono il modello storico Java basato anche su classloader, bytecode verifier e Security Manager. Il Security Manager è stato disabilitato permanentemente da JDK 24; non bisogna assumere che il caricamento remoto sia sicuro per il solo fatto di usare RMI.

<h2 style="color:#C00000"><strong>3. MODALITÀ D’INVOCAZIONE E TRASPORTO</strong></h2>

- **Unicast:** un client invoca un singolo server. È il caso normale delle chiamate RMI unicast.
- **Multicast:** una richiesta viene inviata a più server replicati, per esempio per ottenere ridondanza. È un modello generale distribuito, non una funzionalità automaticamente fornita dalle normali chiamate RMI.
- **Attivazione su richiesta:** l’idea è attivare l’oggetto quando viene invocato invece di mantenerlo sempre attivo. RMI Activation è una funzione storica rimossa dal JDK 17.
- **Persistenza dei riferimenti:** un riferimento può consentire di ritrovare un oggetto in un secondo momento, ma conservarlo non garantisce che l’oggetto sopravviva a un riavvio del server.

Il **livello di trasporto** trasferisce i dati fra client e server e gestisce connessioni e richieste in arrivo. La separazione dei livelli consente di evolvere il trasporto senza riscrivere tutta la logica applicativa. La slide chiama **Java Remote Method Protocol** il protocollo proprietario usato da RMI nel modello presentato.

<h2 style="color:#C00000"><strong>4. OGGETTI DISTRIBUITI E INTERFACCIA REMOTA</strong></h2>

Un **oggetto remoto** è accessibile da un altro spazio di indirizzamento, cioè da un altro processo/JVM e, potenzialmente, da un’altra macchina. La JVM client non può leggere o modificare direttamente la memoria della JVM server: comunica inviando richieste e dati.

L’**interfaccia remota** dichiara i servizi accessibili e separa il contratto dall’implementazione. Il client dipende dall’interfaccia, non dalla classe concreta del server.

Esempio:

```java
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Calcolatrice extends Remote {
    int somma(int a, int b) throws RemoteException;
}
```

`Remote` è un’interfaccia marker: non contiene metodi, ma indica che l’interfaccia descrive un oggetto remoto. `somma` specifica parametri, risultato e possibile errore di comunicazione.

Solo i metodi dichiarati nell’interfaccia remota fanno parte del servizio esposto tramite RMI. Il fatto che un metodo della classe server sia `public` non lo rende automaticamente invocabile da un client remoto.

<h3 style="color:#C00000"><strong>ECCEZIONI REMOTE</strong></h3>

I metodi remoti dichiarano `RemoteException` oppure un supertipo ammesso. È una checked exception, quindi il chiamante deve gestirla con `try/catch` o dichiararla. Può segnalare, per esempio, che il server non è raggiungibile o che la comunicazione si è interrotta.

`RemoteException` deriva da `IOException`, coerentemente con gli errori che possono verificarsi durante la comunicazione.

<h3 style="color:#C00000"><strong>PACKAGE PRINCIPALI</strong></h3>

La suddivisione riportata nelle slide comprende cinque package:

| Package | Ruolo |
|---|---|
| `java.rmi` | Tipi fondamentali, tra cui `Remote`, `RemoteException` e `Naming`. |
| `java.rmi.server` | Supporto per implementare, esportare e gestire oggetti remoti sul server. |
| `java.rmi.activation` | Attivazione degli oggetti remoti; package storico rimosso dal JDK 17. |
| `java.rmi.dgc` | Supporto alla garbage collection distribuita. |
| `java.rmi.registry` | Registrazione e ricerca dei riferimenti remoti. |

<h2 style="color:#C00000"><strong>5. IMPLEMENTARE ED ESPORTARE UN OGGETTO REMOTO</strong></h2>

La classe server implementa l’interfaccia remota. Un modo consiste nell’estendere `UnicastRemoteObject`:

```java
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CalcolatriceImpl extends UnicastRemoteObject
        implements Calcolatrice {

    public CalcolatriceImpl() throws RemoteException {
        super();
    }

    @Override
    public int somma(int a, int b) throws RemoteException {
        return a + b;
    }
}
```

`implements Calcolatrice` realizza il contratto; il metodo contiene la logica concreta. Il costruttore dichiara `RemoteException` perché la preparazione dell’oggetto per l’uso remoto può fallire.

In Java una classe può estendere una sola classe. Se l’implementazione deve già estendere un’altra classe, si può implementare l’interfaccia remota ed esportare l’istanza esplicitamente:

```java
CalcolatriceImpl oggetto = new CalcolatriceImpl();
Calcolatrice riferimento =
    (Calcolatrice) UnicastRemoteObject.exportObject(oggetto, 0);
```

L’esportazione predispone l’oggetto a ricevere chiamate RMI. Il valore `0` chiede a RMI di scegliere una porta disponibile. **Esportare** l’oggetto e **registrarlo** nel registry sono passaggi distinti.

<h2 style="color:#C00000"><strong>6. RIFERIMENTI REMOTI, STUB E LOCALIZZAZIONE</strong></h2>

Il client non accede direttamente all’oggetto server. Usa uno **stub**, un riferimento locale che rappresenta il servizio remoto e inoltra le invocazioni attraverso RMI.

Per ottenere il riferimento il client può:

1. riceverlo direttamente da un’altra parte del programma; oppure
2. cercarlo tramite un servizio di naming, normalmente il **RMI Registry**.

Il registry funziona come un dizionario: associa un nome (chiave) al riferimento remoto (valore). Il client esegue `lookup`, riceve il riferimento/stub e poi invoca direttamente il servizio. Il registry aiuta a trovare il servizio: non contiene l’implementazione e non esegue né inoltra tutte le chiamate del servizio.

La classe `Naming` offre operazioni come `lookup`, `bind`, `rebind`, `unbind` e `list`. Un nome può avere forma simile a `rmi://server/Calcolatrice`. Il client usa il risultato attraverso il tipo dell’interfaccia remota:

```java
Calcolatrice calc =
    (Calcolatrice) Naming.lookup("rmi://server/Calcolatrice");
int risultato = calc.somma(2, 3);
```

Avere ottenuto uno stub non garantisce che il server resti raggiungibile: ogni chiamata può comunque fallire.

<h2 style="color:#C00000"><strong>7. PARAMETRI, RISULTATI E SERIALIZZAZIONE</strong></h2>

I parametri e i risultati di una chiamata remota devono poter essere trasferiti. I normali oggetti di valore sono serializzati: il destinatario ricostruisce una copia, e modificare quella copia non modifica automaticamente l’originale nella JVM mittente.

Quando si passano riferimenti a oggetti remoti, RMI trasferisce il riferimento remoto (lo stub), non l’implementazione concreta. Il destinatario può quindi continuare a invocare il servizio che rimane sul server.

La serializzazione Java mantiene anche la coerenza dei riferimenti all’interno dello stesso flusso: se due campi puntano allo stesso oggetto, dopo la ricostruzione possono puntare alla stessa copia. Questo non significa che copia e originale siano la stessa istanza in memoria.

<h3 style="color:#C00000"><strong>IDENTITÀ REMOTA E METODI DI <code>Object</code></strong></h3>

RMI adatta il comportamento di metodi come:

- `equals()`: confronta se due riferimenti rappresentano lo stesso oggetto remoto, non se i dati interni sono uguali;
- `hashCode()`: fornisce un valore coerente per usare i riferimenti remoti in strutture basate su hash;
- `toString()`: fornisce una rappresentazione testuale del riferimento remoto.

Cambiare una variabile locale che contiene lo stub non modifica l’oggetto server:

```java
Calcolatrice servizio = stub;
servizio = null; // cambia la variabile locale, non l’oggetto remoto
```

Per modificare lo stato dell’oggetto server, il client deve invocare un metodo che lo modifica, per esempio `servizio.impostaPrezzo(15)`.

<h2 style="color:#C00000"><strong>8. ARCHITETTURA A LIVELLI</strong></h2>

Java RMI è descritto attraverso tre livelli:

1. **Stub/Skeleton layer**: collega il programma ai livelli di comunicazione. Lo stub è dal lato client; lo skeleton riceve e distribuisce la chiamata sul server nel modello tradizionale.
2. **Remote Reference Layer**: gestisce semantica e riferimenti delle invocazioni remote.
3. **Transport Layer**: gestisce connessione e trasferimento dati tra macchine.

Il percorso tipico è:

**Client → Stub → Remote Reference Layer → Transport Layer → Skeleton → Implementazione**

La risposta percorre il cammino inverso.

<h3 style="color:#C00000"><strong>STUB E SKELETON: MARSHALLING</strong></h3>

Lo stub raccoglie i parametri, li prepara per il trasferimento (**marshalling**), attende la risposta, ricostruisce il risultato (**unmarshalling**) e lo restituisce al client. Sul server, lo skeleton ricostruisce i parametri, invoca il metodo dell’implementazione e prepara il risultato.

Le slide descrivono stub e skeleton statici. Nelle versioni moderne, gli stub sono in genere generati dinamicamente e non è necessaria una classe skeleton separata.

<h3 style="color:#C00000"><strong>REMOTE REFERENCE LAYER</strong></h3>

Questo livello gestisce il significato dell’invocazione e dei riferimenti remoti. Le slide citano unicast, multicast, attivazione e riconnessione come modalità o possibili comportamenti del modello distribuito; non tutte sono funzionalità automatiche del RMI moderno.

<h3 style="color:#C00000"><strong>TRANSPORT LAYER</strong></h3>

Il livello di trasporto stabilisce e gestisce le connessioni verso gli indirizzi remoti, resta in ascolto di richieste e consegna le chiamate al dispatcher/oggetto appropriato sul lato server.

<h2 style="color:#C00000"><strong>9. CARICAMENTO DINAMICO DELLE CLASSI E SICUREZZA</strong></h2>

Per ricostruire un oggetto serializzato, la JVM destinataria deve conoscere la sua classe. Nel modello tradizionale, se non è già disponibile, la definizione poteva essere cercata presso un **codebase**, un URL che indicava dove recuperare il file `.class`.

Il modello storico di sicurezza illustrato nelle slide include:

- **Classloader**, che carica le classi e ne gestisce origine e namespace;
- **Bytecode verifier**, che verifica che il bytecode rispetti le regole previste;
- **Security Manager**, che applicava i confini di sicurezza e i permessi.

Il Security Manager è stato disabilitato permanentemente da JDK 24. Non bisogna presumere che le classi vengano scaricate automaticamente o che il codice remoto sia sicuro: caricamento, configurazione e fiducia vanno considerati esplicitamente.

<h2 style="color:#C00000"><strong>10. MARSHALLING RMI E <code>replaceObject()</code></strong></h2>

RMI estende la serializzazione Java per gestire i riferimenti remoti. Durante il marshalling può sostituire un oggetto remoto con il suo stub. La slide descrive l’uso di `ObjectOutputStream` e dei metodi di personalizzazione:

- `replaceObject()`: sostituisce un oggetto durante la serializzazione. Per un oggetto remoto esportato restituisce lo stub. Secondo il meccanismo descritto nelle slide, se l’oggetto remoto non è esportato può essere restituito l’oggetto stesso; in tal caso la normale serializzazione deve poter gestire la classe e il suo stato. Per un oggetto non remoto si continua con la serializzazione ordinaria.
- `enableReplaceObject()`: abilita il meccanismo di sostituzione nel modello descritto.
- `annotateClass()`: permette di aggiungere annotazioni/informazioni sulla classe, usate nel meccanismo storico di caricamento dinamico.

In questo modo i parametri possono includere riferimenti a servizi remoti: il flusso contiene il riferimento/stub, non l’implementazione remota. La serializzazione conserva inoltre le relazioni tra riferimenti condivisi nello stesso flusso.

<h2 style="color:#C00000"><strong>11. PROCEDURA PER CREARE ED ESEGUIRE UN PROGRAMMA RMI</strong></h2>

1. **Definire l’interfaccia remota**, estendendo `Remote` e dichiarando `RemoteException` nei metodi.
2. **Implementare l’interfaccia sul server** e creare l’oggetto remoto.
3. **Esportare l’oggetto**, estendendo `UnicastRemoteObject` o usando `exportObject`.
4. **Compilare** le classi necessarie. La slide cita `rmic` per generare stub e skeleton statici; è una procedura storica e normalmente non serve con RMI moderno.
5. **Avviare il registry** RMI, se non viene avviato nel processo server.
6. **Registrare il riferimento del servizio** con un nome, usando le operazioni di naming appropriate.
7. **Implementare e avviare il client**: il client cerca il nome nel registry, ottiene lo stub e usa il servizio tramite l’interfaccia remota.
8. **Gestire gli errori remoti** nel client e nel server.

La policy `AllPermission` mostrata nelle slide concede permessi molto ampi ed è un esempio storico: non va copiata come impostazione di sicurezza generale. Registry, esportazione e registrazione sono operazioni distinte.

<h2 style="color:#C00000"><strong>12. ADAPTER</strong></h2>

L’**Adapter** è un pattern strutturale che consente al client di usare una classe esistente con un’interfaccia diversa da quella attesa. L’adapter espone l’interfaccia richiesta, riceve le chiamate e le traduce in operazioni della classe adattata. È utile quando la classe esistente non può o non deve essere modificata.

<h2 style="color:#C00000"><strong>DOMANDE POSTE DALL’UTENTE</strong></h2>

<h3 style="color:#C00000"><strong>CHE COS’È JAVA RMI?</strong></h3>

È una tecnologia Java che permette a un oggetto in una JVM di invocare metodi di un oggetto in un’altra JVM, anche su una macchina diversa, per realizzare applicazioni distribuite.

<h3 style="color:#C00000"><strong>QUALI SONO GLI OBIETTIVI PRINCIPALI DI JAVA RMI?</strong></h3>

Semplificare le chiamate remote, integrarsi nel linguaggio Java, rendere riconoscibile la distinzione tra oggetti locali e remoti, limitare la complessità di client e server e preservare le proprietà di sicurezza della piattaforma.

<h3 style="color:#C00000"><strong>CHE COSA SIGNIFICA INVOCAZIONE TRASPARENTE?</strong></h3>

RMI permette di invocare un metodo remoto con una sintassi simile a quella di una chiamata locale, mentre gestisce i dettagli della comunicazione. Rimangono possibili ritardi e fallimenti.

<h3 style="color:#C00000"><strong>PERCHÉ DEVE ESSERE CHIARO SE UN OGGETTO È LOCALE O REMOTO?</strong></h3>

Perché una chiamata remota comporta comunicazione tra JVM, possibili ritardi e fallimenti; la somiglianza nella sintassi non elimina queste differenze.

<h3 style="color:#C00000"><strong>UNA JVM PUÒ COMUNICARE CON LO SPAZIO DI INDIRIZZAMENTO DI UN’ALTRA?</strong></h3>

Sì, comunica scambiando richieste e dati, ma non accede direttamente alla memoria dell’altra JVM. Ogni processo ha il proprio spazio di indirizzamento.

<h3 style="color:#C00000"><strong>IL CLIENT USA L’INTERFACCIA REMOTA O LA CLASSE IMPLEMENTATA?</strong></h3>

Usa l’interfaccia remota, per esempio `Calcolatrice`, e riceve uno stub. Non ha bisogno di conoscere la classe concreta server `CalcolatriceImpl`.

<h3 style="color:#C00000"><strong>UN METODO <code>public</code> È ACCESSIBILE TRAMITE RMI SOLO PERCHÉ È <code>public</code>?</strong></h3>

No. Deve anche essere dichiarato nell’interfaccia remota e l’oggetto deve essere esportato.

<h3 style="color:#C00000"><strong>QUANDO SI USA <code>exportObject</code> INVECE DI ESTENDERE <code>UnicastRemoteObject</code>?</strong></h3>

Quando la classe non può estendere `UnicastRemoteObject`, per esempio perché estende già un’altra classe. Si esporta l’istanza esplicitamente e si usa il riferimento remoto restituito.

<h3 style="color:#C00000"><strong>SE <code>CalcolatriceImpl</code> ESTENDE GIÀ UN’ALTRA CLASSE, COME PUÒ DIVENTARE REMOTA?</strong></h3>

Può implementare `Calcolatrice` ed essere esportata con `UnicastRemoteObject.exportObject(oggetto, 0)`, senza estendere `UnicastRemoteObject`.

<h3 style="color:#C00000"><strong>QUAL È IL PERCORSO DELLA RICHIESTA E DEL RISULTATO?</strong></h3>

Richiesta: **Client → stub → rete/livelli RMI → skeleton o dispatcher → oggetto remoto**. La risposta torna dal server allo stub e quindi al client. Nel RMI moderno il runtime svolge il ruolo dello skeleton senza una classe separata.

<h3 style="color:#C00000"><strong>IL REGISTRY È COME UN DIZIONARIO?</strong></h3>

Sì: associa un nome al riferimento remoto. Il client cerca il nome con `lookup` e ottiene uno stub; poi invoca direttamente il servizio. Il registry non esegue i metodi.

<h2 style="color:#C00000"><strong>APPROFONDIMENTI UTILI PER L’ESAME</strong></h2>

### Obiettivi RMI da saper distinguere

- **Invocazione trasparente:** chiamare un metodo remoto con un’interfaccia Java, lasciando a RMI la comunicazione.
- **Integrazione:** usare oggetti, interfacce, eccezioni e strumenti Java anche in un sistema distribuito.
- **Distinzione locale/remoto:** non dimenticare che la chiamata remota ha latenza e guasti possibili.
- **Semplicità:** ridurre il codice di rete scritto a mano, mantenendo la gestione esplicita degli errori remoti.
- **Sicurezza:** rispettare le protezioni della piattaforma; RMI non rende sicuro automaticamente il codice scaricato o deserializzato.

### Percorso completo da ricordare

1. L’implementazione sul server viene creata ed **esportata**.
2. Il server pubblica il riferimento nel registry con un nome.
3. Il client esegue `lookup` e riceve uno stub attraverso l’interfaccia remota.
4. Il client invoca il metodo sullo stub.
5. Stub/runtime esegue marshalling e trasferisce la richiesta.
6. Il server ricostruisce i parametri, esegue il metodo e serializza la risposta.
7. Il client ricostruisce il risultato oppure riceve un errore remoto.

### Dettaglio dei livelli

- **Stub/Skeleton:** lo stub lato client apre o usa la comunicazione, effettua il marshalling degli argomenti, attende l’esito, fa unmarshalling del risultato e lo restituisce. Nel modello tradizionale lo skeleton lato server fa unmarshalling, invoca il metodo concreto e prepara il risultato.
- **Remote Reference Layer:** tiene conto del tipo di riferimento e della semantica di invocazione (per esempio unicast); le slide citano anche multicast, attivazione e riconnessione come modelli possibili o storici.
- **Transport Layer:** stabilisce e gestisce connessioni verso indirizzi IP remoti, ascolta richieste in arrivo, mantiene lo stato delle connessioni e consegna le richieste al dispatcher/oggetto corretto.
- Il protocollo mostrato nelle slide è **Java Remote Method Protocol**, protocollo proprietario usato per il trasporto delle chiamate RMI.

### Interfacce e tipi passati

Una firma di metodo remoto può contenere valori primitivi e oggetti serializzabili. Un riferimento remoto deve essere espresso tramite l’interfaccia remota del servizio, non tramite la classe concreta server. La serializzazione di un normale oggetto trasferisce il suo stato; per un oggetto remoto, RMI sostituisce l’implementazione con lo stub, se è stato esportato.

Un riferimento locale non può attraversare direttamente lo spazio di indirizzamento dell’altra JVM: il middleware costruisce uno stub che rappresenta il servizio remoto.

### Avvio ed esecuzione: esempio minimo moderno

Server (se si estende `UnicastRemoteObject`, l’istanza viene esportata dal costruttore):

```java
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) throws Exception {
        Calcolatrice servizio = new CalcolatriceImpl();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("Calcolatrice", servizio);
    }
}
```

Client:

```java
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        Calcolatrice servizio =
            (Calcolatrice) registry.lookup("Calcolatrice");
        System.out.println(servizio.somma(2, 3));
    }
}
```

Nel secondo modo di esportazione, il server registra il riferimento restituito da `exportObject(...)`. Registry e oggetto remoto possono trovarsi nello stesso processo o essere avviati separatamente, secondo la configurazione. Il client deve avere l’interfaccia e i tipi necessari a interpretare parametri e risultati.

### Caricamento dinamico e serializzazione: punti di attenzione

Nel modello storico, un **codebase** indica dove recuperare classi non presenti nella JVM ricevente. Il classloader carica la classe, il bytecode verifier controlla il bytecode e il Security Manager del vecchio modello applica permessi. Oggi non bisogna fare affidamento sul download automatico da URL né sul Security Manager come protezione disponibile.

La slide cita i metodi di personalizzazione della serializzazione:

- `replaceObject()` può sostituire un oggetto serializzato con il suo stub;
- `enableReplaceObject()` abilita la sostituzione nel modello descritto;
- `annotateClass()` aggiunge informazioni associate alla classe, tra cui quelle usate nel modello storico del codebase.

La sostituzione di riferimenti remoti è ciò che permette di passare un servizio remoto come argomento o risultato senza trasferire la sua implementazione server.

### Confronti che non vanno confusi

- **Registry vs stub:** il registry risolve un nome nel riferimento; lo stub viene usato per le chiamate.
- **Esportazione vs registrazione:** esportare rende l’oggetto raggiungibile via RMI; registrare associa il riferimento a un nome.
- **Oggetto serializzato vs oggetto remoto:** il primo viene copiato come dati; il secondo resta sul server e viene raggiunto tramite stub.
- **RMI vs chiamata locale:** la sintassi può somigliare, ma RMI può fallire per rete, latenza o indisponibilità del server.
- **Interfaccia `Remote` vs classe concreta:** il client programma contro il contratto; il server fornisce l’implementazione.

### Sequenza pratica da ripetere

**Interfaccia `extends Remote` → metodi `throws RemoteException` → implementazione → esportazione → registry → `bind/rebind` → client `lookup` → chiamata via interfaccia → gestione degli errori.**
