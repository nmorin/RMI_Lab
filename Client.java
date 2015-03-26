/* 
 * Nicole Morin and Megan Maher
 * Bowdoin Class of 2016
 * Distributed Systems: RMI Lab
 * 
 * Created: February 17, 2015
 * Last Modified: March 25, 2015
 *
 * Client class is so a user can access the front end server
 * and make requests without knowing about the catalog or 
 * order servers. In this, we locate and lookup the front end
 * server registry and stub, which we use to call the remote functions
 * that access the other servers. This class also parses and manages
 * input from the user from the command line. 
 */

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.io.*;

public class Client {
    private static final int NUM_QUERIES = 500;
    private static final int NUM_CLIENTS = 1;

    private static FrontEndServerInterface server;
    private static final int TEST_PRODUCT_NUMBER = 57471;
    private static long[] buyTimes;
    private static long[] lookTimes;
    private static String requestType;
    private static String queryType;

    private static String performLookUp(int number) {
        String answer = "";
        try {
            answer = server.lookUp(number);
        } catch (Exception e) {
            System.err.println("Exception in lookUp");
            e.printStackTrace();
        }
        return answer;
    }

    private static String performSearch(String searchTerm) {
        String answer = "";
        try {
            answer = server.search(searchTerm);
        } catch (Exception e) {
            System.err.println("Exception in search");
            e.printStackTrace();
        }
        return answer;
    }

    private static String performBuy(int number) {
        String answer = "";
        try {
            answer = server.buy(number);
        } catch (Exception e) {
            System.err.println("Exception in lookUp");
            e.printStackTrace();
        }
        return answer;
    }

    /* Used for the intensive query. Looks at all the times found
     * and calculates the average time found in nanoseconds. */
    private static void printAvgTimes(int numQueries) {
        if (requestType.equals("b")) {
            long buySum = (long)0.0;
            for (int i = 0; i < numQueries; i++) {
                buySum += buyTimes[i];
            }
            long buyAvg = buySum / (long)numQueries;
            System.out.println(buyAvg);
        } else {
            long lookSum = (long)0.0;
            for (int i = 0; i < numQueries; i++) {
                lookSum += lookTimes[i];
            }
            long lookAvg = lookSum / (long)numQueries;
            System.out.println(lookAvg);
        }
    }

    /* Used for testing, this method will perform the specified number of queries
    sequentially or simultaneously. */
    private static void intensiveQuery(int numQueries) {
        long startTime, endTime;
        lookTimes = new long[numQueries];
        buyTimes = new long[numQueries];
        System.out.println("Timing " + numQueries + " queries, beginning with lookUps");
        for (int i = 0; i < numQueries; i++) {
            if (requestType.equals("b")) {
                startTime = System.nanoTime();
                performBuy(TEST_PRODUCT_NUMBER);
                endTime = System.nanoTime();
                // System.out.println(endTime - startTime);
                buyTimes[i] = (endTime - startTime);// / (long)1000000.0; //divide by 1000000 to get milliseconds
            } else {
                startTime = System.nanoTime();
                performLookUp(TEST_PRODUCT_NUMBER);
                endTime = System.nanoTime();
                // System.out.println(endTime - startTime);
                lookTimes[i] = (endTime - startTime);// / (long)1000000.0; //divide by 1000000 to get milliseconds
            }
        }
        printAvgTimes(numQueries);
    }

    private static void concurrentQueries(int numClients) {
        for (int i = 0; i < numClients; i++) {
            if (requestType.equals("b")) {
                ConcurrentQuery qBuy = new ConcurrentQuery("buy");
                qBuy.start();
            } else {
                ConcurrentQuery qLook = new ConcurrentQuery("lookup");
                qLook.start();
            }
        }
    }

