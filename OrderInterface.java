import java.rmi.Remote;
import java.rmi.RemoteException;

public interface OrderInterface extends Remote {
        public String buy(int itemNumber) throws RemoteException;
}