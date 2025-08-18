add_library(memory_dumper SHARED memory_dumper.cpp)

target_include_directories(memory_dumper PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})

if (MSVC)
    target_compile_options(memory_dumper PRIVATE /std:c++17 /EHsc /MT)
endif()

set_target_properties(memory_dumper PROPERTIES PREFIX "")

add_custom_command(TARGET memory_dumper POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E make_directory "${CMAKE_SOURCE_DIR}/target/native"
        COMMAND ${CMAKE_COMMAND} -E copy_if_different "$<TARGET_FILE:memory_dumper>" "${CMAKE_SOURCE_DIR}/target/native"
)
