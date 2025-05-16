#!/bin/bash

cd app/src/native/chess

echo -e "\e[31mRemoving\e[0m build directory"
rm -rf build

echo -e "\e[32mInitialising\e[0m chess build"
cmake -S . -G Ninja -B build

echo -e "\e[32mBuilding\e[0m chess"
cmake --build build

echo -e "\e[32mMoving\e[0m libchess.so to lib directory"
mv build/lib/libchess.so lib/libchess.so

cd ../../../..

echo -e "\e[32mBuild complete!\e[0m"