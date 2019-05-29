# OWLmaker
### Convert tab delimited or csv file to OWL format file
## Run OWLmaker.jar
#### Requirement:
Java 1.7 and up
#### Command:
java -jar file_path/OWLmaker.jar -settingFilename file_path/setting_filename 
## Test files
Under test directory
#### Example:
java -jar OWLmaker.jar -settingFilename test/setting.txt 
(path to "test" directory must be set in setting.txt)
## Preparation of Setting File
The setting.txt file is tab-delimited and contains two columns, which specify the information needed to convert a tab-delimited or CSV file to OWL. The first column contains parameters (these should not be renamed) whose values can be added to the second column as follows:

#### path (required):
Full path to the location of file to be converted (the output file will also be placed here)
#### input file (required):
Name of file to be converted
#### output file (required):
name of output OWL file (warning: will overwrite if file with this name already exists)
#### ontology IRI (required):
IRI to attribute to OWL file
#### IRI base (required):
xml:base URI of the ontology
#### prefix (required)
IRI prefix for newly generated ontology terms
#### start ID (required)
Starting ID number for new ontology terms
#### external ontology file (optional)
External ontology used to find the ontology term IRI based on labels
#### term position (required)
Number of the column in the input file that contains the label for each term
#### term IRI position (required)
Number of the column in the input file that contains the label for each term (This column may contain some empty cells, in which case the script either gets the term IRI based on the labels in the external ontology or creates the IRI from the base, prefix, and start ID parameters.)
#### term parent position (required)
Number of the column that contains the label for the parent of each term in the same row (This column may contain some empty cells)
#### term parent IRI position (required)
Number of the column that contains the IRI for the parent of each term in the same row (This column may contain some empty cells, in which case the term will become an immediate child of owl:Thing.)
#### annotation property (optional, may have multiple rows)
Determines annotation property based on the column header in the input file that matches the parameter value. IRIs or IDs can be specified by appending "|" and the IRI (or ID) after the parameter value; if not specified, it will be generated from the base, prefix, and start ID parameters.

