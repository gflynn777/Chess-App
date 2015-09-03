package com.liflynn.chessgame;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.liflynn.chess.Moves;

public class StoredGames extends Activity {
	ArrayAdapter<Moves> adapter;
	ArrayList<Moves> moves;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stored_games);
		
		ListView list = (ListView)findViewById(R.id.listview);
		HashMap<String, Moves> map = null;
		try {
			FileInputStream fis = openFileInput(Chess.REPLAY_DATA);
			ObjectInputStream is = new ObjectInputStream(fis);
			map = (HashMap<String, Moves>) is.readObject();
			is.close();
			fis.close();
		} catch(Exception e) {
			e.printStackTrace();
			Log.e("Replay", "unable to open");
		}
		if (map != null)
		{
			moves = new ArrayList<Moves>(map.values());
			adapter = new ListAdapter<Moves>(this, 
					android.R.layout.simple_list_item_1, android.R.id.text1, moves);
		} else
			adapter = new ListAdapter<Moves>(this, 
					android.R.layout.simple_list_item_1, android.R.id.text1);
			
		list.setAdapter(adapter);
	    list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putString(Chess.STORED_GAME, adapter.getItem(position).toString());
				Log.d("game name", adapter.getItem(position).toString());
				
				//Send back to caller
				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
	    	
	    });
	}
	
	private class ListAdapter<T> extends ArrayAdapter<T>
	{
		private Context context;
		public ListAdapter(Context context, int resource,
				int textViewResourceId, List<T> objects) {
			super(context, resource, textViewResourceId, objects);
			this.context = context;
		}
		
		public ListAdapter(Context context, int resource,
				int textViewResourceId) {
			super(context, resource, textViewResourceId);
			this.context = context;
		}
		@Override
		 public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(R.layout.game_item, parent, false);

			TextView listTitle = (TextView) row.findViewById(R.id.page_title);
			 listTitle.setText(moves.get(position).name);

			TextView date = (TextView) row.findViewById(R.id.page_date); 
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");
			date.setText(df.format(moves.get(position).time.getTime()));

			return row;
		 }
	}
	
	public void byName(View view) {
		adapter.sort(Moves.SortByName);
	}
	
	public void byDate(View view) {
		adapter.sort(Moves.SortByDate);
	}
}
