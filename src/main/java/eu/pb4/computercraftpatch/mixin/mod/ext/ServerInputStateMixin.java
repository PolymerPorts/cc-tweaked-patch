package eu.pb4.computercraftpatch.mixin.mod.ext;

import dan200.computercraft.shared.computer.menu.ServerInputState;
import eu.pb4.computercraftpatch.impl.poly.ServerInputStateExt;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ServerInputState.class)
public class ServerInputStateMixin implements ServerInputStateExt {
    @Shadow @Final private IntSet keysDown;

    @Override
    public boolean isKeyDown(int key) {
        return this.keysDown.contains(key);
    }
}
