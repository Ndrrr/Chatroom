package chat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import javax.crypto.*;

public class Message implements Serializable, Comparable<Message>{

    private static final long serialVersionUID = 1L;
    public static Message START= new Message(-1,"start");
    public static Message END = new Message(-2, "end");
    public static final Object[] colors = Arrays.stream(ANSI.Colors.values()).map(ANSI.Colors::getValue).toArray();
    private int id;
    private String message;
    private String sender;
    private LocalDateTime dateTime;
    private int color;
    private SealedObject encryptedMessage;
    public Message(){}
    public Message(int id, String message){
        this.id = id;
        this.message = message;
    }
    public Message(int id, String message, String sender, LocalDateTime dateTime){
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color % colors.length;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public SealedObject getEncryptedMessage() {
        return encryptedMessage;
    }

    public void setEncryptedMessage(SealedObject encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
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
        return colors[color] + dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM HH:mm:ss"))
                + " " +ANSI.BOLD.getValue() + sender + ANSI.RESET_FORMAT.getValue() + ": " + message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return getId() == message.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public int compareTo(Message o) {
        return dateTime.compareTo(o.getDateTime());
    }
}

