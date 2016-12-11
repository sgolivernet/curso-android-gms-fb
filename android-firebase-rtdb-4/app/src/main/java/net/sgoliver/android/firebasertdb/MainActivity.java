package net.sgoliver.android.firebasertdb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAGLOG = "firebase-db";

    private Button btnEscribirSimple;
    private Button btnEscribirMap;
    private Button btnEscribirList;
    private Button btnEscribirObjeto;
    private Button btnUpdateChildren;
    private Button btnInsertarPush;
    private Button btnEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEscribirSimple = (Button)findViewById(R.id.btnEscribirSimple);
        btnEscribirSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbRef =
                        FirebaseDatabase.getInstance().getReference()
                                .child("dias-semana");

                dbRef.child("dia7").setValue("domingo");
            }
        });

        btnEscribirMap = (Button)findViewById(R.id.btnEscribirMap);
        btnEscribirMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbRef =
                        FirebaseDatabase.getInstance().getReference()
                                .child("dias-semana");

                Map<String, String> domingo = new HashMap<>();
                domingo.put("periodo-1", "domingo-mañana");
                domingo.put("periodo-2", "domingo-tarde");
                domingo.put("periodo-3", "domingo-noche");

                dbRef.child("dia7").setValue(domingo);
            }
        });

        btnEscribirList = (Button)findViewById(R.id.btnEscribirList);
        btnEscribirList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbRef =
                        FirebaseDatabase.getInstance().getReference()
                                .child("dias-semana");

                List<String> domingo = new LinkedList<>();
                domingo.add("mañana");
                domingo.add("tarde");
                domingo.add("noche");

                dbRef.child("dia7").setValue(domingo);
            }
        });

        btnEscribirObjeto = (Button)findViewById(R.id.btnEscribirObjeto);
        btnEscribirObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbRef =
                        FirebaseDatabase.getInstance().getReference()
                                .child("predicciones");

                Prediccion pred =
                        new Prediccion("01/12/2016", "Despejado", 29, 35);

                dbRef.child("20161201").setValue(pred);
            }
        });

        btnUpdateChildren = (Button)findViewById(R.id.btnUpdateChildren);
        btnUpdateChildren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //EJEMPLO 1 (Sin evento onComplete)
                /*
                DatabaseReference dbRef =
                        FirebaseDatabase.getInstance().getReference()
                                .child("predicciones");

                Map<String, Object> actualizacion = new HashMap<>();
                actualizacion.put("/temperatura", 29);
                        actualizacion.put("/humedad", 34);

                dbRef.child("20161120")
                        .updateChildren(actualizacion);
                */

                //EJEMPLO 2 (Con evento onComplete)
                DatabaseReference dbRef =
                        FirebaseDatabase.getInstance().getReference();

                Map<String, Object> actualizacion2 = new HashMap<>();
                actualizacion2.put("/20161120/temperatura", 25);
                actualizacion2.put("/20161121/humedad", 31);

                dbRef.child("predicciones")
                    .updateChildren(actualizacion2, new DatabaseReference.CompletionListener(){
                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                            if(error == null)
                                Log.i(TAGLOG, "Operación OK");
                            else
                                Log.e(TAGLOG, "Error: " + error.getMessage());
                        }
                    });
            }
        });

        btnInsertarPush = (Button)findViewById(R.id.btnInsertarPush);
        btnInsertarPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbPredicciones =
                        FirebaseDatabase.getInstance().getReference()
                                .child("predicciones");

                Prediccion p1 = new Prediccion("02/12/2016", "Soleado", 29, 35);
                Prediccion p2 = new Prediccion("03/12/2016", "Nublado", 23, 52);

                dbPredicciones.push().setValue(p1);
                dbPredicciones.push().setValue(p2);
            }
        });

        btnEliminar = (Button)findViewById(R.id.btnEliminar);
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbRef =
                        FirebaseDatabase.getInstance().getReference();

                //Alternativa 1
                dbRef.child("dias-semana").child("dia6").setValue(null);

                //Alternativa 2
                dbRef.child("dias-semana").child("dia7").removeValue();
            }
        });
    }
}
