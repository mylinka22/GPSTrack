package com.example.gpstrack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private LocationManager locationManager;
    private DatabaseReference locationsRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private boolean initialPositionSet = false;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        sessionManager = new SessionManager(this);

        // Получаем фрагмент карты из макета
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Асинхронно вызываем onMapReady, когда карта готова к использованию
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Инициализация Firebase
        //locationsRef = database.getReference("locations");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String userEmail = currentUser.getEmail();


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            startLocationUpdates();
        }

        ImageButton im2 = findViewById(R.id.im2);
        im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); // Выход из Firebase
                sessionManager.setLoggedIn(false);
                // Перемещение на предыдущую активити
                finish();
            }
        });



        // Получаем ссылку на базу данных Firebase и настроим слушатель для получения данных
        locationsRef = FirebaseDatabase.getInstance().getReference("locations");
        locationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Очищаем маркеры на карте перед добавлением новых
                googleMap.clear();

                // Перебираем каждую запись в базе данных Firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String user = snapshot.getKey();
                    Double latitude = snapshot.child("latitude").getValue(Double.class);
                    Double longitude = snapshot.child("longitude").getValue(Double.class);

                    if (latitude != null && longitude != null) {
                        LatLng location = new LatLng(latitude, longitude);
                        if (!user.equals(currentUser.getEmail().replace('.', '_').replace('@', '_'))) {


                            View markerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                                    .inflate(R.layout.custom_marker_layout, null);


                            // Получаем ссылку на ImageView из макета
                            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textView = markerView.findViewById(R.id.textView2);

                            // Устанавливаем новое изображение
                            String name = String.valueOf(user.charAt(0)).toUpperCase();
                            textView.setText(name);


                            // Добавление маркера на карту
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(location)
                                    .title(user)
                                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, markerView)));
                            googleMap.addMarker(markerOptions);

                            //googleMap.addMarker(new MarkerOptions().position(location).title(user));
                        } else {

                            // Создание пользовательского макета маркера
                            View markerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                                    .inflate(R.layout.custom_marker_layout, null);


                            // Получаем ссылку на ImageView из макета
                            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textView = markerView.findViewById(R.id.textView2);

                        // Устанавливаем новое изображение
                            String name = String.valueOf(user.charAt(0));
                            textView.setText(name);

                            // Настройка значений макета
//                            ImageView icon = markerView.findViewById(R.id.icon);
//                            TextView emailTextView = markerView.findViewById(R.id.emailTextView);
//                            icon.setImageResource(R.drawable.location_icon);
//                            emailTextView.setText(user);

                            // Добавление маркера на карту
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(location)
                                    .title(user)
                                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, markerView)));
                            googleMap.addMarker(markerOptions);


                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок при чтении из базы данных
            }
        });
    }

    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }



//    private void requestLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    LOCATION_PERMISSION_REQUEST_CODE);
//        }
//    }


    private void startLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // Интервал запроса местоположения (в миллисекундах)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Установка приоритета
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }

            for (Location location : locationResult.getLocations()) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Заменяем символы, которые не допускаются в пути Firebase Database
                String firebasePath = currentUser.getEmail().replace('.', '_').replace('@', '_');

                // Теперь вы можете использовать firebasePath для сохранения локации в Firebase
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations").child(firebasePath);
                ref.child("latitude").setValue(latitude);
                ref.child("longitude").setValue(longitude);


//                googleMap.addMarker(new MarkerOptions().position(initialLatLng).title("Моя позиция"));

                if (!initialPositionSet) {
                    LatLng initialLatLng = new LatLng(latitude, longitude);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 14.0f));
                    initialPositionSet = true;
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Получаем объект GoogleMap
        this.googleMap = googleMap;
        // Устанавливаем начальное положение и масштаб карты
        LatLng initialPosition = new LatLng(55.7583367, 37.8499733); // Начальные координаты (Москва)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 14.0f)); // Устанавливаем масштаб 14.0f
    }
}
