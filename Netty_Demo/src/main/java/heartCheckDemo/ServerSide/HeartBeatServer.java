package heartCheckDemo.ServerSide;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class HeartBeatServer {  
    public void start(int port){  
        EventLoopGroup bossGroup = new NioEventLoopGroup();  
        EventLoopGroup workerGroup = new NioEventLoopGroup();  
        try{  
            ServerBootstrap b = new ServerBootstrap();  
            b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)  
                    .childHandler(new ChannelInitializer<SocketChannel>() {  
                        @Override  
                        protected void initChannel(SocketChannel ch) throws Exception {  
                            ch.pipeline().addLast(new IdleStateHandler(5,0,0, TimeUnit.SECONDS));  
                            ch.pipeline().addLast("decoder",new StringDecoder());  
                            ch.pipeline().addLast("encoder",new StringEncoder());  
                            ch.pipeline().addLast(new HeartBeatServerHandler());  
                        }  
                    });  
            //绑定端口，开始接收进来的连接  
            ChannelFuture future = b.bind(port).sync();  
            System.out.println("Server start at port:"+port);  
            future.channel().closeFuture().sync();  
        } catch (Exception e) {  
            e.printStackTrace();  
            bossGroup.shutdownGracefully();  
            workerGroup.shutdownGracefully();  
        }  
    }  
  
    public static void main(String[] args) {  
        int port;  
        if(args.length>0){  
            port = Integer.parseInt(args[0]);  
        }else {  
            port = 6666;  
        }  
        new HeartBeatServer().start(port);  
    }  
}  
