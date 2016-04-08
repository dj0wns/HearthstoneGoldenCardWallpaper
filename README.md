# HearthstoneGoldenCardWallpaper

A Live wallpaper for Android devices which shows Golden Hearthstone Cards

Currently set to download a random hearthstone card gif from hearthhead every 2 hours and setting it as the background image.

Averages around 1mb every 2 hours or 1gb every 85 days (assuming a phone is open to the home screen for 85 days straight).

There are now settings to change the refresh rate and frame.

Since this is mostly a proof of concept I recognize that the code is not currently at a professional standard and plan on tidying everything up in the coming weeks.

I am open to all kinds of constructive criticism.

###TODO:
* Create an icon for the wallpaper
* Clean up code
* Add additional settings toggles 
* Parse the JSON on a separate thread
* Find a way to have the initial image be random
* Test with devices other than my Note4

###Special thanks to:
* Ashraff Hathibelagal for the article on how to write a live wallpaper
  * http://code.tutsplus.com/tutorials/create-a-live-wallpaper-on-android-using-an-animated-gif--cms-23088
* The Hearthhead team for the gifs
* The team behind HearthstoneJSON.com for providing a wonderful JSON with card id's
