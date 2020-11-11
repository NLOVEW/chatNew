package com.nhb.chat.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhb.chat.bean.IpCity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取客户端真实终端IP
 *
 * @author luck_nhb
 */
public class IPUtil {
    private static Logger logger = LoggerFactory.getLogger(IPUtil.class);

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址。
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    logger.error("获取网卡IP失败:{}", e.getMessage());
                }
                ip = inet.getHostAddress();
            }
        }
        return ip;
    }


    public static IpCity parseCityByIP(String ip) {
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request().newBuilder().header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                    .build();
            return chain.proceed(request);
        }).build();
        String ipCityRequestUrl = "https://restapi.amap.com/v3/ip?ip=replaceIP&key=d5e39bc59de64d5b7d699bfd744d809e".replace("replaceIP", ip);
        Request request = new Request.Builder().url(ipCityRequestUrl).get().build();
        try {
            Response response = httpClient.newCall(request).execute();
            ObjectMapper objectMapper = new ObjectMapper();
            IpCity ipCity = objectMapper.readValue(response.body().string(),IpCity.class);
            return ipCity;
        } catch (IOException e) {
            logger.error("request:{} happen error,the because of :{}", ipCityRequestUrl, e.getMessage());
        }
        return null;
    }

    @Test
    public void test(){
        IpCity ipCity = parseCityByIP("121.69.42.10");
        System.out.println(ipCity.getCity());
    }
}