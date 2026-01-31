package com.dizzyz7.treechop;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeChopMod implements ModInitializer {
    public static final String MOD_ID = "simple_treechop";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("DizzyZ7's TreeChop Mod has been initialized!");

        // Регистрация события разрушения блока
        PlayerBlockBreakEvents.AFTER.register(TreeChopLogic::onBlockBreak);
    }
}
