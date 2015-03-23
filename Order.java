import java.rmi.*;
import java.util.ArrayList;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;


/* The order server maintains a list of all orders made. 
The class checks whether the item requested is in stock, and 
if it is, decrements the quantity by 1 and returns a success
message. If not in stock, it lets the user know. 
It keeps track of the orders made, whether they were successful
or not, and for what items. */
public class Order implements OrderInterface {

    private static CatalogInterface catalogStub;
    private static ArrayList<String> orders;

    public Order() {
        orders = new ArrayList<String>();
    }

    public String buy(int itemNumber) {
        String result = null;
        System.out.println("I'm in buy for " + itemNumber);
        try {
            result = catalogStub.query(itemNumber);
        } catch (Exception e) {
            System.out.println("Exception occurred in order buy");
            e.printStackTrace();
        }
        
        if (result == null) {
            return "That item does not exist";
        }
        else {
            String[] resultSplit = result.split("\n");
            String[] resultSplit2 = resultSplit[1].split(" ");
            int quantity = Integer.parseInt(resultSplit2[0]);
            if (quantity < 1) {
                orders.add("Unsuccessful order for item #" + itemNumber);
                return "Sorry, item not in stock";
            }
            try {
                catalogStub.updateQuantity(itemNumber, -1);
            } catch (Exception e) {
                System.out.println("Exception occurred decrementing the stock");
                e.printStackTrace();
            }
            orders.add("Successful purchase of item #" + itemNumber);
            return "Purchase of " + resultSplit[0] + " completed.";
        }
    }

    public static void main(String[] args) {
        // if (System.getSecurityManager() == null) {
        //     System.getSecurityManager(new SecurityManager());
        // }
        String host = (args.length < 1) ? "localhost" : args[0];
        try {
            Registry lookupRegistry = LocateRegistry.getRegistry(host, 8888);
            catalogStub = (CatalogInterface) lookupRegistry.lookup("CatalogInterface");
            System.out.println("Found catalogstub!!");

            String name = "OrderInterface";
            Order orderServer = new Order();
            OrderInterface stub = (OrderInterface) UnicastRemoteObject.exportObject(orderServer, 0);

            Registry registry = LocateRegistry.createRegistry(8887);
            registry.bind(name, stub);
            System.out.println("Bound the orderserver");

        } catch (Exception e) {
            System.err.println("Exception");
            e.printStackTrace();
        }
    }

}