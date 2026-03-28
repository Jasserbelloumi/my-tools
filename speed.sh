while true; do
  # قراءة بيانات الاستقبال والارسال
  R1=$(cat /sys/class/net/wlan0/statistics/rx_bytes)
  T1=$(cat /sys/class/net/wlan0/statistics/tx_bytes)
  sleep 1
  R2=$(cat /sys/class/net/wlan0/statistics/rx_bytes)
  T2=$(cat /sys/class/net/wlan0/statistics/tx_bytes)
  
  # حساب السرعة بالكيلوبايت
  DOWN=$(( (R2 - R1) / 1024 ))
  UP=$(( (T2 - T1) / 1024 ))
  
  # طباعة النتيجة في سطر واحد يتحدث تلقائيا
  printf "[1;32m[Download:    0 KB/s] [1;34m[Upload:    0 KB/s][0m " "$DOWN" "$UP"
done