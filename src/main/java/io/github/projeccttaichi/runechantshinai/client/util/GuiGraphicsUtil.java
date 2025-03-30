package io.github.projeccttaichi.runechantshinai.client.util;

import net.minecraft.client.gui.GuiGraphics;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class GuiGraphicsUtil {

    public static void enableScissorTransformed(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY) {

        Matrix4f pose = guiGraphics.pose().last().pose();

        Vector3f topLeft = new Vector3f(minX, minY, 0);
        Vector3f bottomRight = new Vector3f(maxX, maxY, 0);

        topLeft = pose.transformPosition(topLeft);
        bottomRight = pose.transformPosition(bottomRight);

        int minX1 = (int) Math.floor(topLeft.x());
        int minY1 = (int) Math.floor(topLeft.y());

        int width1 = (int) Math.floor(bottomRight.x() - topLeft.x());
        int height1 = (int) Math.floor(bottomRight.y() - topLeft.y());

        guiGraphics.enableScissor(minX1, minY1, minX1 + width1, minY1 + height1);

    }
}
