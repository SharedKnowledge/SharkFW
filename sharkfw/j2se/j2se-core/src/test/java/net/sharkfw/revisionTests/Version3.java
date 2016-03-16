package net.sharkfw.revisiontests;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import net.sharkfw.asiptests.ASIPSerializerTest;
import net.sharkfw.asiptests.ASIPSharkKBImplTest;
import net.sharkfw.security.BasicSecurityTests;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author thsc
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
            Version2.class,
            BasicSecurityTests.class,
            ASIPSerializerTest.class,
            ASIPSharkKBImplTest.class
                
        })
public class Version3 {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
