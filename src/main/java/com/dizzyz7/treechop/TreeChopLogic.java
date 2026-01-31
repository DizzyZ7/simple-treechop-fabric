package com.dizzyz7.treechop;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class TreeChopLogic {
    // Лимит блоков, чтобы не "повесить" сервер огромными деревьями
    private static final int MAX_BLOCKS = 512;

    public static void onBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity entity) {
        // Проверяем, что мы на сервере и игрок не в присяде
        if (world.isClient || !(world instanceof ServerWorld serverWorld) || player.isSneaking()) return;

        ItemStack tool = player.getMainHandStack();

        // Если в руке топор, а сломанный блок — дерево
        if (tool.isIn(ItemTags.AXES) && state.isIn(BlockTags.LOGS)) {
            executeTreeChop(serverWorld, pos, player);
        }
    }

    private static void executeTreeChop(ServerWorld world, BlockPos startPos, PlayerEntity player) {
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(startPos);
        visited.add(startPos);

        int blocksBroken = 0;

        while (!queue.isEmpty() && blocksBroken < MAX_BLOCKS) {
            BlockPos current = queue.poll();

            // Поиск соседних блоков в области 3x3x3
            for (BlockPos neighbor : BlockPos.iterate(current.add(-1, -1, -1), current.add(1, 1, 1))) {
                BlockPos immutableNeighbor = neighbor.toImmutable();

                if (!visited.contains(immutableNeighbor)) {
                    BlockState neighborState = world.getBlockState(immutableNeighbor);

                    // Если сосед — тоже дерево
                    if (neighborState.isIn(BlockTags.LOGS)) {
                        visited.add(immutableNeighbor);
                        queue.add(immutableNeighbor);

                        // Ломаем блок с учетом прочности топора
                        world.breakBlock(immutableNeighbor, true, player);
                        blocksBroken++;
                    }
                }
            }
        }
    }
}
