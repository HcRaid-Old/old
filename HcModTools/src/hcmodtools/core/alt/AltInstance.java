package hcmodtools.core.alt;

import java.util.ArrayList;
import java.util.List;

public class AltInstance {
	private String username;
	private ArrayList<String> ipList = new ArrayList<String>();

	public AltInstance(String username, String ipAddress) {
		this.username = username;
		this.ipList.add(ipAddress);
	}

	public void addIp(String ipAddress) {
		ipList.add(ipAddress);
	}

	public boolean containsIp(String ip) {
		return ipList.contains(ip);
	}

	public List<String> getIpList() {
		return ipList;
	}

	public String getName() {
		return username;
	}
}
