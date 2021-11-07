package pmf.novak101.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    TextView latTextView;
    TextView lonTextView;
    TextView accTextView;
    TextView altTextView;
    TextView addressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latTextView = findViewById(R.id.textViewLattitude);
        lonTextView = findViewById(R.id.textViewLongtitude);
        accTextView = findViewById(R.id.textViewAccuracy);
        altTextView = findViewById(R.id.textViewAltitude);
        addressTextView = findViewById(R.id.textViewAddress);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateLocationInfo(location);

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            Location lastKnwonLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnwonLocation != null){
                updateLocationInfo(lastKnwonLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }

    public void startListening() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void updateLocationInfo(Location location){
        latTextView.setText("Lattitude: "+location.getLatitude());
        lonTextView.setText("Longtitude: "+ location.getLatitude());

        accTextView.setText("Accuracy: "+ location.getAccuracy());
        altTextView.setText("Altitude: "+ location.getAltitude());

        String address = "Could not find address";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> listAdresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (listAdresses != null && listAdresses.size() > 0) {
                address = "Address: \n";
                Address addressAid = listAdresses.get(0);
                if(addressAid.getThoroughfare() != null) {
                    address += addressAid.getThoroughfare()+"\n";
                }
                if(addressAid.getLocality() != null) {
                    address += addressAid.getLocality()+" ";
                }
                if(addressAid.getPostalCode() != null) {
                    address += addressAid.getPostalCode()+" ";
                }
                if(addressAid.getAdminArea() != null) {
                    address += addressAid.getAdminArea();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        addressTextView.setText(address);

    }
}