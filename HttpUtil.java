package com.finda.main.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {
	  private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);
	  
    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            
            //https服务支持
            trustAllHosts();
            HttpsURLConnection https = (HttpsURLConnection) realUrl.openConnection();
            if (realUrl.getProtocol().toLowerCase().equals("https")) {
                https.setHostnameVerifier(DO_NOT_VERIFY);
                connection = https;
            } else {
            	connection = (HttpURLConnection) realUrl.openConnection();
            }
            
            
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
            		"Mozilla/5.0 (compatible;Windows NT 6.1; WOW64;Trident/6.0;MSIE 9.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.27 Safari/537.36");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            // 建立实际的连接
            connection.connect();
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        	logger.error("+ ex.getMessage() + "+ e.getMessage());
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }    

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是json的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param, String charset) {
    	BufferedOutputStream out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            
            
            trustAllHosts();
            HttpsURLConnection https = (HttpsURLConnection) realUrl.openConnection();
            if (realUrl.getProtocol().toLowerCase().equals("https")) {
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            } else {
            	conn = (HttpURLConnection) realUrl.openConnection();
            }
            
            
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/json;charset=" + charset);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            // 获取URLConnection对象对应的输出流
            out = new BufferedOutputStream(conn.getOutputStream());
            // 发送请求参数
            //out.print(param);
            out.write(param.getBytes(charset));
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        	logger.error("+ ex.getMessage() + "+ e.getMessage());
            System.out.println("otoapi_sendPost发送 POST 请求出现异常！");
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
            	System.out.println("otoapi_sendPost-err：发送数据异常");
                //ex.printStackTrace();
            }
        }
        return result;
    }
    
    
    
    
	public static String doPost(String httpUrl, String param,String contType) {
	
	    HttpURLConnection connection = null;
	    InputStream is = null;
	    OutputStream os = null;
	    BufferedReader br = null;
	    String result = null;
	    try {
	        URL url = new URL(httpUrl);
	        // 通过远程url连接对象打开连接
	        connection = (HttpURLConnection) url.openConnection();
	        
	        trustAllHosts();
	        HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
	        if (url.getProtocol().toLowerCase().equals("https")) {
	            https.setHostnameVerifier(DO_NOT_VERIFY);
	            connection = https;
	        } else {
	        	connection = (HttpURLConnection) url.openConnection();
	        }
	        
	        
	        // 设置连接请求方式
	        connection.setRequestMethod("POST");
	        // 设置连接主机服务器超时时间：15000毫秒
	        connection.setConnectTimeout(15000);
	        // 设置读取主机服务器返回数据超时时间：60000毫秒
	        connection.setReadTimeout(60000);
	
	        // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
	        connection.setDoOutput(true);
	        // 默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
	        connection.setDoInput(true);
	        // 设置传入参数的格式:请求参数应该是 name1=value1&name2=value2 的形式。
	        connection.setRequestProperty("Content-Type", contType);
	        // 设置鉴权信息：Authorization: Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0
	        connection.setRequestProperty("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
	        connection.setRequestProperty("Charsert", "UTF-8"); //设置请求编码
	        // 通过连接对象获取一个输出流
	        os = connection.getOutputStream();
	        // 通过输出流对象将参数写出去/传输出去,它是通过字节数组写出的
	        os.write(param.getBytes());
	        // 通过连接对象获取一个输入流，向远程读取
	        if (connection.getResponseCode() == 200) {
	
	            is = connection.getInputStream();
	            // 对输入流对象进行包装:charset根据工作项目组的要求来设置
	            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	
	            StringBuffer sbf = new StringBuffer();
	            String temp = null;
	            // 循环遍历一行一行读取数据
	            while ((temp = br.readLine()) != null) {
	                sbf.append(temp);
	                sbf.append("\r\n");
	            }
	            result = sbf.toString();
	        }
	    } catch (MalformedURLException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        // 关闭资源
	        if (null != br) {
	            try {
	                br.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        if (null != os) {
	            try {
	                os.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        if (null != is) {
	            try {
	                is.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        // 断开与远程地址url的连接
	        connection.disconnect();
	    }
	    return result;
	}
 
	
	//============================================
	 final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
	        public boolean verify(String hostname, SSLSession session) {
	            return true;
	        }
	    };
	 
	    /**
	     * Trust every server - dont check for any certificate
	     */
	    private static void trustAllHosts() {
	        final String TAG = "trustAllHosts";
	        // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
	            @Override
	            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
	                //log.info(TAG + " checkClientTrusted");
	            }
	 
	            @Override
	            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
	                //log.info(TAG + " checkServerTrusted");
	            }
	 
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return new java.security.cert.X509Certificate[]{};
	            }
	        }};
	        // Install the all-trusting trust manager
	        try {
	            SSLContext sc = SSLContext.getInstance("TLS");
	            sc.init(null, trustAllCerts, new java.security.SecureRandom());
	            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
    
}
/********************************************** 请求示例************************************************
//发送 GET 请求
String s=HttpRequest.sendGet("http://localhost:6144/Home/RequestString", "key=123&v=456");
System.out.println(s);

//发送 POST 请求
String sr=HttpRequest.sendPost("http://localhost:6144/Home/RequestPostString", "key=123&v=456");
System.out.println(sr);
******************************************************************************************************/
