/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ApiRev1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import junit.framework.Assert;
import net.sharkfw.kep.KEPMessage;
import net.sharkfw.protocols.SharkInputStream;
import net.sharkfw.protocols.SharkOutputStream;
import net.sharkfw.protocols.StandardSharkInputStream;
import net.sharkfw.protocols.UTF8SharkOutputStream;
import net.sharkfw.system.Util;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mfi
 */
public class StreamTests {
  
  public StreamTests() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  
    @Test
    public void testSharkStreams() throws IOException {
      String test = "Hello World!";
      String test2 = "42!";
      
      String someBytes = "vh347z3ifv";
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      SharkOutputStream sos = new UTF8SharkOutputStream(baos);
      
      // Write Strings first
      sos.write(test);
      sos.write(test2);
      
      // Write len as String before binary data
      int len = someBytes.getBytes().length;
      sos.write(Integer.toString(len));
      
      // Write binary data
      sos.getOutputStream().write(someBytes.getBytes());
      
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      SharkInputStream sis = new StandardSharkInputStream(bais);
      
      // Read UTF-strings first
      String result1 = sis.readUTF8();
      String result2 = sis.readUTF8();
      
      // Read len as String before reading binary data
      String lenString = sis.readUTF8();
      int lenBytes = Integer.parseInt(lenString);
      byte[] someResultBytes = new byte[lenBytes];
      
      // Read binary data
      sis.getInputStream().read(someResultBytes);
      
      // Compare results
      Assert.assertEquals(test, result1);
      Assert.assertEquals(test2, result2);
      Assert.assertTrue(Arrays.equals(someBytes.getBytes(), someResultBytes));
      
      // Try to read more bytes - must fail with IOException
      boolean exception = false;
      try {
        String trailing = sis.readUTF8();
        System.out.println("Trailing bytes:" + trailing);
      } catch (IOException ioe) {
        exception = true;
      }
      
      // Check that the exception has been thrown
      Assert.assertTrue(exception);
  }
    
}
