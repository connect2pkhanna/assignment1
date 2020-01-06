package com.assignment.producer;

public abstract class Extractor {
	private String sourceId;

	// access to isReadCompletely is synchrnozed so that multiple threads do not
	// lead to data corruption in case of race-condition
	private boolean isReadCompletely = false;

	public abstract void defineSource();

	// validate that the source exists either csv,db or flat file
	public abstract boolean validateSourceAvailability();

	// this method is used in the conditions when the source is completely read &
	// then process has to start
	// such as sorting of the output can only be done when extraction is fully done.
	// This method is also used to interrupt the thread of source reading when the
	// source is completely read.

	public synchronized boolean isRead() {
		return isReadCompletely;
	}

	// isRead && markSourceAsRead can be accessed with only one thread,both methods
	// have two different syn blocks as java locks are re-entrant
	// i.e ( you can acquire the already acquired lock again)
	public synchronized void markSourceAsRead() {
		if (!isRead())
		{
			this.isReadCompletely = true;
		}
	}

	public void setSourceDescritption(String description) {
		this.sourceId = description;
	}

	public String getSourceDescription() {
		return this.sourceId;
	}

}
