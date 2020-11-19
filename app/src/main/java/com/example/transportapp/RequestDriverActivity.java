package com.example.transportapp;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.transportapp.Model.DriverGeoModel;
import com.example.transportapp.Model.EventBus.DeclineRequestAndRemoveTripFromDriver;
import com.example.transportapp.Model.EventBus.DeclineRequestFromDriver;
import com.example.transportapp.Model.EventBus.DriverAcceptTripEvent;
import com.example.transportapp.Model.EventBus.DriverCompleteTripEvent;
import com.example.transportapp.Model.EventBus.SelectePlaceEvent;
import com.example.transportapp.Model.TripPlanModel;
import com.example.transportapp.Remote.IGoogleAPI;
import com.example.transportapp.Remote.RetrofitClient;
import com.example.transportapp.Utils.UserUtilsRider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RequestDriverActivity extends FragmentActivity implements OnMapReadyCallback {

    TextView txt_origin;

    //Slowly camera spinning
    private ValueAnimator animator;
    private static final int DESIRED_NUM_OF_SPINS = 5;
    private static final int DESIRED_SECONDS_PER_ONE_FULL_360_SPIN=40;

    //Effect
    private Circle lastUserCircle;
    private long duration = 1000;
    private ValueAnimator lastPulseAnimator;



    //View
    @BindView(R.id.main_layout)
    RelativeLayout main_layout;
    @BindView(R.id.finding_your_ride_layout)
    CardView finding_your_ride_layout;
    @BindView(R.id.confirm_uber_layout)
    CardView confirm_uber_layout;
    @BindView(R.id.btn_confirm_uber)
    Button btn_confirm_uber;
    @BindView(R.id.confirm_pickup_layout)
    CardView confirm_pickup_layout;
    @BindView(R.id.btn_confirm_pickup)
    Button btn_confirm_pickup;
    @BindView(R.id.txt_address_pickup)
    TextView txt_address_pickup;
    @BindView(R.id.driver_info_layout)
    CardView driver_info_layout;
    @BindView(R.id.txt_driver_name)
    TextView txt_driver_name;
    @BindView(R.id.img_driver)
    ImageView img_driver;

    @BindView(R.id.fill_maps)
    View fill_maps;
    private DriverGeoModel lastDriverCall;
    private String driverOldPosition="";
    private Handler handler;
    private float v;
    private double lat,lng;
    private int index,next;
    private LatLng start,end;


    @OnClick(R.id.btn_confirm_uber)
    void onConfirmUber(){
        confirm_pickup_layout.setVisibility(View.VISIBLE); //Show pickup layout
        confirm_uber_layout.setVisibility(View.GONE); // Hidden Uber layout

        setDataPickup();
    }

    @OnClick(R.id.btn_confirm_pickup)
    void onConfirmPickup(){
        if(mMap == null) return;
        if(selectePlaceEvent == null) return;

        //Clear map
        mMap.clear();
        //Tilt
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(selectePlaceEvent.getOrigin())
                .tilt(45f)
                .zoom(16f)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //Start animation
        addMarkerWithPulseAnimation();
    }

    private void addMarkerWithPulseAnimation() {
        confirm_pickup_layout.setVisibility(View.GONE);
        fill_maps.setVisibility(View.VISIBLE);
        finding_your_ride_layout.setVisibility(View.VISIBLE);

        originMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker())
                .position(selectePlaceEvent.getOrigin()));

        addPulsatingEffect(selectePlaceEvent);


    }

    private void addPulsatingEffect(SelectePlaceEvent selectePlaceEvent) {
        if(lastPulseAnimator != null) lastPulseAnimator.cancel();
        if(lastUserCircle != null) lastUserCircle.setCenter(selectePlaceEvent.getOrigin());

        lastPulseAnimator = CommonRider.valueAnimate(duration, animation -> {
            if(lastUserCircle != null) lastUserCircle.setRadius((Float)animation.getAnimatedValue());
            else{
                lastUserCircle = mMap.addCircle(new CircleOptions()
                        .center(selectePlaceEvent.getOrigin())
                        .radius((Float)animation.getAnimatedValue())
                        .strokeColor(Color.WHITE)
                        .fillColor(Color.parseColor("#33333333"))
                );
            }
        });

        startMapCameraSpinningAnimation(selectePlaceEvent);

    }

    private void startMapCameraSpinningAnimation(SelectePlaceEvent selectePlaceEvent) {
        if(animator != null) animator.cancel();
        animator = ValueAnimator.ofFloat(0,DESIRED_NUM_OF_SPINS*360);
        animator.setDuration(DESIRED_SECONDS_PER_ONE_FULL_360_SPIN*DESIRED_NUM_OF_SPINS*1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setStartDelay(100);
        animator.addUpdateListener(valueAnimator -> {
            Float newBearingValue = (Float) valueAnimator.getAnimatedValue();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(selectePlaceEvent.getOrigin())
                    .zoom(16f)
                    .tilt(45f)
                    .bearing(newBearingValue)
                    .build()));

        });
        animator.start();

        //After start animation, find driver
        findNearbyDriver(selectePlaceEvent);

    }

    private void findNearbyDriver(SelectePlaceEvent selectePlaceEvent) {
        if(CommonRider.driversFound.size() > 0)
        {
            float min_distance = 0; // default min distance = 0;
            DriverGeoModel foundDriver = null;
            Location currentRiderLocation = new Location("");
            currentRiderLocation.setLatitude(selectePlaceEvent.getOrigin().latitude);
            currentRiderLocation.setLongitude(selectePlaceEvent.getOrigin().longitude);
            for(String key: CommonRider.driversFound.keySet())
            {
                Location driverLocation = new Location("");
                driverLocation.setLatitude(CommonRider.driversFound.get(key).getGeoLocation().latitude);
                driverLocation.setLongitude(CommonRider.driversFound.get(key).getGeoLocation().longitude);

                //Compare 2 Location
                if(min_distance == 0)
                {
                    min_distance = driverLocation.distanceTo(currentRiderLocation); // First default min_distance

                    if(!CommonRider.driversFound.get(key).isDecline()) // If not decline before
                    {
                        foundDriver = CommonRider.driversFound.get(key);
                        break; //Exit loop because we found driver
                    }
                    else
                        continue; // If already decline before, just skip and continue
                }
                else if(driverLocation.distanceTo(currentRiderLocation) < min_distance)
                {
                    // If have any driver smaller min_distance, just get it!
                    min_distance = driverLocation.distanceTo(currentRiderLocation); // First default min_distance
                    if(!CommonRider.driversFound.get(key).isDecline()) // If not decline before
                    {
                        foundDriver = CommonRider.driversFound.get(key);
                        break; //Exit loop because we found driver
                    }
                    else
                        continue; // If already decline before, just skip and continue
                }
//


            }
            //After loop
            if(foundDriver != null)
            {
                UserUtilsRider.sendRequestToDriver(this,main_layout,foundDriver,selectePlaceEvent);
                lastDriverCall = foundDriver;
            }
            else
            {
                Toast.makeText(this, getString(R.string.no_driver_accept_request), Toast.LENGTH_SHORT).show();
                lastDriverCall=null;
                finish();
            }
        }
        else
        {
            //Not found
            Snackbar.make(main_layout,getString(R.string.drivers_not_found),Snackbar.LENGTH_LONG).show();
            lastDriverCall=null;
            finish();

        }
    }

    @Override
    protected void onDestroy() {
        if(animator != null) animator.end();
        super.onDestroy();
    }

    private GoogleMap mMap;

    private SelectePlaceEvent selectePlaceEvent;

    //Routes
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IGoogleAPI iGoogleAPI;
    private Polyline blackPolyline,greyPolyline;
    private PolylineOptions polylineOptions,blackPolylineOptions;
    private List<LatLng> polylineList;

    private Marker originMarker,destinationMarker;

    private void setDataPickup() {
        txt_address_pickup.setText(txt_origin != null ? txt_origin.getText() : "None");
        mMap.clear(); //Clear all on map

        addPickupMarker();
    }

    private void addPickupMarker() {
        View view = getLayoutInflater().inflate(R.layout.pickup_info_windows, null);

        //Create Icon for Marker
        IconGenerator generator = new IconGenerator(this);
        generator.setContentView(view);
        generator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        Bitmap icon = generator.makeIcon();

        originMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .position(selectePlaceEvent.getOrigin()));

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
        if(EventBus.getDefault().hasSubscriberForEvent(SelectePlaceEvent.class))
            EventBus.getDefault().removeStickyEvent(SelectePlaceEvent.class);
        if(EventBus.getDefault().hasSubscriberForEvent(DeclineRequestFromDriver.class))
            EventBus.getDefault().removeStickyEvent(DeclineRequestFromDriver.class);
        if(EventBus.getDefault().hasSubscriberForEvent(DriverAcceptTripEvent.class))
            EventBus.getDefault().removeStickyEvent(DriverAcceptTripEvent.class);
        if(EventBus.getDefault().hasSubscriberForEvent(DeclineRequestAndRemoveTripFromDriver.class))
            EventBus.getDefault().removeStickyEvent(DeclineRequestAndRemoveTripFromDriver.class);
        if(EventBus.getDefault().hasSubscriberForEvent(DriverCompleteTripEvent.class))
            EventBus.getDefault().removeStickyEvent(DriverCompleteTripEvent.class);
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onDriverCompleteTrip(DriverCompleteTripEvent event)
    {

        CommonRider.showNotification(this, new Random().nextInt(),
                "Complete trip",
                "Yout trip"+event.getTripkey()+" has been complete",
                null);
        finish();

    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onDriverAcceptEvent(DriverAcceptTripEvent event)
    {
        //Get trip information
        FirebaseDatabase.getInstance().getReference(CommonRider.TRIP)
                .child(event.getTripIp())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            TripPlanModel tripPlanModel = dataSnapshot.getValue(TripPlanModel.class);
                            mMap.clear();
                            fill_maps.setVisibility(View.GONE);
                            if(animator != null) animator.end();
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(mMap.getCameraPosition().target)
                                    .tilt(0f)
                                    .zoom(mMap.getCameraPosition().zoom)
                                    .build();
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            //Get routers
                            String driverLocation = new StringBuilder()
                                    .append(tripPlanModel.getCurrentLat())
                                    .append(",")
                                    .append(tripPlanModel.getCurrentLng())
                                    .toString();

                            compositeDisposable.add(iGoogleAPI.getDirections("driving",
                                    "less_driving",
                                    tripPlanModel.getOrigin(),driverLocation,
                                    getString(R.string.google_api_key))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(returnResult -> {

                                        PolylineOptions blackPolylineOptions=null;
                                        List<LatLng> polylineList=null;
                                        Polyline blackPolyline=null;

                                        try{
                                            //Parse JSON
                                            JSONObject jsonObject = new JSONObject(returnResult);
                                            JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                            for(int i=0;i< jsonArray.length();i++)
                                            {
                                                JSONObject route = jsonArray.getJSONObject(i);
                                                JSONObject poly = route.getJSONObject("overview_polyline");
                                                String polyline = poly.getString("points");
                                                polylineList = CommonRider.decodePoly(polyline);
                                            }



                                            blackPolylineOptions = new PolylineOptions();
                                            blackPolylineOptions.color(Color.BLACK);
                                            blackPolylineOptions.width(5);
                                            blackPolylineOptions.startCap(new SquareCap());
                                            blackPolylineOptions.jointType(JointType.ROUND);
                                            blackPolylineOptions.addAll(polylineList);
                                            blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                            JSONObject object = jsonArray.getJSONObject(0);
                                            JSONArray legs = object.getJSONArray("legs");
                                            JSONObject legObjects = legs.getJSONObject(0);

                                            JSONObject time = legObjects.getJSONObject("duration");
                                            String duration = time.getString("text");

                                            JSONObject distanceEstimate = legObjects.getJSONObject("distance");
                                            String distance = distanceEstimate.getString("text");

                                            LatLng origin = new LatLng(
                                                    Double.parseDouble(tripPlanModel.getOrigin().split(",")[0]),
                                                    Double.parseDouble(tripPlanModel.getOrigin().split(",")[1]));
                                            LatLng destination = new LatLng(tripPlanModel.getCurrentLat(),tripPlanModel.getCurrentLng());

                                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                                    .include(origin)
                                                    .include(destination)
                                                    .build();

                                            addPickupMarkerWithDuration(duration,origin);
                                            addDriverMarker(destination);

                                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,160));
                                            mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom-1));

                                            initDriverForMoving(event.getTripIp(),tripPlanModel);

                                            //Load driver avatar
                                            Glide.with(RequestDriverActivity.this)
                                                    .load(tripPlanModel.getDriverInfoModel().getAvatar())
                                                    .into(img_driver);
                                            txt_driver_name.setText(tripPlanModel.getDriverInfoModel().getFirstName());

                                            confirm_pickup_layout.setVisibility(View.GONE);
                                            confirm_uber_layout.setVisibility(View.GONE);
                                            driver_info_layout.setVisibility(View.VISIBLE);


                                        }catch (Exception e)
                                        {
                                            //Snackbar.make(getView(),e.getMessage(),Snackbar.LENGTH_LONG).show();
                                            Toast.makeText(RequestDriverActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }



                                    })
                            );


                        }
                        else
                            Snackbar.make(main_layout,getString(R.string.trip_not_found)+event.getTripIp(),Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Snackbar.make(main_layout,databaseError.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void initDriverForMoving(String tripIp, TripPlanModel tripPlanModel) {
        driverOldPosition = new StringBuilder()
                .append(tripPlanModel.getCurrentLat())
                .append(",")
                .append(tripPlanModel.getCurrentLng())
                .toString();

        FirebaseDatabase.getInstance()
                .getReference(CommonRider.TRIP)
                .child(tripIp)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) { // Check exits
                            TripPlanModel newData = dataSnapshot.getValue(TripPlanModel.class); //If it null will make crash

                            String driverNewLocation = new StringBuilder()
                                    .append(newData.getCurrentLat())
                                    .append(",")
                                    .append(newData.getCurrentLng())
                                    .toString();

                            if (!driverOldPosition.equals(driverNewLocation)) // If not equal
                                moveMarkerAnimation(destinationMarker, driverOldPosition, driverNewLocation);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Snackbar.make(main_layout,databaseError.getMessage(),Snackbar.LENGTH_SHORT).show();

                    }
                });
    }

    private void moveMarkerAnimation(Marker marker, String from, String to) {
        compositeDisposable.add(iGoogleAPI.getDirections("driving",
                "less_driving",
                from,to,
                getString(R.string.google_api_key)) //Fix crash context
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(returnResult -> {
                    Log.d("API_RETURN",returnResult);

                    try{
                        //Parse JSON
                        JSONObject jsonObject = new JSONObject(returnResult);
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for(int i=0;i< jsonArray.length();i++)
                        {
                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyline = poly.getString("points");
                            polylineList = CommonRider.decodePoly(polyline);

                        }

                        blackPolylineOptions = new PolylineOptions();
                        blackPolylineOptions.color(Color.BLACK);
                        blackPolylineOptions.width(5);
                        blackPolylineOptions.startCap(new SquareCap());
                        blackPolylineOptions.jointType(JointType.ROUND);
                        blackPolylineOptions.addAll(polylineList);
                        blackPolyline = mMap.addPolyline(blackPolylineOptions);

                        JSONObject object = jsonArray.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");
                        JSONObject legObjects = legs.getJSONObject(0);

                        JSONObject time = legObjects.getJSONObject("duration");
                        String duration = time.getString("text");

                        JSONObject distanceEstimate = legObjects.getJSONObject("distance");
                        String distance = distanceEstimate.getString("text");

                        Bitmap bitmap = CommonRider.createIconWithDuration(RequestDriverActivity.this,duration);
                        originMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));

                        //Moving
                        handler = new Handler();
                        index = -1;
                        next = 1;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if(index < polylineList.size() - 2 )
                                {
                                    index++;
                                    next = index+1;
                                    start = polylineList.get(index);
                                    end = polylineList.get(next);

                                }
                                ValueAnimator valueAnimator = ValueAnimator.ofInt(0,1);
                                valueAnimator.setDuration(1500);
                                valueAnimator.setInterpolator(new LinearInterpolator());
                                valueAnimator.addUpdateListener(valueAnimator1 -> {
                                    v = valueAnimator1.getAnimatedFraction();
                                    lng = v*end.longitude+(1-v)*start.longitude;
                                    lat = v*end.latitude+(1-v)*start.latitude;
                                    LatLng newPos = new LatLng(lat,lng);
                                    marker.setPosition(newPos);
                                    marker.setAnchor(0.5f,0.5f);
                                    marker.setRotation(CommonRider.getBearing(start,newPos));

                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(newPos));
                                });

                                valueAnimator.start();
                                if(index < polylineList.size() -2 )
                                    handler.postDelayed(this,1500);
                                else if (index < polylineList.size() -1)
                                {

                                }
                            }
                        },1500);

                        driverOldPosition = to; //set new driver position

                    }catch (Exception e)
                    {
                        Snackbar.make(main_layout,e.getMessage(),Snackbar.LENGTH_LONG).show();

                    }

                },throwable -> {
                    if(throwable != null)
                        Snackbar.make(main_layout,throwable.getMessage(),Snackbar.LENGTH_SHORT).show();
                })
        );
    }

    private void addDriverMarker(LatLng destination) {
        destinationMarker = mMap.addMarker(new MarkerOptions().position(destination).flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

    }

    private void addPickupMarkerWithDuration(String duration, LatLng origin) {
        Bitmap icon = CommonRider.createIconWithDuration(this,duration);
        originMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(origin));

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onSelectePlaceEvent(SelectePlaceEvent event)
    {
        selectePlaceEvent = event;
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onDeclineRequestEvent(DeclineRequestFromDriver event)
    {
        if(lastDriverCall != null)
        {
            CommonRider.driversFound.get(lastDriverCall.getKey()).setDecline(true);
            //Driver has been decline request, just find new driver
            findNearbyDriver(selectePlaceEvent);
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onDeclineRequestAndRemoveTripEvent(DeclineRequestAndRemoveTripFromDriver event)
    {
        if(lastDriverCall != null)
        {
            CommonRider.driversFound.get(lastDriverCall.getKey()).setDecline(true);
            //Driver has been decline request, just finish this activity
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_driver);

        init();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void init() {
        ButterKnife.bind(this);
        iGoogleAPI = RetrofitClient.getInstance().create(IGoogleAPI.class);
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

        drawPath(selectePlaceEvent);

        try{
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,
                    R.raw.transport_maps_style));
            if(!success)
                Toast.makeText(this, "Load map style failed", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void drawPath(SelectePlaceEvent selectePlaceEvent) {
        //Request API
        compositeDisposable.add(iGoogleAPI.getDirections("driving",
                "less_driving",
                selectePlaceEvent.getOriginString(),selectePlaceEvent.getDestinatinString(),
                getString(R.string.google_api_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(returnResult -> {
                    Log.d("API_RETURN",returnResult);

                    try{
                        //Parse JSON
                        JSONObject jsonObject = new JSONObject(returnResult);
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for(int i=0;i< jsonArray.length();i++)
                        {
                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyline = poly.getString("points");
                            polylineList = CommonRider.decodePoly(polyline);
                        }

                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.GRAY);
                        polylineOptions.width(12);
                        polylineOptions.startCap(new SquareCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(polylineList);
                        greyPolyline = mMap.addPolyline(polylineOptions);

                        blackPolylineOptions = new PolylineOptions();
                        blackPolylineOptions.color(Color.BLACK);
                        blackPolylineOptions.width(5);
                        blackPolylineOptions.startCap(new SquareCap());
                        blackPolylineOptions.jointType(JointType.ROUND);
                        blackPolylineOptions.addAll(polylineList);
                        blackPolyline = mMap.addPolyline(blackPolylineOptions);

                        //Animator
                        ValueAnimator valueAnimator = ValueAnimator.ofInt(0,100);
                        valueAnimator.setDuration(1100);
                        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                        valueAnimator.setInterpolator(new LinearInterpolator());
                        valueAnimator.addUpdateListener(value -> {
                            List<LatLng> points = greyPolyline.getPoints();
                            int percentValue = (int)value.getAnimatedValue();
                            int size = points.size();
                            int newPoints = (int)(size*(percentValue/100.0f));
                            List<LatLng> p = points.subList(0,newPoints);
                            blackPolyline.setPoints(p);
                        });

                        valueAnimator.start();

                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                .include(selectePlaceEvent.getOrigin())
                                .include(selectePlaceEvent.getDestination())
                                .build();

                        //Add car icon for origin
                        JSONObject object = jsonArray.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");
                        JSONObject legObjects = legs.getJSONObject(0);

                        JSONObject time = legObjects.getJSONObject("duration");
                        String duration = time.getString("text");

                        String start_address = legObjects.getString("start_address");
                        String end_address = legObjects.getString("end_address");

                        addOriginMarker(duration,start_address);

                        addDestinationMarker(end_address);

                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,160));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom-1));
                    }catch (Exception e)
                    {
                        //Snackbar.make(getView(),e.getMessage(),Snackbar.LENGTH_LONG).show();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }



                })
        );
    }

    private void addDestinationMarker(String end_address) {
        View view = getLayoutInflater().inflate(R.layout.destination_info_windows,null);
        TextView txt_destination = (TextView)view.findViewById(R.id.txt_destination);

        txt_destination.setText(CommonRider.formatAddress(end_address));

        IconGenerator generator = new IconGenerator(this);
        generator.setContentView(view);
        generator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        Bitmap icon = generator.makeIcon();

        destinationMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .position(selectePlaceEvent.getDestination()));
    }

    private void addOriginMarker(String duration, String start_address) {
        View view = getLayoutInflater().inflate(R.layout.origin_info_windows,null);

        TextView txt_time = (TextView)view.findViewById(R.id.txt_time);
        txt_origin = (TextView)view.findViewById(R.id.txt_origin);

        txt_time.setText(CommonRider.formatDuration(duration));
        txt_origin.setText(CommonRider.formatAddress(start_address));

        //Create Icon for Marker
        IconGenerator generator = new IconGenerator(this);
        generator.setContentView(view);
        generator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        Bitmap icon = generator.makeIcon();

        originMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .position(selectePlaceEvent.getOrigin()));


    }
}