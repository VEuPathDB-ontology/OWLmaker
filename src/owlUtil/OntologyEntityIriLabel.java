package owlUtil;

import java.util.Enumeration;
import java.util.Hashtable;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/*
 * This class will pull out the Label and IRI mappings from an ontology. It is easier to handle the input that entity IRIs are not provided and 
 * entity labels are used.
 *  
 * The label and IRI mappings can be list based on the entity type: class, annotation property and object property.
 * 
 * The label is rdfs:label or last part of the entity IRI when rdfs:label is not available. When entity doesn't have rdfs:label, warning message will be given.
 * 
 * The entities that shared labels will be recorded. For any redundant label used by the entity, the one that find first will be provided.
 * 
 * The entities without labels will also be identified. (include IRI and entityType information) 
 */

public class OntologyEntityIriLabel {
	// all entities IRI and Labels in the ontology
	private Hashtable<String,String> entityObjects;
	private Hashtable<String,String> classObjects;
	private Hashtable<String,String> objectPropObjects;
	private Hashtable<String,String> annotationPropObjects;	
	private Hashtable<String,String> redundantLabelObjects;
	private Hashtable<String,String> noLabelObjects;
	
	public OntologyEntityIriLabel() {
		this.entityObjects = new Hashtable<String,String>();	
		this.classObjects = new Hashtable<String,String>();
		this.objectPropObjects = new Hashtable<String,String>();
		this.annotationPropObjects = new Hashtable<String,String>();
		this.redundantLabelObjects = new Hashtable<String,String>();
		this.noLabelObjects = new Hashtable<String,String>();
	}
	
	public OntologyEntityIriLabel(OWLOntologyManager manager, OWLOntology ont) {
		this.entityObjects = new Hashtable<String,String>();	
		this.classObjects = new Hashtable<String,String>();
		this.objectPropObjects = new Hashtable<String,String>();
		this.annotationPropObjects = new Hashtable<String,String>();
		this.redundantLabelObjects = new Hashtable<String,String>();
		this.noLabelObjects = new Hashtable<String,String>();
		
		setEntityObjects (manager, ont);
	}	
	
	public void setEntityObjects (OWLOntologyManager manager, OWLOntology ont) {
		OWLDataFactory df = manager.getOWLDataFactory();
		
		for (OWLEntity ent : ont.getSignature()) {
			String entityIRIstr = ent.getIRI().toString();
			String label = OBOentity.getLabel(ent, ont, df).toLowerCase();
			String rdfsLabel = OBOentity.getRDFsLabel(ent, ont, df).toLowerCase();
			
			if (rdfsLabel.length() == 0) {
				this.noLabelObjects.put(entityIRIstr, ent.getEntityType().toString());
				System.out.println("WARNING: entity, " + entityIRIstr + ", has no rdfs:label annotation.");
			}
			
			if (label.length() > 0) {
				// the label already used for another entity
				if (this.entityObjects.containsKey(label)) {
					addRedundantLabelObjects(entityObjects, label, entityIRIstr);
					System.out.println("WARNING: Duplicate label - " + label);
				} else {
					this.entityObjects.put(label, entityIRIstr);
				}
				
				// add label and IRI mapping based on entity type
				if (ent.isOWLClass() && !this.classObjects.containsKey(label)) 							this.classObjects.put(label, entityIRIstr);
				if (ent.isOWLObjectProperty() && !this.objectPropObjects.containsKey(label)) 			this.objectPropObjects.put(label, entityIRIstr);
				if (ent.isOWLAnnotationProperty() && !this.annotationPropObjects.containsKey(label)) 	this.annotationPropObjects.put(label, entityIRIstr);
			} else {
				System.out.println("WARNING: entity, " + entityIRIstr + ", has no label and is not included in the label, IRI mapping list.");
			}
		}
	}
	
	private void addRedundantLabelObjects(Hashtable<String,String> mappings, String label, String iriStr) {
		// the redundant information have been recorded, add current entity IRI
		if (this.redundantLabelObjects.containsKey(label)) {
			this.redundantLabelObjects.put(label, this.redundantLabelObjects.get(label) + ";" + iriStr);					
		} // record non-unique label and their IRIs
		else {
			String existIRIstr = mappings.get(label);
			this.redundantLabelObjects.put(label, existIRIstr + ";" + iriStr);	
		}
	}
	
