package eu.pb4.cctpatch.impl.poly.model.generic;

import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class BlockStateModel extends BlockModel {
    private final List<ItemDisplayElement> modelElements = new ArrayList<>();
    public BlockStateModel(BlockState state) {
        var model = BlockStateModelManager.get(state);

        this.applyModel(model);
    }

    @Override
    public void notifyUpdate(HolderAttachment.UpdateType updateType) {
        super.notifyUpdate(updateType);
        if (updateType == BlockAwareAttachment.BLOCK_STATE_UPDATE) {
            applyModel(BlockStateModelManager.get(this.blockState()));
        }
    }

    private void applyModel(List<BlockStateModelManager.ModelGetter> models) {
        var random = Random.create(this.blockPos().asLong());
        int i = 0;
        while (models.size() < modelElements.size()) {
            this.removeElement(this.modelElements.remove(this.modelElements.size() - 1));
        }
        for (; i < models.size(); i++) {
            var newModel = false;
            ItemDisplayElement element;
            if (this.modelElements.size() == i) {
                element = ItemDisplayElementUtil.createSimple();
                element.setTeleportDuration(0);
                element.setModelTransformation(ModelTransformationMode.NONE);
                element.setYaw(180);
                newModel = true;
                this.modelElements.add(element);
            } else {
                element = this.modelElements.get(i);
            }

            var model = models.get(i).getModel(random);

            element.setItem(model.stack());
            element.setLeftRotation(model.quaternionfc());

            if (newModel) {
                this.addElement(element);
            } else {
                element.tick();
            }
        }
    }
}
