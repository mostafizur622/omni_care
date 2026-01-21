package com.srapp;

import static com.srapp.Db_Actions.Tables.MARKET_ID;
import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.FaceDetection.LivenessOverlayActivity.EXTRA_BASE64;
import static com.srapp.LoginImageCapture.LocationUtils.isInsideGeofence;
import static com.srapp.LoginImageCapture.LocationUtils.isWithinRadius;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Db_Actions.DBListener;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.LoginImageCapture.CameraHelper;
import com.srapp.LoginImageCapture.LocationHelper;
import com.srapp.Model.CenterModel;
import com.srapp.Model.CoordinateModel;
import com.srapp.Util.JAPIClient;
import com.srapp.apiService.ApiInterfaceForJava;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitActivity extends AppCompatActivity implements DBListener, BasicFunctionListener {
    Spinner route;
    ApiInterfaceForJava api;
    ArrayList<String> ThanaName=new ArrayList<String>();
    private LocationHelper locationHelper;
    private volatile Location latestFix;
    ImageView outletImage;
    private CameraHelper cameraHelper;
    String imageBase64 = "";
    Button submitBtn;
    EditText remarks;
    String centerID;
    String loginLatStr, loginLngStr, radiusMeter;
    Data_Source ds;
    String mapType;
    ImageView homeBtn,backBtn;
    private CenterModel selectedCenter = null;
    private ArrayList<CenterModel> centerList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);
        route= findViewById(R.id.leave_type);
        route= findViewById(R.id.leave_type);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( VisitActivity.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( VisitActivity.this, Dashboard.class));
                finishAffinity();
            }
        });
        api = JAPIClient.getClient().create(ApiInterfaceForJava.class);
        routeSpinnerSetup();
        outletImage=findViewById(R.id.outletImage);
        submitBtn= findViewById(R.id.newApply);
        remarks= findViewById(R.id.remarks);
        ds = new Data_Source(this, this, this);
        locationHelper = new LocationHelper(this);
        if (!locationHelper.hasFinePermission()) {
            locationHelper.requestFinePermission();
        } else {
            fetchLocationOrAskSettings();
        }

        route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                CenterModel selected = centerList.get(arg2);
                selectedCenter = centerList.get(arg2);  // ✅ store globally
                centerID = selected.id;
                mapType = selected.mapType;
                radiusMeter = selected.radiusMeter;

                // ✅ If you need first coordinate (single point case)
                if (selected.coordinates != null && !selected.coordinates.isEmpty()) {
                    CoordinateModel first = selected.coordinates.get(0);
                    loginLatStr = String.valueOf(first.lat);
                    loginLngStr = String.valueOf(first.lng);
                } else {
                    loginLatStr = "";
                    loginLngStr = "";
                }

                // ✅ Log everything
                Log.e("SelectedCenter", "ID=" + selected.id
                        + " name=" + selected.name
                        + " mapType=" + selected.mapType
                        + " radius=" + selected.radiusMeter
                        + " coordCount=" + (selected.coordinates == null ? 0 : selected.coordinates.size()));

                // ✅ Log full coordinate array
                if (selected.coordinates != null) {
                    for (int i = 0; i < selected.coordinates.size(); i++) {
                        CoordinateModel c = selected.coordinates.get(i);
                        Log.e("SelectedCoords", (i + 1) + ") lat=" + c.lat + ", lng=" + c.lng);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //TODO Auto-generated method stub

            }
        });


        outletImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraHelper = new CameraHelper(VisitActivity.this, new CameraHelper.CameraCallback() {
                    @Override
                    public void onImageCaptured(Bitmap bitmap, String base64String) {
                        imageBase64 = base64String;
                        Log.d("ImageBase64", imageBase64);
                        // ✅ Show bitmap directly (Best)
                        outletImage.setImageBitmap(bitmap);
                        Toast.makeText(VisitActivity.this, "Image Captured!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(VisitActivity.this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                    }
                });
                // Trigger the camera flow
                cameraHelper.startCameraFlow();
            }
        });
        JSONObject jsonObject = new JSONObject();
        ProgressDialog dailog = CheckConnection(VisitActivity.this,"Getting Data...");
        if (dailog==null)
            return;
        api.diagnosticsList(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(response.body());
                    dailog.dismiss();
                    JSONArray jsonArray=jsonObject.getJSONArray("res");
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject dataObject = jsonArray.getJSONObject(k);

                        String id = dataObject.getString("id");
                        String name = dataObject.getString("name");
                        String mapType = dataObject.getString("map_type");
                        String radius = dataObject.getString("radius_miter");

                        // ✅ parse coordinates array
                        JSONArray coordArray = dataObject.getJSONArray("coordinates");
                        ArrayList<CoordinateModel> coords = new ArrayList<>();

                        for (int j = 0; j < coordArray.length(); j++) {
                            JSONObject coordObj = coordArray.getJSONObject(j);

                            double lat = Double.parseDouble(coordObj.getString("lat"));
                            double lng = Double.parseDouble(coordObj.getString("lng"));

                            coords.add(new CoordinateModel(lat, lng));
                        }

                        centerList.add(new CenterModel(id, name, mapType, radius, coords));
                        ThanaName.add(name);
                    }
                    ((SpinnerAdapter) route.getAdapter()).notifyDataSetChanged();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dailog.dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("mapType", "onClick: "+mapType);
                double currentLat = latestFix.getLatitude();
                double currentLng = latestFix.getLongitude();
                Location currentLocation = new Location("");
                currentLocation.setLatitude(currentLat);
                currentLocation.setLongitude(currentLng);
                if (centerID == null || centerID.equals("0")) {
                    Toast.makeText(VisitActivity.this, "Please select a center.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (latestFix == null) {
                    Toast.makeText(getApplicationContext(), "Getting location... Please wait a moment.", Toast.LENGTH_SHORT).show();
                    fetchLocationOrAskSettings();
                    return;
                }
                if (TextUtils.isEmpty(imageBase64)){
                    Toast.makeText(VisitActivity.this, "Please capture an image before submitting.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mapType.equalsIgnoreCase("1")){


                    double loginLat = Double.parseDouble(loginLatStr);
                    double loginLng = Double.parseDouble(loginLngStr);
                    Location loginLocation = new Location("");
                    loginLocation.setLatitude(loginLat);
                    loginLocation.setLongitude(loginLng);
                    //checkValidation
                    boolean isAllowed = isWithinRadius(currentLocation, loginLocation, Float.parseFloat(radiusMeter));
                    if (isAllowed) {
                        //save data
                        ContentValues values = new ContentValues();
                        values.put("center_id", centerID);
                        values.put("imageBase64", imageBase64);
                        values.put("lat", latestFix.getLongitude());
                        values.put("long", latestFix.getLatitude());
                        values.put("is_pushed", "0");
                        values.put("remarks", remarks.getText().toString().trim());

                        ds.insertBoolenatable(values, Tables.TABLE_Name_Visit_Image);
                        Toast.makeText(VisitActivity.this, "Visit Successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(),Dashboard.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(VisitActivity.this, "You are not allowed from this location.", Toast.LENGTH_LONG).show();
                    }

                } else if (mapType.equalsIgnoreCase("2")) {

                    Log.e("LocationMode", "GeoFence Polygon Mode");

                    // ✅ Safety checks
                    if (selectedCenter == null || selectedCenter.coordinates == null || selectedCenter.coordinates.size() < 3) {
                        Toast.makeText(VisitActivity.this, "Invalid geofence polygon", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<CoordinateModel> coords = selectedCenter.coordinates;

                    // ✅ Convert to polygon points for check
                    List<Location> geofencePoints = new ArrayList<>();

                    for (int i = 0; i < coords.size(); i++) {
                        CoordinateModel c = coords.get(i);

                        Log.e("GeoFenceCheck", "Pin " + i + ": lat=" + c.lat + ", lng=" + c.lng);

                        Location location = new Location("");
                        location.setLatitude(c.lat);
                        location.setLongitude(c.lng);

                        geofencePoints.add(location);
                    }
                    Log.e("GeoFenceCheck", "Geofence Points Size: " + geofencePoints.size());
                    // ✅ Check inside polygon
                    boolean inside = isInsideGeofence(currentLocation, geofencePoints);
                    Log.d("GeoFenceCheck", "Current Location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
                    Log.d("GeoFenceCheck", "Inside geofence? " + inside);

                    if (inside) {
                        ContentValues values = new ContentValues();
                        values.put("center_id", centerID);
                        values.put("imageBase64", imageBase64);
                        values.put("lat", latestFix.getLongitude());
                        values.put("long", latestFix.getLatitude());
                        values.put("is_pushed", "0");
                        values.put("remarks", remarks.getText().toString().trim());

                        ds.insertBoolenatable(values, Tables.TABLE_Name_Visit_Image);

                        Toast.makeText(VisitActivity.this, "Visit Successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(),Dashboard.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(VisitActivity.this, "You are not allowed from this location.", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
    }
    private void routeSpinnerSetup()
    {
        int position = 0;
        centerList.clear();
        ThanaName.clear();

        // default item
        centerList.add(new CenterModel("0", "Select Center", "0", "0", new ArrayList<>()));
        ThanaName.add("Select Center");

        SpinnerAdapter dataAdapter = new SpinnerAdapter(this, R.layout
                .spinner_item, ThanaName);
        route.setAdapter(dataAdapter);
        route.setSelection(position);

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( VisitActivity.this, Dashboard.class));
            finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
    private void fetchLocationOrAskSettings() {
        if (!locationHelper.hasFinePermission()) {
            locationHelper.requestFinePermission();
            return;
        }
        locationHelper.requestSingleFix(new LocationHelper.SingleFixCallback() {
            @Override
            public void onLocationReady(@NonNull Location loc) {
                latestFix = loc;
                Log.e("Location", "Fix: lat=" + loc.getLatitude() + " lon=" + loc.getLongitude()
                        + " acc=" + loc.getAccuracy());
                Toast.makeText(VisitActivity.this, "Location ready", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(@NonNull String reason) {
                Log.e("Location", "Failed: " + reason);
                Toast.makeText(VisitActivity.this, "Location failed: " + reason, Toast.LENGTH_LONG).show();
            }


            @Override
            public void onResolutionRequired(ResolvableApiException resolvable) {
                try {
                    resolvable.startResolutionForResult(VisitActivity.this, 7001);
                } catch (IntentSender.SendIntentException e) {
                    onFailed("Resolution launch failed");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ✅ Handle CameraHelper result ONLY when requestCode is CAMERA_REQUEST_CODE
        if (cameraHelper != null && requestCode == 2002) {
            cameraHelper.handleActivityResult(requestCode, resultCode, data);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (cameraHelper != null) {
            cameraHelper.handleRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Handle the case where the permission is denied
                Toast.makeText(this, "Permission denied. Cannot proceed with location features.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void OnLocalDBdataRetrive(String json) throws JSONException {

    }

    @Override
    public void OnLocalDBdataRetrive(ArrayList<HashMap<String, String>> arrayList) {

    }

    @Override
    public void OnLocalDBdataRetrive(HashMap<String, String> hasmap) {

    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {

    }

    @Override
    public void OnConnetivityError() {

    }

}