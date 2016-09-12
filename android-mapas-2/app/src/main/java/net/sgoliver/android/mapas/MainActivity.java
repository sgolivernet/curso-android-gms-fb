package net.sgoliver.android.mapas;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mapa;

    private Button btnOpciones;
    private Button btnMover;
    private Button btnAnimar;
    private Button btnPosicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        btnOpciones = (Button)findViewById(R.id.btnOpciones);
        btnOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarOpciones();
            }
        });

        btnMover = (Button)findViewById(R.id.btnMover);
        btnMover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moverMadrid();
            }
        });

        btnAnimar = (Button)findViewById(R.id.btnAnimar);
        btnAnimar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animarMadrid();
            }
        });

        btnPosicion = (Button)findViewById(R.id.btnPosicion);
        btnPosicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerPosicion();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapa = map;

        mapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                Projection proj = mapa.getProjection();
                Point coord = proj.toScreenLocation(point);

                Toast.makeText(
                        MainActivity.this,
                        "Click\n" +
                                "Lat: " + point.latitude + "\n" +
                                "Lng: " + point.longitude + "\n" +
                                "X: " + coord.x + " - Y: " + coord.y,
                        Toast.LENGTH_SHORT).show();
            }
        });

        mapa.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position) {
                Toast.makeText(
                        MainActivity.this,
                        "Cambio Cámara\n" +
                                "Lat: " + position.target.latitude + "\n" +
                                "Lng: " + position.target.longitude + "\n" +
                                "Zoom: " + position.zoom + "\n" +
                                "Orientación: " + position.bearing + "\n" +
                                "Ángulo: " + position.tilt,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cambiarOpciones()
    {
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mapa.getUiSettings().setZoomControlsEnabled(true);
    }

    private void moverMadrid()
    {
        CameraUpdate camUpd1 =
                CameraUpdateFactory
                        .newLatLngZoom(new LatLng(40.41, -3.69), 5);

        mapa.moveCamera(camUpd1);
    }

    private void animarMadrid()
    {
        LatLng madrid = new LatLng(40.417325, -3.683081);
        CameraPosition camPos = new CameraPosition.Builder()
                .target(madrid)   //Centramos el mapa en Madrid
                .zoom(19)         //Establecemos el zoom en 19
                .bearing(45)      //Establecemos la orientación con el noreste arriba
                .tilt(70)         //Bajamos el punto de vista de la cámara 70 grados
                .build();

        CameraUpdate camUpd3 =
                CameraUpdateFactory.newCameraPosition(camPos);

        mapa.animateCamera(camUpd3);
    }

    private void obtenerPosicion()
    {
        CameraPosition camPos = mapa.getCameraPosition();

        LatLng coordenadas = camPos.target;
        double latitud = coordenadas.latitude;
        double longitud = coordenadas.longitude;

        Toast.makeText(this, "Lat: " + latitud + " | Long: " + longitud, Toast.LENGTH_SHORT).show();
    }
}
