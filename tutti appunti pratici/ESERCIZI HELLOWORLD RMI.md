<h1 style="color:#C00000"><strong>HELLOWORLD RMI — ESERCIZI 49–51</strong></h1>

Questa pagina completa le [domande teoriche 9–48](../tutti%20appunti/JAVA%20RMI.md). I sorgenti sono in [examples/hello-rmi](../examples/hello-rmi/README.md). Il progetto contiene **due programmi separati**, `server` e `client`, e un contratto `shared-api` compilato indipendentemente in entrambi: il client non dipende dall'implementazione del server.

<h2 style="color:#C00000"><strong>49. TROVARE UNA POLICY MINIMA PER HELLOWORLD</strong></h2>

Per l'esempio locale, il server esporta il servizio sulla porta **1100** e crea il registry sulla **1099**. Una policy vuota, provata con `-Djava.security.manager` e `-Djava.security.policy==file:/tmp/hello-empty.policy` su JDK 21, fallisce già su `SocketPermission "localhost:1100" "listen"`. Dopo aver aggiunto soltanto i permessi di socket necessari, server e client si avviano e il client stampa `Ciao, mondo!`.

Policy del [server](../examples/hello-rmi/server.policy):

```java
grant {
    permission java.net.SocketPermission "localhost:1099-1100", "listen";
    permission java.net.SocketPermission "127.0.0.1:1024-", "accept";
};
```

Policy del [client](../examples/hello-rmi/client.policy):

```java
grant {
    permission java.net.SocketPermission "localhost:1099", "connect";
    permission java.net.SocketPermission "127.0.0.1:1100", "connect";
};
```

Il server deve poter ascoltare sulle due porte e accettare una connessione da una **porta effimera** del client; il client deve collegarsi al registry e al servizio. `resolve` è implicito nelle azioni di `SocketPermission`. Ho tolto ciascuna regola a turno: senza `listen` il server non si avvia; senza `accept` il server rifiuta la richiesta del client (sul server compare `AccessControlException`); senza uno dei due `connect` il client fallisce rispettivamente su registry o servizio. Le regole sono quindi il **minimo funzionale riscontrato per questa configurazione di test**: indirizzo pubblicizzato da RMI, interfacce, porte, eventuali servizi aggiuntivi e versione del JDK possono cambiare i permessi richiesti. Non c'è `AllPermission`, né accesso a file o host esterni.

Da JDK 24 il Security Manager è disabilitato permanentemente: questo esercizio sulle policy è **storico** e non è una strategia di sicurezza utilizzabile sui JDK moderni. In JDK 21 compaiono avvisi di deprecazione.

<h2 style="color:#C00000"><strong>50. HELLOWORLD CON IMPLEMENTAZIONE LOCALE ED <code>exportObject()</code></strong></h2>

L'interfaccia comune [Hello.java](../examples/hello-rmi/shared-api/src/studio/Hello.java) estende `Remote` e dichiara `RemoteException`. [HelloImpl.java](../examples/hello-rmi/server/src/studio/HelloImpl.java) la implementa **senza** estendere `UnicastRemoteObject`. Nel [Server.java](../examples/hello-rmi/server/src/studio/Server.java):

```java
servizio = new HelloImpl();
Hello stub = (Hello) UnicastRemoteObject.exportObject(servizio, 1100);
registro = LocateRegistry.createRegistry(1099);
registro.rebind("Hello", stub);
```

L'oggetto viene esportato **una sola volta**; si registra lo stub restituito, non una nuova implementazione. I campi statici del server mantengono riferimenti forti durante l'esecuzione. Il [Client.java](../examples/hello-rmi/client/src/studio/Client.java) prende lo stub con `lookup("Hello")` e chiama `saluta("mondo")`.

Dalla radice del repository, con un JDK che include `javac`:

```sh
mkdir -p examples/hello-rmi/server/build examples/hello-rmi/client/build
javac -d examples/hello-rmi/server/build examples/hello-rmi/shared-api/src/studio/Hello.java examples/hello-rmi/server/src/studio/*.java
javac -d examples/hello-rmi/client/build examples/hello-rmi/shared-api/src/studio/Hello.java examples/hello-rmi/client/src/studio/*.java
```

Qui è installato il modulo `jdk.compiler`, ma manca l'eseguibile `javac`: per compilare è stato usato `java --module jdk.compiler/com.sun.tools.javac.Main` al posto di `javac`, con gli stessi argomenti.

In due terminali, sempre dalla radice:

```sh
java -Djava.security.manager -Djava.security.policy==file:examples/hello-rmi/server.policy -cp examples/hello-rmi/server/build studio.Server
java -Djava.security.manager -Djava.security.policy==file:examples/hello-rmi/client.policy -cp examples/hello-rmi/client/build studio.Client
```

Per la sola verifica del funzionamento RMI su JDK 21 si possono omettere le opzioni `-Djava.security.*`. Il client dovrebbe stampare `Ciao, mondo!`. Su macchine diverse, passare l'host del server come argomento al client e configurare correttamente l'indirizzo pubblicizzato (`java.rmi.server.hostname`) e le porte: le policy locali sopra non valgono per host remoti.

<h2 style="color:#C00000"><strong>51. DUE PROGETTI E STUB/SKELETON GENERATI CON <code>rmic</code></strong></h2>

La struttura dei sorgenti realizza la separazione: `server` contiene `HelloImpl` e `Server`; `client` contiene soltanto `Client`; `shared-api` contiene `Hello`, da distribuire in versione compatibile a entrambi. Le due compilazioni del punto 50 producono directory `build` autonome. Il client non compila né importa `HelloImpl`.

Su un **JDK 8 con `rmic`**, compilare **sia server sia client con quello stesso JDK 8** e poi eseguire dalla radice:

```sh
rmic -vcompat -classpath examples/hello-rmi/server/build -d examples/hello-rmi/server/build studio.HelloImpl
cp examples/hello-rmi/server/build/studio/HelloImpl_Stub.class examples/hello-rmi/client/build/studio/
```

`-vcompat` produce lo stub statico `HelloImpl_Stub.class` e lo skeleton legacy `HelloImpl_Skel.class` nella directory del server. Lo **stub** va anche nel progetto client; lo **skeleton** resta nel server. Su JDK 8 si avviano poi server e client con i rispettivi `build` come classpath. Il runtime deve trovare lo stub statico sul server; usare l'opzione di compatibilità solo per l'esercizio storico. Se si eliminano i file generati, il progetto resta un esempio RMI moderno con stub dinamici.

**Limite della verifica:** in questo ambiente c'è JDK 21, che non fornisce `rmic`; sono stati compilati e provati i due progetti con stub dinamici, mentre i due comandi `rmic`/`cp` sono la procedura per JDK 8 e non sono stati eseguiti qui. Con un JDK moderno non va simulata la presenza di uno skeleton generato. Interfaccia e eventuali DTO serializzabili devono essere comuni; l'implementazione server non va copiata nel client.
