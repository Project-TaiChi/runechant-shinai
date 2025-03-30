package io.github.projeccttaichi.runechantshinai.client.sprites;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.guiLoc;

public enum RecordIcons {
    RECORD_1(0, 0),

    ;

    RecordIcons(int column, int row) {
        final int GIRD_SIZE = 18;
        final int SPELL_ICON_SIZE = 18;
        this.x = column * GIRD_SIZE;
        this.y = row * GIRD_SIZE;
        this.width = SPELL_ICON_SIZE;
        this.height = SPELL_ICON_SIZE;
    }



    public static final ResourceLocation TEXTURE = guiLoc("container/records.png");
    public static final int TEXTURE_WIDTH = 256;
    public static final int TEXTURE_HEIGHT = 256;

    private final int x;
    private final int y;
    private final int width;
    private final int height;


    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void blit(GuiGraphics guiGraphics, int x0, int y0) {
        guiGraphics.blit(TEXTURE, x0, y0, x(), y(), width(), height(), TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

}

