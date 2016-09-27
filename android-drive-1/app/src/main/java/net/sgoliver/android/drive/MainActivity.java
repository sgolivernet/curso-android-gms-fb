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

    protected static final int REQ_CREATE_FILE = 1001;

    private GoogleApiClient apiClient;

    private Button btnCrearCarpeta;
    private Button btnCrearFichero;
    private Button btnCrearFicheroAct;
    private Button btnEliminarFichero;

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

        btnCrearCarpeta = (Button)findViewById(R.id.btnCrearCarpeta);
        btnCrearFichero = (Button)findViewById(R.id.btnCrearFichero);
        btnCrearFicheroAct = (Button)findViewById(R.id.btnCrearFicheroAct);
        btnEliminarFichero = (Button)findViewById(R.id.btnEliminarFichero);

        btnCrearCarpeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        createFolder("Pruebas");
                    }
                }.start();
            }
        });

        btnCrearFichero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        createFile("prueba1.txt");
                    }
                }.start();
            }
        });

        btnCrearFicheroAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFileWithActivity();
            }
        });

        btnEliminarFichero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        deleteFile(DriveId.decodeFromString("DriveId:CAESABjMGSD6wKnM7lQoAA=="));
                    }
                }.start();
            }
        });
    }

    private void createFolder(final String foldername) {

        MetadataChangeSet changeSet =
            new MetadataChangeSet.Builder()
                .setTitle(foldername)
                .build();

        //Opción 1: Directorio raíz
        DriveFolder folder = Drive.DriveApi.getRootFolder(apiClient);

        //Opción 2: Otra carpeta distinta al directorio raiz
        //DriveFolder folder =
        //        DriveId.decodeFromString("DriveId:CAESABjKGSD6wKnM7lQoAQ==").asDriveFolder();

        //Opción 3: Carpeta de Aplicación (App Folder)
        //DriveFolder folder = Drive.DriveApi.getAppFolder(apiClient);

        folder.createFolder(apiClient, changeSet).setResultCallback(
            new ResultCallback<DriveFolder.DriveFolderResult>() {
                @Override
                public void onResult(DriveFolder.DriveFolderResult result) {
                    if (result.getStatus().isSuccess())
                        Log.i(LOGTAG, "Carpeta creada con ID = " + result.getDriveFolder().getDriveId());
                    else
                        Log.e(LOGTAG, "Error al crear carpeta");
                }
            });
    }

    private void createFile(final String filename) {

        Drive.DriveApi.newDriveContents(apiClient)
            .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (result.getStatus().isSuccess()) {

                        writeSampleText(result.getDriveContents());

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(filename)
                            .setMimeType("text/plain")
                            .build();

                        //Opción 1: Directorio raíz
                        DriveFolder folder = Drive.DriveApi.getRootFolder(apiClient);

                        //Opción 2: Otra carpeta distinta al directorio raiz
                        //DriveFolder folder =
                        //    DriveId.decodeFromString("DriveId:CAESABjcGSD6wKnM7lQoAQ==").asDriveFolder();

                        //Opción 3: Carpeta de Aplicación (App Folder)
                        //DriveFolder folder = Drive.DriveApi.getAppFolder(apiClient);

                        folder.createFile(apiClient, changeSet, result.getDriveContents())
                            .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                @Override
                                public void onResult(DriveFolder.DriveFileResult result) {
                                    if (result.getStatus().isSuccess()) {
                                        Log.i(LOGTAG, "Fichero creado con ID = " + result.getDriveFile().getDriveId());
                                    } else {
                                        Log.e(LOGTAG, "Error al crear el fichero");
                                    }
                                }
                            });
                    } else {
                        Log.e(LOGTAG, "Error al crear DriveContents");
                    }
                }
            });
    }

    private void createFileWithActivity() {

        Drive.DriveApi.newDriveContents(apiClient)
            .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    MetadataChangeSet changeSet =
                        new MetadataChangeSet.Builder()
                            .setMimeType("text/plain")
                            .build();

                    writeSampleText(result.getDriveContents());

                    IntentSender intentSender = Drive.DriveApi
                        .newCreateFileActivityBuilder()
                        .setInitialMetadata(changeSet)
                        .setInitialDriveContents(result.getDriveContents())
                        .build(apiClient);

                    try {
                        startIntentSenderForResult(
                            intentSender, REQ_CREATE_FILE, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(LOGTAG, "Error al iniciar actividad: Create File", e);
                    }
                }
            });
    }

    private void writeSampleText(DriveContents driveContents) {
        OutputStream outputStream = driveContents.getOutputStream();
        Writer writer = new OutputStreamWriter(outputStream);

        try {
            writer.write("Esto es un texto de prueba!");
            writer.close();
        } catch (IOException e) {
            Log.e(LOGTAG, "Error al escribir en el fichero: " + e.getMessage());
        }
    }

    private void deleteFile(DriveId fileDriveId) {
        DriveFile file = fileDriveId.asDriveFile();

        //Opción 1: Enviar a la papelera
        file.trash(apiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if(status.isSuccess())
                    Log.i(LOGTAG, "Fichero eliminado correctamente.");
                else
                    Log.e(LOGTAG, "Error al eliminar el fichero");
            }
        });

        //Opción 2: Eliminar
        //file.delete(apiClient).setResultCallback(...)
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CREATE_FILE:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    Log.i(LOGTAG, "Fichero creado con ID = " + driveId);
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
