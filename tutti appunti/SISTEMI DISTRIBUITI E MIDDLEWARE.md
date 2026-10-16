<h1 style="color:#C00000"><strong>SISTEMI DISTRIBUITI E MIDDLEWARE — APPUNTI TEORICI</strong></h1>

Questi appunti raccolgono il modello dei sistemi distribuiti, i requisiti di qualità, le trasparenze e l’evoluzione dal middleware RPC agli oggetti e componenti distribuiti, compresi gli esempi .NET ed Enterprise Java.

<h2 style="color:#C00000"><strong>1. MODELLO DI RIFERIMENTO E RM-ODP</strong></h2>

Un **sistema distribuito** è un insieme di computer o nodi autonomi, collegati da una rete, che collaborano per offrire servizi. I nodi mantengono una propria autonomia e comunicano tramite protocolli e, spesso, un livello middleware.

Un **modello di riferimento** è un modello concettuale, indipendente da una tecnologia specifica, che aiuta a descrivere e progettare un sistema.

**RM-ODP** (*Reference Model for Open Distributed Processing*) è un modello di riferimento per l’elaborazione distribuita aperta. Descrive il sistema da punti di vista diversi e aiuta a separare i problemi. Il testo che hai inviato lo collega a OSI tramite un layer **APPLICATION**, relativo alla comunicazione fra applicazioni. Questa è la formulazione del tuo testo; senza la slide originale non si può attribuirle con certezza un significato più preciso. Precisazione formale: RM-ODP non è soltanto un ulteriore livello dello stack OSI, ma un modello architetturale distinto e complementare. Conserva quindi il collegamento annotato nel corso, distinguendolo dalla definizione formale del modello.

<h2 style="color:#C00000"><strong>2. PERCHÉ SI USANO I SISTEMI DISTRIBUITI</strong></h2>

<h3 style="color:#C00000"><strong>MOTIVAZIONI ECONOMICHE</strong></h3>

- **Integrare sistemi aziendali:** organizzazioni diverse possono collegare infrastrutture e applicazioni già esistenti.
- **Downsizing:** spostare funzioni da sistemi centralizzati e costosi verso più sistemi distribuiti, spesso meno costosi e più facili da espandere.
- **Ridurre il time-to-market:** riusare componenti e integrare servizi esistenti può accelerare la realizzazione e il rilascio di un prodotto. Il riuso richiede però interfacce compatibili e integrazione ben progettata.

<h3 style="color:#C00000"><strong>MOTIVAZIONI TECNOLOGICHE</strong></h3>

- **Riuso ed evoluzione:** adottare nuove tecnologie senza ricominciare ogni volta da zero.
- **Integrazione di eterogeneità:** far collaborare hardware, sistemi operativi, linguaggi e servizi differenti.
- **Condivisione delle risorse e crescita:** aggiungere nodi o servizi per distribuire il lavoro.
- **Gestione di latenza e malfunzionamenti:** progettare la comunicazione considerando che la rete introduce ritardi e guasti parziali; il sistema distribuito non li elimina.

<h3 style="color:#C00000"><strong>LEGGI EMPIRICHE CITATE NEGLI APPUNTI</strong></h3>

Sono osservazioni storiche usate per descrivere la crescita delle risorse tecnologiche, non leggi fisiche che garantiscono una scadenza esatta:

- **Legge di Moore — formulazione dei tuoi appunti:** numero/densità dei transistor circa raddoppiati ogni 24 mesi. È un andamento storico: non implica automaticamente che ogni programma diventi due volte più veloce.
- **Legge di Gilder:** la capacità di trasmissione delle reti cresce molto rapidamente; negli appunti è riportato un raddoppio circa annuale.
- **Legge di “Shugard” — nome riportato nei tuoi appunti:** capacità di memorizzazione circa decuplicata ogni quattro anni. Mantengo nome e valore come nella fonte; l’attribuzione non è verificabile da questi soli appunti.

