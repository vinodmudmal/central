package edu.yu.einstein.wasp.controller.util;

import java.util.Date;

public class BatchJobTreeModel extends ExtTreeModel {
	
	private static final long serialVersionUID = 1483666735851913153L;

	private String name = "";
	
	private Long executionId = 0L;
	
	private Date startTime = null;
	
	private Date endTime = null;
	
	private String status = "";
	
	private String exitCode = "";
	
	private String exitMessage = "";

	public BatchJobTreeModel() {
	}

	public BatchJobTreeModel(String id, ExtIcon iconCls, boolean isExpanded, boolean isLeaf) {
		super(id, iconCls, isExpanded, isLeaf);
	}

	public BatchJobTreeModel(String id, ExtIcon iconCls, boolean isExpanded, boolean isLeaf, String name, Long executionId, Date startTime, Date endTime,
			String status, String exitCode, String exitMessage) {
		super(id, iconCls, isExpanded, isLeaf);
		this.name = name;
		this.executionId = executionId;
		this.startTime = startTime;
		this.endTime = endTime;
		status = status;
		exitCode = exitCode;
		exitMessage = exitMessage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getExecutionId() {
		return executionId;
	}

	public void setExecutionId(Long executionId) {
		this.executionId = executionId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExitCode() {
		return exitCode;
	}

	public void setExitCode(String exitCode) {
		this.exitCode = exitCode;
	}

	public String getExitMessage() {
		return exitMessage;
	}

	public void setExitMessage(String exitMessage) {
		this.exitMessage = exitMessage;
	}

}
