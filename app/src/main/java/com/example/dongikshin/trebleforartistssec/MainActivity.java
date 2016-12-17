package com.example.dongikshin.trebleforartistssec;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity  {

    int page = 0;
    LinearLayout layout;
    LinearLayout layout2;
    LinearLayout layout3;
    Button Bmenu;
    Button Bleft;
    Button Bright;
    Button Bmate;
    Button Bhome;
    RelativeLayout layout5;
    TextView textview;
    CalendarView cal;

    private GoogleMap googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView)).getMap();
    private SensorManager mSensorManager;
    private boolean mCompassEnabled;

    private static final int SELECT_VIDEO = 3;
    private String selectedPath;

    int year,month,dayOfMonth;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (LinearLayout) findViewById(R.id.Layout_menu);
        layout2 = (LinearLayout) findViewById(R.id.Layout_artistsonly);
        layout3 = (LinearLayout) findViewById(R.id.Layout_video);
        layout5 = (RelativeLayout) findViewById(R.id.layout_schedule);

        Bmenu = (Button) findViewById(R.id.button_menu);
        Bleft = (Button) findViewById(R.id.button_left);
        Bright = (Button) findViewById(R.id.button_right);
        Bmate = (Button) findViewById(R.id.button_mate);
        Bhome = (Button) findViewById(R.id.button_home);

        cal = (CalendarView)findViewById(R.id.calendarView1);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(CalendarView view, int y, int m,
                                            int d){
                year = y;
                month = m;
                dayOfMonth = d;
                //Toast.makeText(getApplicationContext(),""+year+"/"+(month+1)+"/"+dayOfMonth,Toast.LENGTH_SHORT).show();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        startLocationService();

    }


    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "비디오를 골라주세요 "), SELECT_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                Uri selectedImageUri = data.getData();
                selectedPath = getPath(selectedImageUri);
                uploadVideo();
            }
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();
        return path;
    }

    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {
            ProgressDialog uploading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(MainActivity.this, "파일을 업로드 중입니다", "기다려 주세요...", false, false);
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.uploadVideo(selectedPath);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    public void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

    }

    public void onPause() {
        super.onPause();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(false);
        if(mCompassEnabled){
            mSensorManager.unregisterListener(mListener);
        }
    }
    private void startLocationService(){
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GPSListner gpsLisner = new GPSListener():
        long minTIme = 10000;
        float minDistance =0;

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTIme, minDistance, gpsListener);


    }

    private  class GPSListner implements LocationListener{
        public void onLocationChanged(Location location){}
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String msg = "Latitude" + latitude + "\nLongitude:"+ longitude;
        showCurrentLocation(latitude, longitude);
    }

    @param latitude
    @param longitude

    private void showCurrentLocation(double latitude, double longitude){
        LatLng curpoint = new LatLng(latitude, longitude);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curpoint, 15));

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
    private final SensprEventListner mListener = new SensorEventListener(){
        private int iOrientation = -1;

        public void onAccuracyChanged(Sensor sensor, int accuracy){
            public void onSensorChanged(SensorEvent event){
                if(iOrientation <0)
                    iOrientation = ((WindowManager)) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            }
        }
    }


    //UI start
    public void onMenuClick(View v) {
        layout.setVisibility(View.VISIBLE);
        layout3.setVisibility(View.GONE);;

    }

    public void onMateClick(View v) {
        layout.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), "개발 중이에요 :)", Toast.LENGTH_LONG).show();

    }

    public void onLeftClick(View v) {
        layout.setVisibility(View.GONE);
        layout3.setVisibility(View.VISIBLE);
        if (page == 0) {
            Toast.makeText(getApplicationContext(), "첫번 째 페이지 입니다", Toast.LENGTH_LONG).show();
        }
    }

    public void onRightClick(View v) {
        layout.setVisibility(View.GONE);
        layout3.setVisibility(View.VISIBLE);
    }

    public void onHomeClick(View v) {
        layout.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.VISIBLE);

    }

    public void onHotliveClick(View v) {
        Toast.makeText(getApplicationContext(), "개발 중이에요 :)", Toast.LENGTH_LONG).show();
    }

    public void onRecordClick(View v) {
        Toast.makeText(getApplicationContext(), "개발 중이에요 :)", Toast.LENGTH_LONG).show();
    }

    public void onNewsClick(View v) {
        Toast.makeText(getApplicationContext(), "개발 중이에요 :)", Toast.LENGTH_LONG).show();
    }

    public void onStarChartClick(View v) {
        Toast.makeText(getApplicationContext(), "개발 중이에요 :)", Toast.LENGTH_LONG).show();
    }

    public void onStreetChartClick(View v) {
        Toast.makeText(getApplicationContext(), "개발 중이에요 :)", Toast.LENGTH_LONG).show();
    }

    public void onClassClick(View v) {
        Toast.makeText(getApplicationContext(), "개발 중이에요 :)", Toast.LENGTH_LONG).show();
    }

    public void onArtistClick(View v) {
        layout.setVisibility(View.GONE);
        layout2.setVisibility(View.VISIBLE);
        layout3.setVisibility(View.VISIBLE);

    }

    public void onHistoryClick(View v) {
        Toast.makeText(getApplicationContext(), "개발 중이에요 :)", Toast.LENGTH_LONG).show();
    }

    public void onAboutTrebleClick(View v) {
        Toast.makeText(getApplicationContext(), "개발 중이에요 :)", Toast.LENGTH_LONG).show();
    }

    public void onSettingClick(View v) {
        Toast.makeText(getApplicationContext(), "개발 중이에요 :)", Toast.LENGTH_LONG).show();
    }

    public void onUploadClick(View v) {
        Toast.makeText(getApplicationContext(), "개발 중이에요 :)", Toast.LENGTH_LONG).show();
    }

    public void onLiveClick(View v) {
        Toast.makeText(getApplicationContext(), "개발 중이에요 :)", Toast.LENGTH_LONG).show();
    }

    public void onScheduleClick(View v) {
        layout.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);

        layout5.setVisibility(View.VISIBLE);
        Bmenu.setVisibility(View.GONE);
        Bleft.setVisibility(View.GONE);
        Bright.setVisibility(View.GONE);
        Bmate.setVisibility(View.GONE);
        Bhome.setVisibility(View.GONE);

    }
    public void onAddClick(View v) {
        Toast.makeText(getApplicationContext(), "개발 중이에요 :)", Toast.LENGTH_LONG).show();
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void onCancelClick(View v) {
        Toast.makeText(getApplicationContext(), "개발 중이에요 :)", Toast.LENGTH_LONG).show();
    }

    public void onComSetClick(View v) {
        layout.setVisibility(View.GONE);
        layout5.setVisibility(View.GONE);
        layout2.setVisibility(View.VISIBLE);
        layout3.setVisibility(View.VISIBLE);

        Bmenu.setVisibility(View.VISIBLE);
        Bleft.setVisibility(View.VISIBLE);
        Bright.setVisibility(View.VISIBLE);
        Bmate.setVisibility(View.VISIBLE);
        Bhome.setVisibility(View.VISIBLE);

    }
    //UI end
}
