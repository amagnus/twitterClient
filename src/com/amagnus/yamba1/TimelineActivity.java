package com.amagnus.yamba1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends BaseActivity {

	DbHelper dbHelper;
	Cursor cursor;
	ListView listTimeline;
	TimelineAdapter adapter;
	static final String[] FROM = { DbHelper.C_CREATED_AT, DbHelper.C_USER, DbHelper.C_TEXT };
	static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText };
	private YambaApplication yamba;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline_basic);
		
		// Check whether preferences have been set
		if (yamba.getPrefs().getString("username", null) == null) {
			startActivity(new Intent(this, PrefsActivity.class));
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
		}
		
		// Find your views
		listTimeline = (ListView) findViewById(R.id.listTimeline);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// Close the database
		yamba.getStatusData().close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Setup List
		this.setupList();
	}
	
	// Responsible for fetching data and setting up the list and the adapter
	private void setupList() {
		
		// Get the data
		cursor = yamba.getStatusData().getStatusUpdates();
		startManagingCursor(cursor);
	
		// Setup Adapter
		adapter = (TimelineAdapter) new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);
		listTimeline.setAdapter(adapter);
	}
	
	// View binder constant to inject business logic that converts a timestamp to relative time
	static final ViewBinder VIEW_BINDER = new ViewBinder() {
		
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() != R.id.textCreatedAt)
				return false;
			
			// Update the created at text to relative time
			long timestamp = cursor.getLong(columnIndex);
			CharSequence relTime = DateUtils.getRelativeTimeSpanString(view.getContext(), timestamp);
			((TextView) view).setText(relTime);
			return true;
		}	
	};
	
}
