package hcmodtools.core.chatcontrol;

import hcmodtools.core.ModTool;

import org.bukkit.plugin.java.JavaPlugin;

public class ChatControl implements ModTool {
	private JavaPlugin jp;

	public ChatControl(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public void onStart() {
		jp.getServer().getPluginCommand("chatclear")
				.setExecutor(new ChatClearer());
		ChatSilencer cs = new ChatSilencer();
		ChatMonitor ch = new ChatMonitor();
		jp.getServer().getPluginCommand("chatsilence").setExecutor(cs);
		jp.getServer().getPluginCommand("chatmon").setExecutor(ch);
		jp.getServer().getPluginManager().registerEvents(ch,jp);
		jp.getServer().getPluginManager().registerEvents(cs, jp);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

}
