package trikita.router;

import java.util.Map;
import android.util.Log;

public class Utils {
	private final static String tag = "Utils";

	public static boolean matchUri(String uri, String route, Map<String, String> properties) {
		Log.d(tag, "uri: \""+uri+"\"");
		Log.d(tag, "route: \""+route+"\"");

		if (route.length() == 0 || uri.length() == 0) {
			return false;
		}

		int lastRouteIndex = route.indexOf("/");
		int lastUriIndex = uri.indexOf("/");

		// detects uris that do not contain '/'
		if (lastRouteIndex != 0 || lastUriIndex != 0) {
			return false;
		}

		// route consists of only one character, simple compare it with the uri
		if (route.length() <= lastRouteIndex+1) {
			return route.equals(uri);
		}

		// uri consists of only one character, simple compare it with the route
		if (uri.length() <= lastUriIndex+1) {
			return route.equals(uri);
		}

		int newRouteIndex, newUriIndex;
		boolean routeEnded = false;
		boolean uriEnded = false;
		while (true) {
			newRouteIndex = route.indexOf("/", lastRouteIndex+1);
			newUriIndex = uri.indexOf("/", lastUriIndex+1);

			if (newRouteIndex == -1) {
				newRouteIndex = route.length();
				routeEnded = true;
			}

			if (newUriIndex == -1) {
				newUriIndex = uri.length();
				uriEnded = true;
			}
			Log.d(tag, "lu="+lastUriIndex+" nu="+newUriIndex);
			Log.d(tag, "lr="+lastRouteIndex+" nr="+newRouteIndex);

			String routeToken = route.substring(lastRouteIndex+1, newRouteIndex);
			String token = uri.substring(lastUriIndex+1, newUriIndex);

			Log.d(tag, "uri token="+token);
			Log.d(tag, "route token="+routeToken);

			// copy uri params into the properties
			if (routeToken.startsWith(":")) {
				properties.put(routeToken.substring(1), token);
			// copy uri remainder for parsing by nested routers into the properties
			} else if (routeToken.equals("...")) {
				properties.put("uriRemainder", uri.substring(lastUriIndex));
				return true;
			// mismatch found, go to the next route
			} else if (!routeToken.equals(token)) {
				return false;
			}

			if ((routeEnded && !uriEnded) || (uriEnded && !routeEnded)) {
				return false;
			}

			if (uriEnded && routeEnded) {
				break;
			}

			lastRouteIndex = newRouteIndex;
			lastUriIndex = newUriIndex;
		}

		return true;
	}
}
