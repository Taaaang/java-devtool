package per.jndi.learning.example.inject.server;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.naming.NamingException;
import javax.naming.Reference;
import static per.jndi.learning.example.inject.common.ConstantSupport.*;

/**
 * @author: TangFenQi
 * @description: 服务端（进行注入）
 * @date：2021/12/11 11:35
 */
public class ServerSupport {

  public static void main(String[] args)
      throws RemoteException, NamingException, AlreadyBoundException {
    Registry registry= LocateRegistry.createRegistry(SERVER_PORT);
    Reference reference=new Reference("per.jndi.learning.example.inject.server.undoer.OpenCalc",
        "per.jndi.learning.example.inject.server.undoer.OpenCalc","http://localhost:/");
    registry.bind(METHOD,new ReferenceWrapper(reference));
    System.out.println("注入服务启动完毕");
  }

}
