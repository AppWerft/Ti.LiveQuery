package ti.livequery;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;

public class KrollCallbacks {
	public KrollFunction onSuccess;
	public KrollFunction onError;
	public KrollFunction onEvent;

	public KrollCallbacks(KrollDict opts) {
		if (opts.containsKeyAndNotNull("onsuccess"))
			onSuccess = (KrollFunction) opts.get("onsuccess");
		else
			onSuccess = null;
		if (opts.containsKeyAndNotNull("onerror"))
			onError = (KrollFunction) opts.get("onerror");
		else
			onError = null;
		if (opts.containsKeyAndNotNull("onevent"))
			onEvent = (KrollFunction) opts.get("onevent");
		else
			onEvent = null;

	}

}
