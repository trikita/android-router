# android-router

[![Build Status](https://travis-ci.org/trikita/android-router.svg?branch=master)](https://travis-ci.org/trikita/android-router)

A simple router (view dispatcher) for Android Activities and Views.

## Why?

The concept of a router is very common in single-page web applications. The reason why people prefer routers instead of showing/hiding/modifying views directly is because:

- Routers are declarative so you can immediately see what screens your app has
	and what parameters are passed between them
- Routers keep your UI in sync with the "url" in a reactive way (e.g. you
	change the url and views are updated automatically), and we all know that
	reactive is bettern than proactive if we talk about UI. That's why people
	love React.js, RxJava, Anvil etc.

## Install

```gradle
repositories {
	maven {  url = 'https://jitpack.io' }
}
dependencies {
	compile 'com.github.trikita:android-router:1.0.0'
}
```

## Activity Router

Declare routes:

```java
ActivityRouter.getDefault()
	.add("/splashscreen", SplashScreenActivity.class)
	.add("/login", LoginActivity.class)
	.add("/profile/:userId", ProfileActivity.class);
```

Navigate (open certain activity):

```java
ActivityRouter.getDefault().route(mContext, "/profile/" + mUserId);
```

Get route parameters inside an Activity:

```java
String userId = getIntent().getStringExtra("userId")
```

## View router

View router is most helpful if you want your application to be a single-activity app with multiple viewgroups inside (see ["Advocating agains fragments"](https://corner.squareup.com/2014/10/advocating-against-android-fragments.html) to find out why it's a good approach).

Declare routes and navigate:

```java
public class MainActivity extends Activity {
	ViewRouter mRouter = new ViewRouter(this);
	...
	mRouter
		.add("/splashscreen", SplashScreen.class)
		.add("/login", LoginScreen.class)
		.add("/profile/:userId/...", ProfileScreen.class); // note the "..." - it allows to nest view routers

	mRouter.route(String.format("/profile/%s/general", "user1234"));
```

Parameters are passed as [Properties]():

```java
public class ProfileScreen extends ViewGroup {

	String userId; // will be set to "user1234" automatically
	String uriRemainder; // will be set to "general"

	ViewRouter mPageRouter;
	
	ProfileScreen(Context c) {
		// Nested router will dispatch between certain view mounted inside the current one
		mPageRouter = new ViewRouter(findViewById(R.id.page_content))
			.add("/general", ProfileGeneralView.class)
			.add("/general", ProfileGeneralView.class);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		mPageRouter.route(this, uriRemainder);
	}
}
```

View router has its own backstack which can be unrolled with `mRouter.back()`.
This returns true if the navigation happened successfully, false if the
backstack was empty.

## Saving router state

Activity router state is saved automatically in the backstack.

View router state can be saved into a bundle using `mRouter.save(b)` and restored by `mRouter.load(b)`.

## License

Library is distributed under MIT license.
