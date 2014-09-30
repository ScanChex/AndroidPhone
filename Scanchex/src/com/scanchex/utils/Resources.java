package com.scanchex.utils;

import java.util.ArrayList;
import java.util.Vector;

import android.app.PendingIntent;
import android.content.Context;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.Item;
import com.scanchex.bo.SCAdminAssetDetailsInfo;
import com.scanchex.bo.SCDocumentInfo;
import com.scanchex.bo.SCHistoryInfo;
import com.scanchex.bo.SCMessageInfo;
import com.scanchex.bo.SCQuestionsInfo;
import com.scanchex.bo.SCTicketExtraInfo;
import com.scanchex.bo.ScCheckPoints;

public class Resources {

	private static Resources resources;

	public static Resources getResources() {
		if (resources == null) {
			resources = new Resources();
		}
		return resources;
	}

	private String locale;
	private String ticketHistoryId;
	private boolean isFromAdminTakePicture;
	private boolean isCloseTicket;
	private ArrayList<ScCheckPoints> checkPointModelArray;
	private ScCheckPoints checkPointModel;
	ArrayList<Item> itemList = new ArrayList<Item>();
	private AssetsTicketsInfo assetTicketInfo;
	private SCAdminAssetDetailsInfo assetDetailInfo;
	private SCMessageInfo messageInfo;
	private Vector<SCDocumentInfo> documentsData;
	private Vector<SCHistoryInfo> historyData;
	private Vector<SCTicketExtraInfo> ticketExtraData;
	private Vector<AssetsTicketsInfo> assetsTicketData;
	private ArrayList<ScCheckPoints> checkPoints;
	private Vector<SCQuestionsInfo> questionsData;
	private PendingIntent pIntent;
	private String pushNotificationId;
	private int totalScans = 0;
	private boolean isForDoubleScan;
	private boolean isQuestionsSubmitted = false;
	private boolean isFirstScanDone = false;
	private boolean isCheckPointDone = false;
	private boolean isCheckPointScan;
	private boolean correctTicket = false; 
	private long timeToStartTicket;

	public long getTimeToStartTicket() {
		return timeToStartTicket;
	}

	public void setTimeToStartTicket(long timeToStartTicket) {
		this.timeToStartTicket = timeToStartTicket;
	}

	public boolean isCorrectTicket() {
		return correctTicket;
	}

	public void setCorrectTicket(boolean correctTicket) {
		this.correctTicket = correctTicket;
	}

	public SCMessageInfo getMessageInfo() {
		return messageInfo;
	}

	public void setMessageInfo(SCMessageInfo messageInfo) {
		this.messageInfo = messageInfo;
	}

	public ArrayList<ScCheckPoints> getCheckPoints() {
		return checkPoints;
	}

	public void setCheckPoints(ArrayList<ScCheckPoints> checkPoints) {
		this.checkPoints = checkPoints;
	}

	public ArrayList<Item> getItemList() {
		return itemList;
	}

	public void setItemList(ArrayList<Item> itemList) {
		this.itemList = itemList;
	}

	public boolean isCloseTicket() {
		return isCloseTicket;
	}

	public void setCloseTicket(boolean isCloseTicket) {
		this.isCloseTicket = isCloseTicket;
	}

	public ScCheckPoints getCheckPointModel() {
		return checkPointModel;
	}

	public void setCheckPointModel(ScCheckPoints checkPointModel) {
		this.checkPointModel = checkPointModel;
	}

	public ArrayList<ScCheckPoints> getCheckPointModelArray() {
		return checkPointModelArray;
	}

	public void setCheckPointModelArray(
			ArrayList<ScCheckPoints> checkPointModelArray) {
		this.checkPointModelArray = checkPointModelArray;
	}

	public boolean isCheckPointScan() {
		return isCheckPointScan;
	}

	public void setCheckPointScan(boolean isCheckPointScan) {
		this.isCheckPointScan = isCheckPointScan;
	}

	public boolean isCheckPointDone() {
		return isCheckPointDone;
	}

	public void setCheckPointDone(boolean isCheckPointDone) {
		this.isCheckPointDone = isCheckPointDone;
	}

	private Context currentContext;

	public Context getCurrentContext() {
		return currentContext;
	}

	public void setCurrentContext(Context currentContext) {
		this.currentContext = currentContext;
	}

	public boolean isFirstScanDone() {
		return isFirstScanDone;
	}

	public void setFirstScanDone(boolean isFirstScanDone) {
		this.isFirstScanDone = isFirstScanDone;
	}

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

	public String getPushNotificationId() {
		return pushNotificationId;
	}

	public void setPushNotificationId(String pushNotificationId) {
		this.pushNotificationId = pushNotificationId;
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
