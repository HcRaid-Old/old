package com.addongaming.hcessentials.faq;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FAQSelection {
	private String[] faqList = null;
	private final String faqName;

	public FAQSelection(File file) {
		faqName = file.getName().replaceAll("(.txt)", "");
		try {
			Scanner scan = new Scanner(file);
			List<String> tempList = new ArrayList<String>();
			while (scan.hasNextLine())
				tempList.add(scan.nextLine());
			scan.close();
			faqList = tempList.toArray(new String[tempList.size()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String[] getFaqList() {
		return faqList;
	}

	public String getFaqName() {
		return this.faqName;
	}
}
