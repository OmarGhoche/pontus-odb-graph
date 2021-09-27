package com.pontusvision.gdpr;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orientechnologies.apache.commons.csv.CSVFormat;
import com.orientechnologies.apache.commons.csv.CSVRecord;
import com.pontusvision.ingestion.Ingestion;
import com.pontusvision.ingestion.IngestionCSVFileRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

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
@TestMethodOrder(MethodOrderer.MethodName.class)

public class AppTest2  extends AppTest{
  /**
   * Create the test case
   *
   * @param testName name of the test case
   */

  /**
   * @return the suite of tests being tested
   */


  @Test
  public void testPhase1CSVIngestion() throws InterruptedException {
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

      csvTestUtil("sap-polaris-clientes.csv",  "Cliente_SAP_PosVenda_POLARIS");

//    Testing for Person.Natural WITH Tax_Number
      String userId0 =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('MATEUS SILVA PINTO'))\n" +
                              ".next().id().toString()").get().toString();
      String pintoConnectionsQuery = "App.g.V(\"" + userId0 +"\").bothE().count().next().toString()";
      String pintoConnections = App.executor.eval(pintoConnectionsQuery)
              .get().toString();
      assertEquals("5", pintoConnections); // 2 phone numbers + 1 identity card + 1 location + 1 ingestion event

//    Testing for Person.Natural withOUT Tax_Number
      String munirConnections =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('MUNIR HANDAUS'))\n" +
                      ".bothE().count().next().toString()").get().toString();
//      String munirConnectionsQuery = "App.g.V(\"" + userId1 +"\").bothE().count().next().toString()";
//      String munirConnections = App.executor.eval(munirConnectionsQuery).get().toString();
      assertEquals("3", munirConnections); // 1 phone number + 0 identity card (sem tax) + 1 location + 1 ingestion event

     String personNaturalTypes =
             App.executor.eval("App.g.V().has('Person.Natural.Type', eq('Clientes Sem Tax'))" +
                     ".count().next().toString()").get().toString();
     assertEquals("5",personNaturalTypes);

     System.out.println("*******************************************************\n" + personNaturalTypes);


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
      String sharepointDataSourceId =
              App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq('sharepoint/treinamentos'))" +
                      ".next().id().toString()").get().toString();
//      assertEquals(userId3,"#82:0"); -- Id can change!!
      String rootConnectionsQuery = "App.g.V(\"" + sharepointDataSourceId +"\").bothE().count().next().toString()";
      String rootConnections = App.executor.eval(rootConnectionsQuery).get().toString();
      assertEquals("1",rootConnections);

//    test for Object.Awareness_Campaign.Description
      String userId4 =
              App.executor.eval("App.g.V().has('Object.Awareness_Campaign.Description', " +
                 "eq('1º CURSO - LGPD - LEI GERAL DE PROTEÇÃO DE DADOS')).next().id().toString()").get().toString();
      String curso1ConnectionsQuery = "App.g.V(\"" + userId4 +"\").bothE().count().next().toString()";
      String curso1Connections = App.executor.eval(curso1ConnectionsQuery).get().toString();
      assertEquals(curso1Connections,"2");

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

  @Test
  public void testSharepointFontesDeDados() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-fontes-de-dados.json",  "$.queryResp[*].fields",
              "sharepoint_fontes_de_dados");

//    test for COUNT(dataSources)
      String countDataSources =
              App.executor.eval("App.g.V().has('Metadata.Type.Object.Data_Source', eq('Object.Data_Source'))\n" +
                      ".count().next().toString()").get().toString();
      assertEquals("8", countDataSources);

//    test for COUNT(event Ingestions)
      String countEventIngestions =
              App.executor.eval("App.g.V().has('Metadata.Type.Event.Ingestion', eq('Event.Ingestion'))\n" +
                      ".count().next().toString()").get().toString();
      // expecting 1 less Event.Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals("7", countEventIngestions);

      String countObjectDataPolicy =
          App.executor.eval("App.g.V().has('Metadata.Type.Object.Data_Policy', eq('Object.Data_Policy'))\n" +
              ".count().next().toString()").get().toString();
      // expecting 1 less Event.Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals("7", countObjectDataPolicy);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void testSharepointMapeamentoDeProcesso() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-mapeamento-de-processo.json",  "$.queryResp[*].fields",
              "sharepoint_mapeamentos");

