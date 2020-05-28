package pl.tysia.maggwarehouse.Persistance.Connection;

import android.content.Context;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.URL;

public interface URLConnectionManager extends ConnectionManager<URL> {
    void setMethod(String method) throws ProtocolException;

    String get() throws IOException;
    String post(String obj) throws IOException;

    URL getConnectionURL(Context context);
}
