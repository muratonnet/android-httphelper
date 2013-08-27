package com.muratonnet.httphelper;

import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpResponseObject{

	public int StatusCode ;
	public String StatusText ;
	public InputStream Data;
	public Exception Ex;	
	
	public InputStreamReader getDataReader()
	{
		return new InputStreamReader(Data);
	}

}