package heartCheckDemo.CustomerSide;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class HeartBeatClientHandler extends SimpleChannelInboundHandler<Object> {  
	  
    private static final ByteBuf HEART_BEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("心跳检测：PING...",  
            CharsetUtil.UTF_8));  
    private static final int TRY_TIME = 3;  
    private int currentTime = 0;  
  
    @Override  
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {  
        System.out.println("停止时间是："+new Date());  
        System.out.println("HeartBeatClientHandler channelInactive");  
    }  
  
    @Override  
    public void channelActive(ChannelHandlerContext ctx) throws Exception {  
        System.out.println("激活时间是："+new Date());  
        System.out.println("HeartBeatClientHandler channelActive");  
        ctx.fireChannelActive();  
    }  
  
    @Override  
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {  
        System.out.println("循环触发时间："+new Date());  
        if(evt instanceof IdleStateEvent){  
            IdleStateEvent event = (IdleStateEvent) evt;  
            if(event.state()== IdleState.WRITER_IDLE){  
                if(currentTime<=TRY_TIME){  
                    System.out.println("currentTime:"+currentTime);  
                    currentTime++;  
                    ctx.channel().writeAndFlush(HEART_BEAT_SEQUENCE.duplicate());  
                }  
            }  
        }  
    }  
  
    @Override  
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {  
        String message = (String) msg;  
        System.out.println(message);  
        if(message.equals("HeartBeat")){  
            ctx.write("has read message from server......");  
            ctx.flush();  
        }  
        ReferenceCountUtil.release(msg);  
    }

}  
