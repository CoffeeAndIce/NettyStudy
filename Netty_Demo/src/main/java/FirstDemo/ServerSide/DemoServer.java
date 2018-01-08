package FirstDemo.ServerSide;
import java.util.Iterator;
import java.util.Map;

import FirstDemo.OutHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 服务器启动逻辑
 */
public class DemoServer {
	public static void main(String[] args) throws Exception {
        int port = 8000;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                //采用默认值
            }
        }
        Runnable sendTask = new Runnable() {
            public void run() {
                sendTaskLoop:
                    for(;;){
                        System.out.println("task is beginning...");
                        try{
                            Map<String, SocketChannel> map = GateWayService.getChannels();
                            Iterator<String> it = map.keySet().iterator();
                            while (it.hasNext()) {
                                String key = it.next();
                                SocketChannel obj = map.get(key);
                                System.out.println("channel id is: " + key);
                                System.out.println("channel: " + obj.isActive());
                                obj.writeAndFlush("hello, it is Server test header ping");
                                if(!obj.isActive()){
                                map.remove(key);
                                }
                            }
                        }catch(Exception e){break sendTaskLoop;}
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
            }
        };
        new Thread(sendTask).start();
        new DemoServer().bind(port);
    }

    public void bind(int port) throws Exception {
        //配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new OutHandler())
                    .childHandler(new ServerChannelInitializer());

            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            Channel channel = f.channel();
            
            //等待服务器监听端口关闭
            f.channel().closeFuture().sync();
            
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
