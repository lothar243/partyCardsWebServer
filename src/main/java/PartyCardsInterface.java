package main.java;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

// ====================================================================================================================
// PartyCardsInterface.java
// --------------------------------------------------------------------------------------------------------------------
// Party Cards Server: Android Networking Project
// CSCI-466: Networks
// Jeff Arends, Lee Curran, Angela Gross, Andrew Meissner
// Spring 2015
// --------------------------------------------------------------------------------------------------------------------
// This interface spells out the promised services. It is used both to ensure those functions have the correct
// argument and output types, and it is also used to generate the WSDL file.
// ====================================================================================================================

@WebService
@SOAPBinding(style = Style.RPC)
public interface PartyCardsInterface 
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    public static final int GET_GAMES = 1;
    Integer [] getGames(); // return array of game names

    public static final int CREATE_NEW_GAME = 2;
    int createNewGame(String gameName); // returns id of game

    public static final int JOIN_GAME = 3;
    int joinGame(int gameId, String userName); // returns your player number in the game

    public static final int LIST_PLAYERS = 4;
    String[] listPlayers(int gameId); // return array of names

    public static final int DESTROY_GAME = 5;
    boolean destroyGame(int gameId); // true if success

    public static final int GET_GAME_NAME = 6;
    String getGameName(int gameId); // return name of game with a given id

    public static final int GAME_IS_FORMING = 7;
    boolean gameIsForming(int gameId);

    public static final int PLAYER_IS_CARD_CZAR = 8;
    int playerIsCardCzar(int gameId, int playerId);

    public static final int GET_TURN_PHASE = 9;
    int getTurnPhase(int gameId); // 0 means normal players are choosing cards, 1 means card czar is choosing card

    public static final int GET_HAND = 10;
    String[] getHand(int gameId, int playerId);

    public static final int GET_BLACK_CARD = 11;
    String getBlackCard(int gameId);

    public static final int CHOOSE_CARD = 12;
    int chooseCard(int gameId, int playerId, int cardNumber); // returns the number of people still choosing

    public static final int STAR_NEW_GAME = 13;
    void startNewGame(int gameId);

    public static final int REPORT_CURRENT_STATUS = 14;
    void reportCurrentStatus();

    public static final int GET_TURN_STATUS = 15;
    Integer[] getTurnStatus(int gameId); // returns [turn number][phase number][last chosen card]

    public static final int GET_SCORE = 16;
    Integer[] getScore(int gameId);

    public static final int ROUND_SUMMARY = 17;
    String [] roundSummary(int gameId);

    public static final int GET_GAME_DATA = 18;
    InGameData getGameData(int gameId, int playerId);

    public static final int GET_BASIC_GAME_DATA = 19;
    BasicGameData [] getBasicGameData();

    public static final int GET_BASIC_GAME_DATA_SINGLE_GAME = 20;
    BasicGameData getBasicGameDataSingleGame(int gameId);
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}