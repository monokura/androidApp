package com.example.openglsmaple4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class MyCircle {

	private FloatBuffer	mVertexBuffer;

	public MyCircle(){
		
	}
	
	public void draw(GL10 gl,float x, float y, float z,float radius) {
		int div = 32;
		float[] vertices = new float[div*3*3];
		int count = 0;
		for (int i = 0; i < div; i++) {
			float theta1 = (float) (2.0f /div*i*Math.PI);
			float theta2 = (float) (2.0f /div*(i+1)*Math.PI);
			vertices[count++] = x;
			vertices[count++] = y;
			vertices[count++] = z;
			vertices[count++] = (float)Math.cos((double)theta1)*radius+x;
			vertices[count++] = (float)Math.sin((double)theta1)*radius+y;
			vertices[count++] = z;
			vertices[count++] = (float)Math.cos((double)theta2)*radius+x;
			vertices[count++] = (float)Math.sin((double)theta2)*radius+y;
			vertices[count++] = z;
		}
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(vertices));
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, div*3);
	}
	
	
	public FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb=ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb=bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}
}
