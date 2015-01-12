package ch.infbr5.sentinel.server.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
   @NamedQuery(name = PrintJob.GET_PRINTJOBS, query = "SELECT p FROM PrintJob p ORDER BY p.printJobDate DESC"),
      @NamedQuery(name = PrintJob.GET_PRINTJOB_BY_ID, query = "SELECT p FROM PrintJob p WHERE p.id = :printjobId") })
public class PrintJob {

   public static final String GET_PRINTJOBS = "getPrintJobs";

   public static final String GET_PRINTJOB_BY_ID = "getPrintJobById";

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private String printJobDesc;
   private Date printJobDate;
   private String pintJobFile;
   private int reprints;

   public Long getId() {
      return id;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public String getPrintJobDesc() {
      return printJobDesc;
   }

   public void setPrintJobDesc(final String printJobDesc) {
      this.printJobDesc = printJobDesc;
   }

   public Date getPrintJobDate() {
      return printJobDate;
   }

   public void setPrintJobDate(final Date printJobDate) {
      this.printJobDate = printJobDate;
   }

   public String getPintJobFile() {
      return pintJobFile;
   }

   public void setPintJobFile(final String pintJobFile) {
      this.pintJobFile = pintJobFile;
   }

   public int getReprints() {
      return reprints;
   }

   public void setReprints(final int reprints) {
      this.reprints = reprints;
   }

}
