package core;

import java.sql.Date;

public class Order {
	private int order_id;
	private int amount;
	private int user_id;
	private Date datePlace;
	public int getOder_id() {
		return order_id;
	}
	public void setOder_id(int oder_id) {
		this.order_id = oder_id;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getUsr_id() {
		return user_id;
	}
	public void setUsr_id(int usr_id) {
		this.user_id = usr_id;
	}
	public void setDatePlace(Date datePlace) {
		this.datePlace = datePlace;
	}
	public Date getDatePlace() {
		return datePlace;
	}
}
