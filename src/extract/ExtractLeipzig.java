package extract;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractLeipzig {

	public ArrayList<LeipzigItem> getItemList(String itemId) throws IOException {

		ArrayList<LeipzigItem> itemList = new ArrayList<LeipzigItem>();

		// http://www.leipzig.de/suchergebnisse-adressdatenbank/?tx_ewerkaddressdatabase_pi[showAll]=1&tx_ewerkaddressdatabase_pi[action]=list&tx_ewerkaddressdatabase_pi[controller]=Address&tx_ewerkaddressdatabase_pi[topics]=111

		Document doc = Jsoup.connect("http://www.leipzig.de/suchergebnisse-adressdatenbank/")
				.data("tx_ewerkaddressdatabase_pi[showAll]", "1").data("tx_ewerkaddressdatabase_pi[action]", "list")
				.data("tx_ewerkaddressdatabase_pi[controller]", "Address")
				.data("tx_ewerkaddressdatabase_pi[topics]", itemId).timeout(0).get();

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

	public String extractOrtsteil(Document innerdoc) throws IOException {

		String result = "";

		BufferedReader br = new BufferedReader(new FileReader("rdf/AdministrativeGliederung.ttl"));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while (line != null) {
			sb.append(line);
			sb.append("\n");
			line = br.readLine();
		}
		String ortsteileReference = sb.toString();
		br.close();

		Elements ulList = innerdoc.select("ul.list.space.clearfix");
		for (Element ul : ulList) {
			Elements li = ul.getElementsByTag("li");
			boolean itemFound = false;
			for (Element liItem : li) {
				String itemText = liItem.text();

				if (itemFound) {
					// erster Teil
					String ortsteil = replaceUmlaute(itemText.split(",")[0].trim());
					String ortsteilFull = "ldot:" + ortsteil;
					if (ortsteileReference.contains(ortsteilFull)) {
						result = ortsteil;
					} else {
						// zweiter Teil
						String ortsteil1 = replaceUmlaute(itemText.split(",")[1].trim());
						String ortsteilFull1 = "ldot:" + ortsteil1;
						if (ortsteileReference.contains(ortsteilFull1)) {
							result = ortsteil1;
						} else {
							System.out.print("error: ");
							System.out.println(ortsteil + ", " + ortsteil1);
						}
					}
				}

				itemFound = false;
				if (itemText.equals("Ortsteil / Gebiet:")) {
					itemFound = true;
				} else {
					break;
				}
			}
		}
		return result;
	}

	public String[] extractAddress(Document innerdoc) {
		String[] result = new String[3];

		Elements liList = innerdoc.select("li.address");
		for (Element li : liList) {

			String itemText = li.text();

			if (itemText.contains("Leipzig")) {

				String[] splits = itemText.split(" ");
				int anz = splits.length;

				String ort = replaceUmlaute(splits[anz - 1]);
				String plz = splits[anz - 2];

				String strasse = "";
				for (int j = 1; j < anz - 2; j++) {
					if (j != 1) {
						strasse += " ";
					}
					strasse += splits[j];
				}

				result[0] = strasse;
				result[1] = plz;
				result[2] = ort;
			}
		}
		return result;
	}
	
	public static String replaceUmlaute(String s) {
		// replace all lower Umlauts
		s = s.replaceAll("ü", "ue").replaceAll("ö", "oe").replaceAll("ä", "ae").replaceAll("ß", "ss")
				.replaceAll("é", "e");

		// first replace all capital umlaute in a non-capitalized context (e.g.
		// Übung)
		s = s.replaceAll("Ü(?=[a-zäöüß ])", "Ue").replaceAll("Ö(?=[a-zäöüß ])", "Oe")
				.replaceAll("Ä(?=[a-zäöüß ])", "Ae");

		// now replace all the other capital umlaute
		s = s.replaceAll("Ü", "UE").replaceAll("Ö", "OE").replaceAll("Ä", "AE");

		return s;
	}
}
