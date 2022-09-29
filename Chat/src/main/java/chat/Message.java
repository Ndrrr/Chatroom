package chat;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String ANSI_RESET = "\u001B[0m";

    private String message;
    private String sender;
    private LocalDateTime dateTime;
    public Message(String message){
        this.message = message;
    }
    public Message(String message, String sender, LocalDateTime dateTime){
        this.message = message;
        this.sender = sender;
        this.dateTime = dateTime;
    }
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getSender(){
        return sender;
    }
    public void setSender(String sender){
        this.sender = sender;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", sender='" + sender + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }

    public String formatted(){
        return dateTime.format(
                java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm"))
                + " " + sender + ANSI_RESET + ": " + message;
    }
}

