package com.example.stickablelistview;

import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {

	private StickListViewV2 stickListView;
	private StickListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		stickListView = (StickListViewV2) findViewById(R.id.listview);
		adapter = new StickListAdapter(this);
		stickListView.setAdapter(adapter);
	}

}