Queste regole aiutano a motivare l’evoluzione dei sistemi distribuiti: capacità di calcolo, rete e memoria crescono, ma cresce anche la necessità di collegare e coordinare risorse diverse.

<h2 style="color:#C00000"><strong>3. LEGGI SUL VALORE DELLE RETI</strong></h2>

Queste leggi descrivono in modo approssimativo come può crescere l’utilità di una rete al crescere degli utenti. Indichiamo con `n` e `m` gli utenti attivi di due gruppi che vengono collegati.

- **Sarnoff — crescita lineare:** il valore è proporzionale al numero di utenti. Unire i due gruppi porta da `n` e `m` utenti a circa `n + m` utenti/valore.
- **Metcalfe — crescita quadratica:** il valore è proporzionale al numero di possibili connessioni a coppie, approssimativamente `N²`. Dopo l’unione il valore è circa `(n + m)²`; l’aumento del modello `N²` è `2nm`. Se conti precisamente le coppie non orientate, il totale è `N(N−1)/2` e le nuove coppie tra i due gruppi sono `nm`, non `2nm`.
- **Reed — crescita esponenziale:** nelle reti che permettono di formare molti gruppi o sottoreti, il numero di gruppi possibili può crescere come `2^N`. Nei tuoi appunti, per l’unione di gruppi di dimensione `n` e `m`, la formula è `2^n · 2^m − (2^n + 2^m) = 2^(n+m) − 2^n − 2^m`.

Se vuoi contare esattamente i gruppi che includono almeno un utente di entrambe le reti, il numero è `(2^n − 1)(2^m − 1) = 2^(n+m) − 2^n − 2^m + 1`: il `+1` corregge il doppio conteggio del gruppo vuoto nella sottrazione. La formula originaria sopra è conservata come approssimazione riportata negli appunti.

Sono modelli intuitivi e semplificati: il valore reale dipende da utenti attivi, qualità dei collegamenti, utilità dei servizi e possibilità concreta di interagire. Non sono formule che predicono da sole il valore economico di qualsiasi rete.

<h2 style="color:#C00000"><strong>4. CARATTERISTICHE DEI SISTEMI DISTRIBUITI</strong></h2>

- **Remoti:** le componenti possono trovarsi su macchine fisicamente diverse e comunicare tramite rete.
- **Concorrenti:** più attività procedono nello stesso periodo su nodi diversi o nella stessa macchina. Senza coordinamento non si può presumere un ordine unico delle operazioni.
- **Senza stato globale immediatamente disponibile:** non esiste necessariamente un unico osservatore che conosca in ogni istante lo stato aggiornato di tutti i nodi. La rete introduce ritardi e informazioni che arrivano in momenti diversi.
- **Soggetti a malfunzionamenti parziali:** un nodo o collegamento può guastarsi mentre altri componenti continuano a funzionare.
- **Eterogenei e autonomi:** possono includere hardware, sistemi operativi, linguaggi e servizi differenti, gestiti da amministratori diversi.
- **In evoluzione:** componenti e tecnologie possono essere sostituite o aggiornate nel tempo.
- **Mobili:** nodi, processi, servizi o dati possono essere spostati tra macchine o luoghi diversi.

<h2 style="color:#C00000"><strong>5. REQUISITI NON FUNZIONALI</strong></h2>

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

<h2 style="color:#C00000"><strong>6. TRASPARENZA DELLA DISTRIBUZIONE</strong></h2>

La **trasparenza** nasconde alcuni dettagli della distribuzione agli utenti o al programmatore, così da fornire un’interfaccia più uniforme. Non significa che ogni dettaglio sia sempre invisibile: ritardi e guasti di rete possono rendere evidente la natura distribuita.

