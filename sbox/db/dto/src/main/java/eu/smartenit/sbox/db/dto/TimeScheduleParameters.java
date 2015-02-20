/**
 * Copyright (C) 2014 The SmartenIT consortium (http://www.smartenit.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.smartenit.sbox.db.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * The TimeScheduleParameters class.
 *
 * @author George Petropoulos
 * @version 3.0
 * 
 */
public final class TimeScheduleParameters implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public TimeScheduleParameters() {
		this.startDate = new Date();
		this.timeUnit = TimeUnit.SECONDS;
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param startDate
	 * @param accountingPeriod
	 * @param reportingPeriod
	 */
	public TimeScheduleParameters(Date startDate, long accountingPeriod,
			long reportingPeriod) {

		this.startDate = startDate;
		this.accountingPeriod = accountingPeriod;
		this.reportingPeriod = reportingPeriod;
		this.timeUnit = TimeUnit.SECONDS;
	}	

	/**
	 * The constructor with arguments.
	 * 
	 * @param startDate
	 * @param accountingPeriod
	 * @param reportingPeriod
	 * @param timeUnit
	 * @param tol1
	 * @param tol2
	 * @param samplingPeriod
	 * @param reportPeriodEA
	 * @param reportPeriodDTM
	 * @param compensationPeriod
	 */
	public TimeScheduleParameters(Date startDate, long accountingPeriod,
			long reportingPeriod, TimeUnit timeUnit, double tol1, double tol2,
			long samplingPeriod, long reportPeriodEA, long reportPeriodDTM,
			long compensationPeriod) {
		
		this.startDate = startDate;
		this.accountingPeriod = accountingPeriod;
		this.reportingPeriod = reportingPeriod;
		this.timeUnit = timeUnit;
		this.tol1 = tol1;
		this.tol2 = tol2;
		this.samplingPeriod = samplingPeriod;
		this.reportPeriodEA = reportPeriodEA;
		this.reportPeriodDTM = reportPeriodDTM;
		this.compensationPeriod = compensationPeriod;
	}


	private Date startDate;
	
	private long accountingPeriod;
	
	private long reportingPeriod;
	
	private TimeUnit timeUnit;
	
	private double tol1;

	private double tol2;
	
	private long samplingPeriod;
	
	private long reportPeriodEA;
	
	private long reportPeriodDTM;
	
	private long compensationPeriod;
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public long getAccountingPeriod() {
		return accountingPeriod;
	}
	
	public void setAccountingPeriod(long accountingPeriod) {
		this.accountingPeriod = accountingPeriod;
	}
	
	public long getReportingPeriod() {
		return reportingPeriod;
	}
	
	public void setReportingPeriod(long reportingPeriod) {
		this.reportingPeriod = reportingPeriod;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public double getTol1() {
		return tol1;
	}

	public void setTol1(double tol1) {
		this.tol1 = tol1;
	}

	public double getTol2() {
		return tol2;
	}

	public void setTol2(double tol2) {
		this.tol2 = tol2;
	}

	public long getSamplingPeriod() {
		return samplingPeriod;
	}

	public void setSamplingPeriod(long samplingPeriod) {
		this.samplingPeriod = samplingPeriod;
	}

	public long getReportPeriodEA() {
		return reportPeriodEA;
	}

	public void setReportPeriodEA(long reportPeriodEA) {
		this.reportPeriodEA = reportPeriodEA;
	}

	public long getReportPeriodDTM() {
		return reportPeriodDTM;
	}

	public void setReportPeriodDTM(long reportPeriodDTM) {
		this.reportPeriodDTM = reportPeriodDTM;
	}

	public long getCompensationPeriod() {
		return compensationPeriod;
	}

	public void setCompensationPeriod(long compensationPeriod) {
		this.compensationPeriod = compensationPeriod;
	}

	@Override
	public String toString() {
		return "TimeScheduleParameters [startDate=" + startDate
				+ ", accountingPeriod=" + accountingPeriod
				+ ", reportingPeriod=" + reportingPeriod + ", timeUnit="
				+ timeUnit + ", tol1=" + tol1 + ", tol2=" + tol2
				+ ", samplingPeriod=" + samplingPeriod + ", reportPeriodEA="
				+ reportPeriodEA + ", reportPeriodDTM=" + reportPeriodDTM
				+ ", compensationPeriod=" + compensationPeriod + "]";
	}
	
	

}
