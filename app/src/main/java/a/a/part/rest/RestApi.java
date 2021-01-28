package a.a.part.rest;

import a.a.part.rest.jadwalsholat.service.JadwalApiService;
import a.a.part.rest.lokasimasjid.service.MasjidApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApi {

    private static final String URL_JADWAL = "https://kalenderindonesia.com/";

    public static JadwalApiService jadwalService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(URL_JADWAL)
            .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(JadwalApiService.class);
    }


    private static final String URL_MASJID = "https://api.mapbox.com/";

    public static MasjidApiService masjidService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(URL_MASJID)
            .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(MasjidApiService.class);
    }

}
