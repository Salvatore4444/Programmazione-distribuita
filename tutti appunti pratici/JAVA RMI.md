<h1 style="color:#C00000"><strong>Java RMI — Appunti pratici</strong></h1>

Solo promemoria operativi da applicare negli esercizi.

<h2 style="color:#C00000"><strong>Checklist di implementazione</strong></h2>

1. Individua le operazioni che il server deve offrire.
2. Dichiara un’interfaccia remota `extends Remote`.
3. Inserisci nell’interfaccia solo le operazioni che il client deve invocare.
4. Dichiara `throws RemoteException` nei metodi remoti.
5. Usa parametri e risultati trasferibili: valori semplici o classi serializzabili; per servizi remoti usa il tipo dell’interfaccia remota.
6. Implementa l’interfaccia nella classe server.
7. Esporta l’oggetto in un solo modo: estendi `UnicastRemoteObject` oppure usa `UnicastRemoteObject.exportObject(...)`.
8. Avvia il registry e registra il riferimento remoto con un nome concordato.
9. Nel client esegui il `lookup`, assegna il risultato al tipo dell’interfaccia e invoca il metodo.
10. Gestisci `RemoteException` e considera ritardi, server non disponibile e interruzioni della rete.

<h2 style="color:#C00000"><strong>Interfaccia remota</strong></h2>

```java
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Calcolatrice extends Remote {
    int somma(int a, int b) throws RemoteException;
}
```

- `Remote` marca l’interfaccia come remota.
- `RemoteException` rende esplicito che la comunicazione può fallire.
- Il client dipende dall’interfaccia, non dalla classe concreta server.
- Un metodo `public` della classe server non è esposto via RMI se non compare nell’interfaccia remota.

<h2 style="color:#C00000"><strong>Implementazione ed esportazione</strong></h2>

Opzione A: estendere `UnicastRemoteObject`:

```java
public class CalcolatriceImpl extends UnicastRemoteObject
        implements Calcolatrice {
    public CalcolatriceImpl() throws RemoteException {
        super();
    }

    public int somma(int a, int b) throws RemoteException {
        return a + b;
    }
}
```

Opzione B: se la classe deve già estendere un’altra classe, implementare l’interfaccia e esportare l’istanza:

```java
CalcolatriceImpl oggetto = new CalcolatriceImpl();
Calcolatrice riferimento =
    (Calcolatrice) UnicastRemoteObject.exportObject(oggetto, 0);
```

Esportare l’oggetto lo predispone a ricevere chiamate remote; registrarlo nel registry gli assegna un nome di ricerca. Sono due operazioni diverse.

<h2 style="color:#C00000"><strong>Registry, stub e client</strong></h2>

- Tratta il registry come una mappa nome → riferimento remoto.
- Il server registra il riferimento; il client fa `lookup` e ottiene uno stub.
- Il client chiama i metodi sullo stub usando l’interfaccia remota.
- Dopo il `lookup`, le invocazioni vanno al servizio tramite lo stub; il registry non esegue né inoltra ogni chiamata.
- Il riferimento non garantisce che il server sia sempre raggiungibile.

<h2 style="color:#C00000"><strong>Parametri e oggetti</strong></h2>

- Le classi concrete dei normali oggetti di valore che attraversano la rete devono essere serializzabili; verifica anche i campi non `transient`.
- Un oggetto serializzato normalmente viene ricostruito come copia: modificarlo non modifica l’originale sul mittente.
- Per passare un servizio remoto, esportalo e passa il riferimento/stub tramite la sua interfaccia, non l’implementazione concreta.
- La serializzazione conserva i riferimenti condivisi all’interno dello stesso flusso; non crea memoria condivisa tra JVM.
- Non confondere `servizio = null` (cambia la variabile locale) con una chiamata remota a un metodo che modifica lo stato sul server.
- Valida i dati deserializzati: la serializzazione non li rende fidati.

<h2 style="color:#C00000"><strong>Percorso della chiamata</strong></h2>

Ricorda la direzione: **client → stub → livello dei riferimenti remoti → trasporto → skeleton/dispatcher → implementazione server**. La risposta torna in senso inverso. Stub e skeleton/runtime curano marshalling e unmarshalling; il trasporto cura la connessione e il trasferimento.

<h2 style="color:#C00000"><strong>Compatibilità e strumenti</strong></h2>

- Tieni interfacce e tipi condivisi disponibili sia a client sia a server.
- Usa classi compatibili tra le parti; considera `serialVersionUID` se le versioni possono differire.
- Non fare affidamento sul download automatico di classi da URL: il codebase è un meccanismo storico, condizionato dalla configurazione e dalla versione Java.
- `rmic` e stub statici appartengono alle procedure legacy; usali solo se l’esercizio o il JDK richiesto li prevedono.
- Non copiare policy permissive come `AllPermission` senza necessità. Il Security Manager delle slide è storico e disabilitato permanentemente da JDK 24.

<h2 style="color:#C00000"><strong>Adapter negli esercizi</strong></h2>

Se il client richiede un’interfaccia diversa da quella della classe già disponibile, crea un Adapter che esponga l’interfaccia attesa e traduca le chiamate verso la classe adattata.

<h2 style="color:#C00000"><strong>Sequenza rapida</strong></h2>

**Interfaccia → implementazione → esportazione → compilazione → avvio registry → registrazione server → avvio client → lookup → invocazione → gestione errori.**
