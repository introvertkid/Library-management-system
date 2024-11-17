package library;

public class Report {
    private int reportID;
    private int userID;
    private String reportType;
    private String title;
    private String content;
    private String status;

    public Report(int reportID, int userID, String reportType, String title, String content, String status) {
        this.reportID = reportID;
        this.userID = userID;
        this.reportType = reportType;
        this.title = title;
        this.content = content;
        this.status = status;
    }

    public int getReportID() { return reportID; }
    public int getUserID() { return userID; }
    public String getReportType() { return reportType; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
