file(GLOB_RECURSE MODULES *.cppm)

add_library(modules SHARED)

set_target_properties(modules PROPERTIES OUTPUT_NAME "chess")

target_sources(modules
    PUBLIC
    FILE_SET CXX_MODULES FILES
    ${MODULES})

set_target_properties(modules PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
