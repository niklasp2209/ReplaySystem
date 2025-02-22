package de.bukkitnews.replay.module.replay.region;

import de.bukkitnews.replay.module.replay.region.region.Region;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Utility class for managing regions, selectors, and temporary teams in a Minecraft world.
 */
@UtilityClass
public class RegionUtil {

    private static final @NotNull String SELECTOR_TAG_PREFIX = "selector-";
    private static final @NotNull String TEAM_PREFIX = "selector-team-";

    /**
     * Removes all selector entities that have a specific tag.
     *
     * @param tag The tag to filter selector entities by.
     */
    public void removeSelectorsByTag(@NotNull String tag) {
        Bukkit.getWorlds().forEach(world ->
                world.getEntities().stream()
                        .filter(entity -> entity.getScoreboardTags().contains(SELECTOR_TAG_PREFIX + tag))
                        .forEach(Entity::remove));
    }

    /**
     * Creates a visual selector for a given region. This involves spawning entities and assigning them to a team.
     *
     * @param region The region to create the selector for.
     * @param world  The world in which to create the selector.
     * @param id     The unique identifier for the selector.
     * @param color  The color for the selector's team.
     */
    public void createSelector(@NotNull Region region, @Nullable World world, @NotNull String id, @NotNull ChatColor color) {
        Optional<Location> corner1Opt = region.getCorner1();
        Optional<Location> corner2Opt = region.getCorner2();

        if (corner1Opt.isEmpty() || corner2Opt.isEmpty() || world == null) {
            return;
        }

        Location corner1 = corner1Opt.get();
        Location corner2 = corner2Opt.get();

        if (!corner1.getWorld().equals(corner2.getWorld())) {
            return;
        }

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        int nextTeamNumber = board.getTeams().stream()
                .filter(team -> team.getName().startsWith(TEAM_PREFIX))
                .mapToInt(RegionUtil::parseNumberFromTeam).max()
                .orElse(0) + 1;

        Team team = board.registerNewTeam(TEAM_PREFIX + nextTeamNumber);
        if (color != null) team.setColor(color);
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

        IntStream.rangeClosed(minX, maxX).forEach(x -> {
            spawnSelectorEntity(world, team, id, x, minY, minZ);
            spawnSelectorEntity(world, team, id, x, maxY, minZ);
            spawnSelectorEntity(world, team, id, x, minY, maxZ);
            spawnSelectorEntity(world, team, id, x, maxY, maxZ);
        });

        IntStream.rangeClosed(minY, maxY).forEach(y -> {
            spawnSelectorEntity(world, team, id, minX, y, minZ);
            spawnSelectorEntity(world, team, id, maxX, y, minZ);
            spawnSelectorEntity(world, team, id, minX, y, maxZ);
            spawnSelectorEntity(world, team, id, maxX, y, maxZ);
        });

        IntStream.rangeClosed(minZ, maxZ).forEach(z -> {
            spawnSelectorEntity(world, team, id, minX, minY, z);
            spawnSelectorEntity(world, team, id, maxX, minY, z);
            spawnSelectorEntity(world, team, id, minX, maxY, z);
            spawnSelectorEntity(world, team, id, maxX, maxY, z);
        });
    }

    /**
     * Spawns a slime entity to act as a visual marker for the selector.
     *
     * @param world The world in which to spawn the entity.
     * @param team  The team to associate the entity with.
     * @param id    The unique identifier for the selector.
     * @param x     The X-coordinate of the entity.
     * @param y     The Y-coordinate of the entity.
     * @param z     The Z-coordinate of the entity.
     */
    private void spawnSelectorEntity(@NotNull World world, @NotNull Team team, @NotNull String id, double x, double y, double z) {
        Slime slime = (Slime) world.spawnEntity(new Location(world, x + 0.5, y, z + 0.5), EntityType.SLIME);
        slime.setInvisible(true);
        slime.setSize(2);
        slime.setAI(false);
        slime.setGravity(false);
        slime.setCollidable(false);
        slime.setSilent(true);
        slime.setCanPickupItems(false);
        slime.setGlowing(true);
        slime.setInvulnerable(true);
        slime.addScoreboardTag(SELECTOR_TAG_PREFIX + id);
        slime.setPersistent(true);
        team.addEntry(slime.getUniqueId().toString());
    }

    private int parseNumberFromTeam(@NotNull Team team) {
        try {
            return Integer.parseInt(team.getName().substring(TEAM_PREFIX.length()));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}