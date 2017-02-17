package app;

import adapter.SystemModel;
import analyzer.utility.ISystemModel;
import config.IConfiguration;
import generator.IGenerator;
import model.SystemModelFactory;
import runner.GraphVizRunner;
import runner.IRunner;

/**
 * This is a concrete implementation of algorithm for analyzing a UML. It
 * depends on the Configuration setup and SystemModel in our program
 */
public class UMLEngine extends AbstractUMLEngine {
    private EngineConfiguration config;

    private UMLEngine(EngineConfiguration configuration) {
        config = configuration;
    }

    /**
     * @param config the configuration object read from command line arguments, or
     *               parsed from file. See {@link ConfigurationFactor}
     * @return the UML engine
     */
    public static UMLEngine getInstance(IConfiguration config) {
        return new UMLEngine(config.createConfiguration(EngineConfiguration.class));
    }

    @Override
    public ISystemModel createSystemModel() {
        return new SystemModel(SystemModelFactory.getInstance(config));
    }

    @Override
    public void analyze(ISystemModel systemModel) {
        this.config.getAnalyzers().forEach((analyzer) -> analyzer.analyze(systemModel, this.config));
    }

    @Override
    String generate(ISystemModel systemModel) {
        IGenerator generator = config.getGenerator();
        return generator.generate(systemModel, config);
    }

    @Override
    void executeRunner(String graphVisStr) {
        IRunner runner = GraphVizRunner.getInstance(config);
        try {
            runner.execute(graphVisStr);
        } catch (Exception e) {
            throw new RuntimeException("[ INFO ]: Ensure that GraphViz bin folder is set in the environment variable.",
                    e);
        }
    }

}
