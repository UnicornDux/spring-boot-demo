package com.xkcoding.websocket.server;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * websocket 服务端处理程序
 *
 * 因为WebSocket是类似客户端服务端的形式(采用ws协议)，那么这里的WebSocketServer其实就相当于
 * 一个ws协议的Controller
 *
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 * 值得注意的是:
 *
 * |>-------------------------------------------------------------------------<|
 * |>>>>  以下这些注解定义的规范都是在 jakarta 这个包里面，在 jdk 9 之前这个包是 javax <<<|
 * |>-------------------------------------------------------------------------<|
 *    - @OnOpen
 *    - @OnClose
 *    - @OnMessage
 *    - @OnError
 *
 * 新建一个ConcurrentHashMap webSocketMap 用于接收当前userId的WebSocket，
 * 方便传递之间对userId进行推送消息。
 *
 */
@Slf4j
@Component
@ServerEndpoint("/api/websocket/{sid}")
public class WebsocketServer {

  // 静态变量,用于记录当前在线的连接数
  private static int onlineCount = 0;
  // Concurrent 包中的 Set (线程安全的) 用于存放每个客户端连接的 websocket 连接对象
  private static CopyOnWriteArraySet<WebsocketServer> websocketSet = new CopyOnWriteArraySet<>();
  // 与 某个客户端的连接会话,需要通过它来给客户端发送数据
  private Session session;

  // 接收 sid
  private String sid = "";

  /**
   * 连接建立成功调用的方法
   */
  @OnOpen
  public void onOpen(Session session, @PathParam("sid") String sid) {
    this.session = session;
    websocketSet.add(this);
    this.sid = sid;
    addOnlineCount();
    try {
      sendMessage("connected_success");
      log.info("有新窗口开始监听:" + sid + ",当前在线人数:" + getOnlineCount());
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      log.info("websocket IO Exception");
    }
  }

  /**
   * 连接关闭时调用
   */
  @OnClose
  public void onClose() {
    websocketSet.remove(this);
    subOnlineCount();
    // 断开连接的情况下，更新主板占用情况为释放
    log.info("用户下线:" + sid);
    // 连接释放时候需要进行的业务处理
    // ......
    log.info("用户下线，当前连接数为:" + getOnlineCount());
  }

  /**
   * 收到客户端消息之后调用的方法
   */
  @OnMessage
  public void onMessage(String message, Session session) {
    log.info("收到来自窗口:" + sid + "的信息:" + message);
    // 群发消息
    for (WebsocketServer server : websocketSet) {
      try {
        server.sendMessage(message);
      } catch (IOException e) {
        e.printStackTrace();
        log.info("消息发送失败:==>>");
      }
    }
  }

  /**
   * 发生错误时候的回调·
   */
  @OnError
  public void onError(Session session, Throwable error) {
    log.error("发生错误==>>");
    error.printStackTrace();
  }

  /**
   * 实现服务器消息的主动推送
   *
   * @param message
   * @throws IOException
   */
  public void sendMessage(String message) throws IOException {
    this.session.getBasicRemote().sendText(message);
  }

  /**
   * 群发自定义消息
   */
  public static void sendInfo(String message, @PathParam("sid") String sid) {
    log.info("推送消息窗口:" + sid + ",推送内容：" + message);
    for (WebsocketServer item : websocketSet) {
      try {
        // 设定 当 sid 不为 null 的时候给这个sid 推送，否则全部都推送
        if (sid == null) {
          item.sendMessage(message);
        } else if (item.sid.equals(sid)) {
          item.sendMessage(message);
        }
      } catch (IOException e) {
        log.error(item.sid + "推送消息异常：" + e.getMessage());
        continue;
      }
    }
  }

  /**
   * 获取在线用户数量
   *
   * @return
   */
  public static synchronized int getOnlineCount() {
    return onlineCount;
  }

  /**
   * 添加在线用户数
   */
  private static synchronized void addOnlineCount() {
    WebsocketServer.onlineCount++;
  }

  /**
   * 人员下线
   */
  public static synchronized void subOnlineCount() {
    WebsocketServer.onlineCount--;
  }

  /**
   * 获取会话存储数据
   */
  public static CopyOnWriteArraySet<WebsocketServer> getWebsocketSet() {
    return websocketSet;
  }

  public void sendMessage(String message, String userId) {
    log.info("推送消息窗口:" + userId + ",推送内容：" + message);
    for (WebsocketServer item : websocketSet) {
      try {
        // 给当前这个发起启动命令的人来发送日志信息
        if (item.sid.equals(userId)) {
          item.sendMessage(message);
        }
      } catch (IOException e) {
        log.error(item.sid + "推送消息异常：" + e.getMessage());
        continue;
      }
    }
  }
}
