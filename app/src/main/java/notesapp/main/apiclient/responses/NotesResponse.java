package notesapp.main.apiclient.responses;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NotesResponse {

    @SerializedName("notes")
    private List<NoteItem> notes;

    public List<NoteItem> getNotes() {
        return notes;
    }

    public static class NoteItem {

        @SerializedName("uuid")
        private String uuid;

        @SerializedName("hash")
        private String hash;

        public String getUuid() {
            return uuid;
        }

        public String getHash() {
            return hash;
        }
    }
}
