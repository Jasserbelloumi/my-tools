package com.example.vcam;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.InputConfiguration;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;

/* loaded from: classes.dex */
public class HookMain implements IXposedHookLoadPackage {
    public static Surface c1_fake_surface = null;
    public static SurfaceTexture c1_fake_texture = null;
    public static CaptureRequest.Builder c2_builder = null;
    public static VideoToFrames c2_hw_decode_obj = null;
    public static VideoToFrames c2_hw_decode_obj_1 = null;
    public static MediaPlayer c2_player = null;
    public static MediaPlayer c2_player_1 = null;
    public static Surface c2_preview_Surfcae = null;
    public static Surface c2_preview_Surfcae_1 = null;
    public static Surface c2_reader_Surfcae = null;
    public static Surface c2_reader_Surfcae_1 = null;
    public static Class c2_state_callback = null;
    public static CameraDevice.StateCallback c2_state_cb = null;
    public static Surface c2_virtual_surface = null;
    public static SurfaceTexture c2_virtual_surfaceTexture = null;
    public static Class camera_callback_calss = null;
    public static Camera camera_onPreviewFrame = null;
    public static volatile byte[] data_buffer = {0};
    public static SurfaceTexture fake_SurfaceTexture = null;
    public static SessionConfiguration fake_sessionConfiguration = null;
    public static VideoToFrames hw_decode_obj = null;
    public static byte[] input = null;
    public static boolean is_first_hook_build = true;
    public static boolean is_hooked = false;
    public static boolean is_someone_playing = false;
    public static MediaPlayer mMediaPlayer = null;
    public static Surface mSurface = null;
    public static SurfaceTexture mSurfacetexture = null;
    public static Camera mcamera1 = null;
    public static int mhight = 0;
    public static MediaPlayer mplayer1 = null;
    public static int mwidth = 0;
    public static int onemhight = 0;
    public static int onemwidth = 0;
    public static SurfaceHolder ori_holder = null;
    public static Camera origin_preview_camera = null;
    public static OutputConfiguration outputConfiguration = null;
    public static SessionConfiguration sessionConfiguration = null;
    public static Camera start_preview_camera = null;
    public static String video_path = "/storage/emulated/0/DCIM/Camera1/";
    public boolean need_recreate;
    public Context toast_content;
    public int imageReaderFormat = 0;
    public boolean need_to_show_toast = true;
    public int c2_ori_width = 1280;
    public int c2_ori_height = 720;

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
        XposedHelpers.findAndHookMethod("android.hardware.Camera", loadPackageParam.classLoader, "setPreviewTexture", new Object[]{SurfaceTexture.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.1
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                if (new File(HookMain.video_path + "virtual.mp4").exists()) {
                    if (new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/disable.jpg").exists()) {
                        return;
                    }
                    if (HookMain.is_hooked) {
                        HookMain.is_hooked = false;
                        return;
                    }
                    if (methodHookParam.args[0] == null || methodHookParam.args[0].equals(HookMain.c1_fake_texture)) {
                        return;
                    }
                    if (HookMain.origin_preview_camera != null && HookMain.origin_preview_camera.equals(methodHookParam.thisObject)) {
                        methodHookParam.args[0] = HookMain.fake_SurfaceTexture;
                        XposedBridge.log("【VCAM】发现重复" + HookMain.origin_preview_camera.toString());
                        return;
                    }
                    XposedBridge.log("【VCAM】创建预览");
                    HookMain.origin_preview_camera = (Camera) methodHookParam.thisObject;
                    HookMain.mSurfacetexture = (SurfaceTexture) methodHookParam.args[0];
                    if (HookMain.fake_SurfaceTexture == null) {
                        HookMain.fake_SurfaceTexture = new SurfaceTexture(10);
                    } else {
                        HookMain.fake_SurfaceTexture.release();
                        HookMain.fake_SurfaceTexture = new SurfaceTexture(10);
                    }
                    methodHookParam.args[0] = HookMain.fake_SurfaceTexture;
                    return;
                }
                HookMain.this.need_to_show_toast = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no_toast.jpg").exists() ^ true;
                if (HookMain.this.toast_content == null || !HookMain.this.need_to_show_toast) {
                    return;
                }
                try {
                    Toast.makeText(HookMain.this.toast_content, "不存在替换视频\n" + loadPackageParam.packageName + "当前路径：" + HookMain.video_path, 0).show();
                } catch (Exception e) {
                    XposedBridge.log("【VCAM】[toast]" + e.toString());
                }
            }
        }});
        XposedHelpers.findAndHookMethod("android.hardware.camera2.CameraManager", loadPackageParam.classLoader, "openCamera", new Object[]{String.class, CameraDevice.StateCallback.class, Handler.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.2
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                if (methodHookParam.args[1] == null || methodHookParam.args[1].equals(HookMain.c2_state_cb)) {
                    return;
                }
                HookMain.c2_state_cb = (CameraDevice.StateCallback) methodHookParam.args[1];
                HookMain.c2_state_callback = methodHookParam.args[1].getClass();
                if (new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/disable.jpg").exists()) {
                    return;
                }
                File file = new File(HookMain.video_path + "virtual.mp4");
                HookMain.this.need_to_show_toast = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no_toast.jpg").exists() ^ true;
                if (!file.exists()) {
                    if (HookMain.this.toast_content == null || !HookMain.this.need_to_show_toast) {
                        return;
                    }
                    try {
                        Toast.makeText(HookMain.this.toast_content, "不存在替换视频\n" + loadPackageParam.packageName + "当前路径：" + HookMain.video_path, 0).show();
                        return;
                    } catch (Exception e) {
                        XposedBridge.log("【VCAM】[toast]" + e.toString());
                        return;
                    }
                }
                XposedBridge.log("【VCAM】1位参数初始化相机，类：" + HookMain.c2_state_callback.toString());
                HookMain.is_first_hook_build = true;
                HookMain.this.process_camera2_init(HookMain.c2_state_callback);
            }
        }});
        if (Build.VERSION.SDK_INT >= 28) {
            XposedHelpers.findAndHookMethod("android.hardware.camera2.CameraManager", loadPackageParam.classLoader, "openCamera", new Object[]{String.class, Executor.class, CameraDevice.StateCallback.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.3
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                    if (methodHookParam.args[2] == null || methodHookParam.args[2].equals(HookMain.c2_state_cb)) {
                        return;
                    }
                    HookMain.c2_state_cb = (CameraDevice.StateCallback) methodHookParam.args[2];
                    if (new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/disable.jpg").exists()) {
                        return;
                    }
                    File file = new File(HookMain.video_path + "virtual.mp4");
                    HookMain.this.need_to_show_toast = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no_toast.jpg").exists() ^ true;
                    if (!file.exists()) {
                        if (HookMain.this.toast_content == null || !HookMain.this.need_to_show_toast) {
                            return;
                        }
                        try {
                            Toast.makeText(HookMain.this.toast_content, "不存在替换视频\n" + loadPackageParam.packageName + "当前路径：" + HookMain.video_path, 0).show();
                            return;
                        } catch (Exception e) {
                            XposedBridge.log("【VCAM】[toast]" + e.toString());
                            return;
                        }
                    }
                    HookMain.c2_state_callback = methodHookParam.args[2].getClass();
                    XposedBridge.log("【VCAM】2位参数初始化相机，类：" + HookMain.c2_state_callback.toString());
                    HookMain.is_first_hook_build = true;
                    HookMain.this.process_camera2_init(HookMain.c2_state_callback);
                }
            }});
        }
        XposedHelpers.findAndHookMethod("android.hardware.Camera", loadPackageParam.classLoader, "setPreviewCallbackWithBuffer", new Object[]{Camera.PreviewCallback.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.4
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                if (methodHookParam.args[0] != null) {
                    HookMain.this.process_callback(methodHookParam);
                }
            }
        }});
        XposedHelpers.findAndHookMethod("android.hardware.Camera", loadPackageParam.classLoader, "addCallbackBuffer", new Object[]{byte[].class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.5
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                if (methodHookParam.args[0] != null) {
                    methodHookParam.args[0] = new byte[((byte[]) methodHookParam.args[0]).length];
                }
            }
        }});
        XposedHelpers.findAndHookMethod("android.hardware.Camera", loadPackageParam.classLoader, "setPreviewCallback", new Object[]{Camera.PreviewCallback.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.6
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                if (methodHookParam.args[0] != null) {
                    HookMain.this.process_callback(methodHookParam);
                }
            }
        }});
        XposedHelpers.findAndHookMethod("android.hardware.Camera", loadPackageParam.classLoader, "setOneShotPreviewCallback", new Object[]{Camera.PreviewCallback.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.7
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                if (methodHookParam.args[0] != null) {
                    HookMain.this.process_callback(methodHookParam);
                }
            }
        }});
        XposedHelpers.findAndHookMethod("android.hardware.Camera", loadPackageParam.classLoader, "takePicture", new Object[]{Camera.ShutterCallback.class, Camera.PictureCallback.class, Camera.PictureCallback.class, Camera.PictureCallback.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.8
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                XposedBridge.log("【VCAM】4参数拍照");
                if (methodHookParam.args[1] != null) {
                    HookMain.this.process_a_shot_YUV(methodHookParam);
                }
                if (methodHookParam.args[3] != null) {
                    HookMain.this.process_a_shot_jpeg(methodHookParam, 3);
                }
            }
        }});
        XposedHelpers.findAndHookMethod("android.media.MediaRecorder", loadPackageParam.classLoader, "setCamera", new Object[]{Camera.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.9
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                super.beforeHookedMethod(methodHookParam);
                HookMain.this.need_to_show_toast = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no_toast.jpg").exists() ^ true;
                XposedBridge.log("【VCAM】[record]" + loadPackageParam.packageName);
                if (HookMain.this.toast_content == null || !HookMain.this.need_to_show_toast) {
                    return;
                }
                try {
                    Toast.makeText(HookMain.this.toast_content, "应用：" + loadPackageParam.appInfo.name + "(" + loadPackageParam.packageName + ")触发了录像，但目前无法拦截", 0).show();
                } catch (Exception e) {
                    XposedBridge.log("【VCAM】[toast]" + Arrays.toString(e.getStackTrace()));
                }
            }
        }});
        XposedHelpers.findAndHookMethod("android.app.Instrumentation", loadPackageParam.classLoader, "callApplicationOnCreate", new Object[]{Application.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.10
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                int i;
                super.afterHookedMethod(methodHookParam);
                if (methodHookParam.args[0] instanceof Application) {
                    try {
                        HookMain.this.toast_content = ((Application) methodHookParam.args[0]).getApplicationContext();
                    } catch (Exception e) {
                        XposedBridge.log("【VCAM】" + e.toString());
                    }
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera1/private_dir.jpg");
                    if (HookMain.this.toast_content != null) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            try {
                                i = HookMain.this.toast_content.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") + 1 + 0;
                            } catch (Exception e2) {
                                XposedBridge.log("【VCAM】[permission-check]" + e2.toString());
                                i = 0;
                            }
                            try {
                                if (Build.VERSION.SDK_INT >= 30) {
                                    i += HookMain.this.toast_content.checkSelfPermission("android.permission.MANAGE_EXTERNAL_STORAGE") + 1;
                                }
                            } catch (Exception e3) {
                                XposedBridge.log("【VCAM】[permission-check]" + e3.toString());
                            }
                        } else {
                            i = HookMain.this.toast_content.checkCallingPermission("android.permission.READ_EXTERNAL_STORAGE") == 0 ? 2 : 0;
                        }
                        if (i < 1 || file.exists()) {
                            File file2 = new File(HookMain.this.toast_content.getExternalFilesDir(null).getAbsolutePath() + "/Camera1/");
                            if (!file2.isDirectory() && file2.exists()) {
                                file2.delete();
                            }
                            if (!file2.exists()) {
                                file2.mkdir();
                            }
                            File file3 = new File(HookMain.this.toast_content.getExternalFilesDir(null).getAbsolutePath() + "/Camera1/has_shown");
                            File file4 = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/force_show.jpg");
                            if (!loadPackageParam.packageName.equals(BuildConfig.APPLICATION_ID) && (!file3.exists() || file4.exists())) {
                                try {
                                    Toast.makeText(HookMain.this.toast_content, loadPackageParam.packageName + "未授予读取本地目录权限，请检查权限\nCamera1目前重定向为 " + HookMain.this.toast_content.getExternalFilesDir(null).getAbsolutePath() + "/Camera1/", 0).show();
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(HookMain.this.toast_content.getExternalFilesDir(null).getAbsolutePath());
                                    sb.append("/Camera1/has_shown");
                                    FileOutputStream fileOutputStream = new FileOutputStream(sb.toString());
                                    fileOutputStream.write("shown".getBytes());
                                    fileOutputStream.flush();
                                    fileOutputStream.close();
                                } catch (Exception e4) {
                                    XposedBridge.log("【VCAM】[switch-dir]" + e4.toString());
                                }
                            }
                            HookMain.video_path = HookMain.this.toast_content.getExternalFilesDir(null).getAbsolutePath() + "/Camera1/";
                            return;
                        }
                        HookMain.video_path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/";
                        return;
                    }
                    HookMain.video_path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/";
                    if (new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/").canWrite()) {
                        File file5 = new File(HookMain.video_path);
                        if (file5.exists()) {
                            return;
                        }
                        file5.mkdir();
                    }
                }
            }
        }});
        XposedHelpers.findAndHookMethod("android.hardware.Camera", loadPackageParam.classLoader, "startPreview", new Object[]{new XC_MethodHook() { // from class: com.example.vcam.HookMain.11
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                File file = new File(HookMain.video_path + "virtual.mp4");
                HookMain.this.need_to_show_toast = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no_toast.jpg").exists() ^ true;
                if (!file.exists()) {
                    if (HookMain.this.toast_content == null || !HookMain.this.need_to_show_toast) {
                        return;
                    }
                    try {
                        Toast.makeText(HookMain.this.toast_content, "不存在替换视频\n" + loadPackageParam.packageName + "当前路径：" + HookMain.video_path, 0).show();
                        return;
                    } catch (Exception e) {
                        XposedBridge.log("【VCAM】[toast]" + e.toString());
                        return;
                    }
                }
                if (new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/disable.jpg").exists()) {
                    return;
                }
                HookMain.is_someone_playing = false;
                XposedBridge.log("【VCAM】开始预览");
                HookMain.start_preview_camera = (Camera) methodHookParam.thisObject;
                if (HookMain.ori_holder != null) {
                    if (HookMain.mplayer1 == null) {
                        HookMain.mplayer1 = new MediaPlayer();
                    } else {
                        HookMain.mplayer1.release();
                        HookMain.mplayer1 = null;
                        HookMain.mplayer1 = new MediaPlayer();
                    }
                    if (!HookMain.ori_holder.getSurface().isValid() || HookMain.ori_holder == null) {
                        return;
                    }
                    HookMain.mplayer1.setSurface(HookMain.ori_holder.getSurface());
                    if (!new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no-silent.jpg").exists() || HookMain.is_someone_playing) {
                        HookMain.mplayer1.setVolume(0.0f, 0.0f);
                        HookMain.is_someone_playing = false;
                    } else {
                        HookMain.is_someone_playing = true;
                    }
                    HookMain.mplayer1.setLooping(true);
                    HookMain.mplayer1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.example.vcam.HookMain.11.1
                        @Override // android.media.MediaPlayer.OnPreparedListener
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            HookMain.mplayer1.start();
                        }
                    });
                    try {
                        HookMain.mplayer1.setDataSource(HookMain.video_path + "virtual.mp4");
                        HookMain.mplayer1.prepare();
                    } catch (IOException e2) {
                        XposedBridge.log("【VCAM】" + e2.toString());
                    }
                }
                if (HookMain.mSurfacetexture != null) {
                    if (HookMain.mSurface == null) {
                        HookMain.mSurface = new Surface(HookMain.mSurfacetexture);
                    } else {
                        HookMain.mSurface.release();
                        HookMain.mSurface = new Surface(HookMain.mSurfacetexture);
                    }
                    if (HookMain.mMediaPlayer == null) {
                        HookMain.mMediaPlayer = new MediaPlayer();
                    } else {
                        HookMain.mMediaPlayer.release();
                        HookMain.mMediaPlayer = new MediaPlayer();
                    }
                    HookMain.mMediaPlayer.setSurface(HookMain.mSurface);
                    if (!new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no-silent.jpg").exists() || HookMain.is_someone_playing) {
                        HookMain.mMediaPlayer.setVolume(0.0f, 0.0f);
                        HookMain.is_someone_playing = false;
                    } else {
                        HookMain.is_someone_playing = true;
                    }
                    HookMain.mMediaPlayer.setLooping(true);
                    HookMain.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.example.vcam.HookMain.11.2
                        @Override // android.media.MediaPlayer.OnPreparedListener
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            HookMain.mMediaPlayer.start();
                        }
                    });
                    try {
                        HookMain.mMediaPlayer.setDataSource(HookMain.video_path + "virtual.mp4");
                        HookMain.mMediaPlayer.prepare();
                    } catch (IOException e3) {
                        XposedBridge.log("【VCAM】" + e3.toString());
                    }
                }
            }
        }});
        XposedHelpers.findAndHookMethod("android.hardware.Camera", loadPackageParam.classLoader, "setPreviewDisplay", new Object[]{SurfaceHolder.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.12
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                XposedBridge.log("【VCAM】添加Surfaceview预览");
                File file = new File(HookMain.video_path + "virtual.mp4");
                HookMain.this.need_to_show_toast = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no_toast.jpg").exists() ^ true;
                if (!file.exists()) {
                    if (HookMain.this.toast_content == null || !HookMain.this.need_to_show_toast) {
                        return;
                    }
                    try {
                        Toast.makeText(HookMain.this.toast_content, "不存在替换视频\n" + loadPackageParam.packageName + "当前路径：" + HookMain.video_path, 0).show();
                        return;
                    } catch (Exception e) {
                        XposedBridge.log("【VCAM】[toast]" + e.toString());
                        return;
                    }
                }
                if (new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/disable.jpg").exists()) {
                    return;
                }
                HookMain.mcamera1 = (Camera) methodHookParam.thisObject;
                HookMain.ori_holder = (SurfaceHolder) methodHookParam.args[0];
                if (HookMain.c1_fake_texture == null) {
                    HookMain.c1_fake_texture = new SurfaceTexture(11);
                } else {
                    HookMain.c1_fake_texture.release();
                    HookMain.c1_fake_texture = null;
                    HookMain.c1_fake_texture = new SurfaceTexture(11);
                }
                if (HookMain.c1_fake_surface == null) {
                    HookMain.c1_fake_surface = new Surface(HookMain.c1_fake_texture);
                } else {
                    HookMain.c1_fake_surface.release();
                    HookMain.c1_fake_surface = null;
                    HookMain.c1_fake_surface = new Surface(HookMain.c1_fake_texture);
                }
                HookMain.is_hooked = true;
                HookMain.mcamera1.setPreviewTexture(HookMain.c1_fake_texture);
                methodHookParam.setResult((Object) null);
            }
        }});
        XposedHelpers.findAndHookMethod("android.hardware.camera2.CaptureRequest.Builder", loadPackageParam.classLoader, "addTarget", new Object[]{Surface.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.13
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                if (methodHookParam.args[0] == null || methodHookParam.thisObject == null) {
                    return;
                }
                File file = new File(HookMain.video_path + "virtual.mp4");
                HookMain.this.need_to_show_toast = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no_toast.jpg").exists() ^ true;
                if (!file.exists()) {
                    if (HookMain.this.toast_content == null || !HookMain.this.need_to_show_toast) {
                        return;
                    }
                    try {
                        Toast.makeText(HookMain.this.toast_content, "不存在替换视频\n" + loadPackageParam.packageName + "当前路径：" + HookMain.video_path, 0).show();
                        return;
                    } catch (Exception e) {
                        XposedBridge.log("【VCAM】[toast]" + e.toString());
                        return;
                    }
                }
                if (methodHookParam.args[0].equals(HookMain.c2_virtual_surface)) {
                    return;
                }
                if (new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/disable.jpg").exists()) {
                    return;
                }
                if (methodHookParam.args[0].toString().contains("Surface(name=null)")) {
                    if (HookMain.c2_reader_Surfcae == null) {
                        HookMain.c2_reader_Surfcae = (Surface) methodHookParam.args[0];
                    } else if (!HookMain.c2_reader_Surfcae.equals(methodHookParam.args[0]) && HookMain.c2_reader_Surfcae_1 == null) {
                        HookMain.c2_reader_Surfcae_1 = (Surface) methodHookParam.args[0];
                    }
                } else if (HookMain.c2_preview_Surfcae == null) {
                    HookMain.c2_preview_Surfcae = (Surface) methodHookParam.args[0];
                } else if (!HookMain.c2_preview_Surfcae.equals(methodHookParam.args[0]) && HookMain.c2_preview_Surfcae_1 == null) {
                    HookMain.c2_preview_Surfcae_1 = (Surface) methodHookParam.args[0];
                }
                XposedBridge.log("【VCAM】添加目标：" + methodHookParam.args[0].toString());
                methodHookParam.args[0] = HookMain.c2_virtual_surface;
            }
        }});
        XposedHelpers.findAndHookMethod("android.hardware.camera2.CaptureRequest.Builder", loadPackageParam.classLoader, "removeTarget", new Object[]{Surface.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.14
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                if (methodHookParam.args[0] == null || methodHookParam.thisObject == null) {
                    return;
                }
                File file = new File(HookMain.video_path + "virtual.mp4");
                HookMain.this.need_to_show_toast = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no_toast.jpg").exists() ^ true;
                if (!file.exists()) {
                    if (HookMain.this.toast_content == null || !HookMain.this.need_to_show_toast) {
                        return;
                    }
                    try {
                        Toast.makeText(HookMain.this.toast_content, "不存在替换视频\n" + loadPackageParam.packageName + "当前路径：" + HookMain.video_path, 0).show();
                        return;
                    } catch (Exception e) {
                        XposedBridge.log("【VCAM】[toast]" + e.toString());
                        return;
                    }
                }
                if (new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/disable.jpg").exists()) {
                    return;
                }
                Surface surface = (Surface) methodHookParam.args[0];
                if (surface.equals(HookMain.c2_preview_Surfcae)) {
                    HookMain.c2_preview_Surfcae = null;
                }
                if (surface.equals(HookMain.c2_preview_Surfcae_1)) {
                    HookMain.c2_preview_Surfcae_1 = null;
                }
                if (surface.equals(HookMain.c2_reader_Surfcae_1)) {
                    HookMain.c2_reader_Surfcae_1 = null;
                }
                if (surface.equals(HookMain.c2_reader_Surfcae)) {
                    HookMain.c2_reader_Surfcae = null;
                }
                XposedBridge.log("【VCAM】移除目标：" + methodHookParam.args[0].toString());
            }
        }});
        XposedHelpers.findAndHookMethod("android.hardware.camera2.CaptureRequest.Builder", loadPackageParam.classLoader, "build", new Object[]{new XC_MethodHook() { // from class: com.example.vcam.HookMain.15
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                if (methodHookParam.thisObject == null || methodHookParam.thisObject.equals(HookMain.c2_builder)) {
                    return;
                }
                HookMain.c2_builder = (CaptureRequest.Builder) methodHookParam.thisObject;
                File file = new File(HookMain.video_path + "virtual.mp4");
                HookMain.this.need_to_show_toast = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no_toast.jpg").exists() ^ true;
                if (!file.exists() && HookMain.this.need_to_show_toast) {
                    if (HookMain.this.toast_content != null) {
                        try {
                            Toast.makeText(HookMain.this.toast_content, "不存在替换视频\n" + loadPackageParam.packageName + "当前路径：" + HookMain.video_path, 0).show();
                            return;
                        } catch (Exception e) {
                            XposedBridge.log("【VCAM】[toast]" + e.toString());
                            return;
                        }
                    }
                    return;
                }
                if (new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/disable.jpg").exists()) {
                    return;
                }
                XposedBridge.log("【VCAM】开始build请求");
                HookMain.this.process_camera2_play();
            }
        }});
        XposedHelpers.findAndHookMethod("android.media.ImageReader", loadPackageParam.classLoader, "newInstance", new Object[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, new XC_MethodHook() { // from class: com.example.vcam.HookMain.16
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                XposedBridge.log("【VCAM】应用创建了渲染器：宽：" + methodHookParam.args[0] + " 高：" + methodHookParam.args[1] + "格式" + methodHookParam.args[2]);
                HookMain.this.c2_ori_width = ((Integer) methodHookParam.args[0]).intValue();
                HookMain.this.c2_ori_height = ((Integer) methodHookParam.args[1]).intValue();
                HookMain.this.imageReaderFormat = ((Integer) methodHookParam.args[2]).intValue();
                StringBuilder sb = new StringBuilder();
                sb.append(Environment.getExternalStorageDirectory().getPath());
                sb.append("/DCIM/Camera1/no_toast.jpg");
                File file = new File(sb.toString());
                HookMain.this.need_to_show_toast = !file.exists();
                if (HookMain.this.toast_content == null || !HookMain.this.need_to_show_toast) {
                    return;
                }
                try {
                    Toast.makeText(HookMain.this.toast_content, "应用创建了渲染器：\n宽：" + methodHookParam.args[0] + "\n高：" + methodHookParam.args[1] + "\n一般只需要宽高比与视频相同", 0).show();
                } catch (Exception e) {
                    XposedBridge.log("【VCAM】[toast]" + e.toString());
                }
            }
        }});
        XposedHelpers.findAndHookMethod("android.hardware.camera2.CameraCaptureSession.CaptureCallback", loadPackageParam.classLoader, "onCaptureFailed", new Object[]{CameraCaptureSession.class, CaptureRequest.class, CaptureFailure.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.17
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                XposedBridge.log("【VCAM】onCaptureFailed原因：" + ((CaptureFailure) methodHookParam.args[2]).getReason());
            }
        }});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void process_camera2_play() {
        if (c2_reader_Surfcae != null) {
            VideoToFrames videoToFrames = c2_hw_decode_obj;
            if (videoToFrames != null) {
                videoToFrames.stopDecode();
                c2_hw_decode_obj = null;
            }
            VideoToFrames videoToFrames2 = new VideoToFrames();
            c2_hw_decode_obj = videoToFrames2;
            try {
                if (this.imageReaderFormat == 256) {
                    videoToFrames2.setSaveFrames("null", OutputImageFormat.JPEG);
                } else {
                    videoToFrames2.setSaveFrames("null", OutputImageFormat.NV21);
                }
                c2_hw_decode_obj.set_surfcae(c2_reader_Surfcae);
                c2_hw_decode_obj.decode(video_path + "virtual.mp4");
            } catch (Throwable th) {
                XposedBridge.log("【VCAM】" + th);
            }
        }
        if (c2_reader_Surfcae_1 != null) {
            VideoToFrames videoToFrames3 = c2_hw_decode_obj_1;
            if (videoToFrames3 != null) {
                videoToFrames3.stopDecode();
                c2_hw_decode_obj_1 = null;
            }
            VideoToFrames videoToFrames4 = new VideoToFrames();
            c2_hw_decode_obj_1 = videoToFrames4;
            try {
                if (this.imageReaderFormat == 256) {
                    videoToFrames4.setSaveFrames("null", OutputImageFormat.JPEG);
                } else {
                    videoToFrames4.setSaveFrames("null", OutputImageFormat.NV21);
                }
                c2_hw_decode_obj_1.set_surfcae(c2_reader_Surfcae_1);
                c2_hw_decode_obj_1.decode(video_path + "virtual.mp4");
            } catch (Throwable th2) {
                XposedBridge.log("【VCAM】" + th2);
            }
        }
        if (c2_preview_Surfcae != null) {
            MediaPlayer mediaPlayer = c2_player;
            if (mediaPlayer == null) {
                c2_player = new MediaPlayer();
            } else {
                mediaPlayer.release();
                c2_player = new MediaPlayer();
            }
            c2_player.setSurface(c2_preview_Surfcae);
            if (!new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no-silent.jpg").exists()) {
                c2_player.setVolume(0.0f, 0.0f);
            }
            c2_player.setLooping(true);
            try {
                c2_player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.example.vcam.HookMain.18
                    @Override // android.media.MediaPlayer.OnPreparedListener
                    public void onPrepared(MediaPlayer mediaPlayer2) {
                        HookMain.c2_player.start();
                    }
                });
                c2_player.setDataSource(video_path + "virtual.mp4");
                c2_player.prepare();
            } catch (Exception e) {
                XposedBridge.log("【VCAM】[c2player][" + c2_preview_Surfcae.toString() + "]" + e);
            }
        }
        if (c2_preview_Surfcae_1 != null) {
            MediaPlayer mediaPlayer2 = c2_player_1;
            if (mediaPlayer2 == null) {
                c2_player_1 = new MediaPlayer();
            } else {
                mediaPlayer2.release();
                c2_player_1 = new MediaPlayer();
            }
            c2_player_1.setSurface(c2_preview_Surfcae_1);
            if (!new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no-silent.jpg").exists()) {
                c2_player_1.setVolume(0.0f, 0.0f);
            }
            c2_player_1.setLooping(true);
            try {
                c2_player_1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.example.vcam.HookMain.19
                    @Override // android.media.MediaPlayer.OnPreparedListener
                    public void onPrepared(MediaPlayer mediaPlayer3) {
                        HookMain.c2_player_1.start();
                    }
                });
                c2_player_1.setDataSource(video_path + "virtual.mp4");
                c2_player_1.prepare();
            } catch (Exception e2) {
                XposedBridge.log("【VCAM】[c2player1][ " + c2_preview_Surfcae_1.toString() + "]" + e2);
            }
        }
        XposedBridge.log("【VCAM】Camera2处理过程完全执行");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Surface create_virtual_surface() {
        if (this.need_recreate) {
            SurfaceTexture surfaceTexture = c2_virtual_surfaceTexture;
            if (surfaceTexture != null) {
                surfaceTexture.release();
                c2_virtual_surfaceTexture = null;
            }
            Surface surface = c2_virtual_surface;
            if (surface != null) {
                surface.release();
                c2_virtual_surface = null;
            }
            c2_virtual_surfaceTexture = new SurfaceTexture(15);
            c2_virtual_surface = new Surface(c2_virtual_surfaceTexture);
            this.need_recreate = false;
        } else if (c2_virtual_surface == null) {
            this.need_recreate = true;
            c2_virtual_surface = create_virtual_surface();
        }
        XposedBridge.log("【VCAM】【重建垃圾场】" + c2_virtual_surface.toString());
        return c2_virtual_surface;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void process_camera2_init(Class cls) {
        XposedHelpers.findAndHookMethod(cls, "onOpened", new Object[]{CameraDevice.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.20
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                HookMain.this.need_recreate = true;
                HookMain.this.create_virtual_surface();
                if (HookMain.c2_player != null) {
                    HookMain.c2_player.stop();
                    HookMain.c2_player.reset();
                    HookMain.c2_player.release();
                    HookMain.c2_player = null;
                }
                if (HookMain.c2_hw_decode_obj_1 != null) {
                    HookMain.c2_hw_decode_obj_1.stopDecode();
                    HookMain.c2_hw_decode_obj_1 = null;
                }
                if (HookMain.c2_hw_decode_obj != null) {
                    HookMain.c2_hw_decode_obj.stopDecode();
                    HookMain.c2_hw_decode_obj = null;
                }
                if (HookMain.c2_player_1 != null) {
                    HookMain.c2_player_1.stop();
                    HookMain.c2_player_1.reset();
                    HookMain.c2_player_1.release();
                    HookMain.c2_player_1 = null;
                }
                HookMain.c2_preview_Surfcae_1 = null;
                HookMain.c2_reader_Surfcae_1 = null;
                HookMain.c2_reader_Surfcae = null;
                HookMain.c2_preview_Surfcae = null;
                HookMain.is_first_hook_build = true;
                XposedBridge.log("【VCAM】打开相机C2");
                File file = new File(HookMain.video_path + "virtual.mp4");
                HookMain.this.need_to_show_toast = !new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no_toast.jpg").exists();
                if (!file.exists()) {
                    if (HookMain.this.toast_content == null || !HookMain.this.need_to_show_toast) {
                        return;
                    }
                    try {
                        Toast.makeText(HookMain.this.toast_content, "不存在替换视频\n" + HookMain.this.toast_content.getPackageName() + "当前路径：" + HookMain.video_path, 0).show();
                        return;
                    } catch (Exception e) {
                        XposedBridge.log("【VCAM】[toast]" + e.toString());
                        return;
                    }
                }
                XposedHelpers.findAndHookMethod(methodHookParam.args[0].getClass(), "createCaptureSession", new Object[]{List.class, CameraCaptureSession.StateCallback.class, Handler.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.20.1
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam2) throws Throwable {
                        if (methodHookParam2.args[0] != null) {
                            XposedBridge.log("【VCAM】createCaptureSession创捷捕获，原始:" + methodHookParam2.args[0].toString() + "虚拟：" + HookMain.c2_virtual_surface.toString());
                            methodHookParam2.args[0] = Arrays.asList(HookMain.c2_virtual_surface);
                            if (methodHookParam2.args[1] != null) {
                                HookMain.this.process_camera2Session_callback((CameraCaptureSession.StateCallback) methodHookParam2.args[1]);
                            }
                        }
                    }
                }});
                if (Build.VERSION.SDK_INT >= 24) {
                    XposedHelpers.findAndHookMethod(methodHookParam.args[0].getClass(), "createCaptureSessionByOutputConfigurations", new Object[]{List.class, CameraCaptureSession.StateCallback.class, Handler.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.20.2
                        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam2) throws Throwable {
                            super.beforeHookedMethod(methodHookParam2);
                            if (methodHookParam2.args[0] != null) {
                                HookMain.outputConfiguration = new OutputConfiguration(HookMain.c2_virtual_surface);
                                methodHookParam2.args[0] = Arrays.asList(HookMain.outputConfiguration);
                                XposedBridge.log("【VCAM】执行了createCaptureSessionByOutputConfigurations-144777");
                                if (methodHookParam2.args[1] != null) {
                                    HookMain.this.process_camera2Session_callback((CameraCaptureSession.StateCallback) methodHookParam2.args[1]);
                                }
                            }
                        }
                    }});
                }
                XposedHelpers.findAndHookMethod(methodHookParam.args[0].getClass(), "createConstrainedHighSpeedCaptureSession", new Object[]{List.class, CameraCaptureSession.StateCallback.class, Handler.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.20.3
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam2) throws Throwable {
                        super.beforeHookedMethod(methodHookParam2);
                        if (methodHookParam2.args[0] != null) {
                            methodHookParam2.args[0] = Arrays.asList(HookMain.c2_virtual_surface);
                            XposedBridge.log("【VCAM】执行了 createConstrainedHighSpeedCaptureSession -5484987");
                            if (methodHookParam2.args[1] != null) {
                                HookMain.this.process_camera2Session_callback((CameraCaptureSession.StateCallback) methodHookParam2.args[1]);
                            }
                        }
                    }
                }});
                if (Build.VERSION.SDK_INT >= 23) {
                    XposedHelpers.findAndHookMethod(methodHookParam.args[0].getClass(), "createReprocessableCaptureSession", new Object[]{InputConfiguration.class, List.class, CameraCaptureSession.StateCallback.class, Handler.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.20.4
                        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam2) throws Throwable {
                            super.beforeHookedMethod(methodHookParam2);
                            if (methodHookParam2.args[1] != null) {
                                methodHookParam2.args[1] = Arrays.asList(HookMain.c2_virtual_surface);
                                XposedBridge.log("【VCAM】执行了 createReprocessableCaptureSession ");
                                if (methodHookParam2.args[2] != null) {
                                    HookMain.this.process_camera2Session_callback((CameraCaptureSession.StateCallback) methodHookParam2.args[2]);
                                }
                            }
                        }
                    }});
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    XposedHelpers.findAndHookMethod(methodHookParam.args[0].getClass(), "createReprocessableCaptureSessionByConfigurations", new Object[]{InputConfiguration.class, List.class, CameraCaptureSession.StateCallback.class, Handler.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.20.5
                        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam2) throws Throwable {
                            super.beforeHookedMethod(methodHookParam2);
                            if (methodHookParam2.args[1] != null) {
                                HookMain.outputConfiguration = new OutputConfiguration(HookMain.c2_virtual_surface);
                                methodHookParam2.args[0] = Arrays.asList(HookMain.outputConfiguration);
                                XposedBridge.log("【VCAM】执行了 createReprocessableCaptureSessionByConfigurations");
                                if (methodHookParam2.args[2] != null) {
                                    HookMain.this.process_camera2Session_callback((CameraCaptureSession.StateCallback) methodHookParam2.args[2]);
                                }
                            }
                        }
                    }});
                }
                if (Build.VERSION.SDK_INT >= 28) {
                    XposedHelpers.findAndHookMethod(methodHookParam.args[0].getClass(), "createCaptureSession", new Object[]{SessionConfiguration.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.20.6
                        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam2) throws Throwable {
                            super.beforeHookedMethod(methodHookParam2);
                            if (methodHookParam2.args[0] != null) {
                                XposedBridge.log("【VCAM】执行了 createCaptureSession -5484987");
                                HookMain.sessionConfiguration = (SessionConfiguration) methodHookParam2.args[0];
                                HookMain.outputConfiguration = new OutputConfiguration(HookMain.c2_virtual_surface);
                                HookMain.fake_sessionConfiguration = new SessionConfiguration(HookMain.sessionConfiguration.getSessionType(), Arrays.asList(HookMain.outputConfiguration), HookMain.sessionConfiguration.getExecutor(), HookMain.sessionConfiguration.getStateCallback());
                                methodHookParam2.args[0] = HookMain.fake_sessionConfiguration;
                                HookMain.this.process_camera2Session_callback(HookMain.sessionConfiguration.getStateCallback());
                            }
                        }
                    }});
                }
            }
        }});
        XposedHelpers.findAndHookMethod(cls, "onError", new Object[]{CameraDevice.class, Integer.TYPE, new XC_MethodHook() { // from class: com.example.vcam.HookMain.21
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                XposedBridge.log("【VCAM】相机错误onerror：" + ((Integer) methodHookParam.args[1]).intValue());
            }
        }});
        XposedHelpers.findAndHookMethod(cls, "onDisconnected", new Object[]{CameraDevice.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.22
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                XposedBridge.log("【VCAM】相机断开onDisconnected ：");
            }
        }});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public File pickRandomImageFile(String str) {
        File file = new File(str);
        if (!file.exists() || !file.isDirectory()) {
            XposedBridge.log("【VCAM】图像目录不存在：" + str);
            return null;
        }
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            XposedBridge.log("【VCAM】无法列出目录：" + str);
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (File file2 : listFiles) {
            if (file2 != null && file2.isFile() && file2.getName().toLowerCase().endsWith(".bmp")) {
                arrayList.add(file2);
            }
        }
        if (arrayList.isEmpty()) {
            XposedBridge.log("【VCAM】目录中没有可用的BMP文件：" + str);
            return null;
        }
        return (File) arrayList.get(ThreadLocalRandom.current().nextInt(arrayList.size()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void process_a_shot_jpeg(XC_MethodHook.MethodHookParam methodHookParam, int i) {
        try {
            XposedBridge.log("【VCAM】第二个jpeg:" + methodHookParam.args[i].toString());
        } catch (Exception e) {
            XposedBridge.log("【VCAM】" + e);
        }
        XposedHelpers.findAndHookMethod(methodHookParam.args[i].getClass(), "onPictureTaken", new Object[]{byte[].class, Camera.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.23
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam2) throws Throwable {
                try {
                    boolean z = true;
                    Camera camera = (Camera) methodHookParam2.args[1];
                    HookMain.onemwidth = camera.getParameters().getPreviewSize().width;
                    HookMain.onemhight = camera.getParameters().getPreviewSize().height;
                    XposedBridge.log("【VCAM】JPEG拍照回调初始化：宽：" + HookMain.onemwidth + "高：" + HookMain.onemhight + "对应的类：" + camera.toString());
                    StringBuilder sb = new StringBuilder();
                    sb.append(Environment.getExternalStorageDirectory().getPath());
                    sb.append("/DCIM/Camera1/no_toast.jpg");
                    File file = new File(sb.toString());
                    HookMain hookMain = HookMain.this;
                    if (file.exists()) {
                        z = false;
                    }
                    hookMain.need_to_show_toast = z;
                    if (HookMain.this.toast_content != null && HookMain.this.need_to_show_toast) {
                        try {
                            Toast.makeText(HookMain.this.toast_content, "发现拍照\n宽：" + HookMain.onemwidth + "\n高：" + HookMain.onemhight + "\n格式：JPEG", 0).show();
                        } catch (Exception e2) {
                            XposedBridge.log("【VCAM】[toast]" + e2.toString());
                        }
                    }
                    if (new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/disable.jpg").exists()) {
                        return;
                    }
                    File pickRandomImageFile = HookMain.this.pickRandomImageFile(HookMain.video_path);
                    if (pickRandomImageFile != null) {
                        Bitmap bmp = HookMain.this.getBMP(pickRandomImageFile.getAbsolutePath());
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        methodHookParam2.args[0] = byteArrayOutputStream.toByteArray();
                        return;
                    }
                    XposedBridge.log("【VCAM】未找到用于替换的BMP文件");
                } catch (Exception e3) {
                    XposedBridge.log("【VCAM】" + e3.toString());
                }
            }
        }});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void process_a_shot_YUV(XC_MethodHook.MethodHookParam methodHookParam) {
        try {
            XposedBridge.log("【VCAM】发现拍照YUV:" + methodHookParam.args[1].toString());
        } catch (Exception e) {
            XposedBridge.log("【VCAM】" + e);
        }
        XposedHelpers.findAndHookMethod(methodHookParam.args[1].getClass(), "onPictureTaken", new Object[]{byte[].class, Camera.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.24
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam2) throws Throwable {
                try {
                    boolean z = true;
                    Camera camera = (Camera) methodHookParam2.args[1];
                    HookMain.onemwidth = camera.getParameters().getPreviewSize().width;
                    HookMain.onemhight = camera.getParameters().getPreviewSize().height;
                    XposedBridge.log("【VCAM】YUV拍照回调初始化：宽：" + HookMain.onemwidth + "高：" + HookMain.onemhight + "对应的类：" + camera.toString());
                    StringBuilder sb = new StringBuilder();
                    sb.append(Environment.getExternalStorageDirectory().getPath());
                    sb.append("/DCIM/Camera1/no_toast.jpg");
                    File file = new File(sb.toString());
                    HookMain hookMain = HookMain.this;
                    if (file.exists()) {
                        z = false;
                    }
                    hookMain.need_to_show_toast = z;
                    if (HookMain.this.toast_content != null && HookMain.this.need_to_show_toast) {
                        try {
                            Toast.makeText(HookMain.this.toast_content, "发现拍照\n宽：" + HookMain.onemwidth + "\n高：" + HookMain.onemhight + "\n格式：YUV_420_888", 0).show();
                        } catch (Exception e2) {
                            XposedBridge.log("【VCAM】[toast]" + e2.toString());
                        }
                    }
                    if (new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/disable.jpg").exists()) {
                        return;
                    }
                    File pickRandomImageFile = HookMain.this.pickRandomImageFile(HookMain.video_path);
                    if (pickRandomImageFile != null) {
                        HookMain.input = HookMain.getYUVByBitmap(HookMain.this.getBMP(pickRandomImageFile.getAbsolutePath()));
                        methodHookParam2.args[0] = HookMain.input;
                    } else {
                        XposedBridge.log("【VCAM】未找到用于替换的BMP文件");
                    }
                } catch (Exception e3) {
                    XposedBridge.log("【VCAM】" + e3.toString());
                }
            }
        }});
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v2, types: [int] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v8 */
    public void process_callback(XC_MethodHook.MethodHookParam methodHookParam) {
        final Class<?> cls = methodHookParam.args[0].getClass();
        boolean exists = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/disable.jpg").exists();
        File file = new File(video_path + "virtual.mp4");
        this.need_to_show_toast = !new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/no_toast.jpg").exists();
        final ?? r1 = exists;
        if (!file.exists()) {
            Context context = this.toast_content;
            if (context != null && this.need_to_show_toast) {
                try {
                    Toast.makeText(context, "不存在替换视频\n" + this.toast_content.getPackageName() + "当前路径：" + video_path, 0).show();
                } catch (Exception e) {
                    XposedBridge.log("【VCAM】[toast]" + e);
                }
            }
            r1 = 1;
        }
        XposedHelpers.findAndHookMethod(cls, "onPreviewFrame", new Object[]{byte[].class, Camera.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.25
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam2) throws Throwable {
                if (!((Camera) methodHookParam2.args[1]).equals(HookMain.camera_onPreviewFrame)) {
                    HookMain.camera_callback_calss = cls;
                    HookMain.camera_onPreviewFrame = (Camera) methodHookParam2.args[1];
                    HookMain.mwidth = HookMain.camera_onPreviewFrame.getParameters().getPreviewSize().width;
                    HookMain.mhight = HookMain.camera_onPreviewFrame.getParameters().getPreviewSize().height;
                    XposedBridge.log("【VCAM】帧预览回调初始化：宽：" + HookMain.mwidth + " 高：" + HookMain.mhight + " 帧率：" + HookMain.camera_onPreviewFrame.getParameters().getPreviewFrameRate());
                    StringBuilder sb = new StringBuilder();
                    sb.append(Environment.getExternalStorageDirectory().getPath());
                    sb.append("/DCIM/Camera1/no_toast.jpg");
                    HookMain.this.need_to_show_toast = new File(sb.toString()).exists() ^ true;
                    if (HookMain.this.toast_content != null && HookMain.this.need_to_show_toast) {
                        try {
                            Toast.makeText(HookMain.this.toast_content, "发现预览\n宽：" + HookMain.mwidth + "\n高：" + HookMain.mhight + "\n需要视频分辨率与其完全相同", 0).show();
                        } catch (Exception e2) {
                            XposedBridge.log("【VCAM】[toast]" + e2.toString());
                        }
                    }
                    if (r1 == 1) {
                        return;
                    }
                    if (HookMain.hw_decode_obj != null) {
                        HookMain.hw_decode_obj.stopDecode();
                    }
                    HookMain.hw_decode_obj = new VideoToFrames();
                    HookMain.hw_decode_obj.setSaveFrames("", OutputImageFormat.NV21);
                    HookMain.hw_decode_obj.decode(HookMain.video_path + "virtual.mp4");
                    do {
                    } while (HookMain.data_buffer == null);
                    System.arraycopy(HookMain.data_buffer, 0, methodHookParam2.args[0], 0, Math.min(HookMain.data_buffer.length, ((byte[]) methodHookParam2.args[0]).length));
                    return;
                }
                do {
                } while (HookMain.data_buffer == null);
                System.arraycopy(HookMain.data_buffer, 0, methodHookParam2.args[0], 0, Math.min(HookMain.data_buffer.length, ((byte[]) methodHookParam2.args[0]).length));
            }
        }});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void process_camera2Session_callback(CameraCaptureSession.StateCallback stateCallback) {
        if (stateCallback == null) {
            return;
        }
        XposedHelpers.findAndHookMethod(stateCallback.getClass(), "onConfigureFailed", new Object[]{CameraCaptureSession.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.26
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                XposedBridge.log("【VCAM】onConfigureFailed ：" + methodHookParam.args[0].toString());
            }
        }});
        XposedHelpers.findAndHookMethod(stateCallback.getClass(), "onConfigured", new Object[]{CameraCaptureSession.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.27
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                XposedBridge.log("【VCAM】onConfigured ：" + methodHookParam.args[0].toString());
            }
        }});
        XposedHelpers.findAndHookMethod(stateCallback.getClass(), "onClosed", new Object[]{CameraCaptureSession.class, new XC_MethodHook() { // from class: com.example.vcam.HookMain.28
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                XposedBridge.log("【VCAM】onClosed ：" + methodHookParam.args[0].toString());
            }
        }});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap getBMP(String str) throws Throwable {
        return BitmapFactory.decodeFile(str);
    }

    private static byte[] rgb2YCbCr420(int[] iArr, int i, int i2) {
        int i3 = i * i2;
        byte[] bArr = new byte[(i3 * 3) / 2];
        for (int i4 = 0; i4 < i2; i4++) {
            for (int i5 = 0; i5 < i; i5++) {
                int i6 = (i4 * i) + i5;
                int i7 = iArr[i6] & 16777215;
                int i8 = i7 & 255;
                int i9 = (i7 >> 8) & 255;
                int i10 = (i7 >> 16) & 255;
                int i11 = (((((i8 * 66) + (i9 * 129)) + (i10 * 25)) + 128) >> 8) + 16;
                int i12 = (((((i8 * (-38)) - (i9 * 74)) + (i10 * 112)) + 128) >> 8) + 128;
                int i13 = (((((i8 * 112) - (i9 * 94)) - (i10 * 18)) + 128) >> 8) + 128;
                int min = i11 >= 16 ? Math.min(i11, 255) : 16;
                int min2 = i12 < 0 ? 0 : Math.min(i12, 255);
                int min3 = i13 < 0 ? 0 : Math.min(i13, 255);
                bArr[i6] = (byte) min;
                int i14 = ((i4 >> 1) * i) + i3 + (i5 & (-2));
                bArr[i14] = (byte) min2;
                bArr[i14 + 1] = (byte) min3;
            }
        }
        return bArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] getYUVByBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] iArr = new int[width * height];
        bitmap.getPixels(iArr, 0, width, 0, 0, width, height);
        return rgb2YCbCr420(iArr, width, height);
    }
}
