package com.example.arihanemariano.roupah;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private Uri fileUri;
    private Uri selectedImage;
    private Bitmap photo;
    private String picturePath;
    private ImageView img;
    String ba1;
    private Firebase base;
    Button btpic, btnup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final TextView resul = (TextView) findViewById(R.id.result);
        String URL =  new ConnectionBase().getBase();
         base = new Firebase(URL);
        final StringBuilder finalResult = new StringBuilder();

        img = (ImageView)findViewById(R.id.imageView);
        btpic = (Button) findViewById(R.id.cpic);
        btpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPick();
            }
        });

        btnup = (Button) findViewById(R.id.up);
        btnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });


        base.child("img").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot snapshot) {
                /*finalResult.append(snapshot.getValue());
                resul.setText(finalResult);
                Bitmap bitmap = DownloadImage(finalResult.toString());
                img.setImageBitmap(bitmap);*/
                Toast.makeText(getApplication(), "It changed", Toast.LENGTH_LONG).show();


            }

            @Override
            public void onCancelled(FirebaseError error) {
            }

        });


     }

    public void upload(){
        Log.e("path", "----" + picturePath);

        Bitmap bm = BitmapFactory.decodeFile(picturePath);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, bao);
        bm.recycle();
        byte[] ba = bao.toByteArray();
        ba1 = Base64.encodeToString(ba, Base64.DEFAULT);

        Log.e("path", "----" + ba1);

        Roupa roupa = new Roupa(ba1, "teste","descri","cor");
        base.push().setValue(roupa);

    }

    private Bitmap DownloadImage(String URL) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = new ConnectionBase().OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return bitmap;
    }

    private void clickPick(){

        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            startActivityForResult(intent, 100);

        }else{
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 100 && resultCode == RESULT_OK){

            selectedImage = data.getData();
            photo = (Bitmap) data.getExtras().get("data");


            String[] filePathComum ={MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathComum, null, null, null);
            cursor.moveToFirst();

            int columIndex = cursor.getColumnIndex(filePathComum[0]);
            picturePath = cursor.getString(columIndex);

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            img = (ImageView)findViewById(R.id.imageView);
            img.setImageBitmap(photo);

        }else{
            Toast.makeText(getApplication(), "Nothing happened", Toast.LENGTH_LONG).show();
        }

        class UploadToServer extends AsyncTask<Void, Void, String>{
            private ProgressDialog pd = new ProgressDialog(MainActivity.this);

            protected void onPreExecute(){
                super.onPreExecute();
                pd.setMessage("Wait image upload!");
                pd.show();
            }



            @Override
            protected String doInBackground(Void... params) {

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("base64","b1"));
                nameValuePairs.add(new BasicNameValuePair("imageName", System.currentTimeMillis()+".jpg"));



                return null;
            }
        }
    }





    }