| Tipo di trasparenza | Che cosa nasconde |
|---|---|
| Accesso | Differenze di rappresentazione e uso di una risorsa locale o remota. |
| Locazione | La posizione fisica della risorsa. |
| Migrazione | Lo spostamento di una risorsa o di un processo tra computer mentre continua a essere usato. |
| Replica | La presenza di più copie della stessa risorsa e la scelta della copia utilizzata. |
| Transazioni (etichetta presente nei tuoi appunti) | Il sistema applica il coordinamento e le proprietà della transazione, così che più modifiche costituiscano un’unità coerente. |
| Concorrenza (distinzione aggiunta) | Accessi contemporanei alla stessa risorsa vengono coordinati senza affidare ogni dettaglio al client. |
| Malfunzionamenti | Parte del guasto e del recupero, per esempio un tentativo alternativo tramite una replica. La trasparenza completa dei guasti non è sempre possibile. |
| Persistenza | Dove e come una risorsa viene conservata nel tempo. |
| Scalabilità | I dettagli tecnici usati per far fronte alla crescita del carico. |
| Prestazioni | Le decisioni con cui si cerca di migliorare velocità ed efficienza. |

Nel testo originale la riga “Transazioni” descrive soprattutto accessi concorrenti: i due temi sono collegati, ma non equivalenti. La concorrenza riguarda l’interferenza tra attività; una transazione aggiunge un’unità di lavoro e regole come commit/rollback.

**Prestazioni** e **scalabilità** sono collegate ma diverse: le prestazioni riguardano velocità o efficienza nelle condizioni correnti; la scalabilità riguarda il mantenimento di un servizio adeguato quando il carico cresce.

<h2 style="color:#C00000"><strong>7. MIDDLEWARE</strong></h2>

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

<h3 style="color:#C00000"><strong>MIDDLEWARE ESPLICITO E IMPLICITO — SLIDE 32</strong></h3>

- **Esplicito:** il codice applicativo invoca direttamente le API di sicurezza, transazione, database o comunicazione. Offre controllo diretto, ma può mescolare infrastruttura e logica di business e legare il codice a API specifiche.
- **Implicito:** l’applicazione dichiara i servizi richiesti tramite configurazione o metadati; container o runtime li applicano automaticamente, per esempio attraverso interceptor.

Il middleware implicito può separare meglio i compiti, ma richiede configurazione coerente, strumenti adeguati e verifica che le regole dichiarate siano effettivamente applicate.

<h3 style="color:#C00000"><strong>COMUNICAZIONE E SERVIZI ESPLICITI — SLIDE 33–35</strong></h3>

Nel percorso di una chiamata remota, il client invoca l’interfaccia, lo stub prepara la richiesta, la rete la trasporta, lo skeleton la riceve e la inoltra all’oggetto distribuito. L’oggetto esegue il metodo e il risultato torna sul percorso inverso. Stub e skeleton nascondono dettagli di rete.

Il codice può poi invocare direttamente servizi comuni, per esempio:

- **Transaction Service** tramite una Transaction API;
- **Security Service** tramite una Security API;
- **Database Driver** tramite una Database API.

Nell’esempio di trasferimento tra conti, il metodo controlla l’autorizzazione, avvia la transazione, carica i dati, modifica i saldi, salva e conclude la transazione. La logica di business si mescola così con sicurezza, transazioni e database. Il codice diventa più complesso e dipende dalle API specifiche, perciò un cambiamento può richiedere modifiche all’applicazione e ai client.

<h3 style="color:#C00000"><strong>MIDDLEWARE IMPLICITO: DEPLOYMENT E INTERCEPTOR — SLIDE 36–37</strong></h3>

Nel modello desiderato, il metodo contiene la logica di business (per esempio sottrarre da un conto e aggiungere all’altro), mentre sicurezza, persistenza e transazione vengono dichiarate separatamente. Le slide citano un file XML chiamato **deployment descriptor**. Un tool o container legge la configurazione e prepara gli interventi necessari, come un **request interceptor**.

