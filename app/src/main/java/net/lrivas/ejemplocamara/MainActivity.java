package net.lrivas.ejemplocamara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
Button btnTomarFoto;
ImageView imgFoto,imgEmail, imgWhats;
String rutaImgen;

TextView txt1,txt2,txt3;

private static final int REQUEST_CODIGO_CAMARA=200;
private static final int REQUEST_CODIGO_CAPTURA_IMAGEN=300;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgFoto = findViewById(R.id.imageView);
        btnTomarFoto = findViewById(R.id.button);
         txt1 = findViewById(R.id.textView);
        txt2 = findViewById(R.id.textView6);
        txt3 = findViewById(R.id.textView7);

        imgEmail = findViewById(R.id.imageView3);
        imgWhats = findViewById(R.id.imageView2);



        txt1.setVisibility(View.INVISIBLE);
        txt2.setVisibility(View.INVISIBLE);
        txt3.setVisibility(View.INVISIBLE);

        imgEmail.setVisibility(View.INVISIBLE);
        imgWhats.setVisibility(View.INVISIBLE);

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txt1.setVisibility(View.VISIBLE);
                txt2.setVisibility(View.VISIBLE);
                txt3.setVisibility(View.VISIBLE);

                imgEmail.setVisibility(View.VISIBLE);
                imgWhats.setVisibility(View.VISIBLE);
                procesaFoto();
            }
        });

        imgWhats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent whats  = new Intent(Intent.ACTION_SEND);
                whats.setType("image/*");
                whats.putExtra(Intent.EXTRA_STREAM, Uri.parse(rutaImgen));
                whats.setPackage("com.whatsapp");

                startActivity(whats);
            }
        });

        imgEmail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(rutaImgen));
                startActivity(Intent.createChooser(intent,""));
            }
        });
    }

    private void procesaFoto() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){

            if (ActivityCompat.checkSelfPermission(MainActivity.this,android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){

                tomarFoto();
            }else{

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA},REQUEST_CODIGO_CAMARA);
            }


        }else {
            tomarFoto();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult){

        if (requestCode == REQUEST_CODIGO_CAMARA){

            if (permissions.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){

                tomarFoto();
            }else{

                Toast.makeText(MainActivity.this, "Se requiere permisos para la camara", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResult);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data){
      if (requestCode == REQUEST_CODIGO_CAPTURA_IMAGEN){

          if (resultCode == Activity.RESULT_OK){
              imgFoto.setImageURI(Uri.parse((rutaImgen)));

          }
      }
      super.onActivityResult(requestCode,resultCode, data);
    }
    private void tomarFoto() {

        Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentCamara.resolveActivity(getPackageManager())!=null){

            File archivofoto = null;
            archivofoto = crearArchivo();
            if (archivofoto!=null){

                Uri rutaFoto = FileProvider.getUriForFile(
                        MainActivity.this, "net.lrivas.ejemplocamara",
                        archivofoto
                );
                intentCamara.putExtra(MediaStore.EXTRA_OUTPUT, rutaFoto);
                startActivityForResult(intentCamara,REQUEST_CODIGO_CAPTURA_IMAGEN);
            }
        }
    }

    private File crearArchivo() {

        String nomenclatura = new SimpleDateFormat("yyyyMMdd_HHmss", Locale.getDefault()).format(new Date());
        String prefijoArchivo = "APPCAM_" +nomenclatura+"_";
        File directorioImage = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File miImagen = null;
        try {
            miImagen = File.createTempFile(prefijoArchivo, ".jpg", directorioImage);
            rutaImgen = miImagen.getAbsolutePath();
        }catch (IOException e){
            e.printStackTrace();


        }
        return  miImagen;
    }
}