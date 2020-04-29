package com.application.earthquaquewatcher.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.application.earthquaquewatcher.Model.EarthQuake;
import com.application.earthquaquewatcher.R;
import com.application.earthquaquewatcher.Util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EarthquakeListActivity extends AppCompatActivity {
    private RequestQueue queue;

    private ListView listView;
    private ArrayList arrayList;
    private ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_list);
        queue = Volley.newRequestQueue(this);
        listView = (ListView) findViewById(R.id.lstEarthquakeID);
        arrayList = new ArrayList();


        showList();
    }

    public void showList() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray features = null;
                        StringBuilder stringBuilder = new StringBuilder();
                        try {
                            features = response.getJSONArray("features");
                            for (int i = 0; i < Constants.LIMIT; i++) {
                                JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                                EarthQuake earthQuake = new EarthQuake();
                                earthQuake.setPlace(properties.getString("place"));
                                arrayList.add(earthQuake.getPlace());
                            }

                            arrayAdapter = new ArrayAdapter(EarthquakeListActivity.this, android.R.layout.simple_list_item_1,
                                    android.R.id.text1, arrayList);
                            listView.setAdapter(arrayAdapter);
                            arrayAdapter.notifyDataSetChanged();


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
