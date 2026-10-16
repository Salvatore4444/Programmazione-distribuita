# Confronto degli appunti PD con il vault Obsidian

Verifica del **16 ottobre 2026**, rispetto al repository GitHub al commit `2cdd3dc`. Fonte: `Appunti in bella INGEGNERIA.zip` fornito dall’utente.

## Esito e perimetro

La versione precedente copriva le lezioni ricevute in chat, ma **non aveva ancora la stessa copertura del vault**: mancavano le raccolte di domande, gran parte degli esercizi completi, il glossario separato, le immagini e alcune spiegazioni nelle annotazioni. Queste parti sono ora integrate. Le lezioni già confrontate nei testi incollati sono state conservate; non sono state sostituite con un riassunto più breve.

Sono stati selezionati **23 file Markdown di PD** (uno vuoto), **5 file di annotazioni review** e **11 immagini effettivamente richiamate**. Sono comprese le cartelle `2 APPUNTI IN BELLA PD`, `Z TERMINI PD` e la sola nota esterna collegata `spazio di indirizzamento.md`. Le altre materie e configurazioni/plugin Obsidian non sono state importate. I documenti allegati sono stati trattati come materiale da confrontare.

La corrispondenza completa fonte/destinazione è nella tabella sotto e nel [manifest con SHA-256](VERIFICA%20OBSIDIAN%20PD.json). La copertura riguarda il materiale PD presente nello ZIP e i testi precedenti; non certifica un programma d’esame che non è stato fornito.

## Contenuto recuperato

- **Domande:** 9 sul prologo, 46 su sistemi aperti e middleware, 56 sui thread: **111 domande con tutte le risposte**. Le liste iniziali di sole domande duplicate non sono ripetute. Non sono stati inventati quesiti. Il file originale delle domande Socket è vuoto.
- **Thread:** conservati tutti i **23 blocchi originali con classi pubbliche**, inclusi i Main ripetuti delle varianti. La raccolta pratica mantiene contatori, run diretto/start, riempimento array a uno/due/quattro thread, somma sequenziale e con sedici thread, stampa composta, Monte Carlo sequenziale/uno/due/più worker, deadlock con due e tre thread, contatore con otto worker e soluzione sincronizzata con cento worker. Restano anche le spiegazioni passo per passo.
- **Socket:** mantenuto lo scambio a righe e aggiunto integralmente l’esercizio getNome, con Server.java, Client.java, direzioni degli stream, protocollo, risultato atteso e screenshot originali.
- **Termini e immagini:** glossario con le definizioni originali, incluse stub, skeleton, header e spazio di indirizzamento. Tutte le 11 immagini sono conservate byte per byte, con nomi leggibili e link funzionanti. Recuperata anche l’immagine volatile che mancava al precedente confronto.
- **Annotazioni:** integrate le spiegazioni su downsizing, sistemi IT/legacy, portabilità e interoperabilità, mobilità, dipendenze tra trasparenze, retry di un addebito, commit/rollback, riuso dei canali, multicast, attivazione, Amdahl, oggetti remoti, lambda e Runnable. Commenti ripetuti sono accorpati; identificativi e coordinate del plugin non sono contenuto di studio.
- **Architetture:** esplicitati clock globale assente e architettura CORBA con IDL, ORB e IIOP; conservati .NET, EJB, middleware esplicito/implicito e i dettagli RMI già verificati.

## Correzioni evidenziate negli appunti

