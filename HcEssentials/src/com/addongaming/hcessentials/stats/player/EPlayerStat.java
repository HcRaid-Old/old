package com.addongaming.hcessentials.stats.player;

public enum EPlayerStat {

	playersKilled("killedPlayers", "Players killed", Type.INCREMENT), entitiesKilled(
			"killedMobs", "Mobs Killed", Type.INCREMENT), timesDied(
			"killedSelf", "Times died", Type.INCREMENT), blocksBroken(
			"blocksBroken", "Broken blocks", Type.INCREMENT), blocksPlaced(
			"blocksPlaced", "Placed Blocks", Type.INCREMENT), itemsEnchanted(
			"itemsEnchanted", "Items Enchanted", Type.INCREMENT), moneyEarnt(
			"moneyEarnt", "Money earnt", Type.INCREMENT), moneySpent(
			"moneySpent", "Money spent", Type.INCREMENT), itemsRecycled(
			"itemsRecycled", "Items Recycled", Type.INCREMENT), bountiesIssues(
			"bountiesIssued", "Bounties Issued", Type.INCREMENT), bountiesClaimed(
			"bountiesClaimed", "Bounties Claimed", Type.INCREMENT);
	public static enum Type {
		SET, INCREMENT
	}

	private final String name;
	private String readableName;
	private Type type;

	EPlayerStat(String name, String readableName, Type type) {
		this.name = name;
		this.readableName = readableName;
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public Type getType() {
		return this.type;
	}

	public static EPlayerStat getByName(String str) {
		for (EPlayerStat eps : EPlayerStat.values())
			if (eps.getName().equalsIgnoreCase(str))
				return eps;
		return null;
	}

	public String getReadableName() {
		return readableName;
	}
}
