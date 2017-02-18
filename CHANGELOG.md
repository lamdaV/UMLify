# Milestone 01:

- **Fred:**

  - Performed Debugging
  - Discuss Design Decisions
  - Implemented Models.
  - Implemented Displayer.
  - Implemented IFilter.
  - Assisted in Generator Design
  - Refactored Code.
  - Code Review.
  - Wrote test code.

- **Adam:**

  - Managed the creation and updates to the UML
  - Discuss Design Decisions
  - Coded the CommandLineParser and Configuration
  - Performed Debugging
  - Created the README.
  - Wrote test code.

- **David:**

  - Performed Debugging
  - Discuss Design Decisions
  - Added Continuous Integration.
  - Implemented Generator.
  - Implemented Runner.
  - Integrated GraphViz.
  - Setup Project structure.
  - Created Generator Interfaces.
  - Formatted README with MarkDown syntax.
  - Refactored Code.
  - Code Review.
  - Wrote test code.

# Milestone 02:

- **Fred:**

  - Designed the overall decorator pattern, break down generator into classParserAnalyzer, and RelationParserAnalyzer, so that all fields are open to extension
  - Refactored code out of Generator into Relationship.
  - Implemented basic class parsing analyzer from previous generator code.
  - Figured out generics parsing with ASM, parse all the generic signature in JVM.
  - Refactor TypeModel to use decorator pattern, so all generic parameters and types can be represented.
  - Refactored Model to return cardinality information.
  - Add the InstructionModel, so that analyzers can analyzer the details of each java bytecode.
  - Resolved bugs discovered in Model.
  - Wrote test.
  - Code reviewed.

- **Adam:**

  - Updated UML
  - Discovered bugs in output inconsistency with Milestone requirements.
  - Added JSON file support.
  - Wrote Python to get qualified class names.

- **David:**

  - Added bijective arrow Relationship support in GraphViz.
  - Added GraphViz cardinality support in Relationship.
  - Assisted in Relation and Generator refactor.
  - Refactored Configuration to be more extensible.
  - Wrote test.
  - Code reviewed.
  - Cleaned code to for readability.
  - Fixed and improved Python script from Adam.
  - Debug GraphViz generator and Model integrations.
  - Wrote ExplorerRunner to display generated output.
  - Added missing JavaDocs in all packages when possible.
  - Resolved bugs discovered in Generator.

# Milestone 03:

- **Fred:**

  - Refactor the configuration, so that all configurations are stored as a Map from string to string.
  - Make Configuration a factory of any Configurable, which defines its default configuration
  - Rewrote the commandline parser so it takes config jsons and can override specific configuration
  - Implement the SingletonAnalyzer

- **Adam:**

- **David:**

  - Implement the FavorCompositionAnalyzer.
  - Worked with Adam to implement Bidirectional Analyzer
  - Worked with Fred on CommandLine Parser.
  - Discussed about the new config JSON format.
  - Refactored code to meet style standard and added javadoc.

# Milestone 04:

- **Fred:**

  - Implemented the Dependency Inversion violation detector
  - Refactor SystemModel to be an styleRecorder, so we don't have to decorate classModel every time.
  - Fix bugs in model.

- **Adam:**

  - Implemented Adapter Analyzer.
  - Created Demo JSON 
  
- **David:**

  - Implemented Shared Template for Adapter and Decorator analyzers.
  - Found glaring bugs in Model package.
  - Implemented Good and Bad Decorator detection.
  - Discussed with Adam on how to perform Adapter detection.
  - Heavily refactored code base for stylistic changes and Stream incorporation.
  - Wrote README.md edits.
  
# Milestone 05:
  
- **Fred:**

  - Implemented Law of Demeter Analyzer.
  - Worked with David to fix bugs with Adapter and Decorator for specific cases.

- **Adam:**

  - Implemented Public Static Initializer Analyzer.
  - Implemented properties file parser.
  
- **David:**

  - Implemented the Graphic User Interface using MVC.
  - Implemented dynamic config parsing.
