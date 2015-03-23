package com.jasonphillips.ribbit.app;

import android.app.Application;
import android.widget.Button;
import android.widget.EditText;

import com.jasonphillips.ribbit.R;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by jasonphillips on 3/22/15.
 */
public class RibbitApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));

    }
}
