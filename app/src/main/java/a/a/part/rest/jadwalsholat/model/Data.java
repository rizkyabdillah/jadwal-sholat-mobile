package a.a.part.rest.jadwalsholat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("data")
    @Expose
    private Data_ data;

    public Data_ getData() {
        return data;
    }

}
