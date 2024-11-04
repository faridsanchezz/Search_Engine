package Control;

import java.util.ArrayList;

public class Word {
    String word;
    ArrayList<WordOcurrence> wordOcurrenceList;

    public Word(String word, ArrayList<WordOcurrence> wordOcurrenceList) {
        this.word = "";
        this.wordOcurrenceList = new ArrayList<WordOcurrence>();
    }

    public String getWord() {
        return word;
    }

    public ArrayList<WordOcurrence> getWordOcurrenceList() {
        return wordOcurrenceList;
    }
}
