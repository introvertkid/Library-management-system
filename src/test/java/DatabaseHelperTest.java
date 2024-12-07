import library.helper.DatabaseHelper;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

public class DatabaseHelperTest {
    @Test
    public void testGetConnection() throws SQLException {
        Assert.assertNotNull(DatabaseHelper.getConnection());
    }
}
