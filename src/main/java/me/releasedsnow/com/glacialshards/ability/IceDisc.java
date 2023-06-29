package me.releasedsnow.com.glacialshards.ability;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.IceAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;

import me.releasedsnow.com.glacialshards.ConfigManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;


public class IceDisc extends IceAbility implements AddonAbility {


    private enum State {
        SOURCE_SELECTED, DISC
    }

    private static final double Speed = ConfigManager.getConfig().getDouble("Abilities.Ice.IceDisc.Speed");
    private static final double Range = ConfigManager.getConfig().getDouble("Abilities.Ice.IceDisc.Range");
    private static final long Cooldown = ConfigManager.getConfig().getLong("Abilities.Ice.IceDisc.Cooldown");
    private static final double Damage = ConfigManager.getConfig().getDouble("Abilities.Ice.IceDisc.Damage");
    private static final double Duration = ConfigManager.getConfig().getDouble("Abilities.Ice.IceDisc.Duration");
    private static final int freezeTics = ConfigManager.getConfig().getInt("Abilities.Ice.IceDisc.freezeTics");


    State state;
    Block sourceBlock;
    Block discSource;
    Vector direction;
    Location center;
    double distanceTravelled;
    Set<Entity> damageEntity = new HashSet<>();
    TempBlock tempBlock;
    public IceDisc(Player player) {
        super(player);

        Block block = getIceSourceBlock(5);
        if (block == null) {
            return;
        }
        sourceBlock = block;
        distanceTravelled = 0;
        state = State.SOURCE_SELECTED;

        start();
    }

    @Override
    public void progress() {
        if (!bPlayer.canBend(this)) {
            remove();
            return;
        }
        if (System.currentTimeMillis() - getStartTime() >= Duration) {
            remove();
            return;
        }
        switch (state) {
            case SOURCE_SELECTED:
                progressSourceSelected();
                break;
            case DISC:
                createDisc();
                break;
        }
    }

    public void onClick(Block clicked) {
        if (state.equals(State.SOURCE_SELECTED)) {
            if (clicked.equals(discSource)) {
                discSource = clicked;
                state = State.DISC;
            }
        }
    }

    public void progressSourceSelected() {
        Block raised = sourceBlock.getRelative(BlockFace.UP);
        tempBlock = new TempBlock(raised, Material.ICE.createBlockData(), 8000);
        discSource = raised;
    }

    public void createDisc() {
        if (center == null) {
           center = discSource.getLocation().add(.5, 1, .5);
        }
        for (double i= 0 ; i <= 360 ; i += 5) {
            double x = Math.cos(Math.toRadians(i));
            double z = Math.sin(Math.toRadians(i));
            Location circle = center.clone().add(x * 0.5, 0, z * 0.5);
            player.getWorld().spawnParticle(Particle.BLOCK_DUST, circle, 1, Material.ICE.createBlockData());
            GeneralMethods.displayColoredParticle("#b6e3ee", circle, 1, .05, .05, .05);

        }
        if (distanceTravelled / Speed >= Range) {
            remove();
            return;
        }

        distanceTravelled ++;
        direction = player.getEyeLocation().getDirection();
        center.add(direction.multiply(Speed));
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SNOW_BREAK, 2, 2);
        checkCollisions(center);
    }

    public void checkCollisions(Location location) {
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 1)) {
            if (!(entity instanceof ArmorStand) && entity instanceof LivingEntity) {
                if (entity.getUniqueId() != player.getUniqueId() && !damageEntity.contains(entity)) {
                    DamageHandler.damageEntity(entity, player, Damage, this);
                    entity.setFreezeTicks(freezeTics);
                    entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_GLASS_BREAK, 3, 3);
                    damageEntity.add(entity);
                    remove();
                    return;
                }}}

    }
    @Override
    public void remove() {
        tempBlock.setType(Material.AIR.createBlockData());
        bPlayer.addCooldown(this);
        super.remove();
    }
    @Override
    public boolean isSneakAbility() {
        return true;
    }
    @Override
    public boolean isHarmlessAbility() {
        return false;
    }
    @Override
    public long getCooldown() {
        return Cooldown;
    }
    @Override
    public String getName() {
        return "IceDisc";
    }
    @Override
    public Location getLocation() {
        return null;
    }
    @Override
    public void load() {

    }
    @Override
    public void stop() {

    }
    @Override
    public String getAuthor() {
        return "ReleasedSnow";
    }
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "Create a block of solid ice and throw sharp discs of ice through the air at your enemies.";
    }

    @Override
    public String getInstructions() {
        return "Tap sneak at an ice-bendable block, and once the ice has appeared click it to shoot a disc of ice.";
    }
}
