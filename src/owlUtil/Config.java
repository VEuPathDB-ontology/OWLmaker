package owlUtil;

/**
 *  IRIs of ontology metadata annotation properties
 *   
 *  @author Jie Zheng
 *  University of Pennsylvania <br>
 * 	Date: May-12-2015 <br>
 */

public final class Config {
	// Base IRI of OBO Foundry ontologies
	public static final String OBO_Base = "http://purl.obolibrary.org/obo/";
	
	// annotation properties of ontology metadata defined in IAO
	public static final String DEF_AnnotProp = "http://purl.obolibrary.org/obo/IAO_0000115";
	public static final String SYN_AnnotProp = "http://purl.obolibrary.org/obo/IAO_0000118";
	public static final String FGED_SYN_AnnotProp = "http://purl.obolibrary.org/obo/OBI_9991119";
	public static final String IMPORT_FROM_AnnotProp = "http://purl.obolibrary.org/obo/IAO_0000412";
	public static final String PREFERRED_TERM_AnnotPorp = "http://purl.obolibrary.org/obo/IAO_0000111";
	public static final String Deprecated_AnnotPorp =  "http://www.w3.org/2002/07/owl#deprecated";
	public static final String DEFSOURCE_AnnotProp = "http://purl.obolibrary.org/obo/IAO_0000119";
	public static final String IAO_SYN_AnnotProp = "http://purl.obolibrary.org/obo/IAO_0000118";
	public static final String OBO_SYN_AnnotProp = "http://purl.obolibrary.org/obo#Synonym";
	public static final String BROAD_SYN_AnnotProp = "http://www.geneontology.org/formats/oboInOwl#hasBroadSynonym";
	public static final String EXACT_SYN_AnnotProp = "http://www.geneontology.org/formats/oboInOwl#hasExactSynonym";
	public static final String NARROW_SYN_AnnotProp = "http://www.geneontology.org/formats/oboInOwl#hasNarrowSynonym";
	public static final String RELATE_SYN_AnnotProp = "http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym";
	public static final String IEDB_SYN_AnnotProp = "http://purl.obolibrary.org/obo/OBI_9991118";
	public static final String EDITOR_AnnotPorp = "http://purl.obolibrary.org/obo/IAO_0000117";
	public static final String OBSOLETE_CLASS = "http://www.geneontology.org/formats/oboInOwl#ObsoleteClass";
	public static final String OBSOLETE_PROP = "http://www.geneontology.org/formats/oboInOwl#ObsoleteProperty";
	
	// annotation properties used by EFO
	public static final String EFO_SYN_AnnotProp = "http://www.ebi.ac.uk/efo/alternative_term";
	public static final String EFO_DEF_AnnotProp = "http://www.ebi.ac.uk/efo/definition";
	
	// annotation property defined in Eupath ontology used in terminology to specify 'ontological label' and 'ontological definition'
	// when default label and definition are used for user-preferred one
	public static final String ONTOLOGICAL_LABEL_AnnotProp = "http://purl.obolibrary.org/obo/EUPATH_0000266";
	public static final String ONTOLOGICAL_DEF_AnnotProp = "http://purl.obolibrary.org/obo/EUPATH_0000273";	
	public static final String DISPLAYORDER_AnnotProp = "http://purl.obolibrary.org/obo/EUPATH_0000274";
}
