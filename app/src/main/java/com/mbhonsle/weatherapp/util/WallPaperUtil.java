package com.mbhonsle.weatherapp.util;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by mak on 12/17/17.
 */

public class WallPaperUtil {

    private Map<String, ImageHelper> imageUrlMap = new TreeMap<>();
    private WallpaperManager wallpaperManager;
    private Context context;
    private String defaultImagePath = "../../res/drawable-xxhdpi/default_image.jpeg";

    public WallPaperUtil(Context context) {
        this.context = context;
        this.wallpaperManager = WallpaperManager.getInstance(context);
        initializeImageMap();
    }

    private void initializeImageMap() {
        this.imageUrlMap.put("sunny", new ImageHelper("https://images.unsplash.com/photo-1465513788702-c32450dfaad6?auto=format&fit=crop&w=3668&q=80&ixid=dW5zcGxhc2guY29tOzs7Ozs%3D", "sunny"));
        this.imageUrlMap.put("sun", new ImageHelper("https://images.unsplash.com/photo-1465513788702-c32450dfaad6?auto=format&fit=crop&w=3668&q=80&ixid=dW5zcGxhc2guY29tOzs7Ozs%3D", "sun"));
        this.imageUrlMap.put("smoke", new ImageHelper("https://images.unsplash.com/photo-1503198515498-d0bd9ed16902?auto=format&fit=crop&w=934&q=80&ixid=dW5zcGxhc2guY29tOzs7Ozs%3D", "smoke"));
        this.imageUrlMap.put("haze", new ImageHelper("https://images.unsplash.com/photo-1470010553688-8e1aa13f066b?auto=format&fit=crop&w=3437&q=80&ixid=dW5zcGxhc2guY29tOzs7Ozs%3D", "haze"));
        this.imageUrlMap.put("clear", new ImageHelper("https://images.unsplash.com/photo-1495333258329-fe29ba3a7b4b?auto=format&fit=crop&w=3334&q=80&ixid=dW5zcGxhc2guY29tOzs7Ozs%3D", "clear"));
        this.imageUrlMap.put("rain", new ImageHelper("https://images.unsplash.com/photo-1508897855424-c34724f095cb?auto=format&fit=crop&w=3334&q=80&ixid=dW5zcGxhc2guY29tOzs7Ozs%3D", "rain"));
        this.imageUrlMap.put("drizzle", new ImageHelper("https://images.unsplash.com/photo-1498793071176-2c542bef8a93?auto=format&fit=crop&w=2134&q=80&ixid=dW5zcGxhc2guY29tOzs7Ozs%3D", "drizzle"));
        this.imageUrlMap.put("thunderstorm", new ImageHelper("https://images.unsplash.com/photo-1510300101842-d7a2ed0bde3b?auto=format&fit=crop&w=3399&q=80&ixid=dW5zcGxhc2guY29tOzs7Ozs%3D", "thunderstorm"));
        this.imageUrlMap.put("snow", new ImageHelper("https://images.unsplash.com/photo-1452842773071-825bd0110dbd?auto=format&fit=crop&w=3324&q=80&ixid=dW5zcGxhc2guY29tOzs7Ozs%3D", "snow"));
        this.imageUrlMap.put("mist", new ImageHelper("https://images.unsplash.com/photo-1502580969434-4550a9e56735?auto=format&fit=crop&w=3334&q=80&ixid=dW5zcGxhc2guY29tOzs7Ozs%3D", "mist"));
        this.imageUrlMap.put("fog", new ImageHelper("https://images.unsplash.com/photo-1502580969434-4550a9e56735?auto=format&fit=crop&w=3334&q=80&ixid=dW5zcGxhc2guY29tOzs7Ozs%3D", "fog"));
        this.imageUrlMap.put("cloud", new ImageHelper("https://images.unsplash.com/photo-1506280387734-badea65976d5?auto=format&fit=crop&w=1950&q=80&ixid=dW5zcGxhc2guY29tOzs7Ozs%3D", "cloud"));
        this.imageUrlMap.put("clouds", new ImageHelper("https://images.unsplash.com/photo-1506280387734-badea65976d5?auto=format&fit=crop&w=1950&q=80&ixid=dW5zcGxhc2guY29tOzs7Ozs%3D", "clouds"));
        this.imageUrlMap.put("default", new ImageHelper("https://images.unsplash.com/photo-1508325732378-00eafff6c504?auto=format&fit=crop&w=2105&q=80&ixid=dW5zcGxhc2guY29tOzs7Ozs%3D", "default"));
    }

    public void setWallpaper(String weatherString) {
        weatherString = weatherString.toLowerCase();
        Log.i("WEATHER STRING IS: ", weatherString);
        try {
            if (this.imageUrlMap.get(weatherString) == null) {
                this.wallpaperManager.setBitmap(getImage(this.imageUrlMap.get("default")));
            } else {
                this.wallpaperManager.setBitmap(getImage(this.imageUrlMap.get(weatherString)));
            }
        } catch (Exception e) {
            Log.e("FAILED TO SET WALLPAPER", e.getMessage());
        }
    }

    private Bitmap getImage(ImageHelper imageHelper) {
        try {
            return imageHelper.fetch();
        } catch (Exception e) {
            Log.e("IMAGE DOWNLOAD ERROR", e.getMessage());
        }

        return getDefaultImage();
    }

    private Bitmap getDefaultImage() {
        AssetManager assetManager = this.context.getAssets();
        InputStream inputStream;
        Bitmap bitmap = null;
        try {
            inputStream = assetManager.open(this.defaultImagePath);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e("FAILED TO GET DEFAULT IMAGE", e.getMessage());
        }
        return bitmap;
    }
}
