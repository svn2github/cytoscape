package csplugins.isb.dreiss.httpdata.handlers.sbeams;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.HttpURLConnection;

//==================================================================================
// Matt Fitzgibbon, ma2t@u.washington.edu
// 3 Feb 2003
//
// Can specifiy keystore on command line with -Djavax.net.ssl.trustStore=samplecerts
//==================================================================================

public class HttpsClient {
   protected static class Resp {
      String contentType = null;
      String cookie = null;
      String content = null;
   }

   public static void main(String argv[]) throws Exception {
      String userName = argv[ 0 ];
      String password = argv[ 1 ];

      String url = "https://db.systemsbiology.net/sbeams/cgi/Proteomics/BrowseBioSequence.cgi";
      String params = "apply_action=QUERY&row_limit=20&QUERY_NAME=PR_BrowseBioSequence&biosequence_set_id=14&action=QUERY&biosequence_name_constraint=YHL040C%25&output_mode=tsv";

      // CHANGE HERE AS APPROPRIATE
      String cookie = gimmieCookie(userName, password);

      if ( cookie != null ) {
	 System.out.println("Proceeding to POST with cookie: " + cookie);
	 Resp res = postRequest(url, params, cookie);
	 System.out.println("Content-Type: " + res.contentType);
	 System.out.println("Content:\n" + res.content);
      } else {
	 System.out.println("Got no cookies");
      }
   }

   public static String gimmieCookie(String username, String password)
      throws Exception {

      String url = "https://db.systemsbiology.net/sbeams/cgi/main.cgi";

      StringBuffer params = new StringBuffer();

      params.append("username");
      params.append("=");
      params.append(URLEncoder.encode(username, "UTF8"));
      params.append("&");
      params.append("password");
      params.append("=");
      params.append(URLEncoder.encode(password, "UTF8"));
      params.append("&");
      params.append("login");
      params.append("=");
      params.append(URLEncoder.encode(" Login ", "UTF8"));

      Resp res = postRequest(url, params.toString());
      return res.cookie;
   }

   public static Resp postRequest(String urlString, String params)
      throws Exception {
      String cookie = null;
      return postRequest(urlString, params, cookie);
   }

   public static Resp postRequest(String urlString, String params, String cookie)
      throws Exception {

      URL url = new URL(urlString);
      HttpURLConnection uc = (HttpURLConnection)url.openConnection();

      uc.setDoInput(true);
      uc.setDoOutput(true);
      uc.setUseCaches(false);
      uc.setAllowUserInteraction(false);
      uc.setRequestMethod("POST");
      uc.setRequestProperty("ContentType", "application/x-www-form-urlencoded");
      uc.setRequestProperty("User-Agent", "HelloThereEric");

      if ( cookie != null ) {
	 uc.setRequestProperty("Cookie", cookie);
      }

      PrintStream out = new PrintStream(uc.getOutputStream());
      out.print(params);
      out.flush();
      out.close();

      uc.connect();

      StringBuffer sb = new StringBuffer();
      String inputLine;

      BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));

      while ((inputLine = in.readLine()) != null) {
	 sb.append(inputLine + "\n");
      }

      in.close();

      Resp res = new Resp();

      res.content = sb.toString();
      res.contentType = uc.getHeaderField("Content-Type");
      res.cookie = uc.getHeaderField("Set-Cookie");

      return res;
   }
}
