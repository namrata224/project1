package com.sunbeam.messenger.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sunbeam.messenger.R;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static com.sunbeam.messenger.utility.Constants.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class LoginActivity extends AppCompatActivity {

    EditText editLoginName, editPasword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle(getString(R.string.login));

        editLoginName = (EditText) findViewById(R.id.editLoginName);
        editPasword = (EditText) findViewById(R.id.editPassword);
    }

    public void loginUser(View v) {
        if (editLoginName.getText().toString().length() == 0) {
            Toast.makeText(this, "Please enter user name", Toast.LENGTH_SHORT).show();
        } else if (editPasword.getText().toString().length() == 0) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else {
            //http://localhost:8080/Messenger/LoginServlet?LoginName=user2&Password=test
            String url = URL + SERVLET_LOGIN + "?LoginName=" + editLoginName.getText().toString() + "&Password=" + editPasword.getText().toString();
            new LoginTask().execute(url);
        }
    }

    public void signupUser(View v) {
        startActivity(new Intent(this, SignupActivity.class));
    }


    class LoginTask extends AsyncTask<String, Void, InputStream> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
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

            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                parser.parse(inputStream, new LoginXMLHandler());
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
            try {
                InputStreamReader reader = new InputStreamReader(inputStream);

                StringBuilder builder = new StringBuilder();
                int ch = 0;
                while ((ch = reader.read()) != -1) {
                    builder.append((char)ch);
                }

                String result = builder.toString();

                if (Integer.parseInt(result) == -1) {
                    Toast.makeText(LoginActivity.this, "Invalid user name or password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Welcome User", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            */

        }
    }

    class LoginXMLHandler extends DefaultHandler {
        String text, status, userId, fullName;

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            text = new String(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (localName.equals("status")) {
                status = text;
            } else if (localName.equals("userId")) {
                userId = text;
            } else if (localName.equals("fullName")) {
                fullName = text;
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();

            if (status.equals("0")) {
                Toast.makeText(LoginActivity.this, "Welcome User " + fullName, Toast.LENGTH_SHORT).show();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                preferences.edit()
                        .putBoolean(KEY_LOGIN_STATUS, true)
                        .putString(KEY_LOGIN_NAME, fullName)
                        .putInt(KEY_LOGIN_ID, Integer.parseInt(userId))
                        .commit();

                startActivity(new Intent(LoginActivity.this, FriendsListActivity.class));
                finish();
            } else if (status.equals("-1")) {
                Toast.makeText(LoginActivity.this, "Invalid user name or password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
