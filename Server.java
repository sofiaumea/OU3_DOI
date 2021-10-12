import java.net.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;
//org.json.simple.JSONObject.class;

/**
 * Server class that acts as the server that the clients can connect to
 * using the serversocket and its portnumber.
 *
 * @author Vilma Christensen (id20vcn), Sofia Leksell (id20sll)
 *
 * @since 2021-05-10
 */

public class Server {

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
 * @since 2021-05-10
 */


class MultipleClients extends Thread {
        final Socket socket;

        public MultipleClients(Socket socket) {
            this.socket = socket;

        }
        //connection=hur många sockets som skapats
        //uptime= hur länge servern vart igång
        @Override
        public void run() {
            //Här ska det göras något, dvs typ ansluta till servern
            //Vi vill läsa in vad klientens socket har att ge till servern och det är det som trådarna möjliggör.
            //Den vill ta in klientens outputstream och göra det till serverns inputstream.
            //Den ska kunna göra en HTTP Request och filerna som är accepterade är image, index osv.
            //Den ska ignorera det som inte är dessa filer

            //Servern kommer att köra en oändlig while-loop och skapa ny trådar för att skriva ut dem

            try {
                //Connect server to client socket
                PrintWriter output = new PrintWriter(socket.getOutputStream());
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String filename;
                String finishedFilename = "";
                String request = input.readLine();
                System.out.println(request);
                String contentType = "";
                //Get current date and time to put in header
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();


                //Treat a line without GET... as file not found
                //Create a object stringtokenizer to keep a token from the string in
                StringTokenizer keepToken = new StringTokenizer(request);
                //Vill göra om till en sybstring
                //Vi vill ta bort de fyra första characters
                //GET /textfile.txt
                try {
                    //Vill säkerställa att GET finns i requesten och att den är på första plats
                    if (keepToken.nextToken().equals("GET") && keepToken.hasMoreElements()) {
                        //Vi har säkerställt att GET är på första platsen
                        //Vi vill skapa en sträng av det ordet som kommer efter GET alltså
                        filename = keepToken.nextToken();
                    } else {
                        //Ändra exception
                        throw new FileNotFoundException();
                    }

                    if (filename.indexOf("/") == 0) {
                        //Removing first character in the string containing filename and save it as a new string
                        finishedFilename = filename.substring(1);

                    }

                    //If the opened file contains....
                    if (finishedFilename.endsWith("html")) {
                        contentType = "index/html";
                        //öppna filen som ligger i www/ och måste skriva in sökvägen. ska bara behöva skriva in ipadressen, inte mappen
                    }
                    if (finishedFilename.endsWith("png")) {
                        contentType = "image/png";
                        //öppna filen

                        //filenotfoundexpetion
                    }
                    if (finishedFilename.endsWith("txt")) {
                        contentType = "text/plain";
                        //Lika många bytes som antalet tecken
                        //Open file och se hur stor bytes det är på filen, finns i java, content-length
                    }
                    if (finishedFilename.endsWith("debug")) {
                        contentType = "application/json";
                        /*JSONObject jo = new JSONObject();
                        jo.put("name", "jon doe");
                        jo.put("age", "22");
                        jo.put("city", "chicago");*/
                        System.out.println("inside debug");
                        output.println("HTTP/1.1 200 OK\r\n" +
                                "Server: Sofia and Vilma's cool web server\r\n" +
                                "Date: " + dtf.format(now) + "\r\n" +
                                "Content-length: " + 10 + "\r\n" +
                                "Content-type: " + contentType + "\r\n" +
                                "");
                        //byt ut hej på dig mot applicationjson
                        String outputLine= "hej på dig";
                        output.println(outputLine);


                        output.flush();
                    }


                    //Storleken på datan som skickas,
                    output.println("HTTP/1.1 200 OK\r\n" +
                            "Server: Sofia and Vilma's cool web server\r\n" +
                            "Date: " + dtf.format(now) + "\r\n" +
                            "Content-length: " + 10 + "\r\n" +
                            "Content-type: " + contentType + "\r\n" +
                            "");


                    //Inte bara skriva ut application json som print utan skicka tillbaka till klienten
                } catch (FileNotFoundException notFound) {
                    System.out.println("HTTP/1.1 404 Not Found\r\n" +
                            "Server: Sofia and Vilma's cool web server\r\n" +
                            "Content-length: " + dtf.format(now) + "\r\n" +
                            "Content-length: " + contentType + "\r\n" +
                            "Content-type: " + contentType + "\r\n" +
                            "");
                    //Här kanske man ska hunna skriva in requesten igen så att den är på rätt sätt, dvs GET...
                }


            } catch (IOException ignored) {
                // Ignore
            }
        }
}







