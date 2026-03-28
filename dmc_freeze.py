import ctypes
import os
import struct

# إعداد الوصول لذاكرة النظام
libc = ctypes.CDLL("libc.so")

def freeze_dmc(pid):
    maps_path = f"/proc/{pid}/maps"
    mem_path = f"/proc/{pid}/mem"
    
    if not os.path.exists(maps_path):
        print("[-] لم يتم العثور على اللعبة، تأكد من الـ PID")
        return

    target_value = struct.pack('f', 1.0) # القيمة التي نبحث عنها (سرعة 1)
    freeze_value = struct.pack('f', 0.0) # القيمة التي سنضعها (تجميد 0)

    print(f"[*] جاري فحص ذاكرة DMC (PID: {pid})...")

    try:
        with open(maps_path, 'r') as maps:
            for line in maps:
                # نركز على المناطق القابلة للكتابة فقط (rw) لضمان السرعة
                if 'rw' in line:
                    parts = line.split()
                    addr_range = parts[0].split('-')
                    start = int(addr_range[0], 16)
                    end = int(addr_range[1], 16)
                    size = end - start

                    try:
                        with open(mem_path, 'rb+') as mem:
                            mem.seek(start)
                            chunk = mem.read(size)
                            
                            pos = chunk.find(target_value)
                            while pos != -1:
                                abs_addr = start + pos
                                print(f"[+] تم التجميد في العنوان: {hex(abs_addr)}")
                                mem.seek(abs_addr)
                                mem.write(freeze_value)
                                pos = chunk.find(target_value, pos + 4)
                    except Exception:
                        continue
        print("[+] انتهت عملية المسح والتجميد.")
    except PermissionError:
        print("[-] خطأ: يجب تشغيل السكربت بصلاحيات Root (استخدم tsu)")

if __name__ == "__main__":
    # الـ PID الخاص بك الذي استخرجناه سابقاً
    freeze_dmc(25803)

