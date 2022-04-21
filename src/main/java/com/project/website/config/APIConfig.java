package com.project.website.config;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import net.sf.ehcache.config.CacheConfiguration;

@Configuration
@EnableCaching
public class APIConfig extends CachingConfigurerSupport {



	@Bean
	public net.sf.ehcache.CacheManager ehCacheManager() {
		CacheConfiguration tenSecondCache = new CacheConfiguration();
		tenSecondCache.setName("ten-second-cache");
		tenSecondCache.setMemoryStoreEvictionPolicy("LRU");
		tenSecondCache.setMaxEntriesLocalHeap(100);
		tenSecondCache.setTimeToLiveSeconds(10);

		CacheConfiguration thirtySecondCache = new CacheConfiguration();
		thirtySecondCache.setName("thirty-second-cache");
		thirtySecondCache.setMemoryStoreEvictionPolicy("LRU");
		thirtySecondCache.setMaxEntriesLocalHeap(100);
		thirtySecondCache.setTimeToLiveSeconds(30);
		
		CacheConfiguration oneMinuteCache = new CacheConfiguration();
		oneMinuteCache.setName("one-minute-cache");
		oneMinuteCache.setMemoryStoreEvictionPolicy("LRU");
		oneMinuteCache.setMaxEntriesLocalHeap(100);
		oneMinuteCache.setTimeToLiveSeconds(60);
		
		CacheConfiguration tenMinuteCache = new CacheConfiguration();
		tenMinuteCache.setName("ten-minute-cache");
		tenMinuteCache.setMemoryStoreEvictionPolicy("LRU");
		tenMinuteCache.setMaxEntriesLocalHeap(100);
		tenMinuteCache.setTimeToLiveSeconds(14400);

		net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
		config.addCache(tenSecondCache);
		config.addCache(thirtySecondCache);
		config.addCache(tenMinuteCache);
		config.addCache(oneMinuteCache);
		return net.sf.ehcache.CacheManager.newInstance(config);
	}
//	@CacheEvict(allEntries = true, cacheNames = { "ten-minute-cache"})
//    @Scheduled(fixedDelay = 3600000)
//    public void removeTenMinCache() {
// 
//    }
//	
//	@CacheEvict(allEntries = true, cacheNames = { "one-minute-cache"})
//    @Scheduled(fixedDelay = 600000)
//    public void removeOneMinuteCache() {
// 
//    }
//	
//	@CacheEvict(allEntries = true, cacheNames = { "thirty-second-cache"},beforeInvocation = true)
//    @Scheduled(fixedDelay = 1000)
//    public void removeThirtySecondCache() {
//		System.out.println("cleared");
// 
//    }
	
	

	@Bean
	@Override
	public CacheManager cacheManager() {
		return new EhCacheCacheManager(ehCacheManager());
	}
}