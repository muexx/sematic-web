package extract;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ExtractSporthallen extends ExtractLeipzig {

	public ArrayList<LeipzigItem> extract() throws IOException {

		ArrayList<LeipzigItem> itemList = getItemList("375");

		// int i = 0;
		for (LeipzigItem item : itemList) {
			System.out.println(item.getName());

			// if (i >= 3) {
			// break;
			// }
			// i++;

			Document innerdoc = Jsoup.connect("http://www.leipzig.de/detailansicht-adresse/" + item.getUrl() + "/")
					.timeout(0).get();
			
			// get Ortsteil
			String ortsteil = extractOrtsteil(innerdoc);
			item.setOrtsteil(ortsteil);
			
			// get Address
			String[] address = extractAddress(innerdoc);
			item.setStrasse(address[0]);
			item.setPlz(address[1]);
			item.setOrt(address[2]);
			
			System.out.println(item.getName());
		}

		return itemList;
	}

}
