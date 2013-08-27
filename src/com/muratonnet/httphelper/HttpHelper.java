package com.muratonnet.httphelper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.text.TextUtils;

public class HttpHelper {

	public final static int SHORT_CONNECTION_TIMEOUT = 8 * 1000;
	public final static int LONG_CONNECTION_TIMEOUT = 30 * 1000;

	private final static int CONNECTION_TIMEOUT_IN_MILISECONDS = LONG_CONNECTION_TIMEOUT;
	private final static int SOCKET_TIMEOUT_IN_MILISECONDS = CONNECTION_TIMEOUT_IN_MILISECONDS;

	private String _url;
	private HttpClient _httpClient;

	/**
	 * Constructor of HttpHelper (set connectionTimeout = 30 and socketTimeout =
	 * 30 as a default)
	 * 
	 * @param url
	 *            Url to execute
	 */
	public HttpHelper(String url) {
		this(url, CONNECTION_TIMEOUT_IN_MILISECONDS,
				SOCKET_TIMEOUT_IN_MILISECONDS);
	}

	/**
	 * Constructor of HttpHelper
	 * 
	 * @param url
	 *            Url to execute
	 * @param connectionTimeout
	 *            The timeout until a connection is established. A value of zero
	 *            means the timeout is not used.
	 * @param socketTimeout
	 *            The timeout for waiting for data. A timeout value of zero is
	 *            interpreted as an infinite timeout.
	 */
	public HttpHelper(String url, int connectionTimeout, int socketTimeout) {
		// set url
		_url = url;

		// set http client object
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
		HttpConnectionParams.setSoTimeout(params, socketTimeout);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		_httpClient = new DefaultHttpClient(params);

	}

	/**
	 * Shuts down connection manager that created by HttpHelper instance and
	 * releases allocated resources. This includes closing all connections,
	 * whether they are currently used or not.
	 */
	public void closeConnection() {
		if (_httpClient != null) {
			_httpClient.getConnectionManager().shutdown();
			_httpClient = null;
		}
	}

	/**
	 * Executes a http get request.
	 * 
	 * @return The response to the request
	 */
	public HttpResponseObject get() {

		HttpGet httpGet = new HttpGet();
		HttpResponseObject responseObject = execute(httpGet);

		return responseObject;
	}

	/**
	 * Executes a http post request.
	 * 
	 * @return The response to the request
	 */
	public HttpResponseObject post() {
		return post("");
	}

	/**
	 * Executes a http post request with json data
	 * 
	 * @param jsonData
	 *            The json data to send
	 * @return The response to the request
	 */
	public HttpResponseObject post(String jsonData) {

		HttpResponseObject responseObject = new HttpResponseObject();

		try {
			HttpPost httpPost = new HttpPost();
			if (!TextUtils.isEmpty(jsonData)) {
				addJsonHeaders(httpPost);
				StringEntity entity = new StringEntity(jsonData);
				httpPost.setEntity(entity);
			}
			responseObject = execute(httpPost);
		} catch (UnsupportedEncodingException e) {
			responseObject.Ex = e;
		}

		return responseObject;

	}

	/**
	 * Executes a http put request.
	 * 
	 * @return The response to the request
	 */
	public HttpResponseObject put() {
		return put("");
	}

	/**
	 * Executes a http put request with json data
	 * 
	 * @param jsonData
	 *            The json data to send
	 * @return The response to the request
	 */
	public HttpResponseObject put(String jsonData) {
		HttpResponseObject responseObject = new HttpResponseObject();

		try {
			HttpPut httpPut = new HttpPut();
			if (!TextUtils.isEmpty(jsonData)) {
				addJsonHeaders(httpPut);
				StringEntity entity = new StringEntity(jsonData);
				httpPut.setEntity(entity);
			}
			responseObject = execute(httpPut);
		} catch (UnsupportedEncodingException e) {
			responseObject.Ex = e;
		}

		return responseObject;
	}

	/**
	 * Executes a http delete request.
	 * 
	 * @return The response to the request
	 */
	public HttpResponseObject delete() {
		HttpDelete httpDelete = new HttpDelete();
		HttpResponseObject responseObject = execute(httpDelete);

		return responseObject;
	}

	/**
	 * Executes a http request.
	 * 
	 * @param request
	 *            Http request to execute
	 * @return The response to the request
	 */
	private HttpResponseObject execute(HttpRequestBase request) {

		// create new HttpResponseObject for return value
		HttpResponseObject responseObject = new HttpResponseObject();

		// check url
		if (!TextUtils.isEmpty(_url)) {
			// create HttpResponse object
			HttpResponse response = null;
			try {
				// refresh request uri
				request.setURI(new URI(_url));

				// execute request
				response = _httpClient.execute(request);
				// check response
				if (response != null) {
					// get response entity
					HttpEntity responseEntity = response.getEntity();
					// check response entitiy
					if (responseEntity != null) {
						// set HttpResponseObject.Data
						responseObject.Data = responseEntity.getContent();
					}
					// set HttpResponseObject.StatusCode and
					// HttpResponseObject.StatusText
					responseObject.StatusCode = response.getStatusLine()
							.getStatusCode();
					responseObject.StatusText = response.getStatusLine()
							.getReasonPhrase();
				}
			} catch (URISyntaxException e) {
				// some information could not be parsed while creating a URI.
				e.printStackTrace();
				responseObject.Ex = e;
			} catch (ClientProtocolException e) {
				// error in the HTTP protocol.
				e.printStackTrace();
				responseObject.Ex = e;
			} catch (HttpHostConnectException e) {
				// a connection cannot be established to a remote host on a
				// specific port.
				e.printStackTrace();
				responseObject.Ex = e;
			} catch (ConnectTimeoutException e) {
				// a timeout while connecting to an HTTP server or waiting for
				// an available connection from an HttpConnectionManager.
				e.printStackTrace();
				responseObject.Ex = e;
			} catch (SocketTimeoutException e) {
				// when a timeout expired on a socket read or accept operation
				e.printStackTrace();
				responseObject.Ex = e;
			} catch (SocketException e) {
				// an error when during socket creation or setting options
				e.printStackTrace();
				responseObject.Ex = e;
			} catch (IOException e) {
				// the target server failed to respond with a valid HTTP
				// response.
				e.printStackTrace();
				responseObject.Ex = e;
			} catch (Exception e) {
				// general error
				e.printStackTrace();
				responseObject.Ex = e;
			}

		}

		return responseObject;
	}

	/**
	 * Add json header to the http request
	 * 
	 * @param request
	 *            Http request to add json headers
	 */
	private void addJsonHeaders(HttpRequestBase request) {
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");
	}

}
