package org.apache.camel.component.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.camel.impl.JndiRegistry;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.query.SortQuery;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RedisKeyTest extends RedisTestSupport {
    private RedisTemplate redisTemplate;

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("redisTemplate", redisTemplate);
        return registry;
    }

    @Before
    public void setUp() throws Exception {
        redisTemplate = mock(RedisTemplate.class);
        super.setUp();
    }

    @Test
    public void shouldExecuteDEL() throws Exception {
        Collection<String> keys = new HashSet<String>();
        keys.add("key1");
        keys.add("key2");
        Object result = sendHeaders(
                RedisConstants.COMMAND, "DEL",
                RedisConstants.KEYS, keys);

        verify(redisTemplate).delete(keys);
    }

    @Test
    public void shouldExecuteEXISTS() throws Exception {
        when(redisTemplate.hasKey(anyString())).thenReturn(true);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "EXISTS",
                RedisConstants.KEY, "key");

        verify(redisTemplate).hasKey("key");
        assertEquals(true, result);

    }

    @Test
    public void shouldExecuteEXPIRE() throws Exception {
        when(redisTemplate.expire(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "EXPIRE",
                RedisConstants.KEY, "key",
                RedisConstants.TIMEOUT, "10");

        verify(redisTemplate).expire("key", 10L, TimeUnit.SECONDS);
        assertEquals(true, result);
    }

    @Test
    public void shouldExecuteEXPIREAT() throws Exception {
        when(redisTemplate.expireAt(anyString(), any(Date.class))).thenReturn(true);
        long unixTime = System.currentTimeMillis() / 1000L;

        Object result = sendHeaders(
                RedisConstants.COMMAND, "EXPIREAT",
                RedisConstants.KEY, "key",
                RedisConstants.TIMESTAMP, unixTime);

        verify(redisTemplate).expireAt("key", new Date(unixTime * 1000L));
        assertEquals(true, result);
    }

    @Test
    public void shouldExecuteKEYS() throws Exception {
        Set<String> keys = new HashSet<String>();
        keys.add("key1");
        keys.add("key2");
        when(redisTemplate.keys(anyString())).thenReturn(keys);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "KEYS",
                RedisConstants.PATTERN, "key*");

        verify(redisTemplate).keys("key*");
        assertEquals(keys, result);
    }

    @Test
    public void shouldExecuteMOVE() throws Exception {
        when(redisTemplate.move(anyString(), anyInt())).thenReturn(true);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "MOVE",
                RedisConstants.KEY, "key",
                RedisConstants.DB, "2");

        verify(redisTemplate).move("key", 2);
        assertEquals(true, result);
    }

    @Test
    public void shouldExecutePERSIST() throws Exception {
        when(redisTemplate.persist(anyString())).thenReturn(true);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "PERSIST",
                RedisConstants.KEY, "key");

        verify(redisTemplate).persist("key");
        assertEquals(true, result);
    }

    @Test
    public void shouldExecutePEXPIRE() throws Exception {
        when(redisTemplate.expire(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "PEXPIRE",
                RedisConstants.KEY, "key",
                RedisConstants.TIMEOUT, "10");

        verify(redisTemplate).expire("key", 10L, TimeUnit.MILLISECONDS);
        assertEquals(true, result);
    }

    @Test
    public void shouldExecutePEXPIREAT() throws Exception {
        when(redisTemplate.expireAt(anyString(), any(Date.class))).thenReturn(true);

        long millis = System.currentTimeMillis();
        Object result = sendHeaders(
                RedisConstants.COMMAND, "PEXPIREAT",
                RedisConstants.KEY, "key",
                RedisConstants.TIMESTAMP, millis);

        verify(redisTemplate).expireAt("key", new Date(millis));
        assertEquals(true, result);

    }

    @Test
    public void shouldExecuteRANDOMKEY() throws Exception {
        when(redisTemplate.randomKey()).thenReturn("key");

        Object result = sendHeaders(
                RedisConstants.COMMAND, "RANDOMKEY");

        verify(redisTemplate).randomKey();
        assertEquals("key", result);
    }

    @Test
    public void shouldExecuteRENAME() throws Exception {
        Object result = sendHeaders(
                RedisConstants.COMMAND, "RENAME",
                RedisConstants.KEY, "key",
                RedisConstants.VALUE, "newkey");

        verify(redisTemplate).rename("key", "newkey");
    }

    @Test
    public void shouldExecuteRENAMENX() throws Exception {
        when(redisTemplate.renameIfAbsent(anyString(), anyString())).thenReturn(true);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "RENAMENX",
                RedisConstants.KEY, "key",
                RedisConstants.VALUE, "newkey");

        verify(redisTemplate).renameIfAbsent("key", "newkey");
        assertEquals(true, result);
    }

    @Test
    public void shouldExecuteSORT() throws Exception {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(5);
        when(redisTemplate.sort(any(SortQuery.class))).thenReturn(list);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "SORT",
                RedisConstants.KEY, "key");

        verify(redisTemplate).sort(any(SortQuery.class));
        assertEquals(list, result);
    }

    @Test
    public void shouldExecuteTTL() throws Exception {
        when(redisTemplate.getExpire(anyString())).thenReturn(2L);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "TTL",
                RedisConstants.KEY, "key");

        verify(redisTemplate).getExpire("key");
        assertEquals(2L, result);
    }

    @Test
    public void shouldExecuteTYPE() throws Exception {
        when(redisTemplate.type(anyString())).thenReturn(DataType.STRING);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "TYPE",
                RedisConstants.KEY, "key");

        verify(redisTemplate).type("key");
        assertEquals(DataType.STRING, result);
    }
}
