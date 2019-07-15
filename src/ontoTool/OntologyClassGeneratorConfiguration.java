/**
 * 
 */
package ontoTool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author jiezheng
 *
 */
public class OntologyClassGeneratorConfiguration {
	private String path = "";	// path of input file
	private String inputFilename = "";	// tab-delimited file for conversion
	private String ontologyIRIstr = "";		// ontology IRI of converted OWL file
	private String outputFilename = "";	// output converted OWL file name
	private String idBase = "http://purl.obolibrary.org/obo/";	// base URL of new term IRIs
	private String prefix = "";		// prefix for new term ID
	private int startNum = 1;		// start ID number	
	// index for term label, term IRI, term parent label or IRI in the tab-delimited file
	private int iriPos = -1;
	private int labelPos = -1;
	private int parentIriPos = -1;
	private int parentPos = -1;
	private String externalOntologyFilename = "";
	private ArrayList<String> annotLabels;
	
	// TODO: external ontology resource, retrieve IRI of existing term based on term label, optional
	// String externalOntoFilename = "";	
	
	OntologyClassGeneratorConfiguration() {
		this.path = "";	
		this.inputFilename = "";
		this.ontologyIRIstr = "";
		this.outputFilename = "";	
		this.idBase = "http://purl.obolibrary.org/obo/";
		this.prefix = "";	
		this.startNum = 1;	
		this.iriPos = -1;
		this.labelPos = -1;
		this.parentIriPos = -1;
		this.parentPos = -1;
		this.externalOntologyFilename = "";
		this.annotLabels = new ArrayList<String>();
	}
	
	// Read setting file and parse the file to get values of variables need for OWL conversion
	public void parseSetting(String settingFilename) {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(settingFilename));
			String line = null;

			while( (line = br.readLine()) != null)
			{
				if (!(line.trim().startsWith("##") || line.trim().length()==0)) {
					String[] items = line.split("\\s*\t\\s*");
					
					if (items.length == 2) {
						String name = items[0];
						String value = items[1];
						
						switch (name) {
							case "path":
								setPath(value);
								break;
							case "input file":
								setInputFilename(value);
								break;
							case "output file":
								setOutputFilename(value);
								break;
							case "ontology IRI":
								setOntologyIRIstr(value);
								break;
							case "IRI base":
								setIdBase(value);
								break;							
							case "prefix":
								setPrefix(value);
								break;								
							case "start ID":
								setStartNum(Integer.parseInt(value));
								break;	
							case "term position":
								setLabelPos(Integer.parseInt(value)-1);
								break;	
							case "term IRI position":
								setIriPos(Integer.parseInt(value)-1);
								break;	
							case "term parent position":
								setParentPos(Integer.parseInt(value)-1);
								break;	
							case "term parent IRI position":
								setParentIriPos(Integer.parseInt(value)-1);
								break;
							case "external ontology file":	
								setExternalOntologyFilename(value);
								break;
							case "annotation property":
								this.annotLabels.add(value);
								break;								
							default:
								System.out.println("Parameters not used in the script: " + name + ":" + value);
						}
					}
				}				
			}
			System.out.println("Successfully parse setting file: " + inputFilename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if ( br != null ) br.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}			
	}
	
	public String getPath () {
		return this.path;
	}
	
	public String getInputFilename () {
		return this.inputFilename;
	}
	
	public String getOntologyIRIstr () {
		return this.ontologyIRIstr;
	}
	
	public String getOutputFilename () {
		return this.outputFilename;	
	}
	
	public String getIdBase () {
		return this.idBase;
	}
	
	public String getPrefix () {
		return this.prefix;
	}

	public int getStartNum () {
		return this.startNum;
	}
	
	public int getIriPos () {
		return this.iriPos;
	}
	
	public int getLabelPos () {
		return this.labelPos;
	}
	
	public int getParentIriPos () {
		return this.parentIriPos;
	}
	
	public int getParentPos () {
		return this.parentPos;
	}
	
	public String getExternalOntologyFilename () {
		return this.externalOntologyFilename;
	}
	
	public ArrayList<String> getAnnotLabels () {
		return this.annotLabels;
	}
	
	public void setPath (String path) {
		this.path = path;
	}
	
	public void setInputFilename (String inputFilename) {
		this.inputFilename = inputFilename;
	}
	
	public void setOntologyIRIstr (String ontologyIRIstr) {
		this.ontologyIRIstr = ontologyIRIstr;
	}
	
	public void setOutputFilename (String outputFilename) {
		this.outputFilename = outputFilename;	
	}
	
	public void setIdBase (String idBase) {
		this.idBase = idBase;
	}
	
	public void setPrefix (String prefix) {
		this.prefix = prefix;
	}

	public void setStartNum (int startNum) {
		this.startNum = startNum;
	}
	
	public void setIriPos (int iriPos) {
		this.iriPos = iriPos;
	}
	
	public void setLabelPos (int labelPos) {
		this.labelPos = labelPos;
	}
	
	public void setParentIriPos (int parentIriPos) {
		this.parentIriPos = parentIriPos;
	}
	
	public void setExternalOntologyFilename (String externalOntologyFilename) {
		this.externalOntologyFilename = externalOntologyFilename;
	}
	
	public void setParentPos (int parentPos) {
		this.parentPos = parentPos;
	}
}
