package com.blogreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	
	public static final String TAG = MainActivity.class.getSimpleName();
	
	public final int NUMBER_OF_POST = 20;
	protected static JSONObject mBlogData;
	protected ProgressBar mProgressBar;
	public static final String KEY_TITLE = "title";
	public static final String KEY_AUTHOR = "author";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		 
		if(isNetworkAvaillable()){
			mProgressBar.setVisibility(View.VISIBLE);
			
	
			GetBlogPostTask getBlogPostTask = new GetBlogPostTask();
			getBlogPostTask.execute();
	
		}
		else{
			Toast.makeText(this, "Network is unavaillable", Toast.LENGTH_LONG).show();
		}
	
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		try {
			JSONArray jsonPosts = mBlogData.getJSONArray("posts");
			JSONObject jsonPost = jsonPosts.getJSONObject(position);
			String blogURL = jsonPost.getString("url");
			//opening in new action in webview
			Intent intent = new Intent(this, BlogWebView.class);
			//this is for opening in new web
//			Intent intent = new Intent(Intent.ACTION_VIEW);
			//parsing sting blogURL to Uri..
			intent.setData(Uri.parse(blogURL));
			startActivity(intent);
		} catch (JSONException e) {
			Log.e(TAG, "Error Caught: ", e);
		}
		
	}
	public boolean isNetworkAvaillable(){
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if(netInfo != null && netInfo.isConnected()){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void handleBlogResponse(){
		mProgressBar.setVisibility(View.INVISIBLE);

		if( mBlogData == null){
			updateDisplayError();
			
		}else{
			try {
				JSONArray jsonPosts = mBlogData.getJSONArray("posts");
				ArrayList<HashMap<String, String>> blogPosts = new ArrayList<HashMap<String, String>>();
				for(int i = 0; i<jsonPosts.length(); i++){
					JSONObject posts = jsonPosts.getJSONObject(i);
					String title = posts.getString(KEY_TITLE);
					title = Html.fromHtml(title).toString();
					String author = posts.getString(KEY_AUTHOR);
					author = Html.fromHtml(author).toString();
				
					HashMap<String, String> blogPost = new HashMap<String, String>();
					blogPost.put(KEY_TITLE, title);
					blogPost.put(KEY_AUTHOR, author);
					
					blogPosts.add(blogPost);
				}
				String[] keys = {KEY_TITLE, KEY_AUTHOR};
				int[] ids = {android.R.id.text1, android.R.id.text2 };
				
				SimpleAdapter adapter = new SimpleAdapter(this, blogPosts, 
						android.R.layout.simple_list_item_2, keys, ids);
				
				setListAdapter(adapter);
				
				
			} catch (JSONException e) {
				Log.e(TAG, "Exception Caught:", e);
			}catch(Exception e){
				Log.e(TAG, "Exception Caught:", e);
			}
		}
	}

	public void updateDisplayError() {
		AlertDialog.Builder builder= new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.error_title));
		builder.setMessage(getString(R.string.error_message));
		builder.setPositiveButton(android.R.string.ok, null);
		AlertDialog dialog = builder.create();
		dialog.show();
		
		TextView emptyTextView = (TextView) findViewById(R.id.textView1);
		emptyTextView.setText(getString(R.string.no_text));
	}
	


	private class GetBlogPostTask extends AsyncTask<Object, Void, JSONObject> {
		
		@Override
		protected JSONObject doInBackground(Object... arg0) {
			int reponseCode = -1;
			JSONObject jsonResponse = null; 
			
			try {
				URL blogFeedUrl = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count=" + NUMBER_OF_POST);
				HttpURLConnection connection =  (HttpURLConnection) blogFeedUrl.openConnection();
				connection.connect();
				
				reponseCode = connection.getResponseCode();
				
				if(reponseCode == HttpURLConnection.HTTP_OK){
					//getting input stream object from the connection we just made
					InputStream inputStream = connection.getInputStream();
					//Reader will read from inputstream character by character
					Reader reader = new InputStreamReader(inputStream);
					//now, read the number of character in the string array
					int contentLength = connection.getContentLength();
					//declare array and assign contentLength
					char[] charArray = new char[contentLength];
					//Reader object has read method that reads character from input stream and we store it into charArray
					reader.read(charArray);
					//converting char into string array
					String responseData = new String(charArray);
//					Log.v(TAG, responseData);
					
					jsonResponse = new JSONObject(responseData);
//					String status = jsonResponse.getString("status");
//					
//					JSONArray jsonPosts = jsonResponse.getJSONArray("posts");
//					for(int i = 0; i<jsonPosts.length(); i++){
//						JSONObject jsonPost = jsonPosts.getJSONObject(i);
//						String title = jsonPost.getString("title");
//						Log.v(TAG, "post" + i + ":" + title);
//					}
//					
				}
				else{
					
					Log.i(TAG, "Unsuccesful Response Code: " + reponseCode);
				}
				
			} catch (MalformedURLException e) {
				Log.e(TAG, "Exception Caught:", e);
			} catch (IOException e) {
				Log.e(TAG, "Exception Caught:", e);
			} catch(Exception e){
				Log.e(TAG, "Exception Caught:", e);
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
