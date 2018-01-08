package heartCheckDemo.CustomerSide;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class HeartBeatClient {  
    public void connect(int port,String host){  
        EventLoopGroup group = new NioEventLoopGroup();  
        try{  
            Bootstrap b = new Bootstrap();  
            b.group(group)  
                    .channel(NioSocketChannel.class)  
                    .option(ChannelOption.TCP_NODELAY,true)  
                    .handler(new LoggingHandler(LogLevel.INFO))  
                    .handler(new ChannelInitializer<SocketChannel>() {  
                        @Override  
                        protected void initChannel(SocketChannel ch) throws Exception {  
                            ChannelPipeline p = ch.pipeline();  
                            p.addLast("ping",new IdleStateHandler(0,4,0, TimeUnit.SECONDS));  
                            //编/解码类----默认utf-8
                            p.addLast("decoder",new StringDecoder());  
                            p.addLast("encoder",new StringEncoder());  
                            p.addLast(new HeartBeatClientHandler());  
                        }  
                    });  
            ChannelFuture future = b.connect(host, port).sync();  
            future.channel().closeFuture().sync();  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }finally {  
            group.shutdownGracefully();  
        }  
    }  
  
    public static void main(String[] args) {  
        int port = 6666;  
        if(args!=null&&args.length>0){  
            try{  
                port = Integer.valueOf(args[0]);  
            }catch (NumberFormatException e){  
                //采用默认值  
            }  
        }  
        new HeartBeatClient().connect(port,"192.168.88.132");  
    }  
}  