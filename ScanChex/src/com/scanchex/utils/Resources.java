package com.scanchex.utils;

import java.util.Vector;

import android.app.PendingIntent;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.SCAdminAssetDetailsInfo;
import com.scanchex.bo.SCDocumentInfo;
import com.scanchex.bo.SCHistoryInfo;
import com.scanchex.bo.SCQuestionsInfo;
import com.scanchex.bo.SCTicketExtraInfo;




public class Resources {
	
	private static Resources resources;
	
	public static Resources getResources(){
		if(resources == null){
			resources = new Resources();
		}return resources;
	}

	private String locale;
	private String ticketHistoryId;
	private boolean isFromAdminTakePicture;
	

	private AssetsTicketsInfo assetTicketInfo;
	private SCAdminAssetDetailsInfo assetDetailInfo;
	
	

	private Vector<SCDocumentInfo> documentsData;
	private Vector<SCHistoryInfo> historyData;
	private Vector<SCTicketExtraInfo> ticketExtraData;
	private Vector<AssetsTicketsInfo> assetsTicketData;
	
	private Vector<SCQuestionsInfo> questionsData;
	private PendingIntent pIntent;
	
	private int totalScans = 0;
	
	private boolean isForDoubleScan;
	private boolean isQuestionsSubmitted = false;

	
	
	
	
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getTicketHistoryId() {
		return ticketHistoryId;
	}

	public void setTicketHistoryId(String ticketHistoryId) {
		this.ticketHistoryId = ticketHistoryId;
	}
		
	public boolean isFromAdminTakePicture() {
		return isFromAdminTakePicture;
	}

	public void setFromAdminTakePicture(boolean isFromAdminTakePicture) {
		this.isFromAdminTakePicture = isFromAdminTakePicture;
	}
	
	public AssetsTicketsInfo getAssetTicketInfo() {
		return assetTicketInfo;
	}

	public void setAssetTicketInfo(AssetsTicketsInfo assetTicketInfo) {
		this.assetTicketInfo = assetTicketInfo;
	}
	
	public SCAdminAssetDetailsInfo getAssetDetailInfo() {
		return assetDetailInfo;
	}
	public void setAssetDetailInfo(SCAdminAssetDetailsInfo assetDetailInfo) {
		this.assetDetailInfo = assetDetailInfo;
	}

	public Vector<SCDocumentInfo> getDocumentsData() {
		return documentsData;
	}

	public void setDocumentsData(Vector<SCDocumentInfo> documentsData) {
		this.documentsData = documentsData;
	}

	public Vector<SCHistoryInfo> getHistoryData() {
		return historyData;
	}

	public void setHistoryData(Vector<SCHistoryInfo> historyData) {
		this.historyData = historyData;
	}

	public Vector<SCTicketExtraInfo> getTicketExtraData() {
		return ticketExtraData;
	}

	public void setTicketExtraData(Vector<SCTicketExtraInfo> ticketExtraData) {
		this.ticketExtraData = ticketExtraData;
	}

	public Vector<AssetsTicketsInfo> getAssetsTicketData() {
		return assetsTicketData;
	}

	public void setAssetsTicketData(Vector<AssetsTicketsInfo> assetsTicketData) {
		this.assetsTicketData = assetsTicketData;
	}

	public Vector<SCQuestionsInfo> getQuestionsData() {
		return questionsData;
	}

	public void setQuestionsData(Vector<SCQuestionsInfo> questionsData) {
		this.questionsData = questionsData;
	}
	
	
	public PendingIntent getpIntent() {
		return pIntent;
	}
	public void setpIntent(PendingIntent pIntent) {
		this.pIntent = pIntent;
	}

	
	public int getTotalScans() {
		return totalScans;
	}
	public void setTotalScans(int totalScans) {
		this.totalScans = totalScans;
	}
	
	public boolean isForDoubleScan() {
		return isForDoubleScan;
	}
	public void setForDoubleScan(boolean isForDoubleScan) {
		this.isForDoubleScan = isForDoubleScan;
	}
	
	public boolean isQuestionsSubmitted() {
		return isQuestionsSubmitted;
	}
	public void setQuestionsSubmitted(boolean isQuestionsSubmitted) {
		this.isQuestionsSubmitted = isQuestionsSubmitted;
	}
}
