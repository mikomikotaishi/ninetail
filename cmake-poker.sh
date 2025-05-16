#!/bin/bash

cd app/src/native/poker

echo -e "\e[31mRemoving\e[0m build directory"
rm -rf build

echo -e "\e[32mInitialising\e[0m poker build"
cmake -S . -G Ninja -B build

echo -e "\e[32mBuilding\e[0m poker"
cmake --build build

echo -e "\e[32mMoving\e[0m libpoker.so to lib directory"
mv build/lib/libpoker.so lib/libpoker.so

cd ../../../..

echo -e "\e[32mBuild complete!\e[0m"