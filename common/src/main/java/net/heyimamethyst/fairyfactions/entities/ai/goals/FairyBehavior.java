package net.heyimamethyst.fairyfactions.entities.ai.goals;

import net.heyimamethyst.fairyfactions.FairyConfigValues;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.entities.ai.fairy_job.FairyJobManager;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.pathfinder.Path;

import java.util.List;

public class FairyBehavior
{
    private FairyEntity theFairy;
    protected double speedModifier;

    //public FairyJobManager fairyJobManager;

    public FairyBehavior(FairyEntity theFairy, double speedModifier)
    {
        this.theFairy = theFairy;
        this.speedModifier = speedModifier;
        //fairyJobManager = new FairyJobManager(theFairy);
    }

    public void handleAnger()
    {
        theFairy.setEntityFear(null);

        // Lose interest in an entity that is far away or out of sight over
        // time.
        if (theFairy.getTarget() != null)
        {
            final float enemy_dist = theFairy.distanceTo(theFairy.getTarget());

            if (enemy_dist >= FairyConfigValues.BEHAVIOR_PURSUE_RANGE || ( theFairy.getRandom().nextInt(2) == 0
                    && !theFairy.hasLineOfSight(theFairy.getTarget())))
            {
                //theFairy.loseInterest++;
                theFairy.setLooseInterest(theFairy.getLooseInterest() + 1);

                if (theFairy.getLooseInterest() >= ( theFairy.tamed() ? FairyConfigValues.BEHAVIOR_AGGRO_TIMER
                        : FairyConfigValues.BEHAVIOR_AGGRO_TIMER * 3 ))
                {
                    theFairy.setTarget(null);
                    //theFairy.loseInterest = 0;
                    theFairy.setLooseInterest(0);
                }
            }
            else
            {
                //theFairy.loseInterest = 0;
                theFairy.setLooseInterest(0);
            }

            // Guards can fight for a queen - will make her run away instead
            if (theFairy.guard() && theFairy.getFaction() > 0 && theFairy.getRuler() != null
                    && theFairy.getRuler() instanceof FairyEntity)
            {
                FairyEntity fairy = (FairyEntity)this.theFairy.getRuler();

                if (fairy.getTarget()  != null)
                {
                    float queen_dist = theFairy.distanceTo(fairy);

                    if (queen_dist < FairyConfigValues.BEHAVIOR_DEFEND_RANGE
                            && enemy_dist < FairyConfigValues.BEHAVIOR_DEFEND_RANGE
                            && theFairy.hasLineOfSight(fairy))
                    {
                        theFairy.setTarget(fairy.getTarget());
                        fairy.setTarget(null);
                        fairy.setCryTime(100);
                        fairy.setEntityFear(theFairy.getTarget());
                    }
                }
            }
        }
    }

