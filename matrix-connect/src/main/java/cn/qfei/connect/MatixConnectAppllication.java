package cn.qfei.connect;


import cn.qfei.crm.conf.SpringContextHolder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@MapperScan(basePackages = "cn.qfei.connect.mapper")
@EnableConfigurationProperties
@Import(SpringContextHolder.class)
public class MatixConnectAppllication {
    public static void main(String[] args) {
        SpringApplication.run(MatixConnectAppllication.class, args);    }
}
