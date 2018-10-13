import java.io.IOException;
import java.net.Socket;
import java.util.*;

class ServerThread extends Thread{
    private Socket client;
    private TwitterServer twitterServer;
    private ArrayList<String> myHashtags;

    public ServerThread(Socket client, TwitterServer twitterServer){
        this.client = client;
        this.twitterServer = twitterServer;
    }

    @Override
    public void run() {
        try {
            Scanner input = new Scanner(client.getInputStream());
            String message = parseString(input.nextLine());


//            while(!message.equals("disconnect")){
            // continue reading
              while(!message.contains("END")){
                // server always prints incoming messages
                  if(!message.equals("")){
                      System.out.println(message);
                  }

//                twitterServer.addSubscriber(message,client.getPort());
//                output.println("Message " + numMessages + ": " + message); // Step 4
                message = parseString(input.nextLine());
            }
            input.close();
            disconnectClient();
        }catch (IOException IOex){
            System.out.println("Client error");
        }
    }

    private String parseString(String message){
        String parsedMessage = "";
        String[] words = message.split(" ");
//        System.out.println(Arrays.toString(words));
        if (words[0].equals("SUBSCRIBE")){
            String hashtag = words[1];
            twitterServer.addSubscriber(hashtag,client.getPort());

        }else if(words[0].equals("UNSUBSCRIBE")){
            String hashtag = words[1];
            twitterServer.removeSubscriber(hashtag,client.getPort());
        }
        else if(words[0].equals("TWEET")){
            String[] newMess = Arrays.copyOfRange(words,1,words.length);
            for(String s : newMess){
                parsedMessage = parsedMessage + " " + s;
            }
            System.out.println("FATTOSTOCOSO");
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
}