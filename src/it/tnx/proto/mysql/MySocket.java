/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tnx.proto.mysql;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImplFactory;
import java.nio.channels.SocketChannel;

/**
 *
 * @author mceccarelli
 */
public class MySocket extends Socket {

    Socket mysqlsocket;
    NetMonitor netMonitor;

    MySocket(Socket sock, NetMonitor nm) {
        mysqlsocket = sock;
        netMonitor = nm;
    }

    public String toString() {
        return mysqlsocket.toString();
    }

    public void shutdownOutput() throws IOException {
        mysqlsocket.shutdownOutput();
    }

    public void shutdownInput() throws IOException {
        mysqlsocket.shutdownInput();
    }

    public void setTrafficClass(int tc) throws SocketException {
        mysqlsocket.setTrafficClass(tc);
    }

    public void setTcpNoDelay(boolean on) throws SocketException {
        mysqlsocket.setTcpNoDelay(on);
    }

    public static synchronized void setSocketImplFactory(SocketImplFactory fac) throws IOException {
//        mysqlsocket.setSocketImplFactory(fac);
//        setSocketImplFactory(fac);
        System.err.println("setSocketImplFactory:" + fac);
    }

    public synchronized void setSoTimeout(int timeout) throws SocketException {
        mysqlsocket.setSoTimeout(timeout);
    }

    public void setSoLinger(boolean on, int linger) throws SocketException {
        mysqlsocket.setSoLinger(on, linger);
    }

    public synchronized void setSendBufferSize(int size) throws SocketException {
        mysqlsocket.setSendBufferSize(size);
    }

    public void setReuseAddress(boolean on) throws SocketException {
        mysqlsocket.setReuseAddress(on);
    }

    public synchronized void setReceiveBufferSize(int size) throws SocketException {
        mysqlsocket.setReceiveBufferSize(size);
    }

    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        mysqlsocket.setPerformancePreferences(connectionTime, latency, bandwidth);
    }

    public void setOOBInline(boolean on) throws SocketException {
        mysqlsocket.setOOBInline(on);
    }

    public void setKeepAlive(boolean on) throws SocketException {
        mysqlsocket.setKeepAlive(on);
    }

    public void sendUrgentData(int data) throws IOException {
        mysqlsocket.sendUrgentData(data);
    }

    public boolean isOutputShutdown() {
        return mysqlsocket.isOutputShutdown();
    }

    public boolean isInputShutdown() {
        return mysqlsocket.isInputShutdown();
    }

    public boolean isConnected() {
        return mysqlsocket.isConnected();
    }

    public boolean isClosed() {
        return mysqlsocket.isClosed();
    }

    public boolean isBound() {
        return mysqlsocket.isBound();
    }

    public int getTrafficClass() throws SocketException {
        return mysqlsocket.getTrafficClass();
    }

    public boolean getTcpNoDelay() throws SocketException {
        return mysqlsocket.getTcpNoDelay();
    }

    public synchronized int getSoTimeout() throws SocketException {
        return mysqlsocket.getSoTimeout();
    }

    public int getSoLinger() throws SocketException {
        return mysqlsocket.getSoLinger();
    }

    public synchronized int getSendBufferSize() throws SocketException {
        return mysqlsocket.getSendBufferSize();
    }

    public boolean getReuseAddress() throws SocketException {
        return mysqlsocket.getReuseAddress();
    }

    public SocketAddress getRemoteSocketAddress() {
        return mysqlsocket.getRemoteSocketAddress();
    }

    public synchronized int getReceiveBufferSize() throws SocketException {
        return mysqlsocket.getReceiveBufferSize();
    }

    public int getPort() {
        return mysqlsocket.getPort();
    }

    public OutputStream getOutputStream() throws IOException {
        OutputStream outs = mysqlsocket.getOutputStream();
        MyOutputStream myouts = new MyOutputStream(outs, netMonitor);
        return myouts;
    }

    public boolean getOOBInline() throws SocketException {
        return mysqlsocket.getOOBInline();
    }

    public SocketAddress getLocalSocketAddress() {
        return mysqlsocket.getLocalSocketAddress();
    }

    public int getLocalPort() {
        return mysqlsocket.getLocalPort();
    }

    public InetAddress getLocalAddress() {
        return mysqlsocket.getLocalAddress();
    }

    public boolean getKeepAlive() throws SocketException {
        return mysqlsocket.getKeepAlive();
    }

    public InputStream getInputStream() throws IOException {
        InputStream ins = mysqlsocket.getInputStream();
        MyInputStream myins = new MyInputStream(ins, netMonitor);
        return myins;
    }

    public InetAddress getInetAddress() {
        return mysqlsocket.getInetAddress();
    }

    public SocketChannel getChannel() {
        return mysqlsocket.getChannel();
    }

    public void connect(SocketAddress endpoint, int timeout) throws IOException {
        mysqlsocket.connect(endpoint, timeout);
    }

    public void connect(SocketAddress endpoint) throws IOException {
        mysqlsocket.connect(endpoint);
    }

    public synchronized void close() throws IOException {
        mysqlsocket.close();
    }

    public void bind(SocketAddress bindpoint) throws IOException {
        mysqlsocket.bind(bindpoint);
    }
}