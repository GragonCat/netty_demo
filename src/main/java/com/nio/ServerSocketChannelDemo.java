package com.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class ServerSocketChannelDemo {

    public static void main(String[] args) throws  Exception{

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);

        serverSocketChannel.socket().bind(inetSocketAddress);
        SocketChannel socketChannel = serverSocketChannel.accept();


        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        int maxLen = 8;
        int read = 0;
        while (read < maxLen){
            long write = socketChannel.write(byteBuffers);
            read += write;
            Arrays.asList(byteBuffers).stream().map(e->"position:"+e.position()+" limit:"+e.limit()).forEach(System.out::println);
        }


    }


}
