package org.smt.activity;

/**
 * Activity for registring user
 */
import java.util.Date;

import org.smt.R;
import org.smt.model.UserDTO;
import org.smt.tasks.RegisterUserTask;
import org.smt.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class RegisterUserActivity extends Activity {
	private boolean gender;
	public TextView txtShowErrorMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_usuario);
		txtShowErrorMessage = (TextView) findViewById(R.id.showMessageErrorRegister);
		
	}

	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radFemale:
	            if (checked)
	            	((RadioButton)  findViewById(R.id.radMale)).setChecked(false);
	            	gender=false;
	            break;
	        case R.id.radMale:
	            if (checked)
	            	((RadioButton)  findViewById(R.id.radFemale)).setChecked(false);
	            	gender=true;
	            break;
	    }
	}
	
public void guardarNuevoUsuario(View view){
	txtShowErrorMessage.setText("");
	txtShowErrorMessage.setVisibility(View.GONE);
	UserDTO nuevoUsuario=new UserDTO();
	String codigoPostal=((EditText) findViewById(R.id.reg_cp)).getText().toString();
	String fechaNacimiento=((EditText) findViewById(R.id.reg_fnac)).getText().toString();
	nuevoUsuario.setNickName(((EditText) findViewById(R.id.reg_nick_name)).getText().toString());
	nuevoUsuario.setEmail(((EditText) findViewById(R.id.reg_email)).getText().toString());
	nuevoUsuario.setPassword(((EditText) findViewById(R.id.reg_password)).getText().toString());
	String verifyPassword2=((EditText) findViewById(R.id.reg_password2)).getText().toString();
	if(gender){
		nuevoUsuario.setSex(1);
	}else{
		nuevoUsuario.setSex(2);
	}
	
	if(codigoPostal.isEmpty()){
		nuevoUsuario.setCP(0);	
	}else{
		nuevoUsuario.setCP(Integer.parseInt(codigoPostal));	
	}

	if(comprobarCampos(nuevoUsuario,verifyPassword2,fechaNacimiento)){
		nuevoUsuario.setBirthDate(new Date(fechaNacimiento));
		new RegisterUserTask(RegisterUserActivity.this,nuevoUsuario).execute();
	}
	
	
}
public void toLogin() {

	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterUserActivity.this);

		// set title
		alertDialogBuilder.setTitle("Registrado Usuario");

		// set dialog message
		alertDialogBuilder
			.setMessage("Se ha enviado un email a su cuenta. Para hacer login es necesario que lo valides")
			.setCancelable(false)
			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					Intent i = new Intent(RegisterUserActivity.this, LoginActivity.class);
					startActivity(i);
					finish();
				}
			  });

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
	


}
private Boolean comprobarCampos(UserDTO usuarioNuevo,String password2,String fechaNacimiento){
	
	if(usuarioNuevo.getNickName().isEmpty()){
		if(txtShowErrorMessage!=null){
			txtShowErrorMessage.setVisibility(View.VISIBLE);
			txtShowErrorMessage.setText("Debes escribir tu nombre");
		}
		return false;
	}else if(usuarioNuevo.getEmail().isEmpty()){
		if(txtShowErrorMessage!=null){
			txtShowErrorMessage.setVisibility(View.VISIBLE);
			txtShowErrorMessage.setText("Debes escribir un email");
		}
		return false;
	}else if(usuarioNuevo.getPassword().isEmpty()){
		if(txtShowErrorMessage!=null){
			txtShowErrorMessage.setVisibility(View.VISIBLE);
			txtShowErrorMessage.setText("Debes escribir una contraseña");
		}
		return false;
	}else if(!Utils.isformatoFechaCorrecto(fechaNacimiento)){
		if(txtShowErrorMessage!=null){
			txtShowErrorMessage.setVisibility(View.VISIBLE);
			txtShowErrorMessage.setText("Formato de la fecha no esta correcto");
		}
		return false;
	}else if(!Utils.isPasswordSame(usuarioNuevo.getPassword(), password2)){
		if(txtShowErrorMessage!=null){
			txtShowErrorMessage.setVisibility(View.VISIBLE);
			txtShowErrorMessage.setText("los dos contraseña no coinciden");
		}
		return false;
	}else{
		return true;
	}
	
	
}
}
