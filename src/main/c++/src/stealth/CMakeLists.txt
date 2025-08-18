cmake_minimum_required(VERSION 3.15)

add_library(stealth SHARED stealth.cpp)

target_include_directories(stealth PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})

if (MSVC)
    target_compile_options(stealth PRIVATE /std:c++17 /EHsc /MT)
endif()

# FIX: set properties on 'stealth', not 'memory_dumper'
set_target_properties(stealth PROPERTIES
        RUNTIME_OUTPUT_DIRECTORY         "${CMAKE_SOURCE_DIR}/target/native"
        RUNTIME_OUTPUT_DIRECTORY_DEBUG   "${CMAKE_SOURCE_DIR}/target/native"
        RUNTIME_OUTPUT_DIRECTORY_RELEASE "${CMAKE_SOURCE_DIR}/target/native"
)

add_custom_command(TARGET stealth POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E make_directory "${CMAKE_SOURCE_DIR}/target/native"
        COMMAND ${CMAKE_COMMAND} -E copy_if_different "$<TARGET_FILE:stealth>" "${CMAKE_SOURCE_DIR}/target/native"
)
