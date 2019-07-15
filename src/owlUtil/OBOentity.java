package owlUtil;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/** 
 * 	Annotation information of an OWLentity including rdfs:label using OWL API 4.2 
 *
 *  @author Jie Zheng
 *  University of Pennsylvania <br>
 * 	Date: May-16-2016 <br>
 */

public class OBOentity {	
	public static ArrayList<String> getStringArrayAnnotProps (OWLEntity cls, OWLDataFactory df, OWLOntology ont, OWLAnnotationProperty annotProp){
		ArrayList<String> propVals = new ArrayList<String>();
		
		for(OWLAnnotation annotation : EntitySearcher.getAnnotations(cls, ont, annotProp)) {
    		if (annotation.getValue() instanceof OWLLiteral) {
    			OWLLiteral val = (OWLLiteral) annotation.getValue();
    			propVals.add(val.getLiteral());
    		}
    	}
 	
		return propVals;
	}

	public static String getStringAnnotProps (OWLEntity cls, OWLDataFactory df, OWLOntology ont, OWLAnnotationProperty annotProp){
		String propStr = "";
		
		for(OWLAnnotation annotation : EntitySearcher.getAnnotations(cls, ont, annotProp)) {
		    OWLAnnotationValue val = annotation.getValue();
		    if(val instanceof OWLLiteral) {
		    	String strVal = ((OWLLiteral)val).getLiteral();
		    	if (strVal.length()>0) {
    				if (propStr.length() > 0) propStr = propStr + ", " + strVal; 
    				else propStr = strVal;
    			}
		    }
    	}
 	
		return propStr;
	}	
	
	public static ArrayList<IRI> getIRIAnnotProps (OWLEntity cls, OWLDataFactory df, OWLOntology ont, OWLAnnotationProperty  annotProp){
		ArrayList<IRI> propVals = new ArrayList<IRI>();
		
		for(OWLAnnotation annotation : EntitySearcher.getAnnotations(cls, ont, annotProp)) {
    		if (annotation.getValue() instanceof IRI) {
    			IRI val = (IRI) annotation.getValue();
    			propVals.add(val);
    		}
    	}
    	
		return propVals;
	}
	
	public static String getLabel (OWLEntity cls, OWLOntology ont, OWLDataFactory df) {
		String label = "";

		OWLAnnotationProperty annotProp = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		for(OWLAnnotation annotation : EntitySearcher.getAnnotations(cls, ont, annotProp)) {
			label = ((OWLLiteral) annotation.getValue()).getLiteral();
		}
		
		if (label.length()==0) {
			String IRIstr = cls.getIRI().toString();
			String[] parts = IRIstr.split("#");
			if (parts.length > 1) {
				label = parts[parts.length-1];
			}
		}
		
		return label;
	}
	
	
	public static String getRDFsLabel (OWLEntity cls, OWLOntology ont, OWLDataFactory df) {
		String label = "";

		OWLAnnotationProperty annotProp = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		for(OWLAnnotation annotation : EntitySearcher.getAnnotations(cls, ont, annotProp)) {
			label = ((OWLLiteral) annotation.getValue()).getLiteral();
		}
		
		return label;
	}
	
	public static boolean isObsolete (OWLEntity cls, OWLOntology ont, OWLDataFactory df) {
		boolean is_obsolete = false;
		OWLAnnotationProperty annotProp = df.getOWLAnnotationProperty(IRI.create(Config.Deprecated_AnnotPorp));
		
		for(OWLAnnotation annotation : EntitySearcher.getAnnotations(cls, ont, annotProp)) {
    		if (annotation.getValue() instanceof OWLLiteral) {
    			OWLLiteral ol = (OWLLiteral) annotation.getValue();
    			if (ol.isBoolean()) {
    				is_obsolete = ol.parseBoolean();
    			}    			
    		}
		}
		
		return is_obsolete;
	}
	
	public static OWLLiteral getLabelOWLLiteral (OWLEntity cls, OWLOntology ont, OWLDataFactory df) {
		OWLLiteral labelOWLLiteral = null;

		OWLAnnotationProperty annotProp = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		for(OWLAnnotation annotation : EntitySearcher.getAnnotations(cls, ont, annotProp)) {
    		if (annotation.getValue() instanceof OWLLiteral) {
    			labelOWLLiteral = (OWLLiteral) annotation.getValue();
    		}
		}
		return labelOWLLiteral;
	}
	
	// get ontology term ID based on IRI using OBO Foundry and Bioportal format
	// 	- OBO Foundry IRI format is: http://.+[/|#])([A-Za-z_]{2,10}_[0-9]{1,9}
	// 	- Bioportal IRI format is: http://purl.bioontology.org/ontology/DOMAIN/ID, parsed ID as: DOMAIN_ID
	// 	- Other format using any string after char '#'
	public static String getID (String iriStr) {
		String id = iriStr;
		String idPattern = "^(http://.+[/|#])([A-Za-z_]{2,10}_[0-9]{1,9})$";
		Pattern oboIdPattern = Pattern.compile(idPattern);
		
		Matcher m = oboIdPattern.matcher(iriStr);
		if (m.find()) {
			id = m.group(2);
		} else if (iriStr.startsWith("http://purl.bioontology.org/ontology/")) {
			id = iriStr.replace("http://purl.bioontology.org/ontology/", "");
			id = id.replace("/", "_");		
		} else if (iriStr.lastIndexOf('#') > 0) {
			int index = iriStr.lastIndexOf('#');
			if (index < iriStr.length())	id = iriStr.substring(iriStr.lastIndexOf('#')+1);	
		}
		
		return id;
	}
	
	// generate IRI for a new entity, ID with 7 digits 
	public static String getNewEntityIRI (String baseIRI, String prefix, int number) {
		// 7 digits ID
		String s = "0000000" + number; 
					
		return baseIRI + prefix + "_" + s.substring(s.length()-7);
	}
}
