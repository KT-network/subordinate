package com.kt.whose.subordinate.Utils;

import com.kt.whose.subordinate.Interface.SocketClientListener;

import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient {
    private Socket m_Socket;
    private byte[] m_RecvBuffer;
    private boolean m_Running;
    private String m_host;
    private int m_port;
    private Thread m_thread = null;
    private SocketClientListener.TcpClientStateListener connectedListener;
    private SocketClientListener.TcpClientStateListener disconnectedListener;
    private SocketClientListener.TcpClientDataReceiveListener dataReceiveListener;
    public String Name;

    public void setConnectedListener(SocketClientListener.TcpClientStateListener listener) {
        this.connectedListener = listener;
    }

    public void setDisconnectedListener(SocketClientListener.TcpClientStateListener listener) {
        this.disconnectedListener = listener;
    }

    public void setDataReceiveListener(SocketClientListener.TcpClientDataReceiveListener listener) {
        this.dataReceiveListener = listener;
    }

    public boolean getIsRunning() {
        return this.m_Running;
    }

    private void onConnected() {
        if (this.connectedListener != null) {
            this.connectedListener.onHandler();
        }

    }

    private void onDisconnected() {
        if (this.disconnectedListener != null) {
            this.disconnectedListener.onHandler();
        }

    }

    private void onDataReceive(byte[] buffer) {
        if (this.dataReceiveListener != null) {
            this.dataReceiveListener.onDataReceive(buffer);
        }

    }

    private void onTrace(String msg) {
        System.out.println(msg);
    }

    public SocketClient(String host, int port) {
        this.m_host = host;
        this.m_port = port;
        this.m_Running = false;
        this.m_RecvBuffer = new byte[1024];
    }

    public void close() {
        this.m_Running = false;
        if (this.m_Socket != null) {
            try {
                this.m_Socket.shutdownInput();
                this.m_Socket.shutdownOutput();
            } catch (Exception var3) {
                this.onTrace("Socket (" + this.m_host + " Port:" + this.m_port + ") Shutdown Exception.");
            }

            try {
                this.m_Socket.close();
            } catch (Exception var2) {
                this.onTrace("Socket (" + this.m_host + " Port:" + this.m_port + ") Close Exception.");
            }

            this.onDisconnected();
        }

        this.m_thread = null;
        this.m_Socket = null;
    }

    public void connect() {
        (new Thread(new Runnable() {
            public void run() {
                threadRunConnect();
            }
        })).start();
    }

    private void threadRunConnect() {
        if (!this.m_Running && this.m_Socket == null) {
            try {
                this.m_Socket = new Socket();
                this.m_Socket.connect(new InetSocketAddress(this.m_host, this.m_port));
            } catch (Exception var2) {
                this.onTrace("Socket (" + this.m_host + " Port:" + this.m_port + ") Connect Exception.");
                return;
            }

            if (this.m_Socket.isConnected()) {
                this.m_Running = true;
                this.m_thread = new Thread(new Runnable() {
                    public void run() {
                        runFunc();
                    }
                }, "Socket_Thread");
                this.m_thread.setDaemon(true);
                this.m_thread.start();
                this.onConnected();
            }
        }
    }

    private void runFunc() {
        while(this.m_Running) {
            try {
                int size = this.m_Socket.getInputStream().read(this.m_RecvBuffer, 0, this.m_RecvBuffer.length);
                if (size <= 0) {
                    this.close();
                    return;
                }

                byte[] buffer = new byte[size];
                System.arraycopy(this.m_RecvBuffer, 0, buffer, 0, size);
                this.onDataReceive(buffer);
            } catch (Exception var4) {
                var4.printStackTrace();
                this.onTrace("Socket (" + this.m_host + ":" + this.m_port + ") Read Buffer Exception.");
                this.close();
                return;
            }

            if (this.m_Running) {
                try {
                    Thread.sleep(0L);
                } catch (Exception var3) {
                    var3.printStackTrace();
                    this.onTrace("Thread Sleep Exception.");
                    this.close();
                    return;
                }
            }
        }

    }

    public void send(byte[] buffer) {
        try {
            if (this.m_Socket != null) {
                DataOutputStream dos = new DataOutputStream(this.m_Socket.getOutputStream());
                dos.write(buffer, 0, buffer.length);
                dos.flush();
            }
        } catch (Exception var3) {
            var3.printStackTrace();
            this.onTrace("Socket (" + this.m_host + ":" + this.m_port + ") Send Buffer Exception.");
            this.close();
        }

    }
}