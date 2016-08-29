package com.sunbeam.messenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunbeam.messenger.R;
import com.sunbeam.messenger.model.Message;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static com.sunbeam.messenger.utility.Constants.KEY_LOGIN_ID;
import static com.sunbeam.messenger.utility.Constants.KEY_LOGIN_NAME;
import static com.sunbeam.messenger.utility.Constants.SERVLET_FRIENDS_LIST;
import static com.sunbeam.messenger.utility.Constants.SERVLET_MESSAGE_LIST;
import static com.sunbeam.messenger.utility.Constants.SERVLET_SEND_MESSAGE;

public class ChatActivity extends AppCompatActivity {

    class MessageAdapter extends ArrayAdapter<Message> {

        private final Context context;
        private final ArrayList<Message> messages;

        public MessageAdapter(Context context, ArrayList<Message> messages) {
            super(context, android.R.layout.simple_list_item_1);
            this.context = context;
            this.messages = messages;
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.list_item_message, null);

            TextView textMessage = (TextView) layout.findViewById(R.id.textMessage);
            TextView textUserName = (TextView) layout.findViewById(R.id.textUserName);

            Message message = messages.get(position);
            textMessage.setText(message.getContents());

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);
            int sender = preferences.getInt(KEY_LOGIN_ID, 0);
            String userName = preferences.getString(KEY_LOGIN_NAME, "");

            if (message.getSenderId() == sender) {
                textUserName.setText(userName);
                textMessage.setGravity(Gravity.LEFT);
                textUserName.setGravity(Gravity.LEFT);
                textMessage.setTextColor(Color.RED);
            } else {
                textUserName.setText(getIntent().getStringExtra("FULL_NAME"));
                textMessage.setGravity(Gravity.RIGHT);
                textUserName.setGravity(Gravity.RIGHT);
                textMessage.setTextColor(Color.BLUE);
            }

            return layout;
        }
    }

    ArrayList<Message> messages = new ArrayList<>();
    MessageAdapter adapter;
    ListView listView;

    EditText editMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        listView = (ListView) findViewById(R.id.listView);
        adapter = new MessageAdapter(this, messages);
        listView.setAdapter(adapter);

        editMessage = (EditText) findViewById(R.id.editMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuRefresh) {
            new FetchMessagesTask().execute();
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchMessagesTask().execute();
    }

    public void sendMessage(View v) {
        if (editMessage.getText().toString().length() == 0) {
            Toast.makeText(this, "enter message", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);
            int sender = preferences.getInt(KEY_LOGIN_ID, 0);
            int receiver = getIntent().getIntExtra("USER_ID", 0);

            String url = Constants.URL + SERVLET_SEND_MESSAGE
                    + "?Contents=" + editMessage.getText().toString().replace(" ", "%20")
                    + "&Sender=" + sender
                    + "&Receiver=" + receiver;
            new SendMessageTask().execute(url);

            editMessage.setText("");
        }
    }

    class SendMessageTask extends AsyncTask<String, Void, InputStream> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ChatActivity.this);
            dialog.setTitle("Please wait..");
            dialog.setMessage("Please wait.. sending messages");
            dialog.show();
        }

        @Override
        protected InputStream doInBackground(String... urls) {
            try {

                URL url = new URL(urls[0]);
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

            new FetchMessagesTask().execute();
        }
    }

    class FetchMessagesTask extends AsyncTask<Void, Void, InputStream> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ChatActivity.this);
            dialog.setTitle("Please wait..");
            dialog.setMessage("Please wait.. fetching messages");
            dialog.show();
        }

        @Override
        protected InputStream doInBackground(Void... urls) {
            try {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);
                int sender = preferences.getInt(KEY_LOGIN_ID, 0);
                int receiver = getIntent().getIntExtra("USER_ID", 0);

                URL url = new URL(Constants.URL + SERVLET_MESSAGE_LIST + "?Sender=" + sender + "&Receiver=" + receiver);
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
                parser.parse(inputStream, new MessageXMLHandler());
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class MessageXMLHandler extends DefaultHandler {
        Message tempMessage;
        String text;

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            messages.clear();
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            adapter.notifyDataSetChanged();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (localName.equals("Message")) {
                tempMessage = new Message();
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (localName.equals("MessageId")) {
                tempMessage.setMessageId(Integer.parseInt(text));
            } else if (localName.equals("Contents")) {
                tempMessage.setContents(text);
            } else if (localName.equals("SenderId")) {
                tempMessage.setSenderId(Integer.parseInt(text));
            } else if (localName.equals("Receiver")) {
                tempMessage.setReceiverId(Integer.parseInt(text));
            } else if (localName.equals("Message")) {
                messages.add(tempMessage);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            text = new String(ch, start, length);
        }
    }
}
