package main.java;

/**
 * This is the entry
 */
import javax.xml.ws.Endpoint;
import java.net.Inet4Address;
import java.util.ArrayList;


//Endpoint publisher
public class PartyCardsServer {
    /* when running the server, this IP address is automatically generated and output to the screen. Changing
    it in the code is only necessary if you wish to use the additional clients for debugging (promptServerToReportData
     */

    public static String serverIp;

    public static ArrayList<Card> whiteCards;
    public static ArrayList<Card> blackCards;
    public static void main(String[] args) {
        try {
            serverIp = Inet4Address.getLocalHost().getHostAddress();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        FileIO cardReader = new FileIO();
        whiteCards = cardReader.readCards(FileIO.WHITE_CARDS_FILENAME);
        blackCards = cardReader.readCards(FileIO.BLACK_CARDS_FILENAME);


        System.setProperty("javax.xml.bind.JAXBContext", "com.sun.xml.internal.bind.v2.ContextFactory");


        /* changing settings about whether or not to output debugging info. Set debug to true if you wish to see
        each incoming and outgoing xml file, along with other connection data.
         */
        final String DEBUG = "false";
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", DEBUG);
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", DEBUG);
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", DEBUG);
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", DEBUG);
        System.out.println("Publishing on " + "http://" + serverIp + ":52244/ws/partyCards");

        /* The next line establishes a thread that listens for incoming xml files. If the xml file contains the
        proper function name and arguments (function name, arg0, arg1, etc) it will run the corresponding function in
        PartyCardsInterfaceImpl and generate an xml file from the result. It will then send the result back to the
        source of the original request.
         */
        Endpoint.publish("http://" + serverIp + ":52244/ws/partyCards", new PartyCardsInterfaceImpl());
    }

}