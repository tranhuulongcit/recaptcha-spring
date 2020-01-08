package info.cafeit.recaptchaspring.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.*;
import okhttp3.HttpUrl.Builder;

public final class CafeitNetwoking {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private Callback callback = null;
    private OkHttpClient client = null;
    private Request request = null;

    private CafeitNetwoking(){}

    public static class Get {
        private Map<String, String> headerParams;
        private Builder parameterBuider;
        private Map<String, String> parameters;
        private Request request;
        private String url;


        public static Get create() {
            return new Get();
        }

        private Get() {
            this.parameters = null;
            this.headerParams = null;
            this.parameters = new HashMap<>();
            this.headerParams = new HashMap();
        }

        public Get setParams(String key, String value) {
            this.parameters.put(key, value);
            return this;
        }

        public Get setHeaders(String key, String value) {
            this.headerParams.put(key, value);
            return this;
        }

        public Get setUrl(String url2) {
            this.url = url2;
            return this;
        }

        public CafeitNetwoking build() {
            this.parameterBuider = HttpUrl.parse(this.url).newBuilder();
            Headers.Builder headerbuild = new Headers.Builder();
            if (this.parameters != null) {
                for (Map.Entry<String, String> param : this.parameters.entrySet()) {
                    this.parameterBuider.addQueryParameter((String) param.getKey(), (String) param.getValue());
                }
            }
            if (this.headerParams != null) {
                for (Map.Entry<String, String> param2 : this.headerParams.entrySet()) {
                    headerbuild.add((String) param2.getKey(), (String) param2.getValue());
                }
            }
            this.request = new Request.Builder().url(this.parameterBuider.build()).headers(headerbuild.build()).build();
            return new CafeitNetwoking(this.request);
        }
    }

    public static class Post {
        private String bodyJson;
        private Map<String, String> headerParams;
        private Map<String, String> queryParameter;
        private Request request;
        private String url;

        public static Post create() {
            return new Post();
        }

        private Post() {
            this.queryParameter = null;
            this.headerParams = null;
            this.bodyJson = null;
            this.request = null;
            this.queryParameter = new HashMap();
            this.headerParams = new HashMap();
        }

        public Post setParams(String key, String value) {
            this.queryParameter.put(key, value);
            return this;
        }

        public Post setHeaders(String key, String value) {
            this.headerParams.put(key, value);
            return this;
        }

        public Post setUrl(String url2) {
            this.url = url2;
            return this;
        }

        public Post setBody(String bodyJson2) {
            this.bodyJson = bodyJson2;
            return this;
        }

        public CafeitNetwoking build() {
            RequestBody body;
            Headers.Builder headerbuild = new Headers.Builder();
            if (this.headerParams != null) {
                for (Map.Entry<String, String> param : this.headerParams.entrySet()) {
                    headerbuild.add((String) param.getKey(), (String) param.getValue());
                }
            }
            if (this.bodyJson != null) {
                body = RequestBody.create(CafeitNetwoking.JSON, this.bodyJson);
            } else {
                FormBody.Builder bodyBuilder = new FormBody.Builder(Charset.defaultCharset());
                if (this.queryParameter != null) {
                    for (Map.Entry<String, String> param2 : this.queryParameter.entrySet()) {
                        bodyBuilder.addEncoded((String) param2.getKey(), (String) param2.getValue());
                    }
                }
                body = bodyBuilder.build();
            }
            this.request = new Request.Builder().url(this.url).post(body).headers(headerbuild.build()).build();
            return new CafeitNetwoking(this.request);
        }
    }

    private CafeitNetwoking(Request request2) {
        this.request = request2;
    }

    public CafeitNetwoking setCallback(Callback callback2) {
        this.callback = callback2;
        return this;
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            TrustManager[] trustAllCerts = {new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder.connectTimeout(60, TimeUnit.SECONDS).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void executeAsync() throws IOException, InterruptedException {
        if (this.client == null) {
            this.client = getUnsafeOkHttpClient();
        }
        if (this.request == null) {
            throw new InterruptedException("request is null, please create instance ");
        } else if (this.callback != null) {
            this.client.newCall(this.request).enqueue(this.callback);
        } else {
            throw new InterruptedException("callback is null, please setCallBack() before execute");
        }
    }

    public Response execute() throws IOException, InterruptedException {
        if (this.client == null) {
            this.client = getUnsafeOkHttpClient();
        }
        if (this.request == null) {
            throw new InterruptedException("request is null, please create instance.");
        }
        return this.client.newCall(this.request).execute();
    }

}