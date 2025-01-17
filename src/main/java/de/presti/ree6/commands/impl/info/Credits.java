package de.presti.ree6.commands.impl.info;

import de.presti.ree6.commands.*;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.main.Main;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

@Command(name = "credits", description = "See the beautiful and lovely team working behind Ree6!", category = Category.INFO)
public class Credits implements ICommand {

    @Override
    public void onPerform(CommandEvent commandEvent) {
        Main.getInstance().getCommandManager().sendMessage("Lead Developer : Presti | 平和#0240\nSupport Developer : xazed | xazed#5014\ndavid. | david.#3120", commandEvent.getTextChannel(), commandEvent.getInteractionHook());
    }

    @Override
    public CommandData getCommandData() {
        return null;
    }

    @Override
    public String[] getAlias() {
        return new String[] { "cred" };
    }
}
