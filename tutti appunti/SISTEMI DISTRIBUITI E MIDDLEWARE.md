<h1 style="color:#C00000"><strong>Sistemi distribuiti e middleware — Appunti teorici</strong></h1>

Questi appunti raccolgono il modello dei sistemi distribuiti, i requisiti di qualità, le trasparenze e l’evoluzione dal middleware RPC agli oggetti e componenti distribuiti, compresi gli esempi .NET ed Enterprise Java.

<h2 style="color:#C00000"><strong>1. Modello di riferimento e RM-ODP</strong></h2>

Un **modello di riferimento** è un modello concettuale, indipendente da una tecnologia specifica, che aiuta a descrivere e progettare un sistema.

**RM-ODP** (*Reference Model for Open Distributed Processing*) è un modello di riferimento per l’elaborazione distribuita aperta. Descrive il sistema da punti di vista diversi e aiuta a separare i problemi. La nota secondo cui “aggiunge il layer APPLICATION a OSI” è una semplificazione: RM-ODP non è semplicemente un nuovo livello dello stack OSI, ma un modello architetturale distinto e complementare.

<h2 style="color:#C00000"><strong>2. Caratteristiche dei sistemi distribuiti</strong></h2>

- **Remoti:** le componenti possono trovarsi su macchine fisicamente diverse e comunicare tramite rete.
- **Concorrenti:** più attività procedono nello stesso periodo su nodi diversi o nella stessa macchina. Senza coordinamento non si può presumere un ordine unico delle operazioni.
- **Senza stato globale immediatamente disponibile:** non esiste necessariamente un unico osservatore che conosca in ogni istante lo stato aggiornato di tutti i nodi. La rete introduce ritardi e informazioni che arrivano in momenti diversi.
- **Soggetti a malfunzionamenti parziali:** un nodo o collegamento può guastarsi mentre altri componenti continuano a funzionare.
- **Eterogenei e autonomi:** possono includere hardware, sistemi operativi, linguaggi e servizi differenti, gestiti da amministratori diversi.
- **In evoluzione:** componenti e tecnologie possono essere sostituite o aggiornate nel tempo.
- **Mobili:** nodi, processi, servizi o dati possono essere spostati tra macchine o luoghi diversi.

<h2 style="color:#C00000"><strong>3. Requisiti non funzionali</strong></h2>

I **requisiti non funzionali** descrivono qualità e vincoli del sistema, cioè come le funzioni devono essere fornite, non le singole operazioni richieste.

| Qualità | Significato |
|---|---|
| Apertura | Interfacce e protocolli documentati e standard facilitano interoperabilità ed evoluzione. La portabilità permette di eseguire o adattare moduli in ambienti diversi. |
| Integrazione | Sistemi e risorse differenti possono collaborare senza che ogni applicazione debba conoscere dettagli specifici di ciascuna piattaforma. |
| Flessibilità | Il sistema può riconfigurarsi e cambiare organizzazione quando variano requisiti o carico. |
| Modularità | Il sistema è suddiviso in componenti con compiti definiti; si può aggiornare un modulo limitando gli effetti sul resto. |
| Federazione | Sistemi autonomi collaborano per offrire un servizio mantenendo ciascuno la propria gestione. |
| Gestibilità | Sono disponibili strumenti e procedure per configurare, monitorare, aggiornare e amministrare il sistema. |
| Qualità del servizio | Prestazioni, disponibilità e affidabilità rispettano livelli adeguati. La tolleranza ai guasti mira a mantenere almeno le funzioni essenziali quando una parte si guasta. |
| Scalabilità | Il sistema mantiene un servizio adeguato quando crescono utenti, richieste o dati, per esempio aggiungendo risorse o distribuendo il carico. |
| Sicurezza | Accessi e operazioni sono consentiti solo a soggetti autorizzati e i dati sono protetti. |
| Trasparenza | L’utente o il programmatore non deve gestire direttamente ogni dettaglio della distribuzione, come la posizione fisica dei server. |

Queste sono qualità da progettare e verificare: la distribuzione o l’uso di middleware non le garantiscono automaticamente.

<h2 style="color:#C00000"><strong>4. Trasparenza della distribuzione</strong></h2>

La **trasparenza** nasconde alcuni dettagli della distribuzione agli utenti o al programmatore, così da fornire un’interfaccia più uniforme. Non significa che ogni dettaglio sia sempre invisibile: ritardi e guasti di rete possono rendere evidente la natura distribuita.

