package main.java;

/**
 * Created by Jeff on 4/26/2015.
 */

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

public class promptServerToReportData {

    public static void main(String[] args) throws Exception {

        URL url = new URL("http://184.166.76.115:52244/ws/partyCards?wsdl");

        //1st argument service URI, refer to wsdl document above
        //2nd argument is service name, refer to wsdl document above
        QName qname = new QName("http://java.main/", "PartyCardsInterfaceImplService");


        Service partyCardsService = Service.create(url, qname);
        PartyCardsInterface myInterface = partyCardsService.getPort(PartyCardsInterface.class);


//        System.out.println("player 0 hand: " + arrayToString(myInterface.getHand(0, 0)));
//        System.out.println("player 1 hand: " + arrayToString(myInterface.getHand(0, 1)));
        myInterface.reportCurrentStatus();


    }
    public static String arrayToString(String[] input) {
        String output = "";
        if(input.length > 0) {
            output = input[0];
        }
        for(int i = 1; i < input.length; i++) {
            output += ", " + input[i];
        }
        return output;
    }
}