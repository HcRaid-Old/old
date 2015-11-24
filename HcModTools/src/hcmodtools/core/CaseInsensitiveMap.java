package hcmodtools.core;

import hcmodtools.core.xray.XrayInstance;

import java.util.HashMap;

@SuppressWarnings("serial")
public class CaseInsensitiveMap extends HashMap<String, XrayInstance> {
	public CaseInsensitiveMap() {
	}

	@Override
	public XrayInstance put(String key, XrayInstance value) {
		return super.put(key.toLowerCase(), value);
	}

	public XrayInstance get(String key) {
		return super.get(key.toLowerCase());
	}

	public boolean containsKey(String key) {

		return super.containsKey(key.toLowerCase());
	}

	public XrayInstance remove(String key) {
		return super.remove(key.toLowerCase());
	}
}
