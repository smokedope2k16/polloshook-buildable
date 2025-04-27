package me.pollos.polloshook.impl.manager.irc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.irc.IrcServerConnection;
import me.pollos.polloshook.irc.beans.User;
import me.pollos.polloshook.irc.ex.IrcException;

public class IrcManager extends IrcServerConnection {
   public static final String IRC_CHANNEL_NAME = "#keqing4pollos";
   private String username;
   private final List<String> onlineUsers = new ArrayList();

   public void start(String username) {
      if (this.isConnected()) {
         ClientLogger.getLogger().info("Already connected to irc...");
      } else {
         this.username = this.getFixedName(username);
         ClientLogger.getLogger().info("Logging with username: " + this.username);
         this.connect();
      }
   }

   public void connect() {
      this.setAutoNickChange(true);
      this.setName(this.username);
      this.changeNick(this.username);
      ClientLogger.getLogger().info("Connecting To IRC");
      ClientLogger.getLogger().info("Attempting to connect to IRC.");

      try {
         this.connect("irc.libera.chat", 6667);
      } catch (IrcException | IOException var2) {
         var2.printStackTrace();
      }

   }

   public void join() {
      ClientLogger.getLogger().info("Joining Room");
      this.joinChannel("#keqing4pollos");
      ClientLogger.getLogger().info("Logged in");
      ClientLogger.getLogger().info("Connected.");
   }

   public List<String> getFixedUsers() {
      List<String> fixed = new ArrayList();
      Iterator var2 = this.onlineUsers.iterator();

      while(var2.hasNext()) {
         String user = (String)var2.next();
         fixed.add(this.cropNumbers(user));
      }

      return fixed;
   }

   public String cropNumbers(String user) {
      List<Integer> numbers = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
      boolean flag = false; 

      if (user.startsWith("MC") && user.length() > 2) {
          char thirdChar = user.charAt(2);
          if (Character.isDigit(thirdChar)) {
              try {
                  int thirdDigit = Integer.parseInt(String.valueOf(thirdChar));
                  if (numbers.contains(thirdDigit)) {
                      flag = true;
                  }
              } catch (NumberFormatException e) {
                  ClientLogger.getLogger().warn("Failed to parse character as digit: " + thirdChar);
              }
          }
      }

      return flag ? user.replaceFirst("MC", "") : user;
   }


   public boolean isClientUser(String username) {
      Iterator var2 = this.onlineUsers.iterator();

      String user;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         user = (String)var2.next();
         username = this.getFixedName(username);
      } while(!user.equalsIgnoreCase(username));

      return true;
   }

   public void addClientUsers(User[] users) {
      this.onlineUsers.clear();
      User[] var2 = users;
      int var3 = users.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         User user = var2[var4];
         this.onlineUsers.add(user.getNick());
      }

   }

   public void changeUsername(String username) {
      String name = this.getFixedName(username);
      if (this.isConnected() && !this.username.equalsIgnoreCase(name)) {
         ClientLogger.getLogger().info("Changing nickname to: " + name);
         this.username = name;
         this.setName(name);
         this.changeNick(name);
      }

   }

   public String getFixedName(String username) {
      try {
         String firstname = username.substring(0, 1);
         int i = Integer.parseInt(firstname);
         username = "MC" + username;
      } catch (NumberFormatException var4) {}

      return username;
   }


   public String getUsername() {
      return this.username;
   }


   public List<String> getOnlineUsers() {
      return this.onlineUsers;
   }
}
