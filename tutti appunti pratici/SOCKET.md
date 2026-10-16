<h1 style="color:#C00000"><strong>SOCKET — APPUNTI PRATICI</strong></h1>

Promemoria operativo per gli esercizi con socket. La teoria è in [tutti appunti/SOCKET.md](</workspace/Programmazione-distribuita/tutti%20appunti/SOCKET.md>).

<h2 style="color:#C00000"><strong>CONNESSIONE TCP</strong></h2>

- Sul server crea un `ServerSocket` sulla porta prevista.
- Chiama `accept()` per attendere il client. Ricorda che è bloccante.
- Sul client crea `new Socket(host, porta)`.
- Il `Socket` restituito da `accept()` è la connessione di quel client; usa quello per leggere e scrivere, non il `ServerSocket`.
- Per servire più client contemporaneamente, gestisci ogni connessione separatamente, per esempio assegnandola a un thread o a un executor.

<h2 style="color:#C00000"><strong>CREAZIONE DI STREAM A OGGETTI SENZA DEADLOCK</strong></h2>

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

<h2 style="color:#C00000"><strong>DEFINIRE L’ORDINE DEL PROTOCOLLO</strong></h2>

- Concorda tra client e server l’ordine esatto di letture e scritture.
- Ogni dato scritto da un lato deve essere letto dall’altro lato nello stesso ordine e con un formato compatibile.
- Non leggere una risposta prima che il peer l’abbia inviata; attenzione alle letture bloccanti.
- Con `readLine()`, il mittente deve inviare il terminatore di riga e fare `flush()` quando necessario.
- TCP è un flusso: se devi separare messaggi, definisci esplicitamente delimitatori o lunghezze.

<h2 style="color:#C00000"><strong>WRAPPING DI STREAM PER IL TESTO</strong></h2>

Per leggere testo riga per riga usa un reader avvolto sullo stream del socket, specificando una codifica condivisa, ad esempio UTF-8. Anche il writer deve usare la stessa codifica e un formato di messaggio concordato.

<h2 style="color:#C00000"><strong>OGGETTI JAVA</strong></h2>

- Le classi degli oggetti inviati con `writeObject()` devono implementare `Serializable` e le classi necessarie devono essere presenti sul destinatario.
- Usa `writeObject()` per inviare e `readObject()` per ricevere; tratta il valore letto secondo il tipo previsto e valida i dati.
- Non deserializzare oggetti da sorgenti non fidate.
- Se usi stream a oggetti, mantieni lo stesso ordine di creazione su entrambi i lati e stabilisci un protocollo di lettura/scrittura che non faccia attendere reciprocamente input non ancora inviati.

<h2 style="color:#C00000"><strong>CHIUSURA DELLE RISORSE</strong></h2>

- Chiudi socket e stream quando lo scambio è concluso.
- Preferisci `try-with-resources` per garantire la chiusura anche in caso di eccezioni.
- Se non usi `try-with-resources`, chiudi le risorse in un blocco `finally`.
- Sul server chiudi il socket del client al termine della sessione; chiudi il `ServerSocket` quando il server deve terminare.

<h2 style="color:#C00000"><strong>CHIAMATE REMOTE SOPRA SOCKET</strong></h2>

Se l’esercizio richiede stub/skeleton:

1. definisci l’interfaccia e il formato dei messaggi;
2. nello stub serializza metodo e parametri secondo il protocollo concordato;
3. sul server leggi i campi nello stesso ordine e invoca il metodo sull’oggetto vero;
4. invia un risultato o un messaggio di errore;
5. nello stub leggi la risposta e restituisci il risultato al client.

Non confondere questa logica di invocazione remota con le operazioni base di `Socket`: il socket trasferisce i dati, mentre stub e skeleton implementano il protocollo applicativo.

<h2 style="color:#C00000"><strong>SCHEMA SERVER/CLIENT CON MESSAGGI TESTUALI</strong></h2>

Esempio minimo di server TCP che accetta un client, legge una riga e risponde. È un esempio didattico per un client alla volta; un server concorrente deve gestire ogni socket accettato separatamente.

```java
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

try (ServerSocket server = new ServerSocket(5000);
     Socket client = server.accept();
     BufferedReader in = new BufferedReader(new InputStreamReader(
         client.getInputStream(), StandardCharsets.UTF_8));
     BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
         client.getOutputStream(), StandardCharsets.UTF_8))) {

    String richiesta = in.readLine();
    out.write("Ricevuto: " + richiesta);
    out.newLine();
    out.flush();
}
```

Il client crea `new Socket(host, 5000)`, scrive la richiesta seguita da `newLine()` e `flush()`, poi legge la risposta. Entrambi devono rispettare lo stesso protocollo: qui una riga in richiesta e una riga in risposta.

<h2 style="color:#C00000"><strong>CHECKLIST DI DEBUG</strong></h2>

Quando un esercizio si blocca, controlla in quest’ordine:

1. il server sta ascoltando sulla porta prevista?
2. il client sta usando host e porta corretti?
3. il server ha chiamato `accept()` e ha ottenuto un `Socket`?
4. entrambi i lati creano gli stream in un ordine compatibile?
5. il mittente ha inviato il delimitatore o la lunghezza attesa e fatto `flush()`?
6. letture e scritture seguono lo stesso ordine sul protocollo?
7. ogni lato chiude il socket e gli stream quando termina?

Non usare `sleep()` o attese arbitrarie per “sistemare” l’ordine: definisci il protocollo e sincronizza lo scambio con i dati realmente inviati.
