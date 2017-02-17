package app;

import analyzer.adapter.AdapterDetectorAnalyzer;
import analyzer.decorator.BadDecoratorAnalyzer;
import analyzer.decorator.GoodDecoratorAnalyzer;
import analyzer.relationParser.RelationParserAnalyzer;
import analyzer.utility.ISystemModel;
import config.Configuration;
import demos.Decorator;
import demos.SwitchProcessor;
import demos.TextProcessor;
import demos.Transformer;
import dummy.decoratorDummy.*;
import generator.GeneratorConfiguration;
import model.ModelConfiguration;
import runner.RunnerConfiguration;
import viewer.Viewer;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A Test Class that will generate files for local inspection.
 * <p>
 * Created by lamd on 12/15/2016.
 */
public class LocalTester {
    public static void main(String[] args) {
        // Set up the config.
        Configuration config = Configuration.getInstance();
        config.set(ModelConfiguration.IS_RECURSIVE_KEY, Boolean.toString(false));
        config.set(GeneratorConfiguration.NODE_SEP, Double.toString(1.0));
        config.set(GeneratorConfiguration.RANK_DIR, ("BT"));
        config.set(RunnerConfiguration.OUTPUT_DIRECTORY, "./output");
        config.set(RunnerConfiguration.FILE_NAME, "asmClass");
        config.set(RunnerConfiguration.EXECUTABLE_PATH, "dot");
        config.set(RunnerConfiguration.OUTPUT_FORMAT, "svg");
        config.add(EngineConfiguration.ANALYZER_KEY, RelationParserAnalyzer.class.getName(), GoodDecoratorAnalyzer.class.getName(), BadDecoratorAnalyzer.class.getName(), AdapterDetectorAnalyzer.class.getName());
        config.add(ModelConfiguration.CLASSES_KEY, Component.class.getName(), GoodDecorator.class.getName(), GoodConcreteDecorator.class.getName(),
                BadDecorator.class.getName(), BadConcreteDecorator.class.getName(), Adapee.class.getName(), Adapter.class.getName(), AdaptedTo.class.getName());

        config.add(ModelConfiguration.CLASSES_KEY, Decorator.class.getName(), SwitchProcessor.class.getName(), TextProcessor.class.getName(), Transformer.class.getName());
        config.add(ModelConfiguration.CLASSES_KEY, FilterInputStream.class.getName(), FilterOutputStream.class.getName(), InputStream.class.getName(), OutputStream.class.getName());

        UMLEngine engine = UMLEngine.getInstance(config);
        ISystemModel systemModel = engine.createSystemModel();
        engine.analyze(systemModel);

//        systemModel.getClasses().forEach((c) -> System.out.println(systemModel.getNodeStyle(c)));

        String actual = engine.generate(systemModel);
        engine.executeRunner(actual);

        Runnable explorerRunner = new Viewer(config.createConfiguration(RunnerConfiguration.class));
        explorerRunner.run();

        System.out.println("Done");
    }

}
