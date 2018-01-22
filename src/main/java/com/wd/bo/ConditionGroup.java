package com.wd.bo;

public class ConditionGroup {
	
	/**
	 * 逻辑类型:0与，1或，2非
	 */
	private Short logic=0;
	
	/**域*/
	private String field;
	
	/**
	 * 值
	 */
	private String value;
	
	public String toXMLString(){
		StringBuilder sbuilder=new StringBuilder("<group>");
		sbuilder.append("<logic>"+logic+"</logic>");
		sbuilder.append("<field>"+field+"</field>");
		sbuilder.append("<value>"+value+"</value>");
		sbuilder.append("</group>");
		return sbuilder.toString();
	}

	public Short getLogic() {
		return logic;
	}

	public void setLogic(Short logic) {
		this.logic = logic;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString(){
		return String.format("#%d#%s#%s", logic,field==null?"":field,value==null?"":value);
	}


}
