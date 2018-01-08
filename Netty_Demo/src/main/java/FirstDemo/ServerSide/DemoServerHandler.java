package FirstDemo.ServerSide;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

/**
 * 服务器业务逻辑
 */
public class DemoServerHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    	System.out.println("Client :"+ctx.channel().remoteAddress() +" say : " + msg.toString());
        String message = "";
        message =  msg.toString();
        //返回客户端消息 - 我已经接收到了你的消息
        if(message.equals("fuck")){
        	message ="请文明用语";
        }
        ctx.writeAndFlush("Received your message : " + message);
//        channelFuture.addListener(ChannelFutureListener.CLOSE);
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("RemoteAddress : " + ctx.channel().remoteAddress() + " active !");
        ctx.writeAndFlush("连接成功！");
        String uuid = ctx.channel().id().toString();
        System.out.println(uuid);
        GateWayService.addGatewayChannel(uuid, (SocketChannel)ctx.channel());
        System.out.println("a new connect come in: " + uuid);
    }
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
      System.out.println("exception is general");  
  }
    
}
