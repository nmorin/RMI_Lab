import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
// import CatalogInterface;

public class FrontEndServer implements FrontEndServerInterface {

    private static CatalogInterface catalogStub;
    private static OrderInterface orderStub;

    public FrontEndServer() {
        super();
    }

    public String lookUp(int itemNumber) {
        String result = "";
        try {
            System.out.println("I'm looking up from the catalog for " + itemNumber);
            result = catalogStub.query(itemNumber);
        } catch (Exception e) {
            System.out.println("Exception in server lookUp");
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    public String search(String topic) {
        String result = "";
        try {
            System.out.println("I'm searchin the catalog for " + topic);
            result = catalogStub.query(topic);
        } catch (Exception e) {
            System.out.println("Exception in server search");
            e.printStackTrace();
        }
        
        System.out.println(result);
        return result; 
    }
    
    public String buy(int itemNumber) {
        String result = "";
        try {
            System.out.println("I'm buyin " + itemNumber);
            result = orderStub.buy(itemNumber);
        } catch (Exception e) {
            System.out.println("Exception in server buy");
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    public static void main(String[] argv) {
        // if (System.getSecurityManager() == null) {
        //     System.getSecurityManager(new SecurityManager());
        // }
        String host = (argv
            .length < 1) ? "localhost" : argv[0];
        try {
            Registry catalogRegistry = LocateRegistry.getRegistry(host, 8888);
            catalogStub = (CatalogInterface) catalogRegistry.lookup("CatalogInterface");
            System.out.println("Found catalog!");

            Registry orderRegistry = LocateRegistry.getRegistry(host, 8887);
            orderStub = (OrderInterface) orderRegistry.lookup("OrderInterface");
            System.out.println("Found order!");

            String name = "FrontEndServerInterface";
            FrontEndServer server = new FrontEndServer();
            FrontEndServerInterface frontintstub =  (FrontEndServerInterface) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(8886);
            registry.bind(name, frontintstub);
            System.out.println("Binding complete");
        } catch (Exception e) {
            System.out.println("Exception occurred");
            e.printStackTrace();
        }
    }
}
