package library.entity;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;

public class UserTest {

    @Before
    public void setUp() {
        User.setID(1);
        User.setUsername("minhdeptrai");
        User.setPassword("hashed_password_123");
        User.setUserFullName("Nguyen Minh");
        User.setRole("admin");
        User.setGmail("23021638@vnu.edu.vn");
        User.setPhoneNumber("1234567890");
        User.setDateOfBirth(LocalDate.of(2005, 11, 14));
        User.setAvatar("avatar_url");
    }

    @Test
    public void testUserData() {
        assertEquals(1, User.getID());
        assertEquals("minhdeptrai", User.getUsername());
        assertEquals("hashed_password_123", User.getPassword());
        assertEquals("Nguyen Minh", User.getUserFullName());
        assertEquals("admin", User.getRole());
        assertEquals("23021638@vnu.edu.vn", User.getGmail());
        assertEquals("1234567890", User.getPhoneNumber());
        assertEquals(LocalDate.of(2005, 11, 14), User.getDateOfBirth());
        assertEquals("avatar_url", User.getAvatar());
    }

    @Test
    public void testUsername() {
        User.setUsername("new_username");
        assertEquals("new_username", User.getUsername());
    }

    @Test
    public void testGmail() {
        User.setGmail("new_email@example.com");
        assertEquals("new_email@example.com", User.getGmail());
    }

    @Test
    public void testPhoneNumber() {
        User.setPhoneNumber("0987654321");
        assertEquals("0987654321", User.getPhoneNumber());
    }

    @Test
    public void testDateOfBirth() {
        LocalDate newDate = LocalDate.of(2005, 11, 14);
        User.setDateOfBirth(newDate);
        assertEquals(newDate, User.getDateOfBirth());
    }

    @Test
    public void testAvatar() {
        User.setAvatar("new_avatar_url");
        assertEquals("new_avatar_url", User.getAvatar());
    }

    @Test
    public void testNullValues() {
        User.setUsername(null);
        User.setGmail(null);
        assertNull(User.getUsername());
        assertNull(User.getGmail());
    }
}
