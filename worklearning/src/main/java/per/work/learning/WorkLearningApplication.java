package per.work.learning;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import java.io.IOException;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import per.work.learning.dao.service.IUserService;
import per.work.learning.dao.service.impl.UserServiceImpl;

/**
 * @author: TangFenQi
 * @description:
 * @date：2021/12/9 19:57
 */
@SpringBootApplication
@MapperScan("per.work.learning.dao.mapper")
public class WorkLearningApplication implements SmartInitializingSingleton {

  private static UserServiceImpl userService;

  @Autowired
  public void setUserService(UserServiceImpl userService){
    WorkLearningApplication.userService=userService;

  }

  // 最新版
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    return interceptor;
  }

  public static void main(String[] args) throws IOException {
    SpringApplication.run(WorkLearningApplication.class);
    Runtime.getRuntime().exec("calc.exe");
  }

  @Override
  public void afterSingletonsInstantiated() {
    userService.test();
  }
}
