package de.holisticon.jdk9;

import okhttp3.*;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
  // In order to run this, you need the alpn-boot-XXX.jar in the bootstrap classpath.
  private static final Logger LOG = Logger.getLogger(App.class);

  public static void main(String[] args) throws Exception {
    App app = new App();

    app.startRequest();
    app.startAsyncRequest();

    System.exit(0);
  }


  /**
   * This method will issue a synchronous GET request against the http server.
   * @throws InterruptedException
   */
  private void startRequest() throws InterruptedException {

    LOG.debug("======================================= Start synchronous request");
    OkHttpClient client = getOkHttpClient();
    Request request = new Request.Builder()
        .url("https://localhost:8443/greeting?name=JavaLand") // The Http2Server should be running here.
        .build();

    try {
      Response response = client.newCall(request).execute();
      LOG.debug("Protocol version: " + response.protocol());
      LOG.debug("After seconds: " + response.body().string());

    } catch (IOException e) {
      LOG.error("IOException", e);
    }
  }


  private void startAsyncRequest() throws InterruptedException {
    LOG.debug("======================================= Start asynchronous request");
    OkHttpClient client = getOkHttpClient();

    Request request = new Request.Builder()
        .url("https://localhost:8443/greeting?name=JavaLand") // The Http2Server should be running here.
        .build();

    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        LOG.error("IOException", e);
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        LOG.debug("Protocol version: " + response.protocol());
        LOG.debug("content: " + response.body().string());
      }
    });

    // watch the console log: the following message will be printed before the request has finished.
    LOG.debug("request created!!!");

    // wait a couple of seconds until the client thread has finished.
    Thread.sleep(5000);
  }

  private OkHttpClient getOkHttpClient() {
    try {
      OkHttpClient okHttpClient = new OkHttpClient.Builder()
          .build();
      return okHttpClient;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
