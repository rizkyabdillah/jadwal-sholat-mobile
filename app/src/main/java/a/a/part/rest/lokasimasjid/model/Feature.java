package a.a.part.rest.lokasimasjid.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feature {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("place_name")
    @Expose
    private String placeName;
    @SerializedName("center")
    @Expose
    private List<Float> center = null;
    @SerializedName("context")
    @Expose
    private List<Context> context = null;

    public String getText() {
        return text;
    }

    public String getPlaceName() {
        return placeName;
    }

    public List<Float> getCenter() {
        return center;
    }

    public List<Context> getContext() {
        return context;
    }

}