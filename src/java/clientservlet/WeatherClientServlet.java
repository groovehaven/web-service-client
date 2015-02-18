package clientservlet;
import com.cdyne.ws.weatherws.ArrayOfForecast;
import com.cdyne.ws.weatherws.ArrayOfWeatherDescription;
import com.cdyne.ws.weatherws.Forecast;
import com.cdyne.ws.weatherws.ForecastReturn;
import com.cdyne.ws.weatherws.Weather;
import com.cdyne.ws.weatherws.WeatherDescription;
import com.cdyne.ws.weatherws.WeatherReturn;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceRef;

/**
*
* @author Stephen Palmer 1/25/15
*
*/
@WebServlet(name = "WeatherClientServlet", urlPatterns = {"/WeatherClientServlet"})
public class WeatherClientServlet extends HttpServlet {

@WebServiceRef(wsdlLocation = "WEB-INF/wsdl/wsf.cdyne.com/WeatherWS/Weather.asmx.wsdl")
private Weather service;

/**
 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
 * methods.
 *
 * @param request servlet request
 * @param response servlet response
 * @throws ServletException if a servlet-specific error occurs
 * @throws IOException if an I/O error occurs
 */
protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");
    HttpSession session = request.getSession();

    if (request.getParameter("currentWeatherButton") != null) {
        try (PrintWriter out = response.getWriter()) {
            ArrayOfWeatherDescription weatherDescriptionData = getWeatherInformation();
            List<WeatherDescription> listWD = weatherDescriptionData.getWeatherDescription();
            String zipCode = request.getParameter("zip");

            WeatherReturn currentWeather = getCityWeatherByZIP(zipCode);
            String city = currentWeather.getCity();
            String state = currentWeather.getState();
            String temp = currentWeather.getTemperature();
            Short weatherDescriptionID = currentWeather.getWeatherID();

            WeatherDescription cityWD = listWD.get(weatherDescriptionID - 1);

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Current Weather Conditions</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Current Weather Conditions For " + city + ", " + state + "</h1>");

            out.println("<figure><img src=\"" + cityWD.getPictureURL() + "\">"
                    + " <figcaption>" + cityWD.getDescription() + "</figcaption></figure>");
            out.println("temperature: " + temp);
            out.println("</body>");
            out.println("</html>");
        }
    }//end if

    if (request.getParameter("sevenDayForcastButton") != null) {
        try (PrintWriter out = response.getWriter()) {
            ArrayOfWeatherDescription weatherDescriptionData = getWeatherInformation();
            List<WeatherDescription> listWD = weatherDescriptionData.getWeatherDescription();
            String zipCode = request.getParameter("zip");
            ForecastReturn currentForecast = getCityForecastByZIP(zipCode);
            ArrayOfForecast weeklyArrayOfForecast = currentForecast.getForecastResult();
            List<Forecast> weeklyForecastList = weeklyArrayOfForecast.getForecast();
            String cityCF = currentForecast.getCity();
            String stateCF = currentForecast.getState();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Seven Day Weather Forcast</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1 align=\"center\">Seven Day Forecast For " + cityCF + ", " + stateCF + "</h1>");
            out.println("<table><tr>");
            int count = 0;
            for (Forecast f : weeklyForecastList) {
                Short dailyWeatherID = f.getWeatherID();
                WeatherDescription dailyWeatherDescription = listWD.get(dailyWeatherID - 1);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                GregorianCalendar gc = f.getDate().toGregorianCalendar();
                String forecastDate = sdf.format(gc.getTime());
                out.println("");

                if (count < 8) {
                    out.println("<td align=\"center\">" + forecastDate + "<figure><img src=\""
                            + dailyWeatherDescription.getPictureURL() + "\"><figcaption>"
                            + dailyWeatherDescription.getDescription()
                            + "</figcaption></figure>" + "\n " 
                            + "<br>  Chance of Rain <br> Daytime: " + f.getProbabilityOfPrecipiation().getDaytime() + "% "
                            + "  <br> Nighttime: " + f.getProbabilityOfPrecipiation().getNighttime()+ "%</td>");

                    count++;
                } else {
                    out.println("</tr><tr></tr><tr><td align=\"center\">" + forecastDate + "<figure><img src=\""
                            + dailyWeatherDescription.getPictureURL() + "\"><figcaption>"
                            + dailyWeatherDescription.getDescription()
                            + "</figcaption></figure>" + "\n " 
                            + "<br>  Chance of Rain <br> Daytime: " + f.getProbabilityOfPrecipiation().getDaytime() + "% "
                            + "  <br> Nighttime: " 
                            + f.getProbabilityOfPrecipiation().getNighttime()+ "%</td>");
                    out.println("");
                    count = 0;
                }
            }

            out.println("</table>");

            out.println("</body>");
            out.println("</html>");
        }
    }//end if 

    if (request.getParameter("weatherConditionsTableButton") != null) {
    try (PrintWriter out = response.getWriter()) {
        ArrayOfWeatherDescription weatherDescriptionData = getWeatherInformation();
        List<WeatherDescription> listWD = weatherDescriptionData.getWeatherDescription();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Table Of Weather Conditions</title>");
        out.println("</head>");
        out.println("<body>");

        out.println("<h1>Table Of Weather Conditions </h1>");
        out.println("<table><tr>");
        int count = 0;
        for (WeatherDescription wd : listWD) {

            if (count < 5) {
                out.println("<td><figure><img src=\""
                        + wd.getPictureURL() + "\"  \"<figcaption>"
                        + wd.getWeatherID() + ": " + wd.getDescription()
                        + "</figcaption></figure></td>");
                count++;
            } else {
                out.println("</tr><tr>");
                count = 0;
            }
        }
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");

    }
    }//end if
}

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
/**
 * Handles the HTTP <code>GET</code> method.
 *
 * @param request servlet request
 * @param response servlet response
 * @throws ServletException if a servlet-specific error occurs
 * @throws IOException if an I/O error occurs
 */
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    processRequest(request, response);
}

/**
 * Handles the HTTP <code>POST</code> method.
 *
 * @param request servlet request
 * @param response servlet response
 * @throws ServletException if a servlet-specific error occurs
 * @throws IOException if an I/O error occurs
 */
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    processRequest(request, response);
}

/**
 * Returns a short description of the servlet.
 *
 * @return a String containing servlet description
 */
@Override
public String getServletInfo() {
    return "Short description";
}// </editor-fold>

private ForecastReturn getCityForecastByZIP(java.lang.String zip) {
    // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
    // If the calling of port operations may lead to race condition some synchronization is required.
    com.cdyne.ws.weatherws.WeatherSoap port = service.getWeatherSoap();
    return port.getCityForecastByZIP(zip);
}

private WeatherReturn getCityWeatherByZIP(java.lang.String zip) {
    // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
    // If the calling of port operations may lead to race condition some synchronization is required.
    com.cdyne.ws.weatherws.WeatherSoap port = service.getWeatherSoap();
    return port.getCityWeatherByZIP(zip);
}

private ArrayOfWeatherDescription getWeatherInformation() {
    // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
    // If the calling of port operations may lead to race condition some synchronization is required.
    com.cdyne.ws.weatherws.WeatherSoap port = service.getWeatherSoap();
    return port.getWeatherInformation();
}

}
