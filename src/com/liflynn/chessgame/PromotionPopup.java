package com.liflynn.chessgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PromotionPopup extends Activity {
	private String[] values = {"Queen", "Rook", "Bishop", "Knight"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.promo_list);
		
		ListView list = (ListView) findViewById(R.id.list);
	    list.setAdapter(new ImageAdapter(this, values));
	    list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putString(Chess.PROMO, values[position]);
				
				//Send back to caller
				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
	    	
	    });
	}
	
	public class ImageAdapter extends BaseAdapter{
		Context context;
		public ImageAdapter(Context context, String[] values) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null){
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.promo_item, parent, false);
				TextView textView = (TextView) view.findViewById(R.id.label);
				ImageView imageView = (ImageView) view.findViewById(R.id.icon);
				textView.setText(values[position]);
	
				// Change icon based on name
				String s = values[position];
	
				System.out.println(s);
	
				if (s.equals("Queen")) {
					imageView.setImageResource(R.drawable.bqueen);
				} else if (s.equals("Rook")) {
					imageView.setImageResource(R.drawable.brook);
				} else if (s.equals("Bishop")) {
					imageView.setImageResource(R.drawable.bbishop);
				} else {
					imageView.setImageResource(R.drawable.bknight);
				}
			}
			else
				view = convertView;
			return view;
		}
	}
}