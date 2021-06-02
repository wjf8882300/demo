package com.tongu.search.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.util.FileCopyUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Http上传下载操作
 * 
 * @author wangjf
 * @date 2019年7月2日 上午10:44:44
 */
@Slf4j
public class HttpUtil {

	private static final String CHARSET_UTF8 = "utf-8";

	private static CloseableHttpClient closeableHttpClient = getIgnoreSSLClient();

	/**
	 * 创建忽略SSL的HttpClient
	 * @return
	 */
	public static CloseableHttpClient getIgnoreSSLClient() {
		try {
			//创建httpClient
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();
			SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(false).setSoLinger(1).setSoReuseAddress(true).setSoTimeout(10000).setTcpNoDelay(true).build();
			CloseableHttpClient client = HttpClientBuilder.create()
					.setDefaultSocketConfig(socketConfig)
					.setSSLContext(sslContext)
					.setSSLHostnameVerifier(new NoopHostnameVerifier())
					.setRetryHandler(new DefaultHttpRequestRetryHandler(0, true))
					.build();
			return client;
		} catch (Exception e) {
			log.error("初始化CloseableHttpClient失败", e);
		}
		return  null;
	}
	
	/**
	 * 从网络Url中下载文件
	 * 
	 * @param url
	 * @param fileName
	 * @param savePath
	 * @throws IOException
	 */
	public static void downLoadFromUrl(String url, String fileName, String savePath){
		CloseableHttpResponse response = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).setConnectionRequestTimeout(5000).build();
			httpGet.setConfig(requestConfig);
			response = closeableHttpClient.execute(httpGet);
			HttpEntity httpEntity = response.getEntity();
			FileUtils.forceMkdir(new File(savePath));
			FileOutputStream fos = new FileOutputStream(savePath + File.separator + fileName);
			FileCopyUtils.copy(httpEntity.getContent(), fos);
		} catch (IOException e) {
			log.error("下载 {} 失败!", url, e);
		} finally {
			if(response != null) {
				try {
					response.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Get请求
	 * 注：5秒超时
	 * @param url
	 * @return
	 */
	public static String getUrl(String url){
		CloseableHttpResponse response = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).setConnectionRequestTimeout(5000).build();
			httpGet.setConfig(requestConfig);
			response = closeableHttpClient.execute(httpGet);
			return EntityUtils.toString(response.getEntity(), CHARSET_UTF8);
		} catch (IOException e) {
			log.error("Get {} 失败!", url, e);
		} finally {
			if(response != null) {
				try {
					response.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	/**
	 * 带参数Get请求
	 * @param url
	 * @param params
	 * @return
	 */
	public static String getUrl(String url, Map<String, Object> params) {
		if(MapUtils.isEmpty(params)) {
			return getUrl(url);
		}
		List<NameValuePair> paramList = params.entrySet().stream().map(p->new BasicNameValuePair(p.getKey(), p.getValue().toString())).collect(Collectors.toList());
		try {
			return getUrl(url + "?" + EntityUtils.toString(new UrlEncodedFormEntity(paramList, CHARSET_UTF8)));
		} catch (IOException e) {
			log.error("Get {} 失败!", url, e);
		}
		return null;
	}

	/**
	 * Post请求
	 * @param url
	 * @param requestParam
	 * @return
	 */
	public static String postUrl(String url, Map<String, Object> requestParam) {
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> params = requestParam.entrySet().stream().map(p->new BasicNameValuePair(p.getKey(), p.getValue().toString())).collect(Collectors.toList());
			httpPost.setEntity(new UrlEncodedFormEntity(params, Charset.forName(CHARSET_UTF8)));
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).setConnectionRequestTimeout(5000).build();
			httpPost.setConfig(requestConfig);
			response = closeableHttpClient.execute(httpPost);
			return EntityUtils.toString(response.getEntity(), CHARSET_UTF8);
		} catch (IOException e) {
			log.error("Post {} 失败!", url, e);
		} finally {
			if(response != null) {
				try {
					response.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	/**
	 * Post请求（json）
	 * @param url
	 * @param requestParam
	 * @return
	 */
	public static String postJson(String url, Map<String, Object> requestParam) {
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Content-type","application/json; charset=utf-8");
			httpPost.setHeader("Accept", "application/json");
			httpPost.setEntity(new StringEntity(JsonUtil.toJson(requestParam), Charset.forName(CHARSET_UTF8)));
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).setConnectionRequestTimeout(5000).build();
			httpPost.setConfig(requestConfig);
			response = closeableHttpClient.execute(httpPost);
			return EntityUtils.toString(response.getEntity(), CHARSET_UTF8);
		} catch (IOException e) {
			log.error("Post {} 失败!", url, e);
		} finally {
			if(response != null) {
				try {
					response.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	/**
	 * 判断url是否存在
	 * @param url
	 * @return
	 */
	public static boolean exists(String url) {
		//有些url不规范 '\' 需要转为 '/'
		if(url.contains("\\")){
			url = url.replaceAll("\\\\", "/");
		}
		CloseableHttpResponse response = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).setConnectionRequestTimeout(5000).build();
			httpGet.setConfig(requestConfig);
			response = closeableHttpClient.execute(httpGet);
			return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && response.getEntity().getContentLength() > 0;
		} catch (IOException e) {
			log.warn("Get {} 失败!", url, e);
		} finally {
			if(response != null) {
				try {
					response.close();
				} catch (Exception e) {
				}
			}
		}
		return false;
	}
}
