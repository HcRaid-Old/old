package com.addongaming.hcessentials.redeem;

import java.io.Serializable;
import java.util.Date;

public class SyncRedeemTimestamp implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String group;
	private final long dateClaimed;
	private long dateUsed;
	private String itemName;

	public SyncRedeemTimestamp(String group, String itemName, long dateClaimed,
			long dateUsed) {
		this.group = group;
		this.dateClaimed = dateClaimed;
		this.dateUsed = dateUsed;
		this.itemName = itemName;
	}

	public Date getDateClaimed() {
		return new Date(dateClaimed);
	}

	public Date getDateUsed() {
		return (dateUsed != -1 ? new Date(dateUsed) : null);
	}

	public String getGroup() {
		return group;
	}

	public void setDateUsed(long dateUsed) {
		this.dateUsed = dateUsed;
	}

	public String getItemName() {
		return itemName;
	}
}
