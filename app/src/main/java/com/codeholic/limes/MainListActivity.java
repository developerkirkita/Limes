package com.codeholic.limes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.codeholic.limes.adapter.Adapter;
import com.codeholic.limes.helper.ConnectionDetector;
import com.codeholic.limes.ui.RegisterActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainListActivity extends ListActivity {

    public static final int NUMBER_OF_POSTS = 50;
    public static final String TAG = MainListActivity.class.getSimpleName();
    protected JSONObject mBlogData;
    protected ProgressBar mProgressBar;

    private final String KEY_TITLE = "title";
    private final String KEY_AUTHOR = "author";
    private final String KEY_DATE = "date";
    private final String KEY_THUMBNAIL = "thumbnail";

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    Adapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        //Intent i = new Intent(this, RegisterActivity.class);
        //startActivity(i);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

        listView = (ListView) findViewById(android.R.id.list);

        if (isInternetPresent) {
            mProgressBar.setVisibility(View.VISIBLE);
            GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
            getBlogPostsTask.execute();
        }
        else {
            Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG).show();
        }

        //Toast.makeText(this, getString(R.string.no_items), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        try {
            JSONArray jsonPosts = mBlogData.getJSONArray("posts");
            JSONObject jsonPost = jsonPosts.getJSONObject(position);
            String blogUrl = jsonPost.getString("url");

            Intent intent = new Intent(this, BlogWebViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setData(Uri.parse(blogUrl));
            startActivity(intent);
        }
        catch (JSONException e) {
            logException(e);
        }
    }

    private void logException(Exception e) {
        Log.e(TAG, "Exception caught!", e);
    }

    public void handleBlogResponse() {
        mProgressBar.setVisibility(View.INVISIBLE);

        if (mBlogData == null) {
            updateDisplayForError();
        }
        else {
            try {
                JSONArray jsonPosts = mBlogData.getJSONArray("posts");
                ArrayList<HashMap<String, String>> blogPosts =
                        new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < jsonPosts.length(); i++) {
                    JSONObject post = jsonPosts.getJSONObject(i);
                    String title = post.getString(KEY_TITLE);
                    title = Html.fromHtml(title).toString();
                    String author = post.getString(KEY_AUTHOR);
                    author = Html.fromHtml(author).toString();
                    String date = post.getString(KEY_DATE);
                    date = Html.fromHtml(date).toString();
                    String thumbnail = post.getString(KEY_THUMBNAIL);
                    thumbnail = Html.fromHtml(thumbnail).toString();



                    //Log.e("Thumbnail: ", thumbnail);

                    HashMap<String, String> blogPost = new HashMap<String, String>();
                    blogPost.put(KEY_TITLE, title);
                    blogPost.put(KEY_AUTHOR, author);
                    blogPost.put(KEY_DATE, date);

                    blogPosts.add(blogPost);
                }

                Log.e("Adapter: ", "HERE");
                adapter = new Adapter(MainListActivity.this, blogPosts);
                listView.setAdapter(adapter);

                //String[] keys = { KEY_TITLE, KEY_AUTHOR };
                //int[] ids = { android.R.id.text1, android.R.id.text2 };
                //SimpleAdapter adapter = new SimpleAdapter(this, blogPosts,
                        //android.R.layout.simple_list_item_2,
                        //keys, ids);

                //setListAdapter(adapter);


            }
            catch (JSONException e) {
                logException(e);
            }
        }
    }

    private void updateDisplayForError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error_title));
        builder.setMessage(getString(R.string.error_message));
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();

        TextView emptyTextView = (TextView) getListView().getEmptyView();
        emptyTextView.setText(getString(R.string.no_items));
    }

    private class GetBlogPostsTask extends AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... arg0) {
            int responseCode = -1;
            JSONObject jsonResponse = null;

            try {
                URL blogFeedUrl = new URL("link goes here" + NUMBER_OF_POSTS);
                HttpURLConnection connection = (HttpURLConnection) blogFeedUrl.openConnection();
                connection.connect();

                responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    Reader reader = new InputStreamReader(inputStream);
                    int contentLength = connection.getContentLength();
                    char[] charArray = new char[contentLength];
                    reader.read(charArray);
                    String responseData = new String(charArray);

                    jsonResponse = new JSONObject(responseData);
                }
                else {
                    Log.i(TAG, "Unsuccessful HTTP Response Code: " + responseCode);
                }
            }
            catch (MalformedURLException e) {
                logException(e);
            }
            catch (IOException e) {
                logException(e);
            }
            catch (Exception e) {
                logException(e);
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mBlogData = result;
            handleBlogResponse();
        }

    }

}
