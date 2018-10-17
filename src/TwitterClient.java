import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TwitterClient {

    // MAIN
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: ./client SERVER_HOST SERVER_PORT");
            System.exit(1);
        }

        String server_hostname = args[0];
        int server_port = Integer.parseInt(args[1]);
        // Twitter Client object
        TwitterClient tc = new TwitterClient(server_hostname, server_port);

        //method to start the client
        tc.startConnection();
    }

    // Client Socket
    private Socket s = null;
    // Printwriter to send data to server.
    private PrintWriter dataOut;
    // Scanner to read data from terminal
    private Scanner dataIn;


    // TwitterClient Constructor
    public TwitterClient(String hostname, Integer port){
        try {

            s = new Socket(hostname, port);
            dataOut = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
            dataIn = new Scanner(s.getInputStream());

        }catch (IOException e){
            // Unhandled Exception
        }
    }


    // method to initialize the two Threads that handle reading and writing to
    // the terminal console.
    private void startConnection(){
        // Start of a new Thread for sending messages written to the console
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                String msg = "";
//                while (!msg.contains("Disconnect")) {
                while(true){
                    // read the message to deliver.
                    msg = new Scanner(System.in).nextLine();
                    dataOut.println(msg);

                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                String msg = "";
//                while (!msg.contains("Disconnect")) {
                while (true){
                        msg = dataIn.nextLine();
                        System.out.println(msg);
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }

}
