package a.a.part.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.intentfilter.androidpermissions.PermissionManager;
import com.intentfilter.androidpermissions.models.DeniedPermissions;

import java.util.Arrays;
import java.util.List;

import a.a.fixjadwal.R;
import a.a.part.rest.RestApi;
import a.a.part.rest.jadwalsholat.model.Data__;
import a.a.part.rest.jadwalsholat.model.JadwalResponse;
import a.a.part.rest.jadwalsholat.model.Text;
import a.a.part.view.utility.Utility;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JadwalSholatActivity extends AppCompatActivity {

    private TextView hijriah, masehi, kota, imsak, subuh, terbit, dhuha, dzuhur, ashar, maghrib, isya;
    private PermissionManager permissionManager;

    private boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_sholat);

        hijriah = (TextView) findViewById(R.id.hijriah);
        masehi = (TextView) findViewById(R.id.masehi);
        kota = (TextView) findViewById(R.id.kota);
        imsak = (TextView) findViewById(R.id.imsak);
        subuh = (TextView) findViewById(R.id.subuh);
        terbit = (TextView) findViewById(R.id.terbit);
        dhuha = (TextView) findViewById(R.id.dhuha);
        dzuhur = (TextView) findViewById(R.id.dzuhur);
        ashar = (TextView) findViewById(R.id.ashar);
        maghrib = (TextView) findViewById(R.id.maghrib);
        isya = (TextView) findViewById(R.id.isya);

        Button change_date = (Button) findViewById(R.id.ubah_tanggal);

        // Where button change date is on clicking
        change_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if gps provider is enabled
                if(Utility.isProviderEnable(JadwalSholatActivity.this)) {
                    // Where gps provider is enabled than show date picker dialog
                    new DatePickerDialog(JadwalSholatActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Utility.showProgressDialog(JadwalSholatActivity.this);
                            // Show jadwal sholat using date obtained from date picker
                            // and using latitude, longitude from Utility temporary for to
                            // avoid location repeatedly
                            showJadwalSholat(year, month, dayOfMonth, Utility.getLatitude(), Utility.getLongitude(), Utility.getGMT());
                        }
                    }, Utility.getYear(), Utility.getMonth(), Utility.getDay()).show();
                } else {
                    // Where gps provider is not enabled, than show toast information
                    Toast.makeText(getApplicationContext(), "Gps anda belum aktif", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if gps provider is not enabled
        if(!isLoaded) {
            if (!Utility.isProviderEnable(JadwalSholatActivity.this)) {
                // If is not enabled showing alert dialog
                Utility.showDialogEnableGps(JadwalSholatActivity.this);
            } else {
                cekPermission();
            }
        }
    }

    // Request permission function
    private void cekPermission() {
        // Show permission dialog using library androidPermission
        permissionManager = PermissionManager.getInstance(JadwalSholatActivity.this);
        permissionManager.checkPermissions(Arrays.asList(Utility.PERMISSION),
            new PermissionManager.PermissionRequestListener() {
                @Override
                public void onPermissionGranted() {
                    // Write command here where permission is granted
                    showLocation();
                }

                @Override
                public void onPermissionDenied(DeniedPermissions deniedPermissions) {
                    // Write command here where permission is denied
                    Utility.showDialogSetting(JadwalSholatActivity.this, permissionManager);
                }
        });
    }

    private void showLocation() {
        // Start ProgressDialog
        Utility.showProgressDialog(JadwalSholatActivity.this);

        // Get location using library SmartLocation
        SmartLocation.with(JadwalSholatActivity.this).location().config(LocationParams.BEST_EFFORT).oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {

                // Get location using reverse geocode
                SmartLocation.with(JadwalSholatActivity.this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                    @Override
                    public void onAddressResolved(Location location, List<Address> list) {
                        String text_kota = null;
                        if (list.size() > 0) {
                            String kab = list.get(0).getSubAdminArea();
                            String kecamatan = list.get(0).getLocality();
                            String negara = list.get(0).getCountryName();

                            // Setting text kota with kecamatan, kab - negara
                            text_kota = kecamatan.concat(", ").concat(kab).concat(" - ").concat(negara);
                        } else {
                            text_kota = "Location Not Found!";
                        }
                        kota.setText(text_kota);

                    }
                });

                // Save latitude and longitude into Utility as temporary
                Utility.setLatitude(location.getLatitude());
                Utility.setLongitude(location.getLongitude());

                // Show jadwal sholat using response API
                showJadwalSholat(
                    Utility.getYear(), Utility.getMonth(), Utility.getDay(),
                    location.getLatitude(), location.getLongitude(), Utility.getGMT()
                );
            }
        });
    }

    private void showJadwalSholat(int year, int month, int day, double latitude, double longitude, int timezone) {
        // Rest API
        Call<JadwalResponse> responses = RestApi.jadwalService().getJadwalSholat(year, month + 1,day, latitude, longitude, timezone);
        responses.enqueue(new Callback<JadwalResponse>() {
            @Override
            public void onResponse(Call<JadwalResponse> call, Response<JadwalResponse> response) {
                if(response.isSuccessful()) {
                    Data__ jadwal = response.body().getData().getData().getData();
                    imsak.setText(jadwal.getShortImsak());
                    subuh.setText(jadwal.getShortShubuh());
                    terbit.setText(jadwal.getShortSyuruq());
                    dhuha.setText(jadwal.getShortDhuha());
                    dzuhur.setText(jadwal.getShortDhuhur());
                    ashar.setText(jadwal.getShortAshar());
                    maghrib.setText(jadwal.getShortMaghrib());
                    isya.setText(jadwal.getShortIsya());

                    Text dates = response.body().getData().getData().getDate().getText();
                    hijriah.setText(dates.getH());
                    masehi.setText(dates.getM());

                    isLoaded = true;
                }
                Utility.dialogDissmiss();
            }

            @Override
            public void onFailure(Call<JadwalResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Data jadwal tidak ditemukan", Toast.LENGTH_SHORT).show();
                Utility.dialogDissmiss();
            }
        });
    }

}