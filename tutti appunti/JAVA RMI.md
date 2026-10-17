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

La **sandbox** è un ambiente controllato nel quale il codice può compiere solo operazioni autorizzate. Nel modello storico descritto dalle slide, serviva a limitare i danni che poteva causare codice scaricato dalla rete, per esempio impedendogli di leggere file riservati, cancellare documenti, avviare programmi o aprire connessioni verso destinazioni non consentite.

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

Esempio ripreso dai tuoi appunti: `stampaDiagnostica()` può essere un metodo pubblico dell’implementazione, ma resta fuori dal contratto remoto se non è dichiarato in un’interfaccia remota. `somma(int a, int b)` indica invece nome dell’operazione, due argomenti interi, risultato intero e possibile `RemoteException`.

Metodo da inserire nella classe di implementazione, non nell’interfaccia remota:

```java
public void stampaDiagnostica() {
    // Operazione pubblica locale: non esposta dal contratto remoto.
}
```

Quando il parametro è un altro servizio remoto, usa il tipo della sua interfaccia:

```java
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReteCalcolatrici extends Remote {
    void collega(Calcolatrice altra) throws RemoteException;
}
```

Il ricevente può chiamare `altra.somma(...)` attraverso uno stub; non deve conoscere la classe concreta che realizza `altra`.


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
import java.rmi.RemoteException;

// Questa classe NON estende UnicastRemoteObject.
public class CalcolatriceSemplice implements Calcolatrice {
    @Override
    public int somma(int a, int b) throws RemoteException {
        return a + b;
    }
}
```

Poi, nel codice di avvio del server:

```java
CalcolatriceSemplice oggetto = new CalcolatriceSemplice();
Calcolatrice riferimento =
    (Calcolatrice) UnicastRemoteObject.exportObject(oggetto, 0);
