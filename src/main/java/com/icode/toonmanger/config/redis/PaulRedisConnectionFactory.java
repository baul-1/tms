package com.icode.toonmanger.config.redis;


import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;

public class PaulRedisConnectionFactory implements RedisConnectionFactory {


    private PaulRedisConnection redisConnection = new PaulRedisConnection();

    @Override
    public RedisConnection getConnection() {
        return redisConnection;
    }

    @Override
    public RedisClusterConnection getClusterConnection() {
        throw new RuntimeException("A18");
    }

    @Override
    public boolean getConvertPipelineAndTxResults() {
        return false;
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        throw new RuntimeException("redisConnection");
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {

        return new abc(ex.getMessage(),ex);
    }
}


class abc extends DataAccessException{

    public abc(String msg, Throwable cause) {
        super(msg, cause);
    }
}