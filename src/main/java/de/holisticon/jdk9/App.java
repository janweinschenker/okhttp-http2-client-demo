package de.holisticon.jdk9;

import okhttp3.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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


  private void startRequest() throws InterruptedException {

    LOG.debug("======================================= Start synchronous request");
    OkHttpClient client = getUnsafeOkHttpClient();
    Request request = new Request.Builder()
        .url("https://localhost:8443/greeting?name=JavaLand") // The Http2Server should be running here.
        .build();
    long startTime = System.nanoTime();

    try {
      Response response = client.newCall(request).execute();
      long duration = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime);
      LOG.debug("Protocol version: " + response.protocol());
      LOG.debug("After " + duration + " seconds: " + response.body().string());

    } catch (IOException e) {
     LOG.error("IOException", e);
    }
  }



  private void startAsyncRequest() throws InterruptedException {
    LOG.debug("======================================= Start asynchronous request");
    OkHttpClient client = getUnsafeOkHttpClient();
    Request request = new Request.Builder()
        .url("https://localhost:8443/greeting?name=JavaLand") // The Http2Server should be running here.
        .build();
    long startTime = System.nanoTime();
    for (int i = 0; i < 3; i++) {
      Thread.sleep(1000); // http://stackoverflow.com/questions/32625035/when-using-http2-in-okhttp-why-multi-requests-to-the-same-host-didnt-use-just
      client.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
          e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
          long duration = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime);
          LOG.debug("Protocol version: " + response.protocol());
          LOG.debug("After " + duration + " seconds: " + response.body().string());

        }

      });
    }

  }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
