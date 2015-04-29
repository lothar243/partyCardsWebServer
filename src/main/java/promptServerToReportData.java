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


        myInterface.reportCurrentStatus();


    }

}