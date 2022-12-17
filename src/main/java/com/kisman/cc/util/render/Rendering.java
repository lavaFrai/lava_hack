package com.kisman.cc.util.render;

import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.cubic.BoundingBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

/**
 * Is not skidded class
 * _kisman_ wanted this  - Cubic
 */
public class Rendering {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static Tessellator tessellator = Tessellator.getInstance();

    public static BufferBuilder bufferbuilder = tessellator.getBuffer();

    public static Colour DUMMY_COLOR = new Colour(0, 0, 0, 0);

    public enum RenderObject {
        BOX {
            @Override
            void draw(AxisAlignedBB aabb, Color color1, Color color2, boolean gradient, Object... values) {
                if(gradient) {
                    prepare();
                    drawGradientFilledBox(aabb, color1, color2);
                    restore();
                } else drawSelectionBox(aabb, color1);
            }
        },
        OUTLINE {
            @Override
            void draw(AxisAlignedBB aabb, Color color1, Color color2, boolean gradient, Object... values) {
                if(gradient) {
                    prepare();
                    drawGradientBlockOutline(aabb, color1, color2, (float) values[0]);
                    restore();
                } else {
                    glLineWidth((float) values[0]);
                    drawSelectionBoundingBox(aabb, color1);
                }
            }
        },
        WIRE {
            @Override
            void draw(AxisAlignedBB aabb, Color color1, Color color2, boolean gradient, Object... values) {
                glPushMatrix();

                OutlineUtils.setColor(color1);
                OutlineUtils.renderOne((float) values[0]);

                drawDummyBox(aabb);

                OutlineUtils.renderTwo();

                drawDummyBox(aabb);

                OutlineUtils.renderThree();
                OutlineUtils.renderFour(Color.WHITE.getRGB()/*color2.getRGB()*/, 1f);
                OutlineUtils.setColor(gradient ? color2 : color1);

                drawDummyBox(aabb);

                OutlineUtils.renderFive(-1f);
                OutlineUtils.setColor(Color.WHITE);

                glPopMatrix();
            }

            private void drawDummyBox(AxisAlignedBB aabb) {
                drawSelectionBox(aabb, DUMMY_COLOR.getColor());
            }
        }

        ;

        abstract void draw(AxisAlignedBB aabb, Color color1, Color color2, boolean gradient, Object... values);
        
        public void draw(AxisAlignedBB aabb, Color color, Object... values) {
            draw(aabb, color, color, false, values);
        }
        
        public void draw(AxisAlignedBB aabb, Color color1, Color color2, Object... values) {
            draw(aabb, color1, color2, true, values);
        }
    }

    public enum Mode {
        OUTLINE(false, RenderObject.OUTLINE),
        BOX(false, RenderObject.BOX),
        WIRE(false, RenderObject.WIRE),
        BOX_OUTLINE(false, RenderObject.BOX, RenderObject.OUTLINE),
        BOX_WIRE(false, RenderObject.BOX, RenderObject.WIRE),
        WIRE_OUTLINE(false, RenderObject.OUTLINE, RenderObject.WIRE),
        BOX_WIRE_OUTLINE(false, RenderObject.BOX, RenderObject.OUTLINE, RenderObject.WIRE),
        WIRE_GRADIENT(true, RenderObject.WIRE),
        BOX_GRADIENT(true, RenderObject.BOX),
        OUTLINE_GRADIENT(true, RenderObject.OUTLINE),
        BOX_OUTLINE_GRADIENT(true, RenderObject.BOX, RenderObject.OUTLINE),
        BOX_WIRE_GRADIENT(true, RenderObject.BOX, RenderObject.WIRE),
        WIRE_OUTLINE_GRADIENT(true, RenderObject.OUTLINE, RenderObject.WIRE),
        BOX_WIRE_OUTLINE_GRADIENT(true, RenderObject.BOX, RenderObject.OUTLINE, RenderObject.WIRE, RenderObject.BOX)

        ;

        public final ArrayList<RenderObject> objects;
        public final boolean gradient;

