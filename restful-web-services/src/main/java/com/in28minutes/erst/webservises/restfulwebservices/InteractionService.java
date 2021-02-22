//package com.in28minutes.erst.webservises.restfulwebservices;
//
//
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.util.concurrent.TimeUnit;
//
//import javax.json.Json;
//import javax.json.JsonArray;
//import javax.json.JsonObject;
//import javax.json.JsonValue;
//import javax.ws.rs.HttpMethod;
//
//import org.json.JSONObject;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonParser;
//
//import dk.topdanmark.cs.applikation.CSCProfile;
//import dk.topdanmark.cs.log.CLogger;
//import dk.topdanmark.pp.primopc.model.CKlientModel;
//import dk.topdanmark.pp.primopc.model.bean.IMuligeDokumenter;
//
///**
// * @author yaj
// */
//public class InteractionService extends WebServiceInitializer {
///**system variables
// */
//  private static final String INTERACTION_SERVICE = System.getProperty("interaction-service");
//  // interaction service status constants
//  private static final int STATUS_POLL_INTERVAL_IN_MILLIS = 1000;
//  private static final int STATUS_RETRY_COUNT = 10;
//  //singleton reference
//  private static final InteractionService instance = new InteractionService(); 
//  private static final String CLASS_NAME = InteractionService.class.getName();
// 
//  
//  private InteractionService() {}
//
//
//  /**
//   * Returns the <code>InteractionService</code> instance.
//   * @return the <code>InteractionService</code> instance
//   */
//  public static InteractionService getInstance(){
//    return instance;
//  }
//
//  /**
//   * @param fileId 
//   * @param dokumentIdName 
//   * @param documentName 
//   * @param isAftaleRapport 
//   * @param interessentNr 
//   * @param tilbudsNr 
//   * @return String fileObjectId
//   */
//  public String doPrintJobReady(String fileId, String dokumentIdName, String documentName, boolean isAftaleRapport, String interessentNr, String tilbudsNr) {
//    try{
//      this.moSessionData = new EsignaturSessionData();
//      String metadataId = settingMetadataPRIMOquote(fileId, dokumentIdName, documentName, isAftaleRapport, interessentNr, tilbudsNr);
//      String fileObjectId = "";
//      //      String interactionId = statusInteraction(metadataId);
////      String fileObjectId = getDetailsInteraction(interactionId);
//      return fileObjectId ;
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    return null;
//  }
//  /**
//   * @param fileId 
//   * @param documentIdName 
//   * @param documentName2 
//   * @param isAftaleRapport 
//   * @param interessentNr 
//   * @param tilbudsNr 
//   * @return String
//   * @throws IOException 
//   */
//  private String settingMetadataPRIMOquote(String fileId, String documentIdName, String documentName, boolean isAftaleRapport, String interessentNr, String tilbudsNr) throws IOException {
//    String url = INTERACTION_SERVICE + "interaction?X-TOPUP-ENV=" + moSessionData.getEnvironment();
//    System.out.println("URL : " +url);
//    HttpURLConnection con = setHttpURLConnection(url, "settingMetadataPRIMOquote()", HttpMethod.POST);
//    // build metadata json
//    String metadata = createMetadata(fileId, documentIdName, documentName, isAftaleRapport, interessentNr, tilbudsNr);   
//    try (OutputStream outputSteam = con.getOutputStream()){
//      byte[] input = metadata.getBytes(StandardCharsets.UTF_8);
//      outputSteam.write(input, 0, input.length);
//    }    
//
//    int responseCode = con.getResponseCode();
//    CLogger.information(CLASS_NAME, "settingMetadataPRIMOquote()", "HTTP " + responseCode, CSCProfile.getInstance().getLogContext());
//    if (responseCode != HttpURLConnection.HTTP_ACCEPTED){
//      String response = readResponse(con.getErrorStream());
//      con.disconnect();
//      throw new IOException("Could not upload metadata! HTTP " + responseCode + " - response: " + response);
//    }
//
//    String response = readResponse(con.getInputStream());
//    con.disconnect();
//
//    CLogger.information(CLASS_NAME, "settingMetadataPRIMOquote()", "HTTP " + responseCode + " - response: " + response, CSCProfile.getInstance().getLogContext());
//
//    JSONObject responseObj = new JSONObject(response);
//    return "" + responseObj.getInt("id");
//  }
//  
//  private HttpURLConnection setHttpURLConnection(String url, String methodName, String requestMethod) throws MalformedURLException, IOException{
//    CLogger.information(CLASS_NAME, methodName, "start", CSCProfile.getInstance().getLogContext());  
//    HttpURLConnection con =  (HttpURLConnection)new URL(url).openConnection();
//    con.setRequestMethod(requestMethod);
//    con.setRequestProperty("x-pts-sessionid", moSessionData.getSessionId());
//    con.setRequestProperty("x-topup-env", moSessionData.getEnvironment());
//    con.setRequestProperty("x-top-traceid", moSessionData.getTopTraceId());
//    con.setRequestProperty("apim-application-token", WSO2_HEADER);
//    con.setRequestProperty("content-type", "application/json");
//    con.setDoOutput(true);
//    return con;
//  }
//
//  private String createMetadata(String fileId, String documentIdName, String documentName, boolean isAftaleRapport, String interessentNr, String tilbudsNr) {
//    String title = createTitle(documentIdName, isAftaleRapport);
//    JsonObject  jsonSecurity = Json.createObjectBuilder()
//        .add("level", "IKKESENSITIV")
//        .add("owner", "SKADESFORSIKRING-POLICE")
//        .build();
//
//    JsonArray contactMethodsJsonArray = Json.createArrayBuilder().
//        add(Json.createObjectBuilder()
//            .add("type", "CentralPrint")// Means save only
//            .add("distribute", JsonValue.TRUE)// Means save only
//            .add("jobName", "ITTest")) //new
//        .build();// Means save only
//
//    // VIEWABLEFILE
//    JsonArray fileListJsonArray =  Json.createArrayBuilder().
//        add(Json.createObjectBuilder()
//            .add("id", fileId)
//            .add("extension", "pdf")
//            .add("name", documentName)
//            ).build(); 
//
//    JsonArray communicationsJsonArray =  Json.createArrayBuilder().
//        add(Json.createObjectBuilder()
//            .add("title", title)
//            .add("templateName", "PPCTILBU")
//            .add("type",  "QUOTE")
//            .add("direction",  "IN")
//            .add("contactMethods", contactMethodsJsonArray)// Means save only          
//            .add("fileList", fileListJsonArray)
//            ).build();
//
//    JsonArray partiesJsonArray = Json.createArrayBuilder().
//        add(Json.createObjectBuilder()
//            .add("partyId", interessentNr) // The customer number
//            .add("role", "CUSTOMER")
//            .add("type", "WITH")
//            .add("allowedToRead", JsonValue.TRUE)).build();
//
//    JsonArray referencesJsonArray  = Json.createArrayBuilder().
//        add(Json.createObjectBuilder()
//            .add("source", "PRIMOTILB")
//            .add("type", "REFERENCE")
//            .add("value", tilbudsNr)).build();
//
//    JsonObject clientJsonObj =  Json.createObjectBuilder()
//        .add("data", "Your custom data, if you want callback")     
//        .add( "key", " 6ac82f15-80f1-4498-b12b-4bde783e03c9").build(); //TODO WHAT IS THIS KEY?
//
//    // JOURNAL
//    JsonObject builder = Json.createObjectBuilder()
//        .add("client", clientJsonObj)
//        .add("security", jsonSecurity)
//        .add("category", "COMMUNICATION")
//        .add("company", "TOPDANMARK_FORSIKRING")
//        .add("employee", moSessionData.getUser())
//        .add("department", "IT Skade")         //new
//        .add("responsibleDepartment", "IT EBusiness")  //new
//        .add("application", "PRIMO")
//        .add("businessArea", "POLICY_NONLIFE")
//        .add("brand", "TD")
//        .add("description", title)  
//        .add("parties", partiesJsonArray)
//        .add("references", referencesJsonArray)
//        .add("communications", communicationsJsonArray)        
//        .add("businessApplication", "PRIMO")
//        .build();
//    
//    Gson gson = new GsonBuilder().setPrettyPrinting().create();
//    String json = gson.toJson(new JsonParser().parse(builder.toString()));
//   
//    return json;
//  }
//
//  private static String createCanalName(String documentName) {
//    return IMuligeDokumenter.PRINTTYPE_DIGITALT.equals(documentName) ? "MitTopdanmark" : "PDF-Print";
//  }
//
//  private static String createTitle(String dokumentNavn, boolean isAftaleRapport){
//    String tilbudsNr = CKlientModel.getInstance().getTilbud().getTilbudsNr().getValue();
//    // Rebranding projekt ud af DB, så brand på kom.ovs. altid = Top
//    String brandKode = "Top";
//    boolean digitaltTilbud = IMuligeDokumenter.PRINTTYPE_DIGITALT.equals(dokumentNavn);
//    boolean underskriftDokument = IMuligeDokumenter.PRINTTYPE_UNDERSKRIFT.equals(dokumentNavn);
//    String titel;
//    if (isAftaleRapport) {
//      titel = "PPC/" + brandKode + " aftalerapport " + tilbudsNr;
//    } else if (digitaltTilbud) {
//      titel = "PPC/" + brandKode + " digitalt forslag " + tilbudsNr;
//    } else if (!underskriftDokument) {
//      titel = "PPC/" + brandKode + " forslag " + tilbudsNr;
//    } else {
//      titel = "PPC/" + brandKode + " underskriftdokument " + tilbudsNr;
//    }
//    return titel;
//  }
//
//  /**
//   * @param metadataId 
//   * @return [an instance of ...]
//   * @throws MalformedURLException 
//   * @throws IOException 
//   * 
//   * @exception MalformedURLException, IOException
//   */
//
//  public String statusInteraction(String metadataId) throws MalformedURLException, IOException{
//    JSONObject responseObj = null;
//    for (int retry = 1; retry <= STATUS_RETRY_COUNT; retry++) {
//      // TODO consider exponential delay
//      //TimeUnit.SECONDS.sleep(STATUS_POLL_INTERVAL_IN_SECONDS*retry*retry);
//
//      delay(STATUS_POLL_INTERVAL_IN_MILLIS);
//      String url = INTERACTION_SERVICE + "interaction/" + metadataId + "/status";
//      HttpURLConnection con = setHttpURLConnection(url, "statusInteraction()", HttpMethod.GET);    
//      int responseCode = checkResponceCode(con, "Could not get status for interaction! HTTP ");
//      String response = readResponse(con.getInputStream());   
//      CLogger.information(CLASS_NAME, "statusInteraction()", "HTTP " + responseCode + " - response: " + response, CSCProfile.getInstance().getLogContext());
//      responseObj = new JSONObject(response);
//      con.disconnect();
//
//      String status = responseObj.getString("status");
//      if ("Success".equalsIgnoreCase(status)) {
//        // pdf document is ready
//        break;
//      }      
//    }
//    if (responseObj == null) {
//      throw new IOException("Could not parse response from interaction status!");
//    }
//    return "" + responseObj.getJSONObject("resource").getInt("id");
//  }
//  
//  /**
//   * @param interactionId 
//   * @return [an instance of ...]
//   * @throws MalformedURLException 
//   * @throws IOException 
//   * 
//   * @exception MalformedURLException, IOException
//   */
//  public String getDetailsInteraction(String interactionId) throws MalformedURLException, IOException{
//    String url = INTERACTION_SERVICE + "interaction/" + interactionId;
//    HttpURLConnection con = setHttpURLConnection(url, "getDetailsInteraction()", HttpMethod.GET);
//
//    int responseCode = checkResponceCode(con,  "Could not get status for interaction!\n\nCheck the users permissions for interaction-service / OSIR.\n\nHTTP ");
//    String response = readResponse(con.getInputStream());   
//    con.disconnect();
//    CLogger.information(CLASS_NAME, "getDetailsInteraction()", "HTTP " + responseCode + " - response:\n" + response, CSCProfile.getInstance().getLogContext());
//
//    JSONObject responseObj = new JSONObject(response);
//    return "" + responseObj.getJSONArray("communications").getJSONObject(0).getJSONArray("fileList").getJSONObject(0).getString("fileObjectId");
//  }
//
//
//  private int checkResponceCode(HttpURLConnection con, String exceptionText) throws IOException{
//    int responseCode = con.getResponseCode();
//    if (responseCode != HttpURLConnection.HTTP_OK) {
//      String response = readResponse(con.getErrorStream());
//      con.disconnect();
//      throw new IOException(exceptionText + responseCode + " - response: " + response);
//    }
//    return responseCode;
//  }
//
//  private void delay(int delay){
//    try {
//      TimeUnit.MILLISECONDS.sleep(delay); 
//    }catch (InterruptedException e)  {
//      // ignore
//    }
//  }
//
//}
