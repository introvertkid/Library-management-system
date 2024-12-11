package library.entity;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ReportTest {

    @Test
    public void testConstructorAndGetters() {
        int reportID = 1;
        int userID = 101;
        String reportType = "Bug";
        String title = "Issue with Login";
        String content = "Unable to login";
        String status = "Pending";

        Report report = new Report(reportID, userID, reportType, title, content, status);

        assertEquals(reportID, report.getReportID());
        assertEquals(userID, report.getUserID());
        assertEquals(reportType, report.getReportType());
        assertEquals(title, report.getTitle());
        assertEquals(content, report.getContent());
        assertEquals(status, report.getStatus());
    }

    @Test
    public void testSetStatus() {
        int reportID = 2;
        int userID = 102;
        String reportType = "Bug";
        String title = "Can't load document";
        String content = "Empty document list";
        String status = "Pending";

        Report report = new Report(reportID, userID, reportType, title, content, status);

        String newStatus = "Handled";
        report.setStatus(newStatus);

        assertEquals(newStatus, report.getStatus());
    }
}
