package eu.pb4.computercraftpatch.impl.poly;

import dan200.computercraft.shared.ComputerCraft;
import dan200.computercraft.shared.ModRegistry;
import eu.pb4.computercraftpatch.impl.poly.model.BlockStateModelManager;
import eu.pb4.computercraftpatch.impl.poly.textures.GuiTextures;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.IdentityHashMap;
import java.util.Map;

public class PolymerSetup {
    public static final GuiElementBuilder FILLER_ITEM = new GuiElementBuilder(Items.WHITE_STAINED_GLASS_PANE)
            .setName(Text.empty());
    public static final Identifier GUI_FONT = new Identifier("computercraft", "gui");

    public static void setup() {
        Fonts.TERMINAL_FONT.hashCode();
        GuiTextures.ADVANCED_COMPUTER.hashCode();

        PolymerResourcePackUtils.addModAssets("computercraft");

        FILLER_ITEM.setCustomModelData(PolymerResourcePackUtils.requestModel(Items.WHITE_STAINED_GLASS_PANE, new Identifier("computercraft", "poly_gui/filler")).value());

    }
}
