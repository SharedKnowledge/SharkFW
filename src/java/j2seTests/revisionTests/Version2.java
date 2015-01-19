package revisionTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author thsc
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    knowledgeBaseTests.STTests.class,
    ApiRev1.SimpleKBTest.class, 
    ApiRev1.SerializationTest.class, 
    ApiRev1.Assimilate_ExtractionTests.class, 
    ApiRev1.FSSharkKBTest.class,
    ApiRev1.ExchangeTests.class,
    ApiRev1.StreamTests.class,
    ApiRev1.DifferentAddresses.class
})
public class Version2 {
    
}