| Tipo di trasparenza | Che cosa nasconde |
|---|---|
| Accesso | Differenze di rappresentazione e uso di una risorsa locale o remota. |
| Locazione | La posizione fisica della risorsa. |
| Migrazione | Lo spostamento di una risorsa o di un processo tra computer mentre continua a essere usato. |
| Replica | La presenza di più copie della stessa risorsa e la scelta della copia utilizzata. |
| Concorrenza/transazione | Il coordinamento necessario quando più utenti o processi lavorano sugli stessi dati. |
| Malfunzionamenti | Parte del guasto e del recupero, per esempio un tentativo alternativo tramite una replica. La trasparenza completa dei guasti non è sempre possibile. |
| Persistenza | Dove e come una risorsa viene conservata nel tempo. |
| Scalabilità | I dettagli tecnici usati per far fronte alla crescita del carico. |
| Prestazioni | Le decisioni con cui si cerca di migliorare velocità ed efficienza. |

**Prestazioni** e **scalabilità** sono collegate ma diverse: le prestazioni riguardano velocità o efficienza nelle condizioni correnti; la scalabilità riguarda il mantenimento di un servizio adeguato quando il carico cresce.

<h2 style="color:#C00000"><strong>5. Middleware</strong></h2>

Il **middleware** è uno strato software tra le applicazioni e i servizi di sistema che fornisce funzioni comuni, soprattutto quando le componenti comunicano tra computer e piattaforme differenti.

Si può leggere attraverso tre ruoli:

| Ruolo | Funzioni |
|---|---|
| Infrastruttura | Nasconde differenze tra sistemi operativi e fornisce basi comuni per comunicazione e concorrenza. |
| Supporto alla distribuzione | Automatizza operazioni come marshalling, riuso dei canali, invocazioni remote e rilevamento/gestione di alcuni guasti. |
| Servizi comuni | Offre funzioni riutilizzabili come sicurezza, persistenza e gestione delle transazioni. |

**Marshalling** è la conversione di parametri e dati in una forma trasmissibile; sul destinatario i dati vengono ricostruiti (unmarshalling). Un canale, come un socket, può trasportare più richieste secondo un protocollo concordato.

Il middleware può supportare diverse modalità di invocazione, per esempio unicast o multicast. Alcuni sistemi storici prevedevano l’attivazione di oggetti su richiesta; la disponibilità concreta dipende dalla tecnologia.

Separare questi compiti consente al programmatore di concentrarsi sulla logica di business senza reimplementare per ogni applicazione tutto il trasporto e i servizi comuni.

<h3 style="color:#C00000"><strong>Middleware esplicito e implicito — Slide 32</strong></h3>

- **Esplicito:** il codice applicativo invoca direttamente le API di sicurezza, transazione, database o comunicazione. Offre controllo diretto, ma può mescolare infrastruttura e logica di business e legare il codice a API specifiche.
- **Implicito:** l’applicazione dichiara i servizi richiesti tramite configurazione o metadati; container o runtime li applicano automaticamente, per esempio attraverso interceptor.

Il middleware implicito può separare meglio i compiti, ma richiede configurazione coerente, strumenti adeguati e verifica che le regole dichiarate siano effettivamente applicate.

<h3 style="color:#C00000"><strong>Comunicazione e servizi espliciti — Slide 33–35</strong></h3>

Nel percorso di una chiamata remota, il client invoca l’interfaccia, lo stub prepara la richiesta, la rete la trasporta, lo skeleton la riceve e la inoltra all’oggetto distribuito. L’oggetto esegue il metodo e il risultato torna sul percorso inverso. Stub e skeleton nascondono dettagli di rete.

Il codice può poi invocare direttamente servizi comuni, per esempio:

- **Transaction Service** tramite una Transaction API;
- **Security Service** tramite una Security API;
- **Database Driver** tramite una Database API.

Nell’esempio di trasferimento tra conti, il metodo controlla l’autorizzazione, avvia la transazione, carica i dati, modifica i saldi, salva e conclude la transazione. La logica di business si mescola così con sicurezza, transazioni e database. Il codice diventa più complesso e dipende dalle API specifiche, perciò un cambiamento può richiedere modifiche all’applicazione e ai client.

<h3 style="color:#C00000"><strong>Middleware implicito: deployment e interceptor — Slide 36–37</strong></h3>

Nel modello desiderato, il metodo contiene la logica di business (per esempio sottrarre da un conto e aggiungere all’altro), mentre sicurezza, persistenza e transazione vengono dichiarate separatamente. Le slide citano un file XML chiamato **deployment descriptor**. Un tool o container legge la configurazione e prepara gli interventi necessari, come un **request interceptor**.

L’interceptor opera prima che la richiesta raggiunga l’oggetto: riceve la chiamata, applica i servizi dichiarati (per esempio controlli di sicurezza o transazione, con accesso al driver database quando serve) e inoltra poi la richiesta al componente. L’oggetto può concentrarsi sulla logica applicativa; configurazione e codice devono comunque restare coerenti.

<h2 style="color:#C00000"><strong>6. Da RPC agli oggetti distribuiti</strong></h2>

