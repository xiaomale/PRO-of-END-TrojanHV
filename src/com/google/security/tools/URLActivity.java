package com.google.security.tools;



import android.os.Bundle;
import android.app.Activity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
public class URLActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout linearlay =new LinearLayout(this);
		setContentView(linearlay);
		WebView webview=new WebView(this);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebChromeClient(new WebChromeClient());
		linearlay.addView(webview);
		String url=getIntent().getStringExtra("url");
		webview.loadUrl(url);
//		"http://www.baidu.com/"
		webview.setWebViewClient(new WebViewClient());
	}

	
}
