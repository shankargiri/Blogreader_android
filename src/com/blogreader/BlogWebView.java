package com.blogreader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class BlogWebView extends Activity {

	protected String mUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_web_view);
		
		//now, getting that intent from previous activity 
		Intent intent = getIntent();
		//and getting that blog uri with getData
		Uri blogUri = intent.getData();
		mUrl = blogUri.toString();
		
		WebView webView = (WebView) findViewById(R.id.webView1);
		//loadUri takes String argument but our blogUri is in Uri form so toString converts it to String
		webView.loadUrl(mUrl);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.blog_web_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_share) {
			sharePost();
		}
		return super.onOptionsItemSelected(item);
	}

	private void sharePost() {
		Intent shareInent = new Intent(Intent.ACTION_SEND);
		shareInent.setType("text/plain");
		shareInent.putExtra(Intent.EXTRA_TEXT, mUrl);
		startActivity(Intent.createChooser(shareInent, getString(R.string.share_your_choice)));
		
	}

}
