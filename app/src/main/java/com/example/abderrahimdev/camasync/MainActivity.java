package com.example.abderrahimdev.camasync;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Camera myCamera;
    LinearLayout imagesLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //on récupère le layout contenue dans la scrollView dédié pour afficher les photos
        imagesLayout = findViewById(R.id.imageLayout);
        Button btn = findViewById(R.id.button2);

        //en cliquant sur le button pour
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CamAsync().execute();
            }
        });

    }

    public byte[] imageByte = null;


    class CamAsync extends AsyncTask<Void, byte[], Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //réinitialise l'interface
            imagesLayout.removeAllViews();

            try {
                //on récupère un instance de la cam arrière
                myCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

            } catch (Exception e) {
                Log.e(getString(R.string.app_name), "On n'a pas pu ouvrir la camera");
                e.printStackTrace();
            }
            Toast.makeText(MainActivity.this, "Accés au Camera en cours ..", Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < 3; i++) {

                if (myCamera != null) {
                    try {
                        SurfaceTexture surfaceTexture = new SurfaceTexture(0);
                        myCamera.setPreviewTexture(surfaceTexture);
                        while (imageByte == null)
                        {
                            try{
                                myCamera.startPreview();
                                myCamera.takePicture(null, null, new PicCallback());
                            }catch(Exception ex){
                                System.out.print("Except");
                            }

                        }
                        publishProgress(imageByte);
                        SystemClock.sleep(1000);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    //si camera == null
                    System.out.print("ERR Cam  !");
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(byte[]... pic) {
            super.onProgressUpdate(pic);
            ImageView img = new ImageView(MainActivity.this);
            //conversion byte[] => bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(pic[0], 0, pic[0].length);
            //densité de la photo 320 => 70
            bitmap.setDensity(70);
            img.setImageBitmap(bitmap);
            img.setRotation(90);
            img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            img.setAdjustViewBounds(true);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 250);
            img.setLayoutParams(params);
            //Ajout de l'imageView au linear layout inclut dans la scroll view
            imagesLayout.addView(img);
            Toast.makeText(MainActivity.this, "Photo prise !", Toast.LENGTH_SHORT).show();
            imageByte = null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this, "Fin !", Toast.LENGTH_SHORT).show();

            //on libère la camera
            myCamera.release();
            myCamera = null;
        }


    }

    class PicCallback implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            imageByte = data;
        }
    }

}
