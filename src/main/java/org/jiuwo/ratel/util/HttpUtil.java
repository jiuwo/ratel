package org.jiuwo.ratel.util;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jiuwo.ratel.exception.RatelException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Steven Han
 */
@Slf4j
public class HttpUtil {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String HTTPS = "https";

    public static OkHttpClient getHttpClient(String url) {
        if (url.startsWith(HTTPS)) {
            return okHttpClientSSL();
        }
        return okHttpClient();
    }

    public static String get(String url) {
        Response response = null;
        try {
            Request request = new Request.Builder().url(url).build();
            response = getHttpClient(url).newCall(request).execute();
            return response.body().string();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeResponse(response);
        }
    }

    private static void closeResponse(Response response) {
        if (response != null) {
            response.close();
        }
    }

    public static String postJson(String url, String json) {
        Response response = null;
        try {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            response = getHttpClient(url).newCall(request).execute();
            return response.body().string();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeResponse(response);
        }
    }

    public static String postMap(String url, Map<String, String> params) {
        Response response = null;
        try {
            MultipartBody.Builder urlBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            if (params != null) {
                params.forEach((k, v) -> {
                    if (v != null) {
                        urlBuilder.addFormDataPart(k, v);
                    }
                });
            }

            Request request = new Request.Builder()
                    .url(url)
                    .post(urlBuilder.build())
                    .build();
            response = getHttpClient(url).newCall(request).execute();
            return response.body().string();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeResponse(response);
        }
    }

    public static OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        return builder.build();
    }

    public static OkHttpClient okHttpClientSSL() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .sslSocketFactory(createSSLSocketFactory(), new TrustAllManager())
                .hostnameVerifier(new TrustAllHostnameVerifier());
        return builder.build();
    }


    /**
     * 默认信任所有的证书
     *
     * @return SSLSocketFactory
     */
    private static SSLSocketFactory createSSLSocketFactory() {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
            return sc.getSocketFactory();
        } catch (Exception e) {
            throw new RatelException(e);
        }
    }

    static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}


