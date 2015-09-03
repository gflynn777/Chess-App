package com.liflynn.chessgame;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import com.liflynn.chess.Game;
import com.liflynn.chess.Moves;
import com.liflynn.piece.GamePiece;
import com.liflynn.util.Util;

/**
 * @author Greg Flynn
 * @author Bohan Li
 *
 */


public class Chess extends Activity implements OnItemClickListener, OnClickListener, ChessInterface{
	public static final int PIECE_HEIGHT = 100;
	public static final int PIECE_WIDTH = 100;
	public static final int PROMO_CODE = 0;
	public static final int STORED_GAMES_CODE = 1;
	public static final int SAVE_REPLAY_CODE = 2;
	public static final String PROMO = "promo";
	public static final String STORED_GAME = "storedGame";
	public static final String REPLAY_DATA = "replays.dat";
	public static final String EXISTING_REPLAY_NAMES = "existingReplayNames";
	public static final String REPLAY_NAME = "replayName";
	private boolean firstClick = true;
	private int toggledPos = -1;
	private View toggledView = null;
	private Game game = new Game(this);
	private ImageView[] grid = new ImageView[64];
	private byte gridCount = 0;
	public Button undoButton;
	private boolean selectedSquare = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    
	    GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new ImageAdapter(this));
	    gridview.setOnItemClickListener(this);
	    undoButton = (Button) findViewById(R.id.undoButton);
	    undoButton.setOnClickListener(this);
	    undoButton.setEnabled(false);
	    Button aiButton = (Button) findViewById(R.id.ai_button);
	    aiButton.setOnClickListener(this);
	    Button drawButton = (Button) findViewById(R.id.draw_button);
	    drawButton.setOnClickListener(this);
	    Button resignButton = (Button) findViewById(R.id.resign_button);
	    resignButton.setOnClickListener(this);
	    Button storeButton = (Button) findViewById(R.id.stored_button);
	    storeButton.setOnClickListener(this);
	    
	    
		// register for context menu
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

		        Display display = getWindowManager().getDefaultDisplay();
		        Point size = new Point();
		        display.getSize(size);
		        int width = size.x;
	            //If it's not recycled, initialize some attributes
	            view.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, (width-10)/8));
	            view.setPadding(1, 1, 1, 1);
		        
		        //Set the Backgrounds
	            background = (ImageView)view.findViewById(R.id.square_background);
		        background.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            
		        if (isWhite(position))
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
    
    public static boolean isWhite(int position){
    	int row = position / 8;
    	int col = position % 8;
    	return (row + col) % 2 == 0;
    }
    
	public void move (int from, int to)
	{
		Log.d("Move", from + " " + to);
		grid[to].setImageDrawable(grid[from].getDrawable());
		grid[from].setImageDrawable(null);
	}
	
	public void remove (int pos)
	{
		grid[pos].setImageDrawable(null);//
	}
	
	public void syncChess()
	{
		for (int i = 0; i < grid.length; i++)
		{
			byte[] pos = Util.convertPosition(i);
			GamePiece p = game.getSpaceAtPosition(pos).getPiece();
			Bitmap shouldBe = p == null ? null : getBitmap(getResources(), p.toString(), PIECE_HEIGHT, PIECE_WIDTH);
			//Compare bitmaps (Avoiding NullPointerExceptions)
			if (shouldBe == null || grid[i].getDrawable() == null || ((BitmapDrawable)grid[i].getDrawable()).getBitmap() == null ||
					!((BitmapDrawable)grid[i].getDrawable()).getBitmap().sameAs(shouldBe)){
				grid[i].setImageBitmap(shouldBe);
			}
		}		
	}
	
	public void startPromoActivity(){
		Intent i = new Intent(getApplicationContext(), PromotionPopup.class);
		startActivityForResult(i, PROMO_CODE);
	}
	
	public void askForSave(String winner, int icon){
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setIcon(icon)
		.setTitle(winner)
		.setMessage("Would you like to save this game for future playback?")
	    .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				saveReplay(null);
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){
				recreate();
			}
		})
		.show();
	}
	
	public void setUndo(boolean value){
		undoButton.setEnabled(value);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		if (firstClick){
			toggledView = view;
			//Check to make sure its an actual piece
			ImageView piece = (ImageView)view.findViewById(R.id.piece);
			if(piece.getDrawable() == null || !game.validFrom(Util.convertPosition(position)))
				return;
			
			ImageView background = (ImageView)toggledView.findViewById(R.id.square_background);
			background.setImageResource(R.layout.selected_square);
			selectedSquare = true;
			toggledPos = position;
			firstClick = false;
		}
		else{
			firstClick = true;
			setSquareBack();
			
			//Perform move
			if(toggledPos == position)
				return;
			game.move(Util.convertPosition(toggledPos), Util.convertPosition(position), true, false);
		}
	}
	
	//Need to do this only with the just clicked square
	public void setSquareBack(){
		if (toggledPos != -1 && selectedSquare){
			ImageView background = (ImageView)toggledView.findViewById(R.id.square_background);
			if (isWhite(toggledPos))
				background.setImageResource(R.layout.white_square);
			else
				background.setImageResource(R.layout.black_square);
			selectedSquare = false;
		}
	}
	
	private ArrayList<String> getReplayNames()
	{
		HashMap<String, Moves> map = null;
		try {
			FileInputStream fis = openFileInput(REPLAY_DATA);
			ObjectInputStream is = new ObjectInputStream(fis);
			map = (HashMap<String, Moves>) is.readObject();
			is.close();
			fis.close();
		} catch(Exception e) {
			e.printStackTrace();
			Log.e("Replay", "unable to open");
		}
		if (map == null)
			return null;
		return new ArrayList<String>(map.keySet());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.undoButton:
			if (game.undo()){
				syncChess();
				undoButton.setEnabled(false);
				setSquareBack();
			}
			else
				Toast.makeText(getApplicationContext(), "Can't undo", Toast.LENGTH_SHORT).show();
			return;
			
		case R.id.ai_button:
			setSquareBack();
			game.performAiMove();
			syncChess();
			return;
			
		case R.id.draw_button:
			AlertDialog.Builder ab = new AlertDialog.Builder(v.getContext());
			if (game.getWhoseTurn() == GamePiece.WHITE)
				ab.setIcon(R.drawable.wknight);
			else
				ab.setIcon(R.drawable.bknight);
			ab.setTitle("Player Requests Draw")
			.setMessage("Do you accept?")
			.setNegativeButton("Deny", null)
			.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					game.endGame(Game.DRAW, false);
				}
			});
			ab.show();
			return;
			
		case R.id.resign_button:
			game.endGame(game.notWhoseTurn(), true);
			return;
			
		case R.id.stored_button: 
			Intent i = new Intent(getApplicationContext(), StoredGames.class);
			this.startActivityForResult(i, STORED_GAMES_CODE);
			return;
		}
	}
	
	public void saveReplay(View view) {
		Intent intent = new Intent(this, SaveReplay.class);
		Bundle extras = new Bundle();
		extras.putStringArrayList(EXISTING_REPLAY_NAMES, getReplayNames());
		intent.putExtras(extras);
		startActivityForResult(intent, SAVE_REPLAY_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == PROMO_CODE){
			if (resultCode != Activity.RESULT_OK)
				return;
			
			Bundle bundle = intent.getExtras();
			if (bundle == null)
				return;
			String chosenPromo = bundle.getString(PROMO);
			game.performPromotion(chosenPromo);
		}
		
		else if (requestCode == STORED_GAMES_CODE){
			if (resultCode != Activity.RESULT_OK)
				return;
			Bundle bundle = intent.getExtras();
			if (bundle == null)
				return;
			String storedGame = bundle.getString(STORED_GAME);
			Intent i = new Intent(this, ReplayChess.class);
			Bundle extras = new Bundle();
			extras.putString(REPLAY_NAME, storedGame);
			i.putExtras(extras);
			this.startActivity(i);
		}
		
		else if (requestCode == SAVE_REPLAY_CODE){
			if (resultCode != RESULT_OK)
			{
				if (game.isGameOver())
					recreate();
				return;
			}
	
			Bundle bundle = intent.getExtras();
			if (bundle == null)
				return;
			
			HashMap<String, Moves> map = null;
			try {
				FileInputStream fis = openFileInput(REPLAY_DATA);
				ObjectInputStream is = new ObjectInputStream(fis);
				map = (HashMap<String, Moves>) is.readObject();
				is.close();
				fis.close();
			} catch(Exception e) {
				e.printStackTrace();
				Log.e("Replay", "unable to open");
			}
			if (map == null)
				map = new HashMap<String, Moves>();
	
			String name = bundle.getString(REPLAY_NAME);
			Log.d("replay name", name);
			game.moves.name = name;
			map.put(name, game.moves);
			
			try {
				FileOutputStream fos = openFileOutput(REPLAY_DATA, Context.MODE_PRIVATE);
				ObjectOutputStream os = new ObjectOutputStream(fos);
				os.writeObject(map);
				os.close();
				fos.close();
			} catch(Exception e) {
				e.printStackTrace();
				Log.e("Replay", "unable to save");
			}
			//Start new game after save
			recreate();
		}
	}

	@Override
	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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