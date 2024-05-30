package notesapp.main.utils;

import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import notesapp.main.NotesApp;
import notesapp.main.entities.Note;

public abstract class UtilityFunctions {

    public static String getTimeAgo(Date date) {
        if (date == null) {
            return null;
        }

        long timeDifference = System.currentTimeMillis() - date.getTime();
        long seconds = timeDifference / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days / 30; // Approximating a month as 30 days

        if (months > 0) {
            return months + (months == 1 ? " month ago" : " months ago");
        } else if (days > 0) {
            return days + (days == 1 ? " day ago" : " days ago");
        } else if (hours > 0) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else if (minutes > 0) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        } else {
            return seconds + (seconds == 1 ? " second ago" : " seconds ago");
        }
    }

    public static String formatDateWithSuffix(Date date) {
        // Define the desired date format with the day of the month suffix
        SimpleDateFormat dateFormat = new SimpleDateFormat("d' 'MMMM, yyyy", Locale.getDefault());

        // Get the day of the month
        int day = Integer.parseInt(new SimpleDateFormat("d", Locale.getDefault()).format(date));

        // Determine the day suffix
        String suffix;
        if (day >= 11 && day <= 13) {
            suffix = "th";
        } else {
            switch (day % 10) {
                case 1:
                    suffix = "st";
                    break;
                case 2:
                    suffix = "nd";
                    break;
                case 3:
                    suffix = "rd";
                    break;
                default:
                    suffix = "th";
                    break;
            }
        }

        // Append the suffix to the day of the month in the format
        return dateFormat.format(date).replaceFirst("d", day + suffix);
    }

    public static String createJsonString(List<Note> notesToBeConverted) {
        StringBuilder jsonBuilder = new StringBuilder("{ \"notes\" : [ ");
        int index = 0;

        for (Note note : notesToBeConverted) {
            if (index != 0) {
                jsonBuilder.append(",");
            }
            index++;

            jsonBuilder.append("{ \"uuid\": \"").append(note.getUuId()).append("\",");
            jsonBuilder.append("\"date\": \"").append(note.getLastEdited()).append("\",");
            jsonBuilder.append("\"title\": \"").append(note.getTitle()).append("\",");
            jsonBuilder.append("\"content\": \"").append(note.getContent()).append("\",");
            jsonBuilder.append("\"color\": \"").append(note.getColor()).append("\",");
            jsonBuilder.append("\"hash\": \"").append(note.getHash()).append("\"}");
        }

        jsonBuilder.append("]}");

        return jsonBuilder.toString();
    }

    public static List<Note> singleItemList(Note note){
        List<Note> singleItemList = new ArrayList<>();
        singleItemList.add(note);
        return singleItemList;
    }

    public static void showToast(String message) {
        if (NotesApp.getInstance() != null) {
            Toast.makeText(NotesApp.getInstance().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

}
