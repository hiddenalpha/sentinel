package ch.infbr5.sentinel.server.ws;

public class SystemInfo {

   private String sentinelVersion;

   private String sentinelBuild;

   private String javaVendor;

   private String javaVersion;

   private String javaHome;

   private String osArch;

   private String osName;

   private String osVersion;

   private String userDir;

   public String getSentinelVersion() {
      return sentinelVersion;
   }

   public void setSentinelVersion(final String sentinelVersion) {
      this.sentinelVersion = sentinelVersion;
   }

   public String getSentinelBuild() {
      return sentinelBuild;
   }

   public void setSentinelBuild(final String sentinelBuild) {
      this.sentinelBuild = sentinelBuild;
   }

   public String getJavaVendor() {
      return javaVendor;
   }

   public void setJavaVendor(final String javaVendor) {
      this.javaVendor = javaVendor;
   }

   public String getJavaVersion() {
      return javaVersion;
   }

   public void setJavaVersion(final String javaVersion) {
      this.javaVersion = javaVersion;
   }

   public String getJavaHome() {
      return javaHome;
   }

   public void setJavaHome(final String javaHome) {
      this.javaHome = javaHome;
   }

   public String getOsArch() {
      return osArch;
   }

   public void setOsArch(final String osArch) {
      this.osArch = osArch;
   }

   public String getOsName() {
      return osName;
   }

   public void setOsName(final String osName) {
      this.osName = osName;
   }

   public String getOsVersion() {
      return osVersion;
   }

   public void setOsVersion(final String osVersion) {
      this.osVersion = osVersion;
   }

   public String getUserDir() {
      return userDir;
   }

   public void setUserDir(final String userDir) {
      this.userDir = userDir;
   }

}
