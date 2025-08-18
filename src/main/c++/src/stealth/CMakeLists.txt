add_library(stealth SHARED stealth.cpp)

target_include_directories(stealth PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})

if (MSVC)
    target_compile_options(stealth PRIVATE /std:c++17 /EHsc /MT)
endif()

set_target_properties(stealth PROPERTIES PREFIX "")

add_custom_command(TARGET stealth POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E make_directory "${CMAKE_SOURCE_DIR}/target/native"
        COMMAND ${CMAKE_COMMAND} -E copy_if_different "$<TARGET_FILE:stealth>" "${CMAKE_SOURCE_DIR}/target/native"
)
