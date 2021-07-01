package ontoTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import com.opencsv.CSVReader;
import com.monitorjbl.xlsx.StreamingReader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import owlUtil.Config;
import owlUtil.OBOentity;
import owlUtil.OntologyEntityIriLabel;
import owlUtil.OntologyManipulator;

/** 
 * 	Read tab-delimited file (.txt) or Excel file (.xlsx), convert terms associated with their annotations in OWL format
 *  
 *  Argument: a setting file that contains all needed information for OWL maker.
 *  
 *  @author Jie Zheng
 */

public class OntologyClassGenerator {
	public static final String CNAME_IRI = "iri";
	public static final String CNAME_LABEL = "label";
	public static final String CNAME_PARENT_IRI = "parentiri";
	public static final String CNAME_PARENT_LABEL = "parentlabel";
			
	public static void main(String[] args) {
		OntologyClassGeneratorOptions bean = new OntologyClassGeneratorOptions();
	    CmdLineParser parser = new CmdLineParser(bean);

	    try {
	        parser.parseArgument(args);
	    } catch( CmdLineException e ) {
	        System.err.println(e.getMessage());
	        parser.printUsage(System.err);
	        System.exit(1);
	    }

		String settingFilename = bean.getSettingFilename();

		
		// --------------------------------------------------------
		// set variables from giving configuration file
		// --------------------------------------------------------
		OntologyClassGeneratorConfiguration config = new OntologyClassGeneratorConfiguration();
		config.parseSetting(settingFilename);
		
		// path of input file
		String path = config.getPath();
		
		// tab-delimited file for conversion
		String inputFilename = config.getInputFilename();
		
		// ontology IRI of converted OWL file
		String ontoIRIstr = config.getOntologyIRIstr();
		
		// output converted OWL file name
		String outputFilename = config.getOutputFilename();
		
		// base URL of new term IRIs
		String idBase = config.getIdBase();
		
		// prefix for new term ID
		String prefix = config.getPrefix();
		
		// start ID number
		int startNum = config.getStartNum();
		
		// external ontology as reference of existing ontology class and annotation property
		String externalOntologyFilename = config.getExternalOntologyFilename();
		
		// labels/IRI of annotation properties that will be used in conversion
		ArrayList<String> annotLabels = config.getAnnotLabels();
	
		
		// --------------------------------------------------------
		// load input file into a matrix for conversion
		// --------------------------------------------------------

		ArrayList <String[]> matrix = null;
		
		if (inputFilename.endsWith(".txt")) matrix = readTabFile(path + inputFilename);
		else if (inputFilename.endsWith(".csv")) matrix = readCSVFile(path + inputFilename);		
		else if (inputFilename.endsWith(".xlsx")) matrix = readExcelFile(path + inputFilename);
		else {
			System.out.println("The script can only handel .txt, .csv and .xlsx file");
			System.exit(0);
		}

    	// create an ontology OWL file
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();	
        OWLDataFactory df = manager.getOWLDataFactory();
		OWLOntology outOWL = OntologyManipulator.create(manager, ontoIRIstr);       

		// ontology entity label and corresponding IRI, the label should be unique.
		Hashtable<String, String> labelIriObjects = new Hashtable<String, String>();

        //Load external ontology for existing entity label and IRI mappings
		if (externalOntologyFilename.length() > 0) {
			OWLOntology externalOnt = OntologyManipulator.load(externalOntologyFilename, manager);
			
			//get class and annotation property label and IRI mapping in the external ontology
			OntologyEntityIriLabel labelIriFinder = new OntologyEntityIriLabel();
			
			// get annotation property label and IRI mapping in the external ontology
			labelIriFinder.setAnnotationPropObjects(manager, externalOnt);
			labelIriObjects.putAll(labelIriFinder.getAnnotationPropObjects());
			labelIriFinder.setClassObjects(manager, externalOnt);
			labelIriObjects.putAll(labelIriFinder.getClassObjects());
		}
    	
        // Create annotation properties, and save annotation label, IRI in the hashtable
        for(int i = 0; i < annotLabels.size(); i++) {
        	String annotLabel = annotLabels.get(i);
        	String annotIriStr = "";
        	
        	// check whether annotation property IRI provided 
        	if (annotLabel.contains("|")) {
         		String[] aa = annotLabel.split("\\|"); 
        		
        		if (aa.length == 2) {
        			annotLabel = cleanString(aa[0]);
        			annotIriStr = cleanString(aa[1]);
        			if (!annotIriStr.isEmpty() && !annotIriStr.contains("http")) annotIriStr = Config.OBO_Base + annotIriStr;
        		} else annotLabel = cleanString(aa[0]);
        	} 
        	
        	if (annotIriStr.isEmpty()) {
        		if (labelIriObjects.containsKey(annotLabel.toLowerCase()))	annotIriStr = labelIriObjects.get(annotLabel);
        		else {
        			annotIriStr = OBOentity.getNewEntityIRI (idBase, prefix, startNum);
        			startNum ++;
        		}
        	}
        	     	
			// add new annotation in the newly created ontology
			OWLAnnotationProperty annotProp = df.getOWLAnnotationProperty(IRI.create(annotIriStr));        	
			OWLAnnotationProperty labelAnnotProp = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
			OWLAnnotation labelAnnot = df.getOWLAnnotation(labelAnnotProp, df.getOWLLiteral(annotLabel));
			OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(annotProp.getIRI(), labelAnnot);
			manager.applyChange(new AddAxiom(outOWL, axiom));  
			System.out.println("Add annotation property: " + annotLabel + ", " + annotIriStr);
        	
    		labelIriObjects.put(annotLabel.toLowerCase(), annotIriStr);
        }

		//annotation Property IRI and its index in the input file  
		Hashtable<Integer, String> annotProps = new Hashtable<Integer, String>(); 				
		
		// index for term label, term IRI, term parent label or IRI in the tab-delimited file
		int iriPos = -1;
		int labelPos = -1;
		int parentIriPos = -1;
		int parentPos = -1;
		
        // Find term and its parent IRI and label position, and annotation property index from header of input file, any column name is not specified as
        // annotation property in the setting file will be skipped
        String[] items = matrix.get(0);
        
        for (int k = 0; k< items.length; k++) {
        	String item = items[k];
        	
        	if (item != null && !item.isEmpty()) {
        		if (item.toLowerCase().equals(CNAME_IRI))					iriPos = k;
        		else if (item.toLowerCase().equals(CNAME_LABEL))			labelPos = k;
        		else if (item.toLowerCase().equals(CNAME_PARENT_IRI))		parentIriPos = k;
        		else if (item.toLowerCase().equals(CNAME_PARENT_LABEL))		parentPos = k;
        		else if (labelIriObjects.containsKey(item.toLowerCase()))	annotProps.put(new Integer(k), labelIriObjects.get(item.toLowerCase()));
        	}
		}
        
        // create classes
	   	for (int i = 1; i < matrix.size(); i++) {
	   		items = matrix.get(i);

	   		String termLabel 	= "";
	   		String termIRIstr 	= "";
	   		String parentLabel 	= "";
	   		String parentIRIstr	= "";
	   		
	   		// -- get term label and IRIstr --
	   		// Term label must be provided in the input file
	   		if (labelPos > -1 && labelPos < items.length)	termLabel = items[labelPos];
	   		else {
	   			System.out.println("ERROR:The term labels are not provided in the input file. row = "+i + "\nlabelPos " + labelPos);
	   			System.exit(0);
	   		}
	   		
	   		// Term IRI string is optional
	   		// If Term IRI provide, get from the input file and save in the hashtable, it may be reused 
	   		if (iriPos > -1 && iriPos < items.length)	{
	   			termIRIstr = items[iriPos];
	   			if (!termIRIstr.isEmpty())	labelIriObjects.put(termLabel.toLowerCase(), termIRIstr);
	   		} 
	   		
	   		// if term IRI not provide (i.e. mapped to existing ontology term), check whether it has been created
	   		// if new term IRI does not exist, will create a new IRI
	   		if (termIRIstr.isEmpty() && !termLabel.isEmpty()) {				
	   			if (labelIriObjects.containsKey(termLabel.toLowerCase()))	{
	   				termIRIstr = labelIriObjects.get(termLabel.toLowerCase());
	   			}
	   			else {
	   				termIRIstr = OBOentity.getNewEntityIRI (idBase, prefix, startNum);
	   				startNum ++;
	   				labelIriObjects.put(termLabel.toLowerCase(), termIRIstr);
	   				System.out.println("-- Add new class: " + termLabel);
	   			}
	   		}
	   		
	   		if (termIRIstr.isEmpty()) {
	   			System.out.println("Reading in Line: " + i + ", does not provide either term label or term IRI. The class cannot be added to the ontology.");
	   		}	   		
	   		// create owl class
	   		else {
	   			OWLClass cls = df.getOWLClass(IRI.create(termIRIstr));

	   			// get term parent label from the input file
	   			if (parentPos > -1 && parentPos < items.length)	{
	   				parentLabel = items[parentPos];
	   				//System.out.println("parent: " + parentLabel);
	   			}
	   		
	   			//  get term parent IRI from the input file
	   			if (parentIriPos > -1 && parentIriPos < items.length)	{
	   				// System.out.println("parent IRI pos: " + parentIriPos);
	   				parentIRIstr = items[parentIriPos];
	   				if (!parentIRIstr.isEmpty())	labelIriObjects.put(parentLabel.toLowerCase(), parentIRIstr);
	   				else parentIRIstr = "http://www.w3.org/2002/07/owl#Thing";
	   				//System.out.println("parent IRI: " + parentIRIstr);
	   			} 
	   			// figure out parent IRI based on its label, if parent term label is not provide, set it as subClassOf OWL:Thing
	   			else {
	   				if (!parentLabel.isEmpty()) {
	   					//System.out.println("Parent: " + parentLabel);
	   					if (labelIriObjects.containsKey(parentLabel.toLowerCase()))	{
	   						parentIRIstr = labelIriObjects.get(parentLabel.toLowerCase());
	   						//System.out.println("find parent exists");
	   					}
	   					else {
	   						System.out.println("Cannot find parent: " + parentLabel);
	   						parentIRIstr = OBOentity.getNewEntityIRI (idBase, prefix, startNum);
	   						startNum ++;
	   						labelIriObjects.put(parentLabel.toLowerCase(), parentIRIstr);
	   					}
	   				} else {
	   					parentIRIstr = "http://www.w3.org/2002/07/owl#Thing";
	   				}
	   			}
	   			//System.out.println("term label: " + termLabel);
	   			//System.out.println("parent Label: " + parentLabel);
	   			//System.out.println("parent IRI: " + parentIRIstr);
	   			
	   			// add class parent
	   			OWLClass parent = df.getOWLClass(IRI.create(parentIRIstr));	   		
	   			OWLAxiom axiom = df.getOWLSubClassOfAxiom(cls, parent);
	   			manager.applyChange(new AddAxiom(outOWL, axiom));
	   		
	   			// add term label
	   			OWLAnnotationProperty labelProp = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	   			OWLAnnotation label = df.getOWLAnnotation(labelProp, df.getOWLLiteral(termLabel));
	   			axiom = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), label);
	   			manager.applyChange(new AddAxiom(outOWL, axiom));
   	    
	   			// add annotation properties other than label
	   			Set<Integer> keys = annotProps.keySet();
        	
	   			for(Integer key: keys){
	   				int annotPos = key.intValue();
	   				if (annotPos < items.length && items[annotPos].length() > 0) {
	   					OWLAnnotationProperty annotProp = df.getOWLAnnotationProperty(IRI.create(annotProps.get(key)));
	   					
	   					String annotVals[] = items[annotPos].split("\\|");
	   					
	   					for (int m = 0; m < annotVals.length; m ++) {
	   						OWLAnnotation annot = df.getOWLAnnotation(annotProp, df.getOWLLiteral(annotVals[m].trim()));
	   						axiom = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), annot);
	   						manager.applyChange(new AddAxiom(outOWL, axiom));
	   					}
	   				}
	   			}
	   		}
	   	}

	   	OntologyManipulator.saveToFile(manager, outOWL, path + outputFilename);
	}
	
	// The Excel package does not handle empty cells correctly
	public static ArrayList <String[]> readExcelFile (String inputFile) {
		System.out.println("Input is an .xlsx Excel file");

		ArrayList <String[]> matrix = new ArrayList <String[]> ();
		
		InputStream is = null;
		try {
			is = new FileInputStream(new File(inputFile));
			Workbook workbook = StreamingReader.builder()
		        .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
		        .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
		        .open(is);            // InputStream or File for XLSX file (required)
			
			for (Sheet sheet : workbook){
				// System.out.println(sheet.getSheetName());
				for (Row r : sheet) {
					ArrayList<String> values = new ArrayList<String>();
					for (Cell c : r) {
						if (c.getStringCellValue().trim().length() == 0) {
							values.add("");
						} else {
							values.add(cleanString(c.getStringCellValue()));
						}
					}
					String[] items = new String[values.size()];
					
					for(int m = 0; m < values.size(); m++) {
						items[m] = values.get(m);
					}
					if (!items[0].startsWith("##"))	matrix.add(items);
				}
			}
			System.out.println("Successfully read input text file: " + inputFile);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if ( is != null ) is.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}			

		return matrix;
	}
	
	public static ArrayList <String[]> readTabFile (String inputFile) {
		System.out.println("Input is a tab-delimited file");

		ArrayList <String[]> matrix = new ArrayList <String[]> ();
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(inputFile));
			String line = null;

			while( (line = br.readLine()) != null)
			{
				// comment line start with ##, will be ignored
				if (!(line.trim().startsWith("##") || line.trim().length()==0)) {
					String[] items = line.split("\t");
					for (int i = 0; i<items.length; i++)	items[i] = cleanString(items[i]);
					matrix.add(items);
				}
			}
			System.out.println("Successfully read input text file: " + inputFile);
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

		return matrix;
	}	

	@SuppressWarnings("resource")
	public static ArrayList <String[]> readCSVFile (String inputFile) {
		System.out.println("Input is a csv file");

		ArrayList <String[]> matrix = new ArrayList <String[]> ();
		
		try {
            	CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(inputFile), StandardCharsets.UTF_8));

            	String[] row;
            	while ((row = csvReader.readNext()) != null) {  
            		for (int i = 0; i<row.length; i++)	row[i] = cleanString(row[i]);
            		matrix.add(row);
            	}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return matrix;
	}	
	
	public static void printHashtable (Hashtable<String,String> pairs, String message) {
		if (pairs.isEmpty()) {
			System.out.println("No " + message);
		} else {
			System.out.println(message);

			Enumeration<String> keys = pairs.keys();
			
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
				System.out.println(key + ", " + pairs.get(key));
			}
		}
	}
	
	public static String cleanString (String s) {
		s = s.trim().replaceAll("^\"|\"$", "");	
		
		if (s.toUpperCase().equals("NA"))	s = "";

		return s;
	}
}
