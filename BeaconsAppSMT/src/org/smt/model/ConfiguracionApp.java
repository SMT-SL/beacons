package org.smt.model;

public class ConfiguracionApp {

	private int pos;
	private int typeConfig;
	private String title;
	
	public ConfiguracionApp(){
		
	}
	
	public ConfiguracionApp(int pos,int typeConfig,String tit){
		setPos(pos);
		setTypeConfig(typeConfig);
		setTitle(tit);
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getTypeConfig() {
		return typeConfig;
	}
	public void setTypeConfig(int typeConfig) {
		this.typeConfig = typeConfig;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	
}
