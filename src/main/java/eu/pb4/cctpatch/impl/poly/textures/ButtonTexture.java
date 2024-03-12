package eu.pb4.cctpatch.impl.poly.textures;

import eu.pb4.mapcanvas.api.core.CanvasImage;

public record ButtonTexture(CanvasImage base, CanvasImage hover) {
    public static ButtonTexture of(CanvasImage texture) {
        return new ButtonTexture(texture, texture);
    }
}
