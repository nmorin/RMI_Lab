/* 
 * Nicole Morin and Megan Maher
 * Bowdoin Class of 2016
 * Distributed Systems: RMI Lab
 * 
 * Created: February 17, 2015
 * Last Modified: March 6, 2015
 *
 * The Front End Server deals direclty with the client, and 
 * depending on the request, will forward the request to the
 * Catalog server or the Order server. It then will forward
 * the response from one of these servers back to the client.
 */

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
        String host1 = (argv
            .length < 1) ? "localhost" : argv[0];
        String host2 = (argv
            .length < 1) ? "localhost" : argv[1];
        try {
            Registry catalogRegistry = LocateRegistry.getRegistry(host1, 8888);
            catalogStub = (CatalogInterface) catalogRegistry.lookup("CatalogInterface");
            System.out.println("Found catalog!");

            Registry orderRegistry = LocateRegistry.getRegistry(host2, 8887);
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
