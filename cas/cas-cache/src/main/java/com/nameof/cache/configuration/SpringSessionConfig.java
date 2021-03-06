package com.nameof.cache.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import com.nameof.common.constant.Constants;
import com.nameof.common.constant.SessionAccessor;
import com.nameof.common.enums.DataFormatEnum;
/**
 * 这里默认集成redis，除此之外，spring-session还支持
 * org.redisson.spring.session.config.EnableRedissonHttpSession
 * org.springframework.session.data.gemfire.config.annotation.web.http.EnableGemFireHttpSession
 * org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession
 * org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession
 * org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession
 * @author ChengPan
 */
@EnableRedisHttpSession
@Profile(SessionAccessor.SPRING_SESSION)
public class SpringSessionConfig extends AbstractHttpSessionApplicationInitializer {
	
	@Value("${redis.host}")
	private String redisHost;
	
	@Value("${redis.port}")
	private int redisPort;
	
	@Value("${session.format}")
	private DataFormatEnum format = DataFormatEnum.BINARY;
	
	@Bean
    public JedisConnectionFactory connectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisHost);
        factory.setPort(redisPort);
        return factory;
    }
	
	@Bean(name = "springSessionDefaultRedisSerializer")
	public RedisSerializer<Object> serializer() {
        switch (format) {
			case JSON:
				return new GenericJackson2JsonRedisSerializer();
			default:
				return new JdkSerializationRedisSerializer();
		}
    }
	
	@Bean
    public CookieSerializer cookieSerializer() {
            DefaultCookieSerializer serializer = new DefaultCookieSerializer();
            //修改spring-session的cookieName为本cas系统全局统一的sessionid，以支持扫码登录、单点登录
            serializer.setCookieName(Constants.GLOBAL_SESSION_ID); 
            return serializer;
    }
}
