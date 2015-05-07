package main.java;

// ====================================================================================================================
// BasicGameData.java
// --------------------------------------------------------------------------------------------------------------------
// Party Cards Server: Android Networking Project
// CSCI-466: Networks
// Jeff Arends, Lee Curran, Angela Gross, Andrew Meissner
// Spring 2015
// --------------------------------------------------------------------------------------------------------------------
// This is nothing more than a data type used to pass information about games before the players has joined any.
// ====================================================================================================================

public class BasicGameData 
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    public int gameId;
    public String gameName;
    public boolean gameIsNew;
    public String[] playerNames;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
