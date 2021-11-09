import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestGeneratorFromTestSuite {

    public static void main(String[] args) throws IOException {
        String path = "../JSON-Schema-Test-Suite/tests/draft2020-12/optional/format/uri.json";

        Map<String, Object> map = loadTests(path);

        var map2 = (Map<String, Object>) map.get("schema");
        String format = (String) map2.get("format");
        String className = "JSONSchemaGeneratedFormat" + format.substring(0,1).toUpperCase()+format.substring(1) + "Test";
        String fileName = className+".java";

        String javaClass = Header1;
        javaClass += " "+className +" ";
        javaClass += Header2;
        javaClass += FormatTestMethod(map);
        javaClass += createTests(map);
        javaClass += Footer;



        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(javaClass);

        writer.close();

    }

    private static Map<String, Object> loadTests(String path) throws IOException {
        String content = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
        content = content.substring(2, content.length() - 3);

        return new ObjectMapper().readValue(content, Map.class);
    }

    private static final String Header1 = """
            import javax.script.Invocable;
            import javax.script.ScriptEngine;
            import javax.script.ScriptEngineManager;
            import javax.script.ScriptException;
            import org.junit.jupiter.api.Assertions;
            import org.junit.jupiter.api.BeforeAll;
            import org.junit.jupiter.api.Test;


            public class""";
    private static final String Header2= """
             {

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
                }""";

    private static final String Footer = "}";

    private static String FormatTestMethod(Map<String, Object> map) {
        var map2 = (Map<String, Object>) map.get("schema");
        String format = (String) map2.get("format");
        return "    private static boolean FormatTest(String toTest) throws ScriptException, NoSuchMethodException{\n" +
                "        String regex =JsonSchemaFormatRegexGenerator.GetRegexForJsonSchemaFormat(\"" + format + "\");\n" +
                "        return (boolean) testMethodInvoker.invokeFunction(\"evalRegex\", regex, toTest);\n" +
                "    }";
    }

    private static String createTests(Map<String, Object> map) {
        StringBuilder builder = new StringBuilder();
        List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) map.get("tests");
        builder.append("\n\n");
        for (int i = 0; i < list.size(); i++) {
            builder.append("    @Test \n");
            builder.append("    public void Test");
            builder.append(i);
            builder.append("() throws ScriptException, NoSuchMethodException{\n");
            CreateCommend(list.get(i), builder);
            CreateMethodCalls(list.get(i), builder);
            builder.append("    }\n\n");
        }
        return builder.toString();
    }

    private static void CreateMethodCalls(Map<String, Object> map, StringBuilder builder) {
        if ((boolean) map.get("valid")) {
            builder.append("        Assertions.assertTrue(FormatTest(\"");
        } else {
            builder.append("        Assertions.assertFalse(FormatTest(\"");
        }
        String data = (String) map.get("data");
        data = data.replace("\\","\\\\");

        builder.append(data);
        builder.append("\"));\n");
    }


    private static void CreateCommend(Map<String, Object> map, StringBuilder builder) {
        builder.append("        //");
        builder.append(map.get("description"));
        builder.append("\n");
    }


}
