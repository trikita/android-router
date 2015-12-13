package trikita.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import java.util.*;

public class ActivityRouter {
	private final static String tag = "ActivityRouter";

	public static final ActivityRouter sInstance = new ActivityRouter();

	private List<Pair<String, Class<? extends Activity>>> mRouting =
		new ArrayList<>();

	public ActivityRouter() {
		// 
	}

	public static ActivityRouter getDefault() {
		return sInstance;
	}

	// TODO validate uri, throw RuntimeException if it's invalid
	public ActivityRouter add(String uri, Class<? extends Activity> a) {
		mRouting.add(new Pair(uri, a));
		return this;
	}

	public boolean route(Context c, String uri) {
		Intent intent = findRoute(c, uri);
		if (intent == null) {
			return false;
		}
		// navigate to the matched activity
		c.startActivity(intent);
		return true;
	}

	Intent findRoute(Context c, String uri) {
		Map<String, String> properties = new HashMap<String, String>();

		for (Pair<String, Class<? extends Activity>> entry : mRouting) {
			properties.clear();

			if (Utils.matchUri(uri, entry.first, properties)) {
				Intent intent = new Intent(c, entry.second);

				// copy parsed uri params into intent extras
				for (Map.Entry<String, String> p : properties.entrySet()) {
					intent.putExtra(p.getKey(), p.getValue());
				}
				return intent;
			}
		}
		return null;
	}
}
