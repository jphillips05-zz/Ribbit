package com.jasonphillips.ribbit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.jasonphillips.ribbit.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.security.PublicKey;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class LoginActivity extends Activity {

    protected TextView mSignupTextView;

    @InjectView(R.id.username_text_view)
    EditText mUsername;
    @InjectView(R.id.password_text_view)
    EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);

    }

    @OnClick(R.id.sign_up_text_view)
    public void sendUserToSignup(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_button)
    public void login(View view) {
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();


        if (username.isEmpty() || password.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(R.string.login_error_message)
                    .setTitle(R.string.error_title)
                    .setPositiveButton(android.R.string.ok, null);

            builder.create().show();

        } else {
            setProgressBarIndeterminateVisibility(true);
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    setProgressBarIndeterminateVisibility(false);
                    if (parseUser != null) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(getString(R.string.error_invalid_credentials))
                                .setTitle(R.string.error_title)
                                .setPositiveButton(getString(R.string.btn_ok), null);

                        builder.create().show();
                    }
                }
            });
        }
    }
}
