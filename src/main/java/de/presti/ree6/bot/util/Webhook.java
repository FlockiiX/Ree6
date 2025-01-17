package de.presti.ree6.bot.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessage;
import de.presti.ree6.logger.events.LogMessage;
import de.presti.ree6.main.Main;

/**
 * Class to handle Webhook sends.
 */
public class Webhook {

    /**
     * Constructor should not be called, since it is a utility class that doesn't need an instance.
     * @throws IllegalStateException it is a utility class.
     */
    private Webhook() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Send a Webhook-message to the wanted Webhook.
     * @param loggerMessage the MessageContent, if it has been merged.
     * @param message the MessageContent.
     * @param webhookId the ID of the Webhook.
     * @param webhookToken the Auth-Token of the Webhook.
     * @param isLog is the Webhook Message a Log-Message?
     */
    public static void sendWebhook(LogMessage loggerMessage, WebhookMessage message, long webhookId, String webhookToken, boolean isLog) {
        Main.getInstance().getAnalyticsLogger().debug("Received a Webhook to send. (Log-Typ: {})", isLog ? loggerMessage != null ? loggerMessage.getType().name() : "NONE-LOG" : "NONE-LOG");
        // Check if the given data is valid.
        if (webhookToken.contains("Not setup!") || webhookId == 0) return;

        // Check if the given data is in the Database.
        if (isLog && !Main.getInstance().getSqlConnector().getSqlWorker().existsLogData(webhookId, webhookToken)) return;

        // Check if the LoggerMessage is canceled.
        if (isLog && (loggerMessage == null || loggerMessage.isCanceled())) {
            // If so, inform about invalid send.
            Main.getInstance().getLogger().error("[Webhook] Got a Invalid or canceled LoggerMessage!");
            return;
        }

        // Try sending a Webhook to the given data.
        try (WebhookClient wcl = WebhookClient.withId(webhookId, webhookToken)) {
            // Send the message and handle exceptions.
            wcl.send(message).exceptionally(throwable -> {
                // If the error 404 comes that means that the webhook is invalid.
                if (throwable.getMessage().contains("failure 404")) {
                    // Inform and delete invalid webhook.
                    if (isLog) {
                        Main.getInstance().getSqlConnector().getSqlWorker().deleteLogWebhook(webhookId, webhookToken);
                        Main.getInstance().getLogger().error("[Webhook] Deleted invalid Webhook: " + webhookId + " - " + webhookToken);
                    } else {
                        Main.getInstance().getLogger().error("[Webhook] Invalid Webhook: " + webhookId + " - " + webhookToken + ", has not been deleted since it is not a Log-Webhook.");
                    }
                } else if (throwable.getMessage().contains("failure 400")) {
                    // If 404 inform that the Message had an invalid Body.
                    Main.getInstance().getLogger().error("[Webhook] Invalid Body with LogTyp: " + loggerMessage.getType().name());
                }
                return null;
            });
        } catch (Exception ex) {
            // Inform that this is an Invalid Webhook.
            Main.getInstance().getLogger().error("[Webhook] Invalid Webhook: " + webhookId + " - " + webhookToken);
            Main.getInstance().getLogger().error("[Webhook] " +ex.getMessage());
        }
    }
}