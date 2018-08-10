package me.leojlindo.travelbud;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.leojlindo.travelbud.Route.MapHttpConnection;
import me.leojlindo.travelbud.Route.PathJSONParser;
import me.leojlindo.travelbud.models.PlaceInfo;

import static com.parse.Parse.getApplicationContext;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "HomeFragment";

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_CODE = 1234;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    //widgets
    private AutoCompleteTextView startLocation;
    private AutoCompleteTextView endLocation;
    private ImageView mapGps;
    private Button goBtn;
    private Button friendsBtn;
    private Button clearBtn;


    //variables
    private boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private Marker marker;
    private LatLng latlngOne;
    private LatLng latlngTwo;
    private boolean isStart = true;
    float[] results = new float[1];
    float resultOne;
    private List<LatLng> lstLatLngRoute = new ArrayList<LatLng>();
    Boolean isStartLocation = true;
    Boolean isEndLocation = false;

    //bottom sheet
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;

    //onCreateView method is called when Fragment should create its View object hierarchy
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, parent, false);
        startLocation = view.findViewById(R.id.start_location);
        endLocation = view.findViewById(R.id.end_location);
        mapGps = view.findViewById(R.id.ic_gps);
        goBtn = view.findViewById(R.id.go_btn);
        friendsBtn = view.findViewById(R.id.friends_btn);
        clearBtn = view.findViewById(R.id.clear_btn);

        LinearLayout bottomSheetViewGroup = (LinearLayout) view.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottomSheetViewGroup);

        sheetBehavior.setPeekHeight(0);

        getLocationPermission();


        //when go button is clicked it draws the route
                goBtn.setOnClickListener(new View.OnClickListener()
                {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void onClick(View v)
                    {
                            if(startLocation.getText().length() < 1 || endLocation.getText().length() < 1){
                                // Display toast
                                Toast.makeText(getApplicationContext(), "Please enter your route!",Toast.LENGTH_LONG).show();
                        } else {
                            getPath();
                            zoomRoute();
                            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }


                        //when find friends button is clicked
                friendsBtn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        startActivity(new Intent(getActivity(), UserList.class));

                    }
                });

            }
        });

        //clear start and end location when clear button clicked
        clearBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;

    }

    // This is triggered soon after onCreateView()
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //initializing everything after getting permission
        init();
    }

    //checking permissions
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting getting location permission");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        boolean permissionGranted = ActivityCompat.checkSelfPermission(getContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean secondPermissionGranted = ActivityCompat.checkSelfPermission(getContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        //if passes both permissions then we can access users location else ask for it
        if (permissionGranted){
            if (secondPermissionGranted) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch(requestCode) {
            case LOCATION_PERMISSION_CODE: {
                //some location permission was granted
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i ++) {
                        //if one result doesnt have access then its denied
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    //initialize map
                    initMap();
                }
            }

        }
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void init() {
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(),this)
                .build();

        startLocation.setOnItemClickListener(autocompleteClickListener);
        endLocation.setOnItemClickListener(autocompleteClickListener);

        //passing google client to adapter to get search suggestions
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), Places.getGeoDataClient(getActivity(), null), LAT_LNG_BOUNDS,null);

        startLocation.setAdapter(placeAutocompleteAdapter);
        endLocation.setAdapter(placeAutocompleteAdapter);

        //letting user hit 'enter' to enter location
        startLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute method for searching location
                    geoLocate();
                }
                return false;
            }
        });

        //end location input
        endLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute method for searching location
                    geoLocate();
                }
                return false;
            }
        });

        //going back to 'my location' when gps icon clicked
        mapGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        hideSoftKeyboard();
    }

    private void getDeviceLocation() {
        Log.d(TAG,"getDeviceLocation: getting current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    15f, "My location");
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    //called when map is ready to be used
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        //styling the map
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.mapstyle));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");

        FragmentManager cfm = getChildFragmentManager();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            cfm.beginTransaction().replace(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

    }


    //geolocating the string the user entered into search
    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");
        String startString = startLocation.getText().toString();
        String endString = endLocation.getText().toString();
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(startString, 1);
            list = geocoder.getFromLocationName(endString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException" + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 15f, address.getAddressLine(0));
        }
    }

    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        Log.d(TAG, "moveCamera: moving the camera to: latitude: " + latLng.latitude + ", longitude: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        //first clear all markers on map
        mMap.clear();

        //adding address and phone num to show up on marker
        if (placeInfo != null) {
            try {
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);
                marker = mMap.addMarker(options);
                //setting marker icons for if its the start or end location
                if (isStartLocation) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_trip_origin_black_18dp));
                    isStartLocation = false;
                } else {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_place_black_18dp));
                }

            } catch (NullPointerException e) {
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }
        hideSoftKeyboard();
    }

    //redirecting camera of the location you want to show
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: latitude: " + latLng.latitude + ", longitude: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    //hiding keyboard after user enters location
    private void hideSoftKeyboard() {
        // getting a fcous
        View view = getActivity().getCurrentFocus();
        //now closing the keyboard right away
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }


    ///////// Search auto Complete suggestions methods

    private AdapterView.OnItemClickListener autocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            //need place object which contains properties like phone and latitude and longitude
            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            //submit a request
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

        }
    };


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                //need to release or will get memory leaks
                places.release();
                return;
            }

            final Place place = places.get(0);

            try {
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                //mPlace.setAttributions(place.getAttributions().toString());
                mPlace.setId(place.getId());
                mPlace.setLatlng(place.getLatLng());

                Log.d(TAG, "onResult: place: " + mPlace.toString());
            } catch (NullPointerException e) {
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());

            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude), 15f, mPlace);

            if (isStart) {
                latlngOne = new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude);
                //isStartLocation = true;
                isStart = false;
            } else {
                latlngTwo = new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude);
                saveRoute(latlngOne, latlngTwo);
            }

            places.release();

        }
    };

    public void saveRoute(LatLng start, LatLng end) {
        ParseObject po = new ParseObject("Routes");
        po.put("username", MessageFragment.user.getUsername());
        po.put("start_location", latlngOne.toString());
        po.put("end_location", latlngTwo.toString());
        po.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });

        Location.distanceBetween(latlngOne.latitude, latlngOne.longitude,
                latlngTwo.latitude, latlngTwo.longitude,
                results);

        resultOne = results[0];


    }


    //getting direction route of the start location and the end location
    private String  getMapsApiDirectionsUrl(LatLng origin,LatLng dest) {
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }


    private class ReadTask extends AsyncTask<String, Void , String> {

        @Override
        protected String doInBackground(String... url) {
            // TODO Auto-generated method stub
            String data = "";
            try {
                MapHttpConnection http = new MapHttpConnection();
                data = http.readUr(url[0]);


            } catch (Exception e) {
                // TODO: handle exception
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }

    }

    private class ParserTask extends AsyncTask<String,Integer, List<List<HashMap<String , String >>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
            // TODO Auto-generated method stub
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = new ArrayList<LatLng>();
            PolylineOptions polyLineOptions = new PolylineOptions();

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(12);
                polyLineOptions.color(getResources().getColor(R.color.appGreen));
            }

            mMap.addPolyline(polyLineOptions);

        }
        }


    public void getPath() {
            String url = getMapsApiDirectionsUrl(latlngOne, latlngTwo);
            ReadTask downloadTask = new ReadTask();
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

    }

    //zooming out to show full route
    public void zoomRoute() {

        lstLatLngRoute.add(latlngOne);
        lstLatLngRoute.add(latlngTwo);

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 450;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }


    //wont crash when going to home fragment a second time
    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }


}









