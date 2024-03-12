package eu.pb4.cctpatch.impl.poly.textures;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;

public record RepeatingCanvas(DrawableCanvas source, int width, int height) implements DrawableCanvas {

    @Override
    public byte getRaw(int x, int y) {
        return this.source.getRaw(x % this.source.getWidth(), y % this.source.getHeight());
    }

    @Override
    public void setRaw(int x, int y, byte color) {
        this.source.setRaw(x % this.source.getWidth(), y % this.source.getHeight(), color);
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }
}
