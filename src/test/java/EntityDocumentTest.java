import org.junit.Test;
import library.entity.Document;

public class EntityDocumentTest {
    @Test
    public void testEntityDocument() {
        Document document = new Document(123,
                "hjhj",
                "haha",
                "abc.xyz",
                "ok",
                "abc, 123");

        assert document.getDocumentName().equals("hjhj");
        assert document.getAuthors().equals("haha");
        assert document.getTagName().equals("abc, 123");
        assert document.getFileName().equals("abc.xyz");
    }
}
