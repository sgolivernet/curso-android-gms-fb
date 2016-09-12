package net.sgoliver.android.mapas;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mapa;

    private Button btnMarcador;
    private Button btnLineas;
    private Button btnPoligono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        btnMarcador = (Button)findViewById(R.id.btnMarcador);
        btnMarcador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarMarcador();
            }
        });

        btnLineas = (Button)findViewById(R.id.btnLineas);
        btnLineas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarLineas();
            }
        });

        btnPoligono = (Button)findViewById(R.id.btnPoligono);
        btnPoligono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarPoligono();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapa = map;

        mapa.getUiSettings().setMapToolbarEnabled(false);

        mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(
                        MainActivity.this,
                        "Marcador pulsado:\n" +
                                marker.getTitle(),
                        Toast.LENGTH_SHORT).show();

                return true;
            }
        });
    }

    private void insertarMarcador() {
        mapa.addMarker(new MarkerOptions()
                .position(new LatLng(40.3936945, -3.701519))
                .title("Pais: España"));
    }

    private void mostrarLineas()
    {
        //Dibujo con Lineas

        PolylineOptions lineas = new PolylineOptions()
                .add(new LatLng(45.0, -12.0))
                .add(new LatLng(45.0, 5.0))
                .add(new LatLng(34.5, 5.0))
                .add(new LatLng(34.5, -12.0))
                .add(new LatLng(45.0, -12.0));

        lineas.width(8);
        lineas.color(Color.RED);

        mapa.addPolyline(lineas);
    }

    private void mostrarPoligono()
    {
        //Dibujo con Poligonos

        PolygonOptions rectangulo = new PolygonOptions()
                .add(new LatLng(45.0, -12.0),
                        new LatLng(45.0, 5.0),
                        new LatLng(34.5, 5.0),
                        new LatLng(34.5, -12.0),
                        new LatLng(45.0, -12.0));

        rectangulo.strokeWidth(8);
        rectangulo.strokeColor(Color.RED);

        mapa.addPolygon(rectangulo);
    }
}
