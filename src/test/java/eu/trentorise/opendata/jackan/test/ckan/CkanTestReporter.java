/* 
 * Copyright 2015 Trento Rise  (trentorise.eu) 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.trentorise.opendata.jackan.test.ckan;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;
import eu.trentorise.opendata.commons.BuildInfo;
import eu.trentorise.opendata.jackan.ckan.CkanClient;
import eu.trentorise.opendata.jackan.test.JackanTestConfig;
import eu.trentorise.opendata.commons.OdtUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.href;
import static org.rendersnake.HtmlAttributesFactory.style;
import org.rendersnake.HtmlCanvas;

/**
 * Little app to test a list of catalogs and produce an HTML report.
 *
 * @author David Leoni
 */
public class CkanTestReporter {

    private static final Logger logger = Logger.getLogger(CkanTestReporter.class.getName());

    public static final List<String> ALL_TEST_NAMES
            = ImmutableList.of("testApiVersionSupported",
                    "testDatasetList",
                    "testDatasetListWithLimit",
                    "testSearchDatasetsByText",
                    "testDatasetAndResource",
                    "testLicenseList",
                    "testTagList",
                    "testTagNameList",
                    "testUserList",
                    "testUser",
                    "testGroupList",
                    "testGroup",
                    "testOrganizationList",
                    "testOrganization",
                    "testFormatList",
                    "testSearchDatasetsByGroups",
                    "testSearchDatasetsByOrganization",
                    "testSearchDatasetsByTags",
                    "testSearchDatasetsByLicenseIds"
            );

    private static String ERROR_CLASS = "jackan-error";
    private static String JACKAN_TABLE_CLASS = "jackan-table";

    
  /**
     * By default test/resources/ckan-instances.txt file is used.
     */
    public static void main(String[] args) {

        JackanTestConfig.of().loadConfig();

        String catFilename = null;

        if (args.length == 2) {
            catFilename = args[1];
            logger.info("Using provided catalogs file " + catFilename + ".");
        } else {
            catFilename = "ckan-instances.txt";
            logger.info("Using default catalogs file " + catFilename + ". If you wish to provide yours pass filename as first argument.");
        }

        Map<String, String> catalogsNames = readCatalogsList(catFilename);
        //ImmutableMap.of("http://dati.trentino.it", "dati.trentino.it", "http://dati.toscana.it", "dati.toscana.it");// "http://publicdata.eu/", "http://publicdata.eu/"

        List<String> testNames = ALL_TEST_NAMES; //.subList(0, 2);

        RunSuite testResults = runTests(catalogsNames, testNames);

        String content = renderRunSuite(catalogsNames, testNames, testResults);

        saveToDirectory(new File("reports/" + REPORT_PREFIX + "-" + new DateTime().toString("dd-MM-yyyy--HH-mm-ss")), content, testResults);

        File latestDir = new File("reports/" + REPORT_PREFIX + "-latest");

        FileUtils.deleteQuietly(latestDir);
        saveToDirectory(latestDir, content, testResults);

    }
    
    
    /**
     * @param catalogListFilepath absolute file path
     */
    public static Map<String, String> readCatalogsList(String catalogListFilepath) {

        // catalog url, name
        ImmutableMap.Builder<String, String> catalogsBuilder = ImmutableMap.builder();

        InputStream is = null;

        try {
            is = new FileInputStream(catalogListFilepath);
        }
        catch (FileNotFoundException fex) {

            logger.info("Trying to take file " + catalogListFilepath + " from test resources");
            is = CkanTestReporter.class.getClassLoader().getResourceAsStream(catalogListFilepath);
            if (is == null) {
                throw new RuntimeException("Couldn't find file " + catalogListFilepath);
            }

        }
        try {
            String str;

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            boolean readingName = true;
            String name = "";
            while ((str = reader.readLine()) != null) {
                if (readingName) {
                    name = str;
                } else {
                    catalogsBuilder.put(OdtUtils.removeTrailingSlash(str), name);
                }

                readingName = !readingName;
            }

        }
        catch (IOException ex) {
            Logger.getLogger(CkanTestReporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (Throwable ignore) {
            }
        }
        /*
         TODO validate urls:
         URL catURL;
         try {
         catURL = new URL(catalogURL);
         }
         catch (MalformedURLException ex) {
         html.tr()
         .td(class_(ERROR_CLASS)) // todo we are skipping columns....
         .write("Bad catalog URL: " + catalogURL + " for catalog " + catalogs.get(catalogURL))
         ._td()
         ._tr();
         continue;
         }
         */
        return catalogsBuilder.build();
    }

    public static final String REPORT_PREFIX = "jackan-scan";
    public static final String TEST_RESULT_PREFIX = "test-result-";

  
    private static TestResult runTest(int testId, CkanClient client, String catalogName, String testName) {
        Optional<Throwable> error;
        CkanClientIT ckanTests = new CkanClientIT();
        try {
            java.lang.reflect.Method method;

            method = CkanClientIT.class.getMethod(testName, CkanClient.class);
            method.invoke(ckanTests, client);
            error = Optional.absent();
        }
        catch (Throwable t) {
            error = Optional.of(t);
        }
        return new TestResult(testId, testName, client.getCatalogURL(), catalogName, error);
    }

    static class RunSuite {

        private DateTime startTime;
        private DateTime endTime;
        private ImmutableList<TestResult> results;

        public RunSuite(DateTime startTime, DateTime endTime, List<TestResult> results) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.results = ImmutableList.copyOf(results);
        }

        public DateTime getStartTime() {
            return startTime;
        }

        public DateTime getEndTime() {
            return endTime;
        }

        public ImmutableList<TestResult> getResults() {
            return results;
        }

    }

