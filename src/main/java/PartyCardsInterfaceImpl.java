package main.java;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Collections;

// ====================================================================================================================
// PartyCardsInterfaceImpl.java
// --------------------------------------------------------------------------------------------------------------------
// Party Cards Server: Android Networking Project
// CSCI-466: Networks
// Jeff Arends, Lee Curran, Angela Gross, Andrew Meissner
// Spring 2015
// --------------------------------------------------------------------------------------------------------------------
// This file actually implements the functions promised by PartyCardsInterface. It includes all of the logic for the
// gameplay - including creating a game, joining a game, starting a game, listing what your hand is, and choosing 
// cards.
//=====================================================================================================================

@WebService(endpointInterface = "main.java.PartyCardsInterface")
public class PartyCardsInterfaceImpl implements PartyCardsInterface 
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // PARTYCARDSINTERFACEIMPL ATTRIBUTES
	
    public static final int NO_CARD_SELECTED = -1;

    private static final int HANDSIZE = 7;
    private static final int MAX_SIMULTANEOUS_GAMES = 10;
    private static final int MAX_NUMBER_OF_PLAYERS = 10;
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // ===============================================================================================================
    // GETGAMES
    // ---------------------------------------------------------------------------------------------------------------
    // Pass an array of gameIDs of all active games
    // ===============================================================================================================
    @Deprecated
    @Override
    public Integer[] getGames() 
    {
        ArrayList<Integer> newGames = new ArrayList<Integer>();
        for(int i = 0; i < gameNumberIterator; i++)
        {
            if(gameIsActive.get(i)) 
            {
                newGames.add(i);
            }
        }
        return convertFromArrayList(newGames);
    }

    // ===============================================================================================================
    // CREATENEWGAME
    // ---------------------------------------------------------------------------------------------------------------
    // Create a new game with the given name. If there is already a game with that name, it modifies the name
    // slightly to make it unique.
    // @param gameName the proposed name for the game to be created
    // @return gameId  the numerical id of the game that was created, or -1 if there was an error
    // ===============================================================================================================
    @Override
    public int createNewGame(String gameName) 
    {

        while(gameNames.contains(gameName)) 
        {
            // error game name already exists, making it unique
            gameName = "" + gameNumberIterator + gameName;
        }
        boolean ableToRecycle = false;
        int gameId = -1;
        // search for a game that has ended to reuse its place
        for(int i = 0; i < gameNumberIterator; i++) 
        {
            if(!gameIsActive.get(i)) 
            {
                gameId = i;
                ableToRecycle = true;
                break;
            }
        }
        if(ableToRecycle) 
        {
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
        else if(gameNumberIterator <= MAX_SIMULTANEOUS_GAMES) 
        {
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
        else 
        {
            // too many simultaneous games
            System.out.println("Warning, someone is trying to create more than " + MAX_SIMULTANEOUS_GAMES + " games");
            // todo (eventually) perform cleanup for games that have been inactive for a certain amount of time
        }
        return gameId;
    }

    // ===============================================================================================================
    // JOINGAME
    // ---------------------------------------------------------------------------------------------------------------
    // Join a certain game that has been created, but has not yet started. If the player name is already in the game,
    // the ID of the existing player is returned (We assume the player lost a connecting and is re-connecting)
    // @param gameId   The ID of the game to be joined
    // @param userName The name of the player that is joining the game
    // @return userID  An ID of the player in that particular game, or -1 if there was a failure.
    // ===============================================================================================================
    @Override
    public int joinGame(int gameId, String userName) 
    {

        if(gameId >= gameNumberIterator || gameId < 0) 
        {
            // gameId out of bounds
            return -1;
        }
        else if ( playerNames.get(gameId).contains(userName)) 
        {
            return playerNames.get(gameId).indexOf(userName);
        }
        else if(!gameIsNew.get(gameId)) 
        {
            // player hasn't joined and game has already started
            return -1;
        }
        else if(userName.equals("")) 
        {
            // no username was supplied
            return -1;
        }
        else if(playerNames.get(gameId).size() >= MAX_NUMBER_OF_PLAYERS) 
        {
            // the game is full already
            return -1;
        }
        else 
        {
            playerNames.get(gameId).add(userName);
            return playerNames.get(gameId).size() - 1; // return the player's id in the game
        }

    }

    // ===============================================================================================================
    // LISTPLAYERS
    // ---------------------------------------------------------------------------------------------------------------
    // List the names of each of the players that have joined a particular game
    // @param gameId The ID of the game in question
    // @return A array of strings containing the player names in the given game
    // ===============================================================================================================
    @Override
    public String[] listPlayers(int gameId) 
    {
        if(gameId >= gameNumberIterator || gameId < 0) 
        {
            // error
            return new String[]{};
        }
        else 
        {
            String [] output;
            if(playerNames.get(gameId).size() > 0) 
            {
                output = new String[playerNames.get(gameId).size()];
                for (int i = 0; i < output.length; i++) 
                {
                    output[i] = playerNames.get(gameId).get(i);
                }
            }
            else 
            {
                output = new String[]{};
            }

            return output;
        }
    }

    // ===============================================================================================================
    // DESTROYGAME
    // ---------------------------------------------------------------------------------------------------------------
    // If a game reaches a winning condition or a player decides to end a game, this turns the game
    // to non-active, allowing another game to take its slot
    // @param gameId The ID of the game that has finished
    // @return true on success
    // ===============================================================================================================
    @Override
    public boolean destroyGame(int gameId) 
    {
        if(gameId >= gameNumberIterator || gameId < 0) 
        {
            return false;
        }
        else 
        {
            gameIsActive.set(gameId, false);
            return true;
        }
    }

    // ===============================================================================================================
    // GETGAMENAME
    // ---------------------------------------------------------------------------------------------------------------
    // Fetch the name of a particular game
    // @param gameId The ID of the game in question
    // @return The name of the game in question
    // ===============================================================================================================
    @Deprecated
    @Override
    public String getGameName(int gameId) 
    {
        if(gameId >= gameNumberIterator || gameId < 0) 
        {
            return "Invalid gameId";
        }
        return gameNames.get(gameId);
    }

    // ===============================================================================================================
    // GAMEISFORMING
    // ---------------------------------------------------------------------------------------------------------------
    // Fetch status about whether a game is still forming or has started already
    // @param gameId The ID of the game in question
    // @return true if the game has not yet started
    // ===============================================================================================================
    @Deprecated
    @Override
    public boolean gameIsForming(int gameId) 
    {
        if(gameId >= gameNumberIterator || gameId < 0) 
        {
            // error, gameId is out of bounds
            return false;
        }
        return gameIsNew.get(gameId);
    }

    // ===============================================================================================================
    // GAMEISFORMING
    // ---------------------------------------------------------------------------------------------------------------
    // Determine if a particular player is the card czard of a particular game
    // @param gameId  The game in question
    // @param playerId The player in question
    // @return 0 for normal players, 1 for the current card czar, -1 for errors
    // ===============================================================================================================
    @Deprecated
    @Override
    public int playerIsCardCzar(int gameId, int playerId) 
    {
        if(gameId >= gameNumberIterator || gameId < 0) 
        {
            //gameId is out of bounds
            return -1;
        }
        if(currentCardCzar.get(gameId) == playerId) 
        {
            return 1;
        }
        return 0;
    }

    // ===============================================================================================================
    // GETTURNPHASE
    // ---------------------------------------------------------------------------------------------------------------
    // Returns the current turn phase of the current game. Turn phase 1 means the normal players
    // are choosing their cards. Turn phase 2 means the card czar is choosing their card.
    // @param gameId The ID of the game in question
    // @return 1 for normal player phase, 2 for card czar phase, -1 for error.
    // ===============================================================================================================
    @Deprecated
    @Override
    public int getTurnPhase(int gameId) 
    {
        if(gameId >= gameNumberIterator || gameId < 0) 
        {
            //gameId is out of bounds
            return -1;
        }
        return turnPhase.get(gameId);
    }
    
    // ===============================================================================================================
    // GETHAND
    // ---------------------------------------------------------------------------------------------------------------
    // Get the text of the cards in the current player's hand. This was originally intended to be
    // used as a webservice function, but that functionality was deprecated. It is now used as part of the
    // getInGameData function.
    // @param gameId The ID of the current game
    // @param playerId The ID of the current player
    // @return An array of strings, one string per card. During times when the player isn't choosing a card, it will
    // return additional data about the game (score, turn number, and which card won the previous round)
    // ===============================================================================================================
    @Override
    public String[] getHand(int gameId, int playerId) 
    {
        if(gameId >= gameNumberIterator || gameId < 0) 
        {
            //gameId is out of bounds
            return new String[]{"GameId out of bounds"};
        }
        if(playerId >= playerNames.get(gameId).size() || playerId < 0) 
        {
            return new String[]{"PlayerId out of bounds"};
        }
        if(turnPhase.get(gameId) == 1) 
        {
            // send the normal player's hand
            String[] output;
            if(currentCardCzar.get(gameId) == playerId)
            {
                output = new String[2];
                output[0] = "You are the card czar, and must wait on the other players";;
                output[1] = generateScoreReport(gameId);
            }
            else 
            {
                output = new String[HANDSIZE];
                for(int cardNum = 0; cardNum < HANDSIZE; cardNum++) 
                {
                    output[cardNum] = PartyCardsServer.whiteCards.get(playerHand.get(gameId).get(playerId).get(cardNum)).content;
                }
            }
            return output;
        }
        if(turnPhase.get(gameId) == 2) 
        {
            // show everyone which cards were chosen.
            // the card czar will choose one

            if(shuffledPlayerQueue.get(gameId).size() == 0) 
            {
                // need to shuffle the submitted white cards
                shuffledPlayerQueue.set(gameId, new ArrayList<Integer>());
                // add the other player ids
                for (int otherPlayerId = 0; otherPlayerId < playerNames.get(gameId).size(); otherPlayerId++) 
                {
                    if (otherPlayerId != currentCardCzar.get(gameId)) 
                    {            
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
            for(int i = 0; i < output.length; i++) 
            {
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

    // ===============================================================================================================
    // GETBLACKCARD
    // ---------------------------------------------------------------------------------------------------------------
    // This was originally intended as a webservice function, but is now only used for the getInGameData.
    // It returns the text of the current black card for the given game.
    // @param gameId The ID of the game in question
    // @return The text of the current black card
    // ===============================================================================================================
    @Override
    public String getBlackCard(int gameId) 
    {
        if(gameId >= gameNumberIterator || gameId < 0) 
        {
            //gameId is out of bounds
            return "";
        }
        int indexInUnshuffledDeck = gameBlackDeck.get(gameId) [currentBlackIndex.get(gameId)];
        return PartyCardsServer.blackCards.get(indexInUnshuffledDeck ).content;
    }
    
    // ===============================================================================================================
    // CHOOSECARD
    // ---------------------------------------------------------------------------------------------------------------
    // This is the function that players call when they make their choice about a particular card.
    // Each time the function is called, it check to see if everyone has made a choice. If everyone has made a choice,
    // it advances the game to the next phase or the next turn, depending on which phase it is currently in.
    // @param gameId The game in quesion
    // @param playerId The ID of the player making the choice
    // @param cardNumber The position of the card in the players hand (typically 0 through 6)
    // @return An int of the number of players still choosing that round, so they can know if they need to update
    // the InGameData immediately (or potentially report about how many players they're waiting on)
    // ===============================================================================================================
    @Override
    public int chooseCard(int gameId, int playerId, int cardNumber) 
    {
        if(gameId >= gameNumberIterator || gameId < 0) 
        {
            //gameId is out of bounds
            return -1;
        }
        if(playerId >= playerNames.get(gameId).size() || playerId < 0) 
        {
            // playerId is out of bounds
            return -2;
        }
        if(currentCardCzar.get(gameId) == playerId) 
        {
            if(cardNumber >= playerNames.get(gameId).size() || cardNumber < 0) 
            {
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
            dealNextTurn(gameId);

            return 0;

        }
        else 
        {
            if(cardNumber >= HANDSIZE || cardNumber < 0) 
            {
                // cardNumber is out of bounds
                return -6;
            }

            // player is a normal player, place the selection and check to see if the round is over

            // cardnumber refers to which of the 7 cards in the hand, we need the index in the deck
            playerCardSelection.get(gameId).set(playerId, cardNumber);

            // if the round phase is over, proceed to the next phase
            int playersStillSelecting = 0;
            for(int playerIterator = 0; playerIterator < playerNames.get(gameId).size(); playerIterator++) 
            {
                if((currentCardCzar.get(gameId) != playerIterator) && (playerCardSelection.get(gameId).get(playerIterator) == -1))
                {
                    playersStillSelecting++;
                }
            }
            if(playersStillSelecting == 0) 
            {
                // proceeding to phase 2
                turnPhase.set(gameId, 2);

            }
            return playersStillSelecting;
        }
    }

    // ===============================================================================================================
    // DEALNEXTTURN
    // ===============================================================================================================
    private void dealNextTurn(int gameId) 
    {
        turnPhase.set(gameId, 1); // set phase to 1
        increment(turnNumber, gameId);

        // for each normal player, replace their chosen card with the next white card in the deck
        for(int playerId = 0; playerId < playerNames.get(gameId).size(); playerId++) 
        {
            //todo check to see if we're out of cards, if so, reshuffle the deck
            if(currentCardCzar.get(gameId) != playerId) 
            {
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
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // ===============================================================================================================
    // INCREMENT
    // ---------------------------------------------------------------------------------------------------------------
    // Internal use only, used to increment a value of an ArrayList element
    // @param array name of array
    // @param index index of element to increment
    // ===============================================================================================================
    private void increment(ArrayList<Integer> array, int index) 
    {
        array.set(index, array.get(index) + 1);
    }
    
    // ===============================================================================================================
    // FILL
    // ---------------------------------------------------------------------------------------------------------------
    // Fill an ArrayList with a particular value. Used mostly for re-initializing an ArrayList
    // @param array the array to fill
    // @param value the value to fill the array with
    // ===============================================================================================================
    private void fill(ArrayList<Integer> array, int value) 
    {
        for(int i = 0; i < array.size(); i++) 
        {
            array.set(i, value);
        }
    }

    // ===============================================================================================================
    // CONVERTFROMARRAYLIST
    // ===============================================================================================================
    public Integer[] convertFromArrayList(ArrayList<Integer> before) 
    {
        Integer [] after = new Integer[before.size()];
        System.out.println(before);
        for(int i = 0; i < after.length; i++) 
        {
            after[i] = before.get(i);
        }
        return after;
    }

    // ===============================================================================================================
    // REPORTCURRENTSTATUS
    // ---------------------------------------------------------------------------------------------------------------
    // Called by a remote service when you wish to have the server report what the value of many
    // of its variables. This is used primarily for debugging purposes.
    // ===============================================================================================================
    public void reportCurrentStatus() 
    {
        System.out.println("\n\n-----");
        for(int gameId = 0; gameId < gameNumberIterator; gameId++) 
        {
            System.out.print("Game " + gameId + ", name: " + gameNames.get(gameId) + ", Players: ");
            for(int j = 0; j < playerNames.get(gameId).size(); j++) 
            {
                System.out.print(playerNames.get(gameId).get(j) + ", ");
            }
            System.out.println();
            System.out.println("New: " + gameIsNew.get(gameId) + ", Active: " + gameIsActive.get(gameId));
            System.out.println("Turn number: " + turnNumber.get(gameId) + ", turn phase: " + turnPhase.get(gameId) + ", lastRoundCard: " + lastRoundCard.get(gameId) + ", selections: " + toString(playerCardSelection.get(gameId)));
            System.out.println("Black card " + gameBlackDeck.get(gameId)[currentBlackIndex.get(gameId)] + ": " + getBlackCard(gameId));
            if(!gameIsNew.get(gameId)) 
            {
                for(int playerId = 0; playerId < playerNames.get(gameId).size(); playerId++) 
                {
                    System.out.print("Player hand: " + playerHand.get(gameId).get(playerId));
                    System.out.println(" - " + toString(getHand(gameId, playerId)));
                }
            }
            System.out.println("scoring player: " + lastRoundWinningPlayer.get(gameId) + ", with card: " + lastRoundCard.get(gameId));
            System.out.println();
        }
    }

    // ===============================================================================================================
    // GETTURNSTATUS
    // ---------------------------------------------------------------------------------------------------------------
    // Get some information about the turn of the current game. Not used
    // @param gameId The ID of the game in question
    // @return A 3 valued array: {turn number, phase number, last chosen card}
    // ===============================================================================================================
    @Deprecated
    @Override
    public Integer[] getTurnStatus(int gameId) 
    {
        if(gameId >= gameNumberIterator || gameId < 0) 
        {
            //gameId is out of bounds
            return new Integer[]{};
        }
        Integer [] output = new Integer[3];
        output[0] = turnNumber.get(gameId);
        output[1] = turnPhase.get(gameId);

        if(turnPhase.get(gameId) == 1) 
        {
            int playersStillChoosing = 0;
            for (int playerId = 0; playerId < playerNames.get(gameId).size(); playerId++)
            {
                if (playerId != currentCardCzar.get(gameId) && playerCardSelection.get(gameId).get(playerId) == -1) 
                {
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

    // ===============================================================================================================
    // GETSCORE
    // ---------------------------------------------------------------------------------------------------------------
    // Not used. Was initially intended to return the score of each player in the game
    // @param gameId The ID of the game in question
    // @return An array of the current score of each player
    // ===============================================================================================================
    @Deprecated
    @Override
    public Integer[] getScore(int gameId)
    {
        if(gameId >= gameNumberIterator || gameId < 0) 
        {
            //gameId is out of bounds
            return new Integer[]{};
        }
        Integer [] output = new Integer[playerNames.get(gameId).size()];
        for(int playerNum = 0; playerNum < output.length; playerNum++) 
        {
            output[playerNum] = playerPoints.get(gameId).get(playerNum);
        }
        return output;
    }

    // ===============================================================================================================
    // ROUNDSUMMARY
    // ---------------------------------------------------------------------------------------------------------------
    // Not used. Was originally intended to report about the current state of the game
    // (Score and turn number)
    // @param gameId The game in question
    // @return An array of strings. {The last rounds winner, the score, the current turn}
    // ===============================================================================================================
    @Deprecated
    @Override
    public String[] roundSummary(int gameId)
    {
        if(gameId >= gameNumberIterator || gameId < 0)
        {
            //gameId is out of bounds
            return new String[]{"Error, gameId out of bounds"};
        }
        if(turnNumber.get(gameId) == 1)
        {
            String [] output = new String[1];
            output[0] = "turn: " + turnNumber.get(gameId) + ", phase " + turnPhase.get(gameId) + "\n";
            output[0] += "Scores:";
            for(int playerId = 0; playerId < playerNames.get(gameId).size(); playerId++) 
            {
                output[0] += "\n" + playerNames.get(gameId).get(playerId) + ": " + playerPoints.get(gameId).get(playerId);
            }
            return output;
        }

        String[] output = new String[3];
        output[0] = "Point - " + playerNames.get(gameId).get(lastRoundWinningPlayer.get(gameId)) + "\nChosen card: " + PartyCardsServer.whiteCards.get(lastRoundCard.get(gameId)).content;
        output[1] = "Scores:";
        output[2] = "Turn: " + turnNumber.get(gameId) + ", Phase " + turnPhase.get(gameId) + "\n";
        // list the score and name of each player on the second page
        for(int playerId = 0; playerId < playerNames.get(gameId).size(); playerId++) 
        {
            output[1] += "\n" + playerNames.get(gameId).get(playerId) + ": " + playerPoints.get(gameId).get(playerId);
        }
        return output;
    }

    // ===============================================================================================================
    // GETGAMEDATA
    // ---------------------------------------------------------------------------------------------------------------
    // Return all the important information for one player about a game that is currently in progress.
    // @param gameId The ID of the game in question
    // @param playerId The ID of the player requesting the data
    // @return InGameData about everthing the player needs to know - Their current hand, the black card, if they're the
    // card czar, etc
    // ===============================================================================================================
    public InGameData getGameData(int gameId, int playerId) 
    {
        if(gameId >= gameNumberIterator || gameId < 0 || playerId >= playerNames.get(gameId).size() || playerId < 0)
        {
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
        if(turnNumber.get(gameId) == 1) 
        {
            game.roundText = "Round 1";
        }
        else 
        {
            game.roundText = "Round 2, previous winner: " + playerNames.get(gameId).get(lastRoundWinningPlayer.get(gameId));
            game.roundText += " with\n" + getWhiteCardString(lastRoundCard.get(gameId));
        }
        game.playerId = playerId;

        //playerIsCardCzar
        if(playerId == currentCardCzar.get(gameId)) 
        {
            game.playerIsCardCzar = 1;
        }
        else 
        {
            game.playerIsCardCzar = 0;
        }
        game.turnPhase = turnPhase.get(gameId);
        game.turnNumber = turnNumber.get(gameId);
        game.numberOfPlayersChoosing = 0;

        game.hand = getHand(gameId, playerId);

        return game;
    }

    // ===============================================================================================================
    // GETBASICGAMEDATA
    // ---------------------------------------------------------------------------------------------------------------
    // Returns some of the data about a current game. Intended to be used before a player has joined.
    // This returns a complete list of all the games the server is currently running.
    // @return BasicGameData []: Game name, ID, whether the game has started, the names of the players in the game
    // ===============================================================================================================
    @Override
    public BasicGameData [] getBasicGameData() 
    {
        BasicGameData [] output = new BasicGameData[gameNumberIterator];
        for(int gameId = 0; gameId < output.length; gameId++) 
        {
            output[gameId] = new BasicGameData();
            output[gameId].gameId = gameId;
            output[gameId].gameName = gameNames.get(gameId);
            output[gameId].gameIsNew = gameIsNew.get(gameId);
            output[gameId].playerNames = makeStringArray(playerNames.get(gameId));
        }
        return output;
    }

    // ===============================================================================================================
    // GETBASICGAMEDATASINGLEGAME
    // ---------------------------------------------------------------------------------------------------------------
    // Request the game data for one particular game. Intended to be used after a player
    // has chosen a game and possibly joined, but before they are in the normal gameplay window
    // @param gameId The ID of the game in question
    // @return BasicGameData about the current game - (name, id, whether it has started, and who is in the game)
    // ===============================================================================================================
    @Override
    public BasicGameData getBasicGameDataSingleGame(int gameId) 
    {
        if(gameId >= gameNumberIterator || gameId < 0) 
        {
            return new BasicGameData(); // game has already started
        }
        BasicGameData output = new BasicGameData();
        output.gameId = gameId;
        output.gameName = gameNames.get(gameId);
        output.gameIsNew = gameIsNew.get(gameId);
        output.playerNames = makeStringArray(playerNames.get(gameId));
        return output;
    }

    // ===============================================================================================================
    // STARTNEWGAME
    // ---------------------------------------------------------------------------------------------------------------
    // After enough players have joined the game, this starts the game, preventing anyone else from joining
    // @param gameId The ID of the game to be started
    // ===============================================================================================================
    public void startNewGame(int gameId) 
    {
        if(!gameIsNew.get(gameId)) 
        {
            return; // game has already started
        }
        if(gameId >= gameNumberIterator || gameId < 0)
        {
            return; // gameId out of bounds
        }
        gameIsNew.set(gameId, false);
        int numberOfPlayers = playerNames.get(gameId).size();
        int whiteDeckIndexThisGame = 0;

        //shuffle white deck
        ArrayList<Integer> whiteDeck = new ArrayList<Integer>();
        int numberOfWhiteCards = PartyCardsServer.whiteCards.size();
        for(int i = 0; i < numberOfWhiteCards; i++) 
        {
            whiteDeck.add(i);
        }
        Collections.shuffle(whiteDeck);
        // store the deck so it can be referred to later
        int[] whiteDeckArray = new int[whiteDeck.size()];
        for(int i = 0; i < whiteDeck.size(); i++) 
        {
            whiteDeckArray[i] = whiteDeck.get(i);
        }
        gameWhiteDeck.set(gameId, whiteDeckArray);

        // shuffle black deck
        ArrayList<Integer> blackDeck = new ArrayList<Integer>();
        int numberOfBlackCards = PartyCardsServer.blackCards.size();
        for(int i = 0; i < numberOfBlackCards; i++)
        {
            blackDeck.add(i);
        }
        System.out.println(blackDeck);
        Collections.shuffle(blackDeck);
        System.out.println(blackDeck);

        // store the deck so it can be referred to later (first convert to an array)
        int[] blackDeckArray = new int[blackDeck.size()];
        for(int i = 0; i < blackDeck.size(); i++) 
        {
            blackDeckArray[i] = blackDeck.get(i);
        }
        gameBlackDeck.set(gameId, blackDeckArray);
        System.out.println(toString(gameBlackDeck.get(gameId)));

        playerHand.set(gameId, new ArrayList<ArrayList<Integer>>()); //
        for(int playerId = 0; playerId < numberOfPlayers; playerId++ ) 
        {
            ArrayList<Integer> thisGamePlayerHand = new ArrayList<Integer>();
            //deal 7 white cards to each player
            for(int cardNum = 0; cardNum < HANDSIZE; cardNum++) 
            {
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

    // ===============================================================================================================
    // GETWHITECARDSTRING
    // ---------------------------------------------------------------------------------------------------------------
    // For internal use only. Get the text on a particular card
    // @param indexNumber The index of the card in whitecards.txt
    // @return A string of the text on the card.
    // ===============================================================================================================
    private String getWhiteCardString(int indexNumber) 
    {
        return PartyCardsServer.whiteCards.get(indexNumber).content;
    }

    // ===============================================================================================================
    // GENERATESCOREREPORT
    // ---------------------------------------------------------------------------------------------------------------
    // generateScoreReport: Creates a multi-line string that displays the current score of each player
    // @param gameId The ID of the game in question
    // @return A string of the scores
    // ===============================================================================================================
    private String generateScoreReport(int gameId) 
    {
        String output = "Round " + turnNumber.get(gameId) + " score:\n";
        for(int playerId = 0; playerId < playerNames.get(gameId).size(); playerId++) 
        {
            output += playerNames.get(gameId).get(playerId) + ": " + playerPoints.get(gameId).get(playerId) + "\n";
        }
        return output;
    }
    
    // ===============================================================================================================
    // MAKESTRINGARRAY
    // ===============================================================================================================
    private String[] makeStringArray(ArrayList<String> input) 
    {
        String[] output = new String[input.size()];
        for(int i = 0; i < output.length; i++) 
        {
            output[i] = (String) input.get(i);
        }
        return output;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    // VARIOUS TOSTRING METHODS
    // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private String toString(String[] array) 
    {
        String output = "";
        for(String i: array) 
        {
            output += i + ", ";
        }
        return output;
    }
    private String toString(int[] array) 
    {
        String output = "";
        for(Integer i: array) 
        {
            output += i + ", ";
        }
        return output;
    }
    private String toString(ArrayList<Integer> array) 
    {
        String output = "";
        for (Integer anArray : array) 
        {
            output += anArray + ", ";
        }
        return output;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
