package com.demo.huyaxiaochengxu.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;

@Component
public class JedisAdapter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost",6379);
    }

    public void set(String key, String value, int time) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
            jedis.expire(key, time);
            logger.info("set "+key+" value:"+value+ " into redis expireTime:"+time);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String get(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            logger.info("get "+key+" from redis!");
            return jedis.get(key);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
            return null;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long sadd(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
            return 0;
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public long srem(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
            return 0;
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean sismember(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
            return false;
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
            return 0;
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public void setex(String key,String value){
        // 验证码, 防机器注册，记录上次注册时间，有效期3天
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            jedis.setex(key,10,value);
        }catch(Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally{
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long lpush(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.lpush(key,value);
        }catch(Exception e){
            logger.error("发生异常"+e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout,String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.brpop(timeout,key);
        }catch(Exception e){
            logger.error("发生异常"+e.getMessage());
            return null;
        }finally{
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public void setObject(String key,Object obj){
        set(key, JSON.toJSONString(obj),0);
    }

    public <T> T getObject(String key,Class<T> clazz){
        String value = get(key);
        if (value != null){
            return JSON.parseObject(value,clazz);
        }
        return null;
    }

    public Long hset(String hash, String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.hset(hash,key,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
            return null;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public String hget(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.hget(key,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
            return null;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public Long zadd(String key, Double score, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.zadd(key,score,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
            return null;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }







    public static void print(int index,Object obj){
        System.out.println(String.format("%d,%s",index,obj.toString()));
    }

    public static void main(String[] argv) {
        Jedis jedis = new Jedis();
        jedis.flushAll();

        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newhello");
        print(2, jedis.get("newhello"));
        jedis.setex("hello2", 15, "world");

        jedis.set("pv", "100");
        jedis.incr("pv");
        print(2, jedis.get("pv"));
        jedis.incrBy("pv", 5);

        String listName = "list";
        for (int i = 0; i < 10; ++i) {
            jedis.lpush(listName, String.valueOf(i));
        }
        print(3, jedis.lrange(listName, 0, 12));
        print(4, jedis.llen(listName));
        print(5, jedis.lpop(listName));
        print(6, jedis.llen(listName));
        print(7, jedis.lindex(listName, 7));
        print(8, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "4", "haha"));
        print(9, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "4", "hh"));
        print(10, jedis.lrange(listName, 0, 12));

        String userKey = "userxx";
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "12");
        print(11, jedis.hget(userKey, "name"));
        print(12, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "age");
        print(13, jedis.hkeys(userKey));
        print(14, jedis.hvals(userKey));
        print(15, jedis.hexists(userKey, "email"));
        jedis.hsetnx(userKey, "school", "scut");
        print(16, jedis.hgetAll(userKey));

        String likeKeys1 = "newsLike1";
        String likeKeys2 = "newsLike2";
        for (int i = 0; i < 10; ++i) {
            jedis.sadd(likeKeys1, String.valueOf(i));
            jedis.sadd(likeKeys2, String.valueOf(i * 2));
        }
        print(20, jedis.smembers(likeKeys1));
        jedis.sadd(likeKeys1,"haha");
        print(21, jedis.smembers(likeKeys2));
        print(22, jedis.sinter(likeKeys1, likeKeys2));
        print(23, jedis.sunion(likeKeys1, likeKeys2));
        print(24,jedis.sdiff(likeKeys1,likeKeys2));
        jedis.srem(likeKeys1,"5");
        print(25,jedis.sismember(likeKeys1,"5"));
        print(26,jedis.scard(likeKeys1));
        print(27,jedis.smove(likeKeys2,likeKeys1,"14"));
        print(28,jedis.smembers(likeKeys1));


        String rankKey="rankKey";
        jedis.zadd(rankKey,15,"jim");
        jedis.zadd(rankKey,60,"ben");
        jedis.zadd(rankKey,45,"mei");
        jedis.zadd(rankKey,100,"coral");
        print(30,jedis.zcount(rankKey,60,80));
        print(31,jedis.zscore(rankKey,"coral"));
        jedis.zincrby(rankKey,5,"mei");
        print(32,jedis.zscore(rankKey,"mei"));
        print(33,jedis.zrange(rankKey,0,2));
        print(43,jedis.zrevrange(rankKey,0,2));
        for (Tuple tuple:jedis.zrangeByScoreWithScores(rankKey,"0","100"))
        {
            print(37,tuple.getElement()+":"+String.valueOf(tuple.getScore()));
        }
        print(38,jedis.zrank(rankKey,"coral"));
        print(39,jedis.zrevrank(rankKey,"coral"));

        JedisPool pool = new JedisPool();
        for (int i=0;i<100;++i){
            Jedis j = pool.getResource();
            j.get("a");
            System.out.println("POOL"+i);
            j.close();
        }
    }
}
