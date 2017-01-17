package net.sgoliver.android.fcm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = "android-fcm";

    private Button btnToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnToken = (Button)findViewById(R.id.btnToken);
        btnToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se obtiene el token actualizado
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                Log.d(LOGTAG, "Token actualizado: " + refreshedToken);
            }
        });
    }
}
