package com.liflynn.chessgame;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SaveReplay extends Activity {
	
	private EditText replayName;
	private ArrayList<String> names = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.save_replay);
		
		replayName = (EditText)findViewById(R.id.replay_name);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
			names = bundle.getStringArrayList(Chess.EXISTING_REPLAY_NAMES);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.save_replay, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void save(View view) {
		
		// gather all data
		String name = replayName.getText().toString();
		
		// name is mandatory
		if (name == null || name.equals("")) {

			Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show();	
			return;   // does not quit activity, just returns from method
		}
		
		if (names != null && names.contains(name))
		{
			Toast.makeText(this, "Duplicate name", Toast.LENGTH_SHORT).show();	
			return;
		}
		
		// make Bundle
		Bundle bundle = new Bundle();
		bundle.putString(Chess.REPLAY_NAME, name);
		
		// send back to caller
		Intent intent = new Intent();
		intent.putExtras(bundle);
		
		setResult(RESULT_OK,intent);
		finish(); 
	}
	
	// called when the user taps the Cancel button
	public void cancel(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}
}
