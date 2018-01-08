package heartCheckDemo.ServerSide;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {  
    private int loss_connect_time = 0;  
  
    @Override  
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {  
        if (evt instanceof IdleStateEvent){  
            IdleStateEvent event = (IdleStateEvent) evt;  
            if(event.state()== IdleState.READER_IDLE){  
                loss_connect_time++;  
                System.out.println("5秒没有接收到客户端"+ctx.channel().remoteAddress()+"的信息了...");  
                if(loss_connect_time>2){  
                    System.out.println("关闭这个不活跃的channel"+ctx.channel().remoteAddress());  
                    ctx.channel().close();  
                }  
            }  
        }else {  
            super.userEventTriggered(ctx,evt);  
        }  
    }  
  
    @Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {  
        System.out.println("Server ChannelRead......");  
        System.out.println(ctx.channel().remoteAddress()+"-->>server:"+msg.toString());  
    }  
  
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
        cause.printStackTrace();  
        ctx.close();  
    }  
}  
