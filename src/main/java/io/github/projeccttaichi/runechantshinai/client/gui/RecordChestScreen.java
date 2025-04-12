package io.github.projeccttaichi.runechantshinai.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.projeccttaichi.runechantshinai.client.sprites.RecordAssemblerWidgets;
import io.github.projeccttaichi.runechantshinai.client.util.RecordUtil;
import io.github.projeccttaichi.runechantshinai.constants.Ids;
import io.github.projeccttaichi.runechantshinai.menu.RecordChestMenu;
import io.github.projeccttaichi.runechantshinai.network.c2s.CustomSlotAction;
import io.github.projeccttaichi.runechantshinai.util.HexGrids;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.guiLoc;

@EventBusSubscriber(modid = Ids.MOD_ID, value = Dist.CLIENT)
public class RecordChestScreen extends AbstractContainerScreen<RecordChestMenu> {
    private static final ResourceLocation TEXTURE = guiLoc("container/record-chest.png");
    private static final int SLOT_SIZE = 18;
    private static final int SLOTS_PER_ROW = 7;
    private static final int VISIBLE_ROWS = 5;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 15;
    private static final int SCROLLBAR_X = 175;
    private static final int SCROLLBAR_Y = 18;
    private static final int STORAGE_START_X = 8;
    private static final int STORAGE_START_Y = 18;
    private static final int INVENTORY_START_Y = 103;

    private boolean isScrolling = false;
    private int scrollbarY = SCROLLBAR_Y;

    public RecordChestScreen(RecordChestMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
//        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
//        this.titleLabelY = 6;
    }

    private int hoveringRecordSlot = -1;

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        // Draw background
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {

    }

