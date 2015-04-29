package main.java;



import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Collections;

//Service Implementation
@WebService(endpointInterface = "main.java.PartyCardsInterface")
public class PartyCardsInterfaceImpl implements PartyCardsInterface {
    private static final int HANDSIZE = 7;
    ArrayList<String> gameNames = new ArrayList<String>();
    ArrayList<ArrayList<String>> playerNames = new ArrayList<ArrayList<String>>(); // names of players in the above games
    ArrayList<Boolean> gameIsNew = new ArrayList<Boolean>(); // true during formation of game
    ArrayList<Boolean> gameIsActive = new ArrayList<Boolean>(); // when a game is destroyed (or won), this turns false
    int gameNumberIterator = 0;

    ArrayList<int []> gameWhiteDeck = new ArrayList<int[]>();
    ArrayList<Integer> currentWhiteIndex = new ArrayList<Integer>();
    ArrayList<int[]> gameBlackDeck = new ArrayList<int[]>();
    ArrayList<Integer> currentBlackIndex = new ArrayList<Integer>();

    ArrayList<ArrayList<ArrayList<Integer>>> playerHand = new ArrayList<ArrayList<ArrayList<Integer>>>(); // playerHand[gameId][playerId][cardNumber]



    @Override
    public Integer[] getGames() {
        reportCurrentStatus();
        ArrayList<Integer> newGames = new ArrayList<Integer>();
        for(int i = 0; i < gameNumberIterator; i++) {
            if(gameIsActive.get(i)) {
                newGames.add(i);
            }
        }
        return convertFromArrayList(newGames);
    }

    @Override
    public Integer[] getActiveGames() {
        ArrayList<Integer> newGames = new ArrayList<Integer>();
        for(int i = 0; i < newGames.size(); i++) {
            if(gameIsActive.get(i) && !gameIsNew.get(i)) {
                newGames.add(i);
            }
        }
        return convertFromArrayList(newGames);

    }

    @Override
    public int createNewGame(String gameName) {
        while(gameNames.contains(gameName)) {
            // error game name already exists, making it unique
            gameName = "" + gameNumberIterator + gameName;
        }
        gameNames.add(gameNumberIterator, gameName);
        playerNames.add(gameNumberIterator, new ArrayList<String>());
        gameIsNew.add(gameNumberIterator, true);
        gameIsActive.add(gameNumberIterator, true);
        gameWhiteDeck.add(gameNumberIterator, null);
        gameBlackDeck.add(gameNumberIterator, null);
        currentBlackIndex.add(gameNumberIterator, 0);
        currentWhiteIndex.add(gameNumberIterator, 0);
        playerHand.add(gameNumberIterator, null);

        gameNumberIterator++;

        return gameNumberIterator - 1;
    }

    @Override
    public int joinGame(int gameId, String userName) {

        if(gameId > gameNumberIterator - 1) {
            // gameId out of bounds
            return -1;
        }
        else if ( playerNames.get(gameId).contains(userName)) {
            return playerNames.get(gameId).indexOf(userName);
        }
        else if(!gameIsNew.get(gameId)) {
            // player hasn't joined and game has already started
            return -1;
        }
        else {
            playerNames.get(gameId).add(userName);
            return playerNames.get(gameId).size() - 1; // return the player's id in the game
        }

    }

    @Override
    public String[] listPlayers(int gameId) {
        if(gameId > gameNumberIterator - 1) {
            // error
            return null;
        }
        else {
            String [] output;
            if(playerNames.get(gameId).size() > 0) {
                output = new String[playerNames.get(gameId).size()];
                for (int i = 0; i < output.length; i++) {
                    output[i] = playerNames.get(gameId).get(i);
                }
            }
            else {
                output = null;
            }

            return output;
        }
    }

    @Override
    public boolean destroyGame(int gameId) {
        if(gameId > gameNumberIterator) {
            return false;
        }
        else {
            gameIsActive.set(gameId, false);
            return true;
        }
    }

    @Override
    public String getGameName(int gameId) {
        if(gameId > gameNumberIterator) {
            return "Invalid gameId";
        }
        return gameNames.get(gameId);
    }

