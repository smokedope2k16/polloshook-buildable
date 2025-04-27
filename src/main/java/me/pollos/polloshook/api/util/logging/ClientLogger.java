package me.pollos.polloshook.api.util.logging;

import me.pollos.polloshook.api.interfaces.Loggable;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.crash.CrashReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientLogger implements Loggable {
    private static final Logger log = LoggerFactory.getLogger(ClientLogger.class);
    private static ClientLogger INSTANCE = new ClientLogger();
    private static final int MESSAGE_ID = -2147442069;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public void log(String text) {
        this.log(text, -2147442069);
    }

    public void log(Text text) {
        MutableText append = Text.of(" " + String.valueOf(Formatting.GRAY)).copy();
        TextUtil.printWithID(this.getAlert().copy().append(append).append(text), -2147442069);
    }

    public void log(String text, int id) {
        MutableText var10000 = this.getAlert().copy();
        String var10001 = String.valueOf(Formatting.GRAY);
        TextUtil.printWithID(var10000.append(" " + var10001 + text), id);
    }

    public void log(String text, boolean delete) {
        if (delete) {
            this.log(text);
        } else {
            this.log(text, 0);
        }
    }

    public void log(Text text, int id) {
        MutableText append = Text.of(" " + String.valueOf(Formatting.GRAY)).copy();
        TextUtil.printWithID(this.getAlert().copy().append(append).append(text), id);
    }

    public void log(Text text, boolean delete) {
        if (delete) {
            this.log(text);
        } else {
            MutableText append = Text.of(" " + String.valueOf(Formatting.GRAY)).copy();
            TextUtil.printWithID(this.getAlert().copy().append(append).append(text), 0);
        }
    }

    public void logNoMark(String text) {
        this.logNoMark(text, -2147442069);
    }

    public void logNoMark(String text, int id) {
        TextUtil.printWithID(Text.of(text), id);
    }

    public void report(String text, Exception e) {
        this.mc.printCrashReport(new CrashReport(text, e));
    }

    public void info(String text) {
        log.info(text);
    }

    public void error(String text) {
        log.error(text);
    }

    public void warn(String text) {
        log.warn(text);
    }

    public Text getAlert() {
        return Manager.get().getClientName();
    }

    public static ClientLogger getLogger() {
        return INSTANCE == null ? (INSTANCE = new ClientLogger()) : INSTANCE;
    }

    public MinecraftClient getMc() {
        return this.mc;
    }
}