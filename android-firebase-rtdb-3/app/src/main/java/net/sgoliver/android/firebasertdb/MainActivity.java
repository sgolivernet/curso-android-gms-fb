package net.sgoliver.android.firebasertdb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {

    private static final String TAGLOG = "firebase-db";

    FirebaseRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ejemplo sin FirebaseUI

        Query dbDiasSemana =
            FirebaseDatabase.getInstance().getReference()
                .child("dias-semana")
                .orderByValue()
                .startAt("miercoles")
                .limitToFirst(2);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAGLOG, "onChildAdded: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAGLOG, "onChildChanged: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAGLOG, "onChildRemoved: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAGLOG, "onChildMoved: " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAGLOG, "Error!", databaseError.toException());
            }
        };

        dbDiasSemana.addChildEventListener(childEventListener);


        //Ejemplo con FirebaseUI

        Query dbPredicciones =
            FirebaseDatabase.getInstance().getReference()
                .child("predicciones")
                .orderByChild("cielo");

        RecyclerView recycler = (RecyclerView) findViewById(R.id.lstPredicciones);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        mAdapter =
            new FirebaseRecyclerAdapter<Prediccion, PrediccionHolder>(
                Prediccion.class, R.layout.item_lista, PrediccionHolder.class, dbPredicciones) {

            @Override
            public void populateViewHolder(PrediccionHolder predViewHolder, Prediccion pred, int position) {
                predViewHolder.setFecha(pred.getFecha());
                predViewHolder.setCielo(pred.getCielo());
                predViewHolder.setTemperatura(pred.getTemperatura() + "ºC");
                predViewHolder.setHumedad(pred.getHumedad() + "%");
            }
        };

        recycler.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
