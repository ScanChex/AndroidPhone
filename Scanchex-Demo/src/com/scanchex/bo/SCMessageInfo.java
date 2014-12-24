package com.scanchex.bo;

public class SCMessageInfo {

	public String messageId;
	public String message;
	public String senderId;
	public String senderName;
	public String senderPhoto;
	
	public String receiverId;
	public String receiverName;
	public String receiverPhoto;
	public String dateTime;
	public SCMessageInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getSenderPhoto() {
		return senderPhoto;
	}
	public void setSenderPhoto(String senderPhoto) {
		this.senderPhoto = senderPhoto;
	}
	public String getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getReceiverPhoto() {
		return receiverPhoto;
	}
	public void setReceiverPhoto(String receiverPhoto) {
		this.receiverPhoto = receiverPhoto;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
	
}