    public static RunSuite runTests(Map<String, String> catalogNames, List<String> testNames) {

        DateTime startTime = new DateTime();

        Map<String, CkanClient> clients = new HashMap();

        for (Entry<String, String> e : catalogNames.entrySet()) {
            clients.put(e.getKey(), new CkanClient(e.getKey()));
        }

        ImmutableList.Builder<TestResult> results = ImmutableList.builder();

        int testCounter = 0;

        for (String testName : testNames) { // so we don't stress one catalog with all tests in sequence
            for (String catalogUrl : catalogNames.keySet()) {
                results.add(runTest(testCounter, clients.get(catalogUrl), catalogNames.get(catalogUrl), testName));
                testCounter += 1;
            }
        }

        return new RunSuite(startTime, new DateTime(), results.build());
    }

    /**
     * Formats date time up to the day, in English format
     */
    private static String formatDateUpToDay(DateTime date) {
        return date.toString(DateTimeFormat.mediumDate().withLocale(Locale.ENGLISH));
    }

    /**
     * Formats date time up to the second, in English format
     */
    private static String formatDateUpToSecond(DateTime date) {
        return date.toString(DateTimeFormat.mediumDateTime().withLocale(Locale.ENGLISH));
    }

    public static String renderRunSuite(Map<String, String> catalogs, List<String> testNames, RunSuite runSuite) {
        String outputFileContent;

        BuildInfo buildInfo = JackanTestConfig.of().getBuildInfo();

        try {

            HtmlCanvas html = new HtmlCanvas();

            html.html()
                    .head()
                    .title().content("Jackan Test Results")
                    //.meta(name("description").add("content","Jackan test analyzer",false))
                    //.macros().stylesheet("htdocs/style-01.css"))
                    //.render(JQueryLibrary.core("1.4.3"))
                    //.render(JQueryLibrary.ui("1.8.6"))
                    //.render(JQueryLibrary.baseTheme("1.8"))                    
                    .style()
                    .write("." + ERROR_CLASS + " {color:red}")
                    .write("." + JACKAN_TABLE_CLASS + " { border-collapse:collapse; table-layout: fixed; width: 100%;  }")
                    .write("." + JACKAN_TABLE_CLASS + " td, th { border: 1px solid black; vertical-align: top; padding:10px; width:100px;}")
                    .write("." + JACKAN_TABLE_CLASS + " th { position:absolute; left:0;  width:230px;}")
                    .write(".outer {position:relative}")
                    .write(".inner {\n"
                            + "  overflow-x:scroll;\n"
                            + "  overflow-y:visible;\n"
                            + "  margin-left:250px;\n"
                            + "}")
                    ._style()
                    ._head();

            html.body()
                    .h1().a(href("https://github.com/opendatatrentino/jackan").target("_blank"))
                    .write("Jackan")
                    ._a()
                    .write(" Report - " + formatDateUpToDay(runSuite.getStartTime()))
                    ._h1()
                    .b().write("Note: ")._b()
                    .span().write("Some tests might fail due to missing items in the target catalog (i.e. catalog has no tags or no organizations)")._span().br()
                    .br()
                    .b().write("Jackan Version: ")._b()
                    .span().write(buildInfo.getVersion() + " ")
                    .a(href("https://github.com/opendatatrentino/jackan/commit/" + buildInfo.getGitSha())
                            .target("_blank"))
                    .write("Git commit")
                    ._a()
                    ._span()
                    .br()
                    .b().write("Started: ")._b()
                    .span().write(formatDateUpToSecond(runSuite.getStartTime()))._span().br()
                    .b().write("Finished: ")._b()
                    .span().write(formatDateUpToSecond(runSuite.getEndTime()))._span().br()
                    .br();

            Escaper escaper = HtmlEscapers.htmlEscaper();

            html.div(class_("outer"))
                    .div(class_("inner"));
            html.table(class_(JACKAN_TABLE_CLASS))
                    .tr()
                    .th(style("height:100%")).write("test")._th();

            for (String catalogUrl : catalogs.keySet()) {
                html.td().a(href(catalogUrl).target("_blank")).write(escaper.escape(catalogs.get(catalogUrl)))._a()._td();
            }
            html._tr();

            Iterator<TestResult> resultIterator = runSuite.getResults().iterator();

            for (String testName : testNames) {
                html.tr();
                html.th().b().write(testName)._b()._th();

                for (String catalogURL : catalogs.keySet()) {

                    TestResult result = resultIterator.next();
                    if (result.passed()) {
                        html
                                .td()
                                //.a(href(TEST_RESULT_PREFIX + result.getId() + ".html").target("_blank"))
                                .write("PASSED")._td();
                    } else {
                        html
                                .td()
                                .a(href(TEST_RESULT_PREFIX + result.getId() + ".html")
                                        .target("_blank")
                                        .class_(ERROR_CLASS))
                                .write("ERROR")
                                ._a()
                                ._td();
                    }
                }
                html._tr();
            }

            html
                    ._table()
                    ._div()
                    ._div()
                    ._body()
                    ._html();

            outputFileContent = html.toHtml();
        }
        catch (IOException ex) {
            outputFileContent = "HTML generation problem!" + ex;
        }
        return outputFileContent;
    }

