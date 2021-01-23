package net.lldv.llamavote.components.language;

import cn.nukkit.utils.Config;
import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.llamavote.LlamaVote;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LlamaDevelopment
 * @project LlamaVote
 * @website http://llamadevelopment.net/
 */

public class Language {

    private final static Map<String, String> messages = new HashMap<>();
    private static String prefix;

    public static void init(final LlamaVote plugin) {
        messages.clear();
        plugin.saveResource("messages.yml");
        Config m = new Config(plugin.getDataFolder() + "/messages.yml");
        m.getAll().forEach((key, value) -> {
            if (value instanceof String) {
                String val = (String) value;
                messages.put(key, val);
            }
        });
        prefix = m.getString("prefix");
    }

    public static String get(String key, Object... replacements) {
        String message = prefix.replace("&", "§") + messages.getOrDefault(key, "null").replace("&", "§");

        int i = 0;
        for (Object replacement : replacements) {
            message = message.replace("[" + i + "]", String.valueOf(replacement));
            i++;
        }

        return message;
    }

    public static String getNP(String key, Object... replacements) {
        String message = messages.getOrDefault(key, "null").replace("&", "§");

        int i = 0;
        for (Object replacement : replacements) {
            message = message.replace("[" + i + "]", String.valueOf(replacement));
            i++;
        }

        return message;
    }

}
