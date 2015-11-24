package core.essentials.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class Config {
	public static List<String> bypass = new ArrayList<String>();
	public static List<String> fullBypass = new ArrayList<String>() {
		/**
		 * Default generated ID by inner components
		 */
		private static final long serialVersionUID = 5320279298181647456L;

		{
			add("hamgooof");
			add("dogpatch_1992");
			add("house1234");
			add("danslayerx");
		}
	};
	public static final String tntPerm = "hcraid.blaze";
	public static final Material tntDis = Material.BONE;

	public static class Spawn {
		public static final String world = "world";
		public static final double x = -441;
		public static final double y = 76;
		public static final double z = 594;
	}
}
