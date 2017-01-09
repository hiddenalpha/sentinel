package ch.infbr5.sentinel.server.ws;

import java.util.Date;

public class PrintJobDetails {

	private Long printJobId;
	private String printJobDesc;
	private Date printJobDate;
	private String pintJobFile;
	private byte[] pdf;

	public Long getPrintJobId() {
		return printJobId;
	}

	public void setPrintJobId(Long printJobId) {
		this.printJobId = printJobId;
	}

	public String getPrintJobDesc() {
		return printJobDesc;
	}

	public void setPrintJobDesc(String printJobDesc) {
		this.printJobDesc = printJobDesc;
	}

	public Date getPrintJobDate() {
		return printJobDate;
	}

	public void setPrintJobDate(Date printJobDate) {
		this.printJobDate = printJobDate;
	}

	public String getPintJobFile() {
		return pintJobFile;
	}

	public void setPintJobFile(String pintJobFile) {
		this.pintJobFile = pintJobFile;
	}

	public byte[] getPdf() {
		return pdf;
	}

	public void setPdf(byte[] pdf) {
		this.pdf = pdf;
	}

}
