# Verifica degli appunti rispetto alle fonti

Verifica del 16 ottobre 2026. Confrontati i **13 file già pubblicati**, i **cinque testi allegati** e l’ultimo messaggio con definizione, motivazioni e leggi delle reti. Il branch remoto `work`, prima delle correzioni, era allineato al commit `d708caa`.

## Esito

I quattro macroargomenti erano presenti: Java RMI, Socket, Thread, sistemi distribuiti e middleware. Non sono emersi interi capitoli del materiale ricevuto eliminati. La sintesi aveva però sacrificato **esempi di codice e passaggi esplicativi utili per studiare**, soprattutto nei Socket e nelle relazioni happens-before. Alcune aggiunte avevano inoltre introdotto ambiguità tecniche.

Sono stati reinseriti gli esempi e i dettagli elencati sotto e corretti codice, formule e collegamenti. Non è stata usata una percentuale di parole conservate: ripetizioni, refusi e frasi equivalenti renderebbero quel numero poco significativo. Il confronto riguarda i concetti, gli esempi e il loro significato.

## Fonti confrontate

Tutti gli allegati si chiamavano `Testo incollato.txt`; sono distinti qui per contenuto e identificatore dell’upload.

| Fonte | Upload | Dimensione | Destinazione |
|---|---|---:|---|
| Java RMI | `file_00000000cc6881f495df6b563d604347` | 24.994 byte | [Teoria](tutti%20appunti/JAVA%20RMI.md), [pratica](tutti%20appunti%20pratici/JAVA%20RMI.md) |
| Socket | `file_000000002ab88210a8a8d1a209147ddc` | 8.788 byte | [Teoria](tutti%20appunti/SOCKET.md), [pratica](tutti%20appunti%20pratici/SOCKET.md) |
| Thread e sincronizzazione | `file_00000000bf688210b0f65e27f2739029` | 26.159 byte | [Teoria](tutti%20appunti/THREAD.md), [pratica](tutti%20appunti%20pratici/THREAD.md) |
| .NET, EJB e middleware: slide 29–40 | `file_00000000342081f49863b01d51bb97b0` | 8.572 byte | [Teoria](tutti%20appunti/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md), [pratica](tutti%20appunti%20pratici/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md) |
| Modello, caratteristiche, requisiti, trasparenze e RPC | `file_000000000850820a811feb9ce0c49cde` | 15.958 byte | Stesso macroargomento: sistemi distribuiti e middleware |
| Definizione, motivazioni economiche/tecnologiche, Moore/Gilder/Shugard, Sarnoff/Metcalfe/Reed | Messaggio scritto direttamente in chat | — | Stesso macroargomento: sistemi distribuiti e middleware |

## Confronto per argomento

