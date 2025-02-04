package app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Configuration;


@Configuration
public class S3Config {
    // application 에서 aws s3 동작
    // 동작하려면 어떻게 해야 하는가?
    // => @Bean 으로 등록
    // awsAccessKey 어떻게? => 상수로 해도 위험 => git 같은 데 올리면 외부로 유출
    // 이제 .properties 올리면 안 돼 ; 위험
    // properties 에다가 key 기록해서 가지고 와

    @Value("${cloud.aws.credentials.access-key}")
    private String awsAccessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String awsSecretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3 s3client() {

        // S3 사용 인증 객체
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);

        // 리전 정보 입력 -> S3 사용 객체 생성
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }
}
