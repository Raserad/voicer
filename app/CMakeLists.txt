cmake_minimum_required(VERSION 3.6)

add_library (native-lib SHARED src/main/cpp/native-lib.cpp )

target_link_libraries(native-lib oboe)

# Set the path to the Oboe directory.
set (OBOE_DIR ../oboe)

# Add the Oboe library as a subdirectory in your project.
add_subdirectory (${OBOE_DIR} ./oboe)

# Specify the path to the Oboe header files.
include_directories (${OBOE_DIR}/include)