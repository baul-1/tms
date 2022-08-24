package com.icode.toonmanger.config.redis;

import com.icode.toonmanger.config.CMap;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.core.types.RedisClientInfo;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class PaulRedisConnection implements RedisConnection {

    CMap store = new CMap();

    private static PaulRedisConnection self = new PaulRedisConnection();


    //Todo
    @Override
    public Boolean hSet(byte[] key, byte[] field, byte[] value) {

        String strKey = new String(key, StandardCharsets.UTF_8);
        String strField = new String(field, StandardCharsets.UTF_8);
        String strValue = new String(value, StandardCharsets.UTF_8);

        if(!store.containsKey(strKey)){
            store.put(strKey,new CMap());
        }

        Map ele = (Map) store.get(strKey);
        ele.put(strField,strValue);

        return true;
    }
    @Override
    public byte[] hGet(byte[] key, byte[] field) {
        String strKey = new String(key, StandardCharsets.UTF_8);
        String strField = new String(field, StandardCharsets.UTF_8);

        CMap der = (CMap) store.get(strKey);

        return  der.getS(strField).getBytes();
    }

    @Override
    public Long hDel(byte[] key, byte[]... fields) {

        String strKey = new String(key, StandardCharsets.UTF_8);

        store.remove(strKey);

        return null;
    }


    @Override
    public void close() throws DataAccessException {
        return;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public Object getNativeConnection() {
        return self;
    }

    @Override
    public boolean isQueueing() {
        return false;
    }

    @Override
    public boolean isPipelined() {
        return false;
    }

    @Override
    public void openPipeline() {
        return;
    }

    @Override
    public List<Object> closePipeline() throws RedisPipelineException {
        return new ArrayList<Object>();
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        throw new RuntimeException("PaulRedisConnection - getSentinelConnectionError");
    }

    @Override
    public Object execute(String command, byte[]... args) {
        return null;
    }

    @Override
    public void select(int dbIndex) {
        return;
    }

    @Override
    public byte[] echo(byte[] message) {
        return message;
    }

    @Override
    public String ping() {
        return "pong";
    }

    @Override
    public Long geoAdd(byte[] key, Point point, byte[] member) {
        return 123L;
    }

    @Override
    public Long geoAdd(byte[] key, Map<byte[], Point> memberCoordinateMap) {
        return 123L;
    }

    @Override
    public Long geoAdd(byte[] key, Iterable<GeoLocation<byte[]>> locations) {
        return 123L;
    }

    @Override
    public Distance geoDist(byte[] key, byte[] member1, byte[] member2) {
        return new Distance(0.9);
    }

    @Override
    public Distance geoDist(byte[] key, byte[] member1, byte[] member2, Metric metric) {
        return new Distance(0.9);
    }

    @Override
    public List<String> geoHash(byte[] key, byte[]... members) {
        return new ArrayList<String>();
    }

    @Override
    public List<Point> geoPos(byte[] key, byte[]... members) {
        return new ArrayList<Point>();
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadius(byte[] key, Circle within) {
        return null;
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadius(byte[] key, Circle within, GeoRadiusCommandArgs args) {
        return null;
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member, Distance radius) {
        return null;
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member, Distance radius, GeoRadiusCommandArgs args) {
        return null;
    }




    @Override
    public Long geoRemove(byte[] key, byte[]... members) {
        return null;
    }


    @Override
    public Long hIncrBy(byte[] key, byte[] field, long delta) {
        return null;
    }

    @Override
    public Double hIncrBy(byte[] key, byte[] field, double delta) {
        return null;
    }

    @Override
    public Boolean hExists(byte[] key, byte[] field) {
        return null;
    }

    @Override
    public Long hLen(byte[] key) {
        return null;
    }






    @Override
    public Cursor<Map.Entry<byte[], byte[]>> hScan(byte[] key, ScanOptions options) {
        return null;
    }

    @Override
    public Long hStrLen(byte[] key, byte[] field) {
        return null;
    }

    @Override
    public Long pfAdd(byte[] key, byte[]... values) {
        return null;
    }

    @Override
    public Long pfCount(byte[]... keys) {
        return null;
    }

    @Override
    public void pfMerge(byte[] destinationKey, byte[]... sourceKeys) {

    }





    @Override
    public Long del(byte[]... keys) {
        return null;
    }

    @Override
    public Long unlink(byte[]... keys) {
        return null;
    }

    @Override
    public DataType type(byte[] key) {
        return null;
    }

    @Override
    public Long touch(byte[]... keys) {
        return null;
    }

    @Override
    public Set<byte[]> keys(byte[] pattern) {
        return null;
    }

    @Override
    public Cursor<byte[]> scan(ScanOptions options) {
        return null;
    }

    @Override
    public byte[] randomKey() {
        return new byte[0];
    }

    @Override
    public void rename(byte[] sourceKey, byte[] targetKey) {

    }

    @Override
    public Boolean renameNX(byte[] sourceKey, byte[] targetKey) {
        return null;
    }




    @Override
    public Boolean pExpire(byte[] key, long millis) {
        return null;
    }

    @Override
    public Boolean expireAt(byte[] key, long unixTime) {
        return null;
    }

    @Override
    public Boolean pExpireAt(byte[] key, long unixTimeInMillis) {
        return null;
    }

    @Override
    public Boolean persist(byte[] key) {
        return null;
    }

    @Override
    public Boolean move(byte[] key, int dbIndex) {
        return null;
    }

    @Override
    public Long ttl(byte[] key) {
        return null;
    }

    @Override
    public Long ttl(byte[] key, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Long pTtl(byte[] key) {
        return null;
    }

    @Override
    public Long pTtl(byte[] key, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public List<byte[]> sort(byte[] key, SortParameters params) {
        return null;
    }

    @Override
    public Long sort(byte[] key, SortParameters params, byte[] storeKey) {
        return null;
    }

    @Override
    public byte[] dump(byte[] key) {
        return new byte[0];
    }

    @Override
    public void restore(byte[] key, long ttlInMillis, byte[] serializedValue, boolean replace) {

    }

    @Override
    public ValueEncoding encodingOf(byte[] key) {
        return null;
    }

    @Override
    public Duration idletime(byte[] key) {
        return null;
    }

    @Override
    public Long refcount(byte[] key) {
        return null;
    }

    @Override
    public Long rPush(byte[] key, byte[]... values) {
        return null;
    }

    @Override
    public List<Long> lPos(byte[] key, byte[] element, Integer rank, Integer count) {
        return null;
    }

    @Override
    public Long lPush(byte[] key, byte[]... values) {
        return null;
    }

    @Override
    public Long rPushX(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public Long lPushX(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public Long lLen(byte[] key) {
        return null;
    }

    @Override
    public List<byte[]> lRange(byte[] key, long start, long end) {
        return null;
    }

    @Override
    public void lTrim(byte[] key, long start, long end) {

    }

    @Override
    public byte[] lIndex(byte[] key, long index) {
        return new byte[0];
    }

    @Override
    public Long lInsert(byte[] key, Position where, byte[] pivot, byte[] value) {
        return null;
    }

    @Override
    public void lSet(byte[] key, long index, byte[] value) {

    }

    @Override
    public Long lRem(byte[] key, long count, byte[] value) {
        return null;
    }

    @Override
    public byte[] lPop(byte[] key) {
        return new byte[0];
    }

    @Override
    public byte[] rPop(byte[] key) {
        return new byte[0];
    }

    @Override
    public List<byte[]> bLPop(int timeout, byte[]... keys) {
        return null;
    }

    @Override
    public List<byte[]> bRPop(int timeout, byte[]... keys) {
        return null;
    }

    @Override
    public byte[] rPopLPush(byte[] srcKey, byte[] dstKey) {
        return new byte[0];
    }

    @Override
    public byte[] bRPopLPush(int timeout, byte[] srcKey, byte[] dstKey) {
        return new byte[0];
    }

    @Override
    public boolean isSubscribed() {
        return false;
    }

    @Override
    public Subscription getSubscription() {
        return null;
    }

    @Override
    public Long publish(byte[] channel, byte[] message) {
        return null;
    }

    @Override
    public void subscribe(MessageListener listener, byte[]... channels) {

    }

    @Override
    public void pSubscribe(MessageListener listener, byte[]... patterns) {

    }

    @Override
    public void scriptFlush() {

    }

    @Override
    public void scriptKill() {

    }

    @Override
    public String scriptLoad(byte[] script) {
        return null;
    }

    @Override
    public List<Boolean> scriptExists(String... scriptShas) {
        return null;
    }

    @Override
    public <T> T eval(byte[] script, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        return null;
    }

    @Override
    public <T> T evalSha(String scriptSha, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        return null;
    }

    @Override
    public <T> T evalSha(byte[] scriptSha, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        return null;
    }

    @Override
    public void bgReWriteAof() {

    }

    @Override
    public void bgSave() {

    }

    @Override
    public Long lastSave() {
        return null;
    }

    @Override
    public void save() {

    }

    @Override
    public Long dbSize() {
        return null;
    }

    @Override
    public void flushDb() {

    }

    @Override
    public void flushAll() {

    }

    @Override
    public Properties info() {
        return null;
    }

    @Override
    public Properties info(String section) {
        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void shutdown(ShutdownOption option) {

    }



    @Override
    public void resetConfigStats() {

    }

    @Override
    public void rewriteConfig() {

    }

    @Override
    public Long time(TimeUnit timeUnit) {
        return null;
    }

    @Override
    public void killClient(String host, int port) {

    }

    @Override
    public void setClientName(byte[] name) {

    }

    @Override
    public String getClientName() {
        return null;
    }

    @Override
    public List<RedisClientInfo> getClientList() {
        return null;
    }

    @Override
    public void slaveOf(String host, int port) {

    }

    @Override
    public void slaveOfNoOne() {

    }

    @Override
    public void migrate(byte[] key, RedisNode target, int dbIndex, MigrateOption option) {

    }

    @Override
    public void migrate(byte[] key, RedisNode target, int dbIndex, MigrateOption option, long timeout) {

    }

    @Override
    public Long sAdd(byte[] key, byte[]... values) {
        return null;
    }

    @Override
    public Long sRem(byte[] key, byte[]... values) {
        return null;
    }

    @Override
    public byte[] sPop(byte[] key) {
        return new byte[0];
    }

    @Override
    public List<byte[]> sPop(byte[] key, long count) {
        return null;
    }

    @Override
    public Boolean sMove(byte[] srcKey, byte[] destKey, byte[] value) {
        return null;
    }

    @Override
    public Long sCard(byte[] key) {
        return null;
    }

    @Override
    public Boolean sIsMember(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public Set<byte[]> sInter(byte[]... keys) {
        return null;
    }

    @Override
    public Long sInterStore(byte[] destKey, byte[]... keys) {
        return null;
    }

    @Override
    public Set<byte[]> sUnion(byte[]... keys) {
        return null;
    }

    @Override
    public Long sUnionStore(byte[] destKey, byte[]... keys) {
        return null;
    }

    @Override
    public Set<byte[]> sDiff(byte[]... keys) {
        return null;
    }

    @Override
    public Long sDiffStore(byte[] destKey, byte[]... keys) {
        return null;
    }

    @Override
    public Set<byte[]> sMembers(byte[] key) {
        return null;
    }

    @Override
    public byte[] sRandMember(byte[] key) {
        return new byte[0];
    }

    @Override
    public List<byte[]> sRandMember(byte[] key, long count) {
        return null;
    }

    @Override
    public Cursor<byte[]> sScan(byte[] key, ScanOptions options) {
        return null;
    }

    @Override
    public Long xAck(byte[] key, String group, RecordId... recordIds) {
        return null;
    }

    @Override
    public RecordId xAdd(MapRecord<byte[], byte[], byte[]> record, XAddOptions options) {
        return null;
    }

    @Override
    public List<RecordId> xClaimJustId(byte[] key, String group, String newOwner, XClaimOptions options) {
        return null;
    }

    @Override
    public List<ByteRecord> xClaim(byte[] key, String group, String newOwner, XClaimOptions options) {
        return null;
    }

    @Override
    public Long xDel(byte[] key, RecordId... recordIds) {
        return null;
    }

    @Override
    public String xGroupCreate(byte[] key, String groupName, ReadOffset readOffset) {
        return null;
    }

    @Override
    public String xGroupCreate(byte[] key, String groupName, ReadOffset readOffset, boolean mkStream) {
        return null;
    }

    @Override
    public Boolean xGroupDelConsumer(byte[] key, Consumer consumer) {
        return null;
    }

    @Override
    public Boolean xGroupDestroy(byte[] key, String groupName) {
        return null;
    }

    @Override
    public StreamInfo.XInfoStream xInfo(byte[] key) {
        return null;
    }

    @Override
    public StreamInfo.XInfoGroups xInfoGroups(byte[] key) {
        return null;
    }

    @Override
    public StreamInfo.XInfoConsumers xInfoConsumers(byte[] key, String groupName) {
        return null;
    }

    @Override
    public Long xLen(byte[] key) {
        return null;
    }

    @Override
    public PendingMessagesSummary xPending(byte[] key, String groupName) {
        return null;
    }

    @Override
    public PendingMessages xPending(byte[] key, String groupName, XPendingOptions options) {
        return null;
    }

    @Override
    public List<ByteRecord> xRange(byte[] key, org.springframework.data.domain.Range<String> range, Limit limit) {
        return null;
    }

    @Override
    public List<ByteRecord> xRead(StreamReadOptions readOptions, StreamOffset<byte[]>... streams) {
        return null;
    }

    @Override
    public List<ByteRecord> xReadGroup(Consumer consumer, StreamReadOptions readOptions, StreamOffset<byte[]>... streams) {
        return null;
    }

    @Override
    public List<ByteRecord> xRevRange(byte[] key, org.springframework.data.domain.Range<String> range, Limit limit) {
        return null;
    }

    @Override
    public Long xTrim(byte[] key, long count) {
        return null;
    }

    @Override
    public Long xTrim(byte[] key, long count, boolean approximateTrimming) {
        return null;
    }





    @Override
    public Boolean setNX(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public Boolean setEx(byte[] key, long seconds, byte[] value) {
        return null;
    }

    @Override
    public Boolean pSetEx(byte[] key, long milliseconds, byte[] value) {
        return null;
    }








    @Override
    public Boolean mSetNX(Map<byte[], byte[]> tuple) {
        return null;
    }

    @Override
    public Long incr(byte[] key) {
        return null;
    }

    @Override
    public Long incrBy(byte[] key, long value) {
        return null;
    }

    @Override
    public Double incrBy(byte[] key, double value) {
        return null;
    }

    @Override
    public Long decr(byte[] key) {
        return null;
    }

    @Override
    public Long decrBy(byte[] key, long value) {
        return null;
    }

    @Override
    public Long append(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public byte[] getRange(byte[] key, long start, long end) {
        return new byte[0];
    }

    @Override
    public void setRange(byte[] key, byte[] value, long offset) {

    }

    @Override
    public Boolean getBit(byte[] key, long offset) {
        return null;
    }

    @Override
    public Boolean setBit(byte[] key, long offset, boolean value) {
        return null;
    }

    @Override
    public Long bitCount(byte[] key) {
        return null;
    }

    @Override
    public Long bitCount(byte[] key, long start, long end) {
        return null;
    }

    @Override
    public List<Long> bitField(byte[] key, BitFieldSubCommands subCommands) {
        return null;
    }

    @Override
    public Long bitOp(BitOperation op, byte[] destination, byte[]... keys) {
        return null;
    }

    @Override
    public Long bitPos(byte[] key, boolean bit, org.springframework.data.domain.Range<Long> range) {
        return null;
    }

    @Override
    public Long strLen(byte[] key) {
        return null;
    }

    @Override
    public void multi() {

    }

    @Override
    public List<Object> exec() {
        return null;
    }

    @Override
    public void discard() {

    }

    @Override
    public void watch(byte[]... keys) {

    }

    @Override
    public void unwatch() {

    }

    @Override
    public Boolean zAdd(byte[] key, double score, byte[] value, ZAddArgs args) {
        return null;
    }

    @Override
    public Long zAdd(byte[] key, Set<Tuple> tuples, ZAddArgs args) {
        return null;
    }

    @Override
    public Long zRem(byte[] key, byte[]... values) {
        return null;
    }

    @Override
    public Double zIncrBy(byte[] key, double increment, byte[] value) {
        return null;
    }

    @Override
    public Long zRank(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public Long zRevRank(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public Set<byte[]> zRange(byte[] key, long start, long end) {
        return null;
    }

    @Override
    public Set<Tuple> zRangeWithScores(byte[] key, long start, long end) {
        return null;
    }

    @Override
    public Set<Tuple> zRangeByScoreWithScores(byte[] key, Range range, Limit limit) {
        return null;
    }

    @Override
    public Set<byte[]> zRevRange(byte[] key, long start, long end) {
        return null;
    }

    @Override
    public Set<Tuple> zRevRangeWithScores(byte[] key, long start, long end) {
        return null;
    }

    @Override
    public Set<byte[]> zRevRangeByScore(byte[] key, Range range, Limit limit) {
        return null;
    }

    @Override
    public Set<Tuple> zRevRangeByScoreWithScores(byte[] key, Range range, Limit limit) {
        return null;
    }

    @Override
    public Long zCount(byte[] key, Range range) {
        return null;
    }

    @Override
    public Long zLexCount(byte[] key, Range range) {
        return null;
    }

    @Override
    public Long zCard(byte[] key) {
        return null;
    }

    @Override
    public Double zScore(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public Long zRemRange(byte[] key, long start, long end) {
        return null;
    }

    @Override
    public Long zRemRangeByLex(byte[] key, Range range) {
        return null;
    }

    @Override
    public Long zRemRangeByScore(byte[] key, Range range) {
        return null;
    }

    @Override
    public Long zUnionStore(byte[] destKey, byte[]... sets) {
        return null;
    }

    @Override
    public Long zUnionStore(byte[] destKey, Aggregate aggregate, Weights weights, byte[]... sets) {
        return null;
    }

    @Override
    public Long zInterStore(byte[] destKey, byte[]... sets) {
        return null;
    }

    @Override
    public Long zInterStore(byte[] destKey, Aggregate aggregate, Weights weights, byte[]... sets) {
        return null;
    }

    @Override
    public Cursor<Tuple> zScan(byte[] key, ScanOptions options) {
        return null;
    }

    @Override
    public Set<byte[]> zRangeByScore(byte[] key, String min, String max, long offset, long count) {
        return null;
    }

    @Override
    public Set<byte[]> zRangeByScore(byte[] key, Range range, Limit limit) {
        return null;
    }

    @Override
    public Set<byte[]> zRangeByLex(byte[] key, Range range, Limit limit) {
        return null;
    }

    @Override
    public Set<byte[]> zRevRangeByLex(byte[] key, Range range, Limit limit) {
        return null;
    }






















    @Override
    public Boolean hSetNX(byte[] key, byte[] field, byte[] value) {

        return null;
    }




    @Override
    public List<byte[]> hMGet(byte[] key, byte[]... fields) {
        return null;
    }

    @Override
    public void hMSet(byte[] key, Map<byte[], byte[]> hashes) {

    }

    @Override
    public Set<byte[]> hKeys(byte[] key) {
        return null;
    }

    @Override
    public List<byte[]> hVals(byte[] key) {
        return null;
    }

    @Override
    public Map<byte[], byte[]> hGetAll(byte[] key) {
        return null;
    }

    @Override
    public Long exists(byte[]... keys) {
        return null;
    }

    @Override
    public Boolean expire(byte[] key, long seconds) {
        return true;
    }

    @Override
    public Properties getConfig(String pattern) {
        return null;
    }

    @Override
    public void setConfig(String param, String value) {

    }

    @Override
    public byte[] get(byte[] key) {
        return new byte[0];
    }

    @Override
    public byte[] getSet(byte[] key, byte[] value) {
        return new byte[0];
    }

    @Override
    public List<byte[]> mGet(byte[]... keys) {
        return null;
    }

    @Override
    public Boolean set(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public Boolean set(byte[] key, byte[] value, Expiration expiration, SetOption option) {
        return null;
    }

    @Override
    public Boolean mSet(Map<byte[], byte[]> tuple) {
        return null;
    }
}