<h3 style="color:#C00000"><strong>RPC — Remote Procedure Call</strong></h3>

**RPC** permette a un programma su un computer di invocare una procedura che viene eseguita su un altro computer. La rete trasporta dati, non parametri Java/C# “così come sono”: parametri e risultati vengono codificati (marshalling) in una forma trasmissibile, per esempio byte.

Nel modello sincrono descritto, il client attende la risposta del server. Questo rende semplice seguire la sequenza della chiamata, ma introduce attesa e possibilità di errore dovute alla rete.

Gli **stub** nascondono parte del lavoro:

- lo stub client prepara e invia la richiesta;
- lo stub server riceve la richiesta e la consegna alla procedura corretta.

Questi componenti effettuano anche il marshalling e la ricostruzione dei parametri. La chiamata può apparire simile a una locale, ma non ha le stesse garanzie: la rete può ritardare o fallire.

<h3 style="color:#C00000"><strong>Middleware a oggetti distribuiti</strong></h3>

Il passo successivo integra le chiamate remote con la programmazione a oggetti: il client invoca un’operazione esposta da un oggetto o componente remoto.

Un **componente** è un modulo riutilizzabile che espone funzionalità attraverso un’interfaccia. L’interfaccia comunica cosa offre, mentre l’implementazione descrive come lo fa. Il middleware inoltra le chiamate e riporta i risultati; per interoperare, le parti devono comunque concordare formato e significato dei dati.

Gli obiettivi sono favorire riuso, interoperabilità, estendibilità e gestione. Questi vantaggi dipendono da interfacce compatibili e da una progettazione adeguata: il middleware non converte automaticamente qualsiasi formato o piattaforma incompatibile.

<h2 style="color:#C00000"><strong>7. Esempi di middleware e componenti</strong></h2>

<h3 style="color:#C00000"><strong>CORBA</strong></h3>

CORBA mira a far collaborare oggetti scritti in linguaggi diversi. È un esempio di middleware a oggetti distribuiti; la complessità e le difficoltà di interoperabilità sono fra i limiti discussi nelle slide.

<h3 style="color:#C00000"><strong>Java RMI</strong></h3>

**Java Remote Method Invocation** permette a un programma Java di invocare metodi di oggetti remoti. Il dettaglio di interfacce, stub, registry e serializzazione è raccolto in [Java RMI](</workspace/Programmazione-distribuita/tutti%20appunti/JAVA%20RMI.md>).

<h3 style="color:#C00000"><strong>.NET e .NET Remoting — Slide 29</strong></h3>

Il **.NET Framework** è la piattaforma di sviluppo ed esecuzione illustrata dalla slide; il **CLR** esegue programmi .NET e fornisce servizi comuni a linguaggi come C# e Visual Basic. **.NET Remoting** è una tecnologia storica per comunicare tra oggetti in processi o macchine diversi. La slide menziona anche la successiva **Windows Communication Foundation (WCF)**.

Sono tecnologie legacy rispetto agli strumenti moderni delle rispettive piattaforme: .NET Remoting non è il modello da scegliere per nuove applicazioni .NET moderne; WCF è anch’esso una tecnologia di generazioni precedenti, con supporto dipendente dalla versione. Gli esempi servono a capire l’evoluzione del middleware.

<h3 style="color:#C00000"><strong>Java Enterprise ed EJB — Slide 31</strong></h3>

Le applicazioni enterprise possono separare:

- **livello di presentazione**, che prepara ciò che vede l’utente;
- **livello di business**, che applica le regole dell’applicazione e comunica con i dati o con sistemi legacy.

Gli **Enterprise JavaBeans (EJB)**, oggi Jakarta Enterprise Beans, sono componenti enterprise ospitati da un **container/application server**. Il container può gestire servizi come sicurezza, transazioni e persistenza, riducendo la necessità di implementare manualmente ogni funzione infrastrutturale.

<h2 style="color:#C00000"><strong>8. Riepilogo e filo conduttore — Slide 30 e 38–40</strong></h2>

Le slide 30 e 38 presentano il piano della lezione; la 39 riassume il ruolo degli oggetti distribuiti (riuso, interoperabilità ed evoluzione architetturale); la 40 anticipa il passaggio dai socket, a basso livello, agli oggetti remoti.

L’evoluzione va dalla comunicazione a basso livello con i socket, a RPC, fino a oggetti e componenti distribuiti. Ogni passaggio introduce un’astrazione che nasconde parte dei dettagli tecnici. Il middleware centralizza comunicazione e servizi comuni; l’applicazione definisce le responsabilità dei componenti e le regole di business. Scalabilità e tolleranza ai guasti sono obiettivi progettuali, non proprietà automatiche degli oggetti remoti.
