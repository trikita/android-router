package trikita.router;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.HashMap;

public class ViewRouter {
	private final static String tag = "ViewRouter";

	public final static int NO_HISTORY = 1;

	// Bundle keys
	final static String KEY_CLASSNAME = "className";
	final static String KEY_STATE = "state";
	final static String KEY_BACKSTACK = "backstack";

	private static final Map<Context, SoftReference<ViewRouter>> CACHE = new WeakHashMap<>();

	private ViewGroup mParent;

	private ArrayList<String> mBackstack = new ArrayList<>();

	private List<Pair<String, Class<? extends View>>> mRouting =
		new ArrayList<>();

	public ViewRouter(ViewGroup parent) {
		mParent = parent;
		if (!CACHE.containsKey(parent.getContext())) {
			CACHE.put(parent.getContext(), new SoftReference<>(this));
		}
	}

	public ViewRouter(Activity a) {
		CACHE.put(a, new SoftReference<>(this));
		mParent = new FrameLayout(a);
		a.setContentView(mParent);
	}

	public static ViewRouter get(View v) {
		SoftReference<ViewRouter> ref = CACHE.get(v.getContext());
		return (ref != null ? ref.get() : null);
	}

	// TODO validate uri, throw RuntimeException if it's invalid
	public ViewRouter add(String uri, Class<? extends View> a) {
		mRouting.add(new Pair(uri, a));
		return this;
	}

	public boolean route(String uri) {
		return route(uri, 0);
	}

	public String route() {
		if (mBackstack.size() > 0) {
			return mBackstack.get(mBackstack.size() - 1);
		} else {
			return null;
		}
	}

	public boolean route(String uri, int flags) {
		Map<String, String> properties = new HashMap<String, String>();

		for (Pair<String, Class<? extends View>> entry : mRouting) {
			properties.clear();

			if (Utils.matchUri(uri, entry.first, properties)) {
				View v = createView(entry.second, properties);

				if ((flags & NO_HISTORY) == 0) {
					mBackstack.add(uri);
				}

				// navigate to the matched view
				mParent.removeAllViews();
				mParent.addView(v);
				return true;
			}
		}
		return false;
	}

	public void save(Bundle b) {
		b.putStringArrayList(KEY_BACKSTACK, mBackstack);
	}

	public void load(Bundle b) {
		for (String uri : b.getStringArrayList(KEY_BACKSTACK)) {
			route(uri);
		}
	}

	public boolean back() {
		if (mBackstack.size() > 1) {
			mBackstack.remove(mBackstack.size() - 1);
			if (mBackstack.size() > 0) {
				route(mBackstack.get(mBackstack.size() - 1), NO_HISTORY);
				return true;
			}
		}
		return false;
	}

	private <T extends View> T createView(Class<T> cls, Map<String, String> props) {
		try {
			T v = cls.getConstructor(Context.class).newInstance(mParent.getContext());
			// copy parsed uri params into the view properties 
			for (Map.Entry<String, String> p : props.entrySet()) {
				Property<T, String> property = Property.of(cls, String.class, p.getKey());
				property.set(v, p.getValue());
			}
			return v;
		} catch (NoSuchMethodException|InstantiationException|
				IllegalAccessException|InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
