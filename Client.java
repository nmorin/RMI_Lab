import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

// What assumptions do we make? 

/* Client class is so a user can access the front end server
and make requests without knowing about the catalog or 
order servers. In this, we locate and lookup the front end
server registry and stub, which we use to call the remote functions
that access the other servers. This class also parses and manages
input from the user from the command line. */
public class Client {

    private static final String QUERY_TYPE = "INTENSIVE"; // or "USER"
    private static FrontEndServerInterface server;
    private static final int TEST_PRODUCT_NUMBER = 57471;
    private static long[] buyTimes;
    private static long[] lookTimes;

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

    private static void printAvgTimes(int numQueries) {
        long buySum = (long)0.0;
        long lookSum = (long)0.0;

        for (int i = 0; i < numQueries; i++) {
            buySum += buyTimes[i];
            lookSum += lookTimes[i];
        }

        long buyAvg = buySum / (long)numQueries;
        long lookAvg = lookSum / (long)numQueries;

        System.out.println("BUY AVG TIME: "+buyAvg);
        System.out.println("LOOKUP AVG TIME: "+lookAvg);
    }

    /* Used for testing, this method will perform the specified number of queries
    sequentially or simultaneously. */
    private static void intensiveQuery(int numQueries) {
        long startTimeLook, startTimeBuy, endTimeLook, endTimeBuy, durationLook, durationBuy;
        lookTimes = new long[numQueries];
        buyTimes = new long[numQueries];
        
        System.out.println("Timing " + numQueries + " queries, beginning with lookUps");
        for (int i = 0; i < numQueries; i++) {
            try {
                startTimeLook = System.nanoTime();
                server.lookUp(TEST_PRODUCT_NUMBER);
                endTimeLook = System.nanoTime();

                startTimeBuy = System.nanoTime();
                server.buy(TEST_PRODUCT_NUMBER);
                endTimeBuy = System.nanoTime();

                lookTimes[i] = (endTimeLook - startTimeLook) / (long)1000000.0; //divide by 1000000 to get milliseconds
                buyTimes[i] = (endTimeBuy - startTimeBuy) / (long)1000000.0; //divide by 1000000 to get milliseconds
            } catch (Exception e) {
                System.err.println("Exception in query");
                e.printStackTrace();
            }
        }

        printAvgTimes(numQueries);
    }

    private static void concurrentQueries(int numQueries) {



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
        try {
            String name = "FrontEndServerInterface";
            Registry registry = LocateRegistry.getRegistry(host, 8886);
            server = (FrontEndServerInterface) registry.lookup(name);
            System.out.println("Bound the frontendserver!");

        } catch (Exception e) {
            System.err.println("Client exception:");
            e.printStackTrace();
        }

        if (QUERY_TYPE.equals("USER")) {
            userInputQuery();
        } else {
            intensiveQuery(500);
        }
        
    } 
}


class ConcurrentQuery extends Thread {
   private Thread t;
   private String threadName;
   
   ConcurrentQuery(String name){
       threadName = name;
       System.out.println("Creating " +  threadName );
   }

   public void run() {
      try {
         for(int i = 4; i > 0; i--) {
            System.out.println("Thread: " + threadName + ", " + i);
            // Let the thread sleep for a while.
            Thread.sleep(50);
         }
     } catch (InterruptedException e) {
         System.out.println("Thread " +  threadName + " interrupted.");
     }
     System.out.println("Thread " +  threadName + " exiting.");
   }
   
   public void start()
   {
      if (t == null) {
         t = new Thread (this, threadName);
         t.start(); 
     }
   }

}