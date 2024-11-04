package Control;

public class MetaData {
    private String bookId;
    private String author;
    private String title;
    private String language;
    private String year;
    private String downloadLink;

    public MetaData(String bookId, String author, String title, String language, String year, String downloadLink) {
        this.bookId = bookId;
        this.author = author;
        this.title = title;
        this.language = language;
        this.year = year;
        this.downloadLink = downloadLink;
    }

    public String getBookId() {
        return bookId;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public String getYear() {
        return year;
    }

    public String getDownloadLink() {
        return downloadLink;
    }
}
