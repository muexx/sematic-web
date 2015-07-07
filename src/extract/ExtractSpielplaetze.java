package extract;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Klasse um Spielplaetze zu extrahieren
 * @author Alex
 *
 */
public class ExtractSpielplaetze extends ExtractLeipzig {

	/**
	 * Spielplaetze ertrahieren
	 * @return
	 * @throws IOException
	 */
	public ArrayList<LeipzigItem> extract() throws IOException {

		//Liste mit Spielplaetzen von www.lwipzig.de extrahieren
		ArrayList<LeipzigItem> itemList = getItemList("111");

		for (LeipzigItem item : itemList) {

			//Ortsteil aus Detail-Seite extrahieren
			Document innerdoc = Jsoup.connect("http://www.leipzig.de/detailansicht-adresse/" + item.getUrl() + "/")
					.timeout(0).get();

			//Adresse aus Detail-Seite extrahieren
			String ortsteil = extractOrtsteil(innerdoc);
			item.setOrtsteil(ortsteil);
			
			System.out.println(item.getName());
		}

		return itemList;
	}

}
