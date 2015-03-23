import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CatalogInterface extends Remote {
		public String query(String topic) throws RemoteException;
		public String query(int itemNumber) throws RemoteException;
        public void updateCost(int itemNumber, double newCost) throws RemoteException;
        public void updateQuantity(int itemNumber, int changeQuantity) throws RemoteException;
}