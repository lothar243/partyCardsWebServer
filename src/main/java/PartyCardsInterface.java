package main.java;

/**
 * Created by Jeff on 4/26/2015.
 */
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

//Service Endpoint Interface
@WebService
@SOAPBinding(style = Style.RPC)
public interface PartyCardsInterface {


    @WebMethod Integer [] getGames(); // return array of game names
    @WebMethod Integer [] getActiveGames(); // return array of game names
    @WebMethod int createNewGame(String gameName); // returns id of game
    @WebMethod int joinGame(int gameId, String userName); // returns your player number in the game
    @WebMethod String[] listPlayers(int gameId); // return array of names
    @WebMethod boolean destroyGame(int gameId); // true if success
    @WebMethod String getGameName(int gameId); // return name of game with a given id
    @WebMethod boolean gameIsForming(int gameId);
    @WebMethod boolean playerIsCardCzar(int gameId, int playerId);
    @WebMethod int getTurnPhase(int gameId); // 0 means normal players are choosing cards, 1 means card czar is choosing card
    @WebMethod String[] getHand(int gameId, int playerId);
    @WebMethod String getBlackCard(int gameId);
    @WebMethod int chooseCard(int gameId, int playerId, int cardNumber);
    @WebMethod void startNewGame(int gameId);
    @WebMethod void reportCurrentStatus();
}