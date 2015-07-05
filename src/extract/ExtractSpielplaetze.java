package extract;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractSpielplaetze extends ExtractLeipzig {

	public ArrayList<LeipzigItem> extract() throws IOException {

		ArrayList<LeipzigItem> itemList = getItemList("111");

		//int i = 0;
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
		}

		return itemList;
	}

}
