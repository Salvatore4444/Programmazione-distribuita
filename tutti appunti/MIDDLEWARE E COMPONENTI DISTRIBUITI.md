<h1 style="color:#C00000"><strong>Middleware e componenti distribuiti — Appunti teorici</strong></h1>

Gli appunti riuniscono le slide 29–40: architettura .NET, componenti enterprise Java, middleware esplicito e implicito, oggetti distribuiti e conclusioni.

<h2 style="color:#C00000"><strong>1. Architettura .NET — Slide 29</strong></h2>

La slide distingue tre livelli:

- **.NET Framework:** piattaforma e librerie per sviluppare ed eseguire programmi, per esempio in C# e Visual Basic.
- **CLR (Common Language Runtime):** ambiente di esecuzione comune ai linguaggi .NET; esegue il codice e fornisce servizi a runtime.
- **.NET Remoting:** tecnologia storica per comunicare con oggetti che si trovano in processi o macchine differenti. Riprende idee da tecnologie precedenti come COM e DCOM.

Il CLR si occupa dell’esecuzione e dei servizi comuni; Remoting si occupava della comunicazione remota. **Precisazione:** .NET Remoting è una tecnologia legacy, non il modello da scegliere per nuovi sistemi basati su .NET moderno. La slide lo presenta come esempio storico di middleware a oggetti distribuiti.

<h2 style="color:#C00000"><strong>2. Piano della lezione — Slide 30</strong></h2>

La slide orienta il percorso: middleware a oggetti distribuiti e motivazioni; evoluzione da RPC a oggetti distribuiti; esempi come CORBA, .NET Remoting e Java RMI; conclusioni. L’evidenziazione indica che la lezione sta passando agli esempi concreti.

<h2 style="color:#C00000"><strong>3. Enterprise JavaBeans — Slide 31</strong></h2>

Gli **Enterprise JavaBeans (EJB)** sono componenti per costruire servizi aziendali distribuiti. Fanno parte di un modello a componenti: il programmatore implementa la logica del servizio e un ambiente server, detto **container** o **application server**, ospita e gestisce i componenti.

Il container può fornire servizi infrastrutturali come:

- sicurezza;
- gestione delle transazioni;
- persistenza e accesso ai dati;
- gestione del ciclo di vita dei componenti.

Gli obiettivi presentati dalla slide sono:

- **riuso:** componenti modulari possono essere impiegati in applicazioni diverse;
- **separazione:** la presentazione (ciò che viene mostrato all’utente) resta distinta dalla logica di business (le regole dell’applicazione);
- **trasparenze del sistema:** locazione, accesso e migrazione mirano a ridurre la dipendenza dai dettagli di distribuzione;
- **deployment modulare:** i componenti possono essere distribuiti e combinati senza incorporare tutta l’infrastruttura nell’applicazione.

La slide introduce inoltre il middleware **implicito**: si dichiarano i servizi richiesti e il container li applica, evitando di inserire ogni chiamata infrastrutturale nel codice di business. Oggi la tecnologia si è evoluta da Java EE a Jakarta EE; EJB è noto anche come Jakarta Enterprise Beans.

<h2 style="color:#C00000"><strong>4. Middleware esplicito e implicito — Slide 32</strong></h2>

Il **middleware** fornisce servizi infrastrutturali a un’applicazione, per esempio comunicazione remota, sicurezza, transazioni e accesso al database.

<h3 style="color:#C00000"><strong>Middleware esplicito</strong></h3>

Il codice dell’applicazione chiama direttamente le API dei servizi middleware. Per esempio, il metodo richiama API distinte per controllare i permessi, avviare una transazione e accedere al database.

Secondo le slide, questo approccio può portare a:

- codice più complesso, perché la logica applicativa contiene anche chiamate infrastrutturali;
- gestione degli errori più difficile, con controlli e pulizia delle risorse distribuiti nel codice;
- minore portabilità, perché il programma dipende da API specifiche del middleware scelto.

<h3 style="color:#C00000"><strong>Middleware implicito</strong></h3>

Il codice dichiara quali servizi vuole usare; la configurazione o il deployment specifica i dettagli e il container/runtime li applica automaticamente quando la richiesta attraversa il sistema.

**Differenza essenziale:** nel modello esplicito il codice chiama direttamente i servizi; in quello implicito dichiara il comportamento desiderato e lascia che il middleware lo applichi.

<h2 style="color:#C00000"><strong>5. Comunicazione remota esplicita — Slide 33</strong></h2>

Il diagramma mostra la richiesta fra un client e un oggetto distribuito, come nel modello di Java RMI, CORBA o DCOM:

