import org.apache.zookeeper.server.quorum.QuorumPeerMain;

/**
 * Created by zhoujing on 2015/8/16.
 */
public class ZkServer01 {
	public static void main(String[] args) {
		System.setProperty("zookeeper.log.out","zookeeper01.log");
		args = new String[]{"G:\\Dev\\WorkSpace\\idea\\zookeeper\\resources\\zoo01.cfg"};
		QuorumPeerMain quorumPeerMain = new QuorumPeerMain();
		quorumPeerMain.main(args);
	}
}
