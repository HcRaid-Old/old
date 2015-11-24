package com.addongaming.hcessentials.perks.near;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class NearInstance implements Serializable {
	private static final long serialVersionUID = -4888723687106202396L;
	private String issuer;
	private List<NearObject> objs;
	private final long date;

	public NearInstance(String issuer, List<NearObject> objs) {
		this.issuer = issuer;
		this.objs = objs;
		date = new Date().getTime();
	}

	public long getDate() {
		return date;
	}

	public String getIssuer() {
		return issuer;
	}

	public List<NearObject> getObjs() {
		return objs;
	}

}
