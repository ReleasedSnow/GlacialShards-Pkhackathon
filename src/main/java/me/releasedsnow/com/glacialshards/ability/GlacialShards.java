package me.releasedsnow.com.glacialshards.ability;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.IceAbility;

import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;
import me.releasedsnow.com.glacialshards.ConfigManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class GlacialShards extends IceAbility implements AddonAbility {



    private static final boolean plantBending = ConfigManager.getConfig().getBoolean("Abilities.Ice.GlacialShards.plantBending");
    private static final long Cooldown = ConfigManager.getConfig().getLong("Abilities.Ice.GlacialShards.Cooldown");
    private static final int freezeTics = ConfigManager.getConfig().getInt("Abilities.Ice.GlacialShards.freezeTics");
    private static final double Duration = ConfigManager.getConfig().getDouble("Abilities.Ice.GlacialShards.Duration");
    private static final double range = ConfigManager.getConfig().getDouble("Abilities.Ice.GlacialShards.Range");
    private static final double damage = ConfigManager.getConfig().getDouble("Abilities.Ice.GlacialShards.Damage");
    private static final double speed = ConfigManager.getConfig().getDouble("Abilities.Ice.GlacialShards.Speed");
    private static final String color = ConfigManager.getConfig().getString("Abilities.Ice.GlacialShards.Color");
    private static final double sourceRange = ConfigManager.getConfig().getDouble("Abilities.Ice.GlacialShards.sourceRange");

    ArmorStand[] armorStands;
    int currentArmorStandIndex;
    Set<Entity> damagedentities = new HashSet<>();
    HashMap<Player, ArmorStand[]> armorStandPlayerHashMap = new HashMap<>();
    double angle;





    public GlacialShards(Player player) {
        super(player);

        if (!bPlayer.canBend(this)) {
            remove();
            return;
        }

        Block water_source = getWaterSourceBlock(player, sourceRange, plantBending);
        if (water_source != null) {
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SNOW_BREAK, 4, 3);
            this.angle = 0.0;
            armorStands = new ArmorStand[3];
            currentArmorStandIndex = 0;
            createArmorStands();
            setIce();
            start();
        }



    }


    private void createArmorStands() {
        Location location = player.getLocation();

        for (int i = 0; i < armorStands.length; i++) {
            ItemStack skullItem = new ItemStack(Material.PACKED_ICE);
            Location armorStandLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
            ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(armorStandLocation, EntityType.ARMOR_STAND);
            armorStand.setInvisible(true);
            armorStand.setGravity(false);
            armorStand.setInvulnerable(true);
            armorStand.setCollidable(false);
            armorStand.setBasePlate(false);
            armorStand.setSmall(true);
            armorStand.getEquipment().setHelmet(skullItem);
            armorStand.setCanPickupItems(false);
            armorStand.setRemoveWhenFarAway(false);

            armorStands[i] = armorStand;
            armorStandPlayerHashMap.put(player, armorStands);
        }
    }

    public void setIce() {
        Location location =player.getLocation().subtract(0, 1, 0);
        for (Block iceblock : GeneralMethods.getBlocksAroundPoint(location, 5)) {
            if (!iceblock.getType().isAir()) {
                new TempBlock(iceblock, Material.FROSTED_ICE.createBlockData(), 8000);
            }

        }


    }
    public void throwNextArmorStand() {

        damagedentities.clear();

        ArmorStand[] armorStands1 = armorStandPlayerHashMap.get(player);
        ArmorStand armorStand = armorStands1[currentArmorStandIndex];
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        Vector velocity = direction.clone().normalize().multiply(speed);
        armorStands[currentArmorStandIndex] = null;
        armorStand.setCollidable(true);
        armorStand.setGravity(true);
        armorStand.teleport(eyeLocation);
        player.getWorld().playSound(eyeLocation, Sound.BLOCK_GLASS_BREAK, 3, 3);

        BukkitRunnable task = new BukkitRunnable() {
            double distance = 0.0;
            @Override
            public void run() {
                if (distance >= range/speed) {
                    armorStand.setVelocity(velocity);
                    checkCollision(armorStand);
                    armorStand.remove();
                    damagedentities.clear();
                    this.cancel();
                } else {
                    armorStand.setVelocity(velocity);
                    player.getWorld().spawnParticle(Particle.BLOCK_DUST, armorStand.getLocation(), 3, Material.PACKED_ICE.createBlockData());
                    if (ThreadLocalRandom.current().nextInt(2) == 0) {
                        player.getWorld().spawnParticle(Particle.END_ROD, armorStand.getLocation(), 1, .3, .3, .3, .0);
                    }
                    checkCollision(armorStand);
                }
                distance += 1;
            }
        };
        task.runTaskTimer(ProjectKorra.plugin, 0, 1);
        currentArmorStandIndex++;
        if (currentArmorStandIndex == 3) {
            bPlayer.addCooldown(this);
            remove();
        }
    }

    private void checkCollision(ArmorStand armorStand) {
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(armorStand.getLocation(), 1)) {
            if (!(entity instanceof ArmorStand) && entity instanceof LivingEntity) {
                if (entity.getUniqueId() != player.getUniqueId() && !damagedentities.contains(entity)) {
                    DamageHandler.damageEntity(entity, player, damage, CoreAbility.getAbility(GlacialShards.class));
                    entity.setFreezeTicks(freezeTics);
                    damagedentities.add(entity);
                    iceHit(entity.getLocation());
                    armorStand.remove();
                    break;
                }
            }
        }

    }

    private void iceHit(Location location) {
        for (Block block : GeneralMethods.getBlocksAroundPoint(location, 1.5)) {
            new TempBlock(block, Material.ICE.createBlockData(), 500);
        }

    }

    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= Duration) {
            bPlayer.addCooldown(this);
            remove();
            return;
        }

        for (ArmorStand armorStand : armorStands) {
            if (armorStand != null) {
            armorStand.setInvulnerable(true);
        }}

        Location location = player.getLocation();

        for (double i = 0; i < 360 ; i+= 5) {
            double x = Math.cos(Math.toRadians(i));
            double z = Math.sin(Math.toRadians(i));
            Location particleLocation = location.clone().add(x * 1.5, 1, z * 1.5);
            if (ThreadLocalRandom.current().nextInt(12) == 0) {
                player.getWorld().spawnParticle(Particle.SNOW_SHOVEL, particleLocation, 2, .1, .1, .1);
                if (color != null) {
                    GeneralMethods.displayColoredParticle(color, particleLocation, 1, .2, .2, .2);
                }else System.out.println("Please add a hex colour to the config.yml");

            }
        }

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 5)) {
            if (entity != null && entity.getUniqueId() != player.getUniqueId() && entity instanceof LivingEntity) {
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 2));
            }
        }

        for (Location circle : GeneralMethods.getCircle(player.getLocation().add(0, 5, 0), 5, 2, false, false, 0)) {
            player.getWorld().spawnParticle(Particle.WHITE_ASH, circle, 1, .2, .2, .2, .05);


        }
        rotateArmorStands();
    }

    private void rotateArmorStands() {
        double angleOffset = (2 * Math.PI) / armorStandPlayerHashMap.get(player).length;
        double rotationAngle = angle + Math.PI;
        Location eyeLocation = player.getEyeLocation();
        Location centerLocation = eyeLocation.clone();

        for (int i = 0; i < armorStandPlayerHashMap.get(player).length; i++) {
            ArmorStand armorStand = armorStands[i];

            if (armorStand != null) {
                double offsetX = 2 * Math.cos(rotationAngle + i * angleOffset);
                double offsetZ = 2 * Math.sin(rotationAngle + i * angleOffset);
                Location newLocation = new Location(centerLocation.getWorld(), centerLocation.getX() + offsetX, centerLocation.getY() - 1, centerLocation.getZ() + offsetZ);

                armorStand.teleport(newLocation);
            }
            angle += Math.PI / 40;
            if (angle >= 2 * Math.PI) {
                angle = 0.0;
            }
        }
    }

    @Override
    public void remove() {
        for (ArmorStand stand : armorStandPlayerHashMap.get(player)) {
            if (stand != null) {
                stand.remove();
            }
        }
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
        return "GlacialShards";
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
    public String getDescription() {
        return "Hurl large shards of ice at your opponents, and freeze the environment around you!";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
