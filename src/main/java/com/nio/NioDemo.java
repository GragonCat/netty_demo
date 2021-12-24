package com.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class NioDemo {

    public static void main(String[] args) throws  Exception{

       ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
       InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);
       serverSocketChannel.socket().bind(inetSocketAddress);
       serverSocketChannel.configureBlocking(false);
       System.out.println(serverSocketChannel);

       Selector selector = Selector.open();
       SelectionKey sscKey = serverSocketChannel.register(selector,0,null);
       sscKey.interestOps(SelectionKey.OP_ACCEPT);
        Worker worker1 = new Worker("WORKER-01");
        Worker worker2 = new Worker("WORKER-02");
        boolean flag = true;
        while(true){
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()){

                SelectionKey key = iterator.next();
                iterator.remove();

                if(key.isAcceptable()){
                    ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel)key.channel();
                    SocketChannel socketChannel = serverSocketChannel1.accept();
                    socketChannel.configureBlocking(false);
                    if(flag){
                        worker1.init(socketChannel);
                        flag = !flag;
                    }else{
                        worker2.init(socketChannel);
                        flag = !flag;
                    }
                }
            }
        }


    }

    static class Worker implements Runnable{

        private Thread thread;
        private String name;
        private Selector selector;
        private volatile boolean start;
        private ConcurrentLinkedDeque<Runnable> deque;

        public Worker(String name) throws IOException {
            thread = new Thread(this);
            this.name = name;
            this.selector = Selector.open();
            deque = new ConcurrentLinkedDeque<>();
            thread.start();
        }
        public void init(SocketChannel socketChannel){
            deque.add(()->{
                try {
                    socketChannel.register(selector,SelectionKey.OP_READ,null);
                    System.out.println(this.name+ " 与 客户端建立连接......"+socketChannel);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            selector.wakeup();
        }
        @Override
        public void run() {
            while(true){
                try {
                    selector.select();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(!deque.isEmpty()){
                    deque.poll().run();
                }

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {

                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if(key.isReadable()){
                        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                        SocketChannel socketChannel = (SocketChannel)key.channel();

                        int read = 0;
                        try {
                            read = socketChannel.read(byteBuffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(this.name+ " Read:"+read+", position:"+byteBuffer.position()+", limit:"+byteBuffer.limit());
                        if(read == -1){
                            key.cancel();
                            System.out.println("客户端断开了连接.....");
                        }else{
                            System.out.println(socketChannel +" 发送: "+new String(byteBuffer.array()));
                        }

                    }
                }
            }
        }
    }


}
