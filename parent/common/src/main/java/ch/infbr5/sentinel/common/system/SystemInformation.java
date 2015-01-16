package ch.infbr5.sentinel.common.system;

public class SystemInformation {

   public String getOsVersion() {
      return System.getProperty("os.version");
   }

   public String getOsName() {
      return System.getProperty("os.name");
   }

   public String getOsArch() {
      return System.getProperty("os.arch");
   }

   public int getAvailableProcessors() {
      return Runtime.getRuntime().availableProcessors();
   }

   public long getFreeMemoryInBytes() {
      return Runtime.getRuntime().freeMemory();
   }

   public long getMaxMemoryForJVMInBytes() {
      return Runtime.getRuntime().maxMemory();
   }

   public long getTotalMemoryForJVmAvailableInBytes() {
      return Runtime.getRuntime().totalMemory();
   }

}
