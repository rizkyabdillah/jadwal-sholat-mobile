package a.a.part.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import a.a.fixjadwal.R;
import a.a.part.rest.lokasimasjid.model.Context;
import a.a.part.rest.lokasimasjid.model.Feature;
import a.a.part.rest.lokasimasjid.model.MasjidResponse;

public class CariMasjidAdapter extends RecyclerView.Adapter<CariMasjidAdapter.CariMasjidViewHolder> {

    private MasjidResponse response;
    private double latitude, longitude;
    private Activity activity;

    public CariMasjidAdapter(MasjidResponse response, double latitude, double longitude, Activity activity) {
        this.response = response; this.latitude = latitude; this.longitude = longitude; this.activity = activity;
    }

    @NonNull
    @Override
    public CariMasjidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_masjid_adapter, parent, false);
        return new CariMasjidViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CariMasjidViewHolder holder, int position) {
        List<Feature> data = response.getFeatures();
        holder.getNama_masjid().setText(data.get(position).getText());

        List<Context> data_lokasi = data.get(position).getContext();
        final String lokasi = data_lokasi.get(0).getText().concat(", ")
            .concat(data_lokasi.get((data_lokasi.size() < 4) ? 1 : 2).getText())
            .concat(", ").concat(data_lokasi.get((data_lokasi.size() < 4) ? 2 : 3).getText());
        holder.getLokasi_masjid().setText(lokasi);

        holder.getJarak().setText("" + calculateDistance(this.latitude, this.longitude,
                data.get(position).getCenter().get(1), data.get(position).getCenter().get(0)) + " KM");

        holder.getBtn_buka_lokasi().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String URI = "https://www.google.com/maps/search/?api=1&query=" +
                        data.get(position).getCenter().get(1) + "," + data.get(position).getCenter().get(0);
                activity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(URI)));
            }
        });

        holder.getBtn_buka_rute().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String URI = "https://www.google.com/maps/dir/?api=1&origin="
                    + latitude + "," + longitude + "&destination=" + data.get(position).getCenter().get(1)
                    + "," + data.get(position).getCenter().get(0) + "&travelmode=driving";
                activity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(URI)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return (response != null) ? response.getFeatures().size() : 0;
    }

    private int  calculateDistance(double oriLat, double oriLng, double disLat, double disLng) {
        try{
            final double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
            double latDistance = Math.toRadians(oriLat - disLat);
            double lngDistance = Math.toRadians(oriLng - disLng);

            double average = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(oriLat)) * Math.cos(Math.toRadians(disLat))
                    * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

            double degrees = 2 * Math.atan2(Math.sqrt(average), Math.sqrt(1 - average));
            return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * degrees));

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static class CariMasjidViewHolder extends RecyclerView.ViewHolder {
        private TextView nama_masjid, lokasi_masjid, jarak;
        private MaterialButton btn_buka_lokasi, btn_buka_rute;

        public CariMasjidViewHolder(@NonNull View itemView) {
            super(itemView);
            nama_masjid = (TextView) itemView.findViewById(R.id.nama_masjid);
            lokasi_masjid = (TextView) itemView.findViewById(R.id.lokasi_masjid);
            jarak = (TextView) itemView.findViewById(R.id.jarak);
            btn_buka_lokasi = (MaterialButton) itemView.findViewById(R.id.btn_buka_lokasi);
            btn_buka_rute = (MaterialButton) itemView.findViewById(R.id.btn_buka_rute);
        }

        public TextView getNama_masjid() {
            return this.nama_masjid;
        }

        public TextView getLokasi_masjid() {
            return this.lokasi_masjid;
        }

        public MaterialButton getBtn_buka_lokasi() {
            return this.btn_buka_lokasi;
        }

        public MaterialButton getBtn_buka_rute() {
            return this.btn_buka_rute;
        }

        public TextView getJarak() {
            return jarak;
        }
    }
}
