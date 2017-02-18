# UMLify: Team FAD CSSE 374 Project

**F**red Zhang, **A**dam Finer, **D**avid Lam

[![build status](https://ada.csse.rose-hulman.edu/zhangq2/Fad/badges/master/build.svg)](https://ada.csse.rose-hulman.edu/zhangq2/Fad/commits/master) [![coverage report](https://ada.csse.rose-hulman.edu/zhangq2/Fad/badges/master/coverage.svg)](https://ada.csse.rose-hulman.edu/zhangq2/Fad/commits/master)

## Description:

This project takes a set of classes and draws the UML diagram for those set of classes. It has been design to be able to draw UML such that design patterns can be detected

## Command Line Usage:

### Usage:

```
$ java -cp "/path/to/otherProject.jar;/path/to/Fad-all-1.0.0-SNAPSHOT.jar" app.Application --config[-c] config.json [--param[-p]x=y]
```

### Arguments:

- `config.json`: A Configuration in JSON format that defines configuration settings for UMLify. It defines basic settings or can take customize input settings.
- `param x=y`: Overrides the setting `x` with the value `y`. This is useful for when you wish use the same configuration JSON but with slightly tweaked settings.

## Config JSON:
### Creating a Config JSON:
```
{
  "some_outer_identifier_1" : {
    "key_1" : "value_1",
    "key_2" : "value_2"
    ...
  },
  "some_outer_identifier_2" : {
    "key_1" : "value_3",
    "key_3" : "value_4"
    ...
  }
}
```

A key can be mapped to multiple things as long it is space separated. See [config](https://ada.csse.rose-hulman.edu/zhangq2/Fad/tree/master/config) for examples.

### Built-Ins
|      Identifier     |              Key             |         Default        | Description                                                           |
|:-------------------:|:----------------------------:|:----------------------:|:---------------------------------------------------------------------:|
| classParser         | header                       | GraphVizHeaderParser   | Qualified class name of the header parser                             |
|                     | field                        | GraphVizFieldParser    | Qualified class name of the field parser                              |
|                     | method                       | GraphVizMethodParser   | Qualified class name of the method parser                             |
|                     | type                         | GraphVizTypeParser     | Qualified class name of the type parser                               |
|                     | modifierParser               | GraphVizModifierParser | Qualified class name of the modifier parser                           |
| badDecorator        | fillColor                    | yellow                 | Fill color of the class record that is poorly decorated               |
|                     | parentStereotype             | component              | Class being decorated added stereotype                                |
|                     | childStereotype              | decorator              | Class decorator added stereotype                                      |
|                     | childParentRelationshipLabel | decorates              | Relationship label on the RelationHasA between child and parent.      |
| goodDecorator       | fillColor                    | blue                   | Fill color of the class record that is properly decorated             |
|                     | parentStereotype             | component              | Class being decorated added stereotype                                |
|                     | childStereotype              | decorator              | Class decorator added stereotype                                      |
|                     | childParentRelationshipLabel | decorates              | Relationship label on the RelationHasA between child and parent.      |
| adapter             | fillColor                    | red                    | Fill color of the class record that is in an adaptor pattern          |
|                     | parentStereotype             | target                 | Class being decorated added stereotype                                |
|                     | childStereotype              | adapter                | Class decorator added stereotype                                      |
|                     | adapteeStereotype            | adaptee                | Relationship label on the RelationHasA between child and parent.      |
|                     |childComposedRelationshipLabel| adapts                 | Relationship label on the RelationHasA between child and parent.      |
| dependencyInversion | whiteList                    | java                   | A list of packages that will be ignored if they trigger a violation   |
|                     | color                        | yellow                 | Outline color of the class record violating the dependency            |
| favorComposition    | color                        | orange                 | Outline color of the class record violating the dependency            |
| singleton           | color                        | blue                   | Outline color of the class record violating the dependency            |
| engine              | generator_key                | GraphVizGenerator      | Qualified class name of the header parser of graph generator          |
|                     | analyzer_key                 | RelationParserAnalyzer | Qualified class name of the analyzers to run (order-dependent)        |
| model               | isRecursive                  | false                  | Recursively parse relation if true                                    |
|                     | classes                      |                        | List of classes to parse                                              |
|                     | blackList                    |                        | List of classes to ignore while parsing.                              |
|                     | verbose                      | false                  | Print error messages of classes unable to parse                       |
| graphviz            | nodeSep                      | "1.0"                  | Node Separation factor for GraphViz                                   |
|                     | rankDir                      | BT                     | Rank direction preference                                             |
|                     | nodeStyle                    | node [shape=record]    | Universal Node styling                                                |
| runner              | outputFormat                 | svg                    | Output format of the generated Graph                                  |
|                     | outputDir                    | output                 | Output directory of all generated files                               |
|                     | executablePath               | dot                    | Graph Generator binary executable path                                |
|                     | fileName                     | output                 | Output file name of all generated files                               |

## How To Add Custom Pattern Analyzer:
To create a custom analyzer, you need to create a class the implements `IAnalyzer` and implement the `analyze` method. This method is used to scan the current system model and modify the styling of the classes if needed. If the class requires custom configurations, create a configuration that implements `Configurable`. This must implement the `setup` method. This method should be used to setup default values for the configuration. Here is an example:
```java
public class FavorCompositionConfiguration implements Configurable {
    public static final String CONFIG_PATH = "favorComposition.";                 // This should be the same as the outer identifier.
    public static final String COLOR = CONFIG_PATH + "color";                     // All subsequent field should use this CONFIG_PATH to define other variables.

    private IConfiguration config;                                                // It is a good idea to store the IConfiguration instance.
                                                                                  // IConfiguration stores a map of values and will be passed arround to all classes.

    @Override
    public void setup(IConfiguration config) {
        this.config = config;
        this.config.setIfMissing(FavorCompositionConfiguration.COLOR, "orange");  // setIfMissing and add are useful IConfiguration methods to add values into the IConfiguration
                                                                                  // conditionally.
    }

    public String getFavorComColor() {                                            // It is a good idea for a specific configuration to have a getter method for each static field type.
        return this.config.getValue(FavorCompositionConfiguration.COLOR);
    }
}
```
### How to add style in an analyzer

You can create an class the implments the following interface:
```java
public interface IAnalyzer {
    /**
     * Returns an Analyzed System Model.
     *
     * @param systemModel System Model to be analyzed.
     * @param config      IConfiguration object that the analyzer can retrieves
     *                    configurations from
     */
    void analyze(ISystemModel systemModel, IConfiguration config);
}
```
In an analyzer, you can decorate the classModel, filter out a uneeded methods. You can also add styles to the UML through method in IystemModel.
```java
  /**
     * Set the class set of systemModel
     *
     * @param classSet
     */
    void setClasses(Set<IClassModel> classSet);

    /**
     * Add node style to certain class,
     *
     * @param clazz
     *            the class to add style for
     * @param key
     *            the key of Graphviz style
     * @param value
     *            the value of Graphviz style
     */
    void addClassModelStyle(IClassModel clazz, String key, String value);

    /**
     * Add a stereotypes to class model If stereotype is an empty string, it
     * would ignore and discard it
     * 
     * @param clazz
     *            to add stereotype to
     * @param stereotype
     *            the stereotype String
     */
    void addClassModelSteretype(IClassModel clazz, String stereotype);

    /**
     * Add style to a relation
     *
     * @param from
     *            the edge from here
     * @param to
     *            the edge end here
     * @param relKey
     *            an key that identifies the type of relation. It should be an
     *            unique string
     * @param key
     *            the key of Graphviz style
     * @param value
     *            the value of Graphviz style
     */
    void addStyleToRelation(IClassModel from, IClassModel to, String relKey, String key, String value);

```
## Project UML:
Here is a link to our project's overall UML. There are around 100 classes in our project, so it is a pretty huge UML.
[Project UML svg](/ProjectUML.svg "Project UML")

## Change Logs:

See each members responsibility [here](https://github.com/lamdaV/UMLify/blob/master/CHANGELOG.md).
