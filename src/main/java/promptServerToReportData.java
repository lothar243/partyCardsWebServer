package main.java;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.Inet4Address;
import java.net.URL;

// ====================================================================================================================
// promptServerToReportData.java
// --------------------------------------------------------------------------------------------------------------------
// Party Cards Server: Android Networking Project
// CSCI-466: Networks
// Jeff Arends, Lee Curran, Angela Gross, Andrew Meissner
// Spring 2015
// --------------------------------------------------------------------------------------------------------------------
// This is a separate file used to send some basic queries to the server. This file prompts the server to output
// info about its variables for debugging purposes only.
// =====================================================================================================================

public class promptServerToReportData 
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// MAIN METHOD
    public static void main(String[] args) throws Exception 
    {
        // determine the IP of this computer so it can talk to itself (insert insanity joke here)
        String serverIp = "";
        try
        {
            serverIp = Inet4Address.getLocalHost().getHostAddress();
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }


        URL url = new URL("http://" + serverIp + ":52244/ws/partyCards?wsdl");

        //1st argument service URI, refer to wsdl document above
        //2nd argument is service name, refer to wsdl document above
        QName qname = new QName("http://java.main/", "PartyCardsInterfaceImplService");

        Service partyCardsService = Service.create(url, qname);
        PartyCardsInterface myInterface = partyCardsService.getPort(PartyCardsInterface.class);

        myInterface.reportCurrentStatus();
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    // HELPER METHOD
    // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    
    public static String arrayToString(String[] input) 
    {
        String output = "";
        
        if(input.length > 0) 
        {
            output = input[0];
        }
        
        for(int i = 1; i < input.length; i++) 
        {
            output += ", " + input[i];
        }
        
        return output;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}