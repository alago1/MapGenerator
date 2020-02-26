package com.example.mapgenerator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class TriangleMesh {

    private FloatBuffer verticesBuffer;
    private ShortBuffer faceBuffer;

    public TriangleMesh(float[] vertices){ //vertices should be in counter-clockwise order
        ByteBuffer vertexBB = ByteBuffer.allocateDirect(3*4); // 3 vertices, 4 bytes per vertex
        vertexBB.order(ByteOrder.nativeOrder());
        verticesBuffer = vertexBB.asFloatBuffer();

        ByteBuffer faceBB = ByteBuffer.allocateDirect(3*2); // 3 connections, 2 bytes per vertex
        faceBB.order(ByteOrder.nativeOrder());
        faceBuffer = faceBB.asShortBuffer();

        verticesBuffer.put(vertices[0]);
        verticesBuffer.put(vertices[1]);
        verticesBuffer.put(vertices[2]);

        faceBuffer.put((short) vertices[0]);
        faceBuffer.put((short) vertices[1]);
        faceBuffer.put((short) vertices[2]);

        verticesBuffer.position(0);
        faceBuffer.position(0);
    }
}
