package hcmodtools.core.playerspy;

import java.util.ArrayList;
import java.util.List;

public class WatchInstance {
	List<String> watching = new ArrayList<String>();

	public WatchInstance(String modName) {
		watching.add(modName);
	}

	public void addWatcher(String modName) {
		watching.add(modName);
	}

	public void removeWatcher(String modName) {
		watching.remove(modName);
	}

	public boolean containsWatcher(String modName) {
		return watching.contains(modName);
	}

	public String[] getWatchers() {
		return watching.toArray(new String[watching.size()]);
	}
}
