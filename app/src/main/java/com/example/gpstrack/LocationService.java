//package com.example.gpstrack;
//import android.Manifest;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.IBinder;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//// Класс для службы местоположения
//public class LocationService extends Service implements LocationListener {
//
//    private LocationManager locationManager;
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // Инициализация LocationManager
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        // Получение разрешений на использование местоположения
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return START_NOT_STICKY;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
//
//        return START_STICKY;
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        // Получение новых координат местоположения и отправка их в Firebase
//        double latitude = location.getLatitude();
//        double longitude = location.getLongitude();
//
//        // Отправка данных в Firebase
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
//        ref.push().setValue(new GeoLocation(latitude, longitude));
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        // Остановка службы местоположения при уничтожении службы
//        if (locationManager != null) {
//            locationManager.removeUpdates(this);
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//}
