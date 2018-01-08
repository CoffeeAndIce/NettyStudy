package FirstDemo.ServerSide;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.socket.SocketChannel;

/**
* @ClassName: GateWayService
* @Description: 存放通道的concurrent集合
* @author linge
* @date 2018年1月8日 下午2:09:40
*
*/
public class GateWayService {
	//存放通道链接的集合
	private static Map<String,SocketChannel> map = new ConcurrentHashMap<String, SocketChannel>();
    public static void addGatewayChannel(String id, SocketChannel gateway_channel){
        map.put(id, gateway_channel);
    }
    
    public static Map<String, SocketChannel> getChannels(){
        return map;
    }

    public static SocketChannel getGatewayChannel(String id){
        return map.get(id);
    }
    
    public static void removeGatewayChannel(String id){
        map.remove(id);
    }
}
