package extract;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractSportplaetze extends ExtractLeipzig {

	public ArrayList<LeipzigItem> extract() throws IOException {

		ArrayList<LeipzigItem> itemList = getItemList("377");

//		int i = 0;
		for (LeipzigItem item : itemList) {

//			if (i >= 3) {
//				break;
//			}
//			i++;

			Document innerdoc = Jsoup
					.connect(
							"http://www.leipzig.de/detailansicht-adresse/"
									+ item.getUrl() + "/").timeout(0)
					.get();
			// get Ortsteil
			Elements ulList = innerdoc.select("ul.list.space.clearfix");
			for (Element ul : ulList) {
				Elements li = ul.getElementsByTag("li");
				boolean itemFound = false;
				for (Element liItem : li) {
					String itemText = liItem.text();

					if (itemFound) {
						String ortsteil = itemText.split(",")[0];

						item.setOrtsteil(ortsteil);
						System.out.println(item.getName());
					}

					itemFound = false;
					if (itemText.equals("Ortsteil / Gebiet:")) {
						itemFound = true;
					} else {
						break;
					}
				}
			}

			// get Adresse
			Elements liList = innerdoc.select("li.address");
			for (Element li : liList) {

				String itemText = li.text();

				if (itemText.contains("Leipzig")) {

					String[] splits = itemText.split(" ");
					int anz = splits.length;

					item.setOrt(splits[anz - 1]);
					item.setPlz(splits[anz - 2]);

					String strasse = "";
					for (int j = 1; j < anz - 2; j++) {
						if (j != 1) {
							strasse += " ";
						}
						strasse += splits[j];
					}
					item.setStrasse(strasse);

					System.out.println(strasse);
				}

			}

		}

		return itemList;
	}

}
