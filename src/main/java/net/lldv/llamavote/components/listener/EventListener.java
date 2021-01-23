package net.lldv.llamavote.components.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import lombok.RequiredArgsConstructor;
import net.lldv.llamavote.LlamaVote;
import net.lldv.llamavote.components.language.Language;

/**
 * @author LlamaDevelopment
 * @project LlamaVote
 * @website http://llamadevelopment.net/
 */
@RequiredArgsConstructor
public class EventListener implements Listener {

    private final LlamaVote plugin;

    @EventHandler
    public void on(final PlayerJoinEvent event) {
        if (!this.plugin.isCheckOnJoin()) return;
        this.plugin.getServer().getScheduler().scheduleDelayedTask(this.plugin, () -> {
            this.plugin.getVote(event.getPlayer().getName(), (code) -> {
                if (code.equals("1")) {
                    this.plugin.setVoted(event.getPlayer().getName());
                    this.plugin.reward(event.getPlayer());
                    event.getPlayer().sendMessage(Language.getNP("player.vote"));
                }
            });
        }, 100);
    }

}
