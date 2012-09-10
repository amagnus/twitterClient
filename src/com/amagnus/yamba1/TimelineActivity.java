package com.amagnus.yamba1;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends Activity {

	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	TimelineAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline_basic);
		
		// Find your views
		listTimeline = (ListView) findViewById(R.id.listTimeline);
		
		// Connect to database
		dbHelper = new DbHelper(this); //
		db = dbHelper.getReadableDatabase(); //
		
		// Check whether preferences have been set
		if (yamba1.getPrefs().getString("username", null) == null) {
			startActivity(new Intent(this, PrefsActivity.class));
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// Close the database
		db.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Get the data from the database
		cursor = db.query(DbHelper.TABLE, null, null, null, null, null, DbHelper.C_CREATED_AT + " DESC");
		startManagingCursor(cursor);
	
		// Create the adapter
		adapter = new TimelineAdapter(this, cursor);
		listTimeline.setAdapter(adapter);
		adapter.setViewBinder(VIEW_BINDER);
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
