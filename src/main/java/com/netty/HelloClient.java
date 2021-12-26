package com.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class HelloClient {

    public static void main(String[] args) throws InterruptedException {
        //客户端启动器
        Channel channel = new Bootstrap()
                // 添加循环事件组 监听事件
                .group(new NioEventLoopGroup())
                //绑定socketChannel类型
                .channel(NioSocketChannel.class)
                //绑定channel初始化器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    //channel建立时调用
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //添加编码器
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                //建立连接
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()//阻塞线程，直到连接建立
                .channel();
                //.writeAndFlush("hello...");
        System.out.println();

    }
}
