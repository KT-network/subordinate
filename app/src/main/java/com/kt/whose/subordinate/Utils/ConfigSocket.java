package com.kt.whose.subordinate.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConfigSocket {

    private String host = "192.168.4.1";
    private int port = 1006;
    private Socket mClient;


    public ConfigSocket(){
    }


    public void connect() throws IOException {

        mClient = new Socket(host,port);

    }


}
