package com.foo.hazelcast.pnlAggregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
public class HazelcastConfiguration {
    
    @Bean
    public HazelcastInstance getHazelcastInstance() {
        Config config = new Config();
        config.getGroupConfig().setName("dev");
        config.getGroupConfig().setPassword("dev");
        
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);
        
        config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1");
        config.getNetworkConfig().setInterfaces(new InterfacesConfig().setEnabled(true).addInterface("127.0.0.1"));
//        config.setProperty("hazelcast.socket.bind.any", "false");
        
        return Hazelcast.newHazelcastInstance(config);
        
    }
}