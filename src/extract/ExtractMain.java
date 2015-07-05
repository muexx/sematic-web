package extract;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ExtractMain {

	public static void main(String[] args) throws IOException {

		ExtractSpielplaetze exSpi = new ExtractSpielplaetze();
		ArrayList<LeipzigItem> spielpleatze = exSpi.extract();
		writeJSON(spielpleatze, "spielplaetze");

		ExtractKindergaerten exKg = new ExtractKindergaerten();
		ArrayList<LeipzigItem> kindergaerten = exKg.extract();
		writeJSON(kindergaerten, "kindergaerten");

		ExtractSchwimmbaeder exSw = new ExtractSchwimmbaeder();
		ArrayList<LeipzigItem> schwimmbaeder = exSw.extract();
		writeJSON(schwimmbaeder, "schwimmbaeder");

		ExtractSporthallen exSpo = new ExtractSporthallen();
		ArrayList<LeipzigItem> sporthallen = exSpo.extract();
		writeJSON(sporthallen, "sporthallen");

		ExtractSportplaetze exSpl = new ExtractSportplaetze();
		ArrayList<LeipzigItem> sportplaetze = exSpl.extract();
		writeJSON(sportplaetze, "sportplaetze");

		System.out.println("Exit");
	}

	private static void writeJSON(ArrayList<LeipzigItem> itemList, String filename) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(itemList);

		File file = new File("json/" + filename + ".json");
		FileWriter writer = new FileWriter(file, false);
		writer.write(json);
		writer.close();

	}

}
