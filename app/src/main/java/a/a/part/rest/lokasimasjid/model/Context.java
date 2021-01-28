package a.a.part.rest.lokasimasjid.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Context {

    @SerializedName("text")
    @Expose
    private String text;

    public String getText() {
        return text;
    }
}