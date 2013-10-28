package com.example.openglsample2;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

public class MyRenderer implements GLSurfaceView.Renderer {

	//MyCube cube = new MyCube();
	MySphere sp;
	
	public MyRenderer(){
		sp = new MySphere(0.8f,3, 1.0f, 0.0f, 0.0f);	
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(1, 1, 0);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		sp.draw(gl);
		//cube.draw(gl);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();    
		GLU.gluPerspective(gl, 45f,(float) width / height, 1f, 50f);
		GLU.gluLookAt(gl, 
				0, 0, 10, // pos
				0, 0, 0, // center
				0, 1, 0  // top
				);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		gl.glDisable(GL10.GL_DITHER);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		gl.glClearColor(1,1,1,0);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}


}
