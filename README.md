CERN Android App
================

Using this app one can get the latest news from CERN and its experiments.

The app features:

- CERN news
- CERN history
- CERN physics
- CERN accelerators
- CERN experiments
- Live information of the 4 large LHC experiments
- Latest photos
- Latest videos
- Latest webcasts
- Latest tweets
- Job openings

The app works by monitoring a set of CERN RSS feeds. The web pages obtained via the feeds are cleaned up using the [Readability](https://www.readability.com) service before being shown to the user. As we use private, app specific, Readabilty keys, the files containing these keys are encrypted. If you want to build the full app please ask [us](cern-app@cern.ch) the decryption code.

**The app is still in pre-alpha stage but should get the same features as the [iOS version](https://github.com/cern-app/cern-app) of this app. Anybody who wants to work on this is more than welcome.**

Code
----

[The code](https://github.com/cern-app/cern-app-droid) is available from GitHub.

The code consists of a client side part, the Android app, and a server side part. The client side code works without server side. The server side is used to support push notifications and caching of Readabilty cleaned and already viewed pages. As the code contains also some app specific keys also the server side code is encrypted. Please ask us the decryption key if you want to develop this code.

License
-------

The code is available under the LGPL v2.

