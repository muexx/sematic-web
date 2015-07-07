package extract;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Klasse um Daten von www.leipzig.de zu extrahieren
 * 
 * @author Alex
 *
 */
public class ExtractMain {

	public static void main(String[] args) throws IOException {

		System.out.println("Start");

		// Spielplaetze extrahieren
		ExtractSpielplaetze exSpi = new ExtractSpielplaetze();
		ArrayList<LeipzigItem> spielpleatze = exSpi.extract();
		writeJSON(spielpleatze, "spielplaetze");
		System.out.println("Finish Spielplaetz");

		// Kindergaerten extrahieren
		ExtractKindergaerten exKg = new ExtractKindergaerten();
		ArrayList<LeipzigItem> kindergaerten = exKg.extract();
		writeJSON(kindergaerten, "kindergaerten");
		System.out.println("Finish Kindergaerten");

		// Schwimmbaeder extrahieren
		ExtractSchwimmbaeder exSw = new ExtractSchwimmbaeder();
		ArrayList<LeipzigItem> schwimmbaeder = exSw.extract();
		writeJSON(schwimmbaeder, "schwimmbaeder");
		System.out.println("Finish Schwimmbaeder");

		// Sporthallen extrahieren
		ExtractSporthallen exSpo = new ExtractSporthallen();
		ArrayList<LeipzigItem> sporthallen = exSpo.extract();
		writeJSON(sporthallen, "sporthallen");
		System.out.println("Finish Sporthallen");

		// Sportplaetze extrahieren
		ExtractSportplaetze exSpl = new ExtractSportplaetze();
		ArrayList<LeipzigItem> sportplaetze = exSpl.extract();
		writeJSON(sportplaetze, "sportplaetze");
		System.out.println("Finish Sportplaetze");

		System.out.println("End");
	}

	/**
	 * Gefundene Inhalte als JSON abspeichern
	 * 
	 * @param itemList
	 * @param filename
	 * @throws IOException
	 */
	private static void writeJSON(ArrayList<LeipzigItem> itemList, String filename) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		// JSON mit Gson aus den Objekten erzeugen
		String json = gson.toJson(itemList);

		File file = new File("json/" + filename + ".json");
		FileWriter writer = new FileWriter(file, false);
		// JSON in Datei schreiben
		writer.write(json);
		writer.close();

	}

}
