cmake_minimum_required(VERSION 4.1)
project(Low_Level_Design)

set(CMAKE_CXX_STANDARD 26)

add_executable(Low_Level_Design
        practice_problems/google_docs/src/Client.cpp
        practice_problems/google_docs/src/Client.h
        practice_problems/google_docs/src/DocumentEditor.cpp
        practice_problems/google_docs/src/DocumentEditor.h
        practice_problems/google_docs/src/DocumentElement.cpp
        practice_problems/google_docs/src/DocumentElement.h
        practice_problems/google_docs/src/Document.cpp
        practice_problems/google_docs/src/Document.h
        practice_problems/google_docs/src/TextElement.cpp
        practice_problems/google_docs/src/TextElement.h
        practice_problems/google_docs/src/TabSpaceElement.cpp
        practice_problems/google_docs/src/TabSpaceElement.h
)
