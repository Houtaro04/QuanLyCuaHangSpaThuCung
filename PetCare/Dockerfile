# 1. Dùng JDK 25
FROM container-registry.oracle.com/java/openjdk:25

WORKDIR /app

# 2. Copy source code và thư viện
COPY src ./src
COPY libs ./libs

# Tạo thư mục out để chứa file biên dịch
RUN mkdir out

# 3. Giải nén thư viện SQL vào thư mục out
RUN cd out && jar xf ../libs/*.jar

# --- QUAN TRỌNG: Xóa chữ ký bảo mật gốc của Microsoft để tránh lỗi SecurityException ---
RUN cd out/META-INF && rm -f *.SF *.DSA *.RSA

# 4. Biên dịch Code (trỏ vào libs để không lỗi thiếu thư viện)
RUN javac -cp "libs/*" -d out $(find src -name "*.java")

# 5. Tạo Manifest (Chỉ định class main.Main)
RUN echo "Main-Class: main.Main" > Manifest.txt

# 6. Đóng gói thành file .jar
RUN jar cmf Manifest.txt app-final.jar -C out .