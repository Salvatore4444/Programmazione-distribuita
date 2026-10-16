<h1 style="color:#C00000"><strong>Middleware e componenti distribuiti — Appunti pratici</strong></h1>

Promemoria per analizzare e progettare esempi di middleware a oggetti distribuiti. La teoria delle slide 29–40 è in [tutti appunti/MIDDLEWARE E COMPONENTI DISTRIBUITI.md](</workspace/Programmazione-distribuita/tutti%20appunti/MIDDLEWARE%20E%20COMPONENTI%20DISTRIBUITI.md>).

<h2 style="color:#C00000"><strong>Separare logica applicativa e servizi infrastrutturali</strong></h2>

Quando leggi o scrivi un metodo di business, distingui:

- **logica di business:** cosa deve fare l’operazione (per esempio trasferire un importo tra conti);
- **servizi middleware:** autenticazione/autorizzazione, transazioni, accesso ai dati, logging e comunicazione.

Se il metodo chiama direttamente API per tutti questi servizi, annota le dipendenze esplicite e individua dove iniziano e finiscono gli errori e le risorse da ripulire.

<h2 style="color:#C00000"><strong>Esempio di trasferimento con transazione</strong></h2>

Per un trasferimento tra due conti, verifica che l’operazione sia trattata come un’unità coerente:

1. verificare che il chiamante sia autorizzato;
2. iniziare una transazione;
3. leggere i due conti;
4. controllare le condizioni richieste (per esempio saldo sufficiente);
5. aggiornare entrambi i saldi;
6. salvare le modifiche;
7. completare la transazione;
8. in caso di errore, annullare la transazione e rilasciare le risorse.

La slide usa questo esempio per mostrare la quantità di codice infrastrutturale che può accumularsi quando il middleware è esplicito. In un sistema reale, segui le regole del framework/container utilizzato per commit, rollback e gestione delle eccezioni.

<h2 style="color:#C00000"><strong>Middleware esplicito o implicito</strong></h2>

Quando analizzi un esercizio, individua il modello:

- **Esplicito:** nel codice trovi chiamate dirette a Transaction API, Security API, Database API o altri servizi.
- **Implicito:** il codice dichiara principalmente la logica applicativa; servizi e politiche sono configurati nel container o nel deployment descriptor e applicati da interceptor o meccanismi equivalenti.

Per il modello implicito controlla sempre sia il codice sia la configurazione: dichiarare un servizio non garantisce che il descriptor sia corretto o che l’ambiente lo abbia applicato.

<h2 style="color:#C00000"><strong>Analizzare un request interceptor</strong></h2>

Per seguire una richiesta, ricostruisci l’ordine:

1. chiamata del client e stub;
2. passaggio attraverso rete e skeleton/dispatcher;
3. interceptor e servizi trasversali applicati;
4. oggetto di business;
5. risultato o errore, con passaggio inverso attraverso middleware e client.

Verifica quali operazioni avvengono prima del metodo e quali dopo, in particolare sicurezza, apertura/chiusura della transazione e accesso al database.

<h2 style="color:#C00000"><strong>Regole di progettazione da ricordare</strong></h2>

- Mantieni la logica di business leggibile e separata dai dettagli di sicurezza, transazione e persistenza quando il middleware lo consente.
- Se il middleware è esplicito, gestisci tutti i percorsi d’errore: autorizzazione negata, errore database e rollback.
- Se i servizi sono dichiarati nel deployment descriptor, mantieni configurazione e codice coerenti e controlla che il container abbia caricato la configurazione corretta.
- Non assumere che un oggetto remoto garantisca da solo scalabilità o tolleranza ai guasti: verifica le scelte architetturali che forniscono quelle proprietà.
- Per sistemi nuovi, non scegliere .NET Remoting sulla sola base di questa slide: è una tecnologia legacy; usa la soluzione supportata dalla piattaforma e dai requisiti del progetto.
