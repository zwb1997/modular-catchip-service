package com.ipfetchservice.web.common.banner;

import java.io.PrintStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;


public class ServiceBanner implements Banner {

   private static String SERVICE_BANNER1 = " _____   _____       _____  ______   _____  __      __  _____    _____    ______";

   private static String SERVICE_BANNER2 = "|_   _| |  __ \\    / ____| |  ____| |  __ \\ \\ \\    / / |_   _|  / ____|  |  ____|";

   private static String SERVICE_BANNER3 = "  | |   | |__) |   | (___  | |__    | |__) | \\ \\  / /    | |   | |       |  |__   ";

   private static String SERVICE_BANNER4 = "  | |   |  ___/    \\___ \\  |  __|   |  _  /   \\ \\/ /     | |   | |       |   __|  ";

   private static String SERVICE_BANNER5 = " _| |_  | |         ____)| | |____  | | \\ \\    \\  /     _| |_  | |____   |  |___ ";

   private static String SERVICE_BANNER6 = "|_____| |_|        |_____/ |______| |_|  \\_\\    \\/     |_____|  \\_____|  |______|";

   private static String SERVICE_VERSION= "0.1 VERSION";


   public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
      out.println(SERVICE_BANNER1);
      out.println(SERVICE_BANNER2);
      out.println(SERVICE_BANNER3);
      out.println(SERVICE_BANNER4);
      out.println(SERVICE_BANNER5);
      out.println(SERVICE_BANNER6);
      try{
         out.println(environment.getProperty("JAVA_HOME"));
      }catch(Exception e){
         out.println(" could not detect java version ,maybe not set JAVA_HOME ");
      }
      out.println(SERVICE_VERSION);
       
   }
}
