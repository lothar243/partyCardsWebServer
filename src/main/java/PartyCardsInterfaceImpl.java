package main.java;




import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Collections;

//Service Implementation
@WebService(endpointInterface = "main.java.PartyCardsInterface")
public class PartyCardsInterfaceImpl implements PartyCardsInterface {
    public static final int NO_CARD_SELECTED = -1;

    private static final int HANDSIZE = 7;
    private static final int MAX_SIMULTANEOUS_GAMES = 10;
    ArrayList<String> gameNames = new ArrayList<String>();
    ArrayList<ArrayList<String>> playerNames = new ArrayList<ArrayList<String>>(); // names of players in the above games
    ArrayList<Boolean> gameIsNew = new ArrayList<Boolean>(); // true during formation of game
    ArrayList<Boolean> gameIsActive = new ArrayList<Boolean>(); // when a game is destroyed (or won), this turns false
    int gameNumberIterator = 0;

    ArrayList<int []> gameWhiteDeck = new ArrayList<int[]>();
    ArrayList<Integer> currentWhiteIndex = new ArrayList<Integer>();
    ArrayList<int[]> gameBlackDeck = new ArrayList<int[]>();
    ArrayList<Integer> currentBlackIndex = new ArrayList<Integer>();
    ArrayList<ArrayList<Integer>> playerPoints = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<Integer>> playerCardSelection = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> lastRoundCard = new ArrayList<Integer>();
    ArrayList<Integer> lastRoundWinningPlayer = new ArrayList<Integer>();
    ArrayList<Integer> turnNumber = new ArrayList<Integer>();
    ArrayList<ArrayList<Integer>> shuffledPlayerQueue = new ArrayList<ArrayList<Integer>>();// used to remember who submitted which card after shuffling

    ArrayList<Integer> currentCardCzar = new ArrayList<Integer>();
    ArrayList<Integer> turnPhase = new ArrayList<Integer>();

    ArrayList<ArrayList<ArrayList<Integer>>> playerHand = new ArrayList<ArrayList<ArrayList<Integer>>>(); // playerHand[gameId][playerId][cardNumber]



