package io.github.projeccttaichi.runechantshinai.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.projeccttaichi.runechantshinai.client.gui.widget.RecordListView;
import io.github.projeccttaichi.runechantshinai.client.util.RecordRenderUtil;
import io.github.projeccttaichi.runechantshinai.menu.RecordChestMenu;
import io.github.projeccttaichi.runechantshinai.network.c2s.CustomSlotAction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.guiLoc;

public class RecordChestScreen extends AbstractContainerScreen<RecordChestMenu> implements CustomSlotEventHandler {
    private static final ResourceLocation TEXTURE = guiLoc("container/record-chest.png");


    private final RecordListView listView;

    public RecordChestScreen(RecordChestMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;

        this.listView = new RecordListView(0, menu.getModel(), this);
    }

    @Override
    protected void init() {
        super.init();

        this.listView.init(
                this.font,
                this.leftPos + 5,
                this.topPos + 5,
                166,
                130,
                7,
                5,
                this.leftPos + 9,
                this.topPos + 26,
                this.leftPos + 164,
                this.topPos + 6,
                130,
                this.slotColor
        );
        this.addRenderableWidget(this.listView);
    }

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

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);

        this.listView.renderTooltip(guiGraphics, x, y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    protected void renderFloatingItem(GuiGraphics guiGraphics, ItemStack stack, int x, int y, String text) {
        if(!RecordRenderUtil.renderFloatingRecord(guiGraphics, font, stack, x, y))  {
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
} 