package com.miaosha.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
@Service
public class RedisService {

	@Autowired
	JedisPool jedisPool;
//
//	static {
//		JedisPoolConfig poolConfig = new JedisPoolConfig();
//		poolConfig.setMaxIdle(10);
//		poolConfig.setMaxTotal(10);
//		poolConfig.setMaxWaitMillis(10 * 1000);
//
//		jedisPool = new JedisPool(poolConfig, "127.0.0.1", 6379, 10000, "123456");
//	}

	public String get(String key){
		Jedis jedis=null;
		try {
			jedis = jedisPool.getResource();
			String value=jedis.get(key);
			return value;
		}catch (Exception e){
			log.error("get key:{} error" ,key,e);
			return null;
		} finally {
			returnToPool(jedis);
		}
	}

	public String set(String key,String value){
		Jedis jedis=null;
		try{
			jedis = jedisPool.getResource();
			String result=jedis.set(key,value);
			return result;
		}catch (Exception e){
			log.error("set key:{} error" ,key,e);
			return null;
		} finally {
			returnToPool(jedis);
		}
	}

	public  Long del(String key){
		Jedis jedis=null;
		try{
			jedis = jedisPool.getResource();
			Long result=jedis.del(key);
			return result;
		}catch (Exception e){
			log.error("del key:{} error" ,key,e);
			return null;
		}finally {
			returnToPool(jedis);
		}
	}

	public String setEx(String key,String value,int exTime){
		Jedis jedis=null;
		try{
			jedis = jedisPool.getResource();
			String result=jedis.setex(key,exTime,value);
			return result;
		}catch (Exception e){
			log.error("setex key:{} error" ,key,e);
			return null;
		}finally {
			returnToPool(jedis);
		}
	}

	public String getSet(String key,String value){
		Jedis jedis=null;
		try{
			jedis = jedisPool.getResource();
			String result=jedis.getSet(key,value);
			return result;
		}catch (Exception e){
			log.error("getSet key:{} error" ,key,e);
			return null;
		}finally {
			returnToPool(jedis);
		}
	}
	public Long setnx(String key,String value){
		Jedis jedis=null;
		try{
			jedis = jedisPool.getResource();
			Long result=jedis.setnx(key,value);
			return result;
		}catch (Exception e){
			log.error("getSet key:{} error" ,key,e);
			return null;
		}finally {
			returnToPool(jedis);
		}
	}

	public Long decr(String key){
		Jedis jedis=null;
		try{
			jedis=jedisPool.getResource();
			Long result=jedis.decr(key);
			return result;
		}finally {
			returnToPool(jedis);
		}
	}

	public Long incr(String key) {
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			Long result=jedis.incr(key);
			return  result;
		}finally {
			returnToPool(jedis);
		}
	}

	private void returnToPool(Jedis jedis) {
		if(jedis != null) {
			jedis.close();
		}
	}

}
