package io.github.projeccttaichi.runechantshinai.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.projeccttaichi.runechantshinai.client.gui.widget.RecordListView;
import io.github.projeccttaichi.runechantshinai.client.sprites.RecordAssemblerWidgets;
import io.github.projeccttaichi.runechantshinai.client.util.GuiGraphicsUtil;
import io.github.projeccttaichi.runechantshinai.client.util.RecordRenderUtil;
import io.github.projeccttaichi.runechantshinai.constants.Ids;
import io.github.projeccttaichi.runechantshinai.menu.RecordAssemblerMenu;
import io.github.projeccttaichi.runechantshinai.network.c2s.CustomSlotAction;
import io.github.projeccttaichi.runechantshinai.util.HexGrids;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix2d;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.guiLoc;


@EventBusSubscriber(modid = Ids.MOD_ID, value = Dist.CLIENT)
public class RecordAssemblerScreen extends AbstractContainerScreen<RecordAssemblerMenu> implements CustomSlotEventHandler {
    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 300;

    private static final int GRID_WIDTH = 26;
    private static final int GRID_HEIGHT = 28;
    private static final int GRID_SIZE = 28;

    private static final int VIEWPORT_WIDTH = 210;
    private static final int VIEWPORT_HEIGHT = 182;

    private static final int VIEWPORT_TOP_LEFT_X = 102;
    private static final int VIEWPORT_TOP_LEFT_Y = 9;

    private static final int HORIZONTAL_SPACING = GRID_WIDTH + 2;
    private static final int VERTICAL_SPACING = (GRID_HEIGHT * 3) / 4 + 2;


    private static final int VIEWPORT_CENTER_X = VIEWPORT_TOP_LEFT_X + VIEWPORT_WIDTH / 2 - GRID_SIZE / 2;
    private static final int VIEWPORT_CENTER_Y = VIEWPORT_TOP_LEFT_Y + VIEWPORT_HEIGHT / 2 - GRID_SIZE / 2;
    private final RecordListView listView;


    public RecordAssemblerScreen(RecordAssemblerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);


        this.imageWidth = IMAGE_WIDTH;
        this.imageHeight = IMAGE_HEIGHT;

