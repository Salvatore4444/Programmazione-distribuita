# PRATICA — Buone pratiche per gli esercizi

Promemoria operativo diviso per macroargomento. Aggiungere regole man mano che vengono spiegati nuovi argomenti e slide.

# Java RMI

## Definizione dell’interfaccia remota

- Dichiarare un’interfaccia che estende `java.rmi.Remote`.
- Inserire nell’interfaccia solo le operazioni che il client deve poter invocare da remoto.
- Dichiarare `java.rmi.RemoteException` nei metodi remoti, oppure un suo supertipo ammesso. È una checked exception: il chiamante deve gestirla o dichiararla.
- Usare tipi chiari e stabili nella firma dei metodi. Parametri e risultati devono poter essere trasmessi: i normali valori devono essere serializzabili; i riferimenti a servizi remoti vanno espressi tramite la loro interfaccia remota.

Esempio:

```java
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Calcolatrice extends Remote {
    int somma(int a, int b) throws RemoteException;
}
```

## Implementazione dell’oggetto remoto

- Scrivere una classe che implementa l’interfaccia remota.
- Realizzare tutti i metodi dichiarati nell’interfaccia.
- Scegliere un solo modo per esportare l’oggetto:
  - estendere `UnicastRemoteObject` e dichiarare `RemoteException` nel costruttore;
  - oppure esportare esplicitamente l’istanza con `UnicastRemoteObject.exportObject(...)`, utile se la classe deve già estendere un’altra classe.
- Tenere separati contratto e implementazione: il client deve dipendere dall’interfaccia, non dalla classe concreta del server.
- Ricordare che esportare un oggetto lo rende raggiungibile tramite RMI; registrarlo nel registry è un passaggio distinto.

## Invocazioni e gestione degli errori

- Trattare ogni invocazione remota come un’operazione che può fallire o richiedere tempo: gestire `RemoteException` nel client.
- Non dare per scontato che una chiamata remota si comporti come una chiamata locale, anche se la sintassi è simile.
- Evitare di esporre nell’interfaccia remota metodi che il client non deve usare.
- Passare riferimenti remoti attraverso le interfacce remote; non fare affidamento sull’accesso alla classe concreta presente sul server.

## Promemoria rapido per gli esercizi RMI

1. Individuare quali operazioni deve offrire il server.
2. Dichiarare l’interfaccia: `extends Remote` e metodi con `throws RemoteException`.
3. Implementare l’interfaccia nella classe server.
4. Esportare l’oggetto con `UnicastRemoteObject` (estensione o chiamata esplicita).
5. Pubblicare il riferimento remoto nel servizio di naming/registry, quando previsto dall’esercizio.
6. Nel client cercare il riferimento e usarlo tramite il tipo dell’interfaccia remota.
7. Gestire gli errori di comunicazione.

# Socket

Promemoria da compilare quando affrontiamo le slide e gli esercizi sui socket.

# Thread

Promemoria da compilare quando affrontiamo le slide e gli esercizi sui thread.

## Riferimenti e ricerca degli oggetti remoti

- Il client invoca l’oggetto tramite uno **stub**, cioè il riferimento locale che rappresenta il servizio remoto. Non accede direttamente all’oggetto server.
- Recuperare il riferimento remoto usando un nome concordato, per esempio dal registry RMI, e assegnarlo al tipo dell’interfaccia remota.
- Trattare il riferimento trovato come un riferimento di rete: il fatto di averlo ottenuto non garantisce che il server resti raggiungibile a ogni chiamata.

## Parametri e risultati delle chiamate remote

- Usare parametri e risultati serializzabili quando si devono trasferire normali oggetti tra JVM.
- Ricordare che un normale oggetto serializzato viene passato per valore: il ricevente ottiene una copia ricostruita, non condivide automaticamente lo stesso oggetto in memoria.
- Per condividere un servizio/oggetto remoto, passare il suo riferimento remoto tramite l’interfaccia remota. RMI mantiene l’identità remota attraverso lo stub, non trasferendo l’implementazione server.
- Progettare le chiamate tenendo conto che trasmissione e ricostruzione dei dati possono fallire; gestire le eccezioni remote.

## Architettura RMI a livelli

- Ragionare sul percorso completo di una chiamata: client → stub → livello di riferimento remoto → livello di trasporto → server/skeleton → implementazione; la risposta ripercorre il percorso in senso inverso.
- Lasciare allo stub/skeleton la conversione dei parametri e dei risultati in dati trasmissibili (marshalling/unmarshalling); non confondere questo compito con la logica del metodo remoto.
- Considerare il livello di riferimento remoto responsabile della semantica dell’invocazione (per esempio unicast e comportamento in caso di riconnessione), mentre il trasporto gestisce connessione e trasmissione.
- Il registry aiuta il client a trovare il riferimento iniziale; non esegue i metodi del servizio e non sostituisce i livelli che trasportano le chiamate.

## Serializzazione e caricamento delle classi in RMI

- Rendere `Serializable` le classi concrete degli oggetti di valore che attraversano la rete e mantenere serializzabili i loro campi non `transient`.
- Non trasferire l’implementazione di un servizio remoto come un normale oggetto: esportare il servizio e passare il riferimento remoto/stub.
- Considerare `serialVersionUID` e la compatibilità delle classi quando client e server possono usare versioni diverse.
- Non basare un esercizio o un’applicazione sul caricamento automatico e sicuro di classi da URL: configurazione del codebase, policy e versione della piattaforma incidono sul comportamento. Preferire codice e dipendenze distribuiti in modo controllato.
- Trattare i dati deserializzati come input da validare. La serializzazione non rende fidati gli oggetti ricevuti.
