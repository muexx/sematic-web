package extract;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ExtractSpielplaetze extends ExtractLeipzig {

	public ArrayList<LeipzigItem> extract() throws IOException {

		ArrayList<LeipzigItem> itemList = getItemList("111");

		// int i = 0;
		for (LeipzigItem item : itemList) {

			// if (i >= 3) {
			// break;
			// }
			// i++;

			Document innerdoc = Jsoup.connect("http://www.leipzig.de/detailansicht-adresse/" + item.getUrl() + "/")
					.timeout(0).get();

			// get Ortsteil
			String ortsteil = extractOrtsteil(innerdoc);
			item.setOrtsteil(ortsteil);
			
			System.out.println(item.getName());
		}

		return itemList;
	}

}
