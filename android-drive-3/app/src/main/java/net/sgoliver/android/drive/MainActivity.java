package net.sgoliver.android.drive;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class MainActivity extends AppCompatActivity
             implements GoogleApiClient.OnConnectionFailedListener {

    private final static String LOGTAG = "android-drive";

    private GoogleApiClient apiClient;

    private Button btnListarCarpeta;
    private Button btnBuscarEnCarpeta;
    private Button btnBuscarEnDrive;
    private Button btnEscribirAppFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Drive.API)
            .addScope(Drive.SCOPE_FILE)
            .addScope(Drive.SCOPE_APPFOLDER)
            .build();

        btnListarCarpeta = (Button)findViewById(R.id.btnListarCarpeta);
        btnBuscarEnCarpeta = (Button)findViewById(R.id.btnBuscarEnCarpeta);
        btnBuscarEnDrive = (Button)findViewById(R.id.btnBuscarEnDrive);
        btnEscribirAppFolder = (Button)findViewById(R.id.btnEscribirAppFolder);

        btnListarCarpeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        listFolder(DriveId.decodeFromString("DriveId:CAESABjcGSD6wKnM7lQoAQ=="));
                    }
                }.start();
            }
        });

        btnBuscarEnCarpeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        searchFolder(DriveId.decodeFromString("DriveId:CAESABjcGSD6wKnM7lQoAQ=="));
                    }
                }.start();
            }
        });

        btnBuscarEnDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        searchDrive();
                    }
                }.start();
            }
        });

        btnEscribirAppFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        createFileAppFolder("prueba-appfolder.txt");
                    }
                }.start();
            }
        });
    }

    private void searchFolder(DriveId folderDriveId) {

        DriveFolder folder = folderDriveId.asDriveFolder();

        Query query = new Query.Builder()
            .addFilter(Filters.eq(SearchableField.TITLE, "prueba2.html"))
            .build();

        folder.queryChildren(apiClient, query)
            .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult mdBufferResult) {
                    if (mdBufferResult.getStatus().isSuccess()) {
                        if(mdBufferResult.getMetadataBuffer().getCount() > 0)
                            Log.i(LOGTAG, "Ficheros encontrados!");
                        else
                            Log.i(LOGTAG, "No se han encontrado ficheros!");

                        for(Metadata m: mdBufferResult.getMetadataBuffer())
                            Log.i(LOGTAG, "Fichero: " + m.getTitle() + " [" + m.getDriveId().encodeToString() + "]");
                    }
                    else {
                        Log.e(LOGTAG, "Error al buscar en carpeta");
                    }
                }
            });
    }

    private void searchDrive() {

        Query query = new Query.Builder()
            .addFilter(Filters.and(
                Filters.eq(SearchableField.MIME_TYPE, "text/css"),
                Filters.contains(SearchableField.TITLE, "prueba")))
            .build();

        Drive.DriveApi.query(apiClient, query)
            .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult mdBufferResult) {
                    if (mdBufferResult.getStatus().isSuccess()) {
                        if(mdBufferResult.getMetadataBuffer().getCount() > 0)
                            Log.i(LOGTAG, "Ficheros encontrados!");
                        else
                            Log.i(LOGTAG, "No se han encontrado ficheros!");

                        for(Metadata m: mdBufferResult.getMetadataBuffer())
                            Log.i(LOGTAG, "Fichero: " + m.getTitle() + " [" + m.getDriveId().encodeToString() + "]");
                    }
                    else {
                        Log.e(LOGTAG, "Error al buscar en carpeta");
                    }
                }
            });
    }

    private void listFolder(DriveId folderDriveId) {

        DriveFolder folder = folderDriveId.asDriveFolder();

        folder.listChildren(apiClient).setResultCallback(
            new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                    if (metadataBufferResult.getStatus().isSuccess()) {
                        for(Metadata m: metadataBufferResult.getMetadataBuffer())
                            Log.i(LOGTAG, "Fichero: " + m.getTitle() + " [" + m.getDriveId().encodeToString() + "]");
                    }
                    else {
                        Log.e(LOGTAG, "Error al listar carpeta");
                    }
                }
            });
    }

    private void createFileAppFolder(final String filename) {

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
                        //DriveFolder folder = Drive.DriveApi.getRootFolder(apiClient);

                        //Opción 2: Otra carpeta distinta al directorio raiz
                        //DriveFolder folder =
                        //    DriveId.decodeFromString("DriveId:CAESABjcGSD6wKnM7lQoAQ==").asDriveFolder();

                        //Opción 3: Carpeta de Aplicación (App Folder)
                        DriveFolder folder = Drive.DriveApi.getAppFolder(apiClient);

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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Error de conexion!", Toast.LENGTH_SHORT).show();
        Log.e(LOGTAG, "OnConnectionFailed: " + connectionResult);
    }
}
