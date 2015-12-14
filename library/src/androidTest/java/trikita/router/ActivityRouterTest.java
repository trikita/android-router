package trikita.router;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.test.AndroidTestCase;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;

import org.junit.Assert.*;
import org.junit.Test;

public class ActivityRouterTest extends AndroidTestCase {

	public static class FooActivity extends Activity {}
	public static class BarActivity extends Activity {}
	public static class BazActivity extends Activity {}

	@Test
	public void testSingleInstance() {
		ActivityRouter r1 = ActivityRouter.getDefault();
		ActivityRouter r2 = ActivityRouter.getDefault();
		assertTrue(r1 == r2);
	}

	@Test
	public void testSimpleUris() {
		ActivityRouter r = new ActivityRouter()
			.add("/", BazActivity.class)
			.add("/foo", FooActivity.class);

		Intent intent = r.findRoute(getContext(), "/");
		assertEquals(intent.getComponent().getClassName(),
				"trikita.router.ActivityRouterTest$BazActivity");

		intent = r.findRoute(getContext(), "/foo");
		assertEquals(intent.getComponent().getClassName(),
				"trikita.router.ActivityRouterTest$FooActivity");
	}

	@Test
	public void testUrisWithParams() {
		ActivityRouter r = new ActivityRouter()
			.add("/:uid", BazActivity.class)
			.add("/foo/:uid", FooActivity.class)
			.add("/foo/:uid/bar", BarActivity.class)
			.add("/foo/:uid/:name", FooActivity.class);

		Intent intent = r.findRoute(getContext(), "/hello");
		assertEquals(intent.getComponent().getClassName(),
				"trikita.router.ActivityRouterTest$BazActivity");

		intent = r.findRoute(getContext(), "/foo/helloworld");
		assertEquals(intent.getComponent().getClassName(),
				"trikita.router.ActivityRouterTest$FooActivity");
		assertEquals(intent.getStringExtra("uid"), "helloworld");

		intent = r.findRoute(getContext(), "/foo/hello/world");
		assertEquals(intent.getComponent().getClassName(),
				"trikita.router.ActivityRouterTest$FooActivity");
		assertEquals(intent.getStringExtra("uid"), "hello");
		assertEquals(intent.getStringExtra("name"), "world");

		intent = r.findRoute(getContext(), "/foo/hellohelloworld/bar");
		assertEquals(intent.getComponent().getClassName(),
				"trikita.router.ActivityRouterTest$BarActivity");
		assertEquals(intent.getStringExtra("uid"), "hellohelloworld");
	}
}

