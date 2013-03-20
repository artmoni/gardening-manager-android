package org.gots.ui;

import org.gots.R;
import org.gots.preferences.GotsPreferences;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class LoginActivity extends SherlockActivity {
	private TextView loginText;
	private TextView passwordText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loginText = (TextView) findViewById(R.id.edittextLogin);
		loginText.setText(GotsPreferences.getInstance(this).getNUXEO_LOGIN());
		passwordText = (TextView) findViewById(R.id.edittextPassword);
		passwordText.setText(GotsPreferences.getInstance(this).getNUXEO_PASSWORD());
		
		Button connect = (Button)findViewById(R.id.buttonConnect);
		connect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GotsPreferences.getInstance(LoginActivity.this).setNUXEO_LOGIN(loginText.getText().toString());
				GotsPreferences.getInstance(LoginActivity.this).setNUXEO_PASSWORD(passwordText.getText().toString());	
				GotsPreferences.getInstance(LoginActivity.this).setConnectedToServer(true); 
				finish();
			}
		});

		
	}
}
