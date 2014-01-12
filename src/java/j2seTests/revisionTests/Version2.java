package revisionTests;

import ApiRev1.ExchangeTests;
import ApiRev1.FSSharkKBTest;
import ApiRev1.StreamTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author thsc
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    ApiRev1.SimpleKBTest.class, 
    ApiRev1.SerializationTest.class, 
    ApiRev1.Assimilate_ExtractionTests.class, 
    FSSharkKBTest.class,
    ExchangeTests.class,
    StreamTests.class
})
public class Version2 {
    
}
