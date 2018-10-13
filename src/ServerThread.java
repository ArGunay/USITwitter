import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

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
            String message = input.nextLine();


            while(!message.equals("disconnect")){
                // server always prints incoming messages
                System.out.println(message);

                twitterServer.addSubscriber(message,client.getPort());


//                output.println("Message " + numMessages + ": " + message); // Step 4
                message = input.nextLine();
            }
            input.close();
            disconnectClient();
        }catch (IOException IOex){
            System.out.println("Client error");
        }
    }

    private void parseString(String message){

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