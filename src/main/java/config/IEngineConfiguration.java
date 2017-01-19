package config;

import java.util.List;

import analyzer.utility.IAnalyzer;
import generator.IGenerator;

/**
 * An interface for Configuration object that stores values to be used in
 * generation processes.
 */
public interface IEngineConfiguration extends Configurable, IConfiguration {

    /**
     * Returns the IGenerator
     *
     * @return IGenerator object.
     */
    IGenerator getGenerator();

    /**
     * Returns an Iterable of IAnalyzers.
     *
     * @return Iterable of IAnalyzers.
     */
    List<IAnalyzer> getAnalyzers();

}