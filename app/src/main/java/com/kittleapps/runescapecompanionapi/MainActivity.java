package com.kittleapps.runescapecompanionapi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;

//TODO Make Some URLs (Such as /Community and other possible breaking ones) to Auto-Load About:Blank
//TODO Code-Cleanups... Badly...

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Initializers (with hard-coded strings for now)

    public static WebView browser;
    public static FrameLayout ContentLayout;
    public static TextView DisplayLabel, urlBar;
    public static View ActivityWindow;
    public static ListView lists;
    public static Context CONTEXT;
    public static ArrayAdapter<String> adapter;
    public static List list;
    public static ImageView CharacterAvatar;
    public static boolean LoadingData;
    private static String TempString = "null";
    public static String PleaseWait[] = {"Please Wait While the Data Loads..", "This may possibly take some time.."};
    public static String tempListData[] = {"Welcome to Unofficial Runescape API", "This 3RD party application was created and maintained by Sudo Bash", "Nothing in this 3rd Party app is directly Affiliated with Jagex LTD."};

// Main mechanics to use.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create Basic Application/Context Instances;

        setContentView(R.layout.activity_main);
        CONTEXT = this.getApplicationContext();

        // Create Toolbar Instance

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorOrange));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Create Drawer instance;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Create NavigationView Instance;

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // Android 5.0 Hide System bars (Fullscreen

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

        // Create Basic Layout Instances;

        ContentLayout = (FrameLayout) findViewById(R.id.app_ContentLayout_FrameLayout);
        DisplayLabel = (TextView) findViewById(R.id.app_ContentLabel_textView);
        urlBar = (TextView) findViewById(R.id.app_URLLabel_textView);
        CharacterAvatar = (ImageView) findViewById(R.id.app_ImageView_CharacterAvatar);


        // Create ListView Instance;

        lists = (ListView) findViewById(R.id.app_Lists_ListView);
        list = new LinkedList(asList(tempListData));
        adapter = new ArrayAdapter<>(MainActivity.this, R.layout.custom_listview, list);

        // Load ListView Extra Data;

        lists.setAdapter(adapter);
        lists.setScrollbarFadingEnabled(false);
        lists.setVerticalScrollBarEnabled(true);


        // Create Webview Instance;

        browser = (WebView) findViewById(R.id.app_WebBrowser_WebView);
        browser.clearFormData();
        browser.clearCache(true);
        browser.getContext().setTheme(R.style.WebViewOrange);

        // Load Webview Settings Here;

        WebSettings settings = browser.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);
        settings.setSupportZoom(false);
        settings.setGeolocationEnabled(false);
        settings.setBuiltInZoomControls(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        // Load WebView Extra Data Here

        browser.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                // Webpage finished loading, extract+use data

                final String[] temp = new String[1];
                String page = browser.getUrl().toLowerCase().replace("http://", "").replace("https://", "");
                if (page.startsWith("secure.runescape.com/m=weblogin/loginform?redirto=")) {

                    // Login Page

                    DisplayLabel.setText("Login to Continue.");
                    browser.setVisibility(View.VISIBLE);
                    lists.setVisibility(View.INVISIBLE);
                }
                if (page.startsWith("secure.runescape.com/m=mtxn_rs_shop/api/config?")) {

                    // Solomon's Version information

                    browser.evaluateJavascript(
                            "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {

                                    TempString = html.replace("\\u003Chtml>\\u003Chead>\\u003C/head>\\u003Cbody>\\u003Cpre style=\\\"word-wrap: break-word; white-space: pre-wrap;\\\">", "");
                                    byte ptext[] = TempString.getBytes(ISO_8859_1);
                                    temp[0] = new String(ptext, UTF_8).replaceFirst("\"", "").replace("\\\"", "\"").replace("\\\\", "\\").replace("\\/", "/");
                                    browser.loadUrl("https://secure.runescape.com/m=mtxn_rs_shop/api/shop?version=" + MainParser.getVersion(temp[0]));
                                }
                            });
                }
                if (page.startsWith("secure.runescape.com/m=mtxn_rs_shop/api/shop?version=")) {

                    // Solomons listings+Account payout information

                    browser.evaluateJavascript(
                            "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {

                                    TempString = html.replace("\\u003Chtml>\\u003Chead>\\u003C/head>\\u003Cbody>\\u003Cpre style=\\\"word-wrap: break-word; white-space: pre-wrap;\\\">", "");
                                    byte ptext[] = TempString.getBytes(ISO_8859_1);
                                    temp[0] = new String(ptext, UTF_8).replaceFirst("\"", "").replace("\\\"", "\"").replace("\\\\", "\\").replace("\\/", "/").replace("\\n", System.getProperty("line.separator"))
                                            .replace("{", "{" + System.getProperty("line.separator")).replace("[", "[" + System.getProperty("line.separator")).replace("}", System.getProperty("line.separator") + "}").replace("]", System.getProperty("line.separator") + "]");
                                    MainParser.getListing(temp[0]);
                                }
                            });
                }
                if (page.startsWith("secure.runescape.com/m=mtxn_rs_shop/api/accountinfo")) {

                    // Solomons Membership+Payout Information

                    browser.evaluateJavascript(
                            "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {

                                    TempString = html.replace("\\u003Chtml>\\u003Chead>\\u003C/head>\\u003Cbody>\\u003Cpre style=\\\"word-wrap: break-word; white-space: pre-wrap;\\\">", "");
                                    byte ptext[] = TempString.getBytes(ISO_8859_1);
                                    temp[0] = new String(ptext, UTF_8).replaceFirst("\"", "").replace("\\\"", "\"").replace("\\\\", "\\").replace("\\/", "/").replace("\\n", System.getProperty("line.separator"))
                                            .replace("{", "{" + System.getProperty("line.separator")).replace("[", "[" + System.getProperty("line.separator")).replace("}", System.getProperty("line.separator") + "}").replace("]", System.getProperty("line.separator") + "]");
                                    MainParser.getMembershipInfo(temp[0]);
                                }
                            });
                }
                if (page.startsWith("services.runescape.com/m=hiscore/index_lite.ws?player=")) {

                    // RS3 Stat Page, With rankings/Skill

                    browser.evaluateJavascript(
                            "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {

                                    TempString = html.replace("\\u003Chtml>", "").replace("\\u003Chead>", "").replace("\\u003C/head>", "").replace("\\u003Cbody>", "").replace("\\u003C/body>", "").replace("\\u003C/html>", "").replace("jQuery000000000000000_0000000000(", "").replace(");", "").replace("false", "\"false\"").replace("true", "\"true\"");
                                    byte ptext[] = TempString.getBytes(ISO_8859_1);
                                    temp[0] = new String(ptext, UTF_8).replaceFirst("\"", "").replace("\\\"", "\"").replace("\\\\", "\\").replace("\\/", "/").replace("\\n", System.getProperty("line.separator"))
                                            .replace("{", "{" + System.getProperty("line.separator")).replace("[", "[" + System.getProperty("line.separator")).replace("}", System.getProperty("line.separator") + "}").replace("]", System.getProperty("line.separator") + "]");
                                    MainParser.getCharacterStats(temp[0]);
                                }
                            });
                }
                if (page.startsWith("services.runescape.com/m=website-data/playerdetails.ws?names=[")) {

                    // Players Title+Clan Information

                    browser.evaluateJavascript(
                            "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {

                                    TempString = html.replace("\\u003Chtml>", "").replace("\\u003Chead>", "").replace("\\u003C/head>", "").replace("\\u003Cbody>", "").replace("\\u003C/body>", "").replace("\\u003C/html>", "").replace("jQuery000000000000000_0000000000(", "").replace(");", "").replace("false", "\"false\"").replace("true", "\"true\"");
                                    byte ptext[] = TempString.getBytes(ISO_8859_1);
                                    temp[0] = new String(ptext, UTF_8).replaceFirst("\"", "").replace("\\\"", "\"").replace("\\\\", "\\").replace("\\/", "/").replace("\\n", System.getProperty("line.separator"))
                                            .replace("{", "{" + System.getProperty("line.separator")).replace("[", "[" + System.getProperty("line.separator")).replace("}", System.getProperty("line.separator") + "}").replace("]", System.getProperty("line.separator") + "]");
                                    MainParser.getCharacterInfo(temp[0]); // RS3 Data
                                }
                            });
                }
                if (page.startsWith("secure.runescape.com/m=mtxn_rs_shop/reauth") || page.startsWith("secure.jagex.com/m=mtxn_rs_shop/reauth")) {

                    // Page after successful login, Reload the next step.

                    browser.loadUrl("https://secure.runescape.com/m=mtxn_rs_shop/api/accountInfo");
                }
                if (page.startsWith("www.runescape.com/c=") && page.endsWith("/companion/comapp.ws")) {

                    // Official Companion page, Post-Login
                    // Removes c= Flag from urlBar, as it's sensitive information and shouldn't be in screenshots.
                    // Set the Label Manually. Users would be on the official page to reach this step regardless.

                    urlBar.setText("http://runescape.com/companion/comapp.ws (c= Flag Hidden)");
                }
                if (page.startsWith("www.runescape.com/c=") && page.endsWith("/companion/app_add.ws")) {

                    // Official Companion Page, Pre-Login, Gather Required Device information.
                    // Removes c= Flag from urlBar, as it's sensitive information and shouldn't be in screenshots.
                    // Set the Label Manually. Users would be on the official page to reach this step regardless.

                    urlBar.setText("http://runescape.com/companion/app_add.ws (c= Flag Hidden)");
                }
                if (!url.equalsIgnoreCase("about:blank") &&
                        !(page.startsWith("www.runescape.com/c=") && page.endsWith("/companion/comapp.ws")) &&
                        !(page.startsWith("www.runescape.com/c=") && page.toLowerCase().endsWith("/companion/app_add.ws"))
                        ) {

                    // NOT a Pre-Determined page, Set the urlBar to be safe.

                    urlBar.setText(url);
                }
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                // Check for Data on url loading to set labels

                if (!url.contains("app_add.ws") && !url.contains("comapp.ws")) {

                    //do NOT show the companion url's due to the c= flag;

                    urlBar.setText(url);
                }
                String page = browser.getUrl().toLowerCase().replace("http://", "").replace("https://", "");
                if (page.startsWith("secure.runescape.com/m=weblogin/")) {

                    // Login Page Loaded. Change Label to Reflect This.

                    DisplayLabel.setText("Login to Continue.");
                    browser.setVisibility(View.VISIBLE);
                    lists.setVisibility(View.INVISIBLE);
                }
                if (page.startsWith("secure.runescape.com/m=identity_service/tryAuth") || page.startsWith("secure.jagex.com/m=identity_service/tryauth")) {

                    // Login Page Loading. Change Label to Reflect This.

                    DisplayLabel.setText("Loading Login Page..");
                    browser.setVisibility(View.VISIBLE);
                    lists.setVisibility(View.INVISIBLE);
                }
                if (page.startsWith("secure.runescape.com/m=mtxn_rs_shop/reauth") || page.startsWith("secure.jagex.com/m=mtxn_rs_shop/reauth")) {

                    // Page after successful login. Change Label to Reflect This.
                    // Change Visibility to Reflect the Login Page Possibly Needing Shown.

                    DisplayLabel.setText("Reloading Required to Re-Obtain Data..");
                    browser.setVisibility(View.INVISIBLE);
                    lists.setVisibility(View.VISIBLE);
                }
                if (page.startsWith("services.runescape.com/m=website-data/playerdetails.ws?names=[")) {

                    // Characters Clan/Title Page Loaded. Change Label to Reflect This.

                    DisplayLabel.setText("Parsing Character Title+Clan...");
                }
                if (page.startsWith("services.runescape.com/m=hiscore/index_lite.ws?player=")) {

                    // Characters Stats Page Loaded. Change Label to Reflect This.

                    DisplayLabel.setText("Parsing Character Stats...");
                }
                if (page.startsWith("secure.runescape.com/m=mtxn_rs_shop/api/accountinfo")) {

                    // Solomon's Membership Information Loaded. Change Label to Reflect This.
                    // Change Visibility to Reflect the Login Page Possibly Needing Shown.

                    DisplayLabel.setText("Parsing Solomon's Membership Payout Information..");
                    browser.setVisibility(View.INVISIBLE);
                    lists.setVisibility(View.VISIBLE);
                }
                if (page.startsWith("secure.runescape.com/m=mtxn_rs_shop/api/config?")) {

                    // Solomon's Version Information Loaded. Change Label to Reflect This.
                    // Change Visibility to Reflect the Login Page Possibly Needing Shown.

                    DisplayLabel.setText("Parsing Current Solomon's General Store's version information..");
                    browser.setVisibility(View.INVISIBLE);
                    lists.setVisibility(View.VISIBLE);
                }
                if (page.startsWith("www.runescape.com/") && page.endsWith("/companion/app_add.ws")) {

                    // Official Companion Page, Pre-Login, Gather Required Device information.
                    // Removes c= Flag from urlBar, as it's sensitive information and shouldn't be in screenshots.
                    // Set the Label Manually. Users would be on the official page to reach this step regardless.
                    // Change Visibility to Reflect the Login Page Possibly Needing Shown.

                    DisplayLabel.setText("Welcome to the Internal RuneScape Companion app!");
                    urlBar.setText("http://runescape.com/companion/app_add.ws (c= Flag Hidden)");
                    browser.setVisibility(View.VISIBLE);
                    lists.setVisibility(View.INVISIBLE);
                }
                super.onPageStarted(view, url, favicon);
            }
        });

        /*





         SPACERS DUE TO LONG WEBVIEW EXTRA DATA





        */

        // Load Visibility Defaults Here;

        lists.setVisibility(View.VISIBLE);
        CharacterAvatar.setVisibility(View.GONE);
        browser.setVisibility(View.INVISIBLE);

        // Load Labels Here

        DisplayLabel.setText("Welcome To the Unofficial RuneScape API!");
        urlBar.setText("This 3RD party application was made by Sudo Bash.");

        // End Main Thread Load

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_CompanionApp) {

            // Companion app was selected
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:

                            // Granted Permission to load the internal WebView for the companion app

                            Toast.makeText(CONTEXT, "Loading the internal RuneScape Companion app.", Toast.LENGTH_SHORT).show();
                            try {

                                DisplayLabel.setText("Loading the RuneScape Companion App..");
                                adapter.clear();
                                adapter.notifyDataSetChanged();
                                ToggleViews("Browser", "http://www.runescape.com/companion");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            // Denied Permission to load the internal WebView for the companion app

                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:

                                            // Granted Permission to load the official companion app from Jagex

                                            if (hasRSCompanion()) {
                                                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.jagex.RSCompanion");
                                                if (launchIntent != null) {

                                                    // User had the Companion App, launch it.

                                                    Toast.makeText(CONTEXT, "Launching the official RuneScape Companion app.", Toast.LENGTH_LONG).show();
                                                    startActivity(launchIntent);
                                                }
                                            } else {
                                                try {

                                                    // User didn't have the Companion App, launch Google Play to download it.

                                                    Toast.makeText(CONTEXT, "No RuneScape Companion app installed? Do you know WHAT this Means!?..", Toast.LENGTH_LONG).show();
                                                    Toast.makeText(CONTEXT, "Launching Google Play to download the official app!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.jagex.RSCompanion")));
                                                } catch (android.content.ActivityNotFoundException nope) {

                                                    // User didn't have the Companion App, nor Google Play. Loading Google Play's website to be safe.

                                                    Toast.makeText(CONTEXT, "Nope.. Never mind.. No Google Play app is installed..", Toast.LENGTH_LONG).show();
                                                    Toast.makeText(CONTEXT, "Launching Google Play's WEBSITE to download the official app..", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.jagex.RSCompanion")));
                                                }
                                            }
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:

                                            // Denied Permission to load the official Companion App from Jagex, Abort process.
                                            Toast.makeText(CONTEXT, "Operation Aborted.", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.CONTEXT, R.style.OrangeDialogStyle);
                            builder.setMessage("Do you want to launch the official RuneScape Companion app?").setPositiveButton("Yes.", dialogClickListener)
                                    .setNegativeButton("No.", dialogClickListener).show();
                            break;
                    }
                }
            };
            MainActivity.CONTEXT = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.OrangeDialogStyle);
            builder.setMessage("Do you want to load the internal RuneScape Companion app?\n\nPlease Note: this will require being logged into Jagex's website, which will load in a WebView browser.\n\nIf you are not comfortable with this, Please hit \"No.\" below to attempt to load the Official app.").setPositiveButton("Yes.", dialogClickListener)
                    .setNegativeButton("No.", dialogClickListener).show();
        } else if (id == R.id.nav_Solomons) {

            // Solomon's Price Checker was Selected;

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            try {

                                // Granted Permission to load the information, Request login for required cookies.

                                Toast.makeText(CONTEXT, "Loading Solomon's General Store's Data.", Toast.LENGTH_SHORT).show();
                                DisplayLabel.setText("Loading Solomon's General Store's Data.");
                                adapter.clear();
                                adapter.notifyDataSetChanged();
                                list = new LinkedList(asList(PleaseWait));
                                adapter = new ArrayAdapter<>(CONTEXT, R.layout.custom_listview, list);
                                adapter.notifyDataSetChanged();
                                lists.setAdapter(adapter);
                                ToggleViews("Solomons", "https://secure.jagex.com/m=identity_service/tryAuth?redirto=https%3Arunescape%3Amtxn_rs_shop%3Areauth");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            // Denied Permission to load the information, Abort Process.

                            Toast.makeText(CONTEXT, "Operation Aborted.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.OrangeDialogStyle);
            builder.setMessage("Do you want to load Solomon's General Store's data?\n\nPlease Note: this will require being logged into Jagex's website, which will load in a WebView browser.\n\nIf you are not comfortable with this, Please hit \"No.\" below.").setPositiveButton("Yes.", dialogClickListener)
                    .setNegativeButton("No.", dialogClickListener).show();
        } else if (id == R.id.nav_CharacterInformation) {

            // Character Information Selected

            LayoutInflater inflater = LayoutInflater.from(this);
            final View dialog = inflater.inflate(R.layout.custom_edittext, null);
            final EditText playerToUse = (EditText) dialog.findViewById(R.id.app_CustomEditText);
            AlertDialog.Builder ad = new AlertDialog.Builder(this, R.style.OrangeDialogStyle);
            ad.setTitle("Enter a Character Name");
            ad.setMessage("This will grab information about the player. This includes their skill levels, experience, rankings, current title and clan name if any.");
            ad.setView(dialog);
            ad.setPositiveButton("Get Data!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    // Granted Permission to Search this Characters Data

                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    list = new LinkedList(asList(PleaseWait));
                    adapter = new ArrayAdapter<>(CONTEXT, R.layout.custom_listview, list);
                    adapter.notifyDataSetChanged();
                    lists.setAdapter(adapter);
                    CharacterAvatar.setVisibility(View.VISIBLE);
                    DisplayLabel.setText("Loading Character Data.");
                    lists.setVisibility(View.VISIBLE);
                    browser.setVisibility(View.INVISIBLE);
                    CharacterLoader.SetUsedImage(String.valueOf(playerToUse.getText()));
                    CharacterLoader.Load(String.valueOf(playerToUse.getText()));
                }
            });
            ad.setNegativeButton("Abort!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    // Denied Permission to Search this Characters Data, Abort Process;

                    Toast.makeText(CONTEXT, "Operation Aborted.", Toast.LENGTH_SHORT).show();
                }
            });
            ad.show();
        } else if (id == R.id.nav_CharacterRSSFeed) {
            LayoutInflater inflater = LayoutInflater.from(this);
            final View dialog = inflater.inflate(R.layout.custom_edittext, null);
            final EditText playerToUse = (EditText) dialog.findViewById(R.id.app_CustomEditText);
            AlertDialog.Builder ad = new AlertDialog.Builder(this, R.style.OrangeDialogStyle);
            ad.setTitle("Enter a Character Name");
            ad.setMessage("This will grab information about the player's recent RS3 Activity.");
            ad.setView(dialog);
            ad.setPositiveButton("Get Data!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    browser.setVisibility(View.INVISIBLE);
                    lists.setVisibility(View.VISIBLE);

                    if (android.os.Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy =
                                new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }
                    try {
                        String tempName = playerToUse.getText().toString();
                        tempName = tempName.replace(" ","%20");
                        URL url = new URL("http://services.runescape.com/m=adventurers-log/rssfeed?searchName="+tempName);
                        InputStream TempConnect = url.openStream();
                        RssFeed feed = RssReader.read(url);
                        ArrayList<RssItem> rssItems = feed.getRssItems();
                        String[] values = new String[rssItems.size()+2];
                        values[0] = "Data From: "+url.toString();
                        values[1] = "";
                        DisplayLabel.setText("Recent Activity for: "+playerToUse.getText().toString());
                        urlBar.setText(url.toString());
                        int ListCounter = 2;
                        for(RssItem rssItem : rssItems) {
                            values[ListCounter] = "[ "+rssItem.getTitle()+" ]" + rssItem.getDescription().replace("\t", "") + "\n";
                            ListCounter += 1;
                        }
                        list = new LinkedList(asList(values));
                        adapter = new ArrayAdapter<>(CONTEXT, R.layout.custom_listview, list);
                        lists.setAdapter(adapter);
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e){
                        String[] Phrases = new String[1];
                        Phrases[0] = "This players's information does not exist, or is not able to be read.";
                        list = new LinkedList(asList(Phrases));
                        adapter = new ArrayAdapter<>(CONTEXT, R.layout.custom_listview, list);
                        lists.setAdapter(adapter);
                        DisplayLabel.setText("Error Loading " + playerToUse.getText() + "'s Character.");
                        urlBar.setText("Enjoy this 3RD party Application! - Sudo Bash");
                        browser.setVisibility(View.INVISIBLE);
                        lists.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    lists.setAdapter(adapter);
                    CharacterAvatar.setVisibility(View.VISIBLE);
                    CharacterLoader.SetUsedImage(String.valueOf(playerToUse.getText()));
                }
            });
            ad.setNegativeButton("Abort!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Toast.makeText(CONTEXT, "Operation Aborted.", Toast.LENGTH_SHORT).show();
                }
            });
            ad.show();
        } else if (id == R.id.nav_RecentRS3NewsRSSFeed) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            // Set Visibilities

                            CharacterAvatar.setVisibility(View.GONE);
                            browser.setVisibility(View.INVISIBLE);
                            lists.setVisibility(View.VISIBLE);

                            // Internet-related crash fix..

                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                StrictMode.ThreadPolicy policy =
                                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                            }
                            try {

                                // Load the RSS Feed

                                URL url = new URL("http://services.runescape.com/m=news/latest_news.rss");
                                RssFeed feed = RssReader.read(url);
                                ArrayList<RssItem> rssItems = feed.getRssItems();
                                String[] values = new String[rssItems.size() + 2];
                                values[0] = "Data From: "+url.toString();
                                values[1] = "";
                                DisplayLabel.setText("Recent RS3 News");
                                urlBar.setText(url.toString());

                                // Parse the Data

                                int ListCounter = 2;
                                for (RssItem rssItem : rssItems) {
                                    values[ListCounter] = "[ " + rssItem.getTitle() + " ]" + rssItem.getDescription().replace("\t", "") + "See More Information at: " + rssItem.getLink() + "\n";
                                    ListCounter += 1;
                                }

                                // Display the Data

                                list = new LinkedList(asList(values));
                                adapter = new ArrayAdapter<String>(CONTEXT, R.layout.custom_listview, list);
                                lists.setAdapter(adapter);

                            } catch (SAXException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            // Denied Permission to load the information, Abort Process.

                            Toast.makeText(CONTEXT, "Operation Aborted.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.OrangeDialogStyle);
            builder.setMessage("Do you want to load RuneScape's Recent news RSS Feed?").setPositiveButton("Yes.", dialogClickListener)
                    .setNegativeButton("No.", dialogClickListener).show();
        } else if (id == R.id.nav_RecentOSRSNewsRSSFeed) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:

                            // Set Visibility

                            CharacterAvatar.setVisibility(View.GONE);
                            browser.setVisibility(View.INVISIBLE);
                            lists.setVisibility(View.VISIBLE);

                            // Internat-Related Crash fix..

                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                StrictMode.ThreadPolicy policy =
                                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                            }
                            try {

                                // Load the RSS Feed

                                URL url = new URL("http://services.runescape.com/m=news/latest_news.rss?oldschool=true");
                                RssFeed feed = RssReader.read(url);
                                ArrayList<RssItem> rssItems = feed.getRssItems();
                                String[] values = new String[rssItems.size() + 2];
                                values[0] = "Data From: "+url.toString();
                                values[1] = "";
                                DisplayLabel.setText("Recent OSRS News");
                                urlBar.setText(url.toString());

                                // Parse the Data

                                int ListCounter = 2;
                                for (RssItem rssItem : rssItems) {
                                    values[ListCounter] = "[ " + rssItem.getTitle() + " ]" + rssItem.getDescription().replace("\t", "") + "See More Information at: " + rssItem.getLink() + "\n";
                                    ListCounter += 1;
                                }

                                // Display the Data

                                list = new LinkedList(asList(values));
                                adapter = new ArrayAdapter<>(CONTEXT, R.layout.custom_listview, list);
                                lists.setAdapter(adapter);
                            } catch (SAXException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            // Denied Permission to load the information, Abort Process.

                            Toast.makeText(CONTEXT, "Operation Aborted.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.OrangeDialogStyle);
            builder.setMessage("Do you want to load OldSchool RuneScape's recent news RSS feed?").setPositiveButton("Yes.", dialogClickListener)
                    .setNegativeButton("No.", dialogClickListener).show();
        } else if (id == R.id.nav_GoogleAuthenticator) {

            // Google Auth was selected

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:

                            // Granted Permission to load the Google Authenticator app

                            if (hasGoogleAuth()) {
                                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.authenticator2");
                                if (launchIntent != null) {

                                    // User had the Google Authenticator App, launch it.

                                    Toast.makeText(CONTEXT, "Launching Google's Authenticator.", Toast.LENGTH_LONG).show();
                                    startActivity(launchIntent);
                                }
                            } else {
                                try {

                                    // User didn't have the Google Authenticator App, launch Google Play to download it.

                                    Toast.makeText(CONTEXT, "No Authenticator installed? Do you Know WHAT this Means!?..", Toast.LENGTH_LONG).show();
                                    Toast.makeText(CONTEXT, "Launching Google Play to download the Authenticator.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.authenticator2")));
                                } catch (android.content.ActivityNotFoundException nope) {

                                    // User didn't have the Google Authenticator App, nor Google Play. Loading Google Play's website to be safe.

                                    Toast.makeText(CONTEXT, "Nope.. Never mind.. No Google Play app is installed..", Toast.LENGTH_LONG).show();
                                    Toast.makeText(CONTEXT, "Launching Google Play's WEBSITE to attempt to download the Google Authenticator..", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2")));
                                }
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            // Denied Permission to load the Google Authenticator app, Abort Process.

                            Toast.makeText(CONTEXT, "Operation Aborted.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.OrangeDialogStyle);
            builder.setMessage("Do you want to launch the Google Authenticator?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }  else if (id == R.id.nav_Logout) {

            // Logout was Selected, Kill all Data in the WebView (Minor Added Security), Close App

            adapter.clear();
            browser.clearFormData();
            browser.clearHistory();
            browser.clearSslPreferences();
            browser.clearCache(true);
            CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            finishAffinity();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void ToggleViews(String Displayed, Object extras) throws IOException {

        // Automatic Hiding+showing of Views based on what's in Displayed. (easier to me), Loads data too.

        if (Displayed.equalsIgnoreCase("Blank")) {
            for (int i = 0; i < ContentLayout.getChildCount(); i++) {
                ContentLayout.getChildAt(i).setVisibility(View.INVISIBLE);
            }
            browser.loadUrl("about:blank");
            CharacterAvatar.setVisibility(View.GONE);
            browser.pauseTimers();
        } else if (Displayed.equalsIgnoreCase("Browser")) {
            for (int i = 0; i < ContentLayout.getChildCount(); i++) {
                ContentLayout.getChildAt(i).setVisibility(View.INVISIBLE);
            }
            browser.resumeTimers();
            browser.setVisibility(View.VISIBLE);
            CharacterAvatar.setVisibility(View.GONE);
            browser.loadUrl((String) extras);
        } else if (Displayed.equalsIgnoreCase("Solomons")) {
            for (int i = 0; i < ContentLayout.getChildCount(); i++) {
                ContentLayout.getChildAt(i).setVisibility(View.INVISIBLE);
            }
            LoadingData = true;
            browser.loadUrl((String) extras);
            CharacterAvatar.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < ContentLayout.getChildCount(); i++) {
                ContentLayout.getChildAt(i).setVisibility(View.INVISIBLE);
            }
            browser.setVisibility(View.VISIBLE);
            CharacterAvatar.setVisibility(View.GONE);
            browser.loadUrl("about:blank");
            browser.pauseTimers();
        }
    }

// Finished Methods (do not touch unless needed)
    public boolean hasGoogleAuth() {

    // Check Packages installed for: Google Authenticator (com.google.android.apps.authenticator2)

    List<ApplicationInfo> packages;
    PackageManager pm = getPackageManager();
    packages = pm.getInstalledApplications(0);
    for (ApplicationInfo packageInfo : packages) {
        if (packageInfo.packageName.equals("com.google.android.apps.authenticator2"))
            return true;
    }
    return false;
}
    public boolean hasRSCompanion() {

        // Check Packages installed for: Runescape Companion (com.jagex.RSCompanion)

        List<ApplicationInfo> packages;
        PackageManager pm = getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.jagex.RSCompanion"))
                return true;
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("JavascriptInterface")

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onStop() {
        super.onStop();

        // Kills all Data in the WebView (Minor Added Security) when android kills the app from Recents menu
        browser.clearFormData();
        browser.clearHistory();
        browser.clearSslPreferences();
        browser.clearCache(true);
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Kills all Data in the WebView (Minor Added Security) when android kills the App from Low Memory
        browser.clearFormData();
        browser.clearHistory();
        browser.clearSslPreferences();
        browser.clearCache(true);
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }
    @Override
    public void onBackPressed() {

        // Stop back presses, encourage using "Logout" to delete webview data.

        Toast.makeText(CONTEXT, "Please use the \"Logout\" button to close this application\n\nIt's added to clear the WebView's caches for some added security.", Toast.LENGTH_SHORT).show();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START); // closes the drawer if open
        }
    }

//End of Class
}