//    test for COUNT(dataSources)
      String countDataProcedures =
              App.executor.eval("App.g.V().has('Metadata.Type.Object.Data_Procedures', eq('Object.Data_Procedures'))\n" +
                      ".count().next().toString()").get().toString();
      assertEquals("6", countDataProcedures);

//    test for COUNT(event Ingestions)
      String countEventIngestions =
              App.executor.eval("App.g.V().has('Metadata.Type.Event.Ingestion', eq('Event.Ingestion'))\n" +
                      ".count().next().toString()").get().toString();
      // expecting 1 less Event.Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals("6", countEventIngestions);

      String countObjectDataPolicy =
              App.executor.eval("App.g.V().has('Metadata.Type.Object.Data_Policy', eq('Object.Data_Policy'))\n" +
                      ".count().next().toString()").get().toString();
      // expecting 1 less Event.Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals("6", countObjectDataPolicy);

      String businessAreaResponsible =
              App.executor.eval("App.g.V().has('Object.Data_Procedures.BusinessAreaResponsible'" +
                      ", eq('Distribuidores - 31')).next().id().toString()").get().toString();

      String whyItIsCollectedValues =
              App.executor.eval("App.g.V().values('Object.Data_Procedures.Why_Is_It_Collected')" +
                      ".next()").get().toString();
      System.out.println("***************************************************\n" + whyItIsCollectedValues);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void testTotvsPloomes() throws InterruptedException {
    try {

      jsonTestUtil("ploomes1.json", "$.value", "ploomes_clientes");
      jsonTestUtil("totvs1.json", "$.objs", "totvs_protheus_sa1_clientes");

//    test for COMIDAS 1 as Person.Natural
      String userId0 =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('COMIDAS 1'))\n" +
                      ".next().id().toString()").get().toString();

//      test for COMIDAS 1 as Person.Organisation
//      String userId1 =
//              App.executor.eval("App.g.V().has('Person.Organisation.Short_Name', eq('COMIDAS 1'))\n" +
//                      ".next().id().toString()").get().toString();

//    test for Object.Data_Source.Name
      String userId2 =
              App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq('totvs/protheus/sa1_clientes'))" +
                      ".next().id().toString()").get().toString();
      String rootConnectionsQuery = "App.g.V(\"" + userId2 +"\").bothE().count().next().toString()";
      String rootConnections = App.executor.eval(rootConnectionsQuery).get().toString();
//      assertEquals(rootConnections,"5"); -- vertices podem variar dependendo do timing!

//    test COUNT(Edges) for COMIDAS 2
      String userId3 =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('COMIDAS 2'))" +
                      ".next().id().toString()").get().toString();
      String comidas2ConnectionsQuery = "App.g.V(\"" + userId3 +"\").bothE().count().next().toString()";
      String comidas2Connections = App.executor.eval(comidas2ConnectionsQuery).get().toString();
      assertEquals("5",comidas2Connections);

//    test COUNT(Edges) for Object.Email_Address
      String userId4 =
              App.executor.eval("App.g.V().has('Object.Email_Address.Email',eq('JONAS@COMIDA1.COM.BR'))" +
                      ".next().id().toString()").get().toString();
      String emailConnectionsQuery = "App.g.V(\"" + userId4 +"\").bothE().count().next().toString()";
      String emailConnections = App.executor.eval(emailConnectionsQuery).get().toString();
      assertEquals(emailConnections,"1");

