package zhuhp.demo;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 负责具体的通讯操作.
 */
public class NioConn {
    private SocketChannel socketChannel ;
    private SelectionKey selectionKey ;
    private boolean initialized = false; //判断是否是第一次握手，一般的socket编程模型中用到的通用方式即是如此

    public NioConn(SocketChannel socketChannel, SelectionKey selectionKey) {
        this.socketChannel = socketChannel;
        this.selectionKey = selectionKey;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }
}
