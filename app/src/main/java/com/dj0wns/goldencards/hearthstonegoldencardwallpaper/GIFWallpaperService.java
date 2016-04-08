package com.dj0wns.goldencards.hearthstonegoldencardwallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dj0wns on 3/18/16.
*/

public class GIFWallpaperService extends WallpaperService{

    String url = "http://wow.zamimg" +
               ".com/images/hearthstone/cards/enus/animated/CS2_141_premium.gif";
    Movie next = null;
    boolean nextFlag = true;
    ArrayList<String> cardList = new ArrayList<String>();
    Random generator = new Random();
    Resources res;
    static long delay;
    static int sleep;
    static int downloadFailDelay;
    int totalSleep = 0;
    long startTime = 0;
    DisplayMetrics metrics = new DisplayMetrics();
    float height, width;
    SharedPreferences prefs;




    @Override
    public WallpaperService.Engine onCreateEngine() {
        prefs = PreferenceManager.getDefaultSharedPreferences
                (getApplicationContext());
        delay = Long.decode(prefs.getString("update_frequency", "120")) * 1000 * 60;
        sleep = 1000/Integer.decode(prefs.getString("framerate", "20"));
        downloadFailDelay = Integer.decode(prefs.getString("fail_delay", "30")) *
                1000;
        res= getResources();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;
        InputStream in = getResources().openRawResource(
                R.raw.cards_collectible);
        try {
            readJsonStream(in);
            Log.d("Cards Read", Integer.toString(cardList.size()));
            Movie movie = Movie.decodeStream(
                    getResources().getAssets().open("cs2.gif"));

            return new GIFWallpaperEngine(movie);
        }catch(IOException e){
            Log.d("GIF", "Could not load asset");
            return null;
        }
    }

    private class GIFWallpaperEngine extends WallpaperService.Engine {

        private final int frameDuration = 20;

        private SurfaceHolder holder;
        private Movie movie;
        private boolean visible;
        private Handler handler;
        private AsyncTask downloadTask;
        private float widthScale, heightScale, scale;

        public GIFWallpaperEngine(Movie movie) {
            this.movie = movie;
            handler = new Handler();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
        }

        private Runnable drawGIF = new Runnable() {
            public void run() {
                draw();
            }
        };


        private void draw() {
            if (visible) {
                try {
                    Thread.sleep(sleep);
                    totalSleep ++;
                } catch (Exception e) {
                    Log.d("Sleep failed",e.toString());
                }
                try {
                    Canvas canvas = holder.lockCanvas();
                    canvas.save();
                    canvas.drawColor(Color.BLACK);
                    widthScale = width / movie.width();
                    heightScale = height / movie.height();
                    scale = widthScale < heightScale ? widthScale : heightScale;
                    canvas.scale(scale, scale);

                    movie.draw(canvas, 0, 0);
                    canvas.restore();
                    holder.unlockCanvasAndPost(canvas);
                    movie.setTime((totalSleep % (movie
                            .duration()/100))*100);

                    handler.removeCallbacks(drawGIF);
                    handler.postDelayed(drawGIF, frameDuration);
                    if (System.currentTimeMillis() - delay > startTime) {
                        if (nextFlag == true && next != null) {
                            movie = next;
                            nextFlag = false;
                            downloadTask = new DownloadGIFTask().execute();
                            startTime = System.currentTimeMillis();
                        } else if (nextFlag == true && next == null) {
                            nextFlag = false;
                            downloadTask = new DownloadGIFTask().execute();
                            startTime += downloadFailDelay; //wait 30 seconds before trying again
                        }
                    }
                } catch (Exception e){
                    Log.e("",e.toString());
                }
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawGIF);
            } else {
                handler.removeCallbacks(drawGIF);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawGIF);
        }
    }

    public class DownloadGIFTask extends AsyncTask<Void, Movie, Void> {


        @Override protected Void doInBackground(Void... urls) {
          try {
              nextFlag = false;
              next = null;
              int card = generator.nextInt() % cardList.size();

              String url = res.getString(R.string.image_url_base) +"enus" + res
                      .getString(R.string.image_url_middle)+ cardList.get(card) + res.getString(R.string.image_url_end);
              Log.d("URL", url);
              InputStream is = (InputStream) new URL(url).getContent();
              next = Movie.decodeStream(is);
              nextFlag = true;
          } catch (Exception e) {
              next = null;
              nextFlag = true;
          }
            return null;
        }
    }

    private void parseJson() {

    }

    public void readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            readMessagesArray(reader);
        }finally{
                reader.close();
        }
    }

    public void readMessagesArray(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readMessage(reader);
        }
        reader.endArray();
    }

    public void readMessage(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                cardList.add(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }
}
