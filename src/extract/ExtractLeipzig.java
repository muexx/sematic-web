package extract;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractLeipzig {

	public ArrayList<LeipzigItem> getItemList (String itemId) throws IOException{
		
		ArrayList<LeipzigItem> itemList = new ArrayList<LeipzigItem>();

		// http://www.leipzig.de/suchergebnisse-adressdatenbank/?tx_ewerkaddressdatabase_pi[showAll]=1&tx_ewerkaddressdatabase_pi[action]=list&tx_ewerkaddressdatabase_pi[controller]=Address&tx_ewerkaddressdatabase_pi[topics]=111

		Document doc = Jsoup
				.connect(
						"http://www.leipzig.de/suchergebnisse-adressdatenbank/")
				.data("tx_ewerkaddressdatabase_pi[showAll]", "1")
				.data("tx_ewerkaddressdatabase_pi[action]", "list")
				.data("tx_ewerkaddressdatabase_pi[controller]", "Address")
				.data("tx_ewerkaddressdatabase_pi[topics]", itemId).get();

		Elements results = doc.getElementsByClass("search-result-list");

		for (Element result : results) {
			Elements entries = result.getElementsByTag("h3");
			for (Element entry : entries) {
				Elements links = entry.getElementsByTag("a");
				for (Element link : links) {
					String linkHref = link.attr("href");
					String linkText = link.text();
					linkText = linkText.replaceAll("\"", "");
					linkText = linkText.replaceAll("'", "");
					linkText = linkText.replaceAll("„", "");

					String url = linkHref.split("/")[1];
					LeipzigItem item = new LeipzigItem();
					item.setName(linkText);
					item.setUrl(url);

					itemList.add(item);

				}

			}
		}
		return itemList;
	}
	
	
	
}
