package per.jndi.learning.example.inject.common;

/**
 * @author: TangFenQi
 * @description: 常量信息
 * @date：2021/12/11 11:37
 */
public class ConstantSupport {

  public static int SERVER_PORT = 1200;
  private static String HOST = "127.0.0.1";
  private static String PROTOCOL = "rmi";
  public static String METHOD = "access";
  public static String URL = PROTOCOL + "://" + HOST + ":" + SERVER_PORT + "/" + METHOD;

}