    @Override
    public Integer[] getGames() {
        ArrayList<Integer> newGames = new ArrayList<Integer>();
        for(int i = 0; i < gameNumberIterator; i++) {
            if(gameIsActive.get(i)) {
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
        boolean ableToRecycle = false;
        int gameId = -1;
        // search for a game that has ended to reuse its place
        for(int i = 0; i < gameNumberIterator; i++) {
            if(!gameIsActive.get(i)) {
                gameId = i;
                ableToRecycle = true;
                break;
            }
        }
        if(ableToRecycle) {
            gameNames.set(gameId, gameName);
            playerNames.set(gameId, new ArrayList<String>());
            gameIsNew.set(gameId, true);
            gameIsActive.set(gameId, true);
            gameWhiteDeck.set(gameId, null);
            gameBlackDeck.set(gameId, null);
            currentBlackIndex.set(gameId, 0);
            currentWhiteIndex.set(gameId, 0);
            playerHand.set(gameId, new ArrayList<ArrayList<Integer>>());
            playerPoints.set(gameId, new ArrayList<Integer>());
            playerCardSelection.set(gameId, new ArrayList<Integer>());
            currentCardCzar.set(gameId, -1);
            turnPhase.set(gameId, -1);
            lastRoundCard.set(gameId, -1);
            lastRoundWinningPlayer.set(gameId, -1);
            turnNumber.set(gameId, -1);
            shuffledPlayerQueue.set(gameId, new ArrayList<Integer>());
        }
        else if(gameNumberIterator <= MAX_SIMULTANEOUS_GAMES) {
            gameId = gameNumberIterator++;
            gameNames.add(gameId, gameName);
            playerNames.add(gameId, new ArrayList<String>());
            gameIsNew.add(gameId, true);
            gameIsActive.add(gameId, true);
            gameWhiteDeck.add(gameId, null);
            gameBlackDeck.add(gameId, null);
            currentBlackIndex.add(gameId, 0);
            currentWhiteIndex.add(gameId, 0);
            playerHand.add(gameId, new ArrayList<ArrayList<Integer>>());
            playerPoints.add(gameId, new ArrayList<Integer>());
            playerCardSelection.add(gameId, new ArrayList<Integer>());
            currentCardCzar.add(gameId, -1);
            turnPhase.add(gameId, -1);
            lastRoundCard.add(gameId, -1);
            lastRoundWinningPlayer.add(gameId, -1);
            turnNumber.add(gameId,-1);
            shuffledPlayerQueue.add(gameId, new ArrayList<Integer>());


        }
        else {
            // too many simultaneous games
            System.out.println("Warning, someone is trying to create more than " + MAX_SIMULTANEOUS_GAMES + " games");
            // todo (eventually) perform cleanup for games that have been inactive for a certain amount of time
        }
        return gameId;
    }


    @Override
    public int joinGame(int gameId, String userName) {

        if(gameId >= gameNumberIterator || gameId < 0) {
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
        else if(userName.equals("")) {
            // no username was supplied
            return -1;
        }
        else {
            playerNames.get(gameId).add(userName);
            return playerNames.get(gameId).size() - 1; // return the player's id in the game
        }

    }

    @Override
    public String[] listPlayers(int gameId) {
        if(gameId >= gameNumberIterator || gameId < 0) {
            // error
            return new String[]{};
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
                output = new String[]{};
            }

            return output;
        }
    }

    @Override
    public boolean destroyGame(int gameId) {
        if(gameId >= gameNumberIterator || gameId < 0) {
            return false;
        }
        else {
            gameIsActive.set(gameId, false);
            return true;
        }
    }

    @Override
    public String getGameName(int gameId) {
        if(gameId >= gameNumberIterator || gameId < 0) {
            return "Invalid gameId";
        }
        return gameNames.get(gameId);
    }

    @Override
    public boolean gameIsForming(int gameId) {
        if(gameId >= gameNumberIterator || gameId < 0) {
            // error, gameId is out of bounds
            return false;
        }
        return gameIsNew.get(gameId);
    }

    @Override
    public int playerIsCardCzar(int gameId, int playerId) {
        if(gameId >= gameNumberIterator || gameId < 0) {
            //gameId is out of bounds
            return -1;
        }
        if(currentCardCzar.get(gameId) == playerId) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getTurnPhase(int gameId) {
        if(gameId >= gameNumberIterator || gameId < 0) {
            //gameId is out of bounds
            return -1;
        }
        return turnPhase.get(gameId);
    }

    @Override
    public String[] getHand(int gameId, int playerId) {
        if(gameId >= gameNumberIterator || gameId < 0) {
            //gameId is out of bounds
            return new String[]{"GameId out of bounds"};
        }
        if(playerId >= playerNames.get(gameId).size() || playerId < 0) {
            return new String[]{"PlayerId out of bounds"};
        }
        if(turnPhase.get(gameId) == 1) {
            // send the normal player's hand
            String[] output;
            if(currentCardCzar.get(gameId) == playerId) {
                output = new String[2];
                output[0] = "You are the card czar, and must wait on the other players";;
                output[1] = generateScoreReport(gameId);
            }
            else {
                output = new String[HANDSIZE];
                for(int cardNum = 0; cardNum < HANDSIZE; cardNum++) {
                    output[cardNum] = PartyCardsServer.whiteCards.get(playerHand.get(gameId).get(playerId).get(cardNum)).content;
                }
            }
            return output;
        }
        if(turnPhase.get(gameId) == 2) {
            // show everyone which cards were chosen.
            // the card czar will choose one

            if(shuffledPlayerQueue.get(gameId).size() == 0) {
                // need to shuffle the submitted white cards
                shuffledPlayerQueue.set(gameId, new ArrayList<Integer>());
                // add the other player ids
                for (int otherPlayerId = 0; otherPlayerId < playerNames.get(gameId).size(); otherPlayerId++) {
                    if (otherPlayerId != currentCardCzar.get(gameId)) {
//                        System.out.println("adding to shuffledplayerarra" + otherPlayerId);
                        shuffledPlayerQueue.get(gameId).add(otherPlayerId);
                    }
                }
                if(shuffledPlayerQueue.get(gameId).size() < playerNames.get(gameId).size() - 1) {
                    System.out.println("Shuffledplayerqueue is too small");
                    return new String[]{"Error, for some reason shuffledPlayerQueue generated as the wrong size"};
                }
                //shuffle the ids
                Collections.shuffle(shuffledPlayerQueue.get(gameId));

            }


            // now translate these ids to the card content
            String[] output = new String[shuffledPlayerQueue.get(gameId).size()];
            for(int i = 0; i < output.length; i++) {
                // for each card, write the contents to output
                // shuffledPlayerQueue randomizes the players, so we get the card selected by the i^th player
                int unshuffledPlayerId = shuffledPlayerQueue.get(gameId).get(i);
                int cardIndexInUnshuffledDeck = playerHand.get(gameId).get(unshuffledPlayerId).get(playerCardSelection.get(gameId).get(unshuffledPlayerId));
                output[i] = PartyCardsServer.whiteCards.get(cardIndexInUnshuffledDeck).content;
            }
            return output;
        }
        return new String[]{"Uncaught, turn phase: " + turnPhase.get(gameId)};
    }


    @Override
    public String getBlackCard(int gameId) {
        if(gameId >= gameNumberIterator || gameId < 0) {
            //gameId is out of bounds
            return "";
        }
        int indexInUnshuffledDeck = gameBlackDeck.get(gameId) [currentBlackIndex.get(gameId)];
//        System.out.println("indexInUnshuffledDeck: " + indexInUnshuffledDeck );
        return PartyCardsServer.blackCards.get(indexInUnshuffledDeck ).content;
    }

    @Override
    public int chooseCard(int gameId, int playerId, int cardNumber) {
        if(gameId >= gameNumberIterator || gameId < 0) {
            //gameId is out of bounds
            return -1;
        }
        if(playerId >= playerNames.get(gameId).size() || playerId < 0) {
            // playerId is out of bounds
            return -2;
        }
        if(currentCardCzar.get(gameId) == playerId) {
            if(cardNumber >= playerNames.get(gameId).size() || cardNumber < 0) {
                // cardNumber is out of bounds
                // there should be one less card than the number of players
                return -3;
            }
            // the card czar has chosen a card, award a point to the player whose card was chosen
            int winningPlayer = shuffledPlayerQueue.get(gameId).get(cardNumber);
            playerPoints.get(gameId).set(winningPlayer, playerPoints.get(gameId).get(winningPlayer) + 1);

            // remember this round's card czar selection
            lastRoundCard.set(gameId, playerHand.get(gameId).get(winningPlayer).get(playerCardSelection.get(gameId).get(winningPlayer)));
            lastRoundWinningPlayer.set(gameId, winningPlayer);
//            System.out.println("dealing next cards");
            dealNextTurn(gameId);

            return 0;

        }
        else {
            if(cardNumber >= HANDSIZE || cardNumber < 0) {
                // cardNumber is out of bounds
                return -6;
            }

            // player is a normal player, place the selection and check to see if the round is over

            // cardnumber refers to which of the 7 cards in the hand, we need the index in the deck
            playerCardSelection.get(gameId).set(playerId, cardNumber);

            // if the round phase is over, proceed to the next phase
            int playersStillSelecting = 0;
//            System.out.println("Players still selecting: " + toString(playerCardSelection.get(gameId)));
//            System.out.println("playerNames.get(gameId).size(): " + playerNames.get(gameId).size());
//            System.out.println("currentCardCzar.get(gameId): " + currentCardCzar.get(gameId));
            for(int playerIterator = 0; playerIterator < playerNames.get(gameId).size(); playerIterator++) {
                // we're done if all non-czar players have made a selection
//                System.out.println("playerCardSelection.get(gameId).get(playerId): " + playerCardSelection.get(gameId).get(playerId));
                if((currentCardCzar.get(gameId) != playerIterator) && (playerCardSelection.get(gameId).get(playerIterator) == -1)) {
                    playersStillSelecting++;
                }
            }
//            System.out.println("playersStillSelecting: " + playersStillSelecting);
            if(playersStillSelecting == 0) {
                // proceeding to phase 2
                turnPhase.set(gameId, 2);

            }
            return playersStillSelecting;
        }
    }

    private void dealNextTurn(int gameId) {
        turnPhase.set(gameId, 1); // set phase to 1
        increment(turnNumber, gameId);

        // for each normal player, replace their chosen card with the next white card in the deck
        for(int playerId = 0; playerId < playerNames.get(gameId).size(); playerId++) {
            //todo check to see if we're out of cards, if so, reshuffle the deck
            if(currentCardCzar.get(gameId) != playerId) {
                int handIndexToReplace = playerCardSelection.get(gameId).get(playerId);
                int deckIndexOfNewCard = gameWhiteDeck.get(gameId)[currentWhiteIndex.get(gameId)];
                playerHand.get(gameId).get(playerId).set(handIndexToReplace, deckIndexOfNewCard );
                increment(currentWhiteIndex, gameId);
            }
        }

        // deal the next black card
        increment(currentBlackIndex, gameId);

        //choose the next card czar
        int czar = currentCardCzar.get(gameId);
        czar = (czar + 1) % playerNames.get(gameId).size();
        currentCardCzar.set(gameId, czar);

        // forget last round's chosen cards
        shuffledPlayerQueue.set(gameId, new ArrayList<Integer>());
        fill(playerCardSelection.get(gameId), -1);

    }

    private void increment(ArrayList<Integer> array, int index) {
        array.set(index, array.get(index) + 1);
    }

    private String toString(String[] array) {
        String output = "";
        for(String i: array) {
            output += i + ", ";
        }
        return output;
    }
    private String toString(int[] array) {
        String output = "";
        for(Integer i: array) {
            output += i + ", ";
        }
        return output;
    }
    private String toString(ArrayList<Integer> array) {
        String output = "";
        for (Integer anArray : array) {
            output += anArray + ", ";
        }
        return output;
    }
    private void fill(ArrayList<Integer> array, int value) {
        for(int i = 0; i < array.size(); i++) {
            array.set(i, value);
        }
    }


    public Integer[] convertFromArrayList(ArrayList<Integer> before) {
        Integer [] after = new Integer[before.size()];
        System.out.println(before);
        for(int i = 0; i < after.length; i++) {
            after[i] = before.get(i);
//            System.out.println("Game in session: " + after[i]);
        }
        return after;
    }

    public void reportCurrentStatus() {

        System.out.println("\n\n-----");
        for(int gameId = 0; gameId < gameNumberIterator; gameId++) {
            System.out.print("Game " + gameId + ", name: " + gameNames.get(gameId) + ", Players: ");
            for(int j = 0; j < playerNames.get(gameId).size(); j++) {
                System.out.print(playerNames.get(gameId).get(j) + ", ");
            }
            System.out.println();
            System.out.println("New: " + gameIsNew.get(gameId) + ", Active: " + gameIsActive.get(gameId));
            System.out.println("Turn number: " + turnNumber.get(gameId) + ", turn phase: " + turnPhase.get(gameId) + ", lastRoundCard: " + lastRoundCard.get(gameId) + ", selections: " + toString(playerCardSelection.get(gameId)));
            System.out.println("Black card " + gameBlackDeck.get(gameId)[currentBlackIndex.get(gameId)] + ": " + getBlackCard(gameId));
            if(!gameIsNew.get(gameId)) {
                for(int playerId = 0; playerId < playerNames.get(gameId).size(); playerId++) {
                    System.out.print("Player hand: " + playerHand.get(gameId).get(playerId));
                    System.out.println(" - " + toString(getHand(gameId, playerId)));
                }
            }
            System.out.println("scoring player: " + lastRoundWinningPlayer.get(gameId) + ", with card: " + lastRoundCard.get(gameId));
            System.out.println();
        }
    }

    // returns [turn number][phase number][last chosen card]
    @Override
    public Integer[] getTurnStatus(int gameId) {
        if(gameId >= gameNumberIterator || gameId < 0) {
            //gameId is out of bounds
            return new Integer[]{};
        }
        Integer [] output = new Integer[3];
        output[0] = turnNumber.get(gameId);
        output[1] = turnPhase.get(gameId);

        if(turnPhase.get(gameId) == 1) {
            int playersStillChoosing = 0;
            for (int playerId = 0; playerId < playerNames.get(gameId).size(); playerId++) {
                if (playerId != currentCardCzar.get(gameId) && playerCardSelection.get(gameId).get(playerId) == -1) {
                    // this player still hasn't chosen
                    playersStillChoosing++;
                }
            }
            output[2] = playersStillChoosing;

        }
        else { // the turn phase will be set to 0 automatically when the czar makes a choice, so in phase 2, you're always waiting on 1 player
            output[2] = 1;
        }

        return output;
    }

    @Override
    public Integer[] getScore(int gameId) {
        if(gameId >= gameNumberIterator || gameId < 0) {
            //gameId is out of bounds
            return new Integer[]{};
        }
        Integer [] output = new Integer[playerNames.get(gameId).size()];
        for(int playerNum = 0; playerNum < output.length; playerNum++) {
            output[playerNum] = playerPoints.get(gameId).get(playerNum);
        }
        return output;
    }

    @Override
    public String[] roundSummary(int gameId) {
        if(gameId >= gameNumberIterator || gameId < 0) {
            //gameId is out of bounds
            return new String[]{"Error, gameId out of bounds"};
        }
        if(turnNumber.get(gameId) == 1) {
            String [] output = new String[1];
            output[0] = "turn: " + turnNumber.get(gameId) + ", phase " + turnPhase.get(gameId) + "\n";
            output[0] += "Scores:";
            for(int playerId = 0; playerId < playerNames.get(gameId).size(); playerId++) {
                output[0] += "\n" + playerNames.get(gameId).get(playerId) + ": " + playerPoints.get(gameId).get(playerId);
            }
            return output;
        }

        String[] output = new String[3];
        output[0] = "Point - " + playerNames.get(gameId).get(lastRoundWinningPlayer.get(gameId)) + "\nChosen card: " + PartyCardsServer.whiteCards.get(lastRoundCard.get(gameId)).content;
        output[1] = "Scores:";
        output[2] = "Turn: " + turnNumber.get(gameId) + ", Phase " + turnPhase.get(gameId) + "\n";
        // list the score and name of each player on the second page
        for(int playerId = 0; playerId < playerNames.get(gameId).size(); playerId++) {
            output[1] += "\n" + playerNames.get(gameId).get(playerId) + ": " + playerPoints.get(gameId).get(playerId);
        }
        return output;
    }


    public InGameData getGameData(int gameId, int playerId) {
        if(gameId >= gameNumberIterator || gameId < 0 || playerId >= playerNames.get(gameId).size() || playerId < 0) {
            //gameId is out of bounds
            InGameData errorGame = new InGameData();
            errorGame.blackCard = "error retrieving data";
            errorGame.hand = new String[]{"error retrieving data"};
            return errorGame;
        }
        InGameData game = new InGameData();
        game.blackCard = getBlackCard(gameId);
        game.hand = new String[playerHand.get(gameId).get(playerId).size()];

        //roundText
        if(turnNumber.get(gameId) == 1) {
            game.roundText = "Round 1";
        }
        else {
            game.roundText = "Round 2, previous winner: " + playerNames.get(gameId).get(lastRoundWinningPlayer.get(gameId));
            game.roundText += " with\n" + getWhiteCardString(lastRoundCard.get(gameId));
        }
        game.playerId = playerId;

        //playerIsCardCzar
        if(playerId == currentCardCzar.get(gameId)) {
            game.playerIsCardCzar = 1;
        }
        else {
            game.playerIsCardCzar = 0;
        }
        game.turnPhase = turnPhase.get(gameId);
        game.turnNumber = turnNumber.get(gameId);
        game.numberOfPlayersChoosing = 0;

        game.hand = getHand(gameId, playerId);

        return game;
    }

    @Override
    public BasicGameData [] getBasicGameData() {
        BasicGameData [] output = new BasicGameData[gameNumberIterator];
        for(int gameId = 0; gameId < output.length; gameId++) {
            output[gameId] = new BasicGameData();
            output[gameId].gameId = gameId;
            output[gameId].gameName = gameNames.get(gameId);
            output[gameId].gameIsNew = gameIsNew.get(gameId);
            output[gameId].playerNames = makeStringArray(playerNames.get(gameId));
        }
        return output;
    }

    @Override
    public BasicGameData getBasicGameDataSingleGame(int gameId) {
        if(gameId >= gameNumberIterator || gameId < 0) {
            return new BasicGameData(); // game has already started
        }
        BasicGameData output = new BasicGameData();
        output.gameId = gameId;
        output.gameName = gameNames.get(gameId);
        output.gameIsNew = gameIsNew.get(gameId);
        output.playerNames = makeStringArray(playerNames.get(gameId));
        return output;
    }

    public void startNewGame(int gameId) {
        if(!gameIsNew.get(gameId)) {
            return; // game has already started
        }
        if(gameId >= gameNumberIterator || gameId < 0) {
            return; // gameId out of bounds
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
        System.out.println(blackDeck);
        Collections.shuffle(blackDeck);
        System.out.println(blackDeck);

        // store the deck so it can be referred to later (first convert to an array)
        int[] blackDeckArray = new int[blackDeck.size()];
        for(int i = 0; i < blackDeck.size(); i++) {
            blackDeckArray[i] = blackDeck.get(i);
        }
        gameBlackDeck.set(gameId, blackDeckArray);
        System.out.println(toString(gameBlackDeck.get(gameId)));

        playerHand.set(gameId, new ArrayList<ArrayList<Integer>>()); //
        for(int playerId = 0; playerId < numberOfPlayers; playerId++ ) {
            ArrayList<Integer> thisGamePlayerHand = new ArrayList<Integer>();
            //deal 7 white cards to each player
            for(int cardNum = 0; cardNum < HANDSIZE; cardNum++) {
                thisGamePlayerHand.add(whiteDeckArray[whiteDeckIndexThisGame++]);
            }
            playerHand.get(gameId).add(thisGamePlayerHand);

            //set each players' points to zero
            playerPoints.get(gameId).add(playerId, 0);

            //set the current card selection to "unselected"
            playerCardSelection.get(gameId).add(playerId, NO_CARD_SELECTED);

            //set the first card czar to be player 0
            currentCardCzar.set(gameId, 0);
        }
        turnNumber.set(gameId, 1);
        turnPhase.set(gameId, 1);

        currentWhiteIndex.set(gameId, whiteDeckIndexThisGame);
        currentBlackIndex.set(gameId, 0);
    }

    private String[] makeStringArray(ArrayList<String> input) {
        String[] output = new String[input.size()];
        for(int i = 0; i < output.length; i++) {
            output[i] = (String) input.get(i);
        }
        return output;
    }
    private String getWhiteCardString(int indexNumber) {
        return PartyCardsServer.whiteCards.get(indexNumber).content;
    }
    private String generateScoreReport(int gameId) {
        String output = "Round " + turnNumber.get(gameId) + " score:\n";
        for(int playerId = 0; playerId < playerNames.get(gameId).size(); playerId++) {
            output += playerNames.get(gameId).get(playerId) + ": " + playerPoints.get(gameId).get(playerId) + "\n";
        }
        return output;
    }
}