```

Se necessario, `CalcolatriceSemplice` può estendere una propria superclasse locale. Non esportare di nuovo un’istanza della precedente `CalcolatriceImpl`: il costruttore di `UnicastRemoteObject` l’ha già esportata e una seconda esportazione genera `ExportException`. Il nome diverso evidenzia che sono due implementazioni alternative.

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

Il riferimento può anche essere già disponibile, senza un nuovo `lookup`. Frammento nel client:

```java
Calcolatrice calc = riferimentoRicevuto;
int risultato = calc.somma(2, 3);
```

| Operazione di naming | Effetto |
|---|---|
| `bind` | Associa un nome nuovo; fallisce se è già registrato. |
| `rebind` | Crea o sostituisce l’associazione del nome. |
| `lookup` | Cerca il riferimento associato al nome. |
| `unbind` | Rimuove l’associazione dal registry. |
| `list` | Elenca i nomi registrati. |

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

Per modificare lo stato sul server, il client deve invocare un metodo mutatore previsto dall’interfaccia. La `Calcolatrice` dell’esempio offre solo `somma`: per il prezzo serve un contratto diverso, per esempio:

```java
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProdottoRemoto extends Remote {
    void impostaPrezzo(double prezzo) throws RemoteException;
}
```

Frammento nel client, assumendo che `prodotto` sia stato ottenuto con `lookup`:

```java
ProdottoRemoto copiaDelRiferimento = prodotto;
copiaDelRiferimento = null;  // cambia solo questa variabile locale
prodotto.impostaPrezzo(15);  // invoca il metodo sul server
```

Non chiamare un metodo sulla variabile che hai appena posto a `null`: provocherebbe `NullPointerException` prima di ogni comunicazione di rete. In Java si passano sempre valori; nel caso remoto il valore trasmesso rappresenta un riferimento/stub, non un indirizzo di memoria condivisa tra JVM.

<h2 style="color:#C00000"><strong>8. ARCHITETTURA A LIVELLI</strong></h2>

Java RMI è descritto attraverso tre livelli:

1. **Stub/Skeleton layer**: collega il programma ai livelli di comunicazione. Lo stub è dal lato client; lo skeleton riceve e distribuisce la chiamata sul server nel modello tradizionale.
2. **Remote Reference Layer**: gestisce semantica e riferimenti delle invocazioni remote.
3. **Transport Layer**: gestisce connessione e trasferimento dati tra macchine.

Il percorso tipico è:

**Client → stub → Remote Reference Layer client → trasporto/rete → Remote Reference Layer server → skeleton/dispatcher → implementazione**

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

Sequenza descritta nel testo originale: il marshalling annota il flusso con informazioni sulle classi e sul codebase; all’unmarshalling il classloader prova a risolvere la classe disponibile localmente e, se la configurazione storica lo permette, può recuperarla dal codebase. Il modello riguardava anche il caricamento degli stub. Scaricare la classe e ricostruire l’oggetto sono passaggi distinti: i byte dello stato non costituiscono da soli la definizione della classe.


Il modello storico di sicurezza illustrato nelle slide include:

- **Classloader**, che carica le classi e ne gestisce origine e namespace;
- **Bytecode verifier**, che verifica che il bytecode rispetti le regole previste;
- **Security Manager**, che applicava i confini di sicurezza e i permessi.

Il Security Manager è stato disabilitato permanentemente da JDK 24. Non bisogna presumere che le classi vengano scaricate automaticamente o che il codice remoto sia sicuro: caricamento, configurazione e fiducia vanno considerati esplicitamente.

<h2 style="color:#C00000"><strong>10. MARSHALLING RMI E <code>replaceObject()</code></strong></h2>

RMI estende la serializzazione Java per gestire i riferimenti remoti. Durante il marshalling può sostituire un oggetto remoto con il suo stub. La slide descrive l’uso di `ObjectOutputStream` e dei metodi di personalizzazione:

- `replaceObject()`: sostituisce un oggetto durante la serializzazione. Per un oggetto remoto esportato restituisce lo stub. Secondo il meccanismo descritto nelle slide, se l’oggetto remoto non è esportato può essere restituito l’oggetto stesso; in tal caso la normale serializzazione deve poter gestire la classe e il suo stato. Per un oggetto non remoto si continua con la serializzazione ordinaria.
- `enableReplaceObject(true)`: abilita il meccanismo di sostituzione nel modello descritto.
- `annotateClass()`: permette di aggiungere annotazioni/informazioni sulla classe, usate nel meccanismo storico di caricamento dinamico.

Per un oggetto remoto esportato, il meccanismo descritto nelle slide recupera lo stub tramite `RemoteObject.toStub(...)`. In questo caso il flusso contiene il riferimento/stub, non l’implementazione remota. Un oggetto `Remote` non esportato non diventa raggiungibile da remoto soltanto perché può essere serializzato. La serializzazione conserva inoltre le relazioni tra riferimenti condivisi nello stesso flusso.

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

Nel procedimento storico del corso: compilazione delle classi → eventuale `rmic` per stub/skeleton del vecchio JDK → avvio del registry → avvio e registrazione server → avvio client. Il client deve avere interfacce e classi condivise; con stub statici deve poter caricare anche la classe stub. Con stub dinamici non si copia invece un file stub generato da `rmic`.


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

<h3 style="color:#C00000"><strong>OBIETTIVI RMI DA SAPER DISTINGUERE</strong></h3>

- **Invocazione trasparente:** chiamare un metodo remoto con un’interfaccia Java, lasciando a RMI la comunicazione.
- **Integrazione:** usare oggetti, interfacce, eccezioni e strumenti Java anche in un sistema distribuito.
- **Distinzione locale/remoto:** non dimenticare che la chiamata remota ha latenza e guasti possibili.
- **Semplicità:** ridurre il codice di rete scritto a mano, mantenendo la gestione esplicita degli errori remoti.
- **Sicurezza:** rispettare le protezioni della piattaforma; RMI non rende sicuro automaticamente il codice scaricato o deserializzato.

<h3 style="color:#C00000"><strong>PERCORSO COMPLETO DA RICORDARE</strong></h3>

1. L’implementazione sul server viene creata ed **esportata**.
2. Il server pubblica il riferimento nel registry con un nome.
3. Il client esegue `lookup` e riceve uno stub attraverso l’interfaccia remota.
4. Il client invoca il metodo sullo stub.
5. Stub/runtime esegue marshalling e trasferisce la richiesta.
6. Il server ricostruisce i parametri, esegue il metodo e serializza la risposta.
7. Il client ricostruisce il risultato oppure riceve un errore remoto.

<h3 style="color:#C00000"><strong>DETTAGLIO DEI LIVELLI</strong></h3>

- **Stub/Skeleton:** lo stub lato client apre o usa la comunicazione, effettua il marshalling degli argomenti, attende l’esito, fa unmarshalling del risultato e lo restituisce. Nel modello tradizionale lo skeleton lato server fa unmarshalling, invoca il metodo concreto e prepara il risultato.
- **Remote Reference Layer:** tiene conto del tipo di riferimento e della semantica di invocazione (per esempio unicast); le slide citano anche multicast, attivazione e riconnessione come modelli possibili o storici.
- **Transport Layer:** stabilisce e gestisce connessioni verso indirizzi IP remoti, ascolta richieste in arrivo, mantiene lo stato delle connessioni e consegna le richieste al dispatcher/oggetto corretto.
- Il protocollo mostrato nelle slide è **Java Remote Method Protocol**, protocollo proprietario usato per il trasporto delle chiamate RMI.

<h3 style="color:#C00000"><strong>INTERFACCE E TIPI PASSATI</strong></h3>

Una firma di metodo remoto può contenere valori primitivi e oggetti serializzabili. Un riferimento remoto deve essere espresso tramite l’interfaccia remota del servizio, non tramite la classe concreta server. La serializzazione di un normale oggetto trasferisce il suo stato; per un oggetto remoto, RMI sostituisce l’implementazione con lo stub, se è stato esportato.

Un riferimento locale non può attraversare direttamente lo spazio di indirizzamento dell’altra JVM: il middleware costruisce uno stub che rappresenta il servizio remoto.

<h3 style="color:#C00000"><strong>AVVIO ED ESECUZIONE: ESEMPIO MINIMO MODERNO</strong></h3>

Server (se si estende `UnicastRemoteObject`, l’istanza viene esportata dal costruttore; i campi statici mantengono riferimenti forti agli oggetti pubblicati):

```java
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    private static Calcolatrice servizio;
    private static Registry registry;

    public static void main(String[] args) throws Exception {
        servizio = new CalcolatriceImpl();
        registry = LocateRegistry.createRegistry(1099);
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

<h3 style="color:#C00000"><strong>CARICAMENTO DINAMICO E SERIALIZZAZIONE: PUNTI DI ATTENZIONE</strong></h3>

Nel modello storico, un **codebase** indica dove recuperare classi non presenti nella JVM ricevente. Il classloader carica la classe, il bytecode verifier controlla il bytecode e il Security Manager del vecchio modello applica permessi. Oggi non bisogna fare affidamento sul download automatico da URL né sul Security Manager come protezione disponibile.

La slide cita i metodi di personalizzazione della serializzazione:

- `replaceObject()` può sostituire un oggetto serializzato con il suo stub;
- `enableReplaceObject(true)` abilita la sostituzione nel modello descritto;
- `annotateClass()` aggiunge informazioni associate alla classe, tra cui quelle usate nel modello storico del codebase.

La sostituzione di riferimenti remoti è ciò che permette di passare un servizio remoto come argomento o risultato senza trasferire la sua implementazione server.

<h3 style="color:#C00000"><strong>CONFRONTI CHE NON VANNO CONFUSI</strong></h3>

- **Registry vs stub:** il registry risolve un nome nel riferimento; lo stub viene usato per le chiamate.
- **Esportazione vs registrazione:** esportare rende l’oggetto raggiungibile via RMI; registrare associa il riferimento a un nome.
- **Oggetto serializzato vs oggetto remoto:** il primo viene copiato come dati; il secondo resta sul server e viene raggiunto tramite stub.
- **RMI vs chiamata locale:** la sintassi può somigliare, ma RMI può fallire per rete, latenza o indisponibilità del server.
- **Interfaccia `Remote` vs classe concreta:** il client programma contro il contratto; il server fornisce l’implementazione.

<h3 style="color:#C00000"><strong>SEQUENZA PRATICA DA RIPETERE</strong></h3>

**Interfaccia `extends Remote` → metodi `throws RemoteException` → implementazione → esportazione → registry → `bind/rebind` → client `lookup` → chiamata via interfaccia → gestione degli errori.**

<h2 style="color:#C00000"><strong>DOMANDE NUMERATE — JAVA RMI (9–48)</strong></h2>

### 9. Come influì l'esperienza di Jim Waldo con CORBA sulla progettazione di Java RMI?

L'esperienza con CORBA mostrò il costo di un middleware generale, pensato per linguaggi e piattaforme diversi: IDL, mapping e molte scelte infrastrutturali complicavano il caso Java–Java. RMI fu orientato a usare direttamente interfacce, oggetti, eccezioni e serializzazione di Java, semplificando quel caso. Waldo sottolineò anche che una chiamata remota non può essere del tutto indistinguibile da una locale: rete, latenza e guasti devono restare visibili al programmatore. È una motivazione progettuale, non una promessa di trasparenza totale.

### 10. Perché RMI doveva essere un ambiente semplice e familiare al programmatore Java?

**Semplice**: evitare che ogni applicazione debba costruire protocollo, stub, trasferimento dei parametri e ricerca dei servizi con socket grezzi. **Familiare**: descrivere i servizi con interfacce e chiamarli come metodi Java, usando tipi, eccezioni e strumenti già conosciuti. Questo riduce codice ripetitivo e curva di apprendimento, pur lasciando necessaria la gestione dei guasti di rete.

### 11. Quali vantaggi dà integrare il modello distribuito nel linguaggio?

Il compilatore controlla le firme e i tipi delle interfacce remote; IDE e refactoring lavorano sugli stessi costrutti Java; oggetti serializzabili, eccezioni e garbage collection sono riutilizzati; client e server condividono un contratto verificabile. Si evita di tradurre manualmente un'IDL esterna in tipi Java. Il costo è una minore interoperabilità diretta con programmi non Java rispetto a un middleware multipiattaforma.

### 12. Perché la garbage collection semplifica il lavoro del programmatore?

Recupera automaticamente la memoria degli oggetti non più raggiungibili, evitando molte `free` manuali e i relativi errori, come liberazioni premature e doppie liberazioni. In RMI la **distributed garbage collection** aiuta a tenere conto dei riferimenti remoti. Il programmatore deve comunque chiudere risorse come file e socket e non conservare riferimenti inutili.

### 13. Quali sono i rischi dei memory leak e perché sono critici nei server?

Un leak trattiene risorse non più utili: in Java spesso è un riferimento rimasto in una cache, lista, listener o thread; possono essere trattenuti anche file e socket. Crescono uso di memoria, pause di GC e rischio di `OutOfMemoryError`. Un server vive a lungo e ripete richieste, quindi anche una piccola perdita per richiesta si accumula fino a rallentare o interrompere il servizio per molti client.

### 14. Quali vantaggi e svantaggi hanno gestione manuale della memoria e garbage collection?

| Gestione | Vantaggi | Svantaggi |
|---|---|---|
| Manuale | Controllo esplicito di allocazione e rilascio; utile dove tempi e risorse richiedono un controllo preciso. | Richiede più lavoro; possibili leak, use-after-free, doppia liberazione e errori di concorrenza. |
| Garbage collection | Libera gli oggetti non raggiungibili; semplifica il codice ed evita molti errori di memoria manuale. | Consuma risorse e può introdurre pause; non libera oggetti ancora raggiungibili per errore né sostituisce la chiusura delle risorse esterne. |

### 15. Cosa sono invocazioni unicast, multicast e di oggetti attivabili?

**Unicast**: una chiamata diretta a un solo oggetto remoto, modello ordinario di Java RMI. **Multicast**: una richiesta logica verso più destinatari, per esempio repliche; non è una modalità automatica della normale API RMI. **Oggetto attivabile**: il servizio può essere avviato o riattivato su richiesta; la *RMI Activation* storica è stata rimossa dal JDK 17.

### 16. A cosa serve un'invocazione multicast?

Può inviare un aggiornamento a repliche o cache, oppure interrogare più servizi per ridondanza. Bisogna stabilire quanti risultati attendere, cosa fare con risposte discordanti e come gestire duplicati e guasti. Per ottenerla con RMI ordinario serve una logica applicativa o un altro sistema di comunicazione.

### 17. A cosa servono gli oggetti attivabili?

Consentono di non tenere sempre in memoria servizi usati raramente: il sistema avvia il processo o ricrea l'oggetto alla prima richiesta, riducendo il consumo quando è inattivo. Occorre però ricostruire lo stato persistente e accettare il ritardo di avvio. È il modello storico di RMI Activation; oggi si realizza con altre soluzioni di gestione del servizio.

### 18. Perché sostituire il trasporto senza modificare gli strati superiori?

Se il trasporto espone un contratto stabile al livello dei riferimenti remoti, si possono cambiare connessione, protocollo o configurazione di rete mantenendo interfaccia e logica applicativa. Questo riduce modifiche e regressioni e consente adattamenti infrastrutturali. La sostituzione resta possibile solo se il nuovo trasporto preserva le proprietà richieste dagli strati superiori, come indirizzamento e consegna.

### 19. Cosa significa che un'applicazione Java viene eseguita in una sandbox?

Nel modello storico, codice di origine non fidata poteva essere eseguito con permessi limitati da una policy: per esempio senza accesso a file locali o connessioni arbitrarie. La sandbox combina controlli sul codice caricato e sulle operazioni consentite. Non significa che ogni programma Java o RMI sia automaticamente isolato. Il `SecurityManager` su cui si basavano le policy storiche è disabilitato permanentemente dal JDK 24.

### 20. Quali sono i quattro livelli di sicurezza descritti nel modello didattico Java?

Una suddivisione utile del modello storico è: **(1) linguaggio e compilatore**, **(2) class loader**, **(3) bytecode verifier**, **(4) controlli dei permessi a runtime tramite Security Manager e policy**. I primi tre limitano codice malformato o incompatibile; il quarto limita azioni sensibili. Le implementazioni Java moderne sono cambiate: questa è una descrizione del modello del corso, non una lista di protezioni da presumere oggi per RMI.

### 21. Qual è l'obiettivo di ciascuno dei quattro livelli? Esempi?

| Livello | Obiettivo ed esempio |
|---|---|
| Linguaggio/compilatore | Impedire nel sorgente operazioni non tipate e accessi contrari alle regole Java: `String s = 3` non compila; un campo `private` non è accessibile arbitrariamente. |
| Class loader | Caricare e separare classi per origine e namespace, evitando che un'applicazione sostituisca liberamente classi fondamentali della piattaforma. |
| Bytecode verifier | Controllare il `.class` prima dell'esecuzione: tipi coerenti sullo stack, salti validi, inizializzazione corretta, accessi ammessi. |
| Security Manager/policy storici | Autorizzare operazioni sensibili: una policy poteva concedere `FilePermission` su una directory o `SocketPermission` su un host, negando il resto. |

### 22. Perché il Bytecode Verifier ripete controlli già fatti dal compilatore?

La JVM esegue **bytecode**, che può provenire da rete, file modificati, generatori diversi da `javac` o compilatori difettosi. Non può fidarsi che il sorgente sia stato compilato correttamente. Il verifier controlla la classe effettivamente caricata, anche se la provenienza non è nota; non sostituisce i controlli di permesso sulle operazioni a runtime.

### 23. In quali package si struttura Java RMI e che cosa fanno?

`java.rmi` contiene `Remote`, `RemoteException` e `Naming`; `java.rmi.server` fornisce esportazione e supporto ai riferimenti; `java.rmi.registry` contiene il registry e la ricerca; `java.rmi.dgc` riguarda la garbage collection distribuita. Nel corso compare anche `java.rmi.activation`, package storico per l'attivazione, rimosso dal JDK 17.

### 24. Cos'è un'interfaccia remota?

È un'interfaccia Java che estende `java.rmi.Remote` e dichiara le operazioni invocabili da altre JVM. Ogni metodo remoto deve dichiarare `RemoteException` o un suo supertipo ammesso. Il client conosce l'interfaccia, mentre il server la implementa ed esporta l'oggetto. Esempio: `interface Hello extends Remote { String sayHello() throws RemoteException; }`.

### 25. Come vengono distinti i metodi remoti?

Sono i metodi dichiarati in un'interfaccia che estende `Remote` (anche tramite ereditarietà) e dichiarano la possibile eccezione remota. Un metodo `public` aggiuntivo nella classe di implementazione resta locale e non appare sullo stub. Servono inoltre un'istanza esportata e un riferimento remoto per invocarli da un'altra JVM.

### 26. In che senso l'interfaccia remota aggiunge un modificatore di accesso oltre a `public`, `private`, ecc.?

È una **metafora**: la presenza nel contratto remoto decide se un metodo è esposto tramite RMI, oltre alla normale visibilità Java. Non esiste una keyword Java `remote` né un nuovo modificatore del linguaggio. Un metodo `public` non incluso nell'interfaccia remota non diventa per questo invocabile sullo stub.

### 27. Come devono essere i parametri di un metodo remoto?

Devono essere trasferibili: tipi primitivi, oggetti serializzabili (insieme al loro grafo di campi serializzati), oppure riferimenti a oggetti remoti esportati, espressi tramite interfacce remote. Le stesse regole valgono per risultati e eccezioni applicative trasmesse. Tipi che non implementano `Serializable` possono compilare ma causare un errore durante il marshalling.

### 28. Cosa significa che un oggetto locale viene passato per copia?

RMI serializza un normale oggetto serializzabile e ricostruisce un'altra istanza nella JVM ricevente. Le modifiche alla copia non aggiornano l'originale. Java passa sempre **per valore** gli argomenti: in questo caso il valore trasferito è una rappresentazione serializzata dell'oggetto; non attraversa la rete un puntatore alla memoria originale.

### 29. Cosa garantisce l'integrità referenziale?

Durante la serializzazione di **un singolo grafo di oggetti**, la tabella dei riferimenti conserva le condivisioni e i cicli: due campi che puntano allo stesso oggetto prima della trasmissione punteranno alla stessa **copia** dopo la deserializzazione. RMI preserva inoltre l'identità dei riferimenti a uno stesso oggetto remoto esportato. Non significa condividere memoria tra JVM né garantire identità tra copie spedite in chiamate indipendenti.

### 30. Un esempio di integrità referenziale?

Se `contenitore.a == contenitore.b` perché entrambi puntano alla stessa lista, serializzando `contenitore` in un unico `writeObject` il ricevente ottiene `copia.a == copia.b`. La lista ricevuta è però una copia: `copia.a != contenitore.a` tra le due JVM. Se la stessa lista fosse spedita in due chiamate separate, le due copie non avrebbero necessariamente la stessa identità.

### 31. Come si localizza un oggetto remoto?

Il server esporta l'oggetto e ne registra il riferimento con un nome nel `Registry`, per esempio `registry.rebind("Hello", stub)`. Il client conosce host, porta del registry (di norma 1099) e nome, ottiene il registry con `LocateRegistry.getRegistry(host, porta)` e chiama `lookup("Hello")`. Può anche ricevere un riferimento remoto come parametro o risultato, senza fare un nuovo lookup. Il registry non esegue le chiamate del servizio.

### 32. Perché il compilatore impone la gestione di `RemoteException`?

È una *checked exception*: un metodo remoto può non raggiungere il server o perdere la risposta, quindi il chiamante deve gestire o dichiarare il problema. Questo evita che la somiglianza sintattica con un metodo locale nasconda la possibilità di un guasto di comunicazione. Una `RemoteException` non dice sempre se il metodo sul server sia stato eseguito; occorre progettare con cura i retry.

### 33. Cos'è un metodo idempotente e perché è utile da remoto?

Ripeterlo una o più volte con gli stessi argomenti ha lo stesso effetto finale di eseguirlo una volta. `impostaSaldo(100)` può esserlo; `aggiungiAlSaldo(100)` normalmente non lo è. Se la risposta si perde, il client potrebbe non sapere se l'operazione è stata eseguita: riprovare un'operazione idempotente evita di duplicarne l'effetto. Restano necessari gestione delle eccezioni e controllo della concorrenza.

### 34. Perché gli oggetti remoti dovrebbero somigliare agli oggetti locali?

Interfacce, metodi e tipi Java permettono di riusare competenze, controlli statici e parte della logica applicativa. Il codice client può dipendere da un contratto invece che dai dettagli di rete. La somiglianza è utile finché non induce a fare troppe chiamate piccole: la latenza di rete favorisce operazioni remote più significative.

### 35. Perché non è possibile la trasparenza totale fra oggetti locali e remoti?

Una chiamata remota attraversa processi e rete: ha latenza, può fallire o completarsi senza che arrivi la risposta; parametri e risultati devono essere trasferiti, spesso per copia; l'oggetto server può sparire o essere riavviato. Una chiamata locale non presenta le stesse condizioni. `RemoteException` e il contratto remoto rendono visibile questa differenza.

### 36. Cosa fa `RemoteObject` e quali metodi di `Object` ridefinisce?

`java.rmi.server.RemoteObject` è una base storica per oggetti e stub remoti; contiene un `RemoteRef` e offre `getRef()` e `toStub(Remote)`. Ridefinisce `equals()`, `hashCode()` e `toString()` in termini del riferimento remoto, così due rappresentazioni dello stesso oggetto remoto possono essere confrontate coerentemente. `UnicastRemoteObject` ne deriva. Il normale codice client lavora con l'interfaccia remota.

### 37. Qual è la differenza nel passaggio di parametri remoti?

Un **oggetto di valore** serializzabile viene passato per copia; un **oggetto remoto esportato** viene rappresentato da un riferimento/stub che permette al destinatario di chiamare l'originale. Passare uno stub è comunque passare per valore un riferimento remoto, non un puntatore Java condiviso. Un oggetto che implementa `Remote` ma non è esportato non diventa automaticamente raggiungibile.

### 38. Com'è organizzata l'architettura a layer di RMI e come comunicano i layer?

Nel modello classico: **applicazione client → stub → Remote Reference Layer client → Transport Layer client → rete → Transport Layer server → Remote Reference Layer server → skeleton/dispatcher → implementazione**. La risposta torna lungo il percorso inverso. Ogni livello usa il servizio del livello sottostante e presenta un'interfaccia a quello superiore: il trasporto trasferisce dati, il livello dei riferimenti interpreta la destinazione e la semantica, stub/dispatcher traducono metodo e argomenti. Nel RMI moderno il dispatcher svolge il ruolo dello skeleton separato.

### 39. Quali vantaggi e svantaggi ha un'architettura a layer?

**Vantaggi:** separazione delle responsabilità, sostituibilità e testabilità dei componenti, riuso del trasporto e minori modifiche nell'applicazione quando cambia un livello. **Svantaggi:** attraversare più livelli aggiunge astrazione e un certo costo; la diagnosi di un guasto può richiedere di seguire l'intero percorso; un confine troppo rigido può impedire ottimizzazioni o nascondere proprietà del trasporto utili all'applicazione.

### 40. Che cosa fanno Stub/Skeleton, Remote Reference e Transport layer?

**Stub/Skeleton:** lo stub lato client prepara gli argomenti, invia la chiamata e ricostruisce il risultato; lo skeleton storico o dispatcher lato server ricostruisce gli argomenti, chiama l'implementazione e prepara risposta o eccezione. **Remote Reference:** rappresenta l'identità/destinazione dell'oggetto e la semantica dell'invocazione, normalmente unicast. **Transport:** apre e gestisce connessioni, ascolta e trasferisce i byte fra JVM, smistando le richieste entranti. I confini implementativi precisi possono variare tra versioni.

### 41. In che cosa il marshalling differisce dalla serializzazione?

La **serializzazione** codifica un oggetto Java e il suo grafo in byte. Il **marshalling** prepara un'intera chiamata remota per il protocollo: identifica metodo e destinatario, dispone parametri e tipi, serializza i valori, sostituisce dove necessario gli oggetti remoti con riferimenti/stub e prepara il risultato o l'eccezione. La serializzazione può dunque essere uno strumento usato dal marshalling, ma non comprende da sola tutta l'invocazione.

### 42. Che cosa specializza `ObjectOutputStream` nello stream di marshalling RMI?

Nel modello storico `MarshalOutputStream`, interno al runtime RMI, estende `ObjectOutputStream`: annota le classi con informazioni di codebase (`annotateClass`/`annotateProxyClass`) e abilita la sostituzione (`enableReplaceObject`/`replaceObject`) degli oggetti remoti esportati con i loro stub. Così un parametro remoto conserva la sua natura di riferimento invece di essere copiato come normale oggetto. Non è un'API applicativa pubblica su cui basare nuovo codice.

### 43. Quali passi creano il server RMI?

Definire l'interfaccia `extends Remote` con metodi che dichiarano `RemoteException`; implementarla; compilare; creare ed **esportare** l'istanza, tramite `UnicastRemoteObject` o `exportObject`; avviare o contattare il registry; registrare lo stub con `bind`/`rebind`; mantenere il servizio attivo e gestire gli errori. L'esportazione apre la possibilità di chiamata; la registrazione lo rende cercabile per nome.

### 44. Quali passi creano il client RMI?

Disporre dell'interfaccia remota e dei tipi condivisi compatibili; ottenere il registry dall'host/porta noti; eseguire `lookup` del nome; trattare il risultato come interfaccia remota; invocare i metodi e gestire `RemoteException` e le eccezioni applicative. Se riceve lo stub da un'altra chiamata, il lookup iniziale può non servire.

### 45. Quali passi del server devono precedere l'uso del client?

Il contratto condiviso deve essere disponibile per compilare il client. Perché il client possa fare `lookup`, il registry deve essere raggiungibile e il server deve avere **esportato** e **registrato** il servizio. Il codice client può essere scritto e compilato prima dell'avvio del server; è la sua esecuzione utile a richiedere quei prerequisiti. Il registry da solo non basta se il servizio non è registrato.

### 46. A cosa serve `rmic`?

È il compilatore RMI storico che, a partire dalla classe di implementazione compilata, genera classi di **stub statici** e, con opzioni legacy come `-vcompat`, anche **skeleton**. Queste classi vanno distribuite dove necessarie. Con gli stub dinamici introdotti nelle versioni successive non serve per i normali progetti RMI; il JDK 21 di questo ambiente non include `rmic`.

### 47. Cosa fare dopo modifiche all'implementazione server, al client o all'interfaccia?

| Modifica | Passi strettamente necessari |
|---|---|
| Solo implementazione server, firma remota invariata | Ricompilare e ridistribuire il server, poi riavviare o aggiornare il servizio. Una modifica interna che lascia invariato il contratto non richiede ricompilare il client né rigenerare gli stub statici. |
| Solo implementazione client | Ricompilare e ridistribuire il client; il server può restare attivo se il contratto non cambia. |
| Interfaccia remota o tipi condivisi | Ricompilare entrambe le parti contro la stessa versione compatibile, rigenerare e ridistribuire eventuali stub/skeleton statici, poi avviare le versioni coerenti. Un cambiamento incompatibile richiede migrazione coordinata. |

### 48. Se client e server sono progetti separati, quali file condividono?

Devono condividere la **stessa definizione compatibile delle interfacce remote** e delle classi/enum serializzabili usate nelle firme e nei grafi trasmessi. Nel flusso legacy con `rmic`, il client deve avere anche la classe stub generata (`*_Stub.class`); lo skeleton (`*_Skel.class`) sta sul server se usato. La classe concreta dell'implementazione server non è richiesta dal client. Nel RMI moderno gli stub dinamici eliminano il file stub condiviso.

Le risposte agli esercizi **49–51** e i sorgenti eseguibili sono in [HelloWorld RMI](../tutti%20appunti%20pratici/ESERCIZI%20HELLOWORLD%20RMI.md).
