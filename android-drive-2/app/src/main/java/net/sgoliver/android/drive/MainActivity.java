package net.sgoliver.android.drive;

import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class MainActivity extends AppCompatActivity
                            implements GoogleApiClient.OnConnectionFailedListener {

    private final static String LOGTAG = "android-drive";

    protected static final int REQ_OPEN_FILE = 1002;

    private GoogleApiClient apiClient;

    private Button btnConsultarMetadatos;
    private Button btnModificarMetadatos;
    private Button btnLeerFichero;
    private Button btnEscribirFichero;
    private Button btnLeerFicheroAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Drive.API)
            .addScope(Drive.SCOPE_FILE)
            //.addScope(Drive.SCOPE_APPFOLDER)
            .build();

        btnConsultarMetadatos = (Button)findViewById(R.id.btnConsultarMetadatos);
        btnModificarMetadatos = (Button)findViewById(R.id.btnModificarMetadatos);
        btnLeerFichero = (Button)findViewById(R.id.btnLeerFichero);
        btnEscribirFichero = (Button)findViewById(R.id.btnEscribirFichero);
        btnLeerFicheroAct = (Button)findViewById(R.id.btnLeerFicheroAct);

        btnConsultarMetadatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        getMetadata(DriveId.decodeFromString("DriveId:CAESABjMGSD6wKnM7lQoAA=="));
                    }
                }.start();
            }
        });

        btnModificarMetadatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        updateMetadata(DriveId.decodeFromString("DriveId:CAESABjMGSD6wKnM7lQoAA=="));
                    }
                }.start();
            }
        });

        btnLeerFichero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        readFile(DriveId.decodeFromString("DriveId:CAESABjMGSD6wKnM7lQoAA=="));
                    }
                }.start();
            }
        });

        btnEscribirFichero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        writeFile(DriveId.decodeFromString("DriveId:CAESABjMGSD6wKnM7lQoAA=="));
                    }
                }.start();
            }
        });

        btnLeerFicheroAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileWithActivity();
            }
        });
    }

    private void readFile(DriveId fileDriveId) {

        DriveFile file = fileDriveId.asDriveFile();

        file.open(apiClient, DriveFile.MODE_READ_ONLY, null)
            .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e(LOGTAG,"Error al abrir fichero (readFile)");
                        return;
                    }

                    DriveContents contents = result.getDriveContents();

                    BufferedReader reader =
                        new BufferedReader(
                            new InputStreamReader(contents.getInputStream()));

                    StringBuilder builder = new StringBuilder();

                    try {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    } catch (IOException e) {
                        Log.e(LOGTAG,"Error al leer fichero");
                    }

                    contents.discard(apiClient);

                    Log.i(LOGTAG, "Fichero leido: " + builder.toString());
                }
            });
    }

    private void writeFile(DriveId fileDriveId) {

        DriveFile file = fileDriveId.asDriveFile();

        file.open(apiClient, DriveFile.MODE_WRITE_ONLY, null)
            .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e(LOGTAG,"Error al abrir fichero (writeFile)");
                        return;
                    }

                    DriveContents contents = result.getDriveContents();

                    BufferedWriter writer =
                            new BufferedWriter(
                                    new OutputStreamWriter(contents.getOutputStream()));

                    try {
                        writer.write("Contenido del fichero modificado!");
                        writer.flush();
                    } catch (IOException e) {
                        Log.e(LOGTAG,"Error al escribir fichero");
                    }

                    //Opcional: cambio de metadatos
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setMimeType("text/plain")
                            .build();

                    contents.commit(apiClient, changeSet).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status result) {
                                if(result.getStatus().isSuccess())
                                    Log.i(LOGTAG, "Fichero escrito correctamente");
                                else
                                    Log.e(LOGTAG, "Error al escribir fichero");
                            }
                        });
                }
            });
    }

    private void openFileWithActivity() {

        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[] { "text/plain" })
                .build(apiClient);

        try {
            startIntentSenderForResult(
                    intentSender, REQ_OPEN_FILE, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e(LOGTAG, "Error al iniciar actividad: Open File", e);
        }
    }

    private void getMetadata(DriveId fileDriveId) {
        DriveFile file = fileDriveId.asDriveFile();

        file.getMetadata(apiClient).setResultCallback(
            new ResultCallback<DriveResource.MetadataResult>() {
                @Override
                public void onResult(DriveResource.MetadataResult metadataResult) {
                    if (metadataResult.getStatus().isSuccess()) {
                        Metadata metadata = metadataResult.getMetadata();
                        Log.i(LOGTAG, "Metadatos obtenidos correctamente." +
                                " Title: " + metadata.getTitle() +
                                " LastUpdated: " + metadata.getModifiedDate());
                    }
                    else {
                        Log.e(LOGTAG, "Error al obtener metadatos");
                    }
                }
            });
    }

    private void updateMetadata(DriveId fileDriveId) {
        DriveFile file = fileDriveId.asDriveFile();

        MetadataChangeSet changeSet =
            new MetadataChangeSet.Builder()
                .setTitle("TituloModificado.txt")
                .build();

        file.updateMetadata(apiClient, changeSet).setResultCallback(
            new ResultCallback<DriveResource.MetadataResult>() {
                @Override
                public void onResult(DriveResource.MetadataResult metadataResult) {
                    if (metadataResult.getStatus().isSuccess()) {
                        Metadata metadata = metadataResult.getMetadata();
                        Log.i(LOGTAG, "Metadatos actualizados correctamente.");
                    }
                    else {
                        Log.e(LOGTAG, "Error al actualizar metadatos");
                    }
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_OPEN_FILE:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    Log.i(LOGTAG, "Fichero seleccionado ID = " + driveId);

                    readFile(driveId);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Error de conexion!", Toast.LENGTH_SHORT).show();
        Log.e(LOGTAG, "OnConnectionFailed: " + connectionResult);
    }
}
