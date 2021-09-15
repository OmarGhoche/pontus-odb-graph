package com.pontusvision.gdpr;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orientechnologies.apache.commons.csv.CSVFormat;
import com.orientechnologies.apache.commons.csv.CSVRecord;
import com.pontusvision.ingestion.Ingestion;
import com.pontusvision.ingestion.IngestionCSVFileRequest;
import com.pontusvision.ingestion.IngestionJsonObjArrayRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test for simple App.
 */
public class AppTest2  extends AppTest{
  /**
   * Create the test case
   *
   * @param testName name of the test case
   */

  /**
   * @return the suite of tests being tested
   */



  @AfterClass
  public static void after() throws Exception {
//    App.gserver.stop().join();
    App.oServer.shutdown();
    server.stop();

  }
  public static boolean deleteDirectory(File directoryToBeDeleted) {
    File[] allContents = directoryToBeDeleted.listFiles();
    if (allContents != null) {
      for (File file : allContents) {
        deleteDirectory(file);
      }
    }
    return directoryToBeDeleted.delete();
  }

  @BeforeClass
  public static void before() throws Exception {
    Path resourceDirectory = Paths.get(".");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    String databaseDir = Paths.get(absolutePath, "databases").toString();
    deleteDirectory(new File(databaseDir));

    String jpostalDataDir = Paths.get(absolutePath, "jpostal", "libpostal").toString();
    System.setProperty("user.dir", absolutePath);
    System.setProperty("ORIENTDB_ROOT_PASSWORD", "pa55word");
    System.setProperty("ORIENTDB_HOME", absolutePath);
    System.setProperty("pg.jpostal.datadir", jpostalDataDir);

    server = App.createJettyServer();

    server.start();
    App.init(Paths.get(absolutePath, "config", "gremlin-server.yaml").toString());

     System.out.println("finished Init");
  }

  static Gson gson = new Gson();

  @Test
  public void test3() throws InterruptedException {
    try {

        csvTestUtil("phase1.csv",  "phase1_csv");


      String userId =
          App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('ROZELL COLEMAN')).next().id().toString()")
              .get().toString();

      String rozellConnectionsQuery = "App.g.V(\"" + userId +"\").bothE().count().next().toString()";
      String rozellConnections = App.executor.eval(rozellConnectionsQuery)
          .get().toString();

      assertEquals(rozellConnections,"3");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void testCsvPolaris() throws InterruptedException {
    try {

      csvTestUtil("clientes.csv",  "Cliente_SAP_PosVenda_POLARIS");


      String userId =
              App.executor.eval("App.g.V()\n" +
                              ".has('Person.Natural.Full_Name', eq('MATEUS SILVA PINTO'))\n" +
                              ".next()\n" +
                              ".id()\n" +
                              ".toString()")
                      .get().toString();

////      Debugging
//      System.out.println("***************************************************************************************\n" +
//              userId);

      String oscarConnectionsQuery = "App.g.V(\"" + userId +"\").bothE().count().next().toString()";
      String oscarConnections = App.executor.eval(oscarConnectionsQuery)
              .get().toString();

      assertEquals(oscarConnections,"2");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void testSharepointTreinamentos() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-treinamentos.json",  "$.queryResp[*].fields",
              "sharepoint_treinamentos");

//    test for PEDRO Person.Employee NODE
      String userId =
              App.executor.eval("App.g.V().has('Person.Employee.Full_Name', eq('PEDRO ALVARO CABRAL'))\n" +
                              ".next().id().toString()").get().toString();
      String pedroConnectionsQuery = "App.g.V(\"" + userId +"\").bothE().count().next().toString()";
      String pedroConnections = App.executor.eval(pedroConnectionsQuery).get().toString();
      assertEquals(pedroConnections,"1");

//    test for MARCELA Person.Employee NODE
      String userId1 =
              App.executor.eval("App.g.V().has('Person.Employee.Full_Name', eq('MARCELA ALVES'))\n" +
                      ".next().id().toString()").get().toString();

//    test for JOSE Person.Employee NODE
      String userId2 =
              App.executor.eval("App.g.V().has('Person.Employee.Full_Name', eq('JOSE CARLOS MONSANTO'))\n" +
                      ".next().id().toString()").get().toString();

//    test for Object.Data_Source.Name
      String userId3 =
              App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq('sharepoint/treinamentos'))" +
                      ".next().id().toString()").get().toString();
//      assertEquals(userId3,"#82:0"); -- Id can change!!
      String rootConnectionsQuery = "App.g.V(\"" + userId3 +"\").bothE().count().next().toString()";
      String rootConnections = App.executor.eval(rootConnectionsQuery).get().toString();
      assertEquals(rootConnections,"3");

//    test for Object.Awareness_Campaign.Description
      String userId4 =
              App.executor.eval("App.g.V().has('Object.Awareness_Campaign.Description', " +
                 "eq('1º CURSO - LGPD - LEI GERAL DE PROTEÇÃO DE DADOS')).next().id().toString()").get().toString();
      String curso1ConnectionsQuery = "App.g.V(\"" + userId4 +"\").bothE().count().next().toString()";
      String curso1Connections = App.executor.eval(curso1ConnectionsQuery).get().toString();
      assertEquals(curso1Connections,"2"); // -- number of Group.Ingestion nodes can vary !!

//    test for Object.Awareness_Campaign.Description 2
      String userId5 =
              App.executor.eval("App.g.V().has('Object.Awareness_Campaign.Description', " +
                      "eq('BASES LEGAIS LGPD')).next().id().toString()").get().toString();
      String curso2ConnectionsQuery = "App.g.V(\"" + userId5 +"\").bothE().count().next().toString()";
      String curso2Connections = App.executor.eval(curso2ConnectionsQuery).get().toString();
      assertEquals(curso2Connections,"2");

//    test for Object.Awareness_Campaign.Description 3
      String userId6 =
              App.executor.eval("App.g.V().has('Object.Awareness_Campaign.Description', " +
                      "eq('LGPD - MULTAS E SANÇÕES')).next().id().toString()").get().toString();
      String curso3ConnectionsQuery = "App.g.V(\"" + userId6 +"\").bothE().count().next().toString()";
      String curso3Connections = App.executor.eval(curso3ConnectionsQuery).get().toString();
      assertEquals(curso3Connections,"1");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  public void csvTestUtil(String csvFile,  String ruleName) throws InterruptedException {

    String res = null;
    try {
      Path resourceDirectory = Paths.get(".");
      String pwdAbsolutePath = resourceDirectory.toFile().getAbsolutePath();
      String csvFilePath = Paths.get(pwdAbsolutePath, "src", "test", "resources", csvFile).toString();

      String csvString = FileUtils.readFileToString(new File(csvFilePath), "UTF-8");

      IngestionCSVFileRequest  req = new IngestionCSVFileRequest();
      req.csvBase64 = Base64.getEncoder().encodeToString(csvString.getBytes());
      req.ruleName = ruleName;

      Ingestion ingestion = new Ingestion();

      res = ingestion.csvFile(req);
      System.out.println(res);

    } catch (ExecutionException | IOException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }
}
