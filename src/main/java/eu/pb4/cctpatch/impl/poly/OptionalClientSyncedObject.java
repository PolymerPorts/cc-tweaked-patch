package eu.pb4.cctpatch.impl.poly;

import eu.pb4.cctpatch.impl.util.OptionalClientNetworking;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

public class OptionalClientSyncedObject<T> implements PolymerSyncedObject<T> {
    @Override
    public boolean canSyncRawToClient(PacketContext context) {
        return OptionalClientNetworking.canSyncRawToClient(context);
    }

    @Override
    public T getPolymerReplacement(T object, PacketContext context) {
        if (OptionalClientNetworking.canSyncRawToClient(context)) {
            return object;
        }
        return null;
    }
}
