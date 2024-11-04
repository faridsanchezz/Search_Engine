package Control;

import java.util.ArrayList;

public class WordOcurrence {
    private int bookId;
    private int frecuency;
    private ArrayList<Integer> lines;

    public WordOcurrence(int bookId, int frecuency, ArrayList<Integer> lines) {
        this.bookId = bookId;
        this.frecuency = frecuency;
        this.lines = lines;
    }

    public int getBookId() {
        return bookId;
    }

    public int getFrecuency() {
        return frecuency;
    }

    public ArrayList<Integer> getLines() {
        return lines;
    }
}
