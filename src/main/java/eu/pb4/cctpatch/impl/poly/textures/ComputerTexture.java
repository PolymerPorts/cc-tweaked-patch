package eu.pb4.cctpatch.impl.poly.textures;

import eu.pb4.mapcanvas.api.core.CanvasImage;

public record ComputerTexture(
    CanvasImage top,
    CanvasImage bottom,
    CanvasImage leftSide,
    CanvasImage rightSide,
    CanvasImage leftTop,
    CanvasImage rightTop,
    CanvasImage leftBottom,
    CanvasImage rightBottom,
    CanvasImage sideButtonPlateTop,
    CanvasImage sideButtonPlateSide,
    CanvasImage sideButtonPlateBottom
) {

    public static ComputerTexture from(CanvasImage main, CanvasImage sidebar) {
        return new ComputerTexture(
                main.copy(12, 0, 12, 12),
                main.copy(12, 24, 12, 12),
                main.copy(0, 12, 12, 12),
                main.copy(24, 12, 12, 12),
                main.copy(0, 0, 12, 12),
                main.copy(24, 0, 12, 12),
                main.copy(0, 24, 12, 12),
                main.copy(24, 24, 12, 12),
                sidebar.copy(0, 0, 17, 3),
                sidebar.copy(0, 3, 17, 7),
                sidebar.copy(0, 10, 17, 3)
        );
    }
    public static ComputerTexture fromRestitched(CanvasImage image) {
        return new ComputerTexture(
            image.copy(0, 0, 48, 12),
            image.copy(0, 12, 48, 12),
            image.copy(0, 28, 12, 24),
            image.copy(36, 28, 12, 24),
            image.copy(12, 28, 12, 12),
            image.copy(24, 28, 12, 12),
            image.copy(12, 40, 12, 12),
            image.copy(24, 40, 12, 12),
            //image.copy(0, 56, 48, 20),
            //image.copy(12, 80, 12, 20),
            //image.copy(24, 80, 12, 20),
            image.copy(0, 101, 17, 5),
            image.copy(0, 106, 17, 4),
            image.copy(0, 110, 17, 5)
            );
    }
}
