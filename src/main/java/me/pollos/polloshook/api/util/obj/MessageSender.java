package me.pollos.polloshook.api.util.obj;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.network.NetworkUtil;
import me.pollos.polloshook.api.util.text.TextUtil;

public class MessageSender extends AbstractSendable implements Minecraftable {
    private final String message;

    public MessageSender(String message) {
        this.message = message;
    }

    public void send() {
        if (!TextUtil.isNullOrEmpty(this.message)) {
            NetworkUtil.sendInChat(this.message);
        }
    }

    public String getMessage() {
        return this.message;
    }
}