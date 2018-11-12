package com.pflm.mongodb;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.mongodb.Block;
import com.mongodb.DBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Aggregates.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;


/**
 * mmongodb操作工具类
 * @author qinxuewu
 * @version 1.00
 * @time 7/11/2018下午 2:15
 */
public class MongoSdkBase {


    /**
     * 表连接
     * @param tableName    表名
     * @return
     */
    public static MongoCollection getColl(String tableName){
        MongoDatabase db=MongoFactory.getMongoDb();
        return db.getCollection(tableName);
    }

    /**
     * 表连接
     * @param databaseName  数据库名
     * @param tableName    表名
     * @return
     */
    public static MongoCollection getCollT(String databaseName,String tableName){
        MongoDatabase db=MongoFactory.getMongoDb(databaseName);
        return db.getCollection(tableName);
    }
    /***
     * 插入单条记录
     * @param table  表连接
     * @param obj 单条数据
     * @return
     */
    public static String insertOne(MongoCollection table,Object obj){
        if(obj==null)return null;
        Document docine=new Document().parse(diyObjectIdToJson(obj));
        docine.remove("_id");
        docine.put("_id",new ObjectId().toString());
        table.insertOne(docine);
        return docine.get("_id").toString();
    }
    /**
     * 删除找到的第一条记录
     * @param table  表连接
     * @return
     */
    public static int deleteOne(MongoCollection table,String id){
        Bson filter=eq("_id",id);
        DeleteResult re=table.deleteOne(filter);
        return (int)re.getDeletedCount();
    }
    /**
     * 根据条件更新单条记录
     * @param table 表连接
     * @param obj
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean updateOne(MongoCollection table,String id,Object obj){
        Bson filter=eq("_id",id);//and(eq("_id",_id));
        table.updateOne(filter,_set(diyObjectIdToJson(obj)));
        return true;
    }

    /**
     * 根据条件更新单条记录
     * @param table  表连接
     * @param filter 条件
     * @param obj    需要修改的对象
     * @return
     */
    public static boolean updateOne(MongoCollection table, Bson filter, Object obj) {
//		Bson filter = eq("_id", _id);// and(eq("_id",_id));
        table.updateOne(filter, _set(diyObjectIdToJson(obj)));
        return true;
    }
    /**
     * 查询 单条记录 返回json字符串
     * @param table 表连接
     * @param
     * @return
     */
    public static String seleteOne(MongoCollection table,String id){
        Bson filter=eq("_id",id);
        return diyObjectIdToJson(seleteOneDocument(table, filter));
    }
    /**
     * 查询 单条记录 返回json字符串
     * @param table 表连接
     * @param filter
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String seleteOne(MongoCollection table,Bson filter){
        String diyObjectIdToJson = diyObjectIdToJson(seleteOneDocument(table, filter));
        return diyObjectIdToJson;
    }
    /**
     * 查询 单条记录 返回 org.bson.Document  对象
     * @param table 表连接
     * @param filter
     * @return
     */
    public static Document seleteOneDocument(MongoCollection table,Bson filter){
        FindIterable<Document> result = table.find(filter);
        return result.first();
    }
    /**
     * 查询所有记录  代码控制返回结果数
     * @param table 表连接
     * @param filter 条件  com.mongodb.client.model.Filter
     * @param sort 排序    com.mongodb.client.model.Sorts   可空
     * @return
     */
    public static List<JSONObject> getAll(MongoCollection table,Bson filter,Bson sort){
        List<JSONObject> list=new ArrayList<JSONObject>();
        FindIterable<Document> result=null;
        if(filter==null){
            result=table.find().sort(sort);
        } else{
            result=table.find(filter).sort(sort);
        }
        MongoCursor<Document> iterator = result.iterator();

        while(iterator.hasNext()){
            Object ddd =iterator.next();
            list.add(JSON.parseObject(diyObjectIdToJson(ddd)));
        }
        return list;
    }


