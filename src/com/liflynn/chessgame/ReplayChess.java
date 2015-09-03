package com.liflynn.chessgame;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar.LayoutParams;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.liflynn.chess.Game;
import com.liflynn.chess.Move;
import com.liflynn.chess.Moves;
import com.liflynn.piece.GamePiece;
import com.liflynn.util.Util;

public class ReplayChess extends Activity implements ChessInterface{
	private Moves moves = null;
	private ImageView[] grid = new ImageView[64];
	private byte gridCount = 0;
	private Game game = new Game(this);
	private Iterator<Move> iterator;
	private Move curMove = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.replay_chess);
		
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
		Bundle bundle = getIntent().getExtras();
		if (bundle == null || map == null ||
				(moves = map.get(bundle.getString(Chess.REPLAY_NAME))) == null)
		{
			Log.e("Replay", "Not Found");
			finish();
		}
		iterator = moves.getList().iterator();
		GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new ImageAdapter(this));
		registerForContextMenu(gridview);
	}
	
	public class ImageAdapter extends BaseAdapter {
	    private Context context;

	    public ImageAdapter(Context c) {
	        context = c;
	    }

	    public int getCount() {
	        return 64;
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView background;
	        View view;
	        if (convertView == null) {
	        	
	            //Set LayoutInflator
		        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        view = inflater.inflate(R.layout.square, null);
		        
	            //If it's not recycled, initialize some attributes
		        Display display = getWindowManager().getDefaultDisplay();
		        Point size = new Point();
		        display.getSize(size);
		        int width = size.x;
	            view.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, (width-10)/8));
	            view.setPadding(1, 1, 1, 1);
		        
		        //Set the Backgrounds
	            background = (ImageView)view.findViewById(R.id.square_background);
		        background.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            
		        if (Chess.isWhite(position))
		        	background.setImageResource(R.layout.white_square);
		        else
		        	background.setImageResource(R.layout.black_square);
		        
		        if (grid[position] == null)
		        {
		        	grid[position] = (ImageView)view.findViewById(R.id.piece);
		        	gridCount++;
		        	if (gridCount == grid.length)
		        		syncChess();
		        }
		        	            
	        } else {
	        	view = convertView;
	        }
	        return view;
	    }
	}

	public void exit(View view) {
		finish();
	}
	
	public void next(View view) {
		if (iterator.hasNext())
		{
			curMove = iterator.next();
			game.move(Util.convertPosition(curMove.from), Util.convertPosition(curMove.to), true, false);
		}
		if (!iterator.hasNext())
		{
			Button nextButton = (Button) findViewById(R.id.next_move_button);
			nextButton.setEnabled(false);
		}
	}

	@Override
	public void move(int from, int to) {
		grid[to].setImageDrawable(grid[from].getDrawable());
		grid[from].setImageDrawable(null);
	}

	@Override
	public void remove(int pos) {
		grid[pos].setImageDrawable(null);
	}

	@Override
	public void askForSave(String winner, int icon) {
		// ignore
	}

	@Override
	public void syncChess()
	{
		for (int i = 0; i < grid.length; i++)
		{
			byte[] pos = Util.convertPosition(i);
			GamePiece p = game.getSpaceAtPosition(pos).getPiece();
			Bitmap shouldBe = p == null ? null : getBitmap(getResources(), p.toString(), 100, 100);
			//Compare bitmaps (Avoiding nullPointerExceptions)
			if (shouldBe == null || grid[i].getDrawable() == null || ((BitmapDrawable)grid[i].getDrawable()).getBitmap() == null ||
					!((BitmapDrawable)grid[i].getDrawable()).getBitmap().sameAs(shouldBe)){
				grid[i].setImageBitmap(shouldBe);
			}
		}		
	}

	@Override
	public void startPromoActivity() {
		game.switchColors();
		game.performPromotion(curMove.promo);
		game.switchColors();
	}

	@Override
	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void setUndo(boolean value) {
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
	
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}
	
	public static Bitmap getBitmap(Resources res, String id, int width, int height){
		if (id.equals("bB"))
			return decodeSampledBitmapFromResource(res, R.drawable.bbishop, width, height);
		else if (id.equals("wB"))
			return decodeSampledBitmapFromResource(res, R.drawable.wbishop, width, height);
		else if (id.equals("bK"))
			return decodeSampledBitmapFromResource(res, R.drawable.bking, width, height);
		else if (id.equals("wK"))
			return decodeSampledBitmapFromResource(res, R.drawable.wking, width, height);
		else if (id.equals("bN"))
			return decodeSampledBitmapFromResource(res, R.drawable.bknight, width, height);
		else if (id.equals("wN"))
			return decodeSampledBitmapFromResource(res, R.drawable.wknight, width, height);
		else if (id.equals("bp"))
			return decodeSampledBitmapFromResource(res, R.drawable.bpawn, width, height);
		else if (id.equals("wp"))
			return decodeSampledBitmapFromResource(res, R.drawable.wpawn, width, height);
		else if (id.equals("bQ"))
			return decodeSampledBitmapFromResource(res, R.drawable.bqueen, width, height);
		else if (id.equals("wQ"))
			return decodeSampledBitmapFromResource(res, R.drawable.wqueen, width, height);
		else if (id.equals("bR"))
			return decodeSampledBitmapFromResource(res, R.drawable.brook, width, height);
		else if (id.equals("wR"))
			return decodeSampledBitmapFromResource(res, R.drawable.wrook, width, height);
		else 
			return null;		
	}
}
