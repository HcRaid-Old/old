package core.hwh;

import org.bukkit.WorldType;

public class NewWorldStore {
	private final String worldName;
	private final String worldTypeStr;
	private final WorldType worldType;
	private final int type;

	public NewWorldStore(String worldName, String worldTypeStr,
			WorldType worldType, int type) {
		super();
		this.worldName = worldName;
		this.worldTypeStr = worldTypeStr;
		this.worldType = worldType;
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public String getWorldName() {
		return worldName;
	}

	public WorldType getWorldType() {
		return worldType;
	}

	public String getWorldTypeStr() {
		return worldTypeStr;
	}
}
