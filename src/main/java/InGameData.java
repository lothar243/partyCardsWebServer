package main.java;

/**
 * Created by Jeff on 5/2/2015.
 */
public class InGameData {
    public String blackCard;
    public String roundText;
    public int playerId;
    public int playerIsCardCzar;
    public int turnPhase;
    public int turnNumber;
    public int numberOfPlayersChoosing;
    public String [] hand;

    public String toString() {
        final String DELIM = ",";
        return "summary: " + roundText +
                ", PID: " + playerId +
                ", CCz " + playerIsCardCzar +
                ", Turn: " + turnNumber + " - " + turnPhase +
                ", Black: " + blackCard +
                ", hand: " + arrayToString(hand);
    }
    private String arrayToString(String [] input) {
        if(input == null) {
            return "";
        }
        String output = "0 " + input[0];
        for(int i = 1; i < input.length; i++) {
            output += ", " +  i + " " + input[i];
        }
        return output;
    }

}