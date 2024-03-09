package com.example.gpstrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.HashMap;

public class YandexMapKitView extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private MapView mapView;
    private UserLocationLayer userLocationLayer;
    private boolean flagstart = false;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Создаем HashMap для хранения предыдущих меток каждого пользователя
    HashMap<String, PlacemarkMapObject> previousUserPlacemarks = new HashMap<>();


    // Создаем Map для хранения предыдущих меток каждого пользователя


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapKitFactory.setApiKey("433174eb-3eb8-432b-aadc-37333bc7c474");
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_yandex_map_kit_view);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        //startService(new Intent(this, LocationService.class));

        mapView = findViewById(R.id.mapview);
        mapView.getMap().move(
                new CameraPosition(new Point(59.945933, 30.320045), 14.0f, 0.0f, 0.0f));


        // Получение ссылки на базу данных Firebase и чтение всех местоположений, кроме текущего пользователя
        String userEmail = currentUser.getEmail();
        String firebasePath = userEmail.replace('.', '_');
        firebasePath = firebasePath.replace('@', '_');
        DatabaseReference locationsRef = FirebaseDatabase.getInstance().getReference("locations");
        String finalFirebasePath = firebasePath;


        locationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userEmail = snapshot.getKey();

                    // Проверяем, не является ли это текущий пользователь
                    System.out.println(userEmail);
                    System.out.println(currentUser.getEmail());

                    if (userEmail.equals(currentUser.getEmail().replace('.', '_').replace('@', '_'))) {
                        continue; // Пропускаем текущего пользователя
                    }

                    Double latitudeValue = snapshot.child("latitude").getValue(Double.class);
                    Double longitudeValue = snapshot.child("longitude").getValue(Double.class);

                    if (latitudeValue != null && longitudeValue != null) {
                        double latitude = latitudeValue;
                        double longitude = longitudeValue;

                        // Удаляем предыдущую метку пользователя, если она существует
                        PlacemarkMapObject previousPlacemark = previousUserPlacemarks.get(userEmail);
                        if (previousPlacemark != null) {
                            mapView.getMap().getMapObjects().remove(previousPlacemark);
                        }

                        // Создание новой метки на карте для местоположения пользователя
                        PlacemarkMapObject placemark = mapView.getMap().getMapObjects().addPlacemark(new Point(latitude, longitude));
                        placemark.setOpacity(0.8f);
                        placemark.setIcon(ImageProvider.fromResource(YandexMapKitView.this, R.drawable.location_icon));
                        placemark.setUserData(userEmail);

                        placemark.addTapListener((mapObject, point) -> {
                            String email = placemark.getUserData().toString();
                            Toast.makeText(YandexMapKitView.this, email, Toast.LENGTH_SHORT).show();
                            return true;
                        });

                        // Сохраняем ссылку на новую метку пользователя
                        previousUserPlacemarks.put(userEmail, placemark);
                    }
                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибки при чтении из базы данных
            }
        });

        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

//                if (previousPlacemark2 != null) {
//                    mapView.getMap().getMapObjects().remove(previousPlacemark2);
//                }
                for (Location location : locationResult.getLocations()) {
                    // Получение местоположения
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

//                    PlacemarkMapObject placemark2 = mapView.getMap().getMapObjects().addPlacemark(new Point(latitude, longitude));
//                    placemark2.setOpacity(0.8f);
//                    placemark2.setIcon(ImageProvider.fromResource(YandexMapKitView.this, R.drawable.location_icon));
//                    placemark2.setUserData(userEmail);
//
//                    previousPlacemark2 = placemark2;

                    String userEmail = currentUser.getEmail();
                    // Заменяем символы, которые не допускаются в пути Firebase Database
                    String firebasePath = userEmail.replace('.', '_'); // Заменяем '.' на '_'
                    firebasePath = firebasePath.replace('@', '_'); // Заменяем '@' на '_'

                    // Теперь вы можете использовать firebasePath для сохранения локации в Firebase
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations").child(firebasePath);
                    ref.child("latitude").setValue(latitude);
                    ref.child("longitude").setValue(longitude);

                    // Дальнейшая обработка местоположения
                    if (!flagstart) {
                        Toast.makeText(YandexMapKitView.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                        mapView.getMap().move(
                                new CameraPosition(new Point(latitude, longitude), 14.0f, 0.0f, 0.0f));
                        flagstart = true;
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // Интервал обновления местоположения в миллисекундах

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();


    }


    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }
}