        this.listView = new RecordListView(RecordAssemblerMenu.SLOT_GROUP_STORAGE, menu.getModel(), this);
    }


    private static final ResourceLocation BACKGROUND_LOCATION = guiLoc("container/record-assembler-bg.png");

    private double translateX = 0.0f;
    private double translateY = 0.0f;


    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // skip
    }

    @Override
    protected void init() {
        super.init();


        this.pixel2HexMatrix = HexGrids.pixel2HexMatrix(GRID_WIDTH, GRID_HEIGHT);

        this.listView.init(
                this.font,
                this.leftPos + 16,
                this.topPos + 38,
                78,
                154,
                3,
                6,
                this.leftPos + 20,
                this.topPos + 58,
                this.leftPos + 87,
                this.topPos + 38,
                154,
                this.slotColor
        );
        this.addRenderableWidget(this.listView);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {

        graphics.blit(BACKGROUND_LOCATION,
                this.leftPos, this.topPos,
                0, 0,
                this.imageWidth, this.imageHeight,
                320, 300
        );


        this.renderHexGrid(graphics);


    }

    private HexGrids.Axial hoveringHexSlot = null;
    private Matrix2d pixel2HexMatrix = null;

    private HexGrids.Axial getHoveringHexSlot(int mouseX, int mouseY) {
        int x0 = this.leftPos + VIEWPORT_TOP_LEFT_X;
        int y0 = this.topPos + VIEWPORT_TOP_LEFT_Y;

        if (mouseX < x0 || mouseX > x0 + VIEWPORT_WIDTH || mouseY < y0 || mouseY > y0 + VIEWPORT_HEIGHT) {
            return null;
        }

        double x = mouseX - x0 - VIEWPORT_WIDTH / 2.0 - this.translateX;
        double y = mouseY - y0 - VIEWPORT_HEIGHT / 2.0 - this.translateY;

        Vector2d doubleAxial = new Vector2d(x, y).mul(this.pixel2HexMatrix);

        HexGrids.Axial axial = HexGrids.axialRound(doubleAxial.x, doubleAxial.y);
        if (this.menu.getRecordSequence().isEnabled(axial)) {
            return axial;
        }

        return null;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.hoveringHexSlot = getHoveringHexSlot(mouseX, mouseY);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @SubscribeEvent
    private static void onRenderForeground(ContainerScreenEvent.Render.Foreground event) {
        if (!(event.getContainerScreen() instanceof RecordAssemblerScreen)) {
            return;
        }

        RecordAssemblerScreen screen = (RecordAssemblerScreen) event.getContainerScreen();
        screen.renderHexGridSlots(event.getGuiGraphics());

    }

    private int currentScroll = 0;
    private static final int CHEST_VIEW_SLOT_ROWS = 6;
    private static final int CHEST_VIEW_SLOT_COLS = 3;
    private static final int MAX_CHEST_VIEW_SLOTS = CHEST_VIEW_SLOT_COLS * CHEST_VIEW_SLOT_ROWS;


    private void renderHexGridSlots(GuiGraphics graphics) {


//        graphics.enableScissor(VIEWPORT_TOP_LEFT_X, VIEWPORT_TOP_LEFT_Y, VIEWPORT_TOP_LEFT_X + VIEWPORT_WIDTH, VIEWPORT_TOP_LEFT_Y + VIEWPORT_HEIGHT);
        GuiGraphicsUtil.enableScissorTransformed(graphics, VIEWPORT_TOP_LEFT_X, VIEWPORT_TOP_LEFT_Y, VIEWPORT_TOP_LEFT_X + VIEWPORT_WIDTH, VIEWPORT_TOP_LEFT_Y + VIEWPORT_HEIGHT);

        graphics.pose().pushPose();
        graphics.pose().translate(VIEWPORT_CENTER_X, VIEWPORT_CENTER_Y, 0);
        graphics.pose().translate(translateX, translateY, 0);
        for (HexGrids.Axial axial : this.menu.getRecordSequence().listRecords()) {
            renderHexGridSlot(graphics, axial);
        }

        renderHexHighlight(graphics, this.hoveringHexSlot);

        graphics.pose().popPose();

        graphics.disableScissor();

    }

    private void renderHexGridSlot(GuiGraphics graphics, HexGrids.Axial axial) {
        HexGrids.DoubledOffset doubledOffset = axial.toDoubledOffset();

        int x = doubledOffset.col() * HORIZONTAL_SPACING / 2;
        int y = doubledOffset.row() * VERTICAL_SPACING;

        ItemStack stack = this.menu.getRecordSequence().getRecord(axial);
        if (stack.isEmpty()) {
            return;
        }

//        graphics.renderItem(stack, x + 5, y + 6);

        RecordRenderUtil.renderRecord(graphics, stack, x + 4, y + 5);
    }

    private void renderHexHighlight(GuiGraphics graphics, HexGrids.Axial axial) {
        if (axial == null) {
            return;
        }
        HexGrids.DoubledOffset doubledOffset = axial.toDoubledOffset();

        int x = doubledOffset.col() * HORIZONTAL_SPACING / 2;
        int y = doubledOffset.row() * VERTICAL_SPACING;

        RenderSystem.enableBlend();

        RecordAssemblerWidgets.HEX_SLOT_HIGHLIGHT_FG.blit(graphics, x, y);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);

        if (this.hoveringHexSlot != null && !this.menu.getCarried().isEmpty()) {
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(Component.literal("Hex slot: (" + this.hoveringHexSlot.q() + ", " + this.hoveringHexSlot.r() + ")"));
            ItemStack stack = this.menu.getRecordSequence().getRecord(this.hoveringHexSlot);
            if (!stack.isEmpty()) {
                List<Component> tooltipFromContainerItem = this.getTooltipFromContainerItem(stack);
                tooltips.addAll(tooltipFromContainerItem);
            }

            guiGraphics.renderTooltip(this.font, tooltips, Optional.empty(), x, y);

        }
    }

    private void renderHexGrid(GuiGraphics graphics) {

        int x0 = this.leftPos + VIEWPORT_TOP_LEFT_X;
        int y0 = this.topPos + VIEWPORT_TOP_LEFT_Y;


        graphics.enableScissor(x0, y0, x0 + VIEWPORT_WIDTH, y0 + VIEWPORT_HEIGHT);

        graphics.pose().pushPose();
        graphics.pose().translate(this.leftPos, this.topPos, 0);
        graphics.pose().translate(VIEWPORT_CENTER_X, VIEWPORT_CENTER_Y, 0);
        graphics.pose().translate(translateX, translateY, 0);

//        Matrix2d hex2PixelMatrix = this.pixel2HexMatrix.invert(new Matrix2d());
//
//
//        for (HexGrids.Axial axial : this.menu.wandRecords.keySet()) {
////            HexGrids.DoubledOffset doubledOffset = axial.toDoubledOffset();
//
////            int x = doubledOffset.col() * HORIZONTAL_SPACING / 2;
////            int y = doubledOffset.row() * VERTICAL_SPACING;
//
//            Vector2d pixel = new Vector2d(axial.q(), axial.r()).mul(hex2PixelMatrix);
//
//            int x = (int) pixel.x;
//            int y = (int) pixel.y;
//
//            graphics.blit(RenderType::guiTextured, BACKGROUND_LOCATION,
//                    x, y,
//                    uOffset, vOffset,
//                    GRID_WIDTH, GRID_HEIGHT,
//                    256, 256
//            );
//
//        }

        // doubled offset with less math
        for (HexGrids.Axial axial : this.menu.getRecordSequence().listAvailable()) {

            if (!this.menu.getRecordSequence().isVisible(axial)) {
                continue;
            }

            HexGrids.DoubledOffset doubledOffset = axial.toDoubledOffset();

            int x = doubledOffset.col() * HORIZONTAL_SPACING / 2;
            int y = doubledOffset.row() * VERTICAL_SPACING;


//            graphics.blit(RenderType::guiTextured, BACKGROUND_LOCATION,
//                    x, y,
//                    uOffset, vOffset,
//                    GRID_WIDTH, GRID_HEIGHT,
//                    256, 256
//            );

            if (!this.menu.getRecordSequence().isEnabled(axial)) {
                RecordAssemblerWidgets.HEX_SLOT_LOCKED.blit(graphics, x, y);
            } else if (!this.menu.getRecordSequence().isValidPosition(axial)) {
                RecordAssemblerWidgets.HEX_SLOT_INACTIVE.blit(graphics, x, y);
            } else {
                RecordAssemblerWidgets.HEX_SLOT_ACTIVATE.blit(graphics, x, y);
            }

        }


        graphics.pose().

                popPose();

        graphics.disableScissor();

    }

    private boolean dragging = false;
    private double dragStartX = 0;
    private double dragStartY = 0;
    private double dragStartTranslateX = 0;
    private double dragStartTranslateY = 0;

    public void setTranslation(double x, double y) {
        double n = this.menu.hexSize + 1.5;
        double col = x / HORIZONTAL_SPACING * 2;
        double row = y / VERTICAL_SPACING;

        double dCol = Math.abs(col);
        double dRow = Math.abs(row);
        double l = dRow + Math.max(0, (dCol - dRow) / 2);


        if (l <= n) {
            this.translateX = x;
            this.translateY = y;
            return;
        }

        double scale = n / l;

        this.translateX = x * scale;
        this.translateY = y * scale;

    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging) {
            double newX = dragStartTranslateX + (mouseX - dragStartX);
            double newY = dragStartTranslateY + (mouseY - dragStartY);
            this.setTranslation(newX, newY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        boolean ret = super.mouseClicked(mouseX, mouseY, button);

        if (button == 2) {
            this.dragging = true;
            this.dragStartX = mouseX;
            this.dragStartY = mouseY;
            this.dragStartTranslateX = this.translateX;
            this.dragStartTranslateY = this.translateY;
        }
        if (this.hoveringHexSlot != null) {
            this.customSlotClicked(RecordAssemblerMenu.SLOT_GROUP_HEX, this.hoveringHexSlot.packed(), button, false);
        }

        return ret;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean skip = this.skipNextRelease;
        boolean ret = super.mouseReleased(mouseX, mouseY, button);
        if (button == 2) {
            this.dragging = false;
        }

        if (this.hoveringHexSlot != null && !skip) {
            this.customSlotClicked(RecordAssemblerMenu.SLOT_GROUP_HEX, this.hoveringHexSlot.packed(), button, true);
        }

        return ret;

    }

    @Override
    protected void renderFloatingItem(GuiGraphics guiGraphics, ItemStack stack, int x, int y, String text) {
        if (!RecordRenderUtil.renderFloatingRecord(guiGraphics, font, stack, x, y)) {
            super.renderFloatingItem(guiGraphics, stack, x, y, text);
        }
    }

    @Override
    public void customSlotClicked(int slotGroup, int slotIndex, int button, boolean release) {
        if (button != 0 && button != 1) {
            return;
        }


        CustomSlotAction.OptType optType = button == 0 ? CustomSlotAction.OptType.LEFT_CLICK : CustomSlotAction.OptType.RIGHT_CLICK;

        ItemStack carried = this.menu.getCarried();
        if (carried.isEmpty()) {
            if (hasShiftDown()) {
                PacketDistributor.sendToServer(new CustomSlotAction(this.menu.containerId, slotGroup, slotIndex, CustomSlotAction.Action.QUICK_MOVE, optType));
            } else {
                PacketDistributor.sendToServer(new CustomSlotAction(this.menu.containerId, slotGroup, slotIndex, CustomSlotAction.Action.PICK_OR_REPLACE, optType));
            }
            this.skipNextRelease = true;
        } else if (release) {

            if (this.skipNextRelease || this.isQuickCrafting) {
                return;
            }
            if (!hasShiftDown()) {
                PacketDistributor.sendToServer(new CustomSlotAction(this.menu.containerId, slotGroup, slotIndex, CustomSlotAction.Action.PICK_OR_REPLACE, optType));

            }
        }
    }
//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        boolean handled = super.keyPressed(keyCode, scanCode, modifiers);
//        if(handled){
//            return true;
//        }
//
//        InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
//        final int MOVE_AMOUNT = 5;
//        // wasd movement
//        if (key.getValue() == GLFW.GLFW_KEY_W) {
//            this.setTranslation(this.translateX, this.translateY + MOVE_AMOUNT);
//            return true;
//        }
//        if (key.getValue() == GLFW.GLFW_KEY_S) {
//            this.setTranslation(this.translateX, this.translateY - MOVE_AMOUNT);
//            return true;
//        }
//        if (key.getValue() == GLFW.GLFW_KEY_A) {
//            this.setTranslation(this.translateX - MOVE_AMOUNT, this.translateY);
//            return true;
//        }
//        if (key.getValue() == GLFW.GLFW_KEY_D) {
//            this.setTranslation(this.translateX + MOVE_AMOUNT, this.translateY);
//            return true;
//        }
//
//        return false;
//    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

}
