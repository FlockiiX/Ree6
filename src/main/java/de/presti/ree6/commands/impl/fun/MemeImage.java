package de.presti.ree6.commands.impl.fun;

import com.google.gson.JsonObject;
import de.presti.ree6.bot.BotWorker;
import de.presti.ree6.commands.Category;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.main.Data;
import de.presti.ree6.main.Main;
import de.presti.ree6.utils.external.RequestUtility;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

@Command(name = "randommeme", description = "Wanna see a Meme?", category = Category.FUN)
public class MemeImage implements ICommand {

    @Override
    public void onPerform(CommandEvent commandEvent) {

        JsonObject js = RequestUtility.request(new RequestUtility.Request("https://meme-api.herokuapp.com/gimme")).getAsJsonObject();

        EmbedBuilder em = new EmbedBuilder();

        em.setTitle("Random Meme Image!");
        em.setColor(BotWorker.randomEmbedColor());

        if (js.has("url")) {
            em.setImage(js.get("url").getAsString());
        } else {
            em.setDescription("Couldn't get the Image!");
        }

        em.setFooter("Requested by " + commandEvent.getMember().getUser().getAsTag() + " - " + Data.ADVERTISEMENT, commandEvent.getMember().getUser().getAvatarUrl());
        Main.getInstance().getCommandManager().sendMessage(em, commandEvent.getTextChannel(), commandEvent.getInteractionHook());

    }

    @Override
    public CommandData getCommandData() {
        return null;
    }

    @Override
    public String[] getAlias() {
        return new String[]{"meme", "memeimage"};
    }
}
