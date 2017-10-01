
cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
# ######################### OpenCV 环境 ###########################

set( OpenCV_DIR G:/opencv/OpenCV-android-sdk/sdk/native/jni )

find_package(OpenCV REQUIRED)
if(OpenCV_FOUND)
    include_directories(${OpenCV_INCLUDE_DIRS})
    message(STATUS "OpenCV library status:")
    message(STATUS "    version: ${OpenCV_VERSION}")
    message(STATUS "    libraries: ${OpenCV_LIBS}")
    message(STATUS "    include path: ${OpenCV_INCLUDE_DIRS}")
else(OpenCV_FOUND)
    message(FATAL_ERROR "OpenCV library not found")
endif(OpenCV_FOUND)

# ######################### 原生模块 ##############################

add_library( native-lib
             SHARED
             src/main/cpp/native-lib.cpp)

target_link_libraries( native-lib
                       ${OpenCV_LIBS}
                       log
                       jnigraphics)