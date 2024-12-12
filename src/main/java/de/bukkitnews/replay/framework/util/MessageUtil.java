package de.bukkitnews.replay.framework.util;


import de.bukkitnews.replay.framework.database.ConfigManager;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class MessageUtil {

    private static final Map<String, String> messages = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(MessageUtil.class.getName());

    /**
     * Loads messages from the configuration file.
     *
     * @param configManager The ConfigManager that manages the configuration file.
     */
    public static void loadMessages(ConfigManager configManager) {
        FileConfiguration config = configManager.getConfig();

        if (!config.contains("messages")) {
            LOGGER.log(Level.WARNING, "No 'messages' section found in the configuration file!");
            return;
        }

        config.getConfigurationSection("messages").getKeys(false).stream()
                .filter(key -> {
                    String message = config.getString("messages." + key, "");
                    if (message.isEmpty()) {
                        LOGGER.log(Level.WARNING, "Empty message found for key '" + key + "' in the configuration.");
                        return false;
                    }
                    messages.put(key, message);
                    return true;
                }).forEach(key -> {
                });
    }

    /**
     * Retrieves a formatted message with placeholders.
     *
     * @param key          The key of the message in the configuration.
     * @param placeholders The placeholders to replace in the message.
     * @return The formatted message.
     */
    public static String getMessage(String key, String... placeholders) {
        String template = messages.getOrDefault(key, "Unknown message key: " + key);
        String formatted = formatMessage(template, placeholders);
        return ChatColor.translateAlternateColorCodes('&', formatted);
    }

    /**
     * Retrieves a message without placeholders.
     *
     * @param key The key of the message in the configuration.
     * @return The message without placeholders.
     */
    public static String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes(
                '&', messages.getOrDefault(key, "Unknown message key: " + key));
    }

    /**
     * Helper method to format messages with placeholders.
     *
     * @param template     The message template with placeholders.
     * @param placeholders The placeholders to replace in the message.
     * @return The formatted message.
     */
    private static String formatMessage(String template, String... placeholders) {
        if (placeholders.length != countPlaceholders(template)) {
            LOGGER.log(Level.WARNING, "Number of placeholders does not match the number of '%s' in the template.");
        }
        return String.format(template, (Object[]) placeholders);
    }

    /**
     * Counts the placeholders (%s) in a message template.
     *
     * @param template The message template.
     * @return The number of placeholders.
     */
    private static int countPlaceholders(String template) {
        Pattern pattern = Pattern.compile("%s");
        Matcher matcher = pattern.matcher(template);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Retrieves a message as an Optional, in case the key is not found.
     *
     * @param key The key of the message in the configuration.
     * @return An Optional containing the message, if present, or empty if not found.
     */
    public static Optional<String> getMessageOptional(String key) {
        return Optional.ofNullable(messages.get(key));
    }

    /**
     * Retrieves a formatted message with placeholders as an Optional.
     *
     * @param key          The key of the message.
     * @param placeholders The placeholders to replace in the message.
     * @return The formatted message as an Optional.
     */
    public static Optional<String> getMessageOptional(String key, String... placeholders) {
        return getMessageOptional(key).map(message -> formatMessage(message, placeholders));
    }
}