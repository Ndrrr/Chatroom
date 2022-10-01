package chatroom;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import javax.crypto.*;

public class Message implements Serializable, Comparable<Message>, Cloneable{

    private static final long serialVersionUID = 1L;
    public static Message START= new Message(-1,"start", "START");
    public static Message END = new Message(-2, "end", "END");
    public static final Object[] colors = Arrays.stream(ANSI.Colors.values()).map(ANSI.Colors::getValue).toArray();
    private int id;
    private String message;
    private String sender;
    private LocalDateTime dateTime;
    private int color;
    public Message(){}
    public Message(int id, String message){
        this.id = id;
        this.message = message;
    }
    public Message(int id, String message, String sender){
        this(id,message);
        this.sender = sender;
    }
    public Message(int id, String message, String sender, LocalDateTime dateTime){
        this(id, message, sender);
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

    @Override
    public Message clone(){
        Message msg = new Message();
        msg.setId(this.id);
        msg.setColor(this.color);
        msg.setSender(this.sender);
        msg.setMessage(this.message);
        msg.setDateTime(this.dateTime);
        return msg;
    }
}

