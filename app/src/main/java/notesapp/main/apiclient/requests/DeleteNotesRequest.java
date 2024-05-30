package notesapp.main.apiclient.requests;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DeleteNotesRequest {

    @SerializedName("uuids")
    private List<String> uuids;

    public DeleteNotesRequest(List<String> uuids) {
        this.uuids = uuids;
    }
}
