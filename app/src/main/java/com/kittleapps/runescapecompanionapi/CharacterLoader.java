package com.kittleapps.runescapecompanionapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class CharacterLoader {

    public static String CharacterIconBase = "https://secure.runescape.com/m=avatar-rs/$s/chat.png";
    public static String CharacterInfoBase = "http://services.runescape.com/m=website-data/playerDetails.ws?names=[%22$s%22]&callback=jQuery000000000000000_0000000000&_=0";

    public static void Load(String PlayerName){
        if(IsLegitName(PlayerName)) {
            PlayerName = PlayerName.trim();
            String CharacterInfo = CharacterInfoBase.replace("$s", PlayerName);
            MainActivity.browser.loadUrl(CharacterInfo);
        }
        else{
            // Redundant else statement, only here for quality uses.
        }
    }
    public static void SetUsedImage(String PlayerName) {
        if(IsLegitName(PlayerName)){
            // download and set the avatar image.
        new DownloadImageTask(MainActivity.CharacterAvatar)
            .execute(CharacterIconBase.replace("$s", PlayerName.trim().replace(" ", "+")));
        }
        else{

        }
    }
    private static boolean IsLegitName(String name){
        name = name.toLowerCase().replaceAll("_","").replaceAll("-", "").replaceAll(" ", "")
                .replaceAll("a", "").replaceAll("b", "").replaceAll("c", "").replaceAll("d", "")
                .replaceAll("e", "").replaceAll("f", "").replaceAll("g", "").replaceAll("h", "")
                .replaceAll("i", "").replaceAll("j", "").replaceAll("k", "").replaceAll("l", "")
                .replaceAll("m", "").replaceAll("n", "").replaceAll("o", "").replaceAll("p", "")
                .replaceAll("q", "").replaceAll("r", "").replaceAll("s", "").replaceAll("t", "")
                .replaceAll("u", "").replaceAll("v", "").replaceAll("w", "").replaceAll("x", "")
                .replaceAll("y", "").replaceAll("z", "").replaceAll("1", "").replaceAll("2", "")
                .replaceAll("3", "").replaceAll("4", "").replaceAll("5", "").replaceAll("6", "")
                .replaceAll("7", "").replaceAll("8", "").replaceAll("9", "").replaceAll("0", "");
        if (name.length() >= 1){
            // Name had an invalid character, Return false as it's not legitimate
            return false;
        }
        else{
            // Name has no invalid characters, and was deemed legitimate for this check.
            return true;
        }
    }
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        public DownloadImageTask(ImageView bmImage) {
            MainActivity.CharacterAvatar = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            // Set Downloaded image to the Character Icon;
            MainActivity.CharacterAvatar.setImageBitmap(result);
        }
    }
}
