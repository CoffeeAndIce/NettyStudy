package Nio_one;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class CharsetDemo {
	public static void main(String[] args) throws Exception {
//		readFile();//读取
//		Fastcopy();//复制
		ServerSocketChannel serverSocketChannel  = ServerSocketChannel.open();
		//设置为非阻塞
		serverSocketChannel.configureBlocking(false);
		//绑定IP和接口
		serverSocketChannel.socket().bind(new InetSocketAddress("192.168.1.212", 8080));
		Selector selector = Selector.open();
		// 注册感兴趣事件到selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //判断是否存在通道
        while(selector.select()!=0){
        	Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        	while (iterator.hasNext()) {
        		//循环选中事件
				SelectionKey selecttionKey = iterator.next();
				//删除已经处理的
				iterator.remove();
                if (selecttionKey.isAcceptable()) {
                    // 返回注册该事件时的channel ，即SelectableChannel
                    ServerSocketChannel channel = (ServerSocketChannel) selecttionKey.channel();
                    // 有连接事件来了， 可以处理接收请求了,注意如果不进行accept，select.select()一直能轮询到东西
                    // 接收后返回了个socketchannel，开始配置
                    SocketChannel socketChannel = channel.accept();
                    // 也配置成非阻塞处理
                    socketChannel.configureBlocking(false);
                    // 复用同一个selector上注册感兴趣的事件,并注册感兴趣的可读事件
                    socketChannel.register(selector, selecttionKey.OP_READ);
                }
             // 如果来可以可读事件
                if (selecttionKey.isReadable()) {
                    // 返回注册该事件时的channel ，即实现了SelectableChannel的
                    SocketChannel socketChannel = (SocketChannel) selecttionKey.channel();
                    // 后面就都是通过byteBuffer和channel来读操作了
                    ByteBuffer byteBf = ByteBuffer.allocate(1024);
                    socketChannel.read(byteBf);
                    Charset charset = Charset.forName("utf-8");
                    byteBf.flip();
                    System.out.println("clinet :" + charset.decode(byteBf));
                    // socket是双通道，故也可以直接返回东西了
                    socketChannel.write(charset.encode("test only"));
                    socketChannel.close();
                }
			}
        }
	}

	/**
	* @Title: Fastcopy
	* @Description: 快速复制
	* @throws FileNotFoundException
	* @throws IOException    设定文件 
	* @return void    返回类型 
	* @throws
	*/
	private static void Fastcopy() throws FileNotFoundException, IOException {
		//打开输入流
		FileInputStream fis = new FileInputStream("E:/家园游戏.txt");
		//打开输出流
		FileOutputStream fos = new FileOutputStream("C:/Users/Administrator/Desktop/直播平台数据/game.txt");
		try{
		//获取通道
		FileChannel readchannel = fis.getChannel();
		FileChannel writechannel = fos.getChannel();
		readchannel.transferTo(0, readchannel.size(), writechannel);
		}catch(IOException e){
			e.printStackTrace();
		}finally {
			fis.close();
			fos.close();
		}
	}

	/**
	* @Title: readFile
	* @Description: 读取文件
	* @throws FileNotFoundException
	* @throws IOException    设定文件 
	* @return void    返回类型 
	* @throws
	*/
	private static void readFile() throws FileNotFoundException, IOException {
		//打开输入流
		FileInputStream fis = new FileInputStream("E:/家园游戏.txt");
		try{
		//获取通道
		FileChannel channel = fis.getChannel();
		//设置容量
		ByteBuffer allocate = ByteBuffer.allocate(1024);
		channel.read(allocate);
		//window系统的默认编码
		Charset charset = Charset.forName("gbk");
		//修改为读模式
		allocate.flip();
		System.out.println(charset.decode(allocate));
		}catch(IOException e){
			e.printStackTrace();
		}finally {
			fis.close();
		}
	}
}
