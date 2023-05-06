package net.heyimamethyst.fairyfactions.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class EntityHelper
{
    //These methods came from here:https://github.com/V0idWa1k3r/ExPetrum/blob/master/src/main/java/v0id/exp/util/Helpers.java#L123

    public static <T extends Entity> T getClosest(List<T> entities, Entity to)
    {
        T ret = null;
        double dist = Double.MAX_VALUE;
        for (T t : entities)
        {
            double distance = t.distanceTo(to);
            if (distance < dist)
            {
                dist = distance;
                ret = t;
            }
        }

        return ret;
    }

    public static <T extends Entity> List<T> rayTraceEntities(Level w, Vec3 pos, Vec3 ray, Optional<Predicate<T>> entityFilter, Class<T> entityClazz)
    {
        Vec3 end = pos.add(new Vec3(1, 1, 1));
        AABB aabb = new AABB(pos.x, pos.y, pos.z, end.x, end.y, end.z).inflate(ray.x, ray.y, ray.z);
        Vec3 checkVec = pos.add(ray);
        List<T> ret = Lists.newArrayList();

        for (T t : w.getEntitiesOfClass(entityClazz, aabb, entityFilter.orElse(Predicates.alwaysTrue())))
        {
            AABB entityBB = t.getBoundingBox();
            if (entityBB == null)
            {
                continue;
            }

            if (entityBB.intersects(Math.min(pos.x, checkVec.x), Math.min(pos.y, checkVec.y), Math.min(pos.z, checkVec.z), Math.max(pos.x, checkVec.x), Math.max(pos.y, checkVec.y), Math.max(pos.z, checkVec.z)))
            {
                ret.add(t);
            }
        }

        return ret;
    }
}
