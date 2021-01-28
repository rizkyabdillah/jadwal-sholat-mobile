package a.a.part.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import a.a.fixjadwal.R;
import a.a.part.adapter.CariMasjidAdapter;
import a.a.part.rest.RestApi;
import a.a.part.rest.lokasimasjid.model.MasjidResponse;
import a.a.part.view.utility.Utility;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.intentfilter.androidpermissions.PermissionManager;
import com.intentfilter.androidpermissions.models.DeniedPermissions;

import java.util.Arrays;

public class CariMasjidActivity extends AppCompatActivity {

    private PermissionManager permissionManager;
    private final String ACCESS_TOKEN = "pk.eyJ1Ijoicml6a3lha3MiLCJhIjoiY2tpbXF2OHU4MHcxNTJzcng4cXFpbnVoMCJ9.G0nNs9E5wdYaAbpuZw0-2A";
    private final int LIMIT = 5;
    private final String QUERY = "Mosque";
    private RecyclerView recyclerView;

    private boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_masjid);

        recyclerView = (RecyclerView) findViewById(R.id.list_cari_masjid);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if gps provider is not enabled
        if(!isLoaded) {
            if (!Utility.isProviderEnable(CariMasjidActivity.this)) {
                // If is not enabled showing alert dialog
                Utility.showDialogEnableGps(CariMasjidActivity.this);
            } else {
                cekPermission();
            }
        }
    }

    private void cekPermission() {
        // Show permission dialog using library androidPermission
        permissionManager = PermissionManager.getInstance(CariMasjidActivity.this);
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
                    Utility.showDialogSetting(CariMasjidActivity.this, permissionManager);
                }
        });
    }

    private void showLocation() {
        // Start ProgressDialog
        Utility.showProgressDialog(CariMasjidActivity.this);
        // Get location using library SmartLocation
        SmartLocation.with(CariMasjidActivity.this).location().config(LocationParams.BEST_EFFORT).oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                // Show jadwal sholat using response API
                showLokasiMasjid( location.getLatitude(),location.getLongitude());
            }
        });
    }

    private void showLokasiMasjid(double latitude, double longitude) {
        final String proximity = "" + longitude + "," + latitude;
        Call<MasjidResponse> response = RestApi.masjidService().getMasjidTerdekat(QUERY, proximity, LIMIT, ACCESS_TOKEN);
        response.enqueue(new Callback<MasjidResponse>() {
            @Override
            public void onResponse(Call<MasjidResponse> call, Response<MasjidResponse> response) {
                if(response.isSuccessful()) {
                    final MasjidResponse masjidResponse = response.body();

                    CariMasjidAdapter adapter = new CariMasjidAdapter(masjidResponse,latitude, longitude,CariMasjidActivity.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(CariMasjidActivity.this));
                    recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(adapter);

                    isLoaded = true;
                }
                Utility.dialogDissmiss();
            }

            @Override
            public void onFailure(Call<MasjidResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Data masjid tidak ditemukan",
                        Toast.LENGTH_SHORT).show();
                Utility.dialogDissmiss();
            }
        });
    }
}