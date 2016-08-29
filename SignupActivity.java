package com.sunbeam.messenger.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sunbeam.messenger.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static com.sunbeam.messenger.utility.Constants.*;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    EditText editFullName, editPassword, editLoginName, editMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editFullName = (EditText) findViewById(R.id.editFullName);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editLoginName = (EditText) findViewById(R.id.editLoginName);
        editMobile = (EditText) findViewById(R.id.editMobile);
    }

    public void signupUser(View v) {
        if (editLoginName.getText().toString().length() == 0) {
            Toast.makeText(this, "please enter login name", Toast.LENGTH_SHORT).show();
        } else {

            // http://localhost:8080/Messenger/SignupServlet?FullName=user2%20user2&LoginName=user2&Password=test&Mobile=3324234
            String url = URL + SERVLET_SIGNUP
                    + "?FullName=" + editFullName.getText().toString().replace(" ", "%20")
                    + "&LoginName=" + editLoginName.getText().toString().replace(" ", "%20")
                    + "&Password=" + editPassword.getText().toString().replace(" ", "%20")
                    + "&Mobile=" + editMobile.getText().toString().replace(" ", "%20");
            Log.d(TAG, "url: " + url);

            new SignupTask().execute(url);
        }
    }

    public void cancel(View v) {
        finish();
    }

    class SignupTask extends AsyncTask<String, Void, InputStream> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(SignupActivity.this);
            dialog.setTitle("Please wait..");
            dialog.setMessage("Please wait.. Checking if user exists");
            dialog.show();
        }

        @Override
        protected InputStream doInBackground(String... urls) {
            String strUrl = urls[0];

            try {
                URL url = new URL(strUrl);
                URLConnection connection = url.openConnection();
                InputStream stream = connection.getInputStream();

                return stream;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            dialog.dismiss();

            finish();
        }
    }
}
