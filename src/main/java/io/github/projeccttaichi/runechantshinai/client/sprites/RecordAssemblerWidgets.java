package io.github.projeccttaichi.runechantshinai.client.sprites;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.guiLoc;

public enum RecordAssemblerWidgets {
    // hex slots
    HEX_SLOT_ACTIVATE(0, 0, 26, 28),
    HEX_SLOT_LOCKED(2, 0, 26, 28),
    HEX_SLOT_INACTIVE(4, 0, 26, 28),
    HEX_SLOT_HIGHLIGHT_FG(6, 0, 26, 28),
    HEX_SLOT_HIGHLIGHT_BG(8, 0, 26, 28),

    // gui buttons
    BUTTON_DETAIL(0, 2, 16, 16),
    BUTTON_RECOVER(1, 2, 16, 16),
    BUTTON_EXCHANGED(2, 2, 16, 16),
    BUTTON_CLEAR(3, 2, 16, 16),
    BUTTON_SAVE(4, 2, 16, 16),
    BUTTON_LOAD(5, 2, 16, 16),


    // other tools
    TOOL_SCROLLBAR_INDICATOR(0, 3, 5, 11),


    ;

    RecordAssemblerWidgets(int column, int row, int width, int height) {
        final int GIRD_SIZE = 16;
        this.x = column * GIRD_SIZE;
        this.y = row * GIRD_SIZE;
        this.width = width;
        this.height = height;
    }



    public static final ResourceLocation TEXTURE = guiLoc("container/record-assembler-widgets.png");
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