    @Override
    public boolean gameIsForming(int gameId) {
        if(gameId > gameNumberIterator) {
            // error, gameId is out of bounds
            return false;
        }
        return gameIsNew.get(gameId);
    }

    @Override
    public boolean playerIsCardCzar(int gameId, int playerId) {
        return false;
    }

    @Override
    public int getTurnPhase(int gameId) {
        return 0;
    }

    @Override
    public String[] getHand(int gameId, int playerId) {
        return new String[0];
    }

    @Override
    public String getBlackCard(int gameId) {
        return null;
    }

    @Override
    public int chooseCard(int gameId, int playerId, int cardNumber) {
        return 0;
    }

    public Integer[] convertFromArrayList(ArrayList<Integer> before) {
        Integer [] after = new Integer[before.size()];
        System.out.println(before);
        for(int i = 0; i < after.length; i++) {
            after[i] = before.get(i);
            System.out.println("Game in session: " + after[i]);
        }
        return after;
    }

    public void reportCurrentStatus() {

        for(int gameId = 0; gameId < gameNumberIterator; gameId++) {
            System.out.println("Game " + gameId + ", name: " + gameNames.get(gameId));
            System.out.print("Players: ");
            for(int j = 0; j < playerNames.get(gameId).size(); j++) {
                System.out.print(playerNames.get(gameId).get(j) + ", ");
            }
            System.out.println();
            System.out.println("New: " + gameIsNew.get(gameId) + ", Active: " + gameIsActive.get(gameId));
            if(!gameIsNew.get(gameId)) {
                for(int playerNum = 0; playerNum < playerNames.get(gameId).size(); playerNum++) {
                    System.out.println("Player hand: " + playerHand.get(gameId).get(playerNum));
                }
            }
        }
    }

    public void startNewGame(int gameId) {
        if(!gameIsNew.get(gameId) || gameId >= gameNumberIterator) {
            return;
        }
        gameIsNew.set(gameId, false);
        int numberOfPlayers = playerNames.get(gameId).size();
        int whiteDeckIndexThisGame = 0;

        //shuffle white deck
        ArrayList<Integer> whiteDeck = new ArrayList<Integer>();
        int numberOfWhiteCards = PartyCardsServer.whiteCards.size();
        for(int i = 0; i < numberOfWhiteCards; i++) {
            whiteDeck.add(i);
        }
        Collections.shuffle(whiteDeck);
        // store the deck so it can be referred to later
        int[] whiteDeckArray = new int[whiteDeck.size()];
        for(int i = 0; i < whiteDeck.size(); i++) {
            whiteDeckArray[i] = whiteDeck.get(i);
        }
        gameWhiteDeck.set(gameId, whiteDeckArray);

        // shuffle black deck
        ArrayList<Integer> blackDeck = new ArrayList<Integer>();
        int numberOfBlackCards = PartyCardsServer.blackCards.size();
        for(int i = 0; i < numberOfBlackCards; i++) {
            blackDeck.add(i);
        }
        Collections.shuffle(blackDeck);
        // store the deck so it can be referred to later (first convert to an array)
        int[] blackDeckArray = new int[blackDeck.size()];
        for(int i = 0; i < blackDeck.size(); i++) {
            blackDeckArray[i] = blackDeck.get(i);
        }
        gameBlackDeck.set(gameId, blackDeckArray);

        //deal 7 white cards to each player
        playerHand.set(gameId, new ArrayList<ArrayList<Integer>>()); //
        for(int playerId = 0; playerId < numberOfPlayers; playerId++ ) {
            ArrayList<Integer> thisGamePlayerHand = new ArrayList<Integer>();
            for(int cardNum = 0; cardNum < HANDSIZE; cardNum++) {
                thisGamePlayerHand.add(whiteDeckArray[whiteDeckIndexThisGame++]);
            }
            playerHand.get(gameId).add(thisGamePlayerHand);
        }

        currentWhiteIndex.set(gameId, whiteDeckIndexThisGame);
        currentBlackIndex.set(gameId, 0);
    }
}