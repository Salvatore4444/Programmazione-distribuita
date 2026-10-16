<h1 style="color:#C00000"><strong>SISTEMI DISTRIBUITI E MIDDLEWARE — APPUNTI PRATICI</strong></h1>

Checklist per analizzare esercizi e architetture. La teoria completa, con definizioni, motivazioni, leggi e collegamenti fra gli argomenti, è in [tutti appunti/SISTEMI DISTRIBUITI E MIDDLEWARE.md](../tutti%20appunti/SISTEMI%20DISTRIBUITI%20E%20MIDDLEWARE.md).

<h2 style="color:#C00000"><strong>ANALIZZARE I REQUISITI DI UN SISTEMA DISTRIBUITO</strong></h2>

Quando leggi una traccia, annota:

1. quali componenti e nodi partecipano;
2. dove si trovano le componenti e come comunicano;
3. quali dati o servizi sono condivisi;
4. quali guasti parziali possono verificarsi;
5. quali requisiti di sicurezza, prestazioni, disponibilità e crescita sono richiesti;
6. quali decisioni devono essere nascoste tramite trasparenza e quali devono restare esplicite all’applicazione.

Non assumere che “distribuito” significhi automaticamente scalabile, sicuro o tollerante ai guasti: associa ogni qualità a una scelta concreta e a un modo per verificarla.

<h2 style="color:#C00000"><strong>MOTIVAZIONI E VALORE DELLA RETE</strong></h2>

Negli esercizi teorici, collega ogni motivazione a un effetto concreto:

- economica: integrare sistemi esistenti, ridurre costi di infrastruttura (downsizing) e accelerare il rilascio (time-to-market);
- tecnologica: riusare componenti, aggiungere capacità e integrare piattaforme eterogenee;
- rete di utenti: distingui crescita lineare (Sarnoff), a coppie/quadratica (Metcalfe) e per gruppi/esponenziale (Reed).

Quando applichi una legge di valore, dichiara l’ipotesi: numero di utenti attivi, possibilità di collegamenti a coppie o possibilità di formare gruppi. Presentale come modelli approssimativi, non come previsioni garantite.

<h2 style="color:#C00000"><strong>RICONOSCERE LE TRASPARENZE</strong></h2>

Per una risorsa o un servizio, chiediti quali dettagli il client non dovrebbe gestire:

- accesso: stessa interfaccia per uso locale e remoto?
- locazione: il client conosce l’indirizzo fisico?
- migrazione: il servizio può cambiare macchina durante l’uso?
- replica: chi sceglie e aggiorna le copie?
- concorrenza: come si coordinano accessi simultanei?
- transazione: le modifiche sono trattate come un’unità coerente, con commit o rollback?
- guasti: quali retry, fallback o limiti di attesa esistono?
- persistenza: come si conserva e ritrova lo stato?
- prestazioni e scalabilità: come reagisce il sistema a più richieste?

Documenta i casi in cui la trasparenza non può nascondere la latenza o i guasti della rete.

<h2 style="color:#C00000"><strong>DISTINGUERE RPC, OGGETTI REMOTI E SOCKET</strong></h2>

- Se la traccia descrive una chiamata a procedura remota, identifica parametri, codifica, richiesta, risposta e gestione degli errori: è il percorso RPC.
- Se descrive metodi esposti da un oggetto remoto, individua interfaccia, stub/riferimento e implementazione server.
- Se usa socket direttamente, identifica protocollo, formato dei messaggi e delimitazione dei dati: il socket da solo non definisce chiamate a metodi.
- Ricorda che una chiamata remota può fallire o ritardare anche quando l’API somiglia a una chiamata locale.

<h2 style="color:#C00000"><strong>SEPARARE I COMPITI MIDDLEWARE</strong></h2>

Per ogni servizio, classificalo:

- **infrastruttura/trasporto:** connessione, serializzazione, indirizzamento, dispatch;
- **distribuzione:** marshalling, invocazione, gestione dei guasti e uso dei canali;
- **servizio comune:** sicurezza, transazione, persistenza, database.

Questa mappa aiuta a capire quale parte appartiene alla logica applicativa e quale può essere offerta dal middleware/container.

<h2 style="color:#C00000"><strong>CONFRONTARE MIDDLEWARE ESPLICITO E IMPLICITO</strong></h2>

- Nel modello **esplicito**, cerca nel codice chiamate alle API del middleware e verifica dipendenze, ordine delle operazioni e gestione degli errori.
- Nel modello **implicito**, individua metadati/deployment descriptor, container e interceptor; verifica che la configurazione venga caricata e si applichi al componente previsto.
- In entrambi i modelli, controlla i confini della transazione, l’autorizzazione e il comportamento in caso di errore.

<h2 style="color:#C00000"><strong>ESEMPIO: TRASFERIMENTO TRA CONTI</strong></h2>

Per progettare o leggere l’operazione, verifica che:

1. il chiamante abbia il permesso;
2. la transazione inizi prima di modificare i dati;
3. entrambi i conti vengano letti e convalidati;
4. i due saldi siano aggiornati come un’unica operazione coerente;
5. il successo completi la transazione;
6. un errore annulli le modifiche e rilasci le risorse.

Poi separa questo comportamento di business dai meccanismi di sicurezza, database e transazione, secondo il modello supportato dal middleware.

<h2 style="color:#C00000"><strong>LEGGERE IL PERCORSO DI UNA RICHIESTA</strong></h2>

Traccia la richiesta in ordine: **client → stub/proxy → rete → skeleton/dispatcher → interceptor → oggetto/componente**. Segui poi risposta ed errori a ritroso. Per ogni passaggio chiediti chi serializza, chi controlla l’autorizzazione, chi apre/chiude la transazione e chi accede ai dati.

<h2 style="color:#C00000"><strong>SCELTA DELLE TECNOLOGIE</strong></h2>

Quando un esercizio cita CORBA, .NET Remoting o WCF, riconosci il ruolo storico e i concetti illustrati. Per un progetto nuovo verifica sempre che la tecnologia sia ancora supportata e adatta alla piattaforma, ai protocolli richiesti e ai vincoli del progetto.

<h2 style="color:#C00000"><strong>TABELLA RAPIDA DELLE LEGGI DI RETE</strong></h2>

| Legge | Crescita approssimativa | Parola chiave da ricordare |
|---|---:|---|
| Sarnoff | `N` | singoli utenti |
| Metcalfe | `N²` | collegamenti tra coppie |
| Reed | `2^N` | gruppi/sottoreti |

Per due reti con `n` e `m` utenti attivi, la formula annotata per Reed è `2^n · 2^m − (2^n + 2^m)`. Conservala come formula degli appunti; il conteggio esatto dei gruppi con almeno un membro per rete include un `+1`: `(2^n−1)(2^m−1)`. Distingui anche l’aumento `2nm` del modello di Metcalfe `N²` dalle `nm` nuove coppie non orientate. Spiega che si tratta di modelli del valore potenziale; non presentarla come legge universale del valore economico.
