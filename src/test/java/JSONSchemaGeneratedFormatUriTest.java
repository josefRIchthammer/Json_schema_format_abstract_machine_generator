import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class JSONSchemaGeneratedFormatUriTest {

   private static Invocable testMethodInvoker;
   @BeforeAll
   public static void setUpTest(){
       ScriptEngineManager manager = new ScriptEngineManager();
       ScriptEngine engine = manager.getEngineByName("JavaScript");
       // JavaScript code in a String
       String script2 = "function evalRegex(regex,evalString)"
               + " { return new RegExp(regex).test(evalString) }";

       try {
           engine.eval(script2);
       } catch (ScriptException e) {
           e.printStackTrace();
       }

       testMethodInvoker = (Invocable) engine;
   }    private static boolean FormatTest(String toTest) throws ScriptException, NoSuchMethodException{
        String regex =JsonSchemaFormatRegexGenerator.GetRegexForJsonSchemaFormat("uri");
        return (boolean) testMethodInvoker.invokeFunction("evalRegex", regex, toTest);
    }

    @Test 
    public void Test0() throws ScriptException, NoSuchMethodException{
        //a valid URL with anchor tag
        Assertions.assertTrue(FormatTest("http://foo.bar/?baz=qux#quux"));
    }

    @Test 
    public void Test1() throws ScriptException, NoSuchMethodException{
        //a valid URL with anchor tag and parentheses
        Assertions.assertTrue(FormatTest("http://foo.com/blah_(wikipedia)_blah#cite-1"));
    }

    @Test 
    public void Test2() throws ScriptException, NoSuchMethodException{
        //a valid URL with URL-encoded stuff
        Assertions.assertTrue(FormatTest("http://foo.bar/?q=Test%20URL-encoded%20stuff"));
    }

    @Test 
    public void Test3() throws ScriptException, NoSuchMethodException{
        //a valid puny-coded URL 
        Assertions.assertTrue(FormatTest("http://xn--nw2a.xn--j6w193g/"));
    }

    @Test 
    public void Test4() throws ScriptException, NoSuchMethodException{
        //a valid URL with many special characters
        Assertions.assertTrue(FormatTest("http://-.~_!$&'()*+,;=:%40:80%2f::::::@example.com"));
    }

    @Test 
    public void Test5() throws ScriptException, NoSuchMethodException{
        //a valid URL based on IPv4
        Assertions.assertTrue(FormatTest("http://223.255.255.254"));
    }

    @Test 
    public void Test6() throws ScriptException, NoSuchMethodException{
        //a valid URL with ftp scheme
        Assertions.assertTrue(FormatTest("ftp://ftp.is.co.za/rfc/rfc1808.txt"));
    }

    @Test 
    public void Test7() throws ScriptException, NoSuchMethodException{
        //a valid URL for a simple text file
        Assertions.assertTrue(FormatTest("http://www.ietf.org/rfc/rfc2396.txt"));
    }

    @Test 
    public void Test8() throws ScriptException, NoSuchMethodException{
        //a valid URL 
        Assertions.assertTrue(FormatTest("ldap://[2001:db8::7]/c=GB?objectClass?one"));
    }

    @Test 
    public void Test9() throws ScriptException, NoSuchMethodException{
        //a valid mailto URI
        Assertions.assertTrue(FormatTest("mailto:John.Doe@example.com"));
    }

    @Test 
    public void Test10() throws ScriptException, NoSuchMethodException{
        //a valid newsgroup URI
        Assertions.assertTrue(FormatTest("news:comp.infosystems.www.servers.unix"));
    }

    @Test 
    public void Test11() throws ScriptException, NoSuchMethodException{
        //a valid tel URI
        Assertions.assertTrue(FormatTest("tel:+1-816-555-1212"));
    }

    @Test 
    public void Test12() throws ScriptException, NoSuchMethodException{
        //a valid URN
        Assertions.assertTrue(FormatTest("urn:oasis:names:specification:docbook:dtd:xml:4.1.2"));
    }

    @Test 
    public void Test13() throws ScriptException, NoSuchMethodException{
        //an invalid protocol-relative URI Reference
        Assertions.assertFalse(FormatTest("//foo.bar/?baz=qux#quux"));
    }

    @Test 
    public void Test14() throws ScriptException, NoSuchMethodException{
        //an invalid relative URI Reference
        Assertions.assertFalse(FormatTest("/abc"));
    }

    @Test 
    public void Test15() throws ScriptException, NoSuchMethodException{
        //an invalid URI
        Assertions.assertFalse(FormatTest("\\\\WINDOWS\\fileshare"));
    }

    @Test 
    public void Test16() throws ScriptException, NoSuchMethodException{
        //an invalid URI though valid URI reference
        Assertions.assertFalse(FormatTest("abc"));
    }

    @Test 
    public void Test17() throws ScriptException, NoSuchMethodException{
        //an invalid URI with spaces
        Assertions.assertFalse(FormatTest("http:// shouldfail.com"));
    }

    @Test 
    public void Test18() throws ScriptException, NoSuchMethodException{
        //an invalid URI with spaces and missing scheme
        Assertions.assertFalse(FormatTest(":// should fail"));
    }

    @Test 
    public void Test19() throws ScriptException, NoSuchMethodException{
        //an invalid URI with comma in scheme
        Assertions.assertFalse(FormatTest("bar,baz:foo"));
    }

}