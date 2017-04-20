package de.appwerft.parselivequery;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;

public class KrollCallbacks {
	public KrollFunction onSuccess;
	public KrollFunction onError;

	public KrollCallbacks(KrollDict opts) {
		if (opts.containsKeyAndNotNull("onsuccess"))
			onSuccess = (KrollFunction) opts.get("onsuccess");
		else
			onSuccess = null;
		if (opts.containsKeyAndNotNull("onerror"))
			onError = (KrollFunction) opts.get("onerror");
		else
			onError = null;

	}

}
