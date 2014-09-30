package com.scanchex.bo;

public class EntryItem implements Item{

	public String serviceId;
	public String model;
	public String description;
	public String status;
	public String ticketServiceId;
	public String time;
	public boolean isService;
	public String checkpoint_id;
	public String qr_code;
	public boolean isTrue;	
	
	public EntryItem(String serviceId, String model,String description,String status,String ticketServiceId,
			String time, boolean isServicePoint,String checkPoint_id,String qr_code,boolean isTrue) {
		this.serviceId = serviceId;
		this.model = model;
		this.description = description;
		this.status = status;
		this.ticketServiceId = ticketServiceId;
		this.time = time;
		this.isService = isServicePoint;
		this.checkpoint_id = checkPoint_id;
		this.qr_code = qr_code;
		this.isTrue =  isTrue;
	}
	
	@Override
	public boolean isSection() {
		return false;
	}

}
