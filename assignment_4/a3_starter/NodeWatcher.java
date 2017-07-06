import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

public class NodeWatcher implements CuratorWatcher {
    private CuratorFramework _curClient;
    private ROLE _role = ROLE.UNDEFINED;

    enum ROLE {
        PRIMARY,
        BACKUP,
        UNDEFINED
    }

    public NodeWatcher(CuratorFramework curClient) {
        _curClient = curClient;
    }


    /**
     * based on the number of children currently registered in Zookeeper
     * we can change the role of the current node to be either BACKUP
     * or PRIMARY.
     *
     * If there is only one child then that means the current node is PRIMARY
     * If there is 2 children then that means someone else is PRIMARY therefore
     * the current node is BACKUP
     *
     * @param size - number of child znode names under our $USER znode
     */
    public void classifyNode(int size) {
        if (_role.equals(ROLE.PRIMARY)) {
            return;
        }

        if (size == 1) {
            _role = ROLE.PRIMARY;
        } else if (size == 2) {
            _role = ROLE.BACKUP;
        } else {
            String msg = String.format("There are %d childNodes which makes 0 sense", size);
            throw new RuntimeException(msg);
        }

        System.out.println(_role);
    }


    /**
     * Callback function on the watcher
     *
     * @param event
     */
    synchronized public void process(WatchedEvent event) {
        System.out.println("ZooKeeper event " + event);

        try {
            List<String> children = _curClient.getChildren().usingWatcher(this).forPath("/gla");
            System.out.println("num children: " + children.size());
            classifyNode(children.size());
        } catch (Exception e) {
            System.out.println("Unable to determine primary " + e);
        }

    }

}