| Fonte e parte | Riscontro e intervento |
|---|---|
| RMI: definizione, client/server, JVM e spazio di indirizzamento | Presenti. Esplicitati i tipi di riferimento nei frammenti. |
| RMI: trasparenza, integrazione, DGC, memory leak, complessità e sandbox | Presenti. Ripristinato anche l’esempio delle connessioni non autorizzate. Le limitazioni del DGC e la natura storica della sandbox sono conservate. |
| RMI: unicast, multicast, attivazione, persistenza e trasporto | Presenti con distinzione tra modello delle slide e funzioni disponibili nel RMI corrente. |
| RMI: cinque package | Tutti presenti; `java.rmi.activation` identificato come rimosso dal JDK 17. |
| RMI: `Remote`, `RemoteException`, contratto e metodi pubblici | Presenti. Reinserito l’esempio di parametro remoto `collega(Calcolatrice altra)` in un’interfaccia coerente. Conservata la distinzione per `stampaDiagnostica()`. |
| RMI: le due implementazioni/esportazioni | Presenti ma ambigue: l’esportazione esplicita riutilizzava il nome della classe già esportata dal costruttore. Ora le alternative sono `CalcolatriceImpl` e `CalcolatriceSemplice`, con codice distinto. |
| RMI: riferimento già noto e ricerca nel registry | Reinserito il frammento senza `lookup`; chiarite singolarmente `bind`, `rebind`, `lookup`, `unbind`, `list`. |
| RMI: copia degli oggetti, riferimenti remoti, integrità referenziale e metodi di `Object` | Presenti. Corretto il frammento che invocava un setter non dichiarato dalla `Calcolatrice` e chiarita la differenza tra porre un riferimento a `null` e modificare un oggetto remoto. |
| RMI: tre livelli, percorso, marshalling e sicurezza | Presenti. Reso esplicito il livello di riferimento remoto anche sul lato server; ampliati caricamento dinamico e ruolo del codebase. |
| RMI: `replaceObject`, abilitazione della sostituzione, annotazioni delle classi | Presenti, con distinzione tra oggetto esportato e non esportato. Precisato `enableReplaceObject(true)` e il recupero dello stub. |
| RMI: creazione, compilazione, registry, client, policy e Adapter | Presenti. Mantenuta la procedura storica con stub statici separata dagli esempi moderni. |
| Socket: endpoint, IP/porta, TCP/UDP, `ServerSocket`, `Socket`, `accept` | Presenti. Conservata la correzione di “TCP rischio zero”: affidabilità del trasporto non elimina guasti. |
| Socket: input/output, wrapping, stream a oggetti, header e `flush` | Presenti. Uniformato l’ordine output → flush → input anche nell’esempio teorico. `flush` non equivale ad avvenuta elaborazione remota. |
| Socket: stub, skeleton e contratto `Impiegato` | Il ruolo era spiegato, ma mancava il codice dell’interfaccia. Ripristinato il contratto originale con tutti e quattro i metodi e la nota su `throws Throwable`. |
| Socket: `aumentaStipendio(1000)` e percorso di risposta | Esempio concreto omesso: ripristinata la traccia 30000 → 31000. Chiarito che creare uno stub con `new` non rende locale il lavoro eseguito dal server. |
| Socket: protocollo e chiusura | Presenti. Il precedente frammento server è ora un programma completo, affiancato dal client corrispondente. |
| Thread: core, cache, memoria, multitasking, processi e concorrenza | Presenti. Precisato che un page fault non comporta necessariamente accesso al disco. |
| Thread: due modalità di creazione | La sottoclasse di `Thread` era solo richiamata: aggiunto l’esempio `Lavoratore extends Thread`. Distinto `Runnable` da `Callable`. |
| Thread: `run/start/sleep`, `interrupt`, `join`, `isAlive`, `MessageLoop` | Presenti. Ripristinati i messaggi dell’esempio e precisato il caso `join(0)`. |
| Thread: stati, `yield`, `wait/notify`, parking | Presenti i nuclei. Reinseriti `parkNanos`, `parkUntil` e `unpark`; conservata la distinzione tra risveglio, acquisizione del lock ed esecuzione. |
| Thread: race condition, visibilità e happens-before | Le regole erano presenti; mancavano gli esempi originali con valore 42 prima di `start()` e risultato dopo `join()`. Entrambi reinseriti. |
| Thread: `volatile` come pronto/arresto, limite su `++` | Il primo esempio era troppo schematico e poteva sembrare una variabile locale. Ora i campi sono in una classe; ripristinato il flag di arresto e chiarito che il consumatore può controllare prima del produttore. |
| Thread: monitor, `this` escape, grana fine, metodi statici e atomiche | Presenti. Reinserita l’equivalenza esplicita fra `static synchronized` e `synchronized(NomeClasse.class)`. |
| Thread: deadlock, starvation, livelock e Amdahl | Presenti, con formula ed esempio 90% / 4 processori / circa 3,08. |
| Thread: Singleton lazy, sincronizzato, DCL e holder | Presenti. Ripristinati il metodo sincronizzato, il ruolo dei campi, la sequenza della race e i passaggi del lazy holder. |
| Modello: RM-ODP e caratteristiche dei sistemi distribuiti | Presenti. Separata la formulazione OSI/APPLICATION contenuta nel testo dalla definizione formale di RM-ODP; non è stata attribuita una slide non disponibile. |
| Requisiti non funzionali | Tutte le dieci categorie presenti: apertura, integrazione, flessibilità, modularità, federazione, gestibilità, QoS, scalabilità, sicurezza, trasparenza. |
| Trasparenze | Tutte le etichette originarie conservate. “Transazioni” e concorrenza, prima accorpate, sono ora distinte e spiegate. |
| Middleware: tre ruoli, marshalling, riuso dei canali e guasti | Presenti. Conservate le dipendenze dalla tecnologia e i limiti della gestione automatica dei guasti. |
| RPC e passaggio agli oggetti/componenti | Presenti: sincronismo del modello illustrato, stub, parametri, interfacce, interoperabilità, riuso ed evoluzione. |
| .NET/EJB: slide 29 e 31 | Alcuni dettagli erano stati condensati: ripristinati COM/DCOM, le trasparenze indicate, la distribuzione binaria dei componenti e i sei punti sugli EJB. |
| Middleware esplicito/implicito: slide 32–37 | Presenti: API comuni, trasferimento tra conti, separazione del business, XML/deployment descriptor e request interceptor. |
| Indici/conclusioni: slide 30 e 38–40 | Presenti; ripristinato il percorso della lezione e ciò che cambia nell’evidenziazione delle slide di orientamento. |
| Aggiunte in chat: motivazioni e leggi | Tutte presenti. Mantenuti valori e nome “Shugard” del testo, senza trasformare l’attribuzione in un fatto verificato. Distinti conteggio esatto e approssimazioni per Metcalfe e Reed. |

