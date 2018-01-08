package TimeServer.NIOServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {  
    private Selector selector;  
    private ServerSocketChannel servChannel;  
    private volatile boolean stop;  
  
    /** 
     * 初始化多路复用器、绑定监听窗口 
     * @param port 
     */  
    public MultiplexerTimeServer(int port) {  
        try {  
            //多路复用器，可以同时轮询多个Channel  
            selector = Selector.open();  
            //打开ServerSocketChannel,用于监听客户端的连接  
            servChannel = ServerSocketChannel.open();  
            //绑定监听的端口，设置连接为非阻塞模式  
            servChannel.configureBlocking(false);  
            servChannel.socket().bind(new InetSocketAddress(port), 1024);  
            //将ServerSocketChannel注册到多路复用器上，监听ACCEPT事件  
            servChannel.register(selector, SelectionKey.OP_ACCEPT);  
            System.out.println("The time server is start in port :" + port);  
        } catch (IOException e) {  
            e.printStackTrace();  
            System.exit(1);  
        }
    }  
  
    public void stop() {  
        this.stop = true;  
    }  
  
    public void run() {  
        while (!stop) {  
            try {  
                //多路复用器在线程run方法的无限循环体内轮询准备就绪的Key  
                selector.select(1000);  
                Set<SelectionKey> selectionKey = selector.selectedKeys();  
                Iterator<SelectionKey> it = selectionKey.iterator();  
                SelectionKey key = null;  
                while (it.hasNext()) {  
                    key = it.next();  
                    it.remove();  
                    try {  
                        handleInput(key);  
                    } catch (Exception e) {  
                        key.channel();  
                        if (key.channel() !=null )  
                            key.channel().close();  
                    }  
                }  
            } catch (Throwable t) {  
                t.printStackTrace();  
            }  
        }  
        /** 
         * 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册 并关闭，所以不需要重复释放资源 
         */  
        if (selector != null) {  
            try {  
                selector.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    private void handleInput(SelectionKey key) throws IOException {  
        if(key.isValid()){  
            //处理新接入的请求消息  
            if(key.isAcceptable()){  
                //接收新的连接  
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();  
                SocketChannel sc = ssc.accept();  
                sc.configureBlocking(false);  
                //添加新的连接到多路复用器  
                sc.register(selector, SelectionKey.OP_READ);  
            }  
            if(key.isReadable()){  
                //读取数据  
                SocketChannel sc = (SocketChannel) key.channel();  
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);  
                int readBytes = sc.read(readBuffer);  
                if(readBytes>0){  
                    readBuffer.flip();  
                    byte[] bytes = new byte[readBuffer.remaining()];  
                    readBuffer.get(bytes);  
                    String body = new String(bytes, "utf-8");  
                    System.out.println("The time server receive order :"+body);  
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?  
                            new java.util.Date(System.currentTimeMillis()).toString()  
                            :"BAD ORDER";  
                    doWrite(sc,new Date(System.currentTimeMillis()).toLocaleString());  
                }else if(readBytes<0){  
                    //对端关闭连接  
                    key.channel();  
                    sc.close();  
                }else {  
                    ;//读到0字节，忽略  
                }  
            }  
        }  
    }  
  
    private void doWrite(SocketChannel channel, String response) throws IOException {  
        if(response!=null&&response.trim().length()>0){  
            byte[] bytes = response.getBytes();  
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);  
            writeBuffer.put(bytes);  
            writeBuffer.flip();  
            channel.write(writeBuffer);  
        }  
    }  
}  