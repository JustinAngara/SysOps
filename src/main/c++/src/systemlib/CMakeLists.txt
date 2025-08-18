find_package(JNI REQUIRED)

add_library(systemlib SHARED
        systemlib.cpp
        action_handler.cpp
        process_inject.cpp
)

target_include_directories(systemlib PRIVATE
        ${JNI_INCLUDE_DIRS}
        ${CMAKE_SOURCE_DIR}/src/memory_dumper  # only if using memory_dumper.h
)

if (MSVC)
    target_compile_options(systemlib PRIVATE /std:c++17 /EHsc /MT)
endif()

set_target_properties(systemlib PROPERTIES PREFIX "")

add_custom_command(TARGET systemlib POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E make_directory "${CMAKE_SOURCE_DIR}/target/native"
        COMMAND ${CMAKE_COMMAND} -E copy_if_different "$<TARGET_FILE:systemlib>" "${CMAKE_SOURCE_DIR}/target/native"
)