1. **Downsizing:** le annotazioni chiariscono che qui significa separazione dalla casa madre mantenendo una federazione; corretta la precedente definizione soltanto infrastrutturale.
2. **Moore:** raddoppio ogni 24 mesi significa fattore 10 in circa 6,64 anni, non in poco più di cinque; densità dei transistor e velocità di ogni programma non sono equivalenti.
3. **ArrayInitializer:** le chiamate richiedono il quarto parametro valore; ora passano 42.
4. **ArraySum:** la classe era usata ma assente; è stata completata. Gli intervalli [inizio, fine) coprono anche lunghezze non divisibili per il numero di worker.
5. **Monte Carlo:** il for originale costruiva soltanto i worker e ignorava l’eventuale resto; mantenuto come frammento e affiancato da una versione completa con start, join, distribuzione del resto e riduzione. Restano le tre versioni sequenziale/uno/due worker originali.
6. **Stampa:** conservato il frammento sincronizzato e aggiunto un programma completo con le tre funzioni di stampa.
7. **Concorrenza:** final non rende immutabile un array; il getter non sincronizzato della prima variante è leggibile dopo join, mentre letture durante il lavoro richiedono la stessa disciplina di sincronizzazione. I deadlock restano intenzionali e sono indicati come programmi che non terminano da soli.
8. **Rete e tecnologie storiche:** un timeout può arrivare dopo un commit già avvenuto; i retry devono evitare duplicazioni. Skeleton indica un ruolo server, non obbligatoriamente una classe Thread. Conservate le distinzioni fra le slide storiche e le funzionalità RMI attuali.
9. I marcatori `[cite: ...]` delle risposte originali non avevano una bibliografia allegata: sono rimossi come rimandi non verificabili, senza eliminare il testo delle risposte.

## Verifiche effettuate

- Controllata la conservazione dei 23 blocchi originali di classi degli esercizi Thread, dopo le correzioni dichiarate. Confrontate integralmente le 46 risposte Open e le 56 risposte Thread; delle 9 risposte Prologo, 7 mantengono il testo e 2 esplicitano le correzioni sulla legge di Moore e sul carattere empirico delle leggi.
- Con OpenJDK 21.0.12.1 superati **20 gruppi di compilazione e prove** degli esercizi nuovi, oltre alle verifiche degli esempi già presenti: RMI con somma = 5, esportazione esplicita, socket a righe con risposta Ricevuto: Ciao, start/join e Singleton.
- getNome eseguito con server e client separati: **Nome ricevuto: Mario Rossi**. Verificato l’ordine output → flush → input.
- Contatore sincronizzato: 40.000 e 10.000.000 incrementi esatti nelle rispettive varianti. Le versioni senza protezione hanno mostrato aggiornamenti persi; non viene garantito che l’errore emerga a ogni esecuzione.
- Somma array: primo elemento 0, ultimo 3.599.997 in entrambe le versioni. Provate 20 combinazioni di dimensioni/worker, incluse dimensioni non divisibili e worker in eccesso.
- Stampa composta: 200 gruppi `:-)` integri. Monte Carlo: stessa stima nella versione sequenziale e a un worker con lo stesso seme; nessun punto perso con 17/5, 1/5 e 100003/7 punti/worker.
- Deadlock: rilevato il ciclo effettivo di monitor con ThreadMXBean per 2 e 3 thread; terminato poi il processo di prova. Il blocco è il risultato didattico atteso.
- Confrontati gli SHA-256 delle 11 immagini; controllati 19 documenti Markdown, 118 link locali e 11 riferimenti a immagini, oltre a code fence e titoli HTML: nessun errore rilevato. GitHub può filtrare il colore CSS; i titoli restano grandi e in grassetto.

Le prove dimostrano il comportamento verificato, non tutte le possibili esecuzioni concorrenti né ogni guasto di rete. I numeri temporali non sono usati come benchmark generale.

## Corrispondenza delle fonti

