package example.com.capstoneproject.gui_layer.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import example.com.capstoneproject.R;
import example.com.capstoneproject.management_layer.web_services.WeatherSyncAdapter;

public class PermisionActivity extends AppCompatActivity
{

    private static final int REQUEST_LOCATION_PERMISSIONS = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permision);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            //TODO

        }
        else
        {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSIONS);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_LOCATION_PERMISSIONS)
        {
            for (int i = 0; i < permissions.length; i++)
            {
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    WeatherSyncAdapter.syncImmediately(this);
            }
        }
    }
}
