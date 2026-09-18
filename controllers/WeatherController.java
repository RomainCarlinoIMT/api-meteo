package controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import interfaces.WeatherFacade;

public class WeatherController implements HttpHandler
{
    private final WeatherFacade mWeatherFacade;

    public WeatherController(WeatherFacade weatherFacade)
    {
        this.mWeatherFacade = weatherFacade;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException 
    {
        if(!"GET".equalsIgnoreCase(exchange.getRequestMethod()))
        {
            sendResponse(exchange, 405, "Method Not Allowed");
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQueryParams(query);
        String address = params.get("address");

        if (address == null || address.isBlank())
        {
            sendResponse(exchange, 400, "Missing 'address' query parameter");
            return;
        }

        try 
        {
            String result = mWeatherFacade.getWeatherData(address);

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            sendResponse(exchange, 200, result);
        }
        catch (Exception e) 
        {
            String errorMsg = "{\"error\": \"" + e.getMessage() + "\"}";
            sendResponse(exchange, 500, errorMsg);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException
    {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody())
        {
            os.write(bytes);
        }
    }

    private Map<String, String> parseQueryParams(String query) 
    {
        Map<String, String> result = new HashMap<>();
        if (query == null) 
        {
            return result;
        }

        for (String param : query.split("&"))
        {
            String[] entry = param.split("=");
            if (entry.length > 1) 
            {
                result.put(entry[0], java.net.URLDecoder.decode(entry[1], StandardCharsets.UTF_8));
            } 
            else 
            {
                result.put(entry[0], "");
            }
        }
        return result;
    }
}
