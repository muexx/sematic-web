package extract;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Klasse um Sportplaetze zu extrahieren
 * @author Alex
 *
 */
public class ExtractSportplaetze extends ExtractLeipzig {

	/**
	 * Sportplaetze extrahieren
	 * @return
	 * @throws IOException
	 */
	public ArrayList<LeipzigItem> extract() throws IOException {

		//Liste mit Sportplaetzen von www.lwipzig.de extrahieren
		ArrayList<LeipzigItem> itemList = getItemList("377");
		
		for (LeipzigItem item : itemList) {
			
			// Detail-Seite öffnen
			Document innerdoc = Jsoup.connect("http://www.leipzig.de/detailansicht-adresse/" + item.getUrl() + "/")
					.timeout(0).get();
			
			//Ortsteil aus Detail-Seite extrahieren
			String ortsteil = extractOrtsteil(innerdoc);
			item.setOrtsteil(ortsteil);

			//Adresse aus Detail-Seite extrahieren
			String[] address = extractAddress(innerdoc);
			item.setStrasse(address[0]);
			item.setPlz(address[1]);
			item.setOrt(address[2]);
			
			System.out.println(item.getName());
		}

		return itemList;
	}

}
