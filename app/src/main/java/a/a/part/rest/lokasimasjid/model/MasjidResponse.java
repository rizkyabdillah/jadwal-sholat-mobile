package a.a.part.rest.lokasimasjid.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MasjidResponse {

    @SerializedName("features")
    @Expose
    private List<Feature> features = null;

    public List<Feature> getFeatures() {
        return features;
    }
}