L’interceptor opera prima che la richiesta raggiunga l’oggetto: riceve la chiamata, applica i servizi dichiarati (per esempio controlli di sicurezza o transazione, con accesso al driver database quando serve) e inoltra poi la richiesta al componente. L’oggetto può concentrarsi sulla logica applicativa; configurazione e codice devono comunque restare coerenti.

<h2 style="color:#C00000"><strong>8. DA RPC AGLI OGGETTI DISTRIBUITI</strong></h2>

<h3 style="color:#C00000"><strong>RPC — REMOTE PROCEDURE CALL</strong></h3>

**RPC** permette a un programma su un computer di invocare una procedura che viene eseguita su un altro computer. La rete trasporta dati, non parametri Java/C# “così come sono”: parametri e risultati vengono codificati (marshalling) in una forma trasmissibile, per esempio byte.

Nel modello sincrono descritto, il client attende la risposta del server. Questo rende semplice seguire la sequenza della chiamata, ma introduce attesa e possibilità di errore dovute alla rete.

Gli **stub** nascondono parte del lavoro:

- lo stub client prepara e invia la richiesta;
- lo stub server riceve la richiesta e la consegna alla procedura corretta.

Questi componenti effettuano anche il marshalling e la ricostruzione dei parametri. La chiamata può apparire simile a una locale, ma non ha le stesse garanzie: la rete può ritardare o fallire.

<h3 style="color:#C00000"><strong>MIDDLEWARE A OGGETTI DISTRIBUITI</strong></h3>

Il passo successivo integra le chiamate remote con la programmazione a oggetti: il client invoca un’operazione esposta da un oggetto o componente remoto.

Un **componente** è un modulo riutilizzabile che espone funzionalità attraverso un’interfaccia. L’interfaccia comunica cosa offre, mentre l’implementazione descrive come lo fa. Il middleware inoltra le chiamate e riporta i risultati; per interoperare, le parti devono comunque concordare formato e significato dei dati.

Gli obiettivi sono favorire riuso, interoperabilità, estendibilità e gestione. Questi vantaggi dipendono da interfacce compatibili e da una progettazione adeguata: il middleware non converte automaticamente qualsiasi formato o piattaforma incompatibile.

<h2 style="color:#C00000"><strong>9. ESEMPI DI MIDDLEWARE E COMPONENTI</strong></h2>

<h3 style="color:#C00000"><strong>CORBA</strong></h3>

CORBA mira a far collaborare oggetti scritti in linguaggi diversi. È un esempio di middleware a oggetti distribuiti; la complessità e le difficoltà di interoperabilità sono fra i limiti discussi nelle slide.

<h3 style="color:#C00000"><strong>JAVA RMI</strong></h3>

**Java Remote Method Invocation** permette a un programma Java di invocare metodi di oggetti remoti. Il dettaglio di interfacce, stub, registry e serializzazione è raccolto in [Java RMI](JAVA%20RMI.md).

<h3 style="color:#C00000"><strong>.NET E .NET REMOTING — SLIDE 29</strong></h3>

Il **.NET Framework** è la piattaforma di sviluppo ed esecuzione illustrata dalla slide; il **CLR** esegue programmi .NET e fornisce servizi comuni a linguaggi come C# e Visual Basic. **.NET Remoting** è una tecnologia storica per comunicare tra oggetti in processi o macchine diversi. La slide menziona anche la successiva **Windows Communication Foundation (WCF)**.

La slide 29 collega storicamente questi meccanismi anche a **COM e DCOM**: vanno riconosciuti come predecessori nel percorso della lezione.

Sono tecnologie legacy rispetto agli strumenti moderni delle rispettive piattaforme: .NET Remoting non è il modello da scegliere per nuove applicazioni .NET moderne; WCF è anch’esso una tecnologia di generazioni precedenti, con supporto dipendente dalla versione. Gli esempi servono a capire l’evoluzione del middleware.

<h3 style="color:#C00000"><strong>JAVA ENTERPRISE ED EJB — SLIDE 31</strong></h3>

