package ontoTool;

import org.kohsuke.args4j.Option;

/**
 *  Process arguments passed from command line for ClassWithOntologicalAnnotationGenerator.java
 *
 *  @author Jie Zheng
 */
public class OntologyClassGeneratorOptions {
    @Option(name="-settingFilename", usage ="the configuration used to set all variables required for the OWL conversion", required = false)
    private String settingFilename = "./test/setting.txt";
     
    public String getSettingFilename () {
    	return this.settingFilename;
    }
}
