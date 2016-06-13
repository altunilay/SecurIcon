package com.example.nilayaltun.secureicon;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;
import android.net.Uri;
import android.widget.Toast;
import android.content.DialogInterface;
import android.app.AlertDialog;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import android.database.Cursor;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imageToUpload;
    Button bUploadImage;


    private static final int RESULT_LOAD_IMAGE = 1;
    private String selectedImagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageToUpload = (ImageView) this.findViewById(R.id.imageToUpload);
        bUploadImage = (Button) this.findViewById(R.id.bUploadImage);

        bUploadImage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                new doitAsync().execute();

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Status");
                alertDialog.setMessage(" Your Password is Delivered !");


                alertDialog.show();  
            }

        });

        imageToUpload.setOnClickListener(this);


    }

    class doitAsync extends android.os.AsyncTask<Void, Integer, Integer> {


        @Override
        protected Integer doInBackground(Void... params) {


            Socket sock;
            try {
                sock = new Socket("149.125.73.29", 8000);
                System.out.println("Connecting...");

                // sendfile
                File myFile = new File(selectedImagePath);
                byte[] mybytearray = new byte[(int) myFile.length()];
                FileInputStream fis = new FileInputStream(myFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                bis.read(mybytearray, 0, mybytearray.length);
                OutputStream os = sock.getOutputStream();
                System.out.println("Sending...");
                os.write(mybytearray, 0, mybytearray.length);
                os.flush();
                sock.close();





            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.err.println("Don't know about host : ");
                //System.exit(1);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.err.println("Don't know input output ");
                //System.exit(1);
            }


            return 0;

        }
    }


    @Override
    public void onClick(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            selectedImagePath = getPath(selectedImage);
            imageToUpload.setImageURI(selectedImage);


        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
