package net.sharkfw.revisiontests;

import net.sharkfw.apirev1.*;
import net.sharkfw.knowledgebasetests.InterestsStorage;
import net.sharkfw.knowledgebasetests.STTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author thsc
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    STTests.class,
    InterestsStorage.class,
    SimpleKBTest.class,
    SerializationTest.class,
    Assimilate_ExtractionTests.class,
    FSSharkKBTest.class,
    ExchangeTests.class,
    StreamTests.class,
    DifferentAddresses.class
})
public class Version2 {
    
}
