import java.io.*;
import java.net.*;
import java.util.*;

public class TwitterClient {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: ./client SERVER_HOST SERVER_PORT");
            System.exit(1);
        }

        String server_hostname = args[0];
        int server_port = Integer.parseInt(args[1]);

        TwitterClient tc = new TwitterClient(server_hostname, server_port);
        tc.startConnection();
    }

    private Socket s = null;
    private PrintWriter dataOut;
    private Scanner dataIn;

    public TwitterClient(String hostname, Integer port){
        try {
            s = new Socket(hostname, port);
            dataOut = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
            dataIn = new Scanner(s.getInputStream());
//            ClientRead cr = new ClientRead(s);
//            ClientWrite cw = new ClientWrite(s);
//            cr.start();
//            cw.start();
        }catch (IOException e){
            System.out.println("Unable toconnect ot server");
        }
    }


    private void startConnection(){
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                String msg = "";
                while (!msg.contains("Disconnect")) {

                    // read the message to deliver.
                    msg = new Scanner(System.in).nextLine();

//                    try {
                        // write on the output stream
                        dataOut.println(msg);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                String msg = "";
                while (!msg.contains("Disconnect")) {
//                    try {
                        // read the message sent to this client
                        msg = dataIn.nextLine();
                        System.out.println(msg);
//                    } catch (IOException e) {
//
//                        e.printStackTrace();
//                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }
//    private void startConnection(){
//        try {
//
//            Scanner input = new Scanner(s.getInputStream());
//            PrintWriter output = new PrintWriter(s.getOutputStream(), true);
//            Scanner userEntry = new Scanner(System.in);
//            String message;
//
//            while (true) {
//                if ((message = userEntry.nextLine()) != null){
//                    output.println(message);
//                    output.close();
//                }
//
//                System.out.println(input.nextLine());
//            }
//        }catch (IOException e){
//            System.out.println("Unable to start connection");
//        }
//    }
}
