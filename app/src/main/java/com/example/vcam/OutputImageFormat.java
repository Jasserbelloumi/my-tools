package com.example.vcam;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: VideoToFrames.java */
/* loaded from: classes.dex */
public enum OutputImageFormat {
    I420("I420"),
    NV21("NV21"),
    JPEG("JPEG");

    private final String friendlyName;

    OutputImageFormat(String str) {
        this.friendlyName = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.friendlyName;
    }
}
