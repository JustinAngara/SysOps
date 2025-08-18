cmake_minimum_required(VERSION 3.15)
project(systemlib_jni)

find_package(JNI REQUIRED)

add_library(systemlib SHARED ${CMAKE_CURRENT_SOURCE_DIR}/systemlib.cpp
        action_handler.cpp
        action_handler.h
        stealth.cpp
        stealth.h)
target_include_directories(systemlib PRIVATE ${JNI_INCLUDE_DIRS})

# Name without "lib" prefix on Windows
if (WIN32)
    set_target_properties(systemlib PROPERTIES PREFIX "")
endif()

# Static MSVC runtime (helps demos run without Redistributable)
if (MSVC)
    target_compile_options(systemlib PRIVATE /std:c++17 /EHsc /MT)
endif()

# If using MinGW, uncomment to static link libstdc++/libgcc
# if (MINGW)
#   target_link_options(systemlib PRIVATE -static -static-libstdc++ -static-libgcc)
# endif()

# Copy the built DLL to a stable place for Java to load
set(OUT_NATIVE "${CMAKE_SOURCE_DIR}/target/native")
add_custom_command(TARGET systemlib POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E make_directory "${OUT_NATIVE}"
        COMMAND ${CMAKE_COMMAND} -E copy_if_different "$<TARGET_FILE:systemlib>" "${OUT_NATIVE}"
)
