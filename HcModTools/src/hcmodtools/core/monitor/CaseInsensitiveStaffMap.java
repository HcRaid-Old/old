package hcmodtools.core.monitor;

import hcmodtools.core.monitor.objects.StaffInstance;

import java.util.HashMap;

@SuppressWarnings("serial")
public class CaseInsensitiveStaffMap extends HashMap<String, StaffInstance> {
	public CaseInsensitiveStaffMap() {
	}

	@Override
	public StaffInstance put(String key, StaffInstance value) {
		return super.put(key.toLowerCase(), value);
	}

	public StaffInstance get(String key) {
		return super.get(key.toLowerCase());
	}

	public boolean containsKey(String key) {

		return super.containsKey(key.toLowerCase());
	}

	public StaffInstance remove(String key) {
		return super.remove(key.toLowerCase());
	}
}
