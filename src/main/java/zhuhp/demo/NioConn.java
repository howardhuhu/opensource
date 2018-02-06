package demo;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 负责具体的通讯操作.
 */
public class NioConn {
    private SocketChannel socketChannel ;
    private SelectionKey selectionKey ;

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
