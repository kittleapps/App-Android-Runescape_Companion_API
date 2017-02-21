# Android-App-Runescape_Companion_API
Sloppy Code used for testing+distributions.

> This 3RD Party Application is **NOT** Affiliated with Jagex Limited. The use of this application is an **AS-IS** Basis.

> This Application is Open-Sourced due to handling sensitive login credentials for Jagex accounts to ease the minds of any End-Users (players) of any possible phishing-related activities.

> **The Source WILL be messy, but i will clean it as well as I can. Some methods may be done better, easier, and/or more efficient but to me, it works what it has to.**


**Basics of this application (app) so far**:

* Gives access to an Internal RuneScape Companion (with options to load the offical application), this also has NO push notifications as it would have to access a part of their servers. (This mechanic runs on a WebView Browser which is reliant to your Android rom at times for stability)
* Gives access to Solomon's General Stores price viewing. This requires login information, thus why this application is Open-Sourced. This information shows your charcacters current Runecoins, Loyalty Points, Membership total used, Current Membership Strea. Your next Loyalty Point payout ETA is estimated from the datas loading time. The prices of Solomon's goods include if you own them or not for one-time-purchase goods.
* Gives access to player's skill levels, experience, and ranking (if they're not ranked in a skill, there's no info to read) this also includes their set title, clan, if their clan is recruiting, and optionally downloading their characters RuneMetrics avatar.
* Gives access to a player's recent activities (via RSS feeds). This is reliant on their privacy settings and may not show some or any information based on that. (if they have no info, it will display as such)
* Gives access to the main (RS3) and OldSchool Runescape's news feed's most recent entries (Via RSS feeds) with hyperlinks to load in your browser to view more information.
* Gives a shortcut to load the Google Authenticator (or offer to download it from Google Play) for the internal RuneScape Companion function.

**TO-DO**:

* Cleanups (it really needs it..)
* Better Theming (Some android versions show dark text on dark backgrounds)
* Reduce Overdrawing (currently all the app does 4+ layers..)
* More Mechanics as I think of them.
* Redirect from unused webpages to about:blank
* Stability Fixes
* Returning to the app should* come back to the initial page's view.. will need to look into this.
