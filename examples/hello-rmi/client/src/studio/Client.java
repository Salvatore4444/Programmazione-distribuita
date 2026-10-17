package studio;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class Client {
    public static void main(String[] args) throws Exception {
        String host = args.length == 0 ? "localhost" : args[0];
        Registry registro = LocateRegistry.getRegistry(host, 1099);
        Hello servizio = (Hello) registro.lookup("Hello");
        System.out.println(servizio.saluta("mondo"));
    }
}
