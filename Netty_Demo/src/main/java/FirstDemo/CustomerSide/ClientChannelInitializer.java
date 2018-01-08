package FirstDemo.CustomerSide;

import FirstDemo.OutHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 客户端Channel通道初始化设置
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //这里调用默认的编码解码
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        //客户端的逻辑
        //添加一个Hanlder用来处理各种Channel状态
        pipeline.addLast("handlerIn", new DemoClientHandler());
        //添加一个Handler用来接收监听IO操作的  
        pipeline.addLast("handlerOut", new OutHandler()); 
    }
    
}