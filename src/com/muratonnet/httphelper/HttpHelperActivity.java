package com.muratonnet.httphelper;

import java.io.BufferedReader;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HttpHelperActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		getSample();
	}

	private void getSample() {
		TextView statusView = (TextView) findViewById(R.id.status);
		TextView dataView = (TextView) findViewById(R.id.data);

		String statusText = "";
		String data = "";

		// create http helper instance
		HttpHelper httpHelper = new HttpHelper("http://www.google.com");
		// execute get method
		HttpResponseObject response = httpHelper.get();

		// check exception in response
		if (response.Ex == null) {
			// get status message
			statusText = response.StatusCode + " - " + response.StatusText;
			// get data
			BufferedReader reader = new BufferedReader(response.getDataReader());
			StringBuilder dataBuilder = new StringBuilder();
			String dataLine;
			try {
				while ((dataLine = reader.readLine()) != null) {
					dataBuilder.append(dataLine);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			data = dataBuilder.toString();
		} else {
			// get error message
			statusText = "Error" + " : " + response.Ex.toString();
		}
		// close connection
		httpHelper.closeConnection();

		// show status message
		statusView.setText(statusText);
		// show data
		dataView.setText(data);

	}
}