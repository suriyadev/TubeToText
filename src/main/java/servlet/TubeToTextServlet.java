package servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@WebServlet(
        name = "tubetotext", 
        urlPatterns = {"/"}
    )
public class TubeToTextServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	   List languages;
   		
		   String videoURL = request.getParameter("url");
 
	         Map<String, String> result = new HashMap<String, String>();

	         result.put("videoId", videoURL);

	         try {

	             Document doc;
	             doc = Jsoup.connect(videoURL).get();

	             Elements newsHeadlines = doc.select("script");

	             result.put("title", doc.title());

	             String captionUrl = "";
	             languages = new ArrayList<String>();

	             for (Element headline : newsHeadlines) {

	                 if (headline.data().contains("player_response") == true) {

	                     String value = headline.data();

	                     int i = value.indexOf("{\"");

	                     value = value.substring(i);

	                     JSONObject jsonObj = new JSONObject(value.substring(0, value.indexOf("ytplayer.load")));

	                     JSONObject j2 = new JSONObject(jsonObj.get("args").toString());
	                     JSONObject j5 = new JSONObject(j2.get("player_response").toString());
	                     JSONObject j3 = new JSONObject(j5.get("captions").toString());
	                     JSONObject j4 = new JSONObject(j3.get("playerCaptionsTracklistRenderer").toString());
	                     JSONArray resultCaptions = new JSONArray(j4.get("captionTracks").toString());


	                     captionUrl = new JSONObject(resultCaptions.get(0).toString()).get("baseUrl").toString();

	                     for(int j = 0 ; i < resultCaptions.length();j++) {
	                         languages.add(new JSONObject(resultCaptions.get(j).toString()).get("languageCode").toString());
	                     }

	                     result.put("text", captionUrl);

	                  
	                     break;
	                 }

	             }

	         } catch (Exception e1) {
	             // TODO Auto-generated catch block
	        	   result.put("text", "No Captions Found for this Video");

	         }

	         Map res =  result;
	         String xmlDataUrI = "";
	         ByteArrayOutputStream outstream;

	         URLConnection conn;
	         try {
	             conn = new URL(res.get("text").toString()).openConnection();

	             InputStream is = conn.getInputStream();

	             outstream = new ByteArrayOutputStream();

	             byte[] buffer = new byte[4096];
	             int len;

	             while ((len = is.read(buffer)) > 0) {
	                 outstream.write(buffer, 0, len);
	             }
	             outstream.close();

	             xmlDataUrI = new String(outstream.toByteArray(), "UTF-8");

	         } catch (MalformedURLException e) { // TODO Auto-generated catch block
	     		response.getWriter().append("No Captions Found for this Video");
	        	 } catch (IOException e) { // TODO Auto-generated catch
	        		 response.getWriter().append("No Captions Found for this Video");
	 	        	 //e.printStackTrace();
	         }

	         String uri = xmlDataUrI;

	         String s = "";
	         try {
	             List<String> lines = new ArrayList();
	             JSONObject xmlJSONObj = XML.toJSONObject(uri);

	             JSONObject jj = new JSONObject(xmlJSONObj.get("transcript").toString());

	             JSONArray array = new JSONArray(jj.get("text").toString());



	             for (int i = 0 ; i < array.length();i++) {
	                 JSONObject job = new JSONObject(array.get(i).toString());

	                 String temp = job.get("content").toString() + " ";
	                 lines.add(temp);
	                 s += temp;

	             }

	             s = s.replaceAll("#CCCCCC", "#000000");

	             s = s.replaceAll("#E5E5E5", "#000000");

	             s = s.replaceAll("<font color=\"#000000\">", " ");

	             s = s.replaceAll("</font>", " ");

	             s = s.replaceAll("&#39;", "'");
	         }catch (Exception e){
	        	 
	        	 response.getWriter().append("No Captions Found for this Video");
		        	
	        	 
	         }

		
		
		
		response.getWriter().append(s);
    	
    	
    	
    	
    	
    	
    	
    	
    }
    
}
