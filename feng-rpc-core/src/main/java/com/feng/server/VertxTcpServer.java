package com.feng.server;

import com.feng.server.tcp.TcpServerHandler;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

public class VertxTcpServer implements HttpServer{
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        NetServer netServer = vertx.createNetServer();
        netServer.connectHandler(new TcpServerHandler());
        netServer.listen(port,result->{
            if (result.succeeded()){
                System.out.println("TCP server started on port" + port);
            }else{
                System.out.println("Failed to start TCP server" + result.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);

    }
}
