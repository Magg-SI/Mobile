package pl.tysia.maggwarehouse.Persistance.Connection;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface ConnectionManager<T> {
    void setConnection(T obj) throws IOException;
    HttpURLConnection getConnection();
    void closeConnection();
}
