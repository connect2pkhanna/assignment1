package com.assignment.common;

public class Record implements Comparable<Record>{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Record other = (Record) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

	String to;
	String from;
	Double amount;
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	public boolean validate(){
		if(to==null || from==null || to.isEmpty() || from.isEmpty() || amount==null || amount==0.0 || to.equals(from)) 
		{
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		return "Record [to=" + to + ", from=" + from + ", amount=" + amount + "]";
	}
	@Override
	public int compareTo(Record that) {
		 return this.from.compareTo(that.getFrom());
	}

}
