# Java RMI — Domande dell’utente e risposte

## CHE COS’È JAVA RMI?

Java RMI è una tecnologia Java che permette di invocare metodi di oggetti presenti in un’altra JVM, anche su una macchina diversa, per realizzare applicazioni distribuite.

## QUALI SONO GLI OBIETTIVI PRINCIPALI DI JAVA RMI?

Semplificare le chiamate remote, integrarsi nel linguaggio Java, rendere riconoscibile la distinzione tra oggetti locali e remoti, limitare la complessità di client e server e preservare le proprietà di sicurezza della piattaforma.

## CHE COSA SIGNIFICA INVOCAZIONE TRASPARENTE?

Significa poter invocare un metodo remoto con una sintassi simile a quella di una chiamata locale, mentre RMI gestisce i dettagli della comunicazione. Rimangono possibili ritardi e fallimenti della chiamata.

## PERCHÉ DEVE ESSERE CHIARO SE UN OGGETTO È LOCALE O REMOTO?

Perché una chiamata remota comporta comunicazione tra JVM, possibili ritardi e fallimenti. La somiglianza nella sintassi non elimina queste differenze.

## UNA JVM PUÒ COMUNICARE CON LO SPAZIO DI INDIRIZZAMENTO DI UN’ALTRA?

Una JVM può comunicare con un’altra JVM, ma non accede direttamente al suo spazio di indirizzamento. Ogni JVM eseguita in un processo distinto ha il proprio spazio di indirizzamento, cioè l’insieme degli indirizzi di memoria utilizzabili dal processo, nei quali risiedono dati, oggetti e codice.

Con RMI la comunicazione avviene così:

1. La JVM client invia una richiesta.
2. La JVM server esegue il metodo sull’oggetto nella propria memoria.
3. Il server restituisce il risultato al client.

Le due JVM comunicano quindi scambiandosi dati e richieste, senza condividere direttamente la memoria. Un riferimento locale a un oggetto di una JVM non permette a un’altra JVM di accedere direttamente a quell’oggetto.

## IL CLIENT USA L’INTERFACCIA REMOTA O LA CLASSE IMPLEMENTATA?

Il client conosce e usa il tipo dell’interfaccia remota, per esempio `Calcolatrice`. Il server crea un oggetto della classe concreta `CalcolatriceImpl` e lo rende accessibile tramite RMI. Il client riceve un riferimento remoto che può usare attraverso l’interfaccia, senza dover conoscere la classe concreta.

```java
Calcolatrice servizio = ...; // riferimento remoto ottenuto dal server
int risultato = servizio.somma(2, 3);
```

## UN METODO PUBLIC È ACCESSIBILE TRAMITE RMI SOLO PERCHÉ È PUBLIC?

No. `public` rende il metodo accessibile secondo le normali regole Java nella JVM che ospita l’oggetto. Per invocarlo da un’altra JVM tramite RMI, il metodo deve essere esposto nell’interfaccia remota e l’oggetto deve essere esportato.

## QUANDO SI USA exportObject INVECE DI ESTENDERE UnicastRemoteObject?

Si usa `UnicastRemoteObject.exportObject` quando la classe dell’oggetto remoto non estende `UnicastRemoteObject`, per esempio perché deve già estendere un’altra classe. In Java una classe può estendere una sola classe.

Si crea normalmente l’oggetto e poi lo si esporta:

```java
CalcolatriceImpl oggetto = new CalcolatriceImpl();
Calcolatrice riferimento =
    (Calcolatrice) UnicastRemoteObject.exportObject(oggetto, 0);
```

`oggetto` è l’istanza concreta che si trova sul server. L’esportazione la predispone a ricevere chiamate RMI e restituisce `riferimento`, usato per raggiungerla da un’altra JVM. Il valore `0` chiede a RMI di scegliere una porta disponibile.

## SE CalcolatriceImpl ESTENDE GIÀ UN’ALTRA CLASSE, COME PUÒ DIVENTARE REMOTA?

Non può estendere anche `UnicastRemoteObject`, perché Java consente una sola superclasse. Può però implementare l’interfaccia remota `Calcolatrice` ed essere esportata esplicitamente con `UnicastRemoteObject.exportObject(oggetto, 0)`. In questo modo usa `UnicastRemoteObject` per esportare l’istanza senza estenderlo.

## QUAL È IL PERCORSO DELLA RICHIESTA E DEL RISULTATO?

La richiesta parte dal client e raggiunge l’oggetto remoto sul server:

**Client → stub → rete → skeleton → oggetto remoto**

L’oggetto remoto esegue il metodo e produce il risultato, che torna al client:

**Oggetto remoto → skeleton → rete → stub → client**

Lo stub è il rappresentante locale del servizio remoto usato dal client. Lo skeleton riceve la richiesta sul server e la passa all’oggetto remoto; nel modello RMI moderno, il runtime svolge questo ruolo senza richiedere una classe skeleton separata.

## IL REGISTRY È COME UN DIZIONARIO?

Sì. È utile immaginarlo come un dizionario che associa un nome (chiave) al riferimento remoto di un servizio (valore). Il client cerca il nome con `lookup` e riceve il riferimento, normalmente uno stub, con cui poi invoca direttamente il servizio. Il registry aiuta a trovare il servizio: non contiene l’implementazione e non esegue né inoltra le chiamate ai suoi metodi.
