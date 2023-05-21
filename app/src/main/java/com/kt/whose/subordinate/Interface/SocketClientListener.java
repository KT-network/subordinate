package com.kt.whose.subordinate.Interface;

public class SocketClientListener {

    public interface TcpClientStateListener {
        void onHandler();
    }

    public interface TcpClientDataReceiveListener {
        void onDataReceive(byte[] var1);
    }

}
