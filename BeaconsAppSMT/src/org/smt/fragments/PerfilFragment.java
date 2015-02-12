package org.smt.fragments;


import java.util.Date;

import org.smt.R;
import org.smt.activity.ChangePasswordActivity;
import org.smt.model.UserDTO;
import org.smt.tasks.ModificarPerfilTask;
import org.smt.tasks.RegisterUserTask;
import org.smt.utils.Utils;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class PerfilFragment extends Fragment {

	public PerfilFragment() {
		
	}
	private EditText editTextNombre;
	private EditText editTextPassword;
	private EditText editTextEmail;
	private EditText editTextCp;
	private EditText editTextSexo;
	private EditText editTextfechaNacimiento;
	private Button btnCancelarEditPerfil;
	private Button btnActivarEditPerfil;
	private Button btnGuardarEditPerfil;
	private TextView showPerfilError;
	private RadioButton radMale;
	private RadioButton radFemale;
	private boolean gender;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View rootView = inflater.inflate(R.layout.fragment_perfil, container, false);
		setEditTextNombre((EditText) rootView.findViewById(R.id.editNombre));
		setEditTextPassword((EditText) rootView.findViewById(R.id.editPassword));
		setEditTextEmail((EditText) rootView.findViewById(R.id.editEmail));
		setEditTextCp((EditText) rootView.findViewById(R.id.editCp));
		setShowPerfilError((TextView) rootView.findViewById(R.id.txtViewPerfilShowStatus));
		setEditTextfechaNacimiento( (EditText) rootView.findViewById(R.id.reg_fnac));
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String user = settings.getString("email", "");
	
		if (!user.isEmpty()) {
			editTextNombre.setText(settings.getString("nickName", ""));
			editTextEmail.setText(settings.getString("email", ""));
			editTextCp.setText(settings.getString("CP", ""));
			radMale=(RadioButton)  rootView.findViewById(R.id.radMale);
			radFemale=(RadioButton)  rootView.findViewById(R.id.radFemale);
			if(settings.getInt("Sex", 1)==1){
				((RadioButton)  rootView.findViewById(R.id.radMale)).setChecked(true);
				((RadioButton)  rootView.findViewById(R.id.radFemale)).setChecked(false);
				gender=true;
			}else if(settings.getInt("Sex", 1)==2){
				((RadioButton)  rootView.findViewById(R.id.radFemale)).setChecked(true);
				((RadioButton)  rootView.findViewById(R.id.radMale)).setChecked(false);
				gender=false;
			}
			editTextfechaNacimiento.setText(settings.getString("fechaNacimiento", ""));
		}
		
		View.OnClickListener myOnlyhandler = new View.OnClickListener() {
			  public void onClick(View v) {
			      switch(v.getId()) {
			        case R.id.btnEditPerfil://Para activar los botones 
			        	activarModiPerfil(true);
			          break;
			        case R.id.btnGuardar:
			        	guardarModifiPerfil();
			          break;
			        case R.id.btnCancelar:
			        	activarModiPerfil(false);
				       break;
			        case R.id.radFemale:
			            	((RadioButton)  rootView.findViewById(R.id.radMale)).setChecked(false);
			            	gender=false;
			            break;
			        case R.id.radMale:
			            	((RadioButton)  rootView.findViewById(R.id.radFemale)).setChecked(false);
			            	gender=true;
			            break;
			        case R.id.editPassword:
			        	Intent i = new Intent(getActivity(), ChangePasswordActivity.class);
						startActivity(i);
				       break;
			      }
			  }
			};
			
		 radMale.setOnClickListener(myOnlyhandler);
		 radFemale.setOnClickListener(myOnlyhandler);
		 editTextPassword.setOnClickListener(myOnlyhandler);
		 btnActivarEditPerfil=(Button) rootView.findViewById(R.id.btnEditPerfil);
		 btnActivarEditPerfil.setOnClickListener(myOnlyhandler);
		 btnCancelarEditPerfil=(Button) rootView.findViewById(R.id.btnCancelar);
		 btnCancelarEditPerfil.setOnClickListener(myOnlyhandler);
		 btnGuardarEditPerfil=(Button) rootView.findViewById(R.id.btnGuardar);
		 btnGuardarEditPerfil.setOnClickListener(myOnlyhandler);
		return rootView;
	}
	
	public void activarModiPerfil(Boolean isActivar){
		editTextNombre.setEnabled(isActivar);
		editTextPassword.setEnabled(isActivar);
		editTextEmail.setEnabled(isActivar);
		editTextCp.setEnabled(isActivar);
		radFemale.setEnabled(isActivar);
		radMale.setEnabled(isActivar);
		if(isActivar){
			btnActivarEditPerfil.setVisibility(8);
			btnCancelarEditPerfil.setVisibility(0);
			btnGuardarEditPerfil.setVisibility(0);
		}else{
			btnActivarEditPerfil.setVisibility(0);
			btnCancelarEditPerfil.setVisibility(8);
			btnGuardarEditPerfil.setVisibility(8);
		}
		btnCancelarEditPerfil.setEnabled(isActivar);
		
		
	}

	@SuppressWarnings("deprecation")
	private void guardarModifiPerfil(){
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
		UserDTO datosUserAEditar=new UserDTO();
		datosUserAEditar.setCP(Integer.parseInt(this.getEditTextCp().getText().toString()));
		datosUserAEditar.setDeviceToken("");
		datosUserAEditar.setEmail(this.getEditTextEmail().getText().toString());
		datosUserAEditar.setNickName(this.getEditTextNombre().getText().toString());
		
		if(gender){
			datosUserAEditar.setSex(1);
		}
		else{
			datosUserAEditar.setSex(2);
		}
		datosUserAEditar.setToken(settings.getString("userToken", ""));
		datosUserAEditar.setUserId(settings.getLong("UserId", 0));
		if(comprobarCampos(datosUserAEditar,this.getEditTextfechaNacimiento().getText().toString())){
			if(showPerfilError!=null){
				showPerfilError.setVisibility(View.GONE);
				showPerfilError.setText("");
			}
			datosUserAEditar.setBirthDate(new Date(this.getEditTextfechaNacimiento().getText().toString()));
			new ModificarPerfilTask(this, datosUserAEditar).execute();
		}
	}
	
	private Boolean comprobarCampos(UserDTO usuarioNuevo,String fechaNacimiento){
		
		if(usuarioNuevo.getNickName().isEmpty()){
			if(showPerfilError!=null){
				showPerfilError.setVisibility(View.VISIBLE);
				showPerfilError.setText("Debes escribir tu nombre");
			}
			return false;
		}else if(usuarioNuevo.getEmail().isEmpty()){
			if(showPerfilError!=null){
				showPerfilError.setVisibility(View.VISIBLE);
				showPerfilError.setText("Debes escribir un email");
			}
			return false;
		}else if(!Utils.isformatoFechaCorrecto(fechaNacimiento)){
			if(showPerfilError!=null){
				showPerfilError.setVisibility(View.VISIBLE);
				showPerfilError.setText("Formato de la fecha no esta correcto");
			}
			return false;
		}else{
			return true;
		}
		
		
	}
	public EditText getEditTextNombre() {
		return editTextNombre;
	}

	public void setEditTextNombre(EditText editTextNombre) {
		this.editTextNombre = editTextNombre;
	}

	public EditText getEditTextPassword() {
		return editTextPassword;
	}

	public void setEditTextPassword(EditText editTextPassword) {
		this.editTextPassword = editTextPassword;
	}

	public EditText getEditTextEmail() {
		return editTextEmail;
	}

	public void setEditTextEmail(EditText editTextEmail) {
		this.editTextEmail = editTextEmail;
	}

	public EditText getEditTextCp() {
		return editTextCp;
	}

	public void setEditTextCp(EditText editTextCp) {
		this.editTextCp = editTextCp;
	}

	public EditText getEditTextSexo() {
		return editTextSexo;
	}

	public void setEditTextSexo(EditText editTextSexo) {
		this.editTextSexo = editTextSexo;
	}

	public Button getBtnCancelarEditPerfil() {
		return btnCancelarEditPerfil;
	}

	public void setBtnCancelarEditPerfil(Button btnCancelarEditPerfil) {
		this.btnCancelarEditPerfil = btnCancelarEditPerfil;
	}

	public Button getBtnActivarEditPerfil() {
		return btnActivarEditPerfil;
	}

	public void setBtnActivarEditPerfil(Button btnActivarEditPerfil) {
		this.btnActivarEditPerfil = btnActivarEditPerfil;
	}

	public Button getBtnGuardarEditPerfil() {
		return btnGuardarEditPerfil;
	}

	public void setBtnGuardarEditPerfil(Button btnGuardarEditPerfil) {
		this.btnGuardarEditPerfil = btnGuardarEditPerfil;
	}

	public EditText getEditTextfechaNacimiento() {
		return editTextfechaNacimiento;
	}

	public void setEditTextfechaNacimiento(EditText editTextfechaNacimiento) {
		this.editTextfechaNacimiento = editTextfechaNacimiento;
	}

	public TextView getShowPerfilError() {
		return showPerfilError;
	}

	public void setShowPerfilError(TextView showPerfilError) {
		this.showPerfilError = showPerfilError;
	}
}
