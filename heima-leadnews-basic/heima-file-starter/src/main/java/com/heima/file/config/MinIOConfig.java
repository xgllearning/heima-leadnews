package com.heima.file.config;

import com.heima.file.service.FileStorageService;
import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration//配置类
@EnableConfigurationProperties({MinIOConfigProperties.class})//minioConfigProperties类生效
//当引入FileStorageService接口时
@ConditionalOnClass(FileStorageService.class)//只要引入jar包，条件生效，肯定存在所以一定会生效
public class MinIOConfig {//TODO：当别的微服务引用的时候，此配置类不会生效，因为别的微服务的启动类扫描不到

    @Autowired
    private MinIOConfigProperties minIOConfigProperties;

    @Bean
    public MinioClient buildMinioClient() {//也可以在此处传MinIOConfigProperties参数
        return MinioClient
                .builder()
                .credentials(minIOConfigProperties.getAccessKey(), minIOConfigProperties.getSecretKey())
                .endpoint(minIOConfigProperties.getEndpoint())
                .build();
    }
}