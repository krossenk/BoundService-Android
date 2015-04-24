package com.iha.exercise5;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

/**
 * Created by Rossen on 23/04/15.
 */
public class ChangeValueService extends Service {

    private final IBinder mBinder = new LocalBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void sendNotification(String myString) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(myString)
                        .setContentText("Something interesting happened");
        int NOTIFICATION_ID = 12345;

        /*Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);*/
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * This method takes an image on phone, decodes it into
     */
    public void postImage() {
        String base64String = fileToBase64("/sdcard/before.jpg");
        base64ToBitmap(base64String);

        Intent mIntent = new Intent(getString(R.string.communication));
        sendBroadcast(mIntent);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://iry.dk:8880/IHA/IHA?imgName=PhoneTest&imgDest=PhoneTest.jpg";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("Response: ", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response: ", error.toString());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public String fileToBase64(String path) {
        Bitmap bm = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public Bitmap base64ToBitmap(String base64Img) {
        byte[] base64ToByte = Base64.decode(base64Img, Base64.DEFAULT);
        Bitmap byteToBitmap = BitmapFactory.decodeByteArray(base64ToByte, 0, base64ToByte.length);
        createImageFromBitmap(byteToBitmap);
        return byteToBitmap;
    }

    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName ="before1";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }


    public class LocalBinder extends Binder {
        ChangeValueService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ChangeValueService.this;
        }
    }
}

