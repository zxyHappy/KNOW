package com.bluemsun.know;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Test {

    //阿里云公网地址或者本地端口地址
    private static String HOST = "127.0.0.1";
    //端口号
    private static int PORT = 6379;
    //最大连接数
    private static int MAX_ACTIVE = 1024;
    //最大空闲连接数
    private static int MAX_IDLE = 200;
    //获取可用连接的最大等待时间
    private static int MAX_WAIT = 10000;

    private static JedisPool jedisPool = null;

    /**
     * 初始化redis连接池
     * */
    private static void initPool(){
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            //最大连接数
            config.setMaxTotal(MAX_ACTIVE);
            //最大空闲连接数
            config.setMaxIdle(MAX_IDLE);
            //获取可用连接的最大等待时间
            config.setMaxWaitMillis(MAX_WAIT);
            jedisPool = new JedisPool(config, HOST, PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取jedis实例
     * */
    public static Jedis getJedis() {
        try {
            if(jedisPool == null){
                initPool();
            }
            Jedis jedis = jedisPool.getResource();
            return jedis;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeJedis(Jedis jedis){
        if(jedis != null) {
            jedis.close();
        }
    }

//    public static void main(String[] args) {
//        Jedis jedis  = RedisUtil.getJedis();
//        System.out.println(jedis.ping());
//    }
    @org.junit.jupiter.api.Test
    public void test(){
        System.out.println(getJedis());
    }
}
