package zhuhp.demo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class NioConFactory implements Runnable{

    public NioConFactory() throws IOException{

    }

    private ServerSocketChannel ss ;

    final Selector selector = Selector.open();
    //允许的最大客户端链接数
    private int maxConn = 10 ;

    final ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024*8);

    //构建一个缓存，用来存储所有链接过来的对象 , 需要注意的是，这不是一个thread safe 的集合
    Map<InetAddress,Set<NioConn>> ipMap =  new HashMap<>();

    public void config(InetSocketAddress inetSocketAddress,int maxConnAccept) throws IOException {
        ss = ServerSocketChannel.open();
        ss.socket().setReuseAddress(true);
        ss.socket().bind(inetSocketAddress);
        ss.configureBlocking(false);
        ss.register(selector, SelectionKey.OP_ACCEPT);
        this.maxConn = maxConnAccept ;
    }

    /**
     * 获取来自某个节点的所有连接数.
     * @param inetAddress
     * @return
     */
    public int getClientConnCount(InetAddress inetAddress){
        synchronized (ipMap) {
            Set<NioConn> clientConns = ipMap.get(inetAddress);
            return null == clientConns ? 0 : clientConns.size();
        }
    }

    public void addConn(NioConn conn){
        InetAddress inetAddress = conn.getSocketChannel().socket().getInetAddress();
        synchronized (ipMap) {
            Set<NioConn> s = ipMap.get(inetAddress);
            if(null == s ){
                s = new HashSet<>();
                s.add(conn);
                ipMap.put(inetAddress,s);
            }else{
                s.add(conn);
            }
        }


//        synchronized (this){
//            if(null == ipMap.get(inetAddress)){
//                ipMap.put(inetAddress ,new HashSet<NioConn>());
//            }
//            ipMap.get(inetAddress).add(conn);
//        }


        /*
          :比较上下这两段代码，下面的代码看起来很'简洁',但是实际上操作的次数更多，而且锁加的很随意，直接用this作为锁对象
          :区别在于ipMap中还没有inetAddress时，被屏蔽掉的代码多了一次从map中的get操作.
          :有的时候，代码并不是越短越好
         */

    }


    @Override
    public void run() {

        while(!ss.socket().isClosed()){
            try {
                selector.select(1000);
                Set<SelectionKey>  selectionKeys ;
                synchronized (this) {
                    selectionKeys = selector.selectedKeys();
                }

                List<SelectionKey> selectionKeyList = new ArrayList<>(selectionKeys);
                Collections.shuffle(selectionKeyList);

                for(SelectionKey selectionKey : selectionKeyList){
                   if((selectionKey.readyOps() & SelectionKey.OP_ACCEPT) != 0 ){
                       SocketChannel sc = (SocketChannel) selectionKey.channel();
                       InetAddress clientAddr = sc.socket().getInetAddress();
                       if(getClientConnCount(clientAddr) > maxConn){
                           //log
                           sc.close();
                       }else {

                           sc.configureBlocking(false);

                           NioConn conn = new NioConn(sc,selectionKey);
                           selectionKey.attach(conn);
                           addConn(conn);
                       }

                   }else if ((selectionKey.readyOps() & (SelectionKey.OP_READ | SelectionKey.OP_WRITE)) != 0 )
                   {

                   }else{

                   }
                }
                //暗示垃圾回收
                selectionKeys.clear();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    public void shutdown(){
        try {
            ss.close();
        } catch (IOException e) {
            System.out.println("close serverSocketChannel, ignore these");
        }

        try {
            selector.close();
        } catch (IOException e) {
            System.out.println("close selector exception ,ignore these ");
        }

        ipMap.clear();
    }
}
