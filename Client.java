import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Client class that connects to the server using the clientsocket with the same
 * portnumber as the server.
 *
 * @author Vilma Christensen (id20vcn), Sofia Leksell (id20sll)
 *
 * @since 2021-05-10
 */

public class Client {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(
                    "Wrong input! Should be: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        //Connect client socket to server socket
        try {
                Socket socketFromClient = new Socket(hostName, portNumber);
                //Turns bytes to text from server
                PrintWriter out = new PrintWriter(socketFromClient.getOutputStream(), true);
                //Takes bytes from client to send to server
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socketFromClient.getInputStream()));


            // object of scanner class
            Scanner sc = new Scanner(System.in);
            String line;

            while ((line = in.readLine()) != null)
                // reading from user
                line = sc.nextLine();

                // sending the user input to server
                out.println(line);
                out.flush();

                // displaying server reply
                System.out.println("Server replied "
                        + in.readLine());

            // closing the scanner object
            sc.close();

            /*BufferedReader stdIn =
                    new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;

            //tar in headern och skriver ut headern här i klientklassen
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye."))
                    break;
                //Här kan vår request skrivas ut
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
            }*/

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}



