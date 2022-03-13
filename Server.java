import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

/**
 * Server class that acts as the server that the clients can connect to
 * using the serversocket and its portnumber.
 *
 * @author Vilma Christensen (id20vcn), Sofia Leksell (id20sll)
 *
 * @since 2021-10-22
 */

public class Server {

    /**
     * Creates a server socket and accepts client. For every new client, a new thread is created.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1991);

        while (true) {
            try {
                //The server socket accept connection from client
                Socket connect = serverSocket.accept();
                //Create a thread to run the client in
                Thread t = new MultipleClients(connect);
                t.start();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}

/**
 * MultipleClients class that uses multiple threads to enable the server
 * to communicate with clients parallel.
 *
 * @author Vilma Christensen (id20vcn), Sofia Leksell (id20sll)
 *
 * @since 2021-10-22
 */


class MultipleClients extends Thread {
    final Socket socket;
    public static int count=0;

    public MultipleClients(Socket socket) {
        this.socket = socket;
        count++;

    }

    /**
     * Enables the server to interact with client
     */
    @Override
    public void run() {

        try {
            //Connect server to client socket
            PrintWriter output = new PrintWriter(socket.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String filename;
            String contentType = "";
            String request = getRequest(input);

            //Get the time for which the server has been running
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            RuntimeMXBean uptime = ManagementFactory.getRuntimeMXBean();

            StringTokenizer keepToken = getStringTokenizer(request);

            try {
                filename = saveFilenameAsToken(keepToken);

                File fileSearcher = new File("www/" + filename);

                //Ignores if file exists
                if(ifFileNotFoundThrowException(fileSearcher) && !filename.endsWith("debug")){
                    print404NotFound(output, dtf, now);
                }

                if ((filename.endsWith("html") || filename.endsWith("png") || filename.endsWith("txt")) &&
                        !ifFileNotFoundThrowException(fileSearcher)) {
                    if (filename.endsWith("html")) {
                        contentType = "text/html";
                    } else if (filename.endsWith("png")) {
                        contentType = "image/png";
                    } else if (filename.endsWith("txt")) {
                        contentType = "text/plain";
                    }
                    printHttpResponse(output, contentType, dtf, now, fileSearcher);
                    fileToBytesAndWrite(fileSearcher);
                }

                if (filename.endsWith("debug")) {
                    contentType = "application/json";
                    printOutJson(output, contentType, dtf, now, uptime);
                }

            } catch (IllegalArgumentException argumentException) {
                print404NotFound(output, dtf, now);
            }

        } catch (IOException ignored) {
            System.out.println("Could not complete");
        }

        try {
            System.out.println("Socket is being closed");
            socket.close();
        } catch(IOException e){
                e.printStackTrace();
        }
    }

    /**
     * Reads input from client to get HTTP-request
     *
     * @param input (get request from the client)
     * @return the request as a string
     * @throws IOException
     */
    private String getRequest(BufferedReader input) throws IOException {
        String request = input.readLine();
        System.out.println("Request from client: " + request);
        return request;
    }

    /**
     * The first word in the get-request saves as a token
     *
     * @param request (the HTTP-request as a string)
     * @return the first word from the string
     */
    private StringTokenizer getStringTokenizer(String request) {
        StringTokenizer keepToken;
        if(request != null) {
            keepToken = new StringTokenizer(request);
        } else {
            throw new NullPointerException();
        }
        return keepToken;
    }

    /**
     * Get filename from the get-request
     *
     * @param keepToken (a string of tokens of the request)
     * @return next token in the string, a file name
     */
    private String saveFilenameAsToken(StringTokenizer keepToken) {
        String filename;
        if (keepToken.nextToken().equals("GET") && keepToken.hasMoreElements()) {
            filename = keepToken.nextToken();
        } else {
            throw new IllegalArgumentException();
        }
        return filename;
    }

    /**
     * Prints out Json message
     *
     * @param output (the output to the client)
     * @param contentType (content type of the file)
     * @param dtf (object to print date and time)
     * @param now (get current date and time)
     * @param uptime (how long the server has been running)
     */
    private void printOutJson(PrintWriter output, String contentType, DateTimeFormatter dtf, LocalDateTime now, RuntimeMXBean uptime) {
        String outputLine= "{\n" +
                "  \"name\": \"Vilma and Sofia's cool webserver\",\n" +
                "  \"connections\": " +count+ ",\n"+
                "  \"uptime\": " + uptime.getUptime()+ ",\n"+
                "  \"owners\": [\"Sofia Leksell (id20sll@cs.umu.se)\", \"Vilma Christensen (id20vcn@cs.umu.se)\"]\n" +
                "}";

        int length = outputLine.length();
        output.println("HTTP/1.1 200 OK\r\n" +
                "Server: Sofia and Vilma's cool web server\r\n" +
                "Date: " + dtf.format(now) + "\r\n" +
                "Content-length: " + length + "\r\n" +
                "Content-type: " + contentType + "\r\n" +
                "");
        output.println(outputLine);
        output.flush();
    }

    /**
     * Prints out the HTTP-response
     *
     * @param output (the output to the client)
     * @param contentType (content type of the file)
     * @param dtf (object to print date and time)
     * @param now (get current date and time)
     * @param fileSearcher (the file with path)
     */
    private void printHttpResponse(PrintWriter output, String contentType, DateTimeFormatter dtf, LocalDateTime now, File fileSearcher) {
        String outputLine = ("HTTP/1.1 200 OK\r\n" +
                "Server: Sofia and Vilma's cool web server\r\n" +
                "Date: " + dtf.format(now) + "\r\n" +
                "Content-length: " + fileSearcher.length() + "\r\n" +
                "Content-type: " + contentType + "\r\n" +
                "");
        output.println(outputLine);
        output.flush();
    }

    /**
     * Converts the file to bytes and write to output stream
     *
     * @param f (a file)
     * @throws IOException
     */
    private void fileToBytesAndWrite(File f) throws IOException {
        int lengthOfFile = (int) f.length();
        byte[] byteFile = new byte [lengthOfFile];

        DataInputStream dataIn = new DataInputStream(new FileInputStream(f));
        dataIn.readFully(byteFile);

        BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
        outputStream.write(byteFile, 0, lengthOfFile);
        outputStream.flush();
    }

    /**
     * Checks if file exists
     *
     * @param filename (a file)
     * @return true if file does not exist, otherwise false
     * @throws IllegalArgumentException
     */
    private boolean ifFileNotFoundThrowException(File filename) throws IllegalArgumentException {
        if(!filename.exists()){
            return true;
        }
        return false;
    }

    /**
     * Prints out the HTTP-response 404 Not Found
     *
     * @param output (the output to the client)
     * @param dtf (object to print date and time)
     * @param now (get current date and time)
     */
    private void print404NotFound(PrintWriter output, DateTimeFormatter dtf, LocalDateTime now) {
        String print = ("HTTP/1.1 404 Not Found\r\n" +
                "Server: Sofia and Vilma's cool web server\r\n" +
                "Date: " + dtf.format(now) + "\r\n" +
                "Content-length: 0 \r\n" +
                "Content-type: text/plain \r\n");
        output.println(print);
        output.flush();
    }
}







