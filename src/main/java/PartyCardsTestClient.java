package main.java;


import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

public class PartyCardsTestClient {

    public static PartyCardsInterface myInterface;

    public static void main(String[] args) throws Exception {

        URL url = new URL("http://" + PartyCardsServer.serverIp + ":52244/ws/partyCards?wsdl");

        //1st argument service URI, refer to wsdl document above
        //2nd argument is service name, refer to wsdl document above
        QName qname = new QName("http://java.main/", "PartyCardsInterfaceImplService");


        Service partyCardsService = Service.create(url, qname);
        myInterface = partyCardsService.getPort(PartyCardsInterface.class);

        System.out.println("create new game - gameId: " + myInterface.createNewGame("myGame"));
        System.out.println("Joining game - player position: " + myInterface.joinGame(0, "jeff"));
        System.out.println("Joining game - player position: " + myInterface.joinGame(0, "bob"));
        System.out.println("Joining game - player position: " + myInterface.joinGame(0, "three"));
        System.out.println("Joining game - player position: " + myInterface.joinGame(0, "four"));
        System.out.println("list players in game 1: " + arrayToString(myInterface.listPlayers(1)));
        myInterface.startNewGame(0);
        reportGameStatus();
        System.out.println("Action: player 1 chooses card 1, number of cards remaining: " + myInterface.chooseCard(0, 1, 1));
        reportGameStatus();
        System.out.println("Action: player 2 chooses card 2, number of cards remaining: " + myInterface.chooseCard(0, 2, 2));
        reportGameStatus();
        System.out.println("Action: player 3 chooses card 3, number of cards remaining: " + myInterface.chooseCard(0, 3, 3));
        reportGameStatus();
        System.out.println("Action: player 0 chooses card 0, number of cards remaining: " + myInterface.chooseCard(0, 0, 0));
        reportGameStatus();
        System.out.println("Action: player 0 chooses card 2, number of cards remaining: " + myInterface.chooseCard(0, 0, 2));
        reportGameStatus();
        System.out.println("Action: player 2 chooses card 2, number of cards remaining: " + myInterface.chooseCard(0, 2, 2));
        reportGameStatus();
        System.out.println("Action: player 3 chooses card 2, number of cards remaining: " + myInterface.chooseCard(0, 3, 2));
        reportGameStatus();
        System.out.println("Action: player 1 chooses card 0, number of cards remaining: " + myInterface.chooseCard(0, 1, 0));
        reportGameStatus();

        myInterface.getBasicGameData();



    }

    public static void reportGameStatus() {
        System.out.println("\n ----");
        myInterface.reportCurrentStatus();
        System.out.println("player 0: " + myInterface.getGameData(0, 0).toString());
        System.out.println("player 1: " + myInterface.getGameData(0, 1).toString());
        System.out.println("player 2: " + myInterface.getGameData(0, 2).toString());
        System.out.println("player 3: " + myInterface.getGameData(0, 3).toString());
    }


    public static void reportGameStatus2() {
        myInterface.reportCurrentStatus();
        System.out.println("--report--\n" + arrayToString(myInterface.roundSummary(0)));
        if(myInterface.playerIsCardCzar(0,0) == 1) {
            System.out.print("Player 0");
        }
        else {
            System.out.print("Player 1");
        }
        System.out.println(" is the current czar");
        System.out.println("player 0 hand " +arrayToString(myInterface.getHand(0,0)));
        System.out.println("player 1 hand " +arrayToString(myInterface.getHand(0, 1)));
    }

    public static String arrayToString(String[] input) {
        String output = "";
        if(input.length > 0) {
            output = input[0];
        }
        for(int i = 1; i < input.length; i++) {
            output += ", " + i + " " + input[i];
        }
        return output;
    }
    public static String arrayToString(Integer[] input) {
        String output = "";
        if(input.length > 0) {
            output = input[0].toString();
        }
        for(int i = 1; i < input.length; i++) {
            output += ", " + input[i];
        }
        return output;
    }
}