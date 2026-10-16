<h1 style="color:#C00000"><strong>SOCKET — APPUNTI PRATICI</strong></h1>

Promemoria operativo per gli esercizi con socket. La teoria è in [tutti appunti/SOCKET.md](../tutti%20appunti/SOCKET.md).

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

`flush()` forza lo scaricamento dei dati già scritti e rimasti nel buffer; non crea dati e non garantisce che il programma remoto li abbia già elaborati.

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

Ogni classe va nel proprio file. `ServerRiga` gestisce una sola connessione e poi termina:

```java
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ServerRiga {
    public static void main(String[] args) throws IOException {
        int porta = args.length > 0 ? Integer.parseInt(args[0]) : 5000;
        try (ServerSocket server = new ServerSocket(porta);
             Socket client = server.accept();
             BufferedReader in = new BufferedReader(new InputStreamReader(
                 client.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                 client.getOutputStream(), StandardCharsets.UTF_8))) {
            String richiesta = in.readLine();
            if (richiesta != null) {
                out.write("Ricevuto: " + richiesta);
                out.newLine();
                out.flush();
            }
        }
    }
}
```

Il client invia prima la richiesta e poi legge la risposta:

```java
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ClientRiga {
    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        int porta = args.length > 1 ? Integer.parseInt(args[1]) : 5000;
        try (Socket socket = new Socket(host, porta);
             BufferedReader in = new BufferedReader(new InputStreamReader(
                 socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                 socket.getOutputStream(), StandardCharsets.UTF_8))) {
            out.write("Ciao");
            out.newLine();
            out.flush();
            System.out.println(in.readLine());
        }
    }
}
```

Avvia prima il server e poi il client. Il protocollo è una riga in richiesta e una riga in risposta; senza terminatore, `readLine()` può restare in attesa. I frammenti precedenti sugli stream sono istruzioni da inserire nel contesto di un metodo, non programmi autonomi.

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

<h2 style="color:#C00000"><strong>ESERCIZIO DEL VAULT: GETNOME CON STREAM A OGGETTI</strong></h2>

Esercizio completo da `1 LEZIONI/4.1.md` e `3 ESERCIZI/2 SOCKET.md`, distinto dall’esempio a righe precedente. Salva Server.java e Client.java in una cartella dedicata; questi nomi sono usati anche negli esercizi RMI, che vanno tenuti in progetti separati. Il protocollo serve un client e una richiesta, poi chiude la connessione.

*LATO CLIENT e LATO SERVER*
La regola per capire **su quale lato sei** è guardare **dove viene eseguito quel codice** e **quale socket usa**.


<h3 style="color:#C00000"><strong>LATO CLIENT</strong></h3>


Il client crea il socket per collegarsi al server:

```java
Socket socket = new Socket("localhost", 9000);
```

Poi crea gli stream su quel socket:

```java
ObjectOutputStream out =
    new ObjectOutputStream(socket.getOutputStream());
out.flush(); // invia l’header prima di costruire l’input
ObjectInputStream in =
    new ObjectInputStream(socket.getInputStream());
```

Da quel momento:

- `out` invia dati **dal client al server**;
- `in` legge dati **dal server al client**.


<h3 style="color:#C00000"><strong>LATO SERVER</strong></h3>


Il server crea un `ServerSocket` e aspetta un client:

```java
ServerSocket serverSocket = new ServerSocket(9000);
Socket socket = serverSocket.accept();
```

`accept()` restituisce il socket della connessione accettata. Gli stream creati su questo socket hanno la direzione opposta:

- `in` riceve i dati **dal client**;
- `out` invia dati **al client**.

|Programma|`out` invia verso|`in` riceve da|
|---|---|---|
|Client|Server|Server|
|Server|Client|Client|

I nomi `in` e `out` sono relativi al programma che li usa: `in` significa “input per me”, `out` significa “output da me”.

Puoi seguire il flusso come una conversazione:

```text
Client: out.writeObject("getNome")
Server: in.readObject()            // riceve "getNome"

Server: out.writeObject("Mario")
Client: in.readObject()             // riceve "Mario"
```

Quindi sì, un piccolo esercizio con due programmi separati è il modo migliore per fissarlo.


<h1 style="color:#C00000"><strong>ESEMPIO</strong></h1>

il client chiede il nome al server e il server risponde `"Mario Rossi"`. Sono **due programmi separati**, quindi salvali in due file.


<h3 style="color:#C00000"><strong>SERVER.JAVA</strong></h3>


```java
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws Exception {
        // 1. Il server si mette in ascolto sulla porta 9000
        try (ServerSocket serverSocket = new ServerSocket(9000)) {
            System.out.println("Server in attesa...");

            // 2. Attende che un client si colleghi
            try (Socket socket = serverSocket.accept()) {
                System.out.println("Client collegato");

                // 3. Prepara gli stream del server
                ObjectOutputStream out =
                    new ObjectOutputStream(socket.getOutputStream());
                out.flush(); // invia subito l'header

                ObjectInputStream in =
                    new ObjectInputStream(socket.getInputStream());

                // 4. Riceve la richiesta dal client
                String richiesta = (String) in.readObject();
                System.out.println("Richiesta ricevuta: " + richiesta);

                // 5. Prepara e invia la risposta
                if (richiesta.equals("getNome")) {
                    out.writeObject("Mario Rossi");
                    out.flush();
                }
            }
        }
    }
}
```


<h3 style="color:#C00000"><strong>CLIENT.JAVA</strong></h3>


```java
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception {
        // 1. Il client si collega al server
        try (Socket socket = new Socket("localhost", 9000)) {
            // 2. Prepara gli stream del client
            ObjectOutputStream out =
                new ObjectOutputStream(socket.getOutputStream());
            out.flush(); // invia subito l'header

            ObjectInputStream in =
                new ObjectInputStream(socket.getInputStream());

            // 3. Invia la richiesta al server
            out.writeObject("getNome");
            out.flush();

            // 4. Attende e legge la risposta
            String nome = (String) in.readObject();
            System.out.println("Nome ricevuto: " + nome);
        }
    }
}
```


<h3 style="color:#C00000"><strong>COME SI SVOLGE LO SCAMBIO</strong></h3>


Avvia prima `Server`, poi `Client`.

```text
CLIENT                                  SERVER
out.writeObject("getNome")  ─────────> in.readObject()
in.readObject()             <───────── out.writeObject("Mario Rossi")
```

Compila con `javac Server.java Client.java`; in due terminali avvia `java Server` e poi `java Client`. Risultato atteso sul client: `Nome ricevuto: Mario Rossi`. La porta 9000 deve essere disponibile; se la cambi, cambia entrambi i programmi. Per comandi diversi da getNome il server chiude senza risposta: un protocollo esteso dovrebbe definire anche una risposta d’errore.

I due originali visuali sono conservati insieme al codice trascritto:

![Server originale getNome](../tutti%20appunti/immagini/socket-server-oggetti.png)

![Client originale getNome](../tutti%20appunti/immagini/socket-client-oggetti.png)