| Fonte nello ZIP, relativa alla radice del vault | Destinazione nel repository |
|---|---|
| 2 APPUNTI IN BELLA PD/1 LEZIONI/0 COMANDI.md | [THREAD.md](tutti%20appunti/THREAD.md); [THREAD.md](tutti%20appunti%20pratici/THREAD.md); [JAVA RMI.md](tutti%20appunti/JAVA%20RMI.md); [JAVA RMI.md](tutti%20appunti%20pratici/JAVA%20RMI.md); [ESERCIZI THREAD.md](tutti%20appunti%20pratici/ESERCIZI%20THREAD.md) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/0 FOTO COMANDI.md | [THREAD.md](tutti%20appunti/THREAD.md); [THREAD.md](tutti%20appunti%20pratici/THREAD.md) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/0 FUNZIONAMENTO INTERNO.md | [THREAD.md](tutti%20appunti/THREAD.md); [THREAD.md](tutti%20appunti%20pratici/THREAD.md); [GLOSSARIO PD.md](tutti%20appunti/GLOSSARIO%20PD.md) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/0 GLOSSARIO.md | [GLOSSARIO PD.md](tutti%20appunti/GLOSSARIO%20PD.md) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/0 leggi matematica.md | [SISTEMI DISTRIBUITI E MIDDLEWARE.md](tutti%20appunti/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md); [SISTEMI DISTRIBUITI E MIDDLEWARE.md](tutti%20appunti%20pratici/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/1 PROLOGO AI SISTEMI DISTRIBUITI.md | [SISTEMI DISTRIBUITI E MIDDLEWARE.md](tutti%20appunti/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md); [SISTEMI DISTRIBUITI E MIDDLEWARE.md](tutti%20appunti%20pratici/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/2 OPEN DISTRIBUTED.md | [SISTEMI DISTRIBUITI E MIDDLEWARE.md](tutti%20appunti/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md); [SISTEMI DISTRIBUITI E MIDDLEWARE.md](tutti%20appunti%20pratici/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/2.1 SLIDE MIDDLEWARE IMPLICITO.md | [SISTEMI DISTRIBUITI E MIDDLEWARE.md](tutti%20appunti/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md); [SISTEMI DISTRIBUITI E MIDDLEWARE.md](tutti%20appunti%20pratici/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/3 THREAD.md | [THREAD.md](tutti%20appunti/THREAD.md); [THREAD.md](tutti%20appunti%20pratici/THREAD.md) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/4 SOCKET.md | [SOCKET.md](tutti%20appunti/SOCKET.md); [SOCKET.md](tutti%20appunti%20pratici/SOCKET.md) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/4.1.md | [SOCKET.md](tutti%20appunti%20pratici/SOCKET.md) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/4.2 JAVA RMI.md | [JAVA RMI.md](tutti%20appunti/JAVA%20RMI.md); [JAVA RMI.md](tutti%20appunti%20pratici/JAVA%20RMI.md) |
| 2 APPUNTI IN BELLA PD/2 DOMANDE/1 PROLOGO.md | [SISTEMI DISTRIBUITI E MIDDLEWARE.md](tutti%20appunti/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md) |
| 2 APPUNTI IN BELLA PD/2 DOMANDE/2 OPEN.md | [SISTEMI DISTRIBUITI E MIDDLEWARE.md](tutti%20appunti/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md) |
| 2 APPUNTI IN BELLA PD/2 DOMANDE/3 THREAD.md | [THREAD.md](tutti%20appunti/THREAD.md) |
| 2 APPUNTI IN BELLA PD/2 DOMANDE/4 SOCKET.md | Vuoto già nella fonte |
| 2 APPUNTI IN BELLA PD/3 ESERCIZI/1 COSA IMPARARE DAI THREDA.md | [ESERCIZI THREAD.md](tutti%20appunti%20pratici/ESERCIZI%20THREAD.md); [THREAD.md](tutti%20appunti/THREAD.md); [THREAD.md](tutti%20appunti%20pratici/THREAD.md) |
| 2 APPUNTI IN BELLA PD/3 ESERCIZI/1 THREAD.md | [ESERCIZI THREAD.md](tutti%20appunti%20pratici/ESERCIZI%20THREAD.md) |
| 2 APPUNTI IN BELLA PD/3 ESERCIZI/2 SOCKET.md | [SOCKET.md](tutti%20appunti%20pratici/SOCKET.md) |
| Z TERMINI PD/header.md | [GLOSSARIO PD.md](tutti%20appunti/GLOSSARIO%20PD.md); [SOCKET.md](tutti%20appunti/SOCKET.md); [SOCKET.md](tutti%20appunti%20pratici/SOCKET.md) |
| Z TERMINI PD/skeleton.md | [GLOSSARIO PD.md](tutti%20appunti/GLOSSARIO%20PD.md); [SOCKET.md](tutti%20appunti/SOCKET.md); [SOCKET.md](tutti%20appunti%20pratici/SOCKET.md) |
| Z TERMINI PD/stub.md | [GLOSSARIO PD.md](tutti%20appunti/GLOSSARIO%20PD.md); [SOCKET.md](tutti%20appunti/SOCKET.md); [SOCKET.md](tutti%20appunti%20pratici/SOCKET.md) |
| spazio di indirizzamento.md | [GLOSSARIO PD.md](tutti%20appunti/GLOSSARIO%20PD.md); [JAVA RMI.md](tutti%20appunti/JAVA%20RMI.md); [JAVA RMI.md](tutti%20appunti%20pratici/JAVA%20RMI.md) |
| IMMAGINI/Pasted image 20261011135136.png | [trasparenze-dipendenze.png](tutti%20appunti/immagini/trasparenze-dipendenze.png) |
| IMMAGINI/Pasted image 20261012214539.png | [thread-incrementi-core.png](tutti%20appunti/immagini/thread-incrementi-core.png) |
| IMMAGINI/Pasted image 20261012221126.png | [thread-vs-runnable.png](tutti%20appunti/immagini/thread-vs-runnable.png) |
| IMMAGINI/Pasted image 20261012221520.png | [runnable-esempio.png](tutti%20appunti/immagini/runnable-esempio.png) |
| IMMAGINI/Pasted image 20261013095249.png | [volatile-flag.png](tutti%20appunti/immagini/volatile-flag.png) |
| IMMAGINI/Pasted image 20261013095339.png | [atomicita-incremento.png](tutti%20appunti/immagini/atomicita-incremento.png) |
| IMMAGINI/Pasted image 20261013105642.png | [this-escape.png](tutti%20appunti/immagini/this-escape.png) |
| IMMAGINI/Pasted image 20261014142924.png | [lambda-cattura-variabili.png](tutti%20appunti/immagini/lambda-cattura-variabili.png) |
| IMMAGINI/Pasted image 20261014145134.png | [ripartizione-17-punti.png](tutti%20appunti/immagini/ripartizione-17-punti.png) |
| Pasted image 20261016101453.png | [socket-server-oggetti.png](tutti%20appunti/immagini/socket-server-oggetti.png) |
| Pasted image 20261016101507.png | [socket-client-oggetti.png](tutti%20appunti/immagini/socket-client-oggetti.png) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/1 PROLOGO AI SISTEMI DISTRIBUITI.md.review.yaml | [SISTEMI DISTRIBUITI E MIDDLEWARE.md](tutti%20appunti/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md); [SISTEMI DISTRIBUITI E MIDDLEWARE.md](tutti%20appunti%20pratici/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/2 OPEN DISTRIBUTED.md.review.yaml | [SISTEMI DISTRIBUITI E MIDDLEWARE.md](tutti%20appunti/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md); [SISTEMI DISTRIBUITI E MIDDLEWARE.md](tutti%20appunti%20pratici/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/3 THREAD.md.review.yaml | [THREAD.md](tutti%20appunti/THREAD.md); [THREAD.md](tutti%20appunti%20pratici/THREAD.md); [GLOSSARIO PD.md](tutti%20appunti/GLOSSARIO%20PD.md) |
| 2 APPUNTI IN BELLA PD/1 LEZIONI/4.2 JAVA RMI.md.review.yaml | [JAVA RMI.md](tutti%20appunti/JAVA%20RMI.md); [JAVA RMI.md](tutti%20appunti%20pratici/JAVA%20RMI.md) |
| 2 APPUNTI IN BELLA PD/3 ESERCIZI/1 THREAD.md.review.yaml | [ESERCIZI THREAD.md](tutti%20appunti%20pratici/ESERCIZI%20THREAD.md) |
