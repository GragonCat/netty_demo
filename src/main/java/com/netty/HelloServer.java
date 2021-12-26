package com.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class HelloServer {

    public static void main(String[] args) {
        //1 服务端启动器
        new ServerBootstrap()
                // 添加循环事件组 EventLoopGroup（BOSS,WORKER），服务端添加Boss，监听accept、read等事件
                .group(new NioEventLoopGroup())
                //绑定服务端 NioServerSocketChannel 类型
                .channel(NioServerSocketChannel.class)
                //Boss负责建立连接，worker(child)负责处理读写
                .childHandler(
                        // 添加channel初始化器
                        new ChannelInitializer<NioSocketChannel>() {
                            // 客户端连接时 调用initChannel方法，初始化channel
                            @Override
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                //pipeLine 管道，类似过滤器，收到的数据进行处理
                                nioSocketChannel.pipeline().addLast(new StringDecoder());//解码器
                                //处理数据read事件
                                nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(msg);
                                    }
                                });
                            }
                            //绑定端口
                }).bind(8080);
    }
}
