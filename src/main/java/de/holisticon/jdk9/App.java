package de.holisticon.jdk9;

import okhttp3.*;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Header;
import okhttp3.internal.http2.Http2Connection;
import okhttp3.internal.http2.PushObserver;
import okio.BufferedSource;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.lang.System;

import static org.eclipse.jetty.util.ssl.SslContextFactory.TRUST_ALL_CERTS;

/**
 * Hello world!
 */
public class App {
    // In order to run this, you need the alpn-boot-XXX.jar in the bootstrap classpath.
    private static final Logger LOG = Logger.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        OkHttpClient client = getUnsafeOkHttpClient();
        Request request = new Request.Builder()
                .url("https://nghttp2.org:443") // The Http2Server should be running here.
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
                    System.out.println("After " + duration + " seconds: " + response.body().string());

                }

            });
        }
    }

    // http://stackoverflow.com/questions/25509296/trusting-all-certificates-with-okhttp
    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, TRUST_ALL_CERTS, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