1. il client invoca un metodo attraverso l’interfaccia remota;
2. lo **stub** sul client rappresenta localmente il servizio remoto e prepara la richiesta;
3. la richiesta attraversa la rete;
4. lo **skeleton** sul server riceve i dati e li inoltra all’oggetto corretto;
5. l’oggetto distribuito esegue il metodo;
6. il risultato torna al client sul percorso inverso.

Stub e skeleton nascondono dettagli del trasporto, ma il middleware può comunque essere esplicito se il codice deve richiamare direttamente le API dei servizi infrastrutturali.

<h2 style="color:#C00000"><strong>6. Accesso esplicito a transazioni, sicurezza e database — Slide 34</strong></h2>

Oltre alla chiamata remota, l’oggetto può chiamare direttamente altri servizi:

- un **Transaction Service** tramite una Transaction API;
- un **Security Service** tramite una Security API;
- un **Database Driver** tramite una Database API.

Il codice che usa queste API ottiene controllo esplicito, ma conosce i servizi e le interfacce specifiche da cui dipende.

<h2 style="color:#C00000"><strong>7. Esempio: trasferimento tra conti — Slide 35</strong></h2>

La funzione di trasferimento deve sottrarre l’importo dal conto di origine e aggiungerlo al conto di destinazione. Nell’esempio, il codice contiene anche operazioni infrastrutturali:

1. controllare l’autorizzazione;
2. avviare una transazione;
3. caricare i dati dei conti dal database;
4. aggiornare i saldi;
5. salvare i dati;
6. concludere la transazione.

Il problema illustrato è la **mescolanza tra logica di business e gestione del middleware**. Il metodo deve descrivere il trasferimento ma anche occuparsi di sicurezza, transazioni e persistenza. Questo aumenta la complessità e lega il programma alle API impiegate; un cambiamento delle API può richiedere modifiche al programma e ai suoi client.

<h2 style="color:#C00000"><strong>8. Il modello desiderato: middleware implicito — Slide 36</strong></h2>

L’obiettivo è lasciare nel metodo solo la logica di business, per esempio diminuire il saldo di un conto e aumentare quello dell’altro.

I servizi richiesti — come sicurezza, transazione e persistenza — si dichiarano separatamente, per esempio nella configurazione di deployment. Le slide citano un file XML chiamato **deployment descriptor**. Il container o uno strumento del middleware legge la configurazione e prepara gli interventi necessari, per esempio un **request interceptor**.

Il vantaggio è separare il codice applicativo dai dettagli infrastrutturali. La configurazione deve comunque essere corretta e coerente con il codice.

<h2 style="color:#C00000"><strong>9. Request interceptor — Slide 37</strong></h2>

Il diagramma mostra un intermediario che intercetta le richieste prima che arrivino all’oggetto distribuito:

1. la chiamata passa dal client attraverso stub, rete e skeleton;
2. il **request interceptor** riceve la richiesta prima dell’oggetto;
3. applica i servizi dichiarati, come sicurezza o transazione, ed eventualmente interagisce con il driver del database;
4. poi inoltra la richiesta all’oggetto, che svolge la logica di business.

L’interceptor è un punto in cui applicare automaticamente comportamenti trasversali, senza duplicarli in ogni oggetto. Il meccanismo concreto dipende dalla tecnologia middleware utilizzata.

<h2 style="color:#C00000"><strong>10. Conclusioni e ruolo degli oggetti distribuiti — Slide 38–40</strong></h2>

Le slide 38 e 40 sono riepiloghi del piano della lezione; la slide 39 riassume il ruolo degli oggetti distribuiti.

- I **componenti riutilizzabili** permettono di combinare moduli per costruire funzionalità più complesse.
- L’**interoperabilità** permette a componenti diversi di collaborare in ambienti eterogenei.
- L’architettura è passata da meccanismi di basso livello, come i socket, a RPC e oggetti remoti, che offrono astrazioni più vicine al modello di programmazione a oggetti.
- Tecnologie come Java RMI e .NET Remoting forniscono o fornivano meccanismi per collegare oggetti remoti.

Scalabilità, tolleranza ai malfunzionamenti, estensibilità e facilità di gestione sono **obiettivi progettuali** che possono essere supportati da un’architettura distribuita; non sono garantiti automaticamente dall’uso degli oggetti remoti.

La lezione si chiude anticipando un percorso dai socket, che espongono dettagli di comunicazione di basso livello, agli oggetti remoti, che nascondono parte di tali dettagli.

<h2 style="color:#C00000"><strong>Filo conduttore</strong></h2>

Il middleware esplicito richiede al codice di invocare direttamente i servizi. Il middleware implicito permette di dichiararli e lascia che il container o runtime li applichi. L’obiettivo è separare meglio la logica di business dalle funzioni trasversali, facilitando riuso e manutenzione senza eliminare la necessità di configurare e verificare correttamente il sistema.