	public void setClassObjects (OWLOntologyManager manager, OWLOntology ont) {
		OWLDataFactory df = manager.getOWLDataFactory();
		
		for (OWLClass cls : ont.getClassesInSignature()) {
			String clsIRIstr = cls.getIRI().toString();
			String label = OBOentity.getLabel(cls, ont, df).toLowerCase();
			String rdfsLabel = OBOentity.getRDFsLabel(cls, ont, df).toLowerCase();
			
			if (rdfsLabel.length() == 0) {
				if (!this.noLabelObjects.containsKey(clsIRIstr))	this.noLabelObjects.put(clsIRIstr, cls.getEntityType().toString());
				System.out.println("WARNING: class, " + clsIRIstr + ", has no rdfs:label annotation.");
			}
			
			if (label.length() > 0) {
				// the label already used for another entity
				if (this.classObjects.containsKey(label)) {
					addRedundantLabelObjects(classObjects, label, clsIRIstr);					
				} else {
					this.classObjects.put(label, clsIRIstr);
				}
			} else {
				System.out.println("WARNING: class, " + clsIRIstr + ", has no label and is not included in the label, IRI mapping list.");
			}
		}		
	}	

	public void setObjectPropObjects (OWLOntologyManager manager, OWLOntology ont) {
		OWLDataFactory df = manager.getOWLDataFactory();
		
		for (OWLObjectProperty objectProp : ont.getObjectPropertiesInSignature()) {
			String objectPropIRIstr = objectProp.getIRI().toString();
			String label = OBOentity.getLabel(objectProp, ont, df).toLowerCase();
			String rdfsLabel = OBOentity.getRDFsLabel(objectProp, ont, df).toLowerCase();
			
			if (rdfsLabel.length() == 0) {
				if (!this.noLabelObjects.containsKey(objectPropIRIstr))	this.noLabelObjects.put(objectPropIRIstr, objectProp.getEntityType().toString());
				System.out.println("WARNING: objectProperty, " + objectPropIRIstr + ", has no rdfs:label annotation.");
			}
			
			if (label.length() > 0) {
				// the label already used for another entity
				if (this.objectPropObjects.containsKey(label)) {
					addRedundantLabelObjects(objectPropObjects, label, objectPropIRIstr);					
				} else {
					this.objectPropObjects.put(label, objectPropIRIstr);
				}
			} else {
				System.out.println("WARNING: objectProperty, " + objectPropIRIstr + ", has no label and is not included in the label, IRI mapping list.");
			}
		}		
	}
	
	public void setAnnotationPropObjects (OWLOntologyManager manager, OWLOntology ont) {
		OWLDataFactory df = manager.getOWLDataFactory();
		
		for (OWLAnnotationProperty annotProp : ont.getAnnotationPropertiesInSignature()) {
			String annotPropIRIstr = annotProp.getIRI().toString();
			String label = OBOentity.getLabel(annotProp, ont, df).toLowerCase();
			String rdfsLabel = OBOentity.getRDFsLabel(annotProp, ont, df).toLowerCase();
			
			if (rdfsLabel.length() == 0) {
				if (!this.noLabelObjects.containsKey(annotPropIRIstr))	this.noLabelObjects.put(annotPropIRIstr, annotProp.getEntityType().toString());
				System.out.println("WARNING: annotationProperty, " + annotPropIRIstr + ", has no rdfs:label annotation.");
			}
			
			if (label.length() > 0) {
				// the label already used for another entity
				if (this.annotationPropObjects.containsKey(label)) {
					addRedundantLabelObjects(annotationPropObjects, label, annotPropIRIstr);					
				} else {
					this.annotationPropObjects.put(label, annotPropIRIstr);
				}
			} else {
				System.out.println("WARNING: annotationProperty, " + annotPropIRIstr + ", has no label and is not included in the label, IRI mapping list.");
			}
		}		
	}	
	
	// get all entities' IRIs and Labels in an ontology
	public Hashtable<String,String> getEntityObjects () {
		return this.entityObjects;
	}
	
	// get all classes' IRIs and Labels in an ontology
	public Hashtable<String,String> getClassObjects () {
		return this.classObjects;
	}
	
	// get all object properties' IRIs and Labels in an ontology
	public Hashtable<String,String> getObjectPropObjects () {
		return this.objectPropObjects;
	}
	
	// get all annotation properties' IRIs and Labels in an ontology
	public Hashtable<String,String> getAnnotationPropObjects () {
		return this.annotationPropObjects;
	}

	// get IRI(s) which have common label in an ontology
	public Hashtable<String,String> getNoLabelObjects () {
		return this.noLabelObjects;
	}
	
	public Hashtable<String,String> getRedundantLabelObjects () {
		return this.redundantLabelObjects;
	}
	
	public boolean hasRedundantLabelEntities () {
		boolean redundant = (this.redundantLabelObjects.size() != 0);

		return redundant;
	}
	
	// check whether a label used by more than one entities
	// Note: Some entities without label and the part of IRI string after '#' char is set as label.   
	public boolean isRedundantLabel (String label) {
		return this.redundantLabelObjects.containsKey(label);
	}
	
	public static String findKeyByValue (Hashtable<String,String> pairs, String value) {
		
		if (pairs.contains(value)) {
			Enumeration<String> keys = pairs.keys();
		
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
			
				if (value.toLowerCase().equals(pairs.get(key).toLowerCase()))	return key;
			}	
		}
		
		return null;
	}
}
