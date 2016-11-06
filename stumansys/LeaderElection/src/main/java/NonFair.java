import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhanghongming on 2016/11/5.
 */
public class NonFair {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        String hosts = "www.hadoop01.com:2181,www.hadoop02.com:2181,www.hadoop03.com:2181";
        int SESSION_TIMEOUT = 2000;
        final ZooKeeper zk = new ZooKeeper(hosts, SESSION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println("State:"+watchedEvent.getState());
            }
        });

        String stat1 =zk.create("/chroot/child",null, ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("stat1:"+stat1);
        List<String> childrenNodes = zk.getChildren("/chroot", true);
        Collections.sort(childrenNodes);
        System.out.println(childrenNodes.get(0));
        if(stat1.contains(childrenNodes.get(0))){
            System.out.println("我是leader");
        }else{
            //监听父节点
            zk.create(childrenNodes.get(0), null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
        }
        Thread.sleep(5000);
        String stat2 =zk.create("/chroot/child",null, ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("stat2:"+stat2);
        Collections.sort(childrenNodes);
        if(stat2.contains(childrenNodes.get(0))){
            System.out.println("我是leader");
        }else{
            //监听父节点
            zk.create("/chroot"+childrenNodes.get(0), null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
        }
        Thread.sleep(5000);
        String stat3 =zk.create("/chroot/child",null, ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        Collections.sort(childrenNodes);
        System.out.println("stat3:"+stat3);
        if(stat3.contains(childrenNodes.get(0))){
            System.out.println("我是leader");
        }else{
            //监听父节点
            zk.create("/chroot"+childrenNodes.get(0), null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
        }

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

        Thread.sleep(10000);
        zk.close();
    }

    //     [zk: localhost:2181(CONNECTED) 32] ls /
    //     [zookeeper, kafka, chroot0000000082]
    //
    //     [zk: localhost:2181(CONNECTED) 34] ls /
    //     [zookeeper, chroot0000000083, kafka, chroot0000000082]
    //
    //     [zk: localhost:2181(CONNECTED) 48] ls /
    //     [zookeeper, chroot0000000083, chroot0000000084, chroot0000000082]
}
