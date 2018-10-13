public class TwitterClient {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: ./client SERVER_HOST SERVER_PORT");
            System.exit(1);
        }

        String server_hostname = args[0];
        int server_port = Integer.parseInt(args[1]);
    }
}
