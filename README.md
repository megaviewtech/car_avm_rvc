 🚘 Car AVM / RVC

**Car Around View Monitor (AVM)** and **Rear View Camera (RVC)** system for automotive applications.  
It combines **C/C++**, **Java**, **OpenGL ES**, and **OpenCV** to implement real-time surround view rendering on **Android / Linux** platforms.

---

## 🧩 Project Overview

This project provides a complete car surround view and rear view camera solution:
- 🧠 Real-time image stitching from multiple fisheye cameras  
- 🕹️ 3D visualization with OpenGL ES rendering  
- 🚗 Support for vehicle model and dynamic perspective control  
- ⚡ DMA-BUF / EGLImage for zero-copy GPU rendering  
- 🤖 Compatible with AI modules for object detection or lane assist  
- 📱 Runs on Android and embedded Linux systems (e.g., Allwinner / Raspberry Pi)

---

## 📦 Environment Requirements

| Component | Version / Requirement |
|------------|------------------------|
| OS | Android / Linux |
| Language | Java + C/C++ (JNI) |
| OpenGL ES | 2.0 or higher |
| OpenCV | 3.4.5 or higher |
| NDK | r21 or later |
| Build System | CMake / ndk-build |

---

## ⚙️ Build & Run

### 🧰 1. Clone Repository

git clone https://github.com/megaviewtech/car-avm-rvc.git
cd car-avm-rvc