## Correzioni tecniche e dei file

- I link che iniziavano con un percorso `/workspace/...` sono stati convertiti in link relativi al repository: i percorsi locali non funzionavano per chi leggeva su GitHub.
- I sottotitoli rimasti in Markdown semplice sono stati uniformati agli altri titoli.
- Le formule di Amdahl usano delimitatori supportati dal rendering matematico di GitHub.
- Gli esempi completi e i frammenti che richiedono un metodo/classe circostante sono distinti. La coerenza di nomi, firme e import è stata controllata.
- Le note moderne (`Callable`, executor, UTF-8, codice completo di avvio) sono aggiunte didattiche; non sostituiscono i contenuti del corso né introducono nuove domande attribuite all’utente.
- **Colore dei titoli:** GitHub filtra gli stili CSS nell’HTML dei Markdown. Il grassetto resta; il rosso è visibile solo nei lettori che accettano lo stile. La precedente formattazione non poteva garantirlo nella pagina GitHub.

## Verifiche effettuate

- Analizzati con un parser Markdown tutti i **15 file finali**: nessun link locale inesistente fra i **37 collegamenti** controllati, nessuna code fence o intestazione HTML sbilanciata.
- Controllati gli esempi Java con **OpenJDK 21.0.12.1**: 17 gruppi di compilazione dei documenti, più i wrapper per provare le due alternative di esportazione e lo scambio di oggetti. I frammenti non autonomi sono stati compilati nel contesto dichiarato o identificati esplicitamente come schemi.
- Eseguiti client e server RMI degli appunti teorici e pratici: `somma(2, 3)` restituisce `5` in entrambi i casi. Provata anche `exportObject` sulla classe che non estende `UnicastRemoteObject`.
- Eseguito lo scambio tra `ServerRiga` e `ClientRiga`: risposta `Ricevuto: Ciao`. Provato anche lo scambio di oggetti con output → flush → input su entrambi i lati, senza blocco sull’header.
- Eseguiti i due esempi ripristinati di `start()`/`join()`: risultato `42`. Compilate anche le varianti Singleton e gli esempi di lock e segnalazione. La compilazione non viene presentata come prova che la versione Singleton volutamente non sincronizzata sia thread-safe.
- Ricontrollati il valore numerico di Amdahl e i conteggi di coppie/gruppi per le precisazioni su Metcalfe e Reed. Nessun errore di spaziatura nel diff Git.

Questi controlli confermano gli esempi verificati e la copertura testuale confrontata; non dimostrano tutte le possibili esecuzioni concorrenti o ogni guasto di rete.

## Materiale non verificabile da questi soli testi

**Limite risolto con il successivo ZIP Obsidian:** al momento di questa prima verifica mancava `Pasted image 20261013095249.png`. L’originale è ora disponibile, confrontato e incluso come [volatile-flag.png](tutti%20appunti/immagini/volatile-flag.png). Il [nuovo resoconto](VERIFICA%20OBSIDIAN%20PD.md) copre anche domande, esercizi, termini e annotazioni del vault.

Le formulazioni storiche di Gilder/“Shugard” e il richiamo OSI/APPLICATION sono conservati come riportati nei testi: per attribuirli esattamente al docente servirebbe la fonte originale corrispondente. Questa prima verifica confronta **i testi ricevuti fino a quel momento**; il successivo ZIP è trattato nel nuovo resoconto. Non certifica che sia presente ogni argomento del programma d’esame non inviato.
