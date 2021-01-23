package net.lldv.llamavote.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.ConfigSection;
import net.lldv.llamavote.LlamaVote;
import net.lldv.llamavote.components.language.Language;
import org.apache.commons.codec.language.bm.Lang;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LlamaDevelopment
 * @project MobEarn
 * @website http://llamadevelopment.net/
 */
public class VoteCommand extends PluginCommand<LlamaVote> {

    private final Map<String, Long> cooldown = new HashMap<>();
    private final NumberFormat numberFormat;

    public VoteCommand(final LlamaVote plugin, final ConfigSection section) {
        super(section.getString("Name"), plugin);
        setDescription(section.getString("Description"));
        setUsage(section.getString("Usage"));
        setAliases(section.getStringList("Aliases").toArray(new String[]{}));
        this.numberFormat = NumberFormat.getInstance();
        this.numberFormat.setMaximumFractionDigits(1);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;

            if (this.cooldown.containsKey(sender.getName())) {
                final long until = this.cooldown.get(sender.getName());

                if (System.currentTimeMillis() > until) {
                    this.cooldown.put(sender.getName(), System.currentTimeMillis() + this.getPlugin().getCooldown() * 1000L);
                } else {
                    player.sendMessage(Language.get("command.cooldown", numberFormat.format((until - System.currentTimeMillis()) / 1000)));
                    return false;
                }
            } else {
                this.cooldown.put(sender.getName(), System.currentTimeMillis() + this.getPlugin().getCooldown() * 1000L);
            }

            this.getPlugin().getVote(sender.getName(), (code) -> {
                switch (code) {
                    case "0":
                        sender.sendMessage(Language.get("not.voted"));
                        break;
                    case "1":
                        this.getPlugin().setVoted(sender.getName());
                        this.getPlugin().reward(player);
                        sender.sendMessage(Language.getNP("player.vote"));
                        break;
                    case "2":
                        sender.sendMessage(Language.get("already.voted"));
                        break;
                }
            });
        }
        return false;
    }
}
