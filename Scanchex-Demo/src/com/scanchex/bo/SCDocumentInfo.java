package com.scanchex.bo;

public class SCDocumentInfo implements Item{

	public String documentSubject;
	public String documentUrl;
	public boolean fillable;
	public String status;
	public String document_id;
	public String version;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDocument_id() {
		return document_id;
	}
	public void setDocument_id(String document_id) {
		this.document_id = document_id;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	@Override
	public boolean isSection() {
		return false;
	}
	
	
}
