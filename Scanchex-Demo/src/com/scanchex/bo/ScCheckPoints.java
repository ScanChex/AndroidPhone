package com.scanchex.bo;

public class ScCheckPoints {
	public String checkpoint_id;
	public String qr_code;
	public String description;
	public boolean isTrue;
	public String time;

	public ScCheckPoints() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getCheckpoint_id() {
		return checkpoint_id;
	}

	public void setCheckpoint_id(String checkpoint_id) {
		this.checkpoint_id = checkpoint_id;
	}

	public String getQr_code() {
		return qr_code;
	}

	public void setQr_code(String qr_code) {
		this.qr_code = qr_code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isTrue() {
		return isTrue;
	}

	public void setTrue(boolean isTrue) {
		this.isTrue = isTrue;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
