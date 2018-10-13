import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TwitterServer {

    private ServerSocket ss;

    // HashmMap initialized to String keys and ArrayList<Int> values
    //  The Key is the hashtag
    // The value is an arrayList of socket port numbers of the clients.
    private HashMap<String, ArrayList<Integer>> subscriptionsMap = new HashMap<>();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: ./server LISTEN_PORT");
            System.exit(1);
        }
        // port for the server
        Integer port = Integer.parseInt(args[0]);
        TwitterServer TS = new TwitterServer(port);

        TS.startConnection();
    }

    public TwitterServer(Integer port) {
//        this.listen_port = listen_port;
        try {
            ss = new ServerSocket(port);
        } catch (IOException ioEx) {
            System.out.println("unable to create ServerSocket\n");
            System.exit(1);
        }
    }



    public void startConnection(){
        try {
            while (true) {
                Socket client = ss.accept();
                System.out.println("Connection form client accepted");
                // create the serverThread for this client
                ServerThread serverThread = new ServerThread(client,this);
                serverThread.start();

            }
        } catch (IOException ioEx) {
            // If connection error print warning
            System.out.println("unable to connect\n");
            System.exit(1);
        }
    }




    public synchronized void addSubscriber(String hashtag, Integer client){
        System.out.println(subscriptionsMap);
        // get the list of Sockets that are subscribed to the hasthag
        ArrayList<Integer> clientList = subscriptionsMap.get(hashtag);

        // if the list is empty nobody is subscribed.
        if(clientList == null){
            // create a new arrayList
            clientList = new ArrayList<>();
            // add the first client to the list
            clientList.add(client);
            // add the subscriber of hashtag into map.
            subscriptionsMap.put(hashtag,clientList);
        }
        else{
            // if clientlist of hashtag exists but the client is not in it
            if(!clientList.contains(client)){
                // add this client in the list
                clientList.add(client);
            }
        }
        System.out.println(subscriptionsMap);

    }
    public synchronized void removeSubscriber(String hashtag, Socket client){

    }
}


