package me.pollos.polloshook.irc.api;

import me.pollos.polloshook.irc.DccChat;
import me.pollos.polloshook.irc.DccFileTransfer;
import me.pollos.polloshook.irc.beans.User;

public abstract class IrcEventHandlerBase implements IIrcEventHandler {
   public abstract void onVersion(String var1, String var2, String var3, String var4);

   public abstract void onPing(String var1, String var2, String var3, String var4, String var5);

   public abstract void onServerPing(String var1);

   public abstract void onTime(String var1, String var2, String var3, String var4);

   public abstract void onFinger(String var1, String var2, String var3, String var4);

   public void onConnect() {
   }

   public void onDisconnect() {
   }

   public void onMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
   }

   public void onUserMode(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
   }

   public void onOp(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
   }

   public void onDeop(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
   }

   public void onVoice(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
   }

   public void onDeVoice(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
   }

   public void onSetChannelKey(String channel, String sourceNick, String sourceLogin, String sourceHostname, String key) {
   }

   public void onRemoveChannelKey(String channel, String sourceNick, String sourceLogin, String sourceHostname, String key) {
   }

   public void onSetChannelLimit(String channel, String sourceNick, String sourceLogin, String sourceHostname, int limit) {
   }

   public void onRemoveChannelLimit(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
   }

   public void onSetChannelBan(String channel, String sourceNick, String sourceLogin, String sourceHostname, String hostmask) {
   }

   public void onRemoveChannelBan(String channel, String sourceNick, String sourceLogin, String sourceHostname, String hostmask) {
   }

   public void onSetTopicProtection(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
   }

   public void onRemoveTopicProtection(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
   }

   public void onSetNoExternalMessages(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
   }

   public void onRemoveNoExternalMessages(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
   }

   public void onSetInviteOnly(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
   }

   public void onRemoveInviteOnly(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
   }

   public void onSetModerated(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
   }

   public void onRemoveModerated(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
   }

   public void onSetPrivate(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
   }

   public void onRemovePrivate(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
   }

   public void onSetSecret(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
   }

   public void onRemoveSecret(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
   }

   public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) {
   }

   public void onDccSendRequest(String sourceNick, String sourceLogin, String sourceHostname, String filename, long address, int port, int size) {
   }

   public void onDccChatRequest(String sourceNick, String sourceLogin, String sourceHostname, long address, int port) {
   }

   public void onIncomingFileTransfer(DccFileTransfer transfer) {
   }

   public void onFileTransferFinished(DccFileTransfer transfer, Exception e) {
   }

   public void onIncomingChatRequest(DccChat chat) {
   }

   public void onUnknown(String line) {
   }

   public void onServerResponse(int code, String response) {
   }

   public void onUserList(String channel, User[] users) {
   }

   public void onMessage(String channel, String sender, String login, String hostname, String message) {
   }

   public void onPrivateMessage(String sender, String login, String hostname, String message) {
   }

   public void onAction(String sender, String login, String hostname, String target, String action) {
   }

   public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
   }

   public void onJoin(String channel, String sender, String login, String hostname) {
   }

   public void onPart(String channel, String sender, String login, String hostname) {
   }

   public void onNickChange(String oldNick, String login, String hostname, String newNick) {
   }

   public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
   }

   public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
   }

   public void onTopic(String channel, String topic) {
   }

   public void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
   }

   public void onChannelInfo(String channel, int userCount, String topic) {
   }
}
