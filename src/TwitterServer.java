import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class TwitterServer {

    private ServerSocket ss;

    // HashmMap initialized to String keys and ArrayList<Int> values
    // The Key is the hashtag
    // The value is an arrayList of sockets port number of the clients.
    private HashMap<String, ArrayList<Integer>> subscriptionsMap = new HashMap<>();

    // Hashmap that identifies the socket with its port number
    // used to retrieve the client socket and send the tweet
    private HashMap<Integer, Socket> allClients = new HashMap<>();


    // MAIN METHOD
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: ./server LISTEN_PORT");
            System.exit(1);
        }
        // port for the server
        Integer port = Integer.parseInt(args[0]);
        // Initialize TwitterServer object
        TwitterServer TS = new TwitterServer(port);
        //Execute method that handles connections
        TS.startConnection();
    }

    //Constructor
    public TwitterServer(Integer port) {
        try {
            // Create ServerSocket at the given port
            ss = new ServerSocket(port);

        } catch (IOException ioEx) {
            //DoNothing
        }
    }

    // method to handle the connections.
    // It accepts an incoming connection and hands it over to a new thread
    public void startConnection(){
        try {
            // infinite loop to continuously accept incoming connections
            while (true) {
                //create new client connection
                Socket client = ss.accept();
                // put it in the allClients Hashmap
                allClients.put(client.getPort(),client);
                // create the serverThread for this client
                ServerThread serverThread = new ServerThread(client,this);
                // Start the server Thread.
                serverThread.start();

            }
        } catch (IOException ioEx) {
            // Connection error Exception
        }
    }

    // Synchronized method to retrieve the clients
    public synchronized Socket getClientSocket(Integer portNumber){
        return allClients.get(portNumber);
    }

    // Synchronized method to add a new subscriber
    public synchronized void addSubscriber(String hashtag, Integer client){
        // get the list of Sockets that are subscribed to the hashtag
        ArrayList<Integer> clientList = subscriptionsMap.get(hashtag);

        // if the list is empty nobody is subscribed.
        if(clientList == null){
            // create a new arrayList
            clientList = new ArrayList<>();
            // add the first client to the list
            clientList.add(client);
            // add the subscriber of hashtag into map.
            subscriptionsMap.put(hashtag, clientList);
        }
        else{
            // if clientlist of hashtag exists but the client is not in it
            if(!clientList.contains(client)){
                // add this client in the list
                clientList.add(client);
            }
        }

    }

    // Getter method to retrieve the Hashmap holding the subscribers
    public synchronized HashMap<String, ArrayList<Integer>> getSubscriptionMap(){
        return subscriptionsMap;
    }

    // Method to remove subscirber
    public synchronized void removeSubscriber(String hashtag, Integer client){
        if(subscriptionsMap.containsKey(hashtag)){
            if(subscriptionsMap.get(hashtag).contains(client)){
                subscriptionsMap.get(hashtag).remove(client);
            }
        }
    }
}


