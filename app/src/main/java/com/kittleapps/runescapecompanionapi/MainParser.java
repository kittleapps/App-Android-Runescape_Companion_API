package com.kittleapps.runescapecompanionapi;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Locale;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import static com.kittleapps.runescapecompanionapi.CharacterLoader.CharacterIconBase;
import static com.kittleapps.runescapecompanionapi.MainActivity.CONTEXT;
import static com.kittleapps.runescapecompanionapi.MainActivity.CharacterAvatar;
import static com.kittleapps.runescapecompanionapi.MainActivity.DisplayLabel;
import static com.kittleapps.runescapecompanionapi.MainActivity.adapter;
import static com.kittleapps.runescapecompanionapi.MainActivity.browser;
import static com.kittleapps.runescapecompanionapi.MainActivity.list;
import static com.kittleapps.runescapecompanionapi.MainActivity.lists;
import static com.kittleapps.runescapecompanionapi.MainActivity.tempListData;
import static com.kittleapps.runescapecompanionapi.MainActivity.urlBar;
import static java.util.Arrays.asList;

public class MainParser {
    public static String
            Version,
            isSuffix, Recruiting, Name, Clan, Title,
            accumulativeMonths, consecutiveMonths, nextPayoutTime, nextPayoutBase, nextPayoutBonus,
            memberInfo, memberStreakInfo, payoutInfo, CharacterData, CharacterName;
    private static String[] Phrases;
    private static boolean Done = false;
    public MainParser() {
    }
    public static String getVersion(String html){
        Version = "";
        Done = false;
        InputStream is = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
        try (JsonParser parser = Json.createParser(is)) {
            while (parser.hasNext() && Done == false) {
                Event e = parser.next();
                if (e == Event.KEY_NAME) {
                    switch (parser.getString()) {
                        case "version":
                            // Version found, Save Data+Resume;
                            parser.next();
                            Version = parser.getString();
                            Done = true;
                            parser.close();
                            break;
                    }
                }
            }
        }
        return Version;
    }
    public static String getMembershipInfo(String html){

        // Set Dummy Values for Initializers;

        accumulativeMonths = "40";
        consecutiveMonths = "40";
        nextPayoutTime = "523006219";
        nextPayoutBase = "4040";
        nextPayoutBonus = "4040";

        Done = false;
        InputStream is = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));

        try (JsonParser parser = Json.createParser(is)) {
            while (parser.hasNext() && Done == false) {
                Event e = parser.next();
                if (e == Event.KEY_NAME) {
                    switch (parser.getString()) {

                        // Grab Self-Explainitory Label Values;

                        case "accumulativeMonths":
                            parser.next();
                            accumulativeMonths = parser.getString();
                            break;

                        case "consecutiveMonths":
                            parser.next();
                            consecutiveMonths = parser.getString();
                            break;

                        case "nextPayoutTime":
                            parser.next();
                            nextPayoutTime = parser.getString();
                            break;

                        case "nextPayoutBase":
                            parser.next();
                            nextPayoutBase = parser.getString();
                            break;

                        case "nextPayoutBonus":
                            parser.next();
                            nextPayoutBonus = parser.getString();

                            // Members Total Calculations;

                            int MemberYearTotal = ((Integer.parseInt(accumulativeMonths) - (Integer.parseInt(accumulativeMonths) % 12)) / 12);
                            int MemberMonthTotal = (Integer.parseInt(accumulativeMonths) % 12);
                            int MemberCurrentYearTotal = ((Integer.parseInt(consecutiveMonths) - (Integer.parseInt(consecutiveMonths) % 12)) / 12);
                            int MemberCurrentMonthTotal = (Integer.parseInt(consecutiveMonths) % 12);
                            int MemberPayoutTotal = Integer.parseInt(nextPayoutBase)+Integer.parseInt(nextPayoutBonus);

                            // Time Frame Calculations

                            long seconds = Integer.parseInt(nextPayoutTime) / 1000;
                            long minutes = seconds / 60;
                            long hours = minutes / 60;
                            long days = hours / 24;

                            // Formatting

                            String time = days + ":" + hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
                            String[] MemberDaysLeftToPayout = time.split(":");

                            // Hard-Coded Strings to use

                            memberInfo = "You currently have used: "+MemberYearTotal+" year(s) and "+MemberMonthTotal+" month(s) of membership.";
                            memberStreakInfo = "You currently have streak of: "+MemberCurrentYearTotal+" year(s) and "+MemberCurrentMonthTotal+" month(s) of membership.";
                            payoutInfo = "You will receive: "+NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(MemberPayoutTotal))+" Loyalty Points payout in approximately: "+MemberDaysLeftToPayout[0]+" day(s), "+MemberDaysLeftToPayout[1]+" hour(s), and "+MemberDaysLeftToPayout[2]+" minute(s).\n("+NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(nextPayoutBase))+" Base + "+NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(nextPayoutBonus))+" Bonus)";

                            // Resume Data Gathering
                            MainActivity.browser.loadUrl("https://secure.runescape.com/m=mtxn_rs_shop/api/config?context%5B0%5D=0");
                            Done = true;
                            parser.close();
                            break;
                    }
                }
            }
        }
        return accumulativeMonths+","+consecutiveMonths+","+nextPayoutTime+","+nextPayoutBase+","+nextPayoutBonus;
    }
    public static String getCharacterInfo(String html){

        // Set Dummy Data for Initialization
        isSuffix = " ";
        Recruiting = " ";
        Name = " ";
        Clan = " ";
        Title = " ";
        Done = false;
        InputStream is = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));

        try (JsonParser parser = Json.createParser(is)) {
            while (parser.hasNext() && Done == false) {
                Event e = parser.next();
                if (e == Event.KEY_NAME) {
                    switch (parser.getString()) {

                        // Grab Self-Explainitory Label Values;

                        case "isSuffix":
                            parser.next();
                            isSuffix = parser.getString();
                            break;

                        case "recruiting":
                            parser.next();
                            Recruiting = parser.getString();
                            break;

                        case "name":
                            parser.next();
                            Name = parser.getString();
                            break;

                        case "clan":
                            parser.next();
                            Clan = parser.getString();
                            break;

                        case "title":
                            parser.next();
                            Title = parser.getString();
                            if(Title.equals("")){

                                // No title value found, set Dummy data;

                                Title = " ";
                            }
                            Done = true;
                            parser.close();
                            break;
                    }
                }
            }
        }

        // Parse Character Data, Resume Data Collection;

        ParseCharacterData(isSuffix+","+Recruiting+","+Name+","+Clan+","+Title);
        return isSuffix+","+Recruiting+","+Name+","+Clan+","+Title;
    }
    public static void getListing(String Input){
        try {
            InputStream is = new ByteArrayInputStream(Input.getBytes(StandardCharsets.UTF_8));
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String results =  result.toString("UTF-8").replace("false", "\"False\"").replace("true", "\"True\"");
            is = new ByteArrayInputStream(results.getBytes(StandardCharsets.UTF_8));
            Done = false;
            StringBuilder sb = new StringBuilder();

            try (JsonParser parser = Json.createParser(is)) {
                while (parser.hasNext() && !Done) {
                    Event e = parser.next();
                    if (e == Event.KEY_NAME) switch (parser.getString()) {

                        // Gather Solomons General Store Data from Self-Explainitory Values;

                        case "name":
                            parser.next();

                            // Gather Some Category Titles, Reformat as Individuals;

                            if (parser.getString().equalsIgnoreCase("Free Item for Members")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Bestsellers")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Newly Released")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Featured Loyalty Items")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Solomon's Sale")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Featured")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Packs")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Outfits")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Accessories")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Combat Gear")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Effects")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Wardrobe")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Skills")) {
                                sb.append("\n").append("Animations (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Spells")) {
                                sb.append("\n").append("Animations (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Kills")) {
                                sb.append("\n").append("Animations (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Walks")) {
                                sb.append("\n").append("Animations (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Animations")) {
                                sb.append("\n").append(parser.getString() + " (All)").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Emotes")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Equipment")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Titles")) {
                                sb.append("\n").append(parser.getString()).append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Re-colours")) {
                                sb.append("\n").append("Services (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Services")) {
                                sb.append("\n").append(parser.getString() + " (All)").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Female Hair")) {
                                sb.append("\n").append("HairStyles (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Male Hair")) {
                                sb.append("\n").append("HairStyles (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Hairstyles")) {
                                sb.append("\n").append(parser.getString() + " (All)").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Legendary")) {
                                sb.append("\n").append("Pets (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Companions")) {
                                sb.append("\n").append("Pets (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Followers")) {
                                sb.append("\n").append("Pets (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Treats")) {
                                sb.append("\n").append("Pets (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Pets")) {
                                sb.append("\n").append(parser.getString() + " (All)").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Tier 1")) {
                                sb.append("\n").append("Auras (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Tier 2")) {
                                sb.append("\n").append("Auras (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Tier 3")) {
                                sb.append("\n").append("Auras (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Tier 4")) {
                                sb.append("\n").append("Auras (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Tier 5")) {
                                sb.append("\n").append("Auras (" + parser.getString() + ")").append("\n");
                            } else if (parser.getString().equalsIgnoreCase("Auras")) {

                                // Stop at Auras T5, as it's Redundant for a full auras list when they all are just above this. (can't fix other values atm)

                                Done = true;
                            } else {
                                sb.append(parser.getString());
                                sb.append(":");
                            }
                            break;
                        case "price":
                            parser.next();
                            sb.append(parser.getString());
                            sb.append(":");
                            break;
                        case "isOwned":
                            parser.next();
                            // Swap Values (The General Store uses the Misleading Label "isOwned" for is you CAN Purchase, not if you own it)
                            if (parser.getString().equalsIgnoreCase("False")) {
                                sb.append("is purchased");
                            } else if (parser.getString().equalsIgnoreCase("True")) {
                                sb.append("is NOT purchased");
                            }
                            sb.append("\n");
                            break;
                        case "currency":
                            parser.next();

                            // Currency is in an integer, Change to their Currency Names;

                            if (parser.getString().equals("0")) {
                                sb.append("RuneCoins");
                            } else if (parser.getString().equals("1")) {
                                sb.append("Loyalty Points");
                            } else {
                                // I didn't Think they would add more, so i'm Leaving this here for future patches if-any.
                                sb.append("Unknown Currency");
                            }
                            sb.append(":");
                            break;
                        case "loyalty":
                            parser.next();

                            // This value is read first so adding Account information to the top here to save some loading time;

                            sb.append(parser.getString());
                            sb.append(System.getProperty("line.separator"));
                            sb.append(System.getProperty("line.separator"));
                            sb.append("Featured Banner Items");
                            sb.append(System.getProperty("line.separator"));
                            break;
                        case "runecoins":
                            parser.next();
                            sb.append(parser.getString());
                            sb.append(":");
                            break;
                    }
                }
                if (Done){

                    // Start Organizing Data, and put in the ListView

                    tempListData = sb.toString().split(System.getProperty("line.separator"));
                    for (int i = 0; i < tempListData.length; i++){
                        if(tempListData[i].contains(":") &&
                                !tempListData[i].startsWith("You have been a member for a total of") &&
                                !tempListData[i].startsWith("You will receive")){

                            if ((tempListData[i].replace(";","").split(":").length > 0) && (tempListData[i].replace(";","").split(":").length != 2)) {
                                String[] TempData = tempListData[i].split(":");
                                if (TempData[2].contains("Keepsake Key") ||
                                        TempData[2].contains("Clan Citadel Booster") ||
                                        TempData[2].contains("Challenge Gem") ||
                                        TempData[2].contains("Jelly Treat") ||
                                        TempData[2].contains("Tasty Treat") ||
                                        TempData[2].equalsIgnoreCase("Prismatic Dye") ||
                                        TempData[2].equalsIgnoreCase("Wicked Pouch") ||
                                        TempData[2].equalsIgnoreCase("Dwarven Challenge Barrel 10 Pack") ||
                                        TempData[2].equalsIgnoreCase("Party Pack") ||
                                        TempData[2].equalsIgnoreCase("Chameleon Extract") ||
                                        TempData[2].equalsIgnoreCase("Growth Surge")){

                                    // Unlimited-Purchase items, they default is NOT owned, but that's because they truly never ARE owned.

                                    tempListData[i] = "\""+TempData[2] + "\" currently costs " + TempData[1] + " " + TempData[0] + ", and stock is UNLIMITED.";
                                }
                                else {

                                    // ITEM currently costs CURRENCY_AMOUNT CURRENCY_NAME, and it is OWNED/NOT_OWNED.

                                    tempListData[i] = "\"" +
                                            TempData[2] +
                                            "\" currently costs " +
                                            TempData[1] +
                                            " " +
                                            TempData[0] +
                                            ", and it " +
                                            TempData[3] + ".";
                                }
                            }
                            else{
                                    String[] TempData = tempListData[i].split(":");

                                    // Load Account Information here, Formatted numbers for 32-Bit numbers (having over 32-Bit is unrealistic at the time of creation)

                                    tempListData[i] = "[ Your Account Information ]\n\nYou currently have: " +
                                            NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(TempData[0])) +
                                            " RuneCoins and " +
                                            NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(TempData[1])) +
                                            " Loyalty Points.\n"+memberInfo+"\n"+memberStreakInfo+"\n"+payoutInfo;
                            }
                        }
                        else if (tempListData[i].startsWith("You currently have used: ") ||
                                 tempListData[i].startsWith("You currently have streak of: ") ||
                                 tempListData[i].startsWith("You will receive: ")){

                                // Ignore Premade Account information Strings;

                        }
                        else {
                            if (tempListData[i].length() > 0) {

                                // Automatically Set Category Label Brackets, Normally this will apply to Stray values as well. (helps point out where flaws occur)

                                tempListData[i] = "[ "+tempListData[i]+" ]";
                            }
                            else{

                                // Redundant else, added to prevent any unknown crashes

                            }
                        }
                    }

                    // Change Visibility;

                    browser.setVisibility(View.INVISIBLE);
                    lists.setVisibility(View.VISIBLE);

                    // Set Data

                    list = new LinkedList(asList(tempListData));
                    adapter = new ArrayAdapter<>(CONTEXT, R.layout.custom_listview, list);
                    adapter.notifyDataSetChanged();
                    lists.setAdapter(adapter);

                    // Change Labels

                    DisplayLabel.setText("Loaded Current Solomon's General Store/Account Information.");
                    urlBar.setText("Enjoy this 3RD party Application! - Sudo Bash");

                    // Clear Browser
                    browser.loadUrl("about:blank");
                }
            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    public static void getCharacterStats(String Data){
        if (!Data.contains("error404")) {
            // Hard-Coded Stat List Ordering for RS3 Updated up-to Invention;
            String[] Stats = Data.replace("\n", ",").split(",");
            String[][] rowData = {
                    {CharacterData + "\n"},
                    {"Total", Stats[1], Stats[2], Stats[0]},
                    {"Attack", Stats[4], Stats[5], Stats[3]},
                    {"Defence", Stats[7], Stats[8], Stats[6]},
                    {"Strength", Stats[10], Stats[11], Stats[9]},
                    {"Constitution", Stats[13], Stats[14], Stats[12]},
                    {"Ranged", Stats[16], Stats[17], Stats[15]},
                    {"Prayer", Stats[19], Stats[20], Stats[18]},
                    {"Magic", Stats[22], Stats[23], Stats[21]},
                    {"Cooking", Stats[25], Stats[26], Stats[24]},
                    {"Woodcutting", Stats[28], Stats[29], Stats[27]},
                    {"Fletching", Stats[31], Stats[32], Stats[30]},
                    {"Fishing", Stats[34], Stats[35], Stats[33]},
                    {"Firemaking", Stats[37], Stats[38], Stats[36]},
                    {"Crafting", Stats[40], Stats[41], Stats[39]},
                    {"Smithing", Stats[43], Stats[44], Stats[42]},
                    {"Mining", Stats[46], Stats[47], Stats[45]},
                    {"Herblore", Stats[49], Stats[50], Stats[48]},
                    {"Agility", Stats[52], Stats[53], Stats[51]},
                    {"Thieving", Stats[55], Stats[56], Stats[54]},
                    {"Slayer", Stats[58], Stats[59], Stats[57]},
                    {"Farming", Stats[61], Stats[62], Stats[60]},
                    {"RuneCrafting", Stats[64], Stats[65], Stats[63]},
                    {"Hunter", Stats[67], Stats[68], Stats[66]},
                    {"Construction", Stats[70], Stats[71], Stats[69]},
                    {"Summoning", Stats[73], Stats[74], Stats[72]},
                    {"Dungeoneering", Stats[76], Stats[77], Stats[75]},
                    {"Divination", Stats[79], Stats[80], Stats[78]},
                    {"Invention", Stats[82], Stats[83], Stats[81]},
            };

            Phrases = new String[rowData.length];
            Phrases[0] = rowData[0][0];
            for (int ROW = 1; ROW < rowData.length; ROW++) {

                // Check+Format Information, if not ranked they have no data to display anyways;

                if (Integer.valueOf(rowData[ROW][3]) <= 0) {
                    Phrases[ROW] = "They are currently not ranked in " + rowData[ROW][0] + " on RS3 to gather additional information.\n";
                } else {
                    Phrases[ROW] = "They currently have " + rowData[ROW][1] + " " + rowData[ROW][0] + " levels.\n(" + rowData[ROW][2] + " " + rowData[ROW][0] + " Experience)\nThey are currently Ranked #" + rowData[ROW][3] + " in " + rowData[ROW][0] + " on RS3.\n";
                }
            }

            // Set ListView Data

            list = new LinkedList(asList(Phrases));
            adapter = new ArrayAdapter<>(CONTEXT, R.layout.custom_listview, list);
            lists.setAdapter(adapter);

            // Change Labels

            DisplayLabel.setText("Now introducing " + CharacterName + "'s Character.");
            urlBar.setText("Enjoy this 3RD party Application! - Sudo Bash");

            // Enable Avatar Saving

            CharacterAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DownloadImageTask(MainActivity.CharacterAvatar)
                            .execute(CharacterIconBase.replace("$s", CharacterName.trim().replace(" ", "+")));
                }
            });
            CharacterAvatar.setClickable(true);

            // Set Visibilities

            browser.setVisibility(View.INVISIBLE);
            lists.setVisibility(View.VISIBLE);

            // Clear Browser
            browser.loadUrl("about:blank");
        }else{
            Phrases = new String[1];
            Phrases[0] = "This Characters information does not exist.";

            list = new LinkedList(asList(Phrases));
            adapter = new ArrayAdapter<String>(CONTEXT, R.layout.custom_listview, list);
            lists.setAdapter(adapter);

            // Change Labels

            DisplayLabel.setText("Error Loading " + CharacterName + "'s Character.");
            urlBar.setText("Enjoy this 3RD party Application! - Sudo Bash");

            // Set Visibilities
            CharacterAvatar.setVisibility(View.GONE);
            browser.setVisibility(View.INVISIBLE);
            lists.setVisibility(View.VISIBLE);

            // Clear Browser
            browser.loadUrl("about:blank");
        }
    }

    public static void ParseCharacterData(String Data){

        // Gather Self-Explainitory Character Title/Clan Information

        String[] TempData = Data.split(",");
        boolean hasTitle = false, hasClan = false, clanReq = false;
        if(!TempData[1].equals(" ")){
            clanReq = true;
        }
        if(!TempData[3].equals(" ")){
            hasClan = true;
        }
        if(!TempData[4].equals(" ")){
            hasTitle = true;
        }
        if(hasClan && hasTitle && clanReq){
            CharacterData = TempData[2]+"'s Title is currently: \""+TempData[4]+"\"\nThey're currently in the clan \""+TempData[3]+"\", which is Recruiting.";
        }
        else if(hasClan && hasTitle && !clanReq){
            CharacterData = TempData[2]+"'s Title is currently: \""+TempData[4]+"\"\nThey're currently in the clan \""+TempData[3]+"\", which is NOT Recruiting.";
        }
        else if(!hasClan && hasTitle && !clanReq){
            CharacterData = TempData[2]+"'s Title is currently: \""+TempData[4]+"\"\nThey're not currently in the clan.";
        }
        else if(hasClan && !hasTitle && clanReq){
            CharacterData = TempData[2]+" currently has no Title set.\nThey're currently in the clan \""+TempData[3]+"\", which is Recruiting.";
        }
        else if(hasClan && !hasTitle && !clanReq){
            CharacterData = TempData[2]+" currently has no Title set.\nThey're currently in the clan \""+TempData[3]+"\", which is NOT Recruiting.";
        }
        else if(!hasClan && !hasTitle && !clanReq){
            CharacterData = TempData[2]+" currently has no Title set.\nThey're not currently in the clan.";
        }
        CharacterName = TempData[2];

        // Continue Character Data Gathering.
        MainActivity.browser.loadUrl("http://services.runescape.com/m=hiscore/index_lite.ws?player="+TempData[2]);
    }
    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        // Dedicated Class to download Avatar images to External Storage

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
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            File docs = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "KittleApps");
            File AvatarSavedPath = new File(docs, "RS_API"+File.separator+"Avatars"+File.separator);
            AvatarSavedPath.mkdirs();
            if(!AvatarSavedPath.exists()) {
                AvatarSavedPath.mkdirs();
            }
            else{
                boolean success = false;
                File AvatarSavedFile = new File(AvatarSavedPath, CharacterName.trim().toLowerCase()+"_Chat.png");
                FileOutputStream outStream;
                try {
                    outStream = new FileOutputStream(AvatarSavedFile);
                    result.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                    success = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (success) {
                    Toast.makeText(MainActivity.CONTEXT,
                            "Downloaded "+CharacterName+"'s Avatar to: "+AvatarSavedFile.getAbsolutePath(),
                            Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                        // Media Scan to update the Gallery Apps

                        MediaScannerConnection.scanFile(CONTEXT, new String[]{AvatarSavedFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
                    } else {

                        // Media Scan to update the Gallery Apps

                        CONTEXT.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                                Uri.parse("file://" + AvatarSavedFile.getAbsolutePath())));
                    }
                } else {

                    Toast.makeText(MainActivity.CONTEXT,
                            "Failed to Download "+CharacterName+"'s Avatar to: "+AvatarSavedFile.getAbsolutePath(),
                            Toast.LENGTH_LONG).show();
                }
            }

            // Set Downloaded image to the Character Icon;

            MainActivity.CharacterAvatar.setImageBitmap(result);
        }
    }
}