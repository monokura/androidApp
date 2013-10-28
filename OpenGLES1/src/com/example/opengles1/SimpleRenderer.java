package com.example.opengles1;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

public class SimpleRenderer implements GLSurfaceView.Renderer {
    
    public interface Drawable {
        public void draw(GL10 gl);
    }
    
    Drawable obj;
    private float rx = 0, ry = 0, rz = 0;
    
    public SimpleRenderer(Drawable obj) {
        this.obj = obj;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, 0);
        gl.glRotatef(rx, 1, 0, 0);
        gl.glRotatef(ry, 0, 1, 0);
        gl.glRotatef(rz, 0, 0, 1);
        gl.glScalef(1, 1, 1);
        obj.draw(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45, ((float) width) / height, 1, 50);
        GLU.gluLookAt(gl, 0, 0, 4, 0, 0, 0, 0, 1, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
    }
    
    public void setRotationX(float th) {
        rx = th;
    }
    
    public void setRotationY(float th) {
        ry = th;
    }

    public void setRotationZ(float th) {
        rz = th;
    }

}
