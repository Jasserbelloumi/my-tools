package com.example.vcam;

import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.view.Surface;
import de.robv.android.xposed.XposedBridge;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

/* loaded from: classes.dex */
public class VideoToFrames implements Runnable {
    private static final int COLOR_FormatI420 = 1;
    private static final int COLOR_FormatNV21 = 2;
    private static final long DEFAULT_TIMEOUT_US = 10000;
    private static final String TAG = "VideoToFrames";
    private static final boolean VERBOSE = false;
    private Callback callback;
    private Thread childThread;
    private LinkedBlockingQueue<byte[]> mQueue;
    private OutputImageFormat outputImageFormat;
    private Surface play_surf;
    private Throwable throwable;
    private String videoFilePath;
    private final int decodeColorFormat = 2135033992;
    private boolean stopDecode = false;

    /* loaded from: classes.dex */
    public interface Callback {
        void onDecodeFrame(int i);

        void onFinishDecode();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setEnqueue(LinkedBlockingQueue<byte[]> linkedBlockingQueue) {
        this.mQueue = linkedBlockingQueue;
    }

    public void setSaveFrames(String str, OutputImageFormat outputImageFormat) throws IOException {
        this.outputImageFormat = outputImageFormat;
    }

    public void set_surfcae(Surface surface) {
        if (surface != null) {
            this.play_surf = surface;
        }
    }

    public void stopDecode() {
        this.stopDecode = true;
    }

