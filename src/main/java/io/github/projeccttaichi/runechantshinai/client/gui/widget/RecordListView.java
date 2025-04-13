package io.github.projeccttaichi.runechantshinai.client.gui.widget;

import io.github.projeccttaichi.runechantshinai.client.gui.CustomSlotEventHandler;
import io.github.projeccttaichi.runechantshinai.client.sprites.RecordAssemblerWidgets;
import io.github.projeccttaichi.runechantshinai.client.util.RecordRenderUtil;
import io.github.projeccttaichi.runechantshinai.menu.RecordListModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class RecordListView implements Renderable, GuiEventListener, NarratableEntry {
    private final int slotGroup;
    private final RecordListModel model;
    private final CustomSlotEventHandler slotEventHandler;
    private Font font;

    public RecordListView(int slotGroup, RecordListModel model, CustomSlotEventHandler slotEventHandler) {
        this.slotGroup = slotGroup;
        this.model = model;
        this.slotEventHandler = slotEventHandler;
    }

    // scrollbar
    private int scrollBarLeft = 0;
    private int scrollBarTop = 0;
    private int scrollBarHeight = 126;
    final int SCROLL_BAR_INDICATOR_HEIGHT = RecordAssemblerWidgets.TOOL_SCROLLBAR_INDICATOR.height();

    private int currentScroll = 0;


    // slots
    private int visibleRows = 5;
    private int visibleCols = 7;

    private int hoveringIndex = -1;
    private int slotColor = 0xFFFFFF;

    private int slotTop = 0;
    private int slotLeft = 0;


    // view
    private int topPos = 0;
    private int leftPos = 0;
    private int width = 0;
    private int height = 0;

    public void init(Font font, int leftPos, int topPos, int width, int height, int visibleCols, int visibleRows, int slotLeft, int slotTop, int scrollBarLeft, int scrollBarTop, int scrollBarHeight, int slotColor) {
        this.font = font;
        this.leftPos = leftPos;
        this.topPos = topPos;
        this.width = width;
        this.height = height;
        this.visibleCols = visibleCols;
        this.visibleRows = visibleRows;
        this.slotTop = slotTop;
        this.slotLeft = slotLeft;
        this.scrollBarLeft = scrollBarLeft;
        this.scrollBarTop = scrollBarTop;
        this.scrollBarHeight = scrollBarHeight;
        this.slotColor = slotColor;
    }


    private void renderScrollBarIndicator(GuiGraphics guiGraphics) {
        int totalSlots = model.count();

        int overflowSlots = totalSlots - (visibleRows * visibleCols);

        int indicatorPos = scrollBarTop + 1;
        if (overflowSlots > 0) {
            // compute scroll bar
            int scrollSteps = (overflowSlots + visibleCols - 1) / visibleCols;

            int currentScroll = Math.clamp(this.currentScroll, 0, scrollSteps);

            indicatorPos = scrollBarTop + 1 + (scrollBarHeight - 2 - SCROLL_BAR_INDICATOR_HEIGHT) * currentScroll / scrollSteps;
        }
        RecordAssemblerWidgets.TOOL_SCROLLBAR_INDICATOR.blit(guiGraphics, scrollBarLeft + 1, indicatorPos);
    }


    private int viewSlotIndex(int row, int col) {
        int viewIndex = row * visibleCols + col;
        return viewIndex + this.currentScroll * visibleCols;
    }


    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= (double) (x - 1) && mouseX < (double) (x + width + 1) && mouseY >= (double) (y - 1) && mouseY < (double) (y + height + 1);
    }

    private void renderSlots(GuiGraphics graphics, int mouseX, int mouseY) {
        final int SLOT_SIZE = 18;
        final int SLOT_SPACING = 4;
        hoveringIndex = -1;
        for (int i = 0; i < visibleRows; i++) {
            for (int j = 0; j < visibleCols; j++) {
                int slotIndex = viewSlotIndex(i, j);

                if (slotIndex >= this.model.count()) {
                    break;
                }

                int x = slotLeft + j * (SLOT_SIZE + SLOT_SPACING);
                int y = slotTop + i * (SLOT_SIZE + SLOT_SPACING);


                ItemStack stack = this.model.getStack(slotIndex);
                if (!stack.isEmpty()) {
                    RecordRenderUtil.renderRecord(graphics, stack, x, y);
                    RecordRenderUtil.renderRecordCount(graphics, font, stack.getCount(), x, y);
                }


                if (isHovering(x, y, SLOT_SIZE, SLOT_SIZE, mouseX, mouseY)) {
                    hoveringIndex = slotIndex;
                    graphics.fillGradient(RenderType.guiOverlay(), x, y, x + 18, y + 18, this.slotColor, this.slotColor, 0);
                }

            }
        }
    }

    public void renderTooltip(GuiGraphics graphics, int x, int y) {
        if (hoveringIndex > 0) {
            ItemStack stack = this.model.getStack(hoveringIndex);
            if (!stack.isEmpty()) {
                List<Component> tooltips = Screen.getTooltipFromItem(Minecraft.getInstance(), stack);
                graphics.renderTooltip(this.font, tooltips, Optional.empty(), x, y);
            }
        }
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxScroll = (this.model.count() - visibleCols * (visibleRows - 1) - 1) / visibleCols;
        if(maxScroll <= 0) {
            this.currentScroll = 0;
            return true;
        }
        this.currentScroll = Math.clamp(this.currentScroll - (int) scrollY, 0, maxScroll);
        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if(!this.model.valid()) {
            return false;
        }
        return mouseX >= this.leftPos && mouseX <= this.leftPos + this.width &&
                mouseY >= this.topPos && mouseY <= this.topPos + this.height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.hoveringIndex >= 0) {
            int hoveringContainerIndex = this.model.containerIndex(this.hoveringIndex);
            this.slotEventHandler.customSlotClicked(this.slotGroup, hoveringContainerIndex, button, false);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        final int slotAreaRight = this.slotLeft + this.visibleCols * (18 + 4) - 4;
        final int slotAreaBottom = this.slotTop + this.visibleRows * (18 + 4) - 4;

        if (mouseX >= this.slotLeft && mouseX <= slotAreaRight && mouseY >= this.slotTop && mouseY <= slotAreaBottom) {
            this.slotEventHandler.customSlotClicked(this.slotGroup, -1, button, true);
        }


        return false;

    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderSlots(guiGraphics, mouseX, mouseY);
        this.renderScrollBarIndicator(guiGraphics);
    }

    private boolean focused = false;

    @Override
    public void setFocused(boolean b) {
        this.focused = b;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }
}