    /**
     * Returns a new html page with test result.
     */
    public static String renderTestResult(TestResult result) {

        HtmlCanvas html = new HtmlCanvas();

        try {
            html.html()
                    .head()
                    .title().content("Jackan Test #" + result.getId())
                    //.meta(name("description").add("content","Jackan test anal",false))
                    //.macros().stylesheet("htdocs/style-01.css"))
                    //.render(JQueryLibrary.core("1.4.3"))
                    //.render(JQueryLibrary.ui("1.8.6"))
                    //.render(JQueryLibrary.baseTheme("1.8"))
                    .style()
                    .write("." + ERROR_CLASS + " {color:red}")
                    ._style()
                    ._head();

            html
                    .body()
                    .h1().write("Jackan Test #" + result.getId())._h1();

            if (result.passed()) {
                html.h2().write("Test passed!")._h2();

            } else {
                html.h2().write("Test didn't pass!")._h2();
                html.pre()
                        .write(HtmlEscapers.htmlEscaper().escape(Throwables.getStackTraceAsString(result.getError())))
                        ._pre();
            }

            html._body();
            return html.toHtml();
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, "Error while rendering Jackan Test " + result, ex);
            return "<html><body>Error while rendering Jackan Test #" + result.getId() + " </body></html>";
        }

    }

    public static void saveToDirectory(File outputDirectory, String indexContent, RunSuite runSuite) {

        outputDirectory.mkdirs();

        PrintWriter outIndex;
        try {
            outIndex = new PrintWriter(outputDirectory + "/index.html");
            outIndex.write(indexContent);
            outIndex.close();

            for (TestResult result : runSuite.getResults()) {
                PrintWriter outResult;
                String resultHtml = renderTestResult(result);
                outResult = new PrintWriter(outputDirectory + "/" + TEST_RESULT_PREFIX + result.getId() + ".html");
                outResult.write(resultHtml);
                outResult.close();
            }

            logger.info("Report is now available at " + outputDirectory.getAbsolutePath() + File.separator + "index.html");
        }
        catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

}
