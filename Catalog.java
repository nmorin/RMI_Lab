/* 
 * Nicole Morin and Megan Maher
 * Bowdoin Class of 2016
 * Distributed Systems: RMI Lab
 * 
 * Created: February 17, 2015
 * Last Modified: March 6, 2015
 *
 * Catalog class keeps track of the books for sale,
 * and periodically increases the quantity of stock for each
 * item every ten seconds by one. It handles queries based
 * on topic and item number, returning select details of the 
 * books searched for. It supports two types of requests - queries
 * for information and updates to the stock or price of specified
 * items. 
 */

import java.rmi.*;
import java.util.ArrayList;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

public class Catalog extends TimerTask implements CatalogInterface {

    private static ArrayList<Book> books;
    private static Catalog catalogServer;

    public Catalog() {
        initializeCatalog();
    }

    /* Override timertask run method */
    public void run() {
        System.out.println("All quantities increased by 1");
        for (Book book : books) {
            book.updateQuantity(1);
        }
    }

    private static void initializeCatalog() {
        books = new ArrayList<Book>();
        Book book1 = new Book("Achieving Less Bugs with More Hugs in CSCI 3325",
            "distributed systems", 57471, 6.66);
        Book book2 = new Book("Distributed Systems for Dummies", "distributed systems", 58574, 694.20);
        Book book3 = new Book("Surviving College", "college life", 12395, 14.92);
        Book book4 = new Book("Cooking for the Impatient Undergraduate", "college life", 13298, 36.50);

        books.add(book1);
        books.add(book2);
        books.add(book3);
        books.add(book4);

    }

    public String query(int itemNumber) {
        System.out.println("I'm performing a query on " + itemNumber);
        for (Book book : books) {
            if (book.getItemNumber() == itemNumber) { 
                return (book.getBookDetails());
            }
        }
        System.out.println("Number query");
        return null;
    }

    public String query(String topic) {
        System.out.println("I'm performing a query on " + topic);
        String result = "";
        for (Book book : books) {
            if (book.getTopic().equalsIgnoreCase(topic)) {
                result = result + "\n" + book.getTitleAndItemNum();
            }
        }
        if (result.equals("")) {
            result = "Not a valid topic";
        }
        System.out.println("Topic query");
        return result;
    }

    public void updateCost(int itemNumber, double newCost) {
        for (Book book : books) {
            if (book.getItemNumber() == itemNumber) {
                synchronized(this) {
                    book.setCost(newCost);
                }
            }
        }
    }

    public void updateQuantity(int itemNumber, int changeQuantity) {
        System.out.println("Updating quantity");
        for (Book book : books) {
            if (book.getItemNumber() == itemNumber) {
                System.out.println("match, original quanity = " + book.getQuantity());
                synchronized(this) {
                    book.updateQuantity(changeQuantity);
                }
                System.out.println("new quanity = " + book.getQuantity());
            }
        }
    }

    public static void main(String[] args) {
        // if (System.getSecurityManager() == null) {
        //     System.getSecurityManager(new SecurityManager());
        // }
        try {
            String name = "CatalogInterface";
            catalogServer = new Catalog();
            CatalogInterface stub = (CatalogInterface) UnicastRemoteObject.exportObject(catalogServer, 0);

            Registry registry = LocateRegistry.createRegistry(8888);
            registry.bind(name, stub);
            System.out.println("Bound the catalogserver");

        } catch (Exception e) {
            System.err.println("Exception");
            e.printStackTrace();
        }

        /* Periodically increases the stock of each item by 1 every ten seconds */
        Timer timer = new Timer();
        timer.schedule(catalogServer, 0, 10000);
    }

}