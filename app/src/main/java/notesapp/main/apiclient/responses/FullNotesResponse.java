package notesapp.main.apiclient.responses;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FullNotesResponse {

    @SerializedName("notes")
    private List<NoteItem> notes;

    public List<NoteItem> getNotes() {
        return notes;
    }

    public static class NoteItem {

        @SerializedName("uuid")
        private String uuid;

        @SerializedName("date")
        private String date;

        @SerializedName("lastEdited")
        private String lastEdited;

        @SerializedName("title")
        private String title;

        @SerializedName("content")
        private String content;

        @SerializedName("color")
        private String color;

        @SerializedName("hash")
        private String hash;

        @SerializedName("owned_by")
        private String ownedBy;

        public String getUuid() {
            return uuid;
        }

        public String getDate() {
            return date;
        }

        public String getLastEdited() {
            return lastEdited;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getColor() {
            return color;
        }

        public String getHash() {
            return hash;
        }

        public String getOwnedBy() {
            return ownedBy;
        }
    }
}
