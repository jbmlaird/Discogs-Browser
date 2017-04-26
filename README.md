![app_logo](app/src/main/res/drawable-xxxhdpi/ic_app.png)

# DiscogsBrowser
Android app to interact with Discogs' API

# Description

Common libraries used:

* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [Dagger2](https://google.github.io/dagger/)
* [RxJava2](https://github.com/ReactiveX/RxJava)
* [RxBinding2](https://github.com/JakeWharton/RxBinding)
* [RetroLambda](https://github.com/evant/gradle-retrolambda)
* [LeakCanary](https://github.com/square/leakcanary)
* [RxCache](https://github.com/VictorAlbertos/RxCache) - new favourite library
* etc.

# Usage

This build is login only.

It is not ready for consumers at this stage.

# Features

* Reactive search
* Search history
* Displays artist, master, release and label information.
* Displays marketplace information on release page NB: marketplace searches only return 12"
* Collection/wantlist editing
* Request caching

# Forthcoming

* Carousels for images
* Browsing history
* Recent releases
* Browsing without being logged in
* Listen to releases in-app (Picture in picture YouTube?)
* Notifications (if API allows)
* Adding notes to items in collection

# Limitations

Currently, in April 2017, the public API does not allow any of the following:
* Order placing
* Marketplace actions. This includes purchasing in app, viewing purchases or searching listings.

# License

This project is licensed under the terms of the MIT license.