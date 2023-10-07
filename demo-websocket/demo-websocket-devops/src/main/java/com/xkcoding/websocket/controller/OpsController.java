package com.xkcoding.websocket.controller;

import com.xkcoding.websocket.model.AppInfo;
import com.xkcoding.websocket.server.WebsocketServer;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ops")
public class OpsController {

  @Autowired
  private WebsocketServer websocketServer;

  @Value("${auto-deploy.back.script}")
  private String backScript;

  @Value("${auto-deploy.app.script}")
  private String appScript;

  @Value("${auto-deploy.web.script}")
  private String webScript;

  @PostMapping("/runApp/{userId}")
  public Map<String, Object> runApp(@RequestBody AppInfo appInfo, @PathVariable("userId") String userId) {
    Map<String, Object> map = new HashMap<>();
    if (appInfo == null
      || !StringUtils.hasLength(appInfo.getApplication())
      || !StringUtils.hasLength(appInfo.getEnvironment())) {
      map.put("code", "1");
      map.put("msg", "未收到足够的参数");
      return map;
    }
    runApp0(appInfo, userId);
    map.put("code", "0");
    map.put("msg", "ok");
    return map;
  }

  public void runApp0(AppInfo appInfo, String userId) {
    ProcessBuilder builder = new ProcessBuilder();
    List<String> command = new ArrayList<>();
    String environment = appInfo.getEnvironment();
    String script = "";
    String ops = "";
    String application = appInfo.getApplication();
    switch (application) {
      case "digital-back":
        script = backScript;
        ops = appInfo.getOperation();
        break;
      case "digital-app":
        script = appScript;
        break;
      case "digital-web":
        script = webScript;
        break;
      default:
        log.error("不支持的应用");
        return;
    }
    command.add("sh");
    command.add(script);
    command.add(environment);
    if (StringUtils.hasLength(ops)) {
      command.add(ops);
    }
    builder.command(command);
    builder.redirectErrorStream(true);
    try {
      Process p = builder.start();
      waitFor(p, userId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public int waitFor(Process p, String userId) {
    int exitValue = -1;
    try (
      InputStream in = p.getInputStream();
      InputStream error =  p.getErrorStream();
    ) {
      int retry = 0;
      int maxRetry = 600;// 每次休眠1秒，最长执行时间10分种
      boolean finished = false;
      StringBuffer outputString = new StringBuffer();
      while (!finished) {
        if (retry > maxRetry) {
          return exitValue;
        }
        try {
          byte[] buf = new byte[2048];
          while (in.available() > 0) {
            int n = in.read(buf);
            if (n > 0) {
              String message = new String(buf, StandardCharsets.UTF_8);
              boolean endline = message.endsWith("\n");
              String[] msgList = message.split("\n");
              if (endline) {
                for (int i = 0; i < msgList.length; i++) {
                  outputString.append(msgList[i]);
                  websocketServer.sendMessage(outputString.toString(), userId);
                  outputString = new StringBuffer();
                }
              }else {
                if (msgList.length > 1) {
                  for (int i = 0; i < msgList.length - 1; i++) {
                    outputString.append(msgList[i]);
                    websocketServer.sendMessage(outputString.toString(), userId);
                    outputString = new StringBuffer();
                  }
                  outputString = new StringBuffer(msgList[msgList.length - 1].trim());
                } else {
                  outputString.append(message.trim());
                }
              }
            }
          }
          while (error.available() > 0) {
            int n = in.read(buf);
            if (n > 0) {
              String message = new String(buf, StandardCharsets.UTF_8);
              boolean endline = message.endsWith("\n");
              String[] msgList = message.split("\n");
              if (endline) {
                for (int i = 0; i < msgList.length; i++) {
                  outputString.append(msgList[i]);
                  websocketServer.sendMessage(outputString.toString(), userId);
                  outputString = new StringBuffer();
                }
              }else {
                if (msgList.length > 1) {
                  for (int i = 0; i < msgList.length - 1; i++) {
                    outputString.append(msgList[i]);
                    websocketServer.sendMessage(outputString.toString(), userId);
                    outputString = new StringBuffer();
                  }
                  outputString = new StringBuffer(msgList[msgList.length - 1].trim());
                } else {
                  outputString.append(message.trim());
                }
              }
            }
          }
          // 进程未结束时调用exitValue将抛出异常
          exitValue = p.exitValue();
          finished = true;
        } catch (IllegalThreadStateException e) {
          Thread.sleep(1000);// 休眠1秒
          retry++;
        }
      }
    } catch (Exception e) {
      log.error("error: {}", e.getMessage(), e);
    }
    return exitValue;
  }


  // 数据推送接口
  @ResponseBody
  @RequestMapping("/push/{cid}")
  public Map<String, Object> pushToWeb(@PathVariable("cid") String cid, String message) {
    Map<String, Object> result = new HashMap<>();
    try {
      WebsocketServer.sendInfo(message, cid);
      result.put("code", cid);
      result.put("msg", message);
    } catch (Exception e) {
      log.error("error: {}", e.getMessage(), e);
    }
    return result;
  }
}
