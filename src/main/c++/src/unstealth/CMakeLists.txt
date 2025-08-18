cmake_minimum_required(VERSION 3.15)

add_library(unstealth SHARED unstealth.cpp)

add_custom_command(TARGET unstealth POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E make_directory "${CMAKE_SOURCE_DIR}/target/native"
        COMMAND ${CMAKE_COMMAND} -E copy_if_different "$<TARGET_FILE:unstealth>" "${CMAKE_SOURCE_DIR}/target/native"
)
