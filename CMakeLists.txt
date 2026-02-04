cmake_minimum_required(VERSION 4.1)
project(Low_Level_Design)

set(CMAKE_CXX_STANDARD 26)

add_executable(Low_Level_Design main.cpp
        principles/solid/single_responsibility.cpp
        principles/solid/open_close.cpp
        principles/solid/liskov_substitution.cpp)
