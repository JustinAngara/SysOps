cmake_minimum_required(VERSION 3.15)

add_library(memory_dumper SHARED memory_dumper.cpp)

# Ensure the output directory exists
file(MAKE_DIRECTORY "${CMAKE_SOURCE_DIR}/target/native")

# Set the output location for all build configurations
set_target_properties(memory_dumper PROPERTIES
        LIBRARY_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/target/native"
        RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/target/native"
        LIBRARY_OUTPUT_DIRECTORY_DEBUG "${CMAKE_SOURCE_DIR}/target/native"
        RUNTIME_OUTPUT_DIRECTORY_DEBUG "${CMAKE_SOURCE_DIR}/target/native"
        LIBRARY_OUTPUT_DIRECTORY_RELEASE "${CMAKE_SOURCE_DIR}/target/native"
        RUNTIME_OUTPUT_DIRECTORY_RELEASE "${CMAKE_SOURCE_DIR}/target/native"
        PREFIX ""  # removes 'lib' prefix on Unix systems (optional for consistency)
)
