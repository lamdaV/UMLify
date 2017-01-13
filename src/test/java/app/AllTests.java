package app;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import analyzerClassParser.AnalyzerClassParserTest;
import analyzerRelationParser.AnalyzerRelationParserTest;
import config.CommandLineParserTest;
import model.ASMParserTest;
import model.ClassModelTest;
import model.MethodModelTest;
import model.SystemModelTest;
import model.TypeModelTest;
import model.TypeParserTest;
import utility.IExpanderTest;
import utility.IFilterTest;
import utility.IMapperTest;

/**
 * Runs all test suite.
 * <p>
 * Created by lamd on 12/12/2016.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({SystemTest.class, ASMParserTest.class, CommandLineParserTest.class, SystemModelTest.class,
        ClassModelTest.class, IFilterTest.class, IMapperTest.class, IExpanderTest.class, MethodModelTest.class,
        TypeParserTest.class, TypeModelTest.class, AnalyzerRelationParserTest.class, AnalyzerClassParserTest.class, })
public class AllTests {

}