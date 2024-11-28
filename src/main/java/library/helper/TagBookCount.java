package library.helper;

public class TagBookCount {
    private final String tagName;
    private final int totalBooks;

    public TagBookCount(String tagName, int totalBooks) {
        this.tagName = tagName;
        this.totalBooks = totalBooks;
    }

    public String getTagName() {
        return tagName;
    }

    public int getTotalBooks() {
        return totalBooks;
    }
}
