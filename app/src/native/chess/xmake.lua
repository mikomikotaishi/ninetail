add_rules("mode.release", "mode.debug")
set_languages("c++23")

target("chess")
set_kind("shared")
add_files(
    "src/*.cppm"
)