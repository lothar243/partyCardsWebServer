package main.java;

/**
 * Created by Jeff on 4/26/2015.
 */
import javax.xml.ws.Endpoint;
import java.net.Inet4Address;
import java.util.ArrayList;


//Endpoint publisher
public class PartyCardsServer {
    public static String serverIp = "192.168.1.2";

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

        final String DEBUG = "false";

        System.setProperty("javax.xml.bind.JAXBContext", "com.sun.xml.internal.bind.v2.ContextFactory");
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", DEBUG);
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", DEBUG);
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", DEBUG);
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", DEBUG);
        System.out.println("Publishing on " + "http://" + serverIp + ":52244/ws/partyCards");

        Endpoint.publish("http://" + serverIp + ":52244/ws/partyCards", new PartyCardsInterfaceImpl());
    }

}