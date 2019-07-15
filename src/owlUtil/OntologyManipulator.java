package owlUtil;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import com.google.common.base.Optional;

import java.io.File;
import java.util.Set;

/**
 * manipulate ontology, like creating, loading, merging and saving using OWL-API 4.2
 * get/edit metadata of ontology, like set ontologyID, get prefix namespace information, and add IRI mapping
 * 
 * Author: Jie Zheng <br>
 * University of Pennsylvania <br>
 * Date: May-12-2016 <br>
 */

public class OntologyManipulator {
	// load an ontology
	public static OWLOntology load (String str, OWLOntologyManager manager) 
	{
		OWLOntology ont = null;
		
		if (str.length() == 0) {
			System.out.println("Not specify the ontology location");
			return null;
		}
		
		if (new File(str).exists()) {
		    // load an ontology from a file
			ont = loadFromFile(str, manager);
		} else {
		    // load an ontology from URL
			ont = loadFromWeb(str, manager);
		}

		return ont;
	}
	
	// load an ontology from web
	public static OWLOntology loadFromWeb (String iri, OWLOntologyManager manager) 
	{
		OWLOntology ont = null;
		
		try {
            // load an ontology from the web
            ont = manager.loadOntologyFromOntologyDocument(IRI.create(iri));
            System.out.println("Loaded ontology: " + ont.getOntologyID().toString());  
        }
        catch (OWLOntologyCreationException e) {
            System.out.println("Could not load ontology: " + e.getMessage());
        }
        return ont;
	}

	// load an ontology from a local file but its imported owl files from web if there is any
	public static OWLOntology loadFromFile (String filename, OWLOntologyManager manager)
    {
		OWLOntology ont = null;
		
		try {
			ont = manager.loadOntologyFromOntologyDocument(new File(filename));
			System.out.println("Loaded ontology from local copy: " + ont.getOntologyID().toString() + " from a file: " + filename);
        }
        catch (OWLOntologyCreationException e) {
            System.out.println("Could not load ontology: " + e.getMessage());
        }
		
        return ont;
    }

	public static OWLOntology create (OWLOntologyManager manager, String iri) 
	{
		OWLOntology ont = null;
		
		try {
	    	ont = manager.createOntology(IRI.create(iri));
	    	System.out.println("A new ontology is created, IRI is: " + iri);
		} catch (OWLException e) {
			e.printStackTrace();
		}
		
		return ont;
	}

	public static OWLOntology createAnonymous (OWLOntologyManager manager) 
	{
		OWLOntology ont = null;
		
		try {
	    	ont = manager.createOntology();
	    	System.out.println("A new anonymous ontology is created");
		} catch (OWLException e) {
			e.printStackTrace();
		}
		
		return ont;
	}	
	
	public static OWLOntology createFromOWLontologies (OWLOntologyManager manager, String iri, Set<OWLOntology> onts) 
	{
		OWLOntology ont = null;
		
		try {
	    	ont = manager.createOntology(IRI.create(iri), onts);
	    	System.out.println("A new ontology is created and copied axioms from a set of given ontologies, IRI is: " + iri);
		} catch (OWLException e) {
			e.printStackTrace();
		}
		
		return ont;
	}
	
	public static void saveToFile(OWLOntologyManager manager, OWLOntology ont, String filename) {
	   try {
		   // By default ontologies are saved in the format from which they were loaded.  In this case the
		   // ontology was loaded from an rdf/xml file
	       // Get information about the format of an ontology from its manager		   
		   OWLDocumentFormat format = manager.getOntologyFormat(ont);
	       System.out.println("Save ontology in the format: " + format);
	
		   // Save a local copy of the ontology
		   manager.saveOntology(ont, IRI.create(new File(filename).toURI()));
				   
		   System.out.println("The ontology has been save on: " + filename);
	   }
	   catch (OWLOntologyStorageException e) {
		   System.out.println("Could not save ontology: " + e.getMessage());
	   }
	}
	
	public static OWLOntology merge (OWLOntologyManager manager, String mergeOntURIstr) {
		OWLOntology mergedOnt = null;
		
		OWLOntologyMerger merger = new OWLOntologyMerger(manager);
		
		try {
			mergedOnt = merger.createMergedOntology(manager, IRI.create(mergeOntURIstr));
		} catch (OWLOntologyCreationException e) {
            System.out.println("Could not create merged ontology: " + e.getMessage());
        }
		
		return mergedOnt; 
	}
	
	public static OWLOntology mergeToTargetOnt (OWLOntologyManager manager, OWLOntology targetOnt, OWLOntology ont) {
		Set<OWLAxiom> axs = ont.getAxioms();
		for(OWLAxiom ax : axs) {
	    	if (!targetOnt.containsAxiom(ax)) {
	        	manager.applyChange(new AddAxiom(targetOnt, ax));
	        }
	    }
		
		Optional<IRI> targetOntIRI = targetOnt.getOntologyID().getOntologyIRI();
		Optional<IRI> ontIRI = ont.getOntologyID().getOntologyIRI();
		
		System.out.println("All axioms in ontology " + ontIRI.toString() + " have been merged to the ontology " + targetOntIRI.toString());
		
		return targetOnt;
	}
	
	public static OWLOntology setOntologyID (OWLOntologyManager manager, OWLOntology ont, String newIRIstr) {
		String oldIRIstr = ont.getOntologyID().getOntologyIRI().toString();
		
		if (newIRIstr.equals(oldIRIstr)) {
			System.out.println("The new IRI: " + newIRIstr + " is same to the original one.");
		} else {
			SetOntologyID setOntoID =new SetOntologyID(ont,IRI.create(newIRIstr));
			manager.applyChange(setOntoID);
		}
		
		return ont;
	}
}