package zhuhp.demo;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.spi.SelectorProvider;

public class NioClient {

    public static void main(String[] args) {
        String cn = System.getProperty("java.nio.channels.spi.SelectorProvider");
        System.out.println(cn);
//        System.setProperty("java.nio.channels.spi.SelectorProvider","sun.nio.ch.PollSelectorProvider");


        SelectorProvider provider = SelectorProvider.provider();
        System.out.println(provider.getClass());

        System.out.println(new InetSocketAddress(9999));

        System.out.println( (SelectionKey.OP_READ | SelectionKey.OP_WRITE));
    }
}
