package main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

// ====================================================================================================================
// FileIO.java
// --------------------------------------------------------------------------------------------------------------------
// Party Cards: Android Networking Project
// CSCI-466: Networks
// Jeff Arends, Lee Curran, Angela Gross, Andrew Meissner
// Spring 2015
// --------------------------------------------------------------------------------------------------------------------
// This is the file input and output class. It reads the card files in and outputs arrayLists of those cards.
// The card files should contain the text of one card on each line.
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
