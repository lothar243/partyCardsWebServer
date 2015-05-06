package main.java;

/**
 * This InGameData is nothing more than a data type used to pass information about the current state of the game
 * that a player is already playing. It contains data such as the players current hand, the black card, and whether
 * or not the player is currently the card czar.
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
