package FirstDemo.CustomerSide;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 客户端业务逻辑
 */
public class DemoClientHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if(msg.toString().equals("close") || msg.toString().equals("关闭")){
    		System.exit(0);
    	}
        System.out.println("Server say : " + msg.toString());
    }
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
      System.out.println("exception is general");  
  }
}
