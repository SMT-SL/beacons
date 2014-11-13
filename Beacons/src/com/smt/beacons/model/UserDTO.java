package com.smt.beacons.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class UserDTO {
	
	private long userId;
	private String nickName;
	private String password;
	private String deviceToken;
	private String token;
	private String email;
	private String Sex;
	private int CP;
	private Date birthDate;
	
	public UserDTO(JSONObject jsonInput) {
		try {
			userId = jsonInput.getLong("UserId");
			nickName = jsonInput.getString("nickName");
			password = jsonInput.getString("password");
			deviceToken = jsonInput.getString("deviceToken");
			token = jsonInput.getString("token");
			email = jsonInput.getString("email");
			Sex = jsonInput.getString("Sex");
			CP = jsonInput.getInt("CP");
			birthDate = new Date(Long.valueOf(jsonInput.getString("birthDate").substring(6, jsonInput.getString("birthDate").indexOf('+'))).longValue());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSex() {
		return Sex;
	}

	public void setSex(String sex) {
		Sex = sex;
	}

	public int getCP() {
		return CP;
	}

	public void setCP(int cP) {
		CP = cP;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	
}