//    test COUNT(Edges) for Object.Phone_Number
      String userId5 =
              App.executor.eval("App.g.V().has('Object.Phone_Number.Numbers_Only',eq('111111111'))" +
                      ".next().id().toString()").get().toString();
      String phoneConnectionsQuery = "App.g.V(\"" + userId5 +"\").bothE().count().next().toString()";
      String phoneConnections = App.executor.eval(phoneConnectionsQuery).get().toString();
      assertEquals(phoneConnections,"0");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void testSQL() {
    Gson gson = new Gson();
    RecordRequest req = gson.fromJson(jsonReq, RecordRequest.class);

    req.search.vid = "#-1:-1";
    req.search.relationship = "has_server";
    req.search.direction = "->";

    String sqlCount = req.getSQL(true);

    String expectedSqlCount = "SELECT COUNT(*) FROM (SELECT EXPAND(OUT('has_server')) FROM #-1:-1)";

    assertEquals(expectedSqlCount, sqlCount);

    String sql = req.getSQL(false);
    String expectedSql = "SELECT @rid as id,`Event.Data_Breach.Description`,`Event.Data_Breach.Impact`  FROM (SELECT EXPAND(OUT('has_server')) FROM #-1:-1  WHERE (( `Event.Data_Breach.Impact`  containsText  'OS' ) ) ) ORDER BY `Event.Data_Breach.Impact` DESC SKIP 0 LIMIT 100";
    assertEquals(expectedSql, sql);

    req.search.direction = "<-";
    sqlCount = req.getSQL(true);

    expectedSqlCount = "SELECT COUNT(*) FROM (SELECT EXPAND(IN('has_server')) FROM #-1:-1)";
    assertEquals(expectedSqlCount, sqlCount);

    req.search.vid = null;
    req.search.direction = null;
    req.search.relationship = null;
    sqlCount = req.getSQL(true);
    expectedSqlCount = "SELECT COUNT(*)  FROM `" +
        req.dataType +
        "`  WHERE (( `Event.Data_Breach.Impact`  containsText  'OS' ) ) ";
    assertEquals(expectedSqlCount, sqlCount);


  }

  @Test
  public void testSimpleGremlin() throws InterruptedException {


    String res = null;
    try {
      res = App.executor.eval("App.g.V().hasNext();").get().toString();
      System.out.println(res);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

  @Test
  public void testPhase1CsvDemoIngestion() throws InterruptedException {


    String res = null;
    try {
      Path resourceDirectory = Paths.get(".");
      String pwdAbsolutePath = resourceDirectory.toFile().getAbsolutePath();
      String csvPath = Paths.get(pwdAbsolutePath, "src", "test", "resources", "phase1.csv").toString();

      Reader in = new FileReader(csvPath);
      Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

      List<Map> listOfMaps = new ArrayList<>();
      for (CSVRecord record : records) {
        Map recMap = record.toMap();
        recMap.put("currDate", new Date().toString());
        listOfMaps.add(recMap);
      }


      Map<String, Object> bindings = new HashMap() {{
        put("listOfMaps", listOfMaps);
      }};

      res = App.executor.eval(phase1QueryStr, bindings).get().toString();
      System.out.println(res);

    } catch (ExecutionException | IOException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void testTotvsProtheusSa1Clientes() throws InterruptedException {

    jsonTestUtil("totvs1.json", "$.objs", "totvs_protheus_sa1_clientes");
//    jsonTestUtil("totvs2.json", "$.objs", "totvs_protheus_sa1_clientes");

    try {
      String userId =
          App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('JONAS LEO BATISTA')).next().id().toString()")
              .get().toString();

      Resource res = new Resource();
      GremlinRequest gremlinReq = new GremlinRequest();
      gremlinReq.setGremlin(
          "App.g.V().has('Person.Natural.Full_Name',eq('JONAS LEO BATISTA')).next().id().toString()");
      JsonObject obj = JsonParser.parseString(res.gremlinQuery(gson.toJson(gremlinReq))).getAsJsonObject();
      String stringifiedOutput = gson.toJson(obj);
      String res2;
      try {
        HttpClient client = HttpClients.createMinimal();

        HttpPost request = new HttpPost(URI.create("http://localhost:3001/home/gremlin"));

        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");

        StringEntity data = new StringEntity(gson.toJson(gremlinReq));
//        SerializableEntity data = new SerializableEntity(req);

        request.setEntity(data);


        res2 = IOUtils.toString(client.execute(request).getEntity().getContent());
        System.out.println(res2);


        HttpGet getRequest = new HttpGet(URI.create("http://localhost:3001/home/hello"));

//        request.setHeader("Content-Type", "application/json");
//        request.setHeader("Accept", "application/json");

//        StringEntity getData = new StringEntity(gson.toJson(req));
//        SerializableEntity data = new SerializableEntity(req);

//        request.setEntity(data);


        res2 = IOUtils.toString(client.execute(getRequest).getEntity().getContent());


        System.out.println(res2);

      } catch (IOException e) {
        e.printStackTrace();
        assertNull(e);
      }


      assertEquals(userId, ((obj.get("result").getAsJsonObject())
          .getAsJsonObject().get("data").getAsJsonObject()).get("@value").getAsJsonArray().get(0).getAsString());

      Map<String, Object> bindings = new HashMap() {{
        put("pg_id", userId);
        put("pg_templateText", "eyUgc2V0IHBvc3NpYmxlTWF0Y2hlcyA9IHB2OnBvc3NpYmxlTWF0Y2hlcyhjb250ZXh0LmlkLCd7Ik9iamVjdC5FbWFpbF9BZGRyZXNzIjogMTAuNSwgIk9iamVjdC5JZGVudGl0eV9DYXJkIjogOTAuNSwgIkxvY2F0aW9uLkFkZHJlc3MiOiAxMC4xLCAiT2JqZWN0LlBob25lX051bWJlciI6IDEuMCwgIk9iamVjdC5TZW5zdGl2ZV9EYXRhIjogMTAuMCwgIk9iamVjdC5IZWFsdGgiOiAxLjAsICJPYmplY3QuQmlvbWV0cmljIjogNTAuMCAsICJPYmplY3QuSW5zdXJhbmNlX1BvbGljeSI6IDEuMH0nKSAlfQp7JSBzZXQgbnVtTWF0Y2hlcyA9IHBvc3NpYmxlTWF0Y2hlcy5zaXplKCkgJX0KeyUgaWYgbnVtTWF0Y2hlcyA9PSAwICV9Cnt7IGNvbnRleHQuUGVyc29uX05hdHVyYWxfRnVsbF9OYW1lfX0gw6kgbyDDum5pY28gcmVnaXN0cm8gbm8gc2lzdGVtYS4KCnslIGVsc2UgJX0Ke3sgY29udGV4dC5QZXJzb25fTmF0dXJhbF9GdWxsX05hbWV9fSBDb3JyZXNwb25kZSBwb3RlbmNpYWxtZW50ZSBhIHt7IG51bU1hdGNoZXMgfX0gcmVnaXN0cm97JS0gaWYgbnVtTWF0Y2hlcyAhPSAxIC0lfXN7JSBlbmRpZiAlfS4KCgoKICB7eyAiPHRhYmxlIHN0eWxlPSdtYXJnaW46IDVweCc+PHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+VGl0dWxhcjwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+UGVyY2VudHVhbDwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+UHJvcHJpZWRhZGVzIGVtIENvbXVtPC90aD48L3RyPiIgfX0KICB7JSBmb3IgaXRlbSBpbiBwb3NzaWJsZU1hdGNoZXMuZW50cnlTZXQoKSAlfQogIHt7ICAiPHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPjx0ZCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPiUuMmYlJTwvdGQ+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPiIgfCBmb3JtYXQgKGl0ZW0ua2V5LlBlcnNvbl9OYXR1cmFsX0Z1bGxfTmFtZSAsIGl0ZW0udmFsdWUgKiAxMDAuMCwgaXRlbS5rZXkuTGFiZWxzX0Zvcl9NYXRjaCApIH19CiAgeyUgZW5kZm9yICV9CiAge3sgIjwvdGFibGU+IiB9fQoKeyUgZW5kaWYgJX0=");
      }};

      String report = App.executor.eval("renderReportInBase64(pg_id,pg_templateText)", bindings).get().toString();
      System.out.println(report);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void testPloomesClientes() throws InterruptedException {

    jsonTestUtil("ploomes1.json", "$.value", "ploomes_clientes");
//    jsonTestUtil("ploomes1.json", "$.value", "ploomes_clientes");

    try {
      String userId =
          App.executor.eval("App.g.V().has('Person.Organisation.Short_Name',eq('Pessoa Nova5')).next().id().toString()")
              .get().toString();

      Resource res = new Resource();
      GremlinRequest gremlinReq = new GremlinRequest();
      gremlinReq.setGremlin(
          "App.g.V().has('Person.Organisation.Short_Name',eq('Pessoa Nova5')).next().id().toString()");
      JsonObject obj = JsonParser.parseString(res.gremlinQuery(gson.toJson(gremlinReq))).getAsJsonObject();
//      Gson gson = new Gson();
      String stringifiedOutput = gson.toJson(obj);
      String res2;
      try {
        HttpClient client = HttpClients.createMinimal();

        HttpPost request = new HttpPost(URI.create("http://localhost:3001/home/gremlin"));

        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");

        StringEntity data = new StringEntity(gson.toJson(gremlinReq));
//        SerializableEntity data = new SerializableEntity(req);

        request.setEntity(data);


        res2 = IOUtils.toString(client.execute(request).getEntity().getContent());
        System.out.println(res2);


        HttpGet getRequest = new HttpGet(URI.create("http://localhost:3001/home/hello"));

//        request.setHeader("Content-Type", "application/json");
//        request.setHeader("Accept", "application/json");

//        StringEntity getData = new StringEntity(gson.toJson(req));
//        SerializableEntity data = new SerializableEntity(req);

//        request.setEntity(data);


        res2 = IOUtils.toString(client.execute(getRequest).getEntity().getContent());


        System.out.println(res2);

      } catch (IOException e) {
        e.printStackTrace();
        assertNull(e);
      }


//      String b = "DeBugger Stop!";


      assertEquals(userId, (((obj.get("result").getAsJsonObject())
          .getAsJsonObject().get("data")).getAsJsonObject().get("@value")).getAsJsonArray().get(0).getAsString());

      Map<String, Object> bindings = new HashMap() {{
        put("pg_id", userId);
        put("pg_templateText", "eyUgc2V0IHBvc3NpYmxlTWF0Y2hlcyA9IHB2OnBvc3NpYmxlTWF0Y2hlcyhjb250ZXh0LmlkLCd7Ik9iamVjdC5FbWFpbF9BZGRyZXNzIjogMTAuNSwgIk9iamVjdC5JZGVudGl0eV9DYXJkIjogOTAuNSwgIkxvY2F0aW9uLkFkZHJlc3MiOiAxMC4xLCAiT2JqZWN0LlBob25lX051bWJlciI6IDEuMCwgIk9iamVjdC5TZW5zdGl2ZV9EYXRhIjogMTAuMCwgIk9iamVjdC5IZWFsdGgiOiAxLjAsICJPYmplY3QuQmlvbWV0cmljIjogNTAuMCAsICJPYmplY3QuSW5zdXJhbmNlX1BvbGljeSI6IDEuMH0nKSAlfQp7JSBzZXQgbnVtTWF0Y2hlcyA9IHBvc3NpYmxlTWF0Y2hlcy5zaXplKCkgJX0KeyUgaWYgbnVtTWF0Y2hlcyA9PSAwICV9Cnt7IGNvbnRleHQuUGVyc29uX05hdHVyYWxfRnVsbF9OYW1lfX0gw6kgbyDDum5pY28gcmVnaXN0cm8gbm8gc2lzdGVtYS4KCnslIGVsc2UgJX0Ke3sgY29udGV4dC5QZXJzb25fTmF0dXJhbF9GdWxsX05hbWV9fSBDb3JyZXNwb25kZSBwb3RlbmNpYWxtZW50ZSBhIHt7IG51bU1hdGNoZXMgfX0gcmVnaXN0cm97JS0gaWYgbnVtTWF0Y2hlcyAhPSAxIC0lfXN7JSBlbmRpZiAlfS4KCgoKICB7eyAiPHRhYmxlIHN0eWxlPSdtYXJnaW46IDVweCc+PHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+VGl0dWxhcjwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+UGVyY2VudHVhbDwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+UHJvcHJpZWRhZGVzIGVtIENvbXVtPC90aD48L3RyPiIgfX0KICB7JSBmb3IgaXRlbSBpbiBwb3NzaWJsZU1hdGNoZXMuZW50cnlTZXQoKSAlfQogIHt7ICAiPHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPjx0ZCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPiUuMmYlJTwvdGQ+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPiIgfCBmb3JtYXQgKGl0ZW0ua2V5LlBlcnNvbl9OYXR1cmFsX0Z1bGxfTmFtZSAsIGl0ZW0udmFsdWUgKiAxMDAuMCwgaXRlbS5rZXkuTGFiZWxzX0Zvcl9NYXRjaCApIH19CiAgeyUgZW5kZm9yICV9CiAge3sgIjwvdGFibGU+IiB9fQoKeyUgZW5kaWYgJX0=");
      }};

      String report = App.executor.eval("renderReportInBase64(pg_id,pg_templateText)", bindings).get().toString();
      System.out.println(report);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }


}
