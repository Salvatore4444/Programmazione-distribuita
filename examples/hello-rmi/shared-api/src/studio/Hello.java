package studio;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Hello extends Remote {
    String saluta(String nome) throws RemoteException;
}
