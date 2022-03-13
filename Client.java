import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client class that connects to the server using the clientsocket with the same
 * portnumber as the server.
 *
 * @author Vilma Christensen (id20vcn), Sofia Leksell (id20sll)
 *
 * @since 2021-10-22
 */

public class Client {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Wrong input! Should be: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        String httpRequest = getHttpRequest();

        //Connect client socket to server socket
        try {
            Socket socketFromClient = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(socketFromClient.getOutputStream(), true);

            printHttpRequest(httpRequest, out);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socketFromClient.getInputStream()));

            getResponseFromServer(in);


        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }


    /**
     * Gets HTTP request from user
     *
     * @return HTTP request as string
     */
    private static String getHttpRequest() {
        Scanner getRequest = new Scanner(System.in);
        System.out.println("Make a request: ");
        String httpRequest = getRequest.nextLine();
        return httpRequest;
    }


    /**
     * Get the HTTP response from server and print
     *
     * @param in (reads the input stream from server)
     * @throws IOException
     */
    private static void getResponseFromServer(BufferedReader in) throws IOException {
        String fromServer;
        while((fromServer = in.readLine()) != null){
            System.out.println(fromServer);
        }
    }

    /**
     * Prints the HTTP request to the server
     *
     * @param httpRequest (the input from client)
     * @param out (the output to the server)
     */
    private static void printHttpRequest(String httpRequest, PrintWriter out) {
        out.println(httpRequest);

        out.println();
        out.flush();
    }
}