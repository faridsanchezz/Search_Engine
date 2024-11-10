package model;
import java.util.*;

public class Metadata {

    private final String book_id;
    private final String name;
    private final String author;
    private final int year;
    private final String language;
    private final String downloadLink;

    public Metadata(String book_id, String name, String author, int year, String language, String downloadLink) {
        this.book_id = book_id;
        this.name = name;
        this.author = author;
        this.year = year;
        this.language = language;
        this.downloadLink = downloadLink;
    }

    public String getBook_id() {
        return book_id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public String getLanguage() {
        return language;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    @Override
    public String toString() {
        return book_id + "\n" +
                "    " + name + "\n" +
                "    " + author + "\n" +
                "    " + year + "\n" +
                "    " + language + "\n" +
                "    " + downloadLink + "\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(book_id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Metadata other = (Metadata) obj;
        return Objects.equals(book_id, other.book_id);
    }

}
