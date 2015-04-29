package main.java;

import java.io.*;
import java.util.ArrayList;

// ====================================================================================================================
// FileIO.java
// --------------------------------------------------------------------------------------------------------------------
// Party Cards: Android Networking Project
// CSCI-466: Networks
// Jeff Arends, Lee Curran, Angela Gross, Andrew Meissner
// Spring 2015
// --------------------------------------------------------------------------------------------------------------------
// This is the file input and output class. It reads the card file in, as well as the user config file.
// ====================================================================================================================

public class FileIO {
    public static final String BLACK_CARDS_FILENAME = "blackcards.txt";
    public static final String WHITE_CARDS_FILENAME = "whitecards.txt";

    // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    // READERS
    // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    // ===============================================================================================================
    // READCARDS()
    // ---------------------------------------------------------------------------------------------------------------
    // Reads in from <filename>, a list of strings representing the text to be displayed on the white cards.
    // @return - A "pile" of cards.
    // ===============================================================================================================
	public ArrayList<Card> readCards(String filename) {
		ArrayList<Card> cards = new ArrayList<Card>();
		
		try
        {
            FileReader FileIO = new FileReader(filename);
            BufferedReader in = new BufferedReader(FileIO);
			String nextLine;
            while((nextLine = in.readLine()) != null) {
                cards.add(new Card(nextLine));
            }
			in.close();
            FileIO.close();
		}
        catch (IOException e)
        {
			// log the exception
		}
		
		return cards;
	}

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
}