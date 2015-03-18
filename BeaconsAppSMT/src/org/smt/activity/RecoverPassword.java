package org.smt.activity;

import org.smt.R;
import org.smt.tasks.RecoverPasswordTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RecoverPassword extends Activity{
	private Button btnRecoverPassword;
	private EditText edTextEmail;
	private TextView txtViewErrorMessage;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recover_password);
		btnRecoverPassword = (Button) findViewById(R.id.btnRecoverPassword);
		edTextEmail=(EditText) findViewById(R.id.edBoxEmail);
		txtViewErrorMessage=(TextView) findViewById(R.id.textEmailError);
		btnRecoverPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(edTextEmail.getText().equals("")){
					showErrorMessage("Debes escribir un email");
				}else{
					if(txtViewErrorMessage!=null){
						txtViewErrorMessage.setText("");
						txtViewErrorMessage.setVisibility(View.GONE);
						
					}
					new RecoverPasswordTask(RecoverPassword.this, edTextEmail.getText().toString()).execute();
				}
				
			}
		});
	}
	
	public void showErrorMessage(String message){
		if(txtViewErrorMessage!=null){
			txtViewErrorMessage.setText(message);
			txtViewErrorMessage.setVisibility(View.VISIBLE);
			
		}
		
	}
	

}
