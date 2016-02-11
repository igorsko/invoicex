package it.tnx.proto.mysql;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mceccarelli
 */
public class InvoicexMysqlSocketFactory extends com.mysql.jdbc.StandardSocketFactory {
    Socket mysocket_after = null;
    Socket mysqlsocket_after = null;

    Socket mysocket_before = null;
    Socket mysqlsocket_before = null;

    Socket mysocket_connect = null;
    Socket mysqlsocket_connect = null;

    static NetMonitor netMonitor = null;

    public InvoicexMysqlSocketFactory() {
        if (netMonitor == null) {
            netMonitor = new NetMonitor();
        }
    }

    @Override
    public Socket afterHandshake() throws SocketException, IOException {
        mysqlsocket_after = super.afterHandshake();
        mysocket_after = new MySocket(mysqlsocket_after, netMonitor);
        return mysocket_after;
    }

    @Override
    public Socket beforeHandshake() throws SocketException, IOException {
        mysqlsocket_before = super.beforeHandshake();
        mysocket_before = new MySocket(mysqlsocket_before, netMonitor);
        return mysocket_before;
    }

    @Override
    public Socket connect(String hostname, int portNumber, Properties props) throws SocketException, IOException {
        mysqlsocket_connect = super.connect(hostname, portNumber, props);
        mysocket_connect = new MySocket(mysqlsocket_connect, netMonitor);
        return mysocket_connect;
    }
    
}

