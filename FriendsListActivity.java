package com.sunbeam.messenger.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sunbeam.messenger.R;
import com.sunbeam.messenger.model.User;
import com.sunbeam.messenger.utility.Constants;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static com.sunbeam.messenger.utility.Constants.KEY_LOGIN_ID;
import static com.sunbeam.messenger.utility.Constants.KEY_LOGIN_NAME;
import static com.sunbeam.messenger.utility.Constants.KEY_LOGIN_STATUS;
import static com.sunbeam.messenger.utility.Constants.SERVLET_FRIENDS_LIST;

public class FriendsListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<User> users = new ArrayList<>();
    ListView listView;
    ArrayAdapter<User> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        getSupportActionBar().setTitle("Friends");

        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, users);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new FetchFriendsListTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Logout");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("Logout")) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FriendsListActivity.this);
            preferences.edit()
                    .putBoolean(KEY_LOGIN_STATUS, false)
                    .putString(KEY_LOGIN_NAME, "")
                    .putInt(KEY_LOGIN_ID, -1)
                    .commit();

            startActivity(new Intent(FriendsListActivity.this, LoginActivity.class));
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        User user = users.get(position);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("USER_ID", user.getUserId());
        intent.putExtra("FULL_NAME", user.getFullName());
        startActivity(intent);
    }


    class FetchFriendsListTask extends AsyncTask<Void, Void, InputStream> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(FriendsListActivity.this);
            dialog.setTitle("Please wait..");
            dialog.setMessage("Please wait.. Checking if user exists");
            dialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... urls) {
            try {
                URL url = new URL(Constants.URL + SERVLET_FRIENDS_LIST);
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
                parser.parse(inputStream, new FriendsListXMlHandler());
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class FriendsListXMlHandler extends DefaultHandler {
        User tempUser;
        String text;

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            users.clear();
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            adapter.notifyDataSetChanged();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (localName.equals("User")) {
                tempUser = new User();
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (localName.equals("UserId")) {
                tempUser.setUserId(Integer.parseInt(text));
            } else if (localName.equals("FullName")) {
                tempUser.setFullName(text);
            } else if (localName.equals("Mobile")) {
                tempUser.setMobile(text);
            } else if (localName.equals("User")) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FriendsListActivity.this);
                if (tempUser.getUserId() != preferences.getInt(KEY_LOGIN_ID, 0)) {
                    users.add(tempUser);
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            text = new String(ch, start, length);
        }
    }
}
