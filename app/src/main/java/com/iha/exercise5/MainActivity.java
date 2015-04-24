package com.iha.exercise5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;


public class MainActivity extends Activity {
    ChangeValueService mService;
    EditText mText;
    TextView mTV;
    boolean mBound;
    ServiceMessagesReceiver mReceiver;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTV = (TextView) findViewById(R.id.tv);
        mText = (EditText) findViewById(R.id.editText);
        mImageView = (ImageView) findViewById(R.id.testImage);

        // Bind with ChangeValueService
        Intent intent = new Intent(this, ChangeValueService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        // Register BroadcastReceiver
        mReceiver = new ServiceMessagesReceiver();
        IntentFilter intentFilter = new IntentFilter(getString(R.string.communication));
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public void callSendNotification(View v) {
        mService.sendNotification(mText.getText().toString());
        mService.postImage();
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ChangeValueService.LocalBinder binder = (ChangeValueService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    /**
     * Private broadcastReceive that gets information from Service if camera is interrupted
     * by another application.
     */
    private class ServiceMessagesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(getString(R.string.communication))) {
                // do something
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(openFileInput("before1"));
                    mImageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
