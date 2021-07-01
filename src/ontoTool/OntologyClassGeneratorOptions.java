package ontoTool;

import org.kohsuke.args4j.Option;

/**
 *  Process arguments passed from command line for ClassWithOntologicalAnnotationGenerator.java
 *
 *  @author Jie Zheng
 */
public class OntologyClassGeneratorOptions {
    @Option(name="-settingFilename", usage ="the configuration used to set all variables required for the OWL conversion", required = false)
    private String settingFilename = "/Users/jiezheng/Documents/VEuPathDB-git/ApiCommonData/Load/ontology/script/test-local/setting_test.txt";
    
    //private String settingFilename = "/Users/jiezheng/Documents/ontology/DTO/proteoCommons/class/setting.txt";
 
    public String getSettingFilename () {
    	return this.settingFilename;
    }
}
