import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * Created by zhanghongming on 2016/11/5.
 */
public class Watch {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        String hosts = "www.hadoop01.com:2181,www.hadoop02.com:2181,www.hadoop03.com:2181";
        int SESSION_TIMEOUT = 2000;
        final ZooKeeper zk = new ZooKeeper(hosts, SESSION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println("State:"+watchedEvent.getState());
            }
        });
        zk.exists("/chroot",new Watcher() {
            public void process(WatchedEvent event) {
                System.out.println(event.getState()+"|"+event.getPath()+"|"+event.getType().name());
                try {
                    zk.exists("/chroot",this);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        zk.create("/chroot",null, ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        Thread.sleep(10000);
        zk.close();
    }
}