    public void decode(String str) throws Throwable {
        this.videoFilePath = str;
        if (this.childThread == null) {
            Thread thread = new Thread(this, "decode");
            this.childThread = thread;
            thread.start();
            Throwable th = this.throwable;
            if (th != null) {
                throw th;
            }
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            videoDecode(this.videoFilePath);
        } catch (Throwable th) {
            this.throwable = th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00c1  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00c9  */
    /* JADX WARN: Type inference failed for: r0v1, types: [android.media.MediaCodec, android.media.MediaExtractor] */
    /* JADX WARN: Type inference failed for: r0v3 */
    /* JADX WARN: Type inference failed for: r0v4, types: [android.media.MediaCodec] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void videoDecode(java.lang.String r6) throws java.io.IOException {
        /*
            r5 = this;
            java.lang.String r0 = "【VCAM】【decoder】开始解码"
            de.robv.android.xposed.XposedBridge.log(r0)
            r0 = 0
            java.io.File r1 = new java.io.File     // Catch: java.lang.Throwable -> L93 java.lang.Exception -> L96
            r1.<init>(r6)     // Catch: java.lang.Throwable -> L93 java.lang.Exception -> L96
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch: java.lang.Throwable -> L93 java.lang.Exception -> L96
            r1.<init>()     // Catch: java.lang.Throwable -> L93 java.lang.Exception -> L96
            r1.setDataSource(r6)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            int r2 = selectTrack(r1)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            if (r2 >= 0) goto L2d
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            r3.<init>()     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            java.lang.String r4 = "【VCAM】【decoder】No video track found in "
            r3.append(r4)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            r3.append(r6)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            java.lang.String r6 = r3.toString()     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            de.robv.android.xposed.XposedBridge.log(r6)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
        L2d:
            r1.selectTrack(r2)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            android.media.MediaFormat r6 = r1.getTrackFormat(r2)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            java.lang.String r2 = "mime"
            java.lang.String r2 = r6.getString(r2)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            android.media.MediaCodec r0 = android.media.MediaCodec.createDecoderByType(r2)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            android.media.MediaCodecInfo r3 = r0.getCodecInfo()     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            android.media.MediaCodecInfo$CodecCapabilities r3 = r3.getCapabilitiesForType(r2)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            r5.showSupportedColorFormat(r3)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            android.media.MediaCodecInfo r3 = r0.getCodecInfo()     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            android.media.MediaCodecInfo$CodecCapabilities r2 = r3.getCapabilitiesForType(r2)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            r3 = 2135033992(0x7f420888, float:2.5791453E38)
            boolean r2 = r5.isColorFormatSupported(r3, r2)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            if (r2 == 0) goto L65
            java.lang.String r2 = "color-format"
            r6.setInteger(r2, r3)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            java.lang.String r2 = "【VCAM】【decoder】set decode color format to type 2135033992"
            de.robv.android.xposed.XposedBridge.log(r2)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            goto L71
        L65:
            java.lang.String r2 = "VideoToFrames"
            java.lang.String r3 = "unable to set decode color format, color format type 2135033992 not supported"
            android.util.Log.i(r2, r3)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            java.lang.String r2 = "【VCAM】【decoder】unable to set decode color format, color format type 2135033992 not supported"
            de.robv.android.xposed.XposedBridge.log(r2)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
        L71:
            r5.decodeFramesToImage(r0, r1, r6)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            r0.stop()     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
        L77:
            boolean r2 = r5.stopDecode     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            if (r2 != 0) goto L88
            r2 = 0
            r4 = 0
            r1.seekTo(r2, r4)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            r5.decodeFramesToImage(r0, r1, r6)     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            r0.stop()     // Catch: java.lang.Exception -> L91 java.lang.Throwable -> Lbe
            goto L77
        L88:
            if (r0 == 0) goto Lba
            r0.stop()
            r0.release()
            goto Lba
        L91:
            r6 = move-exception
            goto L98
        L93:
            r6 = move-exception
            r1 = r0
            goto Lbf
        L96:
            r6 = move-exception
            r1 = r0
        L98:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lbe
            r2.<init>()     // Catch: java.lang.Throwable -> Lbe
            java.lang.String r3 = "【VCAM】[videofile]"
            r2.append(r3)     // Catch: java.lang.Throwable -> Lbe
            java.lang.String r6 = r6.toString()     // Catch: java.lang.Throwable -> Lbe
            r2.append(r6)     // Catch: java.lang.Throwable -> Lbe
            java.lang.String r6 = r2.toString()     // Catch: java.lang.Throwable -> Lbe
            de.robv.android.xposed.XposedBridge.log(r6)     // Catch: java.lang.Throwable -> Lbe
            if (r0 == 0) goto Lb8
            r0.stop()
            r0.release()
        Lb8:
            if (r1 == 0) goto Lbd
        Lba:
            r1.release()
        Lbd:
            return
        Lbe:
            r6 = move-exception
        Lbf:
            if (r0 == 0) goto Lc7
            r0.stop()
            r0.release()
        Lc7:
            if (r1 == 0) goto Lcc
            r1.release()
        Lcc:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.vcam.VideoToFrames.videoDecode(java.lang.String):void");
    }

    private void showSupportedColorFormat(MediaCodecInfo.CodecCapabilities codecCapabilities) {
        System.out.print("supported color format: ");
        int[] iArr = codecCapabilities.colorFormats;
        int length = iArr.length;
        for (int i = 0; i < length; i += COLOR_FormatI420) {
            int i2 = iArr[i];
            System.out.print(i2 + "\t");
        }
        System.out.println();
    }

    private boolean isColorFormatSupported(int i, MediaCodecInfo.CodecCapabilities codecCapabilities) {
        int[] iArr = codecCapabilities.colorFormats;
        int length = iArr.length;
        for (int i2 = 0; i2 < length; i2 += COLOR_FormatI420) {
            if (iArr[i2] == i) {
                return true;
            }
        }
        return false;
    }

    private void decodeFramesToImage(MediaCodec mediaCodec, MediaExtractor mediaExtractor, MediaFormat mediaFormat) {
        long j;
        int dequeueInputBuffer;
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        mediaCodec.configure(mediaFormat, this.play_surf, (MediaCrypto) null, 0);
        mediaCodec.start();
        mediaFormat.getInteger("width");
        mediaFormat.getInteger("height");
        boolean z = false;
        boolean z2 = false;
        int i = 0;
        boolean z3 = false;
        long j2 = 0;
        while (!z && !this.stopDecode) {
            if (z2 || (dequeueInputBuffer = mediaCodec.dequeueInputBuffer(DEFAULT_TIMEOUT_US)) < 0) {
                j = 10000;
            } else {
                int readSampleData = mediaExtractor.readSampleData(mediaCodec.getInputBuffer(dequeueInputBuffer), 0);
                if (readSampleData < 0) {
                    j = 10000;
                    z2 = true;
                    mediaCodec.queueInputBuffer(dequeueInputBuffer, 0, 0, 0L, 4);
                } else {
                    j = 10000;
                    mediaCodec.queueInputBuffer(dequeueInputBuffer, 0, readSampleData, mediaExtractor.getSampleTime(), 0);
                    mediaExtractor.advance();
                }
            }
            int dequeueOutputBuffer = mediaCodec.dequeueOutputBuffer(bufferInfo, j);
            if (dequeueOutputBuffer >= 0) {
                boolean z4 = (bufferInfo.flags & 4) != 0 ? true : z;
                if (bufferInfo.size != 0) {
                    i += COLOR_FormatI420;
                    Callback callback = this.callback;
                    if (callback != null) {
                        callback.onDecodeFrame(i);
                    }
                    if (!z3) {
                        j2 = System.currentTimeMillis();
                        z3 = true;
                    }
                    if (this.play_surf == null) {
                        Image outputImage = mediaCodec.getOutputImage(dequeueOutputBuffer);
                        ByteBuffer buffer = outputImage.getPlanes()[0].getBuffer();
                        byte[] bArr = new byte[buffer.remaining()];
                        buffer.get(bArr);
                        LinkedBlockingQueue<byte[]> linkedBlockingQueue = this.mQueue;
                        if (linkedBlockingQueue != null) {
                            try {
                                linkedBlockingQueue.put(bArr);
                            } catch (InterruptedException e) {
                                XposedBridge.log("【VCAM】" + e.toString());
                            }
                        }
                        if (this.outputImageFormat != null) {
                            HookMain.data_buffer = getDataFromImage(outputImage, COLOR_FormatNV21);
                        }
                        outputImage.close();
                    }
                    long currentTimeMillis = (bufferInfo.presentationTimeUs / 1000) - (System.currentTimeMillis() - j2);
                    if (currentTimeMillis > 0) {
                        try {
                            Thread.sleep(currentTimeMillis);
                        } catch (InterruptedException e2) {
                            XposedBridge.log("【VCAM】" + e2.toString());
                            XposedBridge.log("【VCAM】线程延迟出错");
                        }
                    }
                    mediaCodec.releaseOutputBuffer(dequeueOutputBuffer, true);
                }
                z = z4;
            }
        }
        Callback callback2 = this.callback;
        if (callback2 != null) {
            callback2.onFinishDecode();
        }
    }

    private static int selectTrack(MediaExtractor mediaExtractor) {
        int trackCount = mediaExtractor.getTrackCount();
        for (int i = 0; i < trackCount; i += COLOR_FormatI420) {
            if (mediaExtractor.getTrackFormat(i).getString("mime").startsWith("video/")) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isImageFormatSupported(Image image) {
        int format = image.getFormat();
        return format == 17 || format == 35 || format == 842094169;
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x007a  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0098  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x007d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static byte[] getDataFromImage(android.media.Image r20, int r21) {
        /*
            Method dump skipped, instructions count: 248
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.vcam.VideoToFrames.getDataFromImage(android.media.Image, int):byte[]");
    }
}
