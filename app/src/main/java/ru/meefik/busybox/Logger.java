package ru.meefik.busybox;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Logger {

    private static final List<String> protocol = new ArrayList<>();
    private static boolean fragment = false;

    /**
     * Generate timestamp
     *
     * @return timestamp
     */
    private static String getTimeStamp() {
        return "[" + new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date()) + "] ";
    }

    /**
     * Append the message to protocol and show
     *
     * @param c   context
     * @param msg message
     */
    private static synchronized void appendMessage(final Context c, final String msg) {
        final int msgLength = msg.length();
        if (msgLength > 0) {
            final boolean timestamp = PrefStore.isTimestamp(c);
            String[] tokens = {""};
            if (!"\n".equals(msg)) {
                tokens = msg.split("\\n");
            }
            int lastIndex = protocol.size() - 1;
            for (int i = 0, l = tokens.length; i < l; i++) {
                String token = tokens[i];
                // update last record from List if fragment
                if (i == 0 && fragment && lastIndex >= 0) {
                    String last = protocol.get(lastIndex);
                    protocol.set(lastIndex, last + token);
                    continue;
                }
                // add the message to List
                if (timestamp) protocol.add(getTimeStamp() + token);
                else protocol.add(token);
                // remove first line if overflow
                if (protocol.size() > PrefStore.getMaxLines(c)) {
                    protocol.remove(0);
                }
            }
            // set fragment
            fragment = (msg.charAt(msgLength - 1) != '\n');
            // show log
            MainActivity.showLog(get());
            // save the message to file
            if (PrefStore.isLogger(c)) {
                saveToFile(c, msg);
            }
        }
    }

    /**
     * Append message to file
     *
     * @param c   context
     * @param msg message
     */
    private static void saveToFile(Context c, String msg) {
        byte[] data = msg.getBytes();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(PrefStore.getLogFile(c), true);
            fos.write(data);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Clear protocol
     */
    static void clear() {
        protocol.clear();
        fragment = false;
    }

    /**
     * Get protocol
     *
     * @return protocol as text
     */
    static String get() {
        return android.text.TextUtils.join("\n", protocol);
    }

    /**
     * Append message to protocol
     *
     * @param c   context
     * @param msg message
     */
    static void log(Context c, String msg) {
        appendMessage(c, msg);
    }

}
