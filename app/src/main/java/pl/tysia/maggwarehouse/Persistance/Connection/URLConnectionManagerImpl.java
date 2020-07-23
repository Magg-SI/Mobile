package pl.tysia.maggwarehouse.Persistance.Connection;


import android.content.Context;

import pl.tysia.maggwarehouse.Persistance.NetAddressManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class URLConnectionManagerImpl implements URLConnectionManager {
    private HttpURLConnection connection;

    @Override
    public void setConnection(URL url) throws IOException {
        connection = (HttpURLConnection) url.openConnection();

    }


    @Override
    public HttpURLConnection getConnection() {
        return connection;
    }

    @Override
    public void closeConnection() {
        connection.disconnect();
    }


    @Override
    public void setMethod(String method) throws ProtocolException {
        connection.setRequestMethod(method);
    }

    @Override
    public String get() throws IOException {
        connection.setRequestMethod("GET");

        String response = readStream(connection.getInputStream());

        return response;
    }


    @Override
    public String post(String obj) throws IOException {
        connection.setRequestMethod("POST");

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(obj);
        wr.flush();
        wr.close();

        String response = readStream(connection.getInputStream());

        return response;
    }


    private String readStream(InputStream inputStream) throws IOException {
        StringBuilder data = new StringBuilder();

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        int inputStreamData = inputStreamReader.read();
        while (inputStreamData != -1) {
            char current = (char) inputStreamData;
            inputStreamData = inputStreamReader.read();
            data.append(current);
        }

        return data.toString();
    }

    @Override
    public URL getConnectionURL(Context context) {
        return NetAddressManager.Companion.getConnectionURL(context);
    }

}
