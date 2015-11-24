package core.hcrg.bob;

import java.util.ArrayList;
import java.util.List;

public class BlockWrapper {

	private List<GeneratedBlock> gbList = new ArrayList<GeneratedBlock>();

	public void addNewBlock(GeneratedBlock gb) {
		this.gbList.add(gb);
	}

	public void clear() {
		this.gbList.clear();
	}

	public void tick() {
		for (GeneratedBlock gb : gbList)
			gb.onTick();
	}
}
