package me.releasedsnow.com.glacialshards.ability;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class AbilityListener implements Listener {

    @EventHandler
    public void onShift(PlayerToggleSneakEvent e) {

        if (e.getPlayer().isSneaking()) {
            Player player = e.getPlayer();
            BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);

            if (bendingPlayer.canBend(CoreAbility.getAbility(GlacialShards.class)) && !CoreAbility.hasAbility(player, GlacialShards.class)) {
                new GlacialShards(player);
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        Player player = e.getPlayer();
        BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);
        if (bendingPlayer.canBend(CoreAbility.getAbility(GlacialShards.class))) {
            GlacialShards.throwNextArmorStand(bendingPlayer);
        }
    }
}
