package com.scanchex.bo;

public class SCDocumentInfo {

	public String documentSubject;
	public String documentUrl;
	public boolean fillable;
	public SCDocumentInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getDocumentSubject() {
		return documentSubject;
	}
	public void setDocumentSubject(String documentSubject) {
		this.documentSubject = documentSubject;
	}
	public String getDocumentUrl() {
		return documentUrl;
	}
	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}
	public boolean isFillable() {
		return fillable;
	}
	public void setFillable(boolean fillable) {
		this.fillable = fillable;
	}
	
	
}
