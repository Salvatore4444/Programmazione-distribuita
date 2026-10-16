<h1 style="color:#C00000"><strong>Socket — Appunti pratici</strong></h1>

Promemoria operativo per gli esercizi con socket. La teoria è in [tutti appunti/SOCKET.md](</workspace/Programmazione-distribuita/tutti%20appunti/SOCKET.md>).

<h2 style="color:#C00000"><strong>Connessione TCP</strong></h2>

- Sul server crea un `ServerSocket` sulla porta prevista.
- Chiama `accept()` per attendere il client. Ricorda che è bloccante.
- Sul client crea `new Socket(host, porta)`.
- Il `Socket` restituito da `accept()` è la connessione di quel client; usa quello per leggere e scrivere, non il `ServerSocket`.
- Per servire più client contemporaneamente, gestisci ogni connessione separatamente, per esempio assegnandola a un thread o a un executor.

<h2 style="color:#C00000"><strong>Creazione di stream a oggetti senza deadlock</strong></h2>

La costruzione di `ObjectOutputStream` invia un header; la costruzione di `ObjectInputStream` aspetta di leggerlo. Se entrambi i lati creano prima l’input, entrambi possono restare bloccati.

Su **entrambi** i lati, crea e scarica prima l’output, poi crea l’input:

```java
ObjectOutputStream out =
    new ObjectOutputStream(socket.getOutputStream());
out.flush();

ObjectInputStream in =
    new ObjectInputStream(socket.getInputStream());
```

`flush()` invia i dati già scritti e rimasti nel buffer; non crea dati che non sono stati scritti.

<h2 style="color:#C00000"><strong>Definire l’ordine del protocollo</strong></h2>

- Concorda tra client e server l’ordine esatto di letture e scritture.
- Ogni dato scritto da un lato deve essere letto dall’altro lato nello stesso ordine e con un formato compatibile.
- Non leggere una risposta prima che il peer l’abbia inviata; attenzione alle letture bloccanti.
- Con `readLine()`, il mittente deve inviare il terminatore di riga e fare `flush()` quando necessario.
- TCP è un flusso: se devi separare messaggi, definisci esplicitamente delimitatori o lunghezze.

<h2 style="color:#C00000"><strong>Wrapping di stream per il testo</strong></h2>

Per leggere testo riga per riga usa un reader avvolto sullo stream del socket, specificando una codifica condivisa, ad esempio UTF-8. Anche il writer deve usare la stessa codifica e un formato di messaggio concordato.

<h2 style="color:#C00000"><strong>Oggetti Java</strong></h2>

- Le classi degli oggetti inviati con `writeObject()` devono implementare `Serializable` e le classi necessarie devono essere presenti sul destinatario.
- Usa `writeObject()` per inviare e `readObject()` per ricevere; tratta il valore letto secondo il tipo previsto e valida i dati.
- Non deserializzare oggetti da sorgenti non fidate.
- Se usi stream a oggetti, mantieni lo stesso ordine di creazione su entrambi i lati e stabilisci un protocollo di lettura/scrittura che non faccia attendere reciprocamente input non ancora inviati.

<h2 style="color:#C00000"><strong>Chiusura delle risorse</strong></h2>

- Chiudi socket e stream quando lo scambio è concluso.
- Preferisci `try-with-resources` per garantire la chiusura anche in caso di eccezioni.
- Se non usi `try-with-resources`, chiudi le risorse in un blocco `finally`.
- Sul server chiudi il socket del client al termine della sessione; chiudi il `ServerSocket` quando il server deve terminare.

<h2 style="color:#C00000"><strong>Chiamate remote sopra socket</strong></h2>

Se l’esercizio richiede stub/skeleton:

1. definisci l’interfaccia e il formato dei messaggi;
2. nello stub serializza metodo e parametri secondo il protocollo concordato;
3. sul server leggi i campi nello stesso ordine e invoca il metodo sull’oggetto vero;
4. invia un risultato o un messaggio di errore;
5. nello stub leggi la risposta e restituisci il risultato al client.

Non confondere questa logica di invocazione remota con le operazioni base di `Socket`: il socket trasferisce i dati, mentre stub e skeleton implementano il protocollo applicativo.
