package trikita.router;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.*;
import java.lang.reflect.InvocationTargetException;

public class ViewRouter {
	private final static String tag = "ViewRouter";

	private ViewGroup mParent;

	private List<Pair<String, Class<? extends View>>> mRouting =
		new ArrayList<>();

	public ViewRouter(ViewGroup parent) {
		mParent = parent;
	}

	public ViewRouter(Activity a) {
		mParent = new FrameLayout(a);
		a.setContentView(mParent);
	}

	public ViewRouter add(String uri, Class<? extends View> a) {
		mRouting.add(new Pair(uri, a));
		return this;
	}

	private <T extends View> T createView(Class<T> cls, Map<String, String> props) {
		try {
			T v = cls.getConstructor(Context.class, AttributeSet.class)
				.newInstance(mParent.getContext(), null);
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

	public boolean route(String uri) {
		Map<String, String> properties = new HashMap<String, String>();

		for (Pair<String, Class<? extends View>> entry : mRouting) {
			properties.clear();

			if (Utils.matchUri(uri, entry.first, properties)) {
				View v = createView(entry.second, properties);
				// navigate to the matched view
				mParent.removeAllViews();
				mParent.addView(v);
				return true;
			}
		}
		return false;
	}
}
