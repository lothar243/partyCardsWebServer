package main.java;

/**
 * Created by Jeff on 4/26/2015.
 */
import javax.xml.ws.Endpoint;
import java.util.ArrayList;


//Endpoint publisher
public class PartyCardsServer {

    public static ArrayList<Card> whiteCards;
    public static ArrayList<Card> blackCards;
    public static void main(String[] args) {
        FileIO cardReader = new FileIO();
        whiteCards = cardReader.readCards(FileIO.WHITE_CARDS_FILENAME);
        blackCards = cardReader.readCards(FileIO.BLACK_CARDS_FILENAME);



        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        Endpoint.publish("http://192.168.1.2:52244/ws/partyCards", new PartyCardsInterfaceImpl());
    }

}