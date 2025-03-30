package io.github.projeccttaichi.runechantshinai.util;

import net.minecraft.network.codec.StreamCodec;
import org.joml.Matrix2d;

import java.util.ArrayList;
import java.util.List;

public class HexGrids {
    public record Axial(int q, int r) {

        public Cube toCube() {
            return HexGrids.axialToCube(this);
        }

        public Offset toOffset() {
            return HexGrids.axialToOddROffset(this);
        }

        public DoubledOffset toDoubledOffset() {
            return HexGrids.axialToDoubledOffset(this);
        }

        public Axial add(Axial other) {
            return new Axial(this.q() + other.q(), this.r() + other.r());
        }

        public Axial subtract(Axial other) {
            return new Axial(this.q() - other.q(), this.r() - other.r());
        }

        public int length() {
            return this.toCube().length();
        }

        public int packed() {
            return (this.q() << 16) | (this.r() & 0xFFFF);
        }

        public static Axial unpacked(int packed) {
            return new Axial(packed >> 16, packed & 0xFFFF);
        }

    }

    public record Cube(int q, int r, int s) {

        public Axial toAxial() {
            return HexGrids.cubeToAxial(this);
        }

        public Offset toOffset() {
            return this.toAxial().toOffset();
        }

        public Cube subtract(Cube other) {
            return new Cube(this.q() - other.q(), this.r() - other.r(), this.s() - other.s());
        }

        public Cube add(Cube other) {
            return new Cube(this.q() + other.q(), this.r() + other.r(), this.s() + other.s());
        }

        public int length() {
            return Math.max(Math.abs(this.q()), Math.max(Math.abs(this.r()), Math.abs(this.s())));
        }

    }

    public record Offset(int col, int row) {

        public Axial toAxial() {
            return HexGrids.oddROffsetToAxial(this);
        }

        public Cube toCube() {
            return this.toAxial().toCube();
        }
    }


    public record DoubledOffset(int col, int row) {

        public Axial toAxial() {
            return HexGrids.doubledOffsetToAxial(this);
        }

        public Cube toCube() {
            return this.toAxial().toCube();
        }
    }


    private static Axial doubledOffsetToAxial(DoubledOffset doubledOffset) {
        int q = (doubledOffset.col() - doubledOffset.row()) / 2;
        int r = doubledOffset.row();
        return new Axial(q, r);
    }

    private static DoubledOffset axialToDoubledOffset(Axial axial) {
        int col = 2 * axial.q() + axial.r();
        int row = axial.r();
        return new DoubledOffset(col, row);
    }

    public static Matrix2d pixel2HexMatrix(int width, int height) {
        double f0 = Math.sqrt(3.0);

        final double xScale = (width + 2.0) / f0;
        final double yScale = (height + 2.0 + 2.0 / 3.0) / 2.0;

        return new Matrix2d(
                f0 / 3.0 / xScale, 0.0,
                -1.0 / 3.0 / yScale, 2.0 / 3.0 / yScale
        );

    }

    public static Cube cubeRound(double q, double r, double s) {
        int qi = (int) Math.round(q);
        int ri = (int) Math.round(r);
        int si = (int) Math.round(s);

        double qDiff = Math.abs(qi - q);
        double rDiff = Math.abs(ri - r);
        double sDiff = Math.abs(si - s);

        if (qDiff > rDiff && qDiff > sDiff) {
            qi = -ri - si;
        } else if (rDiff > sDiff) {
            ri = -qi - si;
        } else {
            si = -qi - ri;
        }

        return new Cube(qi, ri, si);
    }

    public static Axial axialRound(double q, double r) {
        return cubeRound(q, r, -q - r).toAxial();
    }


    private static Cube axialToCube(Axial axial) {
        int q = axial.q();
        int r = axial.r();
        int s = -q - r;
        return new Cube(q, r, s);
    }

    private static Axial cubeToAxial(Cube cube) {
        int q = cube.q();
        int r = cube.r();
        return new Axial(q, r);
    }

    private static Offset axialToOddROffset(Axial axial) {
        int col = axial.q() + (axial.r() - (axial.r() & 1)) / 2;
        int row = axial.r();
        return new Offset(col, row);
    }

    private static Axial oddROffsetToAxial(Offset offset) {
        int q = offset.col() - (offset.row() - (offset.row() & 1)) / 2;
        int r = offset.row();
        return new Axial(q, r);
    }


    public static int movementRange(int radius) {
        return 1 + 3 * radius * (radius + 1);
    }

    public static List<Axial> axialRange(int radius) {

        List<Axial> results = new ArrayList<>(movementRange(radius));

        for (int q = -radius; q <= radius; q++) {
            for (int r = Math.max(-radius, -q - radius); r <= Math.min(radius, -q + radius); r++) {
                results.add(new Axial(q, r));
            }
        }
        return results;
    }


    enum Direction {

        UP_RIGHT(1, -1, 0),
        RIGHT(1, 0, -1),
        DOWN_RIGHT(0, 1, -1),
        DOWN_LEFT(-1, 1, 0),
        LEFT(-1, 0, 1),
        UP_LEFT(0, -1, 1);

        Direction(int q, int r, int s) {
            this.cube = new Cube(q, r, s);
        }

        static final Direction[] directions = Direction.values();

        public Direction opposite() {
            return directions[(ordinal() + 3) % 6];
        }

        public Direction clockwise() {
            return directions[(ordinal() + 1) % 6];
        }

        public Direction counterClockwise() {
            return directions[(ordinal() + 5) % 6];
        }

        private final Cube cube;

        public Cube cube() {
            return cube;
        }

        public Axial axial() {
            return cube.toAxial();
        }

    }


}