package com.example.openglsample3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGLSurfaceView = new GLSurfaceView(this);
		mGLSurfaceView.setRenderer(new BallRenderer());
		setContentView(mGLSurfaceView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLSurfaceView.onPause();
	}

	private GLSurfaceView mGLSurfaceView;
}

class BallRenderer implements GLSurfaceView.Renderer {
	private Ball mBall1, mBall2;
	private float mAngle;

	public BallRenderer() {
		mBall1 = new Ball(0.8f, 3, 1.0f, 0.0f, 0.0f);
		mBall2 = new Ball(0.5f, 2, 0.0f, 0.0f, 1.0f);
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, -3.0f);
		//gl.glRotatef(mAngle,        0, 1, 0);
		//gl.glRotatef(mAngle*0.25f,  1, 0, 0);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		mBall1.draw(gl);

		//gl.glRotatef(mAngle*2.0f, 0, 1, 1);
		//gl.glTranslatef(0.8f, 0.8f, 0.8f);

		//mBall2.draw(gl);

		mAngle += 1.2f;
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		float ratio = (float) width / height;

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glDisable(GL10.GL_DITHER);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		gl.glClearColor(0,0,0,0);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}
}

class Ball {
	private int mQuality;
	private float mRadius;
	private float mR, mG, mB;

	List<Float> mVerticesList = new ArrayList<Float>();

	private FloatBuffer	mVertexBuffer;

	public Ball(float radius, int quality, float r, float g, float b) {
		float vertices[];
		float vertices1[] = new float[3];
		float vertices2[] = new float[3];
		float vertices3[] = new float[3];
		float theta1, theta2;

		mRadius = radius;
		mQuality = quality;
		mR = r;
		mG = g;
		mB = b;

		for(int j = 0; j < 2; j++) {
			for(int i = 0; i < 4; i++) {
				if(j == 0) {
					theta1 = 2.0f / 4 * (float)i * (float)Math.PI;
					theta2 = 2.0f / 4 * (float)(i + 1) * (float)Math.PI;
				} else {
					theta1 = 2.0f / 4 * (float)(i + 1) * (float)Math.PI;
					theta2 = 2.0f / 4 * (float)i * (float)Math.PI;
				}

				vertices1[0] = (float)Math.cos((double)theta1) * mRadius;
				vertices1[1] = (float)Math.sin((double)theta1) * mRadius;
				vertices1[2] = 0.0f;

				vertices2[0] = 0.0f;
				vertices2[1] = 0.0f;
				if(j == 0) {
					vertices2[2] = mRadius;
				} else {
					vertices2[2] = -mRadius;
				}

				vertices3[0] = (float)Math.cos((double)theta2) * mRadius;
				vertices3[1] = (float)Math.sin((double)theta2) * mRadius;
				vertices3[2] = 0.0f;

				getTriangleDivide(vertices1, vertices2, vertices3, mRadius, mQuality);
			}
		}

		vertices = new float[mVerticesList.size()];
		for(int i = 0; i < vertices.length; i++) {
			vertices[i] = mVerticesList.get(i).floatValue();
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
	}

	public void draw(GL10 gl)
	{
		gl.glFrontFace(GL10.GL_CW);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		for(int i = 0; i < mVertexBuffer.limit() / (3 * 3); i++) {
			gl.glColor4f(mR / 4 * (i % 4 + 1), mG / 4 * (i % 4 + 1), mB / 4 * (i % 4 + 1), 1.0f);
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			gl.glDrawArrays(GL10.GL_TRIANGLES, i * 3, 3);
		}
    }

	public void getTriangleDivide(
			float vertices1[], float vertices2[], float vertices3[], float radius, int level) {
		if(level > 0) {
			float corner1[], corner2[], corner3[];
			float divide1[], divide2[], divide3[];

			corner1 = getVertexPosition(vertices1[0], vertices1[1], vertices1[2],radius);
			corner2 = getVertexPosition(vertices2[0], vertices2[1], vertices2[2],radius);
			corner3 = getVertexPosition(vertices3[0], vertices3[1], vertices3[2],radius);

			divide1 = getVertexPosition(
					(vertices1[0] + vertices2[0]) / 2,
					(vertices1[1] + vertices2[1]) / 2,
					(vertices1[2] + vertices2[2]) / 2,
					radius);

			divide2 = getVertexPosition(
					(vertices1[0] + vertices3[0]) / 2,
					(vertices1[1] + vertices3[1]) / 2,
					(vertices1[2] + vertices3[2]) / 2,
					radius);

			divide3 = getVertexPosition(
					(vertices2[0] + vertices3[0]) / 2,
					(vertices2[1] + vertices3[1]) / 2,
					(vertices2[2] + vertices3[2]) / 2,
					radius);

			getTriangleDivide(corner1, divide1, divide2, radius, level - 1);
			getTriangleDivide(corner2, divide3, divide1, radius, level - 1);
			getTriangleDivide(divide2, divide3, corner3, radius, level - 1);
			getTriangleDivide(divide3, divide2, divide1, radius, level - 1);
		} else {
			mVerticesList.add(vertices1[0]);
			mVerticesList.add(vertices1[1]);
			mVerticesList.add(vertices1[2]);

			mVerticesList.add(vertices2[0]);
			mVerticesList.add(vertices2[1]);
			mVerticesList.add(vertices2[2]);

			mVerticesList.add(vertices3[0]);
			mVerticesList.add(vertices3[1]);
			mVerticesList.add(vertices3[2]);
		}
	}

	public float[] getVertexPosition(float x, float y, float z, float radius) {
		float length;
		float point[] = new float[3];

		length = (float)Math.sqrt(x * x + y * y + z * z);
		point[0] = x / length * radius;
		point[1] = y / length * radius;
		point[2] = z / length * radius;

		return point;
	}
}
          