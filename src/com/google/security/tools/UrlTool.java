package com.google.security.tools;

import android.content.Context;
import android.content.Intent;


public class UrlTool {

	private static UrlTool UrlTool;
	private static final String LOGTAG = "UrlTool";	
	Context context;

	
	private UrlTool(Context context) {
		this.context = context;
	}

	public static UrlTool getIntance(Context context) {
		if (UrlTool == null)
			return new UrlTool(context);
		return UrlTool;
	}
	public void start(String url) {
		Intent intent = new Intent(context, URLActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("url", url);
		context.startActivity(intent);
	}
}
