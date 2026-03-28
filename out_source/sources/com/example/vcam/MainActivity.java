package com.example.vcam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public class MainActivity extends Activity {
    private Switch disable_switch;
    private Switch disable_toast_switch;
    private Switch force_private_dir;
    private Switch force_show_switch;
    private Switch play_sound_switch;

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (iArr.length > 0) {
            if (iArr[0] == -1) {
                Toast.makeText(this, R.string.permission_lack_warn, 0).show();
                return;
            }
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera1/");
            if (file.exists()) {
                return;
            }
            file.mkdir();
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        sync_statue_with_files();
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        this.force_show_switch = (Switch) findViewById(R.id.switch1);
        this.disable_switch = (Switch) findViewById(R.id.switch2);
        this.play_sound_switch = (Switch) findViewById(R.id.switch3);
        this.force_private_dir = (Switch) findViewById(R.id.switch4);
        this.disable_toast_switch = (Switch) findViewById(R.id.switch5);
        sync_statue_with_files();
        button.setOnClickListener(new View.OnClickListener() { // from class: com.example.vcam.MainActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.m0lambda$onCreate$0$comexamplevcamMainActivity(view);
            }
        });
        ((Button) findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() { // from class: com.example.vcam.MainActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.m1lambda$onCreate$1$comexamplevcamMainActivity(view);
            }
        });
        this.disable_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.example.vcam.MainActivity$$ExternalSyntheticLambda4
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                MainActivity.this.m2lambda$onCreate$2$comexamplevcamMainActivity(compoundButton, z);
            }
        });
        this.force_show_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.example.vcam.MainActivity$$ExternalSyntheticLambda5
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                MainActivity.this.m3lambda$onCreate$3$comexamplevcamMainActivity(compoundButton, z);
            }
        });
        this.play_sound_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.example.vcam.MainActivity.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (compoundButton.isPressed()) {
                    if (!MainActivity.this.has_permission()) {
                        MainActivity.this.request_permission();
                    } else {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera1/no-silent.jpg");
                        if (file.exists() != z) {
                            if (z) {
                                try {
                                    file.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                file.delete();
                            }
                        }
                    }
                    MainActivity.this.sync_statue_with_files();
                }
            }
        });
        this.force_private_dir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.example.vcam.MainActivity$$ExternalSyntheticLambda6
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                MainActivity.this.m4lambda$onCreate$4$comexamplevcamMainActivity(compoundButton, z);
            }
        });
        this.disable_toast_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.example.vcam.MainActivity$$ExternalSyntheticLambda7
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                MainActivity.this.m5lambda$onCreate$5$comexamplevcamMainActivity(compoundButton, z);
            }
        });
    }

    /* renamed from: lambda$onCreate$0$com-example-vcam-MainActivity, reason: not valid java name */
    public /* synthetic */ void m0lambda$onCreate$0$comexamplevcamMainActivity(View view) {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://github.com/w2016561536/android_virtual_cam")));
    }

    /* renamed from: lambda$onCreate$1$com-example-vcam-MainActivity, reason: not valid java name */
    public /* synthetic */ void m1lambda$onCreate$1$comexamplevcamMainActivity(View view) {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://gitee.com/w2016561536/android_virtual_cam")));
    }

    /* renamed from: lambda$onCreate$2$com-example-vcam-MainActivity, reason: not valid java name */
    public /* synthetic */ void m2lambda$onCreate$2$comexamplevcamMainActivity(CompoundButton compoundButton, boolean z) {
        if (compoundButton.isPressed()) {
            if (!has_permission()) {
                request_permission();
            } else {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera1/disable.jpg");
                if (file.exists() != z) {
                    if (z) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        file.delete();
                    }
                }
            }
            sync_statue_with_files();
        }
    }

    /* renamed from: lambda$onCreate$3$com-example-vcam-MainActivity, reason: not valid java name */
    public /* synthetic */ void m3lambda$onCreate$3$comexamplevcamMainActivity(CompoundButton compoundButton, boolean z) {
        if (compoundButton.isPressed()) {
            if (!has_permission()) {
                request_permission();
            } else {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera1/force_show.jpg");
                if (file.exists() != z) {
                    if (z) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        file.delete();
                    }
                }
            }
            sync_statue_with_files();
        }
    }

    /* renamed from: lambda$onCreate$4$com-example-vcam-MainActivity, reason: not valid java name */
    public /* synthetic */ void m4lambda$onCreate$4$comexamplevcamMainActivity(CompoundButton compoundButton, boolean z) {
        if (compoundButton.isPressed()) {
            if (!has_permission()) {
                request_permission();
            } else {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera1/private_dir.jpg");
                if (file.exists() != z) {
                    if (z) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        file.delete();
                    }
                }
            }
            sync_statue_with_files();
        }
    }

    /* renamed from: lambda$onCreate$5$com-example-vcam-MainActivity, reason: not valid java name */
    public /* synthetic */ void m5lambda$onCreate$5$comexamplevcamMainActivity(CompoundButton compoundButton, boolean z) {
        if (compoundButton.isPressed()) {
            if (!has_permission()) {
                request_permission();
            } else {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera1/no_toast.jpg");
                if (file.exists() != z) {
                    if (z) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        file.delete();
                    }
                }
            }
            sync_statue_with_files();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void request_permission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == -1 || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == -1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.permission_lack_warn);
                builder.setMessage(R.string.permission_description);
                builder.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() { // from class: com.example.vcam.MainActivity$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.m6lambda$request_permission$6$comexamplevcamMainActivity(dialogInterface, i);
                    }
                });
                builder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() { // from class: com.example.vcam.MainActivity$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.m7lambda$request_permission$7$comexamplevcamMainActivity(dialogInterface, i);
                    }
                });
                builder.show();
            }
        }
    }

    /* renamed from: lambda$request_permission$6$com-example-vcam-MainActivity, reason: not valid java name */
    public /* synthetic */ void m6lambda$request_permission$6$comexamplevcamMainActivity(DialogInterface dialogInterface, int i) {
        Toast.makeText(this, R.string.permission_lack_warn, 0).show();
    }

    /* renamed from: lambda$request_permission$7$com-example-vcam-MainActivity, reason: not valid java name */
    public /* synthetic */ void m7lambda$request_permission$7$comexamplevcamMainActivity(DialogInterface dialogInterface, int i) {
        requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean has_permission() {
        if (Build.VERSION.SDK_INT >= 23) {
            return (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == -1 || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == -1) ? false : true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sync_statue_with_files() {
        Log.d(getApplication().getPackageName(), "【VCAM】[sync]同步开关状态");
        if (!has_permission()) {
            request_permission();
        } else {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera1");
            if (!file.exists()) {
                file.mkdir();
            }
        }
        this.disable_switch.setChecked(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera1/disable.jpg").exists());
        this.force_show_switch.setChecked(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera1/force_show.jpg").exists());
        this.play_sound_switch.setChecked(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera1/no-silent.jpg").exists());
        this.force_private_dir.setChecked(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera1/private_dir.jpg").exists());
        this.disable_toast_switch.setChecked(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera1/no_toast.jpg").exists());
    }
}
