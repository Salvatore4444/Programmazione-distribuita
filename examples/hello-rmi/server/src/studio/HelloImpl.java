package studio;

import java.rmi.RemoteException;

// Implementazione locale: l'esportazione è esplicita in Server.
public final class HelloImpl implements Hello {
    @Override
    public String saluta(String nome) throws RemoteException {
        return "Ciao, " + nome + "!";
    }
}
