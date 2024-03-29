package sample;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Jamie on 06/02/2016.
 */
public class SalesTask extends Task<ObservableList<SalesData>> {

    private static final String URL_STRING = "http://glynserver.cms.livjm.ac.uk/";
    private static final String SERVICE = "DashService/";
    private static final String METHOD = "SalesGetSales";

    private SalesDataParser dataParser = new SalesDataParser();

    public String getData() {
        if (isServiceOnline()) {
            try {
                URL url = new URL(URL_STRING + SERVICE + METHOD);
                HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();
                httpClient.setConnectTimeout(10000);
                httpClient.setRequestMethod("GET");
                httpClient.setRequestProperty("Accept", "application/json");
                httpClient.setRequestProperty("Content-Type", "application/json");

                InputStream is = null;
                int statusCode = httpClient.getResponseCode();

                //if response is in good range, set content, else set status
                //iterate over response using Scanner and InputStream start delimiter
                //InputStream and HttpURLConnection are garbage collected
                if (statusCode >= 200 && statusCode < 400) {
                    // Create an InputStream in order to extract the response object
                    is = httpClient.getInputStream();
                    System.out.println(String.format("SERVICE STATUS - Success - HTTP Response %d", statusCode));
                    return new Scanner(is).useDelimiter("\\A").next();
                } else {
                    System.out.println(String.format("SERVICE_STATUS - Error - HTTP Response %d", statusCode));
                    return "";
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return "";
    }

    public boolean isServiceOnline() {
        boolean online = true;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(URL_STRING);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);
            connection.connect();
            System.out.println("SERVICE_CONNECTION - Connected");
        } catch (Exception e) {
            System.out.println("SERVICE_STATUS_ERROR - " + e.getMessage());
            online = false;
        } finally {
            if (connection != null) {
                connection.disconnect();
                System.out.println("SERVICE_CONNECTION - Disconnected");
            }
        }
        return online;
    }


    @Override
    protected ObservableList<SalesData> call() throws Exception {
        String data = getData();
        System.out.println(data);
        return dataParser.parseJSONData(data);
    }
}
