@echo off
cd /d %~dp0
rmdir /s /q build
mkdir build
cd build
cmake ..
cmake --build . --config Debug
