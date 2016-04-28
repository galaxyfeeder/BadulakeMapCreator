package com.tomorrowdev.badulakemapcreator;

import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ActionBarActivity {

    private EditText name;
    private Switch twentyfour;
    private TextView latitude;
    private TextView longitude;
    private MapView mapView;
    private Button saveActual;
    private ProgressBar progressActual;
    private TextView resultActual;
    private Button saveMarker;
    private ProgressBar progressMarker;
    private TextView resultMarker;
    private Button reset;

    private GoogleMap map;

    private Marker newLocation;

    private final static int RESULT_INIT = 0;
    private final static int RESULT_SAVED = 1;
    private final static int RESULT_FAILED = 2;
    private final static int RESULT_UNDETERMINED = 3;

    private boolean centeredMyLocation = false;
    private boolean hasbeenclicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText) findViewById(R.id.badulake_name);
        twentyfour = (Switch) findViewById(R.id.switch_24h);
        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        mapView = (MapView) findViewById(R.id.map);
        saveActual = (Button) findViewById(R.id.save_actual);
        progressActual = (ProgressBar) findViewById(R.id.progress_actual);
        resultActual = (TextView) findViewById(R.id.result_actual);
        saveMarker = (Button) findViewById(R.id.save_marker);
        progressMarker = (ProgressBar) findViewById(R.id.progress_marker);
        resultMarker = (TextView) findViewById(R.id.result_marker);
        reset = (Button) findViewById(R.id.reset);

        latitude.setText(getString(R.string.latitude) + ":");
        longitude.setText(getString(R.string.longitude) + ":");

        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(final Location location) {
                if (!centeredMyLocation) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17);
                    map.animateCamera(cameraUpdate);
                    centeredMyLocation = true;

                    newLocation = map.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .draggable(true)
                            .title("Badulake being created"));
                }

                latitude.setText(getString(R.string.latitude) + ": " + location.getLatitude() + " / " + newLocation.getPosition().latitude);
                longitude.setText(getString(R.string.longitude) + ": " + location.getLongitude() + " / " + newLocation.getPosition().longitude);
            }
        });
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if (marker != null && marker == newLocation) {
                    latitude.setText(getString(R.string.latitude) + ": " + map.getMyLocation().getLatitude() + " / " + newLocation.getPosition().latitude);
                    longitude.setText(getString(R.string.longitude) + ": " + map.getMyLocation().getLongitude() + " / " + newLocation.getPosition().longitude);
                }
            }
        });

        MapsInitializer.initialize(this);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(41.4, 2.17), 14);
        map.animateCamera(cameraUpdate);

        saveActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasbeenclicked){
                    Location location = map.getMyLocation();

                    String[] params = new String[4];
                    params[0] = name.getText().toString();
                    params[1] = String.valueOf(location.getLatitude());
                    params[2] = String.valueOf(location.getLongitude());
                    if (twentyfour.isChecked()) {
                        params[3] = "True";
                    } else {
                        params[3] = "False";
                    }

                    new CreateBadulakeRequest(true).execute(params);

                    hasbeenclicked = true;
                }
            }
        });

        saveMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newLocation != null && !hasbeenclicked){
                    String[] params = new String[4];
                    params[0] = name.getText().toString();
                    params[1] = String.valueOf(newLocation.getPosition().latitude);
                    params[2] = String.valueOf(newLocation.getPosition().longitude);
                    if (twentyfour.isChecked()) {
                        params[3] = "True";
                    } else {
                        params[3] = "False";
                    }

                    new CreateBadulakeRequest(false).execute(params);

                    hasbeenclicked = true;
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setText("");
                changeResult(RESULT_INIT, true);
                changeResult(RESULT_INIT, false);
                twentyfour.setChecked(false);

                hasbeenclicked = false;

                newLocation.setPosition(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()), 17);
                map.animateCamera(cameraUpdate);
            }
        });

        new GetBadulakesRequest().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    private void changeResult(int action, boolean marker){
        if(marker){
            switch (action){
                case RESULT_INIT:
                    saveMarker.setVisibility(View.VISIBLE);
                    progressMarker.setVisibility(View.GONE);
                    resultMarker.setVisibility(View.GONE);
                    break;
                case RESULT_FAILED:
                    saveMarker.setVisibility(View.GONE);
                    progressMarker.setVisibility(View.GONE);
                    resultMarker.setVisibility(View.VISIBLE);

                    resultMarker.setText(getString(R.string.failed));
                    resultMarker.setTextColor(getResources().getColor(R.color.failed));
                    break;
                case RESULT_SAVED:
                    saveMarker.setVisibility(View.GONE);
                    progressMarker.setVisibility(View.GONE);
                    resultMarker.setVisibility(View.VISIBLE);

                    resultMarker.setText(getString(R.string.saved));
                    resultMarker.setTextColor(getResources().getColor(R.color.saved));
                    break;
                case RESULT_UNDETERMINED:
                    saveMarker.setVisibility(View.GONE);
                    progressMarker.setVisibility(View.VISIBLE);
                    resultMarker.setVisibility(View.GONE);
                    break;
            }

            if(action != RESULT_INIT){
                saveActual.setVisibility(View.GONE);
                progressActual.setVisibility(View.GONE);
                resultActual.setVisibility(View.GONE);
            }
        }else {
            switch (action){
                case RESULT_INIT:
                    saveActual.setVisibility(View.VISIBLE);
                    progressActual.setVisibility(View.GONE);
                    resultActual.setVisibility(View.GONE);
                    break;
                case RESULT_FAILED:
                    saveActual.setVisibility(View.GONE);
                    progressActual.setVisibility(View.GONE);
                    resultActual.setVisibility(View.VISIBLE);

                    resultActual.setText(getString(R.string.failed));
                    resultActual.setTextColor(getResources().getColor(R.color.failed));
                    break;
                case RESULT_SAVED:
                    saveActual.setVisibility(View.GONE);
                    progressActual.setVisibility(View.GONE);
                    resultActual.setVisibility(View.VISIBLE);

                    resultActual.setText(getString(R.string.saved));
                    resultActual.setTextColor(getResources().getColor(R.color.saved));
                    break;
                case RESULT_UNDETERMINED:
                    saveActual.setVisibility(View.GONE);
                    progressActual.setVisibility(View.VISIBLE);
                    resultActual.setVisibility(View.GONE);
                    break;
            }

            if(action != RESULT_INIT){
                saveMarker.setVisibility(View.GONE);
                progressMarker.setVisibility(View.GONE);
                resultMarker.setVisibility(View.GONE);
            }
        }
    }

    private class CreateBadulakeRequest extends AsyncTask<String, Void, Integer> {

        boolean actual = false;

        public CreateBadulakeRequest(boolean actual) {
            this.actual = actual;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            changeResult(RESULT_UNDETERMINED, actual);
            Log.d("I/O", "Request started");
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int status = -1;
            try{
                URL url = new URL("http://badulakemap.herokuapp.com/badulake");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "");
                connection.setRequestMethod("POST");
                connection.setDoInput(false);
                connection.setDoOutput(true);

                HashMap<String, String> params = new HashMap<>();
                params.put("name", strings[0]);
                params.put("latitude", strings[1]);
                params.put("longitude", strings[2]);
                params.put("alwaysopened", strings[3]);
                Log.d("I/O", params.toString());

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                outputStream.close();

                connection.connect();

                status = connection.getResponseCode();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return status;
        }

        @Override
        protected void onPostExecute(Integer status) {
            super.onPostExecute(status);
            Log.d("I/O", "Request finished with status "+ status);
            switch (status){
                case 201:
                    changeResult(RESULT_SAVED, actual);
                    map.clear();
                    if(map.getMyLocation() != null){
                        newLocation = map.addMarker(new MarkerOptions()
                                .position(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                .draggable(true)
                                .title("Badulake being created"));
                    }
                    new GetBadulakesRequest().execute();
                    break;
                default:
                    changeResult(RESULT_FAILED, actual);
                    break;
            }
        }
    }

    private class GetBadulakesRequest extends AsyncTask<Void, Void, BadulakeResponse> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("I/O", "Request started");
        }

        @Override
        protected BadulakeResponse doInBackground(Void... strings) {
            BadulakeResponse response = new BadulakeResponse();
            try{
                URL url = new URL("http://badulakemap.herokuapp.com/badulake");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "");
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                connection.connect();

                String plain = readHttpInputStreamToString(connection);

                Log.d("I/O", plain);

                response.setStatus(connection.getResponseCode());
                response.setResponse(plain);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(BadulakeResponse response) {
            super.onPostExecute(response);
            Log.d("I/O", "Request finished with status " + response.getStatus());
            switch (response.getStatus()){
                case 200:
                    addBadulakesToMap(convertBadulakesFromPlainToList(response.getResponse()));
                    break;
                default:
                    break;
            }
        }
    }

    private void addBadulakesToMap(List<Badulake> badulakes){
        for(Badulake badulake : badulakes){
            if(badulake.isAlwaysopened()){
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(badulake.getLatitude(), badulake.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .title(badulake.getName()));
            }else{
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(badulake.getLatitude(), badulake.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                        .title(badulake.getName()));
            }
        }
    }

    private List<Badulake> convertBadulakesFromPlainToList(String plain){
        List<Badulake> badulakes = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(plain);
            for(int i = 0; i < array.length(); i++){
                Badulake badulake = new Badulake();
                JSONObject jsonObject = array.getJSONObject(i);
                badulake.setAlwaysopened(jsonObject.getBoolean("alwaysopened"));
                badulake.setName(jsonObject.getString("name"));
                badulake.setLatitude(jsonObject.getDouble("latitude"));
                badulake.setLongitude(jsonObject.getDouble("longitude"));
                badulake.setId(jsonObject.getInt("id"));
                badulakes.add(badulake);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return badulakes;
    }

    private String getQuery(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String)entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private String readHttpInputStreamToString(HttpURLConnection connection) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            is = new BufferedInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        }
        catch (Exception e) {
            Log.i("I/O", "Error reading InputStream");
            result = null;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    Log.i("I/O", "Error closing InputStream");
                }
            }
        }

        return result;
    }
}