    public void handleFear()
    {
        if (theFairy.getEntityFear() != null)
        {
            Path currentPath = theFairy.getNavigation().getPath();

            if (!theFairy.getEntityFear().isAlive())
            {
                // Don't fear the dead.
                theFairy.setEntityFear(null);
            }
            else if (currentPath == null && theFairy.hasLineOfSight(theFairy.getEntityFear())
                    && theFairy.willCower())
            {
                float dist = theFairy.distanceTo(theFairy.getEntityFear());

                // Run from entityFear if you can see it and it is close.
                if (dist < FairyConfigValues.BEHAVIOR_FEAR_RANGE)
                {

                    BlockPos pos = theFairy.roamBlockPos(
                            theFairy.getEntityFear().blockPosition().getX(),
                            theFairy.flymode() ? theFairy.blockPosition().getY() : theFairy.getEntityFear().blockPosition().getY(),
                            theFairy.getEntityFear().blockPosition().getZ(), theFairy, theFairy.PATH_AWAY);

                    if (pos != null)
                    {
                        theFairy.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);
                        theFairy.setCryTime(theFairy.getCryTime() + 120);
                    }
                }
            }
        }
    }

    public void handleRuler()
    {
        // TODO: create constants for all of these ranges and time limits

        if (theFairy.getRuler() != null)
        {
            if (theFairy.getRuler().getHealth() <= 0 || theFairy.getRuler().isDeadOrDying())
            {
                // get rid of that ruler.
                theFairy.setRuler(null);
            }
        }

        if (theFairy.getRuler() == null)
        {
            // Looking for a queen to follow.
            if (!theFairy.tamed() && !theFairy.queen())
            {
                //FairyFactions.LOGGER.debug(this.theFairy.toString()+": is looking for a fairy queen to follow");

                double d = 40D;

                if (theFairy.getFaction() == 0)
                {
                    d = 16D;
                }

                List<?> list = theFairy.level().getEntitiesOfClass(FairyEntity.class, theFairy.getBoundingBox().inflate(d, d, d));

                for (int j = 0; j < list.size(); j++)
                {
                    FairyEntity fairy = (FairyEntity) list.get(j);

                    if (fairy != theFairy && fairy.getHealth() > 0
                            && fairy.queen())
                    {
                        if (theFairy.getFaction() > 0
                                && fairy.getFaction() == theFairy.getFaction())
                        {
                            // Fairy finds the queen of its faction, fairly
                            // standard.

                            //FairyFactions.LOGGER.debug(this.theFairy.toString()+": found a its faction ruler");
                            theFairy.setRuler(fairy);
                            break;
                        }
                        else if (theFairy.getFaction() == 0 && fairy.getFaction() > 0
                                && theFairy.hasLineOfSight(fairy))
                        {
                            // A factionless fairy may find a new ruler on its
                            // own.

                            //FairyFactions.LOGGER.debug(this.theFairy.toString()+": found a fairy ruler to follow");

                            theFairy.setRuler(fairy);
                            theFairy.setFaction(fairy.getFaction());

                            break;
                        }
                    }
                }
            }
            else if (theFairy.getFaction() == 0 && theFairy.tamed())
            {
                //FairyFactions.LOGGER.debug(theFairy.toString()+": is looking for a player to follow");

                // Looking for a player to follow.
                List<?> list = theFairy.level().getEntitiesOfClass(Player.class, theFairy.getBoundingBox().inflate(16D, 16D, 16D));

                for (int j = 0; j < list.size(); j++)
                {
                    Player player = (Player) list.get(j);

                    if (player.getHealth() > 0 && theFairy.isRuler(player)
                            && theFairy.hasLineOfSight(player))
                    {
                        //FairyFactions.LOGGER.debug(theFairy.toString()+": found a player ruler to follow");

                        //ruler = player;
                        theFairy.setRuler(player);

                        break;
                    }
                }
            }
        }

        // This makes fairies walk towards their ruler.

        Path currentPath = theFairy.getNavigation().getPath();

        if (theFairy.getRuler() != null && currentPath == null && !theFairy.posted())
        {
            float dist = theFairy.distanceTo(theFairy.getRuler());

            // Guards and Queens walk closer to the player (Medic healing?)
            if (( theFairy.guard() || theFairy.queen() ) && theFairy.hasLineOfSight(theFairy.getRuler()) && dist > 5F
                    && dist < 16F)
            {

                BlockPos targetBlockPos;

                if(theFairy.flymode())
                {
                    targetBlockPos = new BlockPos(theFairy.getRuler().blockPosition().getX(), theFairy.blockPosition().getY(), theFairy.getRuler().blockPosition().getZ());
                }
                else
                {
                    targetBlockPos = theFairy.getRuler().blockPosition();
                }

                theFairy.getNavigation().moveTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), speedModifier);

            }
            else
            {
                if (theFairy.scout() && theFairy.getRuler() instanceof FairyEntity)
                {
                    // Scouts stay way out there on the perimeter.
                    if (dist < 12F)
                    {

                        BlockPos pos = theFairy.roamBlockPos(
                                theFairy.getRuler().blockPosition().getX(),
                                theFairy.flymode() ? theFairy.blockPosition().getY() : theFairy.getRuler().blockPosition().getY(),
                                theFairy.getRuler().blockPosition().getZ(), theFairy, (float) Math.PI);

                        if (pos != null)
                            theFairy.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);
                    }
                    else if (dist > 24F)
                    {

                        BlockPos pos = theFairy.roamBlockPos(
                                theFairy.getRuler().blockPosition().getX(),
                                theFairy.flymode() ? theFairy.blockPosition().getY() : theFairy.getRuler().blockPosition().getY(),
                                theFairy.getRuler().blockPosition().getZ(), theFairy, 0);

                        if (pos != null)
                            theFairy.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);

                    }
                }
                else
                {
                    // Regular fairies stay moderately close.
                    if (dist > FairyConfigValues.TELEPORT_RANGE && theFairy.getRuler() instanceof Player
                            && theFairy.canTeleportToRuler((Player) theFairy.getRuler()))
                    {
                        // Can teleport to the owning player if he has an ender
                        // eye or an ender pearl.
                        theFairy.teleportToRuler(theFairy.getRuler());
                    }
                    else if (dist > ( theFairy.getRuler() instanceof FairyEntity ? 12F : 6F ))
                    {

                        BlockPos pos = theFairy.roamBlockPos(
                                theFairy.getRuler().blockPosition().getX(),
                                theFairy.flymode() ? theFairy.blockPosition().getY() : theFairy.getRuler().blockPosition().getY(),
                                theFairy.getRuler().blockPosition().getZ(), theFairy, 0);

                        if (pos != null)
                            theFairy.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);
                    }
                }
            }

            //FairyFactions.LOGGER.debug(this.theFairy.toString()+": is pathing towards its ruler");
        }

        if (theFairy.getSnowballin() > 0 && theFairy.attackAnim <= 0 && theFairy.getRuler() != null
                && theFairy.getTarget() == null && theFairy.getEntityFear() == null
                && theFairy.getCryTime() == 0)
        {

            float dist = theFairy.distanceTo(theFairy.getRuler());

            if (dist < 10F && theFairy.hasLineOfSight(theFairy.getRuler()))
            {
                theFairy.tossSnowball(theFairy.getRuler());
            }
            else if (currentPath == null && dist < 16F)
            {

                BlockPos pos = theFairy.roamBlockPos(
                        theFairy.getRuler().blockPosition().getX(),
                        theFairy.flymode() ? theFairy.blockPosition().getY() : theFairy.getRuler().blockPosition().getY(),
                        theFairy.getRuler().blockPosition().getZ(), theFairy, 0);

                if (pos != null)
                    theFairy.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);
            }
        }

        if (theFairy.getFaction() > 0)
        {
            // This is a method for making sure that fairies eventually realize
            // they're alone

            boolean flag = false;

            if (!theFairy.queen()
                    && ( theFairy.getRuler() == null || theFairy.distanceTo(theFairy.getRuler()) > 40F ))
            {
                // If a follower has lost its leader
                flag = true;
            }
            else if (theFairy.queen())
            {
                // If a leader has lost her followers

                //flag = true;
                List<?> list = theFairy.level().getEntitiesOfClass(FairyEntity.class,
                        theFairy.getBoundingBox().expandTowards(40D, 40D, 40D));

//                if(list.size() == 1)
//                    flag = true;

                for (int j = 0; j < list.size(); j++)
                {
                    FairyEntity fairy = (FairyEntity) list.get(j);

                    if (fairy != theFairy && FairyUtils.sameTeam(fairy, theFairy)
                            && fairy.getHealth() > 0)
                    {
                        flag = false;
                        break;
                    }
                    else if ( list == null || list.size() < 1 )
                    {
                        flag = true;
                    }
                }
            }
            else if (theFairy.getRuler() != null && theFairy.getRuler() instanceof FairyEntity)
            {
                // If a fairy queen was tamed in peaceful mode
                FairyEntity fairy = (FairyEntity) theFairy.getRuler();

                if (!FairyUtils.sameTeam(theFairy, fairy))
                {
                    flag = true;

                    if (theFairy.getLoseTeam() < 65)
                    {
                        //theFairy.loseTeam = 65 + theFairy.getRandom().nextInt(8);
                        theFairy.setLoseTeam(65 + theFairy.getRandom().nextInt(8));
                    }
                }
            }

            if (flag)
            {
                // Takes a while for it to take effect.

                theFairy.setLoseTeam(theFairy.getLoseTeam() + 1);
                //++theFairy.loseTeam;
                //theFairy.loseTeam++;

                if (theFairy.getLoseTeam() >= 75)
                {
                    theFairy.setRuler(null);
                    theFairy.disband("(lost its team)");
                    //theFairy.loseTeam = 0;
                    theFairy.setLoseTeam(0);
                    theFairy.setCryTime(0);

                    //theFairy.getNavigation().stop();
                    theFairy.getNavigation().moveTo((Path)null, speedModifier);
                }
            }
            else
            {
                //theFairy.loseTeam = 0;
                theFairy.setLoseTeam(0);
            }
        }
    }

    // This handles actions concerning teammates and entities attacking their
    // ruler.
    public void handleSocial()
    {
        if (theFairy.getRandom().nextBoolean())
        {
            return;
        }

        List list = theFairy.level().getEntities(theFairy,
                theFairy.getBoundingBox().inflate(16D, 16D, 16D));

        FairyUtils.shuffle(list, theFairy.getRandom());

        for (int j = 0; j < list.size(); j++)
        {
            Entity entity = (Entity) list.get(j);

            if (entity != theFairy && theFairy.hasLineOfSight(entity) && entity.isAlive())
            {
                Path currentPath = theFairy.getNavigation().getPath();

                if (( theFairy.getRuler() != null || theFairy.queen() )
                        && entity instanceof FairyEntity
                        && FairyUtils.sameTeam(theFairy, (FairyEntity) entity))
                {
                    FairyEntity fairy = (FairyEntity) list.get(j);

                    if (fairy.getHealth() > 0)
                    {
                        Entity scary = (Entity) null;

                        if (fairy.getEntityFear() != null)
                        {
                            scary = fairy.getEntityFear();
                        }
                        else if (fairy.getTarget() != null)
                        {
                            scary = fairy.getTarget();
                        }

                        if (scary != null)
                        {
                            float dist = theFairy.distanceTo(scary);

                            if (dist > 16F || !theFairy.hasLineOfSight(scary))
                            {
                                scary = null;
                            }
                        }

                        if (scary != null)
                        {
                            if (theFairy.willCower())
                            {
                                if (fairy.getTarget() == scary
                                        && theFairy.hasLineOfSight(scary))
                                {
                                    theFairy.setCryTime(120);
                                    theFairy.setEntityFear(scary);

                                    BlockPos pos = theFairy.roamBlockPos(
                                            entity.blockPosition().getX(),
                                            theFairy.flymode() ? theFairy.blockPosition().getY() : entity.blockPosition().getY(),
                                            entity.blockPosition().getZ(), theFairy, (float) Math.PI);

                                    if (pos != null)
                                        theFairy.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);

                                    break;
                                }
                                else if (fairy.getCryTime() > 60)
                                {
                                    theFairy.setCryTime(Math.max(fairy.getCryTime() - 60, 0));
                                    theFairy.setEntityFear(scary);

                                    BlockPos pos = theFairy.roamBlockPos(
                                            entity.blockPosition().getX(),
                                            theFairy.flymode() ? theFairy.blockPosition().getY() : entity.blockPosition().getY(),
                                            entity.blockPosition().getZ(), theFairy, (float) Math.PI);

                                    if (pos != null)
                                        theFairy.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);

                                    break;
                                }
                            }
                            else
                            {
                                theFairy.setTarget((LivingEntity) scary);
                                break;
                            }
                        }
                    }
                }
                else if (theFairy.getRuler() != null && ( theFairy.guard() || theFairy.queen() )
                        && entity instanceof Mob
                        && !( entity instanceof Creeper)
                        && ( !( entity instanceof Animal)
                        || ( !FairyUtils.peacefulAnimal(
                        (Animal) entity) ) ))
                {
                    // Guards proactivley seeking holstile enemies. Will add
                    // slimes? Maybe dunno.
                    Mob creature = (Mob) entity;

                    if (creature.getHealth() > 0
                            && creature.getTarget() != null
                            && creature.getTarget() == theFairy.getRuler())
                    {
                        theFairy.setTarget(creature);
                        break;
                    }
                }
                else if (entity instanceof PrimedTnt && currentPath == null)
                {
                    // Running away from lit TNT.
                    float dist = theFairy.distanceTo(entity);

                    if (dist < 8F)
                    {

                        BlockPos pos = theFairy.roamBlockPos(
                                entity.blockPosition().getX(),
                                theFairy.flymode() ? theFairy.blockPosition().getY() : entity.blockPosition().getY(),
                                entity.blockPosition().getZ(), theFairy, (float) Math.PI);

                        if (pos != null)
                        {
                            theFairy.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);

                            if (!theFairy.flymode())
                            {
                                theFairy.setFlymode(true);
                                theFairy.setJumping(true);
                                theFairy.setFlyTime(100);
                            }

                            break;
                        }
                    }
                }
            }
        }
    }

    // This handles actions of the medics.
    public void handleHealing()
    {
        if (theFairy.getHealTime() > 0)
        {
            //FairyFactions.LOGGER.debug("returning healtime is greater than 0");
            return;
        }

        if (theFairy.entityHeal != null)
        {
            Path currentPath = theFairy.getNavigation().getPath();

            if (theFairy.entityHeal.getHealth() <= 0 || theFairy.entityHeal.isDeadOrDying())
            {
                //FairyFactions.LOGGER.debug("removing entityHeal because entityHeal is dead");
                theFairy.entityHeal = null;
            }
            else if (currentPath == null)
            {

                Path dest = theFairy.getNavigation().createPath(theFairy.entityHeal.blockPosition(), 1, 16);

                if (dest != null)
                {
                    theFairy.getNavigation().moveTo(dest, speedModifier);
                }
                else
                {
                    theFairy.entityHeal = null;
                }

            }
            else
            {
                float g = theFairy.distanceTo(theFairy.entityHeal);

                if (g < 2.5F && theFairy.hasLineOfSight(theFairy.entityHeal))
                {
                    //FairyFactions.LOGGER.debug("healing entityHeal");
                    theFairy.doHeal(theFairy.entityHeal);
                    theFairy.entityHeal = null;
                }
            }
        }

        if (theFairy.entityHeal == null && theFairy.getHealTime() <= 0)
        {
            List list = theFairy.level().getEntities(theFairy,
                    theFairy.getBoundingBox().inflate(16D, 16D, 16D));

            for (int j = 0; j < list.size(); j++)
            {
                Entity entity = (Entity) list.get(j);

                if (entity != theFairy && theFairy.hasLineOfSight(entity) && entity.isAlive())
                {
                    if (entity instanceof FairyEntity)
                    {
                        FairyEntity fairy = (FairyEntity) list.get(j);

                        if (fairy.getHealth() > 0 && FairyUtils.sameTeam(theFairy, fairy)
                                && fairy.getHealth() < fairy.getMaxHealth())
                        {
                            //FairyFactions.LOGGER.debug("setting entityHeal to ruler a fairy");
                            theFairy.entityHeal = fairy;
                            Path dest =  theFairy.getNavigation().createPath(theFairy.entityHeal.blockPosition(), 1, 16);

                            if (dest != null)
                            {
                                theFairy.getNavigation().moveTo(dest, speedModifier);
                            }

                            break;
                        }
                    }
                    else if (entity instanceof LivingEntity && theFairy.getRuler() != null
                            && ( (LivingEntity) entity ) == theFairy.getRuler())
                    {
                        if (theFairy.getRuler().getHealth() > 0
                                && theFairy.getRuler().getHealth() < theFairy.getRuler().getMaxHealth())
                        {
                            //FairyFactions.LOGGER.debug("setting entityHeal to ruler");
                            theFairy.entityHeal = theFairy.getRuler();

                            Path dest =  theFairy.getNavigation().createPath(theFairy.entityHeal.blockPosition(), 1, 16);

                            if (dest != null)
                            {
                                theFairy.getNavigation().moveTo(dest, speedModifier);
                            }

                            break;
                        }
                    }
                }
            }

            if (theFairy.entityHeal == null && theFairy.getHealth() < theFairy.getMaxHealth())
            {
                //FairyFactions.LOGGER.debug("healing self");
                theFairy.doHeal(theFairy);
            }
        }
    }

    // A handler specifically for the rogue class.
    public void handleRogue()
    {
        if (theFairy.getRandom().nextBoolean())
        {
            return;
        }

        List list = theFairy.level().getEntities(theFairy,
                theFairy.getBoundingBox().inflate(16D, 16D, 16D));
        FairyUtils.shuffle(list, theFairy.getRandom());

        for (int j = 0; j < list.size(); j++)
        {
            Entity entity = (Entity) list.get(j);

            Path currentPath = theFairy.getNavigation().getPath();

            if (entity != theFairy && theFairy.hasLineOfSight(entity) && entity.isAlive())
            {
                if (( theFairy.getRuler() != null || theFairy.queen() )
                        && entity instanceof FairyEntity
                        && FairyUtils.sameTeam(theFairy, (FairyEntity) entity))
                {
                    FairyEntity fairy = (FairyEntity) list.get(j);

                    if (fairy.getHealth() > 0)
                    {
                        Entity scary = (Entity) null;

                        if (fairy.getEntityFear() != null)
                        {
                            scary = fairy.getEntityFear();
                        }
                        else if (fairy.getTarget() != null)
                        {
                            scary = fairy.getTarget();
                        }

                        if (scary != null)
                        {
                            float dist = theFairy.distanceTo(scary);

                            if (dist > 16F || !theFairy.hasLineOfSight(scary))
                            {
                                scary = null;
                            }
                        }

                        if (scary != null)
                        {
                            if (theFairy.canHeal())
                            {
                                if (fairy.getTarget() == scary
                                        && theFairy.hasLineOfSight(scary))
                                {
                                    theFairy.setCryTime(120);
                                    theFairy.setEntityFear(scary);

                                    BlockPos pos = theFairy.roamBlockPos(
                                            entity.blockPosition().getX(),
                                            theFairy.flymode() ? theFairy.blockPosition().getY() : entity.blockPosition().getY(),
                                            entity.blockPosition().getZ(), theFairy, (float) Math.PI);

                                    if (pos != null)
                                        theFairy.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);

                                    break;
                                }
                                else if (fairy.getCryTime() > 60)
                                {
                                    theFairy.setCryTime(Math.max(fairy.getCryTime() - 60,
                                            0));
                                    theFairy.setEntityFear(scary);

                                    BlockPos pos = theFairy.roamBlockPos(
                                            entity.blockPosition().getX(),
                                            theFairy.flymode() ? theFairy.blockPosition().getY() : entity.blockPosition().getY(),
                                            entity.blockPosition().getZ(), theFairy, (float) Math.PI);

                                    if (pos != null)
                                        theFairy.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);

                                    break;
                                }
                            }
                            else
                            {
                                theFairy.setTarget((LivingEntity) scary);
                                break;
                            }
                        }
                    }
                }
                else if (theFairy.getRuler() != null && theFairy.canHeal()
                        && entity instanceof Mob
                        && !( entity instanceof Creeper)
                        && ( !( entity instanceof Animal)
                        || ( !FairyUtils.peacefulAnimal(
                        (Animal) entity) ) ))
                {
                    Mob creature = (Mob) entity;

                    if (creature.getHealth() > 0
                            && creature.getTarget() != null
                            && creature.getTarget() == theFairy.getRuler())
                    {
                        theFairy.setTarget(creature);
                        break;
                    }
                }
                else if (entity instanceof PrimedTnt && currentPath == null)
                {
                    // Running away from lit TNT.
                    float dist = theFairy.distanceTo(entity);

                    if (dist < 8F)
                    {

                        BlockPos pos = theFairy.roamBlockPos(
                                entity.blockPosition().getX(),
                                theFairy.flymode() ? theFairy.blockPosition().getY() : entity.blockPosition().getY(),
                                entity.blockPosition().getZ(), theFairy, (float) Math.PI);

                        if (pos != null)
                        {
                            theFairy.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);

                            if (!theFairy.flymode())
                            {
                                theFairy.setFlymode(true);
                                theFairy.setJumping(true);
                                theFairy.setFlyTime(100);
                            }

                            break;
                        }
                    }
                }
            }
        }
    }

    public void handlePosted(Level level, boolean flag)
    {
        //FairyFactions.LOGGER.debug(this.theFairy.toString()+": this fairy's posted value is " + theFairy.posted());

		/*
		if( tamed() ) {
			System.out.println("handlePosted("+flag+") - "+postedCount+" - "+this);
		}
		*/

        if (!theFairy.tamed() || theFairy.getFaction() > 0 /*|| theFairy.postedCount <= ( theFairy.posted() ? 2 : 5 )*/)
        {
            theFairy.postedCount++;
            return; // Avoid processing too often or when not necessary.
        }

        theFairy.postedCount = 0;
        boolean farFlag = false;

        if (theFairy.postY > -1)
        {
            if (theFairy.getVehicle() != null && theFairy.getRuler() != null
                    && theFairy.getVehicle() == theFairy.getRuler())
            {
                theFairy.abandonPost();

                return; // When a the player takes a tamed fairy away, it
                // automatically cancels the post.
            }

            Block block = level.getBlockState(new BlockPos(theFairy.postX, theFairy.postY, theFairy.postZ)).getBlock();

            if (block == null || !( block instanceof SignBlock))
            {
                //FairyFactions.LOGGER.debug(this.theFairy.toString()+": abandoning post (block not instance of sign block)");
                // If the saved position is not a sign block.
                theFairy.abandonPost();
            }
            else
            {
                BlockEntity tileentity = level.getBlockEntity(new BlockPos(theFairy.postX, theFairy.postY, theFairy.postZ));

                if (tileentity == null
                        || !( tileentity instanceof SignBlockEntity))
                {
                    //FairyFactions.LOGGER.debug(this.theFairy.toString()+": abandoning post (tile eneity not instance of SignBlockEntity)");
                    // Make sure the tile entity is right
                    theFairy.abandonPost();
                }
                else
                {
                    SignBlockEntity sign = (SignBlockEntity) tileentity;
                    if (!theFairy.mySign(sign))
                    {
                        //FairyFactions.LOGGER.debug(this.theFairy.toString()+": abandoning post (sign text does not match name)");
                        // Make sure the name still matches
                        theFairy.abandonPost();
                    }
                    else if (theFairy.canRoamFar(sign))
                    {
                        farFlag = true;
                    }
                }
            }
        }
        else
        {

            // Try to find a post. The ruler has to be nearby for it to work.
            if (theFairy.getRuler() != null
                    && ( theFairy.getVehicle() == null || theFairy.getVehicle() != theFairy.getRuler() )
                    && theFairy.distanceTo(theFairy.getRuler()) <= 64F
                    && theFairy.hasLineOfSight(theFairy.getRuler()))
            {
                //FairyFactions.LOGGER.debug(this.theFairy.toString()+": is finding a post");

                // Gets the fairy's relative position
                int aa = (int)Math.floor(theFairy.position().x);
                int bb = (int)Math.floor(theFairy.getBoundingBox().minY);
                int cc = (int)Math.floor(theFairy.position().z);

                for (int i = 0; i < 245; i++)
                {
                    int x = -3 + ( i % 7 ); // Look around randomly.
                    int y = -2 + ( i / 49 );
                    int z = -3 + ( ( i / 7 ) % 7 );

                    if (Math.abs(x) == 3 && Math.abs(z) == 3)
                    {
                        continue;
                    }

                    x += aa;
                    y += bb;
                    z += cc;

                    if (y >= 0 && y < level.getHeight())
                    {
                        final Block block = level.getBlockState(new BlockPos(x, y, z)).getBlock();

                        if (isSign(block))
                        {
                            BlockEntity tileentity = level.getBlockEntity(new BlockPos(x, y, z));

                            if (tileentity != null && tileentity instanceof SignBlockEntity)
                            {
                                SignBlockEntity sign = (SignBlockEntity) tileentity;

                                if (theFairy.mySign(sign))
                                {
                                    //FairyFactions.LOGGER.debug(this.theFairy.toString()+": assigned post");

                                    theFairy.postX = x;
                                    theFairy.postY = y;
                                    theFairy.postZ = z;
                                    theFairy.setPosted(true);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!flag) // Processes fishing, then returns, if sitting.
        {
            if (theFairy.getFishEntity() != null)
            {
                if (theFairy.getFishEntity().gotBite())
                {
                    //theFairy.gotCatch = true;
                    theFairy.castRod();
                    theFairy.attackAnim = 10;
                }
                else if (theFairy.getRandom().nextFloat() < 0.1F)
                {
                    // TODO: handle packet
                    /*
                     * mod_FairyMod.setPrivateValueBoth( EntityLiving.class,
                     * this, "currentTarget", "ay", fishEntity );
                     */
                    //theFairy.numTicksToChaseTarget = 10 + theFairy.getRandom().nextInt(20);
                }
            }
            else if (theFairy.getRandom().nextInt(2) == 0)
            {
                new FairyJobManager(theFairy).sittingFishing(level);
            }

            return;
        }

        if (theFairy.posted() && (theFairy.getHeldItem() == null || theFairy.getHeldItem().isEmpty()) && !theFairy.angry() && !theFairy.crying())
        {
            double aa = (double) theFairy.postX + 0.5D;
            double bb = (double) theFairy.postY + 0.5D;
            double cc = (double) theFairy.postZ + 0.5D;
            double dd = theFairy.position().x - aa;
            double ee = theFairy.getBoundingBox().minY - bb;
            double ff = theFairy.position().z - cc;
            double gg = Math.sqrt(( dd * dd ) + ( ee * ee ) + ( ff * ff ));

            if (gg >= ( farFlag ? 12D : 6D ))
            {

                BlockPos pos = theFairy.roamBlockPos(aa, bb, cc, theFairy, 0F);

                if (pos != null)
                    theFairy.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);
            }
        }

        if (theFairy.posted() && !theFairy.isSleeping())
        {
            new FairyJobManager(theFairy).discover(level);
        }
    }

    private boolean isSign(Block block)
    {
        return block instanceof SignBlock;
    }
}
