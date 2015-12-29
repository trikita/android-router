package trikita.router;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import org.junit.Assert.*;
import org.junit.Test;

public class ViewRouterActivityTest extends ActivityInstrumentationTestCase2<FakeActivity> {

	public static class FooView extends View {
		public FooView(Context c, AttributeSet attrs) {
			super(c, attrs);
		}
	}

	public ViewRouterActivityTest() {
		super(FakeActivity.class);
	}

	@Test
	@UiThreadTest
	public void testRouterInstance() {
		FrameLayout root = new FrameLayout(getActivity());
		ViewRouter r1 = new ViewRouter(root).add("/foo", FooView.class);
		r1.route("/foo");
		FooView foo = (FooView) root.getChildAt(0);
		assertEquals(ViewRouter.get(foo), null);

		ViewRouter r2 = new ViewRouter(getActivity()).add("/foo", FooView.class);
		r2.route("/foo");
		foo = (FooView) root.getChildAt(0);
		assertEquals(ViewRouter.get(foo), r2);
	}
}

