package com.application.earthquaquewatcher.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.application.earthquaquewatcher.Model.EarthQuake;
import com.application.earthquaquewatcher.R;
import com.application.earthquaquewatcher.UI.CustomInfoWindow;
import com.application.earthquaquewatcher.Util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private String[] PERMISSIONS;
    private final int PERMISSION_CODE =122;

    private RequestQueue queue;

    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;

    private BitmapDescriptor[] iconColors;

    private Button btnShowList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnShowList = (Button) findViewById(R.id.btnShowListID);
        btnShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showList();
                startActivity(new Intent(MapsActivity.this, EarthquakeListActivity.class));
            }
        });

        iconColors = new BitmapDescriptor[]{
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW) };
        queue = Volley.newRequestQueue(this);
        getEarthquakes();

    }

    private void getEarthquakes() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray features = response.getJSONArray("features");
                            for (int i=0; i <Constants.LIMIT; i++){
                                JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                                JSONArray coordinates = features.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");
                                Double lon = coordinates.getDouble(0);
                                Double lat = coordinates.getDouble(1);
                                EarthQuake earthQuake= new EarthQuake(properties.getString("place"),
                                        properties.getLong("time"),
                                        properties.getDouble("mag"),
                                        properties.getString("detail"),
                                        lat,
                                        lon,
                                        properties.getString("type"));

                                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                                String formattedDate = dateFormat.format(new Date(Long.valueOf(properties.getLong("time"))).getTime());

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.icon(iconColors[new java.util.Random().nextInt(iconColors.length)]);
                                markerOptions.title(earthQuake.getPlace());
                                markerOptions.position(new LatLng(earthQuake.getLat(),earthQuake.getLon()));
                                markerOptions.snippet("Magnitude: "
                                + earthQuake.getMagnitude() +
                                        "\n"+ "Date: "+ formattedDate);

                                //add circle to markers with magnitude > 2.0

                                if(earthQuake.getMagnitude() >= 2.5){
                                    CircleOptions circleOptions = new CircleOptions();
                                    circleOptions.center(new LatLng(earthQuake.getLat(),earthQuake.getLon()));
                                    circleOptions.radius(30000);
                                    circleOptions.strokeWidth(3.6f);
                                    circleOptions.fillColor(Color.RED);
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    mMap.addCircle(circleOptions);
                                }

                                Marker  marker= mMap.addMarker(markerOptions);
                                marker.setTag(earthQuake.getUrlDetails());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(earthQuake.getLat(),earthQuake.getLon()),1));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(jsonObjectRequest);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));

        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        PERMISSIONS = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        getQuakeDetails(marker.getTag().toString());
    }

    private void getQuakeDetails(final String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url ,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String urlDetail="";
                        try {
                            JSONObject products = response.getJSONObject("properties").getJSONObject("products");
                            urlDetail = products.getJSONArray("geoserve").getJSONObject(0)
                                    .getJSONObject("contents").getJSONObject("geoserve.json").getString("url");

                            getMoreDetails(urlDetail);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void getMoreDetails(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                        View view = getLayoutInflater().inflate(R.layout.popup, null);

                        Button btnDismiss = view.findViewById(R.id.dissmissPop);
                        btnDismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        Button btnDismissTop = view.findViewById(R.id.dismissPopTopID);
                        btnDismissTop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });


                        TextView popList = (TextView) view.findViewById(R.id.popListID);
                        WebView htmlPop = (WebView) view.findViewById(R.id.htmlWebviewID);

                        StringBuilder stringBuilder = new StringBuilder();
                        try {
                            if(response.has("tectonicSummary") && response.getString("tectonicSummary")!=null){
                                JSONObject tectonic  = response.getJSONObject("tectonicSummary");

                                if (tectonic.has("text") && tectonic.getString("text") != null){
                                    String text = tectonic.getString("text");
                                    htmlPop.loadDataWithBaseURL(null, text, "text/html", "UTF-8", null);
                                }
                            }

                            JSONArray cities = response.getJSONArray("cities");
                            for(int i=0; i < cities.length();i++){
                                JSONObject city = cities.getJSONObject(i);
                                stringBuilder.append("City: "+ city.getString("name") +
                                        "\n" + "Distance: "+city.getString("distance")+
                                        "\n"+ "Population: "+city.getString("population"));

                                stringBuilder.append("\n\n");

                            }

                            popList.setText(stringBuilder);

                            dialogBuilder.setView(view);
                            dialog = dialogBuilder.create();
                            dialog.show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(jsonObjectRequest);
    }

}


