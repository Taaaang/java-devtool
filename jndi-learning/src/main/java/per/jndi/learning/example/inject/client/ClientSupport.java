package per.jndi.learning.example.inject.client;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import per.jndi.learning.example.inject.common.ConstantSupport;

/**
 * @author: TangFenQi
 * @description: 客户端（被入侵对象）需要配置参数[com.sun.jndi.rmi.object.trustURLCodebase=true]
 * @date：2021/12/11 11:30
 */
public class ClientSupport {

  public static void info(String name) throws NamingException, ClassNotFoundException {
    Context context = new InitialContext();
    System.out.println("[client]入侵地址:" + name);
    Object lookup = context.lookup(name);
    if (lookup instanceof String) {
      System.out.println("[client]接收到结果:" + lookup);
    }
    Class<?> aClass = ClientSupport.class.getClassLoader()
        .loadClass("per.jndi.learning.example.inject.server.undoer.OpenCalc");
    System.out.println("[client]加载server类:" + aClass.getName());
    System.out.println("[client]客户端加载资源完毕!");
  }

  public static void main(String[] args) throws NamingException, ClassNotFoundException {

    info(ConstantSupport.URL);
  }

}
