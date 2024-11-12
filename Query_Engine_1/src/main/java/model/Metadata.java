package model;

import java.util.Objects;

public class Metadata {

    private final String bookId;
    private final String name;
    private final String author;
    private final int year;
    private final String language;
    private final String downloadLink;

    public Metadata(String bookId, String name, String author, int year, String language, String downloadLink) {
        this.bookId = bookId;
        this.name = name;
        this.author = author;
        this.year = year;
        this.language = language;
        this.downloadLink = downloadLink;
    }

    public String getBookId() {
        return bookId;
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
        return bookId + "\n" +
                "    " + name + "\n" +
                "    " + author + "\n" +
                "    " + year + "\n" +
                "    " + language + "\n" +
                "    " + downloadLink + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Metadata other = (Metadata) obj;
        return Objects.equals(bookId, other.bookId);
    }
}
