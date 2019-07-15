package owlUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

public class OntologyEditor {

	public static void cleanEmptyAnnotationFields() {
		
	}

	public static void addAnnotations(OWLOntologyManager manager, OWLOntology ontology, String annotPropIRI, HashMap<String, String> annotationList) {
		
	}
	
	public static void updateAnnotations(OWLOntologyManager manager, OWLOntology ontology, String annotPropIRI, HashMap<String, String> annotationList) {
 		// Create factory to obtain a reference to a class
        OWLDataFactory df = manager.getOWLDataFactory();

        // Create annotation property
	   	OWLAnnotationProperty annotProp = df.getOWLAnnotationProperty(IRI.create(annotPropIRI));
	   	
	   	
	   	for (String entityIRIstr : annotationList.keySet()) {
	   	    String annotVal = annotationList.get(entityIRIstr);
	   	    
	   	    if (ontology.containsClassInSignature(IRI.create(entityIRIstr))) {
	   	    	OWLClass cls = df.getOWLClass(IRI.create(entityIRIstr));
	   	    	
        		// remove specified annotation property if any
        		Collection<OWLAnnotation> owlAnnots = EntitySearcher.getAnnotations(cls, ontology, annotProp);
        		
        		if(!owlAnnots.isEmpty()) {
        			for (OWLAnnotation owlAnnot : owlAnnots) {
        				OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), owlAnnot);
        				manager.applyChange(new RemoveAxiom(ontology, ax));
        			}
        		}
        		
            	OWLAnnotation newAnnot = df.getOWLAnnotation(annotProp, df.getOWLLiteral(annotVal));
            	OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), newAnnot);
            	manager.applyChange(new AddAxiom(ontology, ax));
	   	    } else {
	   	    	System.out.println(entityIRIstr + " is not in the given ontology");
	   	    }
	   	}

	   	
		// go through each class in the ontology, if they are in the list for editing,
	   	// remove any associate annotation property value(s) of the class if there are any 
	   	// and add new annotation values to the class
	   	/*
	   	for (OWLClass cls : ontology.getClassesInSignature()) {	    	
	     	String termIRIstr = cls.getIRI().toString();

        	if (annotationList.containsKey(termIRIstr)) {
        		// remove specified annotation property if it has been defined
        		Collection<OWLAnnotation> owlAnnots = EntitySearcher.getAnnotations(cls, ontology, annotProp);
        		
        		if(!owlAnnots.isEmpty()) {
        			for (OWLAnnotation owlAnnot : owlAnnots) {
        				OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), owlAnnot);
        				manager.applyChange(new RemoveAxiom(ontology, ax));
        			}
        		}
        		
            	OWLAnnotation newAnnot = df.getOWLAnnotation(annotProp, df.getOWLLiteral(annotationList.get(termIRIstr)));
            	OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), newAnnot);
            	manager.applyChange(new AddAxiom(ontology, ax));
        	} 
        }
        */
	}
	
	// update IRIs based on mapping file
	public static void updateIRIs(OWLOntologyManager manager, Set<OWLOntology> ontologies, HashMap<String, String> mappingList) {
		OWLEntityRenamer renamer = new OWLEntityRenamer(manager, ontologies);
		
		// assign IDs to the newly added terms
    	Iterator<String> iterator = mappingList.keySet().iterator();
    	
    	while (iterator.hasNext()) {
    		String oldIRIstr = iterator.next();
    		String newIRIstr = mappingList.get(oldIRIstr);
    		
    		List<OWLOntologyChange> changes = renamer.changeIRI(IRI.create(oldIRIstr), IRI.create(newIRIstr));
    		
    		for (OWLOntologyChange change : changes) {
    			manager.applyChange(change);
    		}
    	}
	}
}

