import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class JsonSchemaFormatRegexGeneratorURITest {

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
    }

    private static boolean URITest(String toTest) throws ScriptException, NoSuchMethodException{
        String regex =JsonSchemaFormatRegexGenerator.GetRegexForJsonSchemaFormat("uri");
        return (boolean) testMethodInvoker.invokeFunction("evalRegex", regex, toTest);
    }

    @Test
    public void MinimalTest() throws ScriptException, NoSuchMethodException{
        Assertions.assertTrue(URITest("a:"));
        Assertions.assertFalse(URITest("a"));
        Assertions.assertFalse(URITest(":a"));
    }

    @Test
    public void NormalTest() throws ScriptException, NoSuchMethodException{
        Assertions.assertTrue(URITest("https:www.google.de"));
        Assertions.assertTrue(URITest("https:www.google.de/"));
        Assertions.assertTrue(URITest("http://www.ics.uci.edu/pub/ietf/uri/#Related"));
        Assertions.assertFalse(URITest("www.google.de"));
    }

    @Test
    public void SchemaTest() throws ScriptException, NoSuchMethodException{
        Assertions.assertTrue(URITest("a:"));
        Assertions.assertTrue(URITest("f+:"));
        Assertions.assertFalse(URITest("+a:"));
        Assertions.assertFalse(URITest("-:"));
    }

    @Test
    public void AuthorityTest() throws ScriptException, NoSuchMethodException{
        Assertions.assertTrue(URITest("https://--12"));
        Assertions.assertTrue(URITest("https://a@%1F"));
        Assertions.assertTrue(URITest("https://12@er.e.ede:32"));
        Assertions.assertTrue(URITest("https://12@er~e.ede:32"));
        Assertions.assertTrue(URITest("https://"));
        Assertions.assertFalse(URITest("https://@ww:4G"));
        Assertions.assertFalse(URITest("https://@%"));
    }

    @Test
    public void UserinfoTest() throws ScriptException, NoSuchMethodException{
        Assertions.assertTrue(URITest("https://josef@"));
        Assertions.assertTrue(URITest("https://@"));
        Assertions.assertTrue(URITest("https://:@"));
        Assertions.assertTrue(URITest("https://!a!@"));
        Assertions.assertTrue(URITest("https://12@"));
        Assertions.assertTrue(URITest("https://12;@er~e.ede:32"));
        Assertions.assertTrue(URITest("https://%12@"));
        Assertions.assertFalse(URITest("https://}@"));
        Assertions.assertFalse(URITest("https://@@"));
        Assertions.assertFalse(URITest("https://[]@"));
    }

    @Test
    public void PortTest()throws ScriptException,NoSuchMethodException{
        Assertions.assertTrue(URITest("https://josef@foo.bar:123"));
        Assertions.assertTrue(URITest("https://@:12"));
        Assertions.assertTrue(URITest("https://12@23:"));
        Assertions.assertTrue(URITest("https://foo.bar:1222232423234"));
        Assertions.assertFalse(URITest("https://ww.de:1A"));
        Assertions.assertFalse(URITest("https://foo.bar:we"));
    }

    @Test
    public void IPv4Test()throws ScriptException,NoSuchMethodException{
        Assertions.assertTrue(URITest("https://josef@12.12.12.12:123"));
        Assertions.assertTrue(URITest("https://12.12.12.12"));
        /*There isn't a non-correct example, because in RFC 3986
         (https://datatracker.ietf.org/doc/html/rfc3986#section-4.1)
         reg-name can be used instead of IPv4address. reg-name allows any
         combination of digits and points.*/
    }

    @Test
    public void IPv6Test()throws ScriptException,NoSuchMethodException{
        Assertions.assertTrue(URITest("https://[2001:0DB8:85A3:08D3:1319:8A2E:1234::]"));
        Assertions.assertTrue(URITest("https://[2001:0DB8:85A3:08D3:1319:8A2E:1234:E32]"));
        Assertions.assertTrue(URITest("https://[2001:0DB8:85A3:08D3:1319:8A2E:255.234.23.1]"));
        Assertions.assertTrue(URITest("https://[2001:0DB8:85A3:08D3:1319::255.234.23.1]"));
        Assertions.assertTrue(URITest("https://[2001:0DB8:85A3:08D3::1319:255.234.23.1]"));
        Assertions.assertTrue(URITest("https://[2001:0DB8:85A3::08D3:1319:255.234.23.1]"));
        Assertions.assertTrue(URITest("https://[2001:0DB8::85A3:08D3:1319:255.234.23.1]"));
        Assertions.assertTrue(URITest("https://[2001::0DB8:85A3:08D3:1319:255.234.23.1]"));
        Assertions.assertTrue(URITest("https://[::2001:0DB8:85A3:08D3:1319:255.234.23.1]"));
        Assertions.assertTrue(URITest("https://[::]"));
        Assertions.assertFalse(URITest("https://[::2001:0DB8:85A3:08D3:1319:23FD:1324:1234]"));
        Assertions.assertFalse(URITest("https://[::2001:0DB8:85A3:08D3:1319:256.234.23.12]"));
        Assertions.assertFalse(URITest("https://[::2001:0DB8:85A3:08D3:1319:23FD:1324:1234]"));
    }

    @Test
    public void IPvFutureTest()throws ScriptException,NoSuchMethodException{
        Assertions.assertTrue(URITest("https://[v12.1!+34feuded]"));
        Assertions.assertTrue(URITest("https://[vF.1]"));
        Assertions.assertTrue(URITest("https://[vF.1:]"));
        Assertions.assertFalse(URITest("https://[v.1]"));
        Assertions.assertFalse(URITest("https://[F.132]"));
        Assertions.assertFalse(URITest("https://[vGF.1]"));
        Assertions.assertFalse(URITest("https://[v1.?]"));
        Assertions.assertFalse(URITest("https://[v1.1.@]"));
        Assertions.assertFalse(URITest("https://[v1.@]"));
        Assertions.assertFalse(URITest("https://[v2.1/]"));
        Assertions.assertFalse(URITest("https://[v2.1?]"));
    }

    @Test
    public void RegNameTest()throws ScriptException,NoSuchMethodException{
        Assertions.assertTrue(URITest("https://ww"));
        Assertions.assertTrue(URITest("https://de.de.ed"));
        Assertions.assertTrue(URITest("https://%1F"));
        Assertions.assertTrue(URITest("https://22!%3F+*"));
        Assertions.assertFalse(URITest("https://@@"));
    }

    @Test
    public void PathAbemptyTest()throws ScriptException,NoSuchMethodException{
        Assertions.assertTrue(URITest("https://fe"));//PathAbempty missing
        Assertions.assertTrue(URITest("https://fe/er"));
        Assertions.assertTrue(URITest("https://fe/12.23/ee"));
        Assertions.assertTrue(URITest("https://fe/%5Fklw/!"));
        Assertions.assertTrue(URITest("https://fe/er:@/1w/%15"));
        Assertions.assertFalse(URITest("https://fe/%1f"));
        Assertions.assertFalse(URITest("https://fe/[]"));
    }

    @Test
    public void PathAbsoluteTest()throws ScriptException,NoSuchMethodException{
        Assertions.assertTrue(URITest("https:/fe"));
        Assertions.assertTrue(URITest("https:/fe/:d3"));
        Assertions.assertTrue(URITest("https:/%1D"));
        Assertions.assertTrue(URITest("https:/"));
        Assertions.assertFalse(URITest("https:/[]"));
        Assertions.assertFalse(URITest("https:/fe/%1"));
    }

    @Test
    public void PathRootless()throws ScriptException,NoSuchMethodException{
        Assertions.assertTrue(URITest("https:12/rt"));
        Assertions.assertTrue(URITest("https:%1F/fe"));
        Assertions.assertTrue(URITest("https:%1F/fe"));
        Assertions.assertFalse(URITest("https:%1f/fe"));
    }

    @Test
    public void QueryTest()throws ScriptException,NoSuchMethodException{
        Assertions.assertTrue(URITest("https:12/rt"));
        Assertions.assertTrue(URITest("https:12/rt?2%1F"));
        Assertions.assertTrue(URITest("https://fe?2+*+?89u"));
        Assertions.assertTrue(URITest("https:/fe?ert54"));
        Assertions.assertTrue(URITest("https:?12%45"));
        Assertions.assertTrue(URITest("https://fe?@:"));
        Assertions.assertFalse(URITest("https://fe?%1f"));
        Assertions.assertFalse(URITest("https://fe?%1G"));
        Assertions.assertFalse(URITest("https://fe?]"));
        Assertions.assertFalse(URITest("https://fe?["));
    }

    @Test
    public void FragmentTest()throws ScriptException,NoSuchMethodException{
        Assertions.assertTrue(URITest("https:12/rt?2%1F"));
        Assertions.assertTrue(URITest("https:12/rt#"));
        Assertions.assertTrue(URITest("https:12/rt?2%1F#er"));
        Assertions.assertTrue(URITest("https:12/rt"));
        Assertions.assertTrue(URITest("https:12/rt?2%1F#%13"));
        Assertions.assertTrue(URITest("https:12/rt#+++*!$&'()"));
        Assertions.assertTrue(URITest("https:12/rt?2%1F#:@"));
        Assertions.assertTrue(URITest("https:12/rt#/,;"));
        Assertions.assertTrue(URITest("https:12/rt?2%1F#?er?"));
        Assertions.assertFalse(URITest("https://fe##"));
        Assertions.assertFalse(URITest("https://fe#%1f"));
        Assertions.assertFalse(URITest("https://fe#%1G"));
        Assertions.assertFalse(URITest("https://fe#]"));
        Assertions.assertFalse(URITest("https://fe#["));
    }
}
