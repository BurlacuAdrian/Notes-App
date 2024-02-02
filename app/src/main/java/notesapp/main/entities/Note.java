package notesapp.main.entities;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note implements Serializable {

    private Date date, lastEdited;
    private String title, content, color, hash;

    public Note(Date date, String title, String content, String color) {
        this.date = date;
        this.lastEdited = date;
        this.title = title;
        this.content = content;
        updateHash(content);
        this.color=color;
    }

    public Note(Date date, String title, String content) {
        this.date = date;
        this.lastEdited = date;
        this.title = title;
        this.content = content;
        updateHash(content);
    }

    public Note(Date date, String title) {
        this.date = date;
        this.title = title;
    }

    public Note() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(Date lastEdited) {
        this.lastEdited = lastEdited;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return title+" on "+sdf.format(date);
    }

    public String calculateHash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(content.getBytes());
            StringBuilder hashBuilder = new StringBuilder();

            for (byte b : hashBytes) {
                hashBuilder.append(String.format("%02x", b));
            }

            return hashBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle exception
            return null;
        }
    }

    public void setContentAndHash(String newContent) {
        this.content=newContent;
        this.hash = calculateHash(newContent);
    }

    public void updateHash(String newContent){
        this.hash=calculateHash(newContent);
    }

    public boolean hasContentChanged(String newContent){
        return !this.hash.equals(calculateHash(newContent));
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
