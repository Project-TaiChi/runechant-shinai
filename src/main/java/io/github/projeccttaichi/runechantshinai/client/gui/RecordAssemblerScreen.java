package io.github.projeccttaichi.runechantshinai.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.projeccttaichi.runechantshinai.menu.RecordAssemblerMenu;
import io.github.projeccttaichi.runechantshinai.network.c2s.HexSlotAction;
import io.github.projeccttaichi.runechantshinai.util.HexGrids;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix2d;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.guiLoc;

public class RecordAssemblerScreen extends AbstractContainerScreen<RecordAssemblerMenu> {
    private static final int IMAGE_WIDTH = 212;
    private static final int IMAGE_HEIGHT = 199;

    private static final int GRID_WIDTH = 26;
    private static final int GRID_HEIGHT = 28;
    private static final int GRID_SIZE = 28;

    private static final int VIEWPORT_WIDTH = 142;
    private static final int VIEWPORT_HEIGHT = 84;

    private static final int VIEWPORT_TOP_LEFT_X = 62;
    private static final int VIEWPORT_TOP_LEFT_Y = 9;

    private static final int HORIZONTAL_SPACING = GRID_WIDTH + 2;
    private static final int VERTICAL_SPACING = (GRID_HEIGHT * 3) / 4 + 2;


    private static final int VIEWPORT_CENTER_X = VIEWPORT_TOP_LEFT_X + VIEWPORT_WIDTH / 2 - GRID_SIZE / 2;
    private static final int VIEWPORT_CENTER_Y = VIEWPORT_TOP_LEFT_Y + VIEWPORT_HEIGHT / 2 - GRID_SIZE / 2;


    public RecordAssemblerScreen(RecordAssemblerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);


        this.imageWidth = IMAGE_WIDTH;
        this.imageHeight = IMAGE_HEIGHT;
    }


    private static final ResourceLocation BACKGROUND_LOCATION = guiLoc("container/record_assembler.png");

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

    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {

        graphics.blit(RenderType::guiTextured, BACKGROUND_LOCATION,
                this.leftPos, this.topPos,
                0, 0,
                this.imageWidth, this.imageHeight,
                256, 256
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
        if (this.menu.validHexSlot(axial)) {
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

    @Override
    protected void renderSlots(GuiGraphics graphics) {
        super.renderSlots(graphics);


        graphics.enableScissor(VIEWPORT_TOP_LEFT_X, VIEWPORT_TOP_LEFT_Y, VIEWPORT_TOP_LEFT_X + VIEWPORT_WIDTH, VIEWPORT_TOP_LEFT_Y + VIEWPORT_HEIGHT);

        graphics.pose().pushPose();
        graphics.pose().translate(VIEWPORT_CENTER_X, VIEWPORT_CENTER_Y, 0);
        graphics.pose().translate(translateX, translateY, 0);
        for (HexGrids.Axial axial : this.menu.getSlotPositions()) {
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

        ItemStack stack = this.menu.getHexSlotItem(axial);
        if (stack.isEmpty()) {
            return;
        }

        graphics.renderItem(stack, x + 5, y + 6);

    }

    private void renderHexHighlight(GuiGraphics graphics, HexGrids.Axial axial) {
        if (axial == null) {
            return;
        }
        HexGrids.DoubledOffset doubledOffset = axial.toDoubledOffset();

        int x = doubledOffset.col() * HORIZONTAL_SPACING / 2;
        int y = doubledOffset.row() * VERTICAL_SPACING;

        final int uOffset = 212;
        final int vOffset = 29;

        graphics.blit(RenderType::guiTextured, BACKGROUND_LOCATION,
                x, y,
                uOffset, vOffset,
                GRID_WIDTH, GRID_HEIGHT,
                256, 256
        );
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);

        if (this.hoveringHexSlot != null) {
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(Component.literal("Hex slot: (" + this.hoveringHexSlot.q() + ", " + this.hoveringHexSlot.r() + ")"));
            ItemStack stack = this.menu.getHexSlotItem(this.hoveringHexSlot);
            if (!stack.isEmpty()) {
                List<Component> tooltipFromContainerItem = this.getTooltipFromContainerItem(stack);
                tooltips.addAll(tooltipFromContainerItem);
            }

            guiGraphics.renderTooltip(this.font, tooltips, Optional.empty(), x, y, null);

        }
    }

    private void renderHexGrid(GuiGraphics graphics) {

        int x0 = this.leftPos + VIEWPORT_TOP_LEFT_X;
        int y0 = this.topPos + VIEWPORT_TOP_LEFT_Y;


        final int uOffset = 212;
        final int vOffset = 0;


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
        for (HexGrids.Axial axial : this.menu.getSlotPositions()) {
            HexGrids.DoubledOffset doubledOffset = axial.toDoubledOffset();

            int x = doubledOffset.col() * HORIZONTAL_SPACING / 2;
            int y = doubledOffset.row() * VERTICAL_SPACING;


            graphics.blit(RenderType::guiTextured, BACKGROUND_LOCATION,
                    x, y,
                    uOffset, vOffset,
                    GRID_WIDTH, GRID_HEIGHT,
                    256, 256
            );

        }


        graphics.pose().popPose();

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
            this.hexSlotClicked(this.hoveringHexSlot, button, false);
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
            this.hexSlotClicked(this.hoveringHexSlot, button, true);
        }

        return ret;

    }

    private void hexSlotClicked(HexGrids.Axial axial, int button, boolean release) {
        if (button != 0 && button != 1) {
            return;
        }

        ItemStack carried = this.menu.getCarried();
        if (release || carried.isEmpty()) {
            if (hasShiftDown()) {
                PacketDistributor.sendToServer(new HexSlotAction(this.menu.containerId, this.menu.getStateId(), axial, HexSlotAction.Action.QUICK_MOVE));
            } else {
                PacketDistributor.sendToServer(new HexSlotAction(this.menu.containerId, this.menu.getStateId(), axial, HexSlotAction.Action.PICK_OR_REPLACE));
            }

            if (!release) {
                this.skipNextRelease = true;
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
}
