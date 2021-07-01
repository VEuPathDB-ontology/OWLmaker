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
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class OntologyEditor {
	// add an instance, specify its class type and add label annotation if available
	public static void addInstance(OWLOntologyManager manager, OWLOntology ontology, String instanceIRIstr, String classIRIstr, String instanceLabel) {
 		// Create factory to obtain a reference to a class
        OWLDataFactory df = manager.getOWLDataFactory();

        // Create instance
		OWLNamedIndividual ins = df.getOWLNamedIndividual(IRI.create(instanceIRIstr));
		
		// Add asserted class type
        OWLClass cls = df.getOWLClass(IRI.create(classIRIstr));
        OWLClassAssertionAxiom classTypeAxiom = df.getOWLClassAssertionAxiom(cls, ins);
        manager.applyChange(new AddAxiom(ontology, classTypeAxiom));
        
        // Add label if it is not null
        if (instanceLabel.length() > 0 ) {
    		OWLAnnotationProperty labelProp = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
    		OWLAnnotation labelAnnot = df.getOWLAnnotation(labelProp, df.getOWLLiteral(instanceLabel));
    		OWLAxiom labelAxiom = df.getOWLAnnotationAssertionAxiom(IRI.create(instanceIRIstr), labelAnnot);
    		manager.applyChange(new AddAxiom(ontology, labelAxiom));
        }
	}
	
	// add relation between two instances
	public static void addRelation (OWLOntologyManager manager, OWLOntology ontology, String subjectIRIstr, String objectIRIstr, String objectPropIRIstr) {
 		// Create factory to obtain a reference to a class
        OWLDataFactory df = manager.getOWLDataFactory();
		OWLNamedIndividual subject = df.getOWLNamedIndividual(IRI.create(subjectIRIstr));
		OWLNamedIndividual object = df.getOWLNamedIndividual(IRI.create(objectIRIstr));
		OWLObjectProperty predicate = df.getOWLObjectProperty(IRI.create(objectPropIRIstr));
		OWLObjectPropertyAssertionAxiom propAssertion = df.getOWLObjectPropertyAssertionAxiom(predicate, subject, object);
		manager.applyChange(new AddAxiom(ontology,propAssertion));
	}

	// update annotation properties
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

