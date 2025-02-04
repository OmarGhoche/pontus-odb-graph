package com.pontusvision.gdpr;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(9)

public class PVSharepointDSARTests extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  @Test
  public void test00001SharepointDSAR() throws InterruptedException {

    jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json",
            "$.queryResp[*].fields", "sharepoint_mapeamentos");

    jsonTestUtil("sharepoint/pv-extract-sharepoint-dsar.json", "$.queryResp[*].fields", "sharepoint_dsar");

    try {

      String palmitosSARType =
              App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('PALMITOS SA'))" +
                      ".out('Made_SAR_Request').properties('Event_Subject_Access_Request_Request_Type')" +
                      ".value().next().toString()").get().toString();
      assertEquals("Exclusão de e-mail", palmitosSARType, "DSAR Request type made by Palmitos SA");

      String palmitosSARCreateDate =
              App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('PALMITOS SA'))" +
                      ".out('Made_SAR_Request').properties('Event_Subject_Access_Request_Metadata_Create_Date')" +
                      ".value().next().toString()").get().toString();
//      palmitosSARCreateDate = palmitosSARCreateDate.replaceAll("... 2021", "GMT 2021");
      assertEquals(dtfmt.parse("Tue May 18 12:01:00 GMT 2021"), dtfmt.parse(palmitosSARCreateDate),
              "DSAR Request date - Palmitos SA");

      String completedDSARCount =
              App.executor.eval("App.g.V().has('Event_Subject_Access_Request_Status', eq('Completed'))" +
                      ".count().next().toString()").get().toString();
      assertEquals("1", completedDSARCount, "Only 1 DSAR has being Completed!");

      String emailChannelDSARCount =
              App.executor.eval("App.g.V().has('Event_Subject_Access_Request_Request_Channel', " +
                      "eq('Via e-mail')).count().next().toString()").get().toString();
      assertEquals("2", emailChannelDSARCount, "Two DSARs where reaceived via e-mail");

      String angeleBxlDSARType =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('ANGÈLE BRUXÈLE'))" +
                      ".out('Made_SAR_Request').properties('Event_Subject_Access_Request_Request_Type')" +
                      ".value().next().toString()").get().toString();
      assertEquals("Atualização de Endereço", angeleBxlDSARType,
              "Angèle Bruxèle wants to update her Address");

      String miltonOrgDSARStatus =
              App.executor.eval("App.g.V().has('Person_Organisation_Registration_Number', " +
                      "eq('45232190000112')).out('Made_SAR_Request')" +
                      ".properties('Event_Subject_Access_Request_Status').value().next().toString()").get().toString();
      assertEquals("Denied", miltonOrgDSARStatus, "Milton's Company's DSAR Request was Denied!");


//      String DSARTotalCount =
//              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MARGORE PROXANO'))" +
//                      ".out('Made_SAR_Request').as('DSAR').out('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
//                      ".in('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event').dedup()" +
//                      ".count().next().toString()").get().toString();
//      assertEquals("4", DSARTotalCount, "Total count of DSARs: 4");

      String fromDsarToRopa =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MARGORE PROXANO'))" +
                      ".out('Made_SAR_Request').as('DSAR').out('Has_DSAR').as('DSAR_Group').out('Has_DSAR')" +
                      ".as('RoPA').has('Object_Data_Procedures_Form_Id', eq('401'))" +
                      ".values('Object_Data_Procedures_Description')" +
                      ".next().toString()").get().toString();
      assertEquals("Recebimento dos dados via e-mail e envio ao departamentos: Jxxxyyy e Fzzzzzzzzzz.",
              fromDsarToRopa, "RoPA's Data Procedures' Description");

      String fromDsarToRopa2 =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('ANGÈLE BRUXÈLE'))" +
                              ".out('Made_SAR_Request').as('DSAR').out('Has_DSAR').as('DSAR_Group').out('Has_DSAR')" +
                              ".as('RoPA').has('Object_Data_Procedures_Form_Id', eq('402'))" +
                              ".values('Object_Data_Procedures_Interested_Parties_Consulted').next().toString()")
                      .get().toString();
      assertEquals("Jurídico Snowymountain", fromDsarToRopa2,"RoPA's Interested Parties");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}