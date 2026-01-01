package eu.pb4.cctpatch.mixin.mod.ext;

import dan200.computercraft.core.input.UserComputerInput;
import eu.pb4.cctpatch.impl.poly.ext.ComputerInputExt;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(UserComputerInput.class)
public class UserComputerInputMixin implements ComputerInputExt {
    @Shadow @Final private IntSet keysDown;

    @Override
    public boolean isKeyDown(int key) {
        return this.keysDown.contains(key);
    }
}