    @SubscribeEvent
    private static void onRenderForeground(ContainerScreenEvent.Render.Foreground event) {
        if (!(event.getContainerScreen() instanceof RecordChestScreen)) {
            return;
        }

        RecordChestScreen screen = (RecordChestScreen) event.getContainerScreen();
        screen.renderRecordChestSlots(event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
        screen.renderScrollBarIndicator(event.getGuiGraphics());

    }
    private static final int CHEST_VIEW_SLOT_ROWS = 5;
    private static final int CHEST_VIEW_SLOT_COLS = 7;
    private int currentScroll = 0;


    private int getChestViewSlotIndex(int row, int col) {
        int viewIndex = row * CHEST_VIEW_SLOT_COLS + col;
        return viewIndex + this.currentScroll * CHEST_VIEW_SLOT_COLS;
    }


    private void renderRecordChestSlots(GuiGraphics graphics, int mouseX, int mouseY) {
        final int SLOT_SIZE = 18;
        final int SLOT_SPACING = 4;

        final int CHEST_TOP_LEFT_X = 9;
        final int CHEST_TOP_LEFT_Y = 26;

        for (int i = 0; i < CHEST_VIEW_SLOT_ROWS; i++) {
            for (int j = 0; j < CHEST_VIEW_SLOT_COLS; j++) {
                int slotIndex = getChestViewSlotIndex(i, j);

                if (slotIndex >= this.menu.getRecordCount()) {
                    break;
                }

                int x = CHEST_TOP_LEFT_X + j * (SLOT_SIZE + SLOT_SPACING);
                int y = CHEST_TOP_LEFT_Y + i * (SLOT_SIZE + SLOT_SPACING);



                ItemStack stack = this.menu.getRecordStack(slotIndex);
                if (!stack.isEmpty()) {
                    RecordUtil.renderRecord(graphics, stack, x, y);
                    RecordUtil.renderRecordCount(graphics, font, stack.getCount(), x, y);
                }



                if(isHovering(x, y , SLOT_SIZE, SLOT_SIZE, mouseX, mouseY)) {
                    hoveringRecordSlot = slotIndex;
                    graphics.fillGradient(RenderType.guiOverlay(), x, y, x + 18, y + 18, this.slotColor, this.slotColor,  0);
                }

            }
        }
    }

    private void renderScrollBarIndicator(GuiGraphics guiGraphics) {
        final int SCROLL_BAR_HEIGHT = 126;
        final int SCROLL_BAR_TOP_LEFT_X = 165;
        final int SCROLL_BAR_TOP_LEFT_Y = 7;
        final int SCROLL_BAR_INDICATOR_HEIGHT = RecordAssemblerWidgets.TOOL_SCROLLBAR_INDICATOR.height();

        int totalSlots = this.menu.getRecordCount();

        int overflowSlots = totalSlots - (VISIBLE_ROWS * CHEST_VIEW_SLOT_COLS);

        int indicatorPos = SCROLL_BAR_TOP_LEFT_Y;
        if (overflowSlots > 0) {
            // compute scroll bar
            int scrollSteps = (overflowSlots + CHEST_VIEW_SLOT_COLS - 1) / CHEST_VIEW_SLOT_COLS;

            int currentScroll = Math.clamp(this.currentScroll, 0, scrollSteps);

            indicatorPos = SCROLL_BAR_TOP_LEFT_Y + (SCROLL_BAR_HEIGHT - SCROLL_BAR_INDICATOR_HEIGHT) * currentScroll / scrollSteps;
        }
        RecordAssemblerWidgets.TOOL_SCROLLBAR_INDICATOR.blit(guiGraphics, SCROLL_BAR_TOP_LEFT_X, indicatorPos);
    }

    private void recordSlotClicked(int slotIndex, int button, boolean release) {
        if (button != 0 && button != 1) {
            return;
        }

        ItemStack carried = this.menu.getCarried();
        if (release || carried.isEmpty()) {
            if (hasShiftDown()) {
                PacketDistributor.sendToServer(new CustomSlotAction(this.menu.containerId, 0, slotIndex, CustomSlotAction.Action.QUICK_MOVE, CustomSlotAction.PickType.SINGLE));
            } else {
                PacketDistributor.sendToServer(new CustomSlotAction(this.menu.containerId, 0, slotIndex, CustomSlotAction.Action.PICK_OR_REPLACE, CustomSlotAction.PickType.SINGLE));
            }

            if (!release) {
                this.skipNextRelease = true;
            }

        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.isScrolling = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isScrolling) {
            int scrollbarY = this.topPos + SCROLLBAR_Y;
            double scrollableHeight = 90 - SCROLLBAR_HEIGHT;

            double scrollPercent = (mouseY - scrollbarY) / scrollableHeight;
            scrollPercent = Math.max(0.0, Math.min(1.0, scrollPercent));

//            int maxScroll = this.menu.getMaxScroll();
//            int newScroll = (int) (scrollPercent * maxScroll);
//            this.menu.setScrollOffset(newScroll);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta, double deltaY) {
        if(super.mouseScrolled(mouseX, mouseY, delta, deltaY)) {
            return true;
        }

        final int CHEST_SCROLL_RANGE_TOP_LEFT_X = 3;
        final int CHEST_SCROLL_RANGE_TOP_LEFT_Y = 3;
        final int CHEST_SCROLL_RANGE_WIDTH = 170;
        final int CHEST_SCROLL_RANGE_HEIGHT = 135;

        if(mouseX >= this.leftPos + CHEST_SCROLL_RANGE_TOP_LEFT_X && mouseX <= this.leftPos + CHEST_SCROLL_RANGE_TOP_LEFT_X + CHEST_SCROLL_RANGE_WIDTH &&
                mouseY >= this.topPos + CHEST_SCROLL_RANGE_TOP_LEFT_Y && mouseY <= this.topPos + CHEST_SCROLL_RANGE_TOP_LEFT_Y + CHEST_SCROLL_RANGE_HEIGHT) {

            int maxScroll = (this.menu.getRecordCount() - (CHEST_VIEW_SLOT_COLS * CHEST_VIEW_SLOT_ROWS) + CHEST_VIEW_SLOT_COLS - 1) / CHEST_VIEW_SLOT_COLS;
            this.currentScroll = Math.clamp(this.currentScroll - (int) deltaY, 0, maxScroll);

            return true;
        }
        return false;
    }
} 