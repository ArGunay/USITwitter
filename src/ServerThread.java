import java.io.*;
import java.net.*;
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
        try {
            Scanner input = new Scanner(client.getInputStream());
            String message = parseMessage(input.nextLine());


//            while(!message.equals("disconnect")){
            // continue reading
              while(!message.contains("ENDCONNECTION")){
                // server always prints incoming messages
                  if(!message.equals("")){
                      System.out.println(message);
                  }
//                twitterServer.addSubscriber(message,client.getPort());
//                output.println("Message " + numMessages + ": " + message); // Step 4
                message = parseMessage(input.nextLine());
            }
            input.close();
            disconnectClient();
        }catch (IOException IOex){
            System.out.println("Client error");
        }
    }

    private String parseMessage(String message){
        String parsedMessage = "";
        String[] words = message.split(" ");
//        System.out.println(Arrays.toString(words));
        if (words[0].equals("s")){
            String hashtag = words[1];
            twitterServer.addSubscriber(hashtag, this.client.getPort());

        }else if(words[0].equals("u")){
            String hashtag = words[1];
            twitterServer.removeSubscriber(hashtag, this.client.getPort());
        }
        else if(words[0].equals("t")){
            String[] newMess = Arrays.copyOfRange(words,1,words.length);
            for(String s : newMess){
                parsedMessage = parsedMessage + " " + s;
            }

            broadcastMessage(newMess, parsedMessage.trim());
//            System.out.println("FATTOSTOCOSO");
        }

        return parsedMessage.trim();
    }

    public void disconnectClient(){
        try {
            client.close();

        }catch (IOException e){
            System.out.println("Unable To disconnect Client\n");
            System.exit(1);
        }

    }

    public void broadcastMessage(String[] message,String parsedMess){
        Set<Integer> clientsSet = new HashSet<>();

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
        }

        Iterator iterator = clientsSet.iterator();
        while(iterator.hasNext()){
            Integer sendToClient = (Integer) iterator.next();
            Socket clientToSend = twitterServer.getClientSocket(sendToClient);
            try {
                PrintWriter output = new PrintWriter(clientToSend.getOutputStream(),true);
                output.println(parsedMess);
//                output.close();

            }catch (IOException e){
                System.out.println("UNABLE TO SEND TWEET");
            }
        }

    }
}