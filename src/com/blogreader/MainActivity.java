package com.blogreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	protected String[] mBlogTitles;
	public static final String TAG = MainActivity.class.getSimpleName();
	
	public final int NUMBER_OF_POST = 20;
	protected static JSONObject mBlogData;
	
	ListView listView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 
		if(isNetworkAvaillable()){
			listView = (ListView) findViewById(R.id.listView);
			System.out.printf("helo3");
			
			GetBlogPostTask getBlogPostTask = new GetBlogPostTask();
			getBlogPostTask.execute();
			System.out.printf("helo5");
			
		}
		else{
			Toast.makeText(this, "Network is unavaillable", Toast.LENGTH_LONG).show();
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
	
	public void updateList(){

		if( mBlogData == null){
			
			
			
		}else{
			try {
				JSONArray jsonPosts = mBlogData.getJSONArray("posts");
				mBlogTitles = new String[jsonPosts.length()];
				for(int i = 0; i<jsonPosts.length(); i++){
					JSONObject posts = jsonPosts.getJSONObject(i);
					String title = posts.getString("title");
					title = Html.fromHtml(title).toString();
					mBlogTitles[i] = title;
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mBlogTitles);
				listView.setAdapter(adapter);
				
				
			} catch (JSONException e) {
				Log.e(TAG, "Exception Caught:", e);
			}catch(Exception e){
				Log.e(TAG, "Exception Caught:", e);
			}
		}
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
			updateList();
		}
	}

}
