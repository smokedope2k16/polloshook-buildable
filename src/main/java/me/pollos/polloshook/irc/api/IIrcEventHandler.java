package me.pollos.polloshook.irc.api;

import me.pollos.polloshook.irc.DccChat;
import me.pollos.polloshook.irc.DccFileTransfer;
import me.pollos.polloshook.irc.beans.User;

public interface IIrcEventHandler {
   void onConnect();

   void onDisconnect();

   void onServerResponse(int var1, String var2);

   void onUserList(String var1, User[] var2);

   void onMessage(String var1, String var2, String var3, String var4, String var5);

   void onPrivateMessage(String var1, String var2, String var3, String var4);

   void onAction(String var1, String var2, String var3, String var4, String var5);

   void onNotice(String var1, String var2, String var3, String var4, String var5);

   void onJoin(String var1, String var2, String var3, String var4);

   void onPart(String var1, String var2, String var3, String var4);

   void onNickChange(String var1, String var2, String var3, String var4);

   void onKick(String var1, String var2, String var3, String var4, String var5, String var6);

   void onQuit(String var1, String var2, String var3, String var4);

   void onTopic(String var1, String var2);

   void onTopic(String var1, String var2, String var3, long var4, boolean var6);

   void onChannelInfo(String var1, int var2, String var3);

   void onVoice(String var1, String var2, String var3, String var4, String var5);

   void onDeVoice(String var1, String var2, String var3, String var4, String var5);

   void onOp(String var1, String var2, String var3, String var4, String var5);

   void onDeop(String var1, String var2, String var3, String var4, String var5);

   void onSetChannelKey(String var1, String var2, String var3, String var4, String var5);

   void onRemoveChannelKey(String var1, String var2, String var3, String var4, String var5);

   void onSetChannelLimit(String var1, String var2, String var3, String var4, int var5);

   void onRemoveChannelLimit(String var1, String var2, String var3, String var4);

   void onSetChannelBan(String var1, String var2, String var3, String var4, String var5);

   void onRemoveChannelBan(String var1, String var2, String var3, String var4, String var5);

   void onSetTopicProtection(String var1, String var2, String var3, String var4);

   void onRemoveTopicProtection(String var1, String var2, String var3, String var4);

   void onSetNoExternalMessages(String var1, String var2, String var3, String var4);

   void onRemoveNoExternalMessages(String var1, String var2, String var3, String var4);

   void onSetInviteOnly(String var1, String var2, String var3, String var4);

   void onRemoveInviteOnly(String var1, String var2, String var3, String var4);

   void onSetModerated(String var1, String var2, String var3, String var4);

   void onRemoveModerated(String var1, String var2, String var3, String var4);

   void onSetPrivate(String var1, String var2, String var3, String var4);

   void onRemovePrivate(String var1, String var2, String var3, String var4);

   void onSetSecret(String var1, String var2, String var3, String var4);

   void onRemoveSecret(String var1, String var2, String var3, String var4);

   void onInvite(String var1, String var2, String var3, String var4, String var5);

   void onDccSendRequest(String var1, String var2, String var3, String var4, long var5, int var7, int var8);

   void onDccChatRequest(String var1, String var2, String var3, long var4, int var6);

   void onIncomingFileTransfer(DccFileTransfer var1);

   void onFileTransferFinished(DccFileTransfer var1, Exception var2);

   void onIncomingChatRequest(DccChat var1);

   void onFinger(String var1, String var2, String var3, String var4);

   void onMode(String var1, String var2, String var3, String var4, String var5);

   void onPing(String var1, String var2, String var3, String var4, String var5);

   void onServerPing(String var1);

   void onTime(String var1, String var2, String var3, String var4);

   void onUnknown(String var1);

   void onUserMode(String var1, String var2, String var3, String var4, String var5);

   void onVersion(String var1, String var2, String var3, String var4);
}
