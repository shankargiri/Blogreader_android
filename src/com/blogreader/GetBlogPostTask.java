package com.blogreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class GetBlogPostTask extends AsyncTask<Object, Void, JSONObject> {
	
	public final int NUMBER_OF_POST = 20;
	public static final String TAG = MainActivity.class.getSimpleName();
	protected static JSONObject mBlogData;
	
	
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
//				Log.v(TAG, responseData);
				
				jsonResponse = new JSONObject(responseData);
//				String status = jsonResponse.getString("status");
//				
//				JSONArray jsonPosts = jsonResponse.getJSONArray("posts");
//				for(int i = 0; i<jsonPosts.length(); i++){
//					JSONObject jsonPost = jsonPosts.getJSONObject(i);
//					String title = jsonPost.getString("title");
//					Log.v(TAG, "post" + i + ":" + title);
//				}
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
		MainActivity mainC = new MainActivity();
			mBlogData = result;
			mainC.handleBlogResponse();
		
		
	}
}