    /**
     * 分页查询
     * @param table 表连接
     * @param filter 条件  com.mongodb.client.model.Filter
     * @param sort 排序    com.mongodb.client.model.Sorts
     * //ascending(orderBy); //descending("");
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static JSONObject getPage(MongoCollection table,Bson filter,Bson sort,int pageNum, int pageSize){
        if(pageNum==0){ pageNum=1; }
        if(pageSize==0) {pageSize=10;}
        int totalCount=(int) (filter==null? table.count():table.count(filter));
        int totalPage = (int)(totalCount/pageSize + ((totalCount % pageSize== 0) ? 0 : 1));
        JSONObject msg=new JSONObject();
        msg.put("pageNum",pageNum);msg.put("pageSize",pageSize);
        msg.put("totalCount",totalCount);msg.put("totalPage",totalPage);
        List<JSONObject> list=new ArrayList<JSONObject>();
        if(totalCount>0){
            int startRow=pageNum>0?(pageNum - 1)*pageSize:0;
            FindIterable<Document> result=null;
            if(filter==null){result=table.find().sort(sort).skip(startRow).limit(pageSize);}
            else{result=table.find(filter).sort(sort).skip(startRow).limit(pageSize); }
            MongoCursor<Document> iterator = result.iterator();
            while(iterator.hasNext()){
                Document ddd = (Document) iterator.next();
                list.add(JSON.parseObject(diyObjectIdToJson(ddd)));
            }
        }
        msg.put("data",list);
        return msg;
    }


    /**
     * 聚哈函数 统计  ,分组分页查询
     * @param table
     * @param filter
     * @param sort
     * @param pageNum
     * @param pageSize
     * @return
     * 2017年9月19日
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static JSONObject getAggregatePage(MongoCollection table,Bson filter,DBObject group,Bson sort,int pageNum, int pageSize){
        if(pageNum==0) {pageNum=1;}if(pageSize==0) {pageSize=10;}
        int totalCount=0;
        final List<JSONObject> count=new ArrayList<>();
        AggregateIterable<Document> iterableCount =table.aggregate(Arrays.asList(match(filter),group));
        iterableCount.forEach(new Block<Document>() {
            public void apply(final Document document) {
                count.add(JSON.parseObject(diyObjectIdToJson(document)));
            }
        });
        totalCount=count.size();
        int totalPage = (int)(totalCount/pageSize + ((totalCount % pageSize== 0) ? 0 : 1));

        JSONObject msg=new JSONObject();
        msg.put("pageNum",pageNum);msg.put("pageSize",pageSize);
        msg.put("totalCount",totalCount);msg.put("totalPage",totalPage);
        final List<JSONObject> list=new ArrayList<JSONObject>();
        if(totalCount>0){
            int startRow=pageNum>0?(pageNum - 1)*pageSize:0;
            AggregateIterable<Document> iterable =table.aggregate(Arrays.asList(match(filter),group,skip(startRow),limit(pageSize),sort(sort)));
            iterable.forEach(new Block<Document>() {
                public void apply(final Document document) {
                    list.add(JSON.parseObject(diyObjectIdToJson(document)));
                }
            });
        }
        msg.put("data",list);
        return msg;
    }

    /**
     * 聚哈函数 统计  ,查询
     * @param table
     * @param filter
     * @param group
     * @param sort
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<JSONObject> getAggregateList(MongoCollection table,Bson filter,DBObject group,Bson sort){
        final List<JSONObject> list=new ArrayList<JSONObject>();
        AggregateIterable<Document> iterable =table.aggregate(Arrays.asList(match(filter),group,sort(sort)));
        iterable.forEach(new Block<Document>() {
            public void apply(final Document document) {
                list.add(JSON.parseObject(diyObjectIdToJson(document)));
            }
        });
        return list;
    }

    private static SerializeFilter  objectIdSerializer = new ValueFilter  () {
        @Override
        public Object process(Object object, String name, Object value){
            if ("_id".equals(name)) {
                if (value instanceof ObjectId){ return value.toString();  }
            }
            return value;
        }
    };
    /**
     * 出库后查询
     * @param object
     * @return
     */
    public static final String diyObjectIdToJson( Object object){
        return JSON.toJSONString(object,objectIdSerializer,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty);
    }

    /**
     * 出库后查询 null跳过不转换为json
     * @param object
     * @return
     */
    public static final String diyObjToJson( Object object){
        return JSON.toJSONString(object,objectIdSerializer,
                SerializerFeature.WriteDateUseDateFormat);
    }
    /**
     * 更新数据 注意 多余字段 会在库表记录追加
     * @param json
     * @return
     */
    public static Document _set(String json){
        Document b=new Document().parse(json);
        //	b.put(fieldName, value);
        b.remove("_id");
        return new Document("$set", b);
    }
    /**
     * 查询数量
     * @param collection
     * @param bson
     * @return
     */
    public static long getCount(MongoCollection<?> collection,Bson bson){
        long count = 0L;
        try{
            count = bson!=null?collection.count(bson):collection.count();
        }catch (Exception e) {
            count = -1L;
            e.printStackTrace();
        }
        return count;
    }
}
