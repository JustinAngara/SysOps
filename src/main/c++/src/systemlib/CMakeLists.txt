find_package(JNI REQUIRED)

add_library(systemlib SHARED
        systemlib.cpp
        action_handler.cpp
        process_inject.cpp
)

target_include_directories(systemlib PRIVATE
        ${JNI_INCLUDE_DIRS}
        ${CMAKE_SOURCE_DIR}/src/memory_dumper  # if needed
)

if (MSVC)
    target_compile_options(systemlib PRIVATE /std:c++17 /EHsc /MT)
endif()

set_target_properties(systemlib PROPERTIES
        PREFIX ""  # removes 'lib' prefix on Unix
        RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/target/native"
        RUNTIME_OUTPUT_DIRECTORY_DEBUG "${CMAKE_SOURCE_DIR}/target/native"
        RUNTIME_OUTPUT_DIRECTORY_RELEASE "${CMAKE_SOURCE_DIR}/target/native"
)
