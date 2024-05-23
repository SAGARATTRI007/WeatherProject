package MyPackage;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public MyServlet() {
        super();
        
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		Setup API
		String apikey="84d24136a692f7371aef4922bc8ab48b";
		
//		get city name from the form Input
		String city=request.getParameter("city");
		
//		create URLfor WeatherApp API request
		String apiUrl="https://api.openweathermap.org/data/2.5/weather?q=" +city+ "&appid=" +apikey;
		
//		API integration
		URL url=new URL(apiUrl);
		HttpURLConnection connection=(HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		
//		Data comes in form of streams from the network
		InputStream inputstream=connection.getInputStream();
		
//	Read the Stream data with the hrlp of Object of StreamReader class
		InputStreamReader reader=new InputStreamReader(inputstream);
		
//		Want to Store data in String
		StringBuilder responsecontant=new StringBuilder();
		Scanner scanner=new Scanner(reader);
		
		while(scanner.hasNext()) {
			responsecontant.append(scanner.nextLine());
		}
		scanner.close();
		
//		TypeCasting: Parsing the data into JSON
		Gson gson=new Gson();
		JsonObject jsonobject=gson.fromJson(responsecontant.toString(), JsonObject.class);
		
//		Date & Time
		long dateTimestamp=jsonobject.get("dt").getAsLong()*1000;
		String date=new Date(dateTimestamp).toString();
		
//		Temperature
		double temperatureKelvin=jsonobject.getAsJsonObject("main").get("temp").getAsDouble();
		int temperatureCelsius=(int) (temperatureKelvin-273.15);
		
//		Humidity
		int humidity=jsonobject.getAsJsonObject("main").get("humidity").getAsInt();
//		wind Speed
		double windSpeed=jsonobject.getAsJsonObject("wind").get("speed").getAsDouble();
//		Weather Condition
		String weatherCondition=jsonobject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
		
//		set the data as request attributes (for sending to the jsp page)
		request.setAttribute("date", date);
		request.setAttribute("city", city);
		request.setAttribute("temperature", temperatureCelsius);
		request.setAttribute("humidity", humidity);
		request.setAttribute("windSpeed", windSpeed);
		request.setAttribute("weathercondition", weatherCondition);
		request.setAttribute("weatherdata", responsecontant.toString());
		
		connection.disconnect();
		
//		Forward the request to the weather.jsp page for rendering
		RequestDispatcher rd=request.getRequestDispatcher("/weather.jsp");
		rd.forward(request, response);
		
		
	}

}
