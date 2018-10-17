import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

class ServerThread extends Thread{
    private Socket client;
    private TwitterServer twitterServer;

    public ServerThread(Socket client, TwitterServer twitterServer){
        this.client = client;
        this.twitterServer = twitterServer;
    }

    @Override
    public void run() {
        Scanner input =null;
        try {
            input = new Scanner(client.getInputStream());
            String message = parseMessage(input.nextLine());


            // continue reading
//              while(!message.contains("ENDCONNECTION")){
                // server always prints incoming messages
            while(true){
                  if(!message.equals("")){
                      System.out.println(message);
                  }
                message = parseMessage(input.nextLine());
            }

        }catch (Exception IOex){

            disconnectClient(input);
        }
    }


    // parsing the message that has been received by the client
    private String parseMessage(String message){
        String parsedMessage = "";
        String[] words = message.split(" ");

        // Handling subscription to hashtag
        if (words[0].equals("SUBSCRIBE")){
            // Hashtags are words that begin with # character.
            // Words without it are not taken in consideration as hashtags
            if(words[1].contains("#")) {
                String hashtag = words[1];
                twitterServer.addSubscriber(hashtag, this.client.getPort());
            }

        // handling unsubscription
        }else if(words[0].equals("UNSUBSCRIBE")){
            String hashtag = words[1];
            twitterServer.removeSubscriber(hashtag, this.client.getPort());
        }

        // handling tweeting
        else if(words[0].equals("TWEET")){
            String[] newMess = Arrays.copyOfRange(words,1,words.length);
            for(String s : newMess){
                parsedMessage = parsedMessage + " " + s;
            }

            broadcastMessage(newMess, parsedMessage.trim());
        }

        return parsedMessage.trim();
    }

    // Disconnecting the client.
    public void disconnectClient(Scanner input){
        try {
            input.close();
            client.close();

        }catch (IOException e){
            // unhandled exception.
//            System.exit(1);
        }

    }


    // Method for the bradcast of the message to the clients.
    public void broadcastMessage(String[] message,String parsedMess){
        // Set to collect the clients subscribed to the hashtags in the message.
        Set<Integer> clientsSet = new HashSet<>();

        // number port of the client in order to avoid sending the message to itself
        Integer sendigClient = this.client.getPort();

        try {

            HashMap<String, ArrayList<Integer>> subs = twitterServer.getSubscriptionMap();
            //for each word in the tweet
            for (String word : message) {
                // if the word is a tag
                if (word.contains("#")) {
                    // get the clients that are subscribed to that tag
                    ArrayList<Integer> ss = subs.get(word);
                    for (int i = 0; i < ss.size(); i++) {
                        // but not the client that is sending the tweet
                        if (!ss.get(i).equals(sendigClient)) {
                            // and add it to the set of those that will receive it
                            clientsSet.add(ss.get(i));
                        }
                    }
                }
            }
        }catch (NullPointerException e){
            // unhandeled exc
        }


        Iterator iterator = clientsSet.iterator();

        while(iterator.hasNext()){
            Integer sendToClient = (Integer) iterator.next();
            Socket clientToSend = twitterServer.getClientSocket(sendToClient);
            try {
                PrintWriter output = new PrintWriter(clientToSend.getOutputStream(),true);
                output.println(parsedMess);

            }catch (IOException e){
                System.out.println("UNABLE TO SEND TWEET");
            }
        }

    }
}