        Mode(boolean gradient, RenderObject... objects) {
            this.gradient = gradient;
            this.objects = new ArrayList<>(Arrays.asList(objects));
        }
        
        public void draw(AxisAlignedBB aabb, Color filledColor1, Color filledColor2, Color outlineColor1, Color outlineColor2, Color wireColor1, Color wireColor2, Object... values) {
            for(RenderObject object : objects) {
                //TODO: cleanup it!!!!
                if(object == RenderObject.BOX) {
                    object.draw(aabb, filledColor1, filledColor2, gradient, values);
                } else if(object == RenderObject.OUTLINE) {
                    object.draw(aabb, outlineColor1, outlineColor2, gradient, values);
                } else if(object == RenderObject.WIRE) {
                    object.draw(aabb, wireColor1, wireColor2, gradient, values);
                }
            }
        }
    }

    public static void setup(){
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ONE);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(1.5f);
    }

    public static void setup(boolean depth){
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        if(!depth) GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ONE);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(1.5f);
    }

    public static void release(){
        glDisable(GL_LINE_SMOOTH);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void release(boolean depth){
        glDisable(GL_LINE_SMOOTH);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.depthMask(true);
        if(!depth) GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void prepare(){
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL_SMOOTH);
    }

    public static void restore(){
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(GL_FLAT);
    }

    public static void draw0(AxisAlignedBB axisAlignedBB, float lineWidth, Colour c, Colour c1, Mode mode) {
        draw0(axisAlignedBB, lineWidth, c, c1, c.withAlpha(255), c1.withAlpha(255), c.withAlpha(255), c1.withAlpha(255), mode);
    }

    public static void draw0(AxisAlignedBB axisAlignedBB, float lineWidth, Colour c, Colour c1, Colour c2, Colour c3, Colour c4, Colour c5, Mode mode) {
        Color filledColor1 = c.getColor();
        Color filledColor2 = c1.getColor();
        Color outlineColor1 = c2.getColor();
        Color outlineColor2 = c3.getColor();
        Color wireColor1 = c4.getColor();
        Color wireColor2 = c5.getColor();

        System.out.println(c.a + " " + filledColor1.getAlpha());
        
        mode.draw(axisAlignedBB, filledColor1, filledColor2, outlineColor1, outlineColor2, wireColor1, wireColor2, lineWidth);
    }

    public static void draw(AxisAlignedBB axisAlignedBB, float lineWidth, Colour c, Colour c1, Mode mode){
        setup();
        draw0(axisAlignedBB, lineWidth, c, c1, mode);
        release();
    }

    public static AxisAlignedBB correct(AxisAlignedBB aabb){
        return new AxisAlignedBB(aabb.minX - mc.getRenderManager().viewerPosX, aabb.minY - mc.getRenderManager().viewerPosY, aabb.minZ - mc.getRenderManager().viewerPosZ, aabb.maxX - mc.getRenderManager().viewerPosX, aabb.maxY - mc.getRenderManager().viewerPosY, aabb.maxZ - mc.getRenderManager().viewerPosZ);
    }

    // possible not working
    public static BlockPos correct(BlockPos pos){
        AxisAlignedBB abb = correct(new AxisAlignedBB(pos));
        return new BlockPos(abb.minX, abb.minY, abb.minZ);
    }

    public static AxisAlignedBB scale(BlockPos pos, double scale){
        double s = scale * 0.5;
        double x1 = (pos.getX() + 0.5) - s;
        double y1 = (pos.getY() + 0.5) - s;
        double z1 = (pos.getZ() + 0.5) - s;
        double x2 = (pos.getX() + 0.5) + s;
        double y2 = (pos.getY() + 0.5) + s;
        double z2 = (pos.getZ() + 0.5) + s;
        return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    public static AxisAlignedBB scale(AxisAlignedBB bb, double scale) {
        double s = scale * 0.5;
        return new AxisAlignedBB(
                bb.minX - s,
                bb.minY - s,
                bb.minZ - s,
                bb.maxX + s,
                bb.maxY + s,
                bb.maxZ + s
        );
    }

    public static BoundingBox animateMove(BoundingBox origin, BoundingBox destination, float partialTicks, float lengthPartialTicks){
        float m = partialTicks / lengthPartialTicks;
        double maxX = origin.maxX + ((destination.maxX - origin.maxX) * m) + (((destination.maxX - destination.minX) - (origin.maxX - origin.minX)) * 0.5 * m);
        double maxY = origin.maxY + ((destination.maxY - origin.maxY) * m) + (((destination.maxY - destination.minY) - (origin.maxY - origin.minY)) * 0.5 * m);
        double maxZ = origin.maxZ + ((destination.maxZ - origin.maxZ) * m) + (((destination.maxZ - destination.minZ) - (origin.maxZ - origin.minZ)) * 0.5 * m);
        double minX = origin.minX + ((destination.maxX - origin.maxX) * m) + (((destination.maxX - destination.minX) - (origin.maxX - origin.minX)) * 0.5 * m);
        double minY = origin.minY + ((destination.maxY - origin.maxY) * m) + (((destination.maxY - destination.minY) - (origin.maxY - origin.minY)) * 0.5 * m);
        double minZ = origin.minZ + ((destination.maxZ - origin.maxZ) * m) + (((destination.maxZ - destination.minZ) - (origin.maxZ - origin.minZ)) * 0.5 * m);
        return new BoundingBox(new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
    }

    public static void drawTripleGradient(AxisAlignedBB aabb, Colour colour1, Colour colour2, Colour colour3){
        double yAdd = (aabb.maxY - aabb.minY) * 0.5;
        AxisAlignedBB aabb1 = new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY + yAdd, aabb.maxZ);
        AxisAlignedBB aabb2 = new AxisAlignedBB(aabb.minX, aabb.minY + yAdd, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
        prepare();
        drawGradientFilledBox(aabb1, colour1.getColor(), colour2.getColor());
        drawGradientFilledBox(aabb2, colour2.getColor(), colour3.getColor());
        restore();
    }

    public static void drawTripleGradient2(AxisAlignedBB aabb, Colour colour1, Colour colour2, Colour colour3){
        double yAdd = (aabb.maxY - aabb.minY) * 0.5;
        AxisAlignedBB aabb1 = new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY + yAdd, aabb.maxZ);
        AxisAlignedBB aabb2 = new AxisAlignedBB(aabb.minX, aabb.minY + yAdd, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
        setup();
        prepare();
        drawTopOpenGradientBox(aabb2, colour1.getColor(), colour2.getColor());
        drawBottomOpenedGradientBox(aabb1, colour2.getColor(), colour3.getColor());
        restore();
        release();
    }

    public static void drawTopOpenGradientBox(AxisAlignedBB bb, Color startColor, Color endColor){
        float alpha = (float) endColor.getAlpha() / 255.0f;
        float red = (float) endColor.getRed() / 255.0f;
        float green = (float) endColor.getGreen() / 255.0f;
        float blue = (float) endColor.getBlue() / 255.0f;
        float alpha1 = (float) startColor.getAlpha() / 255.0f;
        float red1 = (float) startColor.getRed() / 255.0f;
        float green1 = (float) startColor.getGreen() / 255.0f;
        float blue1 = (float) startColor.getBlue() / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        //bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        //bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        //bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        //bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
    }

    public static void drawBottomOpenedGradientBox(AxisAlignedBB bb, Color startColor, Color endColor){
        float alpha = (float) endColor.getAlpha() / 255.0f;
        float red = (float) endColor.getRed() / 255.0f;
        float green = (float) endColor.getGreen() / 255.0f;
        float blue = (float) endColor.getBlue() / 255.0f;
        float alpha1 = (float) startColor.getAlpha() / 255.0f;
        float red1 = (float) startColor.getRed() / 255.0f;
        float green1 = (float) startColor.getGreen() / 255.0f;
        float blue1 = (float) startColor.getBlue() / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        //bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        //bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        //bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        //bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
    }

    public static void drawSelectionBox(AxisAlignedBB axisAlignedBB, Color color) {
        bufferbuilder.begin(GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        addChainedFilledBoxVertices(bufferbuilder, axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color);
        tessellator.draw();
    }

    public static void addChainedFilledBoxVertices(BufferBuilder builder, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color color) {
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB axisAlignedBB, Color color) {
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        addChainedBoundingBoxVertices(bufferbuilder, axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color);
        tessellator.draw();
    }

    public static void addChainedBoundingBoxVertices(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color color) {
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
    }

    public static void drawGradientFilledBox(AxisAlignedBB bb, Color startColor, Color endColor) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int) 770, (int) 771, (int) 0, (int) 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean) false);
        float alpha = (float) endColor.getAlpha() / 255.0f;
        float red = (float) endColor.getRed() / 255.0f;
        float green = (float) endColor.getGreen() / 255.0f;
        float blue = (float) endColor.getBlue() / 255.0f;
        float alpha1 = (float) startColor.getAlpha() / 255.0f;
        float red1 = (float) startColor.getRed() / 255.0f;
        float green1 = (float) startColor.getGreen() / 255.0f;
        float blue1 = (float) startColor.getBlue() / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.depthMask((boolean) true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawGradientBlockOutline(AxisAlignedBB bb, Color startColor, Color endColor, float linewidth) {
        float red = (float)startColor.getRed() / 255.0f;
        float green = (float)startColor.getGreen() / 255.0f;
        float blue = (float)startColor.getBlue() / 255.0f;
        float alpha = (float)startColor.getAlpha() / 255.0f;
        float red1 = (float)endColor.getRed() / 255.0f;
        float green1 = (float)endColor.getGreen() / 255.0f;
        float blue1 = (float)endColor.getBlue() / 255.0f;
        float alpha1 = (float)endColor.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)linewidth);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable((int)2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /**
     * @author Cubic
     *
     * Work in progress
     */
    public static void drawTripleGradientBox(AxisAlignedBB bb, Color c1, Color c2, Color c3){
        double dY = (bb.maxY - bb.minY) / 2.0;
        AxisAlignedBB bb1 = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY + dY, bb.maxZ);
        AxisAlignedBB bb2 = new AxisAlignedBB(bb.minX, bb.minY + dY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        int red1 = c1.getRed();
        int green1 = c1.getGreen();
        int blue1 = c1.getBlue();
        int alpha1 = c1.getAlpha();
        int red2 = c2.getRed();
        int green2 = c2.getGreen();
        int blue2 = c2.getBlue();
        int alpha2 = c2.getAlpha();
        int red3 = c3.getRed();
        int green3 = c3.getGreen();
        int blue3 = c3.getBlue();
        int alpha3 = c3.getAlpha();
        setup();
        prepare();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        buf.pos(bb1.minX, bb1.minY, bb1.minZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.minY, bb1.minZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.minY, bb1.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.minX, bb1.minY, bb1.maxZ).color(red1, green1, blue1, alpha1).endVertex();

        buf.pos(bb1.minX, bb1.minY, bb1.minZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.minY, bb1.minZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.maxY, bb1.minZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb1.minX, bb1.maxY, bb1.minZ).color(red2, green2, blue2, alpha2).endVertex();

        buf.pos(bb1.minX, bb1.minY, bb1.minZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.minX, bb1.minY, bb1.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.minX, bb1.maxY, bb1.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb1.minX, bb1.maxY, bb1.minZ).color(red2, green2, blue2, alpha2).endVertex();

        buf.pos(bb1.minX, bb1.minY, bb1.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.minY, bb1.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.maxY, bb1.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb1.minX, bb1.maxY, bb1.maxZ).color(red2, green2, blue2, alpha2).endVertex();

        buf.pos(bb1.maxX, bb1.minY, bb1.minZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.minY, bb1.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.maxY, bb1.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb1.maxX, bb1.maxY, bb1.minZ).color(red2, green2, blue2, alpha2).endVertex();

        buf.pos(bb2.minX, bb2.minY, bb2.minZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.maxX, bb2.minY, bb2.minZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.maxX, bb2.maxY, bb2.minZ).color(red3, green3, blue3, alpha3).endVertex();
        buf.pos(bb2.minX, bb2.maxY, bb2.minZ).color(red3, green3, blue3, alpha3).endVertex();

        buf.pos(bb2.minX, bb2.minY, bb2.minZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.minX, bb2.minY, bb2.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.minX, bb2.maxY, bb2.maxZ).color(red3, green3, blue3, alpha3).endVertex();
        buf.pos(bb2.minX, bb2.maxY, bb2.minZ).color(red3, green3, blue3, alpha3).endVertex();

        buf.pos(bb2.minX, bb2.minY, bb2.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.maxX, bb2.minY, bb2.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.maxX, bb2.maxY, bb2.maxZ).color(red3, green3, blue3, alpha3).endVertex();
        buf.pos(bb2.minX, bb2.maxY, bb2.maxZ).color(red3, green3, blue3, alpha3).endVertex();

        buf.pos(bb2.maxX, bb2.minY, bb2.minZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.maxX, bb2.minY, bb2.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.maxX, bb2.maxY, bb2.maxZ).color(red3, green3, blue3, alpha3).endVertex();
        buf.pos(bb2.maxX, bb2.maxY, bb2.minZ).color(red3, green3, blue3, alpha3).endVertex();

        buf.pos(bb2.minX, bb2.minY, bb2.minZ).color(red3, green3, blue3, alpha3).endVertex();
        buf.pos(bb2.maxX, bb2.minY, bb2.minZ).color(red3, green3, blue3, alpha3).endVertex();
        buf.pos(bb2.maxX, bb2.minY, bb2.maxZ).color(red3, green3, blue3, alpha3).endVertex();
        buf.pos(bb2.minX, bb2.minY, bb2.maxZ).color(red3, green3, blue3, alpha3).endVertex();

        tessellator.draw();
        restore();
        release();
    }

    /**
     * @author Cubic
     */
    public static void drawChrome(AxisAlignedBB bb, EnumFacing facing, Color c1, Color c2, Color c3, Color c4){
        setup();
        prepare();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        switch(facing){
            case UP:
                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();
                break;
            case DOWN:
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();
                break;
            case NORTH:
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();
                break;
            case EAST:
                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();
                break;
            case SOUTH:
                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();
                break;
            case WEST:
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();
                break;
        }
        tessellator.draw();
        restore();
        release();
    }

    /**
     * WIP
     * @author Cubic
     */
    public static void drawChromaOutline(AxisAlignedBB bb, EnumFacing facing, float lineWidth, Color c1, Color c2, Color c3, Color c4){
        setup();
        prepare();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        GL11.glLineWidth(lineWidth);
        buf.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        switch(facing){
            case UP:
                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());

                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());
                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());

                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());
                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());

                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());
                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                break;
            case DOWN:
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());

                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());
                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());

                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());
                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());

                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                break;
            case NORTH:
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());

                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());
                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());

                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());
                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());

                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                break;
            case SOUTH:
                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());

                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());
                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());

                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());
                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());

                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());
                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                break;
            case EAST:
                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());

                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());
                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());

                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());
                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());

                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());
                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                break;
            case WEST:
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());

                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());
                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());

                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());
                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());

                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                break;
        }
        tessellator.draw();
        restore();
        release();
    }

    public static void drawChromaBox(AxisAlignedBB bb, Color c1, Color c2, Color c3, Color c4){
        setup();
        prepare();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // Up
        buf.pos(bb.minX, bb.maxY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();

        // Down
        buf.pos(bb.minX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.minY, bb.minZ).color(c1.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        buf.pos(bb.minX, bb.minY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();

        // North
        buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();

        // South
        buf.pos(bb.maxX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();

        // East
        buf.pos(bb.minX, bb.minY, bb.maxZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();

        // West
        buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
        buf.pos(bb.minX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();

        tessellator.draw();
        restore();
        release();
    }
}