Le applicazioni enterprise possono separare:

- **livello di presentazione**, che prepara ciò che vede l’utente;
- **livello di business**, che applica le regole dell’applicazione e comunica con i dati o con sistemi legacy.

Gli **Enterprise JavaBeans (EJB)**, oggi Jakarta Enterprise Beans, sono componenti enterprise ospitati da un **container**, tipicamente all’interno di un application server. La slide 31 sottolinea sei punti:

1. **Riuso e minore complessità:** combinare componenti già realizzati.
2. **Presentazione separata dal business:** sviluppare e scalare le due responsabilità in modo diverso.
3. **Trasparenze di accesso, locazione e migrazione:** ridurre i dettagli distributivi gestiti dall’applicazione, nei limiti del sistema impiegato.
4. **Modello a componenti:** moduli riutilizzabili, combinabili e distribuibili anche in forma binaria.
5. **Container:** ambiente che gestisce componenti e servizi come sicurezza e transazioni; la persistenza dipende dal modello/versione e dall’integrazione adottata.
6. **Middleware implicito:** dichiarare servizi e politiche lasciando che il container li applichi.

Nel testo del corso Java RMI è indicato come supporto alle invocazioni dei componenti Java distribuiti, inclusi gli EJB esposti da remoto. Il contratto può però prevedere anche una vista locale: non ogni chiamata a un componente EJB attraversa necessariamente la rete.


<h2 style="color:#C00000"><strong>10. RIEPILOGO E FILO CONDUTTORE — SLIDE 30 E 38–40</strong></h2>

Il piano esplicito è: **introduzione e motivazioni → evoluzione da RPC agli oggetti → esempi CORBA/.NET Remoting/Java RMI → conclusioni**. La slide 30 evidenzia gli esempi, la 38 le conclusioni; la 39 riassume il ruolo degli oggetti distribuiti (riuso, interoperabilità ed evoluzione architetturale); la 40 anticipa il passaggio dai socket, a basso livello, agli oggetti remoti.

L’evoluzione va dalla comunicazione a basso livello con i socket, a RPC, fino a oggetti e componenti distribuiti. Ogni passaggio introduce un’astrazione che nasconde parte dei dettagli tecnici. Il middleware centralizza comunicazione e servizi comuni; l’applicazione definisce le responsabilità dei componenti e le regole di business. Scalabilità e tolleranza ai guasti sono obiettivi progettuali, non proprietà automatiche degli oggetti remoti.


<h2 style="color:#C00000"><strong>CONCETTI CHIAVE DA SAPER COLLEGARE ALL’ESAME</strong></h2>

- Un sistema distribuito è fatto di nodi autonomi collegati in rete; i guasti possono essere parziali e non esiste necessariamente una vista globale istantanea.
- Distingui caratteristiche del sistema, requisiti non funzionali e trasparenze: le prime descrivono il contesto, i requisiti descrivono le qualità desiderate, le trasparenze nascondono specifici dettagli distributivi.
- Le motivazioni economiche includono integrazione, downsizing e time-to-market; quelle tecnologiche includono riuso, eterogeneità e crescita delle risorse.
- Sarnoff è lineare, Metcalfe quadratica per collegamenti tra coppie, Reed esponenziale per gruppi possibili. Sono modelli del valore di rete, non garanzie universali.
- Il middleware è l’intermediario che offre infrastruttura, supporto alla distribuzione e servizi comuni.
- RPC invoca procedure remote; oggetti distribuiti espongono operazioni tramite interfacce; i componenti sono moduli riusabili che collaborano attraverso contratti.
- Middleware esplicito significa chiamare API dal codice; middleware implicito significa dichiarare servizi e lasciare che container/interceptor li applichino.
- Le tecnologie e le trasparenze riducono dettagli da gestire, ma non eliminano latenza, guasti, incompatibilità o necessità di configurazione.
