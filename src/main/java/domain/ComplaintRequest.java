package domain;

import java.util.Date;

public class ComplaintRequest {
    private String sender;
    private String recipient;
    private Date date;
    private Booking booking;
    private String text;
    private boolean isUrgent;

    public ComplaintRequest(String sender, String recipient, Date date, Booking booking, String text, boolean isUrgent) {
        this.sender = sender;
        this.recipient = recipient;
        this.date = date;
        this.booking = booking;
        this.text = text;
        this.isUrgent = isUrgent;
    }

    // Getters y Setters
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isUrgent() { return isUrgent; }
    public void setUrgent(boolean urgent) { isUrgent = urgent; }
}
