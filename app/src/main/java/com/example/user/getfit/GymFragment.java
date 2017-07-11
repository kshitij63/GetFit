package com.example.user.getfit;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user on 7/6/2017.
 */

public class GymFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private ProgressDialog progressDialog;
    private MapView mapFragment;
    private FloatingActionButton actionButton;
    private ArrayList<Gym> gymArrayList;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Double current_latitude, current_longitude;
    private GoogleMap mgoogleMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.gymfragment, container, false);
        setupGoogleApiClient();
        actionButton = (FloatingActionButton) view.findViewById(R.id.fab_Search);
        recyclerView = (RecyclerView) view.findViewById(R.id.gymRecycle);
        mapFragment = (MapView) view.findViewById(R.id.mapfragment);
        mapFragment.onCreate(savedInstanceState);
        mapFragment.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mgoogleMap = googleMap;
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                    }
                });
                //if(!progressDialog.isShowing()){
                //  LatLng latLng=new LatLng(current_latitude,current_longitude);
                //googleMap.addMarker(new MarkerOptions().position(latLng).title("you"));
                // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
                //}


            }
        });
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                        getActivity().getFragmentManager().findFragmentById(R.id.autocomplete);
                android.app.FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.show(autocompleteFragment);
                transaction.commit();
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        mgoogleMap.clear();
                        get_nearby_gyms(place.getLatLng(), mgoogleMap);
                        mgoogleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));

                        mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));

                    }

                    @Override
                    public void onError(Status status) {

                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {



    }

    @Override
    public void onLocationChanged(Location location) {
        current_latitude = location.getLatitude();
        current_longitude = location.getLongitude();
        LatLng latLng = new LatLng(current_latitude, current_longitude);
        mgoogleMap.addMarker(new MarkerOptions().position(latLng).title("you"));
        mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
        get_nearby_gyms(latLng, mgoogleMap);
    }

    private void setupGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    private void get_nearby_gyms(LatLng latLng, final GoogleMap map) {
        gymArrayList = new ArrayList<>();
        //final String photo_ref = null;
        Double latitude = latLng.latitude;
        Double longitude = latLng.longitude;
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=10000&type=gym&key=" + getActivity().getResources().getString(R.string.map_api_key);
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String photo_ref = null;
                String vicitnity = null;
                String rate = null;
                Boolean status = null;
                try {
                    Log.e("response", response.toString());
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject gym = results.getJSONObject(i);
                        if (gym.has("opening_hour")) {
                            JSONObject opening = gym.getJSONObject("opening_hour");
                            status = opening.getBoolean("open_now");
                        } else {
                            status = null;
                        }

                        if (gym.has("photo")) {
                            JSONArray photo = gym.getJSONArray("photo");
                            JSONObject object = photo.getJSONObject(0);
                            photo_ref = object.getString("photo_reference");
                        } else {
                            photo_ref = getActivity().getResources().getString(R.string.NA);
                        }
                        String vicinity = gym.getString("vicinity");
                        if (gym.has("rating")) {
                            rate = String.valueOf(gym.getDouble("rating"));
                        } else {
                            rate = getActivity().getResources().getString(R.string.NA);
                        }

                        JSONObject geometry = gym.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        Double gym_latitude = location.getDouble("lat");
                        Double gym_longitude = location.getDouble("lng");
                        Gym gym1 = new Gym(photo_ref, rate, gym.getString("name"), vicinity, status);
                        gymArrayList.add(gym1);
                        LatLng latLng1 = new LatLng(gym_latitude, gym_longitude);
                        map.addMarker(new MarkerOptions().title(gym.getString("name")).position(latLng1).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    }
                    recyclerView.setAdapter(new GymAdapter(getContext(), gymArrayList,1008));
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                } catch (JSONException e) {
                    Log.e("exe", e.getMessage());
                }
                queue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                queue.stop();
            }
        });
        queue.add(request);
    }


}