    private static void userInputQuery() {
        Scanner keyboard = new Scanner(System.in);

        /* Infinite loop that will query the user to input commands. To
        exit, type 'exit'. Commands will call the remote methods. */
        while (1 > 0) {
            System.out.println("Enter a request for the server: lookup (by number), buy (by number), or search (by topic)");
            String request = keyboard.nextLine();
            String parsedRequest[] = request.split(" ");
            String secondPartOfRequest = "";

            if (parsedRequest.length > 0 && parsedRequest.length != 1) {
                secondPartOfRequest = parsedRequest[1];
                for (int i = 2; i < parsedRequest.length; i++) {
                    secondPartOfRequest = secondPartOfRequest + " " + parsedRequest[i];
                }
            }
            
            // System.out.println("Second part of request: " + secondPartOfRequest);

            String answer = "";
            if (parsedRequest.length <= 1) {
                System.out.println("Not a valid request");
            }
            else if (parsedRequest[0].equals("lookup")) {
                // perform lookup function
                int number = 0;
                try {
                    number = Integer.parseInt(secondPartOfRequest);
                } catch (Exception e) {
                    System.out.println("Must have a proper item id number");
                }

                answer = performLookUp(number);

                try {
                    answer = server.lookUp(number);
                } catch (Exception e) {
                    System.err.println("Exception in lookUp");
                    e.printStackTrace();
                }
                if (answer == null) {
                    System.out.println("That's not a correct id number");
                }
                else {
                    System.out.println("Nile.com says: " + answer);
                }
            }
            else if (parsedRequest[0].equals("search")) {
                // perform search function
                answer = performSearch(secondPartOfRequest);
                System.out.println("Nile.com says: " + answer);
            }
            else if (parsedRequest[0].equals("buy")) {
                // perform buy function
                int number = 0;
                try {
                    number = Integer.parseInt(secondPartOfRequest);
                } catch (Exception e) {
                    System.out.println("Must have a proper item id number");
                }
                answer = performBuy(number);
                
                System.out.println("Nile.com says: " + answer);
            }
            else if (parsedRequest[0].equals("exit")) {
                System.out.println("Goodbye!");
                break;
            }
            else {
                System.out.println("Incorrect input");
            }
            System.out.println("\n");
        }   
    }

    public static void main(String args[]) {
        String host = (args.length < 1) ? "localhost" : args[0];
        queryType = (args.length < 2) ? "ui" : args[1];
        requestType = (args.length < 3) ? "b" : args[2];
        try {
            String name = "FrontEndServerInterface";
            Registry registry = LocateRegistry.getRegistry(host, 8886);
            server = (FrontEndServerInterface) registry.lookup(name);
            System.out.println("Bound the frontendserver!");

        } catch (Exception e) {
            System.err.println("Client exception:");
            e.printStackTrace();
        }

        if (queryType.equals("ui")) {
            System.out.println("User Input Queries: ");
            userInputQuery();
        } else {
            if (requestType.equals("b")) {
                System.out.print("Buy requests on ");
            } else if (requestType.equals("l")) {
                System.out.print("Lookup requests on ");
            } else {
                System.out.println("Invalid request type. Defaulting to buy on ");
                requestType = "b";
            }
            
            if (queryType.equals("iq")) {
                System.out.println("Intensive Queries: ");
                intensiveQuery(NUM_QUERIES);
            } else if (queryType.equals("cq")) {
                System.out.println("Concurrent Queries: ");
                concurrentQueries(NUM_CLIENTS);
            } else {
                System.out.println("INVALID QUERY TYPE. Defaulting to intensive queries.");
                intensiveQuery(NUM_QUERIES);
            }
        }
        
    } 

    /* Class used to run multiple concurrent threads, when calculating
     * the time it takes for each request. */
    private static class ConcurrentQuery extends Thread {
        private Thread t;
        private String requestType;
        // private static final int TEST_PRODUCT_NUMBER = 57471;
       
        ConcurrentQuery(String requestType_) {
            requestType = requestType_;
        }

        public void run() {
            // long startTime = (long)0.0, endTime = (long)0.0;
            try {
                intensiveQuery(NUM_QUERIES);

                // if (requestType.equals("buy")) {
                //     intensiveQuery(NUM_QUERIES);
                //     // startTime = System.nanoTime();
                //     // performBuy(TEST_PRODUCT_NUMBER);
                //     // endTime = System.nanoTime();
                // } else {

                //     startTime = System.nanoTime();
                //     performLookUp(TEST_PRODUCT_NUMBER);
                //     endTime = System.nanoTime();
                // }
            } catch (Exception e) {
                System.out.println("Thread interrupted.");
            }
            // long time = endTime - startTime;
            // System.out.println(time);
        }
       
        public void start()
        {
            if (t == null) {
                t = new Thread (this, requestType);
                t.start(); 
            }
        }
    }
}

