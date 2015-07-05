package writeRDF;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.google.gson.Gson;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import extract.LeipzigItem;

public class WriteRDF {

	protected static final String AMUE = "http://www.imn.htwk-leipzig.de/~amuelle3/Data/Model/";
	protected static final String SPIE = "http://www.imn.htwk-leipzig.de/~amuelle3/Data/Spielplatz/";
	protected static final String KIGA = "http://www.imn.htwk-leipzig.de/~amuelle3/Data/Kindergarten/";
	protected static final String SWBA = "http://www.imn.htwk-leipzig.de/~amuelle3/Data/Schwimmbad/";
	protected static final String SPHA = "http://www.imn.htwk-leipzig.de/~amuelle3/Data/Sporthalle/";
	protected static final String SPPL = "http://www.imn.htwk-leipzig.de/~amuelle3/Data/Sportplatz/";

	protected static final String OWL_URL = "http://www.w3.org/2002/07/owl#";
	protected static final String LD = "http://leipzig-data.de/Data/Model/";

	public static void main(String[] args) throws IOException {

		writeRDF(SPIE, "spielplaetze", "Spielplaetze der Stadt Leipzig", "Spielplatz", "Spielplatz in Leipzig", false);
		writeRDF(KIGA, "kindergaerten", "Kindergaerten der Stadt Leipzig", "Kindergarten", "Kindergarten in Leipzig",
				true);
		writeRDF(SWBA, "schwimmbaeder", "Schwimmbaeder der Stadt Leipzig", "Schwimmbad", "Schwimmbad in Leipzig", true);
		writeRDF(SPHA, "sporthallen", "Sporthallen der Stadt Leipzig", "Sporthalle", "Sporthalle in Leipzig", true);
		writeRDF(SPPL, "sportplaetze", "Sportplaetze der Stadt Leipzig", "Sportplatz", "Sportplatz in Leipzig", true);

		System.out.println("exit");
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

	private static void writeModel(Model model, String filename) throws FileNotFoundException {

		FileOutputStream fos = new FileOutputStream("rdf/" + filename + ".ttl");
		RDFDataMgr.write(fos, model, Lang.TURTLE);

	}

	public static void writeRDF(String url, String filename, String ontLable, String className, String classLable,
			boolean addressAvailable) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader("rdf/Adressen.ttl"));

		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while (line != null) {
			sb.append(line);
			sb.append("\n");
			line = br.readLine();
		}
		String adressen = sb.toString();

		BufferedReader jsonFile = new BufferedReader(new FileReader("json/" + filename + ".json"));
		Gson gson = new Gson();
		LeipzigItem[] itemList = gson.fromJson(jsonFile, LeipzigItem[].class);

		Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		model.setNsPrefix("ld", LD);
		model.setNsPrefix("am", AMUE);

		// Create Ontology
		Resource ont = model.createResource("");
		model.add(ont, RDF.type, OWL.Ontology);
		model.add(ont, RDFS.label, ontLable);

		// Create Class
		Resource classRes = model.createResource(AMUE + className);
		model.add(classRes, RDF.type, OWL.Class);
		model.add(classRes, RDFS.label, classLable);

		// Create Resources
		for (int i = 0; i < itemList.length; i++) {

			Resource r = model.createProperty(url + itemList[i].getUrl());
			// add Class
			model.add(r, RDF.type, classRes);
			// add Label
			model.add(r, RDFS.label, itemList[i].getName());

			// add Address
			if (addressAvailable) {
				boolean addAdress = true;

				String plz = itemList[i].getPlz();
				String ort = itemList[i].getOrt();
				String strasseNr = itemList[i].getStrasse();
				strasseNr = replaceUmlaute(strasseNr);

				String[] teile = strasseNr.split(" ");
				String str = "";
				String nr = "";
				int nrStart = teile.length - 1;
				for (int j = 0; j < teile.length; j++) {

					if (teile[j].substring(0, 1).matches("\\d") || j > nrStart) {
						nrStart = j;
						teile[j] = teile[j].toUpperCase();
						nr += teile[j];
					} else {
						str += teile[j];
					}
				}

				List<String> results = new ArrayList<String>();
				Pattern pattern = Pattern.compile("\\d+[A-Z]?", Pattern.UNICODE_CASE);
				Matcher matcher = pattern.matcher(nr);

				while (matcher.find()) {
					if (matcher.groupCount() > 0) {
						results.add(matcher.group(1));
					} else {
						results.add(matcher.group());
					}
				}

				if (results.size() != 0) {
					nr = results.get(0);
				}

				// Ausnahmen:
				if (str.equals("Schoenbachstrasse")) {
					plz = "04299";
				}
				if (str.equals("Inselstrasse")) {
					plz = "04103";
				}
				if (str.equals("Bothestrasse")) {
					plz = "04155";
				}
				if (str.equals("AmGutenbergplatz")) {
					str = "Gutenbergplatz";
				}
				if (str.equals("Schongauerstrasse")) {
					plz = "04328";
				}
				if (str.equals("Vollbedingstrasse")) {
					str = "Volbedingstrasse";
				}
				if (str.equals("Hammerstrasse")) {
					plz = "04277";
				}
				if (str.equals("ZumFeld")) {
					plz = "04158";
				}
				if (str.equals("Hans-Weigel-Strasse")) {
					plz = "04319";
				}

				String strasse = "";
				if (nr.isEmpty()) {
					strasse = str;
				} else {
					strasse = str + "." + nr;
				}

				String adresse = "http://leipzig-data.de/Data/" + plz + "." + ort + "." + strasse;

				if (adressen.contains(adresse)) {

				} else {
					adresse = "http://leipzig-data.de/Data/" + plz + "." + ort + "." + str;
					if (adressen.contains(adresse)) {

					} else {
						System.out.print("error: ");
						System.out.println(adresse);
						addAdress = false;
					}
				}

				if (addAdress) {
					Property hasAddress = model.createProperty(LD + "hasAddress");
					model.add(r, hasAddress, adresse);
				}
			}

			// add Ortsteil
			String ortsteil = itemList[i].getOrtsteil();
			ortsteil = replaceUmlaute(ortsteil);
			Property inOrtsteil = model.createProperty(LD + "inOrtsteil");
			model.add(r, inOrtsteil, "http://leipzig-data.de/Data/Ortsteil/" + ortsteil);

		}

		writeModel(model, filename);

		jsonFile.close();
		br.close();
	}
}
