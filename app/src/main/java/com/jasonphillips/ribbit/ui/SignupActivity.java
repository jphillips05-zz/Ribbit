package com.jasonphillips.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.jasonphillips.ribbit.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class SignupActivity extends ActionBarActivity {

    @InjectView(R.id.username_text_view)
    EditText mUsername;
    @InjectView(R.id.password_text_view)
    EditText mPassword;
    @InjectView(R.id.email_text_view)
    EditText mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();

        ButterKnife.inject(this);

    }

    @OnClick(R.id.sign_up_cancel)
    public void cancel(View view) {
        finish();
    }

    @OnClick(R.id.sign_up_button)
    public void signUpClick(View view) {
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String email = mEmail.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
            builder.setMessage(R.string.signup_error_message)
                    .setTitle(R.string.error_title)
                    .setPositiveButton(android.R.string.ok, null);

            builder.create().show();

        } else {
            setProgressBarIndeterminateVisibility(true);

            ParseUser newUser = new ParseUser();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);

            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    setProgressBarIndeterminateVisibility(false);
                    if (e == null) {
                        //success
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        //error
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                        builder.setMessage(e.getMessage())
                                .setTitle(R.string.error_title)
                                .setPositiveButton(android.R.string.ok, null);

                        builder.create().show();
                    }
                }
            });
        }
    }
}
