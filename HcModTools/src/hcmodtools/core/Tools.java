package hcmodtools.core;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Tools {
	private String posTitle;
	private String negTitle;

	public Tools(String posTitle, String negTitle) {
		this.posTitle = posTitle;
		this.negTitle = negTitle;
	}

	public Tools(Tools tool) {
		this.negTitle = tool.getNegTitle();
		this.posTitle = tool.getPosTitle();
	}

	public String getPosTitle() {
		return posTitle;
	}

	public String getNegTitle() {
		return negTitle;
	}

	public void msg(Player cs, String msg) {
		msg((CommandSender) cs, msg);
	}

	public void msg(CommandSender cs, String msg) {
		cs.sendMessage(posTitle + msg);
	}

	public boolean warn(Player cs, String msg) {
		return warn((CommandSender) cs, msg);

	}

	public boolean warn(CommandSender cs, String msg) {
		cs.sendMessage(negTitle + msg);
		return true;
	}

	public boolean smsg(Player cs, String msg) {
		cs.sendMessage("    " + msg);
		return true;
	}

	public boolean smsg(CommandSender cs, String msg) {
		cs.sendMessage("    " + msg);
		return true;
	}
}
