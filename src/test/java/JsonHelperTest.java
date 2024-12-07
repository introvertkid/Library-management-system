import library.helper.JsonHelper;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class JsonHelperTest {
    @Test
    public void decodeURLTest1() {
        String url = "\\u0026";
        String expected = "&";
        String result = JsonHelper.decodeURL(url);
        assertEquals(expected, result);
    }

    @Test
    public void decodeURLTest2() {
        String url = "\\u003d";
        String expected = "=";
        String result = JsonHelper.decodeURL(url);
        assertEquals(expected, result);
    }

    @Test
    public void decodeURLTest3() {
        String url = "\\u003c";
        String expected = "<";
        String result = JsonHelper.decodeURL(url);
        assertEquals(expected, result);
    }
}
