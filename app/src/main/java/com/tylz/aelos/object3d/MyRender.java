package com.tylz.aelos.object3d;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;
import com.tylz.aelos.base.BaseApplication;
import com.tylz.aelos.manager.Constants;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

@SuppressLint("NewApi")
public class MyRender
        extends GLSurfaceView
        implements GLSurfaceView.Renderer
{

    private long        time  = System.nanoTime();
    private FrameBuffer fb    = null;
    private Light       sun   = null;
    private World       world = null;
    private Object3D    cube  = null;
    private int         fps   = 0;
    private Object3D rockModel;
    private Context  mContext;
    private float touchTurn   = 0;
    private float touchTurnUp = 0;

    public MyRender(Context context, Object3D object3D) {
        super(context);
        mContext = context;
        setZOrderOnTop(true);
        setZOrderOnTop(true);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        rockModel = object3D;
        setRenderer(this);

    }


    public void onSurfaceChanged(GL10 gl, int w, int h)
    {
        if (fb != null) {
            fb.dispose();
        }
        fb = new FrameBuffer(gl, w, h);
        GLES20.glViewport(0, 0, w, h);

    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);
        world = new World();
        world.setAmbientLight(150, 150, 150);
        Drawable drawable = mContext.getResources()
                                    .getDrawable(Constants.MODEL_TEXTURE);
        Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(drawable),
                                                           512,
                                                           512));
        try {
            TextureManager.getInstance()
                          .addTexture(Constants.MODEL_TEXTURE_NAME, texture);
        }catch (Exception e){
            e.printStackTrace();
        }
        rockModel.setTexture(Constants.MODEL_TEXTURE_NAME);
        texture.removeAlpha();
        cube = Primitives.getCube(10);
        cube.build();
        rockModel.build();
        if (!BaseApplication.isLoadConfigModel) {
            rockModel.translate(0, 7.5f, 2);
            BaseApplication.isLoadConfigModel = true;
        }
        world.addObject(rockModel);
        sun = new Light(world);
        sun.setIntensity(255, 255, 255);
        Camera cam = world.getCamera();
        cam.moveCamera(Camera.CAMERA_MOVEOUT, 10);
        cam.lookAt(cube.getTransformedCenter());
        SimpleVector sv = new SimpleVector();
        sv.set(rockModel.getTransformedCenter());
        sun.setPosition(sv);
        MemoryHelper.compact();
    }

    public void onDrawFrame(GL10 gl)
    {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        world.renderScene(fb);
        world.draw(fb);
        fb.display();
        if (touchTurn != 0) {
            rockModel.rotateY(touchTurn);
            touchTurn = 0;
        }
        if (touchTurnUp != 0) {
            rockModel.rotateX(touchTurnUp);
            touchTurnUp = 0;
        }
        if (System.nanoTime() - time >= 1000000000) {
            fps = 0;
            time = System.nanoTime();
        }
        fps++;
    }

    public static int loadShader(int type, String shaderCode)
    {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void setTouchTurn(float count)
    {
        touchTurn = count;
    }

    public void setTouchTurnUp(float count)
    {
        touchTurnUp = count;
    }

}
