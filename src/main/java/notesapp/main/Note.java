package notesapp.main;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note implements Serializable {

    private Date date;
    private String title, content;

    public Note(Date date, String title, String content) {
        this.date = date;
        this.title = title;
        this.content = content;
    }

    public Note(Date date, String title) {
        this.date = date;
        this.title = title;
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

    @Override
    public String toString() {
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return title+" on "+sdf.format(date);
    }
}
