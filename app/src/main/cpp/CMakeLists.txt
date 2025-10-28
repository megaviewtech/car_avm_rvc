# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("avm_cam_jni")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#
# In this top level CMakeLists.txt, ${CMAKE_PROJECT_NAME} is used to define
# the target library name; in the sub-module's CMakeLists.txt, ${PROJECT_NAME}
# is preferred for the same purpose.
#
# In order to load a library into your app from Java/Kotlin, you must call
# System.loadLibrary() and pass the name of the library defined here;
# for GameActivity/NativeActivity derived applications, the same library name must be
# used in the AndroidManifest.xml file.
add_library(${CMAKE_PROJECT_NAME} SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
        avm_cam_jni.cpp)

add_library(libgsl SHARED IMPORTED)
set_target_properties(libgsl PROPERTIES IMPORTED_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libgsl.so")

add_library(libmmosal_proprietary SHARED IMPORTED)
set_target_properties(libmmosal_proprietary PROPERTIES IMPORTED_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libmmosal_proprietary.so")

add_library(libC2D2 SHARED IMPORTED)
set_target_properties(libC2D2 PROPERTIES IMPORTED_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libC2D2.so")

add_library(libc2d30_bltlib SHARED IMPORTED)
set_target_properties(libc2d30_bltlib PROPERTIES IMPORTED_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libc2d30_bltlib.so")

add_library(libc2dcolorconvert SHARED IMPORTED)
set_target_properties(libc2dcolorconvert PROPERTIES IMPORTED_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libc2dcolorconvert.so")

add_library(libgui_camera SHARED IMPORTED)
set_target_properties(libgui_camera PROPERTIES IMPORTED_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libgui_camera.so")

add_library(libavm_cam SHARED IMPORTED)
set_target_properties(libavm_cam PROPERTIES IMPORTED_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libavm_cam.so")

add_library(libais_client SHARED IMPORTED)
set_target_properties(libais_client PROPERTIES IMPORTED_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libais_client.so")

add_library(libais_log_proprietary SHARED IMPORTED)
set_target_properties(libais_log_proprietary PROPERTIES IMPORTED_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libais_log_proprietary.so")

add_library(libais_test_util_proprietary SHARED IMPORTED)
set_target_properties(libais_test_util_proprietary PROPERTIES IMPORTED_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libais_test_util_proprietary.so")
# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(${CMAKE_PROJECT_NAME}
        # List libraries link to the target library
        libgsl
        libC2D2
        libc2d30_bltlib
        libmmosal_proprietary
        libgui_camera
        libavm_cam
        libais_client
        libais_log_proprietary
        libais_test_util_proprietary
        android
        log)