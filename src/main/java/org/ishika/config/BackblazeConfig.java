package org.ishika.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class BackblazeConfig {

  @Bean
  public S3Presigner s3Presigner(@Value("${backblaze.b2.endpoint}") String endpoint,
      @Value("${backblaze.b2.region}") String region, @Value("${backblaze.b2.access-key}") String accessKey,
      @Value("${backblaze.b2.secret-key}") String secretKey) {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
    return S3Presigner.builder().endpointOverride(URI.create(endpoint)).region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(credentials)).build();
  }
}