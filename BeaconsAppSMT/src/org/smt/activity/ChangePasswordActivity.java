package org.smt.activity;

import org.smt.R;
import org.smt.tasks.ChangeUserPassworkTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/**
 * Activity for changing password
 * @author Azam-smt
 *
 */
public class ChangePasswordActivity extends Activity {

	private Button btnChangePassword;
	private EditText edtPasswordNew1;
	private EditText edtPasswordNew2;
	private EditText edtPasswrorOld;
	private TextView txtViewError;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		edtPasswordNew1=(EditText) findViewById(R.id.editTxtNewPassword);
		edtPasswordNew2=(EditText) findViewById(R.id.editTxtVerifyPassword);
		edtPasswrorOld= (EditText) findViewById(R.id.editTxtPasswordActual); 
		btnChangePassword=(Button) findViewById(R.id.btnChangePassword);
		txtViewError=(TextView) findViewById(R.id.txtViewShowStatusPassword);

		btnChangePassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(txtViewError!=null){
					txtViewError.setText("");
					txtViewError.setVisibility(View.GONE);
				}
				if(isCambiarPassword()){
					new ChangeUserPassworkTask(ChangePasswordActivity.this, edtPasswrorOld.getText().toString(),edtPasswordNew1.getText().toString()).execute();
				}
			}
		});
	}
	
	public void completePasswordChange() {

		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChangePasswordActivity.this);
		
		// set title
		alertDialogBuilder.setTitle("Cambio de cotraseña");
		
		
		// set dialog message
		alertDialogBuilder
			.setMessage("Se ha cambiado la contraseña con exito!!")
			.setCancelable(true)
			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					ChangePasswordActivity.this.finish();
				}
			  });

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
	

	}
	private boolean isCambiarPassword(){
		
		boolean isPosible=false;
		
		if(edtPasswrorOld==null&&edtPasswrorOld.getText().toString().isEmpty()){
			txtViewError.setVisibility(View.VISIBLE);
			txtViewError.setText("Campo de contraseña esta vacio");
		}else if(!edtPasswordNew1.getText().toString().equals(edtPasswordNew2.getText().toString())){
			txtViewError.setVisibility(View.VISIBLE);
			txtViewError.setText("Las nuevas contraseñas no se coinciden");
		}else{
			isPosible=true;
		}
		return isPosible;
	}
}
