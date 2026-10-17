package studio;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public final class Server {
    private static HelloImpl servizio;
    private static Registry registro;

    public static void main(String[] args) throws Exception {
        servizio = new HelloImpl();
        Hello stub = (Hello) UnicastRemoteObject.exportObject(servizio, 1100);
        registro = LocateRegistry.createRegistry(1099);
        registro.rebind("Hello", stub);
        System.out.println("Server Hello pronto");
    }
}
