package io.github.projeccttaichi.runechantshinai.client.util;

import io.github.projeccttaichi.runechantshinai.compoment.RecordComponent;
import io.github.projeccttaichi.runechantshinai.init.ModComponents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class RecordUtil {
    public static void renderRecord(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {
        RecordComponent component = stack.get(ModComponents.RECORD_COMPONENT);
        if (component != null) {
            if(!renderTestRecord(guiGraphics, component.id(), x, y)) {
                guiGraphics.renderItem(stack, x + 1, y + 1);
            }
        }

    }

    public static boolean renderTestRecord(GuiGraphics guiGraphics, ResourceLocation recordId, int x, int y) {
        if (!recordId.getPath().startsWith("test_")) {
            return false;
        }  // TEST CODE: fetch sprite from AI-generated sprite sheet:

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(recordId.getNamespace(), "textures/record/records.png");

        // Record id starts with test_ and a number
        // the first 49 record drawed
        // the sprite sheet is 22 * 22 gird and actual sprite is 18 * 18 in center

        int recordIdNumber = Integer.parseInt(recordId.getPath().substring(5));
        recordIdNumber %= 49;
        int spriteX = recordIdNumber % 7 * 22 + 2;
        int spriteY = recordIdNumber / 7 * 22 + 2;

        guiGraphics.blit(texture, x, y, spriteX, spriteY, 18, 18, 256, 256);
        return true;
    }

    public static void renderRecord(GuiGraphics guiGraphics, ResourceLocation recordId, int x, int y) {
        // TODO: implement this
    }

    public static void renderRecordCount(GuiGraphics guiGraphics, Font font, int count, int x, int y) {
        if (count > 0) {
            guiGraphics.pose().pushPose();

            String s = String.valueOf(count);
            guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
            guiGraphics.drawString(font, s, x + 21 - 2 - font.width(s), y + 8 + 3, 16777215, true);

            guiGraphics.pose().popPose();
        }

    }
}
