package com.pflm.mongodb;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.pflm.utils.CommonApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mongodb工厂类
 * @author qinxuewu
 * @version 1.00
 * @time 7/11/2018下午 2:09
 */
public class MongoFactory {

    private static final Logger logger = LoggerFactory.getLogger(MongoFactory.class);

    /**
     * 域名 端口
     */
    private static String mongo_host_port=CommonApiUtils.mongo_host_port;
    /**
     * 账号密码
     */
    private static String user_pass_db=CommonApiUtils.mongo_user_pass_db;
    private static Map<String,MongoDatabase> mongoDb_map=new HashMap<String, MongoDatabase>();// 数据库连接到
    private static MongoClient mongoClient = null;
    /***
     * 设置mongo参数
     * @param host_port 域名 端口
     * @param upd  用户名 密码 数据  “:”分隔
     * @return
     */
    public static boolean setMongoConfig(String host_port,String upd){
        if(mongo_host_port==null){
            mongo_host_port=host_port;user_pass_db=upd;
        }
        return false;
    }

    /**
     * 初始化连接池
     */
    private static synchronized void initDBPrompties() {
        if(mongoClient==null){
            try {
                List<MongoCredential> mongoCredential=Collections.<MongoCredential>emptyList();
                //arr 0,1,2 用户名  密码  数据名
                if(user_pass_db!=null&&!"".equals(user_pass_db)){
                    String[] arr = user_pass_db.split(":");
                    MongoCredential credential = MongoCredential.createScramSha1Credential(arr[0],arr[2],arr[1].toCharArray());
                    mongoCredential=new ArrayList<MongoCredential>();
                    mongoCredential.add(credential);
                }
                //.connectionsPerHost(200)
                MongoClientOptions option=new MongoClientOptions.Builder().threadsAllowedToBlockForConnectionMultiplier(10).build();
                String[] hostps = mongo_host_port.split(";");
                //只有一个主 副本集
                if(hostps.length==1){
                    String[] h =hostps[0].split(":");
                    mongoClient = new MongoClient(new ServerAddress(h[0],Integer.parseInt(h[1])),mongoCredential,option);
                }else{
                    List<ServerAddress> serverAddress = new ArrayList<ServerAddress>();
                    for (String hp:hostps) {
                        String[] h =hp.split(":");
                        serverAddress.add(new ServerAddress(h[0],Integer.parseInt(h[1])));
                    }
                    mongoClient = new MongoClient(serverAddress,mongoCredential,option);
                }
                logger.debug("mongoClient 偏好为="+mongoClient.getReadPreference().toString());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("********mongodb 配置错误");
            }

        }
    }

    /***
     * 获取数据库名
     * @return
     */
    public static MongoDatabase getMongoDb(){
        return getMongoDb("wechat");
    }

    public static MongoDatabase getMongoDbApi(){
        return getMongoDb("wechat_api");
    }

    public static MongoDatabase getMongoDb(String databaseName){
        if(mongoDb_map.get(databaseName)==null){
            synchronized (databaseName) {
                if(mongoDb_map.get(databaseName)==null){
                    if(mongoClient==null){ 	initDBPrompties();  }
                    MongoDatabase mongoDb=mongoClient.getDatabase(databaseName).withReadPreference(ReadPreference.secondaryPreferred());
                    mongoDb_map.put(databaseName,mongoDb);
                }
            }
        }
        return mongoDb_map.get(databaseName);
    }


    /***
     * 获取数据连接，自己获取库/collections
     * @return
     */
    public static MongoClient getMongoClient(){
        if(mongoClient==null){
            initDBPrompties();
        }
        return mongoClient;
    }
}

