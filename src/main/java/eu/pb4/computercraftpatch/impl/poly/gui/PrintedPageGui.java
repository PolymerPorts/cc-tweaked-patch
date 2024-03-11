package eu.pb4.computercraftpatch.impl.poly.gui;

import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.core.util.Colour;
import dan200.computercraft.shared.media.items.PrintoutItem;
import eu.pb4.computercraftpatch.impl.poly.Fonts;
import eu.pb4.computercraftpatch.impl.poly.render.CenteredTextView;
import eu.pb4.computercraftpatch.impl.poly.render.ImageView;
import eu.pb4.computercraftpatch.impl.poly.render.TextButton;
import eu.pb4.computercraftpatch.impl.poly.textures.GuiTextures;
import eu.pb4.computercraftpatch.impl.poly.textures.RepeatingCanvas;
import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.CanvasImage;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class PrintedPageGui extends MapGui {
    private final int pageCount;
    private final String title;
    private final String[] lines;
    private final String[] colors;
    private final CanvasImage[] pages;

    private final ImageView displayedPage;
    private final ImageView leftSide;
    private final ImageView rightSide;

    private int currentPage = 0;
    @Nullable
    private final CenteredTextView pageText;

    public PrintedPageGui(ServerPlayerEntity player, ItemStack stack) {
        super(player);

        this.title = PrintoutItem.getTitle(stack);
        this.lines = PrintoutItem.getText(stack);
        this.colors = PrintoutItem.getColours(stack);
        this.pageCount = PrintoutItem.getPageCount(stack);


        var list = new ArrayList<CanvasImage>();
        var type = ((PrintoutItem) stack.getItem()).getType();

        CanvasImage image = null;

        var pageWidth = Fonts.FONT_WIDTH * PrintoutItem.LINE_MAX_LENGTH;
        var pageHeight = Fonts.FONT_HEIGHT * PrintoutItem.LINES_PER_PAGE;



        for (int y = 0; y < this.lines.length; y++) {
            var lY = y % PrintoutItem.LINES_PER_PAGE;
            if (lY == 0) {
                if (image != null) {
                    list.add(image);
                }
                image = new CanvasImage(pageWidth, pageHeight);
            }
            var line = this.lines[y];
            var color = this.colors[y];
            var textLength = Math.min(PrintoutItem.LINES_PER_PAGE, line.length());
            for (int x = 0; x < textLength; x++) {
                var character = line.charAt(x);
                int charWidth = Fonts.TERMINAL_FONT.getGlyphWidth(character, 8, 0);

                Fonts.TERMINAL_FONT.drawGlyph(image, line.charAt(x), x * Fonts.FONT_WIDTH + (Fonts.FONT_WIDTH - charWidth) / 2, lY * Fonts.FONT_HEIGHT, 8, 0,
                        CanvasUtils.findClosestColor(Colour.fromInt(15 - Terminal.getColour(color.charAt(x), Colour.BLACK)).getHex()));
            }
        }

        if (image != null) {
            list.add(image);
        }

        this.pages = list.toArray(new CanvasImage[0]);

        int centerX = canvas.getWidth() / 2;
        int centerY = canvas.getHeight() / 2 - 24;

        if (this.pageCount > 1) {
            var ay = centerY + pageHeight / 2 + 32;
            this.pageText = new CenteredTextView(0, ay, this.renderer.canvas().getWidth(),"AAAAAA", DefaultFonts.VANILLA, 16, CanvasColor.BLACK_HIGH);
            this.pageText.zIndex = 100;
            this.renderer.add(this.pageText);

            this.renderer.add(new TextButton(centerX - 48, ay, 20, 20, "«", DefaultFonts.VANILLA, 16, CanvasColor.BLACK_HIGH, (x, y, z) -> this.previousPage()));
            this.renderer.add(new TextButton(centerX + 48 - 19, ay, 20, 20, "»", DefaultFonts.VANILLA, 16, CanvasColor.BLACK_HIGH, (x, y, z) -> this.nextPage()));
        } else {
            this.pageText = null;
        }

        {
            var bW = GuiTextures.PRINTED_PAGE.centerPage().getWidth();
            var bH = GuiTextures.PRINTED_PAGE.centerPage().getHeight();

            var bX = centerX - bW / 2;
            var bY = centerY - bH / 2;

            var background = new ImageView(bX, bY, GuiTextures.PRINTED_PAGE.centerPage());
            background.zIndex = 8;
            this.renderer.add(background);

            var offset = type == PrintoutItem.Type.PAGE ? 0 : 8;


            this.leftSide = new ImageView(bX - offset, bY, GuiTextures.PRINTED_PAGE.leftPageSide());
            this.leftSide.zIndex = 9 - offset;
            this.renderer.add(this.leftSide);


            this.rightSide = new ImageView(bX + bW + offset - GuiTextures.PRINTED_PAGE.rightPageSide().getWidth(), bY, GuiTextures.PRINTED_PAGE.rightPageSide());
            this.rightSide.zIndex = 9 - offset;
            this.renderer.add(this.rightSide);


            if (type == PrintoutItem.Type.BOOK) {
                this.renderer.add(new ImageView(
                    bX - 3,
                    centerY - GuiTextures.PRINTED_PAGE.leatherRight().getHeight() / 2,
                    new RepeatingCanvas(
                        GuiTextures.PRINTED_PAGE.leatherTop(),
                        bW + 6,
                        GuiTextures.PRINTED_PAGE.leatherTop().getHeight())
                    )
                );

                this.renderer.add(new ImageView(
                        bX - 3,
                        centerY + GuiTextures.PRINTED_PAGE.leatherLeft().getHeight() / 2 - GuiTextures.PRINTED_PAGE.leatherBottom().getHeight(),
                    new RepeatingCanvas(
                            GuiTextures.PRINTED_PAGE.leatherBottom(),
                            bW + 6,
                            GuiTextures.PRINTED_PAGE.leatherBottom().getHeight())
                    )
                );

                this.renderer.add(new ImageView(
                    bX - GuiTextures.PRINTED_PAGE.leatherLeft().getWidth() - 3,
                    centerY - GuiTextures.PRINTED_PAGE.leatherLeft().getHeight() / 2,
                    GuiTextures.PRINTED_PAGE.leatherLeft())
                );

                this.renderer.add(new ImageView(
                    bX + bW + 3,
                    centerY - GuiTextures.PRINTED_PAGE.leatherRight().getHeight() / 2,
                    GuiTextures.PRINTED_PAGE.leatherRight())
                );
            }
        }


        this.displayedPage = new ImageView(centerX - pageWidth / 2, centerY - pageHeight / 2, this.pages[0]);
        this.displayedPage.zIndex = 50;

        this.renderer.add(this.displayedPage);

        this.setPage(0);

        this.render();
        this.open();
    }

    private void nextPage() {
        var page = this.currentPage + 1;

        if (page >= this.pageCount) {
            page = 0;
        }

        this.setPage(page);
    }

    private void previousPage() {
        var page = this.currentPage - 1;

        if (page < 0) {
            page = this.pageCount - 1;
        }

        this.setPage(page);
    }

    private void setPage(int page) {
        this.displayedPage.image = this.pages[page];
        this.currentPage = page;

        if (this.pageText != null) {
            this.pageText.text = (page + 1) + "/" + this.pageCount;
        }
    }

    @Override
    public void onPlayerAction(PlayerActionC2SPacket.Action action, Direction direction, BlockPos pos) {
        if (action == PlayerActionC2SPacket.Action.DROP_ITEM) {
            this.previousPage();
            return;
        }
        super.onPlayerAction(action, direction, pos);
    }

    @Override
    public void onPlayerCommand(int id, ClientCommandC2SPacket.Mode action, int data) {
        if (action == ClientCommandC2SPacket.Mode.OPEN_INVENTORY) {
            this.nextPage();
        }
